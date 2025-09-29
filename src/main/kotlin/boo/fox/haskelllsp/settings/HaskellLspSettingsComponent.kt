package boo.fox.haskelllsp.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class HaskellLspSettingsComponent {
    private val hlsPathField = TextFieldWithBrowseButton()
    val panel: JPanel

    init {
        hlsPathField.addBrowseFolderListener(
            "Select Haskell Language Server Path",
            "Choose the path to haskell-language-server-wrapper",
            null,
            FileChooserDescriptor(true, false, false, false, false, false)
        )

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Haskell Language Server path:"), hlsPathField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getHlsPath(): String = hlsPathField.text

    fun setHlsPath(path: String) {
        hlsPathField.text = path
    }
}