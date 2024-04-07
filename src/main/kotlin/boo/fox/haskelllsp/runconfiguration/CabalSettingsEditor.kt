package boo.fox.haskelllsp.runconfiguration

import boo.fox.haskelllsp.Icons
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class CabalSettingsEditor(
    project: Project
) : SettingsEditor<CabalRunConfiguration>() {
    private val myPanel: JPanel
    private val commandCompletion = TextFieldWithAutoCompletion.StringsCompletionProvider(
        listOf("run", "build", "test", "bench", "repl", "v2-run", "v2-build", "v2-test", "v2-bench", "v2-repl")
    , Icons.Cabal)
    private val commandTextField = TextFieldWithAutoCompletion(project, commandCompletion, true, "run")

    init {
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Command", commandTextField)
            .panel
    }

    override fun resetEditorFrom(cabalRunConfiguration: CabalRunConfiguration) {
        commandTextField.text = cabalRunConfiguration.command!!
    }

    override fun applyEditorTo(cabalRunConfiguration: CabalRunConfiguration) {
        if (commandTextField.text.isEmpty()) {
            commandTextField.text = "run"
        }
        cabalRunConfiguration.command = commandTextField.text
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
