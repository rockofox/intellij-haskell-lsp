package boo.fox.haskelllsp.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
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
        settings.hlsPath = settingsComponent?.getHlsPath() ?: ""
    }

    override fun reset() {
        val settings = HaskellLspSettings.getInstance()
        settingsComponent?.setHlsPath(settings.hlsPath)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}