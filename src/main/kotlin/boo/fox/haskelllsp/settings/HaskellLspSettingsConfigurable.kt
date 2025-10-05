package boo.fox.haskelllsp.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import javax.swing.JComponent

class HaskellLspSettingsConfigurable(private val project: Project) : Configurable {
    private var settingsComponent: HaskellLspSettingsComponent? = null

    override fun getDisplayName(): String = "Haskell LSP"

    override fun createComponent(): JComponent {
        settingsComponent = HaskellLspSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings = HaskellLspSettings.getInstance()
        return settingsComponent?.getHlsPath() != settings.hlsPath
    }

    override fun apply() {
        val settings = HaskellLspSettings.getInstance()
        val oldPath = settings.hlsPath
        settings.hlsPath = settingsComponent?.getHlsPath() ?: ""
        
        // Restart language server if path changed
        if (oldPath != settings.hlsPath) {
            restartLanguageServer()
        }
    }
    
    private fun restartLanguageServer() {
        val manager = LanguageServerManager.getInstance(project)
        // Stop the server first, then restart it
        manager.stop("haskellLanguageServer")
        manager.start("haskellLanguageServer")
    }

    override fun reset() {
        val settings = HaskellLspSettings.getInstance()
        settingsComponent?.setHlsPath(settings.hlsPath)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}