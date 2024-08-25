package boo.fox.haskelllsp

import com.google.gson.JsonParser
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.EnvironmentUtil
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import kotlin.io.path.pathString

class HaskellLanguageServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider = HaskellLanguageServer(project)
}

class HaskellLanguageServer(project: Project) : ProcessStreamConnectionProvider() {
    private fun findExecutableInPATH(executable: String) =
        EnvironmentUtil.getEnvironmentMap().values.flatMap { it.split(File.pathSeparator) }
            .map { File(Paths.get(it, executable).pathString) }.find { it.exists() && it.canExecute() }?.path

    init {
        val hlsPath = findExecutableInPATH("haskell-language-server-wrapper")
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

    override fun getInitializationOptions(rootUri: VirtualFile?): Any? {
        if (rootUri !== null) {
            val hlsConf = rootUri.findFileByRelativePath("hls.json")

            if (hlsConf != null && hlsConf.exists()) {
                val initializationOptions = JsonParser.parseReader(FileReader(hlsConf.path))
                return initializationOptions
            }
        }

        return null;
    }
}