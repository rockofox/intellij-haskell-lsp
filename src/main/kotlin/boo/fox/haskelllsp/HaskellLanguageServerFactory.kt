package boo.fox.haskelllsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider

class HaskellLanguageServerFactory: LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        HaskellLanguageServer()
}

class HaskellLanguageServer: ProcessStreamConnectionProvider() {
    init {
        super.setCommands(listOf("haskell-language-server-wrapper", "--lsp"))
    }
}