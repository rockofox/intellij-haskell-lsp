package boo.fox.haskelllsp.wsl

import kotlin.test.Test
import kotlin.test.assertEquals

class WslLaunchCommandTest {

    @Test
    fun `uses default wrapper when no custom path is configured`() {
        assertEquals(
            "exec 'haskell-language-server-wrapper' --lsp",
            WslHaskellLanguageServer.buildLaunchCommand("", "Ubuntu")
        )
    }

    @Test
    fun `keeps linux custom path and shell quotes it`() {
        assertEquals(
            "exec '/home/user/bin/haskell language server' --lsp",
            WslHaskellLanguageServer.buildLaunchCommand("/home/user/bin/haskell language server", "Ubuntu")
        )
    }

    @Test
    fun `converts matching wsl unc path to linux path`() {
        assertEquals(
            "/home/user/.local/bin/hls",
            WslHaskellLanguageServer.resolveWslExecutable("""\\wsl.localhost\Ubuntu\home\user\.local\bin\hls""", "Ubuntu")
        )
    }

    @Test
    fun `falls back to wrapper for windows host path`() {
        assertEquals(
            "haskell-language-server-wrapper",
            WslHaskellLanguageServer.resolveWslExecutable("""C:\Users\user\AppData\Local\Programs\hls.exe""", "Ubuntu")
        )
    }

    @Test
    fun `returns linux absolute path unchanged`() {
        assertEquals(
            "/home/user/.ghcup/bin/haskell-language-server",
            WslHaskellLanguageServer.resolveWslExecutable("/home/user/.ghcup/bin/haskell-language-server", "Ubuntu")
        )
    }

    @Test
    fun `shell quotes single quotes safely`() {
        assertEquals(
            "exec '/home/user/it'\"'\"'s hls' --lsp",
            WslHaskellLanguageServer.buildLaunchCommand("/home/user/it's hls", "Ubuntu")
        )
    }
}
