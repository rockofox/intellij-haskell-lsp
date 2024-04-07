package boo.fox.haskelllsp.runconfiguration

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class CabalSettingsEditor : SettingsEditor<CabalRunConfiguration>() {
    private val myPanel: JPanel
    private val scriptPathField = TextFieldWithBrowseButton()

    init {
        scriptPathField.addBrowseFolderListener(
            "Select Script File", null, null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Script file", scriptPathField)
            .panel
    }

    override fun resetEditorFrom(cabalRunConfiguration: CabalRunConfiguration) {
        scriptPathField.text = cabalRunConfiguration.scriptName!!
    }

    override fun applyEditorTo(cabalRunConfiguration: CabalRunConfiguration) {
        cabalRunConfiguration.scriptName = scriptPathField.text
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
