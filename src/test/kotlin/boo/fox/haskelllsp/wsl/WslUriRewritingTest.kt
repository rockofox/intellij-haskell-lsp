package boo.fox.haskelllsp.wsl

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the URI rewriting logic used by WslHaskellLanguageServer,
 * and the WSL path detection regex used by WslSupport.
 * These tests exercise the patterns directly without requiring a running WSL instance.
 */
class WslUriRewritingTest {

    private val distroName = "Ubuntu"

    // Same regex as WslSupport.WSL_UNC_PREFIX
    private val wslPathRegex = Regex(
        """^(?:\\\\|//)(?:wsl\$|wsl\.localhost)[/\\]([^/\\]+)(.*)$""",
        RegexOption.IGNORE_CASE
    )

    private fun parseWslPath(path: String): Pair<String, String>? {
        val match = wslPathRegex.matchEntire(path) ?: return null
        val distro = match.groupValues[1]
        val remainder = match.groupValues[2]
        val linuxPath = if (remainder.isEmpty()) "/" else remainder.replace('\\', '/')
        return distro to linuxPath
    }

    // Hackage rewriting: local GHC doc URIs → Hackage URLs
    private val hackageRegex = Regex(
        """file:///[^")*]*?/libraries/([^/"]+)/([^")]*?\.html(?:#[^")]*)?)"""
    )

    // Inbound: HLS (Linux) → IntelliJ (Windows)
    // file:///home/... → file:////wsl.localhost/Ubuntu/home/...
    private val inboundRegex = Regex("""file:///(?![A-Za-z]:)""")
    private val inboundReplacement = "file:////wsl.localhost/$distroName/"

    private fun rewriteInbound(message: String): String {
        val withHackage = hackageRegex.replace(message) { match ->
            val packageVersion = match.groupValues[1]
            val relativePath = match.groupValues[2]
            "https://hackage.haskell.org/package/$packageVersion/docs/$relativePath"
        }
        return inboundRegex.replace(withHackage) { inboundReplacement }
    }

    // Outbound: IntelliJ (Windows) → HLS (Linux)
    // file://{2,4}wsl.localhost/Ubuntu/... → file:///...
    private val outboundRegex = Regex(
        """file:/{2,4}(?:wsl\.localhost|wsl\$|wsl%24)/${Regex.escape(distroName)}/""",
        RegexOption.IGNORE_CASE
    )

    private fun rewriteOutbound(message: String): String =
        outboundRegex.replace(message, "file:///")

    // --- Inbound tests (HLS → IntelliJ) ---

