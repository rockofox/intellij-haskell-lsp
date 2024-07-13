package boo.fox.haskelllsp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import okio.IOException

class HaskellLanguageServerFactory: LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        HaskellLanguageServer(project)
}

class HaskellLanguageServer(private val project: Project): ProcessStreamConnectionProvider() {

    private fun findHls(): String = if (try { ProcessBuilder(LANGUAGE_SERVER_COMMAND, "--version").start() } catch (_: IOException) { null }?.waitFor() == 0) {
        LANGUAGE_SERVER_COMMAND
    } else {
        (try { ProcessBuilder(GHCUP_COMMAND, "whereis", "hls").start() } catch (_: IOException) { null })?.takeIf { it.waitFor() == 0 }?.inputReader()?.readText()
            ?.trim().let {
                if(it == null) {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Haskell LSP")
                        .createNotification("Haskell LSP", "Haskell Language Server not found. Make sure it is installed properly (and is available in PATH or via ghcup), and restart the IDE.", NotificationType.ERROR)
                        .notify(project)
                    LanguageServerManager.getInstance(project).stop("haskellLanguageServer")
                    throw IllegalStateException("Haskell Language Server not found")
                } else {
                    it
                }
            }
    }

    init {
        super.setCommands(listOf(findHls(), "--lsp"))
    }

    companion object {
        const val LANGUAGE_SERVER_COMMAND = "haskell-language-server-wrapper"
        const val GHCUP_COMMAND = "ghcup"
    }
}