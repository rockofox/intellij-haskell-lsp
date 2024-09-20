package boo.fox.haskelllsp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.util.EnvironmentUtil
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString

class HaskellLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider = HaskellLanguageServer(project)
    override fun createLanguageClient(project: Project): LanguageClientImpl = HaskellLanguageClient(project)
}

class HaskellLanguageServer(project: Project) : ProcessStreamConnectionProvider() {
    private fun findExecutableInPATH() =
        EnvironmentUtil.getEnvironmentMap().values.flatMap { it.split(File.pathSeparator) }
            .map { File(Paths.get(it, HLS_EXECUTABLE_NAME).pathString) }.find { it.exists() && it.canExecute() }?.path

    init {
        val hlsPath = findExecutableInPATH()
        if (!hlsPath.isNullOrEmpty()) {
            super.setCommands(listOf(hlsPath, "--lsp"))
            super.setWorkingDirectory(project.basePath)
        } else {
            NotificationGroupManager.getInstance().getNotificationGroup("Haskell LSP").createNotification(
                    "Haskell LSP",
                    "Haskell Language Server not found. Make sure it is installed properly (and is available in PATH), and restart the IDE.",
                    NotificationType.ERROR
                ).notify(project)
            LanguageServerManager.getInstance(project).stop("haskellLanguageServer")
        }
    }

    companion object {
        const val HLS_EXECUTABLE_NAME: String = "haskell-language-server-wrapper"
    }
}

// enabling the semanticTokens plugin is required for semantic tokens support
class HaskellLanguageClient(project: Project): LanguageClientImpl(project) {
    override fun createSettings(): Any? =
        mapOf("haskell" to mapOf("plugin" to mapOf("semanticTokens" to mapOf("globalOn" to true))))

    override fun handleServerStatusChanged(serverStatus: ServerStatus?) {
        if (serverStatus == ServerStatus.started) {
            triggerChangeConfiguration()
        }
    }
}