    @Test
    fun `inbound rewrites Linux file URI to WSL UNC URI`() {
        val input = """{"uri":"file:///home/vojko/project/src/Main.hs"}"""
        val expected = """{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/project/src/Main.hs"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound rewrites multiple URIs in same message`() {
        val input = """{"uri":"file:///home/vojko/a.hs","target":"file:///home/vojko/b.hs"}"""
        val expected = """{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/a.hs","target":"file:////wsl.localhost/Ubuntu/home/vojko/b.hs"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound does not rewrite Windows drive letter URIs`() {
        val input = """{"uri":"file:///C:/Users/vojko/project/Main.hs"}"""
        assertEquals(input, rewriteInbound(input))
    }

    @Test
    fun `inbound handles usr local paths`() {
        val input = """{"uri":"file:///usr/local/lib/ghc/doc/index.html"}"""
        val expected = """{"uri":"file:////wsl.localhost/Ubuntu/usr/local/lib/ghc/doc/index.html"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound preserves non-file URIs`() {
        val input = """{"uri":"https://hackage.haskell.org/package/base"}"""
        assertEquals(input, rewriteInbound(input))
    }

    // --- Hackage doc rewriting tests (inbound) ---

    @Test
    fun `inbound rewrites GHC doc URI to Hackage URL`() {
        val input = """{"uri":"file:///home/vojko/.ghcup/share/doc/ghc-9.8.4/html/libraries/base-4.19.2.0/Data-List.html"}"""
        val expected = """{"uri":"https://hackage.haskell.org/package/base-4.19.2.0/docs/Data-List.html"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound rewrites GHC doc URI with anchor`() {
        val input = """{"uri":"file:///home/vojko/.ghcup/share/doc/ghc-9.8.4/html/libraries/base-4.19.2.0/Data-List.html#v:sort"}"""
        val expected = """{"uri":"https://hackage.haskell.org/package/base-4.19.2.0/docs/Data-List.html#v:sort"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound rewrites GHC source doc URI to Hackage`() {
        val input = """{"uri":"file:///home/vojko/.ghcup/share/doc/ghc-9.8.4/html/libraries/base-4.19.2.0/src/Data.List.html"}"""
        val expected = """{"uri":"https://hackage.haskell.org/package/base-4.19.2.0/docs/src/Data.List.html"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound rewrites different package doc URIs`() {
        val input = """{"uri":"file:///usr/lib/ghc/doc/libraries/containers-0.6.7/Data-Map.html"}"""
        val expected = """{"uri":"https://hackage.haskell.org/package/containers-0.6.7/docs/Data-Map.html"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound rewrites both doc and source links in markdown hover`() {
        // This is the actual format HLS sends: markdown with doc + source links in one JSON string
        val input = """{"value":"[Documentation](file:///home/vojko/.ghcup/ghc/9.6.7/share/doc/ghc-9.6.7/html/libraries/ghc-prim-0.10.0/GHC-Types.html#t:IO)\n\n[Source](file:///home/vojko/.ghcup/ghc/9.6.7/share/doc/ghc-9.6.7/html/libraries/ghc-prim-0.10.0/src/GHC.Types.html#IO)\n\n\n"}"""
        val expected = """{"value":"[Documentation](https://hackage.haskell.org/package/ghc-prim-0.10.0/docs/GHC-Types.html#t:IO)\n\n[Source](https://hackage.haskell.org/package/ghc-prim-0.10.0/docs/src/GHC.Types.html#IO)\n\n\n"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    @Test
    fun `inbound does not rewrite non-library file URIs to Hackage`() {
        val input = """{"uri":"file:///home/vojko/project/src/Main.hs"}"""
        val expected = """{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/project/src/Main.hs"}"""
        assertEquals(expected, rewriteInbound(input))
    }

    // --- Outbound tests (IntelliJ → HLS) ---

    @Test
    fun `outbound rewrites wsl_localhost UNC URI to Linux URI`() {
        val input = """{"rootUri":"file:////wsl.localhost/Ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound rewrites wsl_dollar UNC URI to Linux URI`() {
        val input = """{"rootUri":"file:////wsl$/Ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound rewrites url-encoded wsl dollar URI`() {
        val input = """{"rootUri":"file:////wsl%24/Ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound is case-insensitive for distro name in wsl_localhost`() {
        val input = """{"rootUri":"file:////wsl.localhost/ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound rewrites 3-slash wsl_localhost URI`() {
        val input = """{"rootUri":"file:///wsl.localhost/Ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound rewrites 2-slash wsl_localhost URI`() {
        val input = """{"rootUri":"file://wsl.localhost/Ubuntu/home/vojko/project"}"""
        val expected = """{"rootUri":"file:///home/vojko/project"}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    @Test
    fun `outbound preserves plain Linux URIs`() {
        val input = """{"uri":"file:///home/vojko/project/Main.hs"}"""
        assertEquals(input, rewriteOutbound(input))
    }

    @Test
    fun `outbound rewrites multiple URIs in workspace folders`() {
        val input = """{"workspaceFolders":[{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/a"},{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/b"}]}"""
        val expected = """{"workspaceFolders":[{"uri":"file:///home/vojko/a"},{"uri":"file:///home/vojko/b"}]}"""
        assertEquals(expected, rewriteOutbound(input))
    }

    // --- Round-trip tests ---

    @Test
    fun `round trip outbound then inbound preserves original WSL URI`() {
        val original = """{"uri":"file:////wsl.localhost/Ubuntu/home/vojko/project/Main.hs"}"""
        val afterOutbound = rewriteOutbound(original)
        assertEquals("""{"uri":"file:///home/vojko/project/Main.hs"}""", afterOutbound)
        val afterInbound = rewriteInbound(afterOutbound)
        assertEquals(original, afterInbound)
    }

    // --- WSL path detection tests ---

    @Test
    fun `detects backslash wsl_localhost path`() {
        val result = parseWslPath("""\\wsl.localhost\Ubuntu\home\vojko\project""")
        assertNotNull(result)
        assertEquals("Ubuntu", result.first)
        assertEquals("/home/vojko/project", result.second)
    }

    @Test
    fun `detects forward-slash wsl_localhost path`() {
        val result = parseWslPath("//wsl.localhost/Ubuntu/home/vojko/project")
        assertNotNull(result)
        assertEquals("Ubuntu", result.first)
        assertEquals("/home/vojko/project", result.second)
    }

    @Test
    fun `detects backslash wsl_dollar path`() {
        val result = parseWslPath("""\\wsl$\Ubuntu\home\vojko""")
        assertNotNull(result)
        assertEquals("Ubuntu", result.first)
        assertEquals("/home/vojko", result.second)
    }

    @Test
    fun `detects forward-slash wsl_dollar path`() {
        val result = parseWslPath("//wsl\$/Ubuntu/home/vojko")
        assertNotNull(result)
        assertEquals("Ubuntu", result.first)
        assertEquals("/home/vojko", result.second)
    }

    @Test
    fun `returns null for regular Windows path`() {
        assertNull(parseWslPath("""C:\Users\vojko\project"""))
    }

    @Test
    fun `returns null for regular Linux path`() {
        assertNull(parseWslPath("/home/vojko/project"))
    }
}
