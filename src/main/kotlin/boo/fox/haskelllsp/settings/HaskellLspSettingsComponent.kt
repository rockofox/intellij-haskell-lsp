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
        
        // Set default placeholder text
        hlsPathField.text = DEFAULT_DISPLAY_TEXT
        
        // Add focus listener to clear default text when user starts editing
        hlsPathField.textField.addFocusListener(object : java.awt.event.FocusAdapter() {
            override fun focusGained(e: java.awt.event.FocusEvent?) {
                if (hlsPathField.text == DEFAULT_DISPLAY_TEXT) {
                    hlsPathField.text = ""
                }
            }
            
            override fun focusLost(e: java.awt.event.FocusEvent?) {
                if (hlsPathField.text.isEmpty()) {
                    hlsPathField.text = DEFAULT_DISPLAY_TEXT
                }
            }
        })

        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Haskell Language Server path:"), hlsPathField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getHlsPath(): String = 
        if (hlsPathField.text == DEFAULT_DISPLAY_TEXT) "" else hlsPathField.text

    fun setHlsPath(path: String) {
        hlsPathField.text = if (path.isEmpty()) DEFAULT_DISPLAY_TEXT else path
    }
    
    companion object {
        private const val DEFAULT_DISPLAY_TEXT = "Default (search in PATH)"
    }
}