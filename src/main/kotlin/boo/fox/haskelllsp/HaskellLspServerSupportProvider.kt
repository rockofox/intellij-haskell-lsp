@file:Suppress("UnstableApiUsage")

package boo.fox.haskelllsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

internal class HaskellLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {
        if (file.extension == "hs") {
            serverStarter.ensureServerStarted(HaskellLspServerDescriptor(project))
        }
    }
}

private class HaskellLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "Haskell") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == "hs"
    override fun createCommandLine() = GeneralCommandLine("haskell-language-server-wrapper", "--lsp")
    override val lspGoToDefinitionSupport = true
}