package boo.fox.haskelllsp.wsl

import boo.fox.haskelllsp.settings.HaskellLspSettings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

/**
 * LSP connection provider that launches HLS inside a WSL2 distribution and transparently
 * rewrites file URIs in both directions:
 *
 * - Outbound (IntelliJ → HLS): Windows UNC URIs like file:////wsl.localhost/Ubuntu/home/...
 *   are converted to Linux URIs like file:///home/...
 * - Inbound (HLS → IntelliJ): Linux URIs like file:///home/... are converted to Windows
 *   UNC URIs like file:////wsl.localhost/Ubuntu/home/...
 */
class WslHaskellLanguageServer(
    project: Project,
    private val wslInfo: WslSupport.WslPathInfo
) : ProcessStreamConnectionProvider() {

    private val distroName = wslInfo.distroName

    // Outbound rewriting: strip WSL UNC prefix from URIs (IntelliJ → HLS)
    // Handles 2-4 slashes after "file:" and all WSL UNC variants
    private val outboundRegex = Regex(
        """file:/{2,4}(?:wsl\.localhost|wsl\$|wsl%24)/${Regex.escape(distroName)}/""",
        RegexOption.IGNORE_CASE
    )

    // Inbound: rewrite local GHC doc/source URIs to Hackage URLs so they open in browser.
    // Matches any file:/// URI ending in .html that contains /libraries/<package>/<path>.
    // Uses [^/"]+ for package, [^")"] to stop at both " (JSON) and ) (markdown link end).
    private val hackageRegex = Regex(
        """file:///[^")*]*?/libraries/([^/"]+)/([^")]*?\.html(?:#[^")]*)?)"""
    )

    // Inbound rewriting: file:///home/... → file:////wsl.localhost/Ubuntu/home/...
    // Negative lookahead excludes Windows drive letters (file:///C:/...)
    private val inboundRegex = Regex("""file:///(?![A-Za-z]:)""")
    private val inboundReplacement = "file:////wsl.localhost/$distroName/"

    private var inboundPipe: PipedInputStream? = null
    private var outboundPipe: PipedOutputStream? = null
    private var inboundRewriter: Thread? = null
    private var outboundRewriter: Thread? = null
    private val launchCommand: String

    init {
        val settings = HaskellLspSettings.getInstance()
        launchCommand = buildLaunchCommand(settings.hlsPath, distroName)

        // Use bash login shell so GHCup and other PATH modifications from
        // .profile / .bash_profile are available. "exec" replaces bash with HLS
        // to avoid any shell output corrupting the JSON-RPC stream.
        super.setCommands(
            listOf(
                "wsl.exe", "-d", distroName,
                "--cd", wslInfo.linuxPath,
                "--", "bash", "-lc", launchCommand
            )
        )

        NotificationGroupManager.getInstance().getNotificationGroup("Haskell LSP").createNotification(
            "Haskell LSP",
            "WSL detected ($distroName). Launching HLS inside WSL.",
            NotificationType.INFORMATION
        ).notify(project)
    }

    override fun start() {
        super.start()
        setupStreamRewriting()
    }

    private fun setupStreamRewriting() {
        val rawIn = super.getInputStream() ?: return
        val rawOut = super.getOutputStream() ?: return

        // Inbound: HLS stdout → rewrite Linux URIs to Windows UNC → LSP4IJ reads
        val inPipeOut = PipedOutputStream()
        inboundPipe = PipedInputStream(inPipeOut, PIPE_BUFFER_SIZE)
        val inbound = createRewriterThread("WSL-LSP-Inbound", rawIn, inPipeOut, ::rewriteInbound)
        inboundRewriter = inbound

        // Outbound: LSP4IJ writes → rewrite Windows UNC URIs to Linux → HLS stdin
        val outPipeIn = PipedInputStream(PIPE_BUFFER_SIZE)
        outboundPipe = PipedOutputStream(outPipeIn)
        val outbound = createRewriterThread("WSL-LSP-Outbound", outPipeIn, rawOut, ::rewriteOutbound)
        outboundRewriter = outbound

        inbound.start()
        outbound.start()
    }

    override fun getInputStream(): InputStream? = inboundPipe ?: super.getInputStream()
    override fun getOutputStream(): OutputStream? = outboundPipe ?: super.getOutputStream()

    override fun stop() {
        inboundRewriter?.interrupt()
        outboundRewriter?.interrupt()
        runCatching { inboundPipe?.close() }
        runCatching { outboundPipe?.close() }
        super.stop()
    }

    private fun rewriteInbound(message: String): String {
        // First: rewrite local GHC doc URIs to Hackage URLs (before generic file:/// rewriting)
        val withHackage = hackageRegex.replace(message) { match ->
            val packageVersion = match.groupValues[1]
            val relativePath = match.groupValues[2]
            "https://hackage.haskell.org/package/$packageVersion/docs/$relativePath"
        }
        // Then: rewrite remaining Linux file:/// URIs to Windows WSL UNC URIs
        return inboundRegex.replace(withHackage) { inboundReplacement }
    }

    private fun rewriteOutbound(message: String): String {
        return outboundRegex.replace(message, "file:///")
    }

    companion object {
        private const val PIPE_BUFFER_SIZE = 262144 // 256 KB
        private const val DEFAULT_WSL_HLS_COMMAND = "haskell-language-server-wrapper"

        internal fun buildLaunchCommand(configuredPath: String?, distroName: String): String {
            val executable = resolveWslExecutable(configuredPath, distroName)
            return "exec ${shellQuote(executable)} --lsp"
        }

        internal fun resolveWslExecutable(configuredPath: String?, distroName: String): String {
            val trimmedPath = configuredPath?.trim().orEmpty()
            if (trimmedPath.isEmpty()) return DEFAULT_WSL_HLS_COMMAND

            val uncPath = WslSupport.parseWslUncPath(trimmedPath)
            if (uncPath != null && uncPath.first.equals(distroName, ignoreCase = true)) {
                return uncPath.second
            }

            if (trimmedPath.startsWith("/")) return trimmedPath

            // Windows host paths are not executable inside WSL; fall back to the distro PATH.
            return DEFAULT_WSL_HLS_COMMAND
        }

        internal fun shellQuote(value: String): String = "'${value.replace("'", "'\"'\"'")}'"

        private val LOG = logger<WslHaskellLanguageServer>()

        private fun createRewriterThread(
            name: String,
            input: InputStream,
            output: OutputStream,
            rewrite: (String) -> String
        ): Thread {
            return Thread({
                try {
                    rewriteLoop(input, output, rewrite)
                } catch (_: InterruptedException) {
                } catch (_: java.io.IOException) {
                }
            }, name).apply { isDaemon = true }
        }

        /**
         * Reads JSON-RPC framed messages from [input], applies [rewrite] to each message body,
         * and writes the rewritten message (with updated Content-Length) to [output].
         */
        private fun rewriteLoop(input: InputStream, output: OutputStream, rewrite: (String) -> String) {
            while (!Thread.currentThread().isInterrupted) {
                // Read headers until empty line
                var contentLength = -1
                while (true) {
                    val line = readLine(input) ?: return
                    if (line.isEmpty()) break
                    if (line.startsWith("Content-Length:", ignoreCase = true)) {
                        contentLength = line.substringAfter(":").trim().toIntOrNull() ?: -1
                    }
                }

                if (contentLength < 0) {
                    LOG.warn("WSL LSP rewriter: received message with missing or invalid Content-Length; closing stream")
                    return
                }

                // Read exactly contentLength bytes
                val bodyBytes = ByteArray(contentLength)
                var read = 0
                while (read < contentLength) {
                    val n = input.read(bodyBytes, read, contentLength - read)
                    if (n == -1) return
                    read += n
                }

                // Rewrite URIs in the message body
                val body = String(bodyBytes, Charsets.UTF_8)
                val rewritten = rewrite(body)
                val rewrittenBytes = rewritten.toByteArray(Charsets.UTF_8)

                // Write reframed message
                synchronized(output) {
                    output.write("Content-Length: ${rewrittenBytes.size}\r\n\r\n".toByteArray(Charsets.UTF_8))
                    output.write(rewrittenBytes)
                    output.flush()
                }
            }
        }

        /**
         * Reads a single line terminated by \r\n from the input stream.
         * Returns null on EOF.
         */
        private fun readLine(input: InputStream): String? {
            val sb = StringBuilder()
            while (true) {
                val b = input.read()
                if (b == -1) return null
                if (b == '\r'.code) {
                    val next = input.read()
                    if (next == '\n'.code) return sb.toString()
                    sb.append('\r')
                    if (next == -1) return null
                    sb.append(next.toChar())
                } else {
                    sb.append(b.toChar())
                }
            }
        }
    }
}
