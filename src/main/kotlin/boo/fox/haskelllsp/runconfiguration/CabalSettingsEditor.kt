package boo.fox.haskelllsp.runconfiguration

import boo.fox.haskelllsp.Icons
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

enum class TestFramework {
    TastyAntXml {
        override fun toString(): String {
            return "Tasty + tasty-ant-xml"
        }
    },
    Hspec {
        override fun toString(): String {
            return "Hspec (borked)"
        }
    },
    None {
        override fun toString(): String {
            return "None/Console"
        }
    }
}

class CabalSettingsEditor(
    project: Project
) : SettingsEditor<CabalRunConfiguration>() {
    private val myPanel: JPanel
    private val commandCompletion = TextFieldWithAutoCompletion.StringsCompletionProvider(
        listOf("run", "build", "test", "bench", "repl", "v2-run", "v2-build", "v2-test", "v2-bench", "v2-repl")
    , Icons.Cabal)
    private val commandTextField = TextFieldWithAutoCompletion(project, commandCompletion, true, "run")
    private val testFrameworkDropdown = ComboBox(TestFramework.values())
    private val testFrameworkLabel = JLabel("Test framework")

    private fun updateTestFrameworkComponentState() {
        testFrameworkDropdown.isVisible = commandTextField.text == "test"
        testFrameworkLabel.isVisible = commandTextField.text == "test"
    }

    init {
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Command", commandTextField)
            .addLabeledComponent(testFrameworkLabel, testFrameworkDropdown)
            .panel
        updateTestFrameworkComponentState()
        commandTextField.addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
            override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                updateTestFrameworkComponentState()
            }
        })
    }

    override fun resetEditorFrom(cabalRunConfiguration: CabalRunConfiguration) {
        commandTextField.text = cabalRunConfiguration.options.command!!
        testFrameworkDropdown.selectedItem = cabalRunConfiguration.options.testFramework
    }

    override fun applyEditorTo(cabalRunConfiguration: CabalRunConfiguration) {
        if (commandTextField.text.isEmpty()) {
            commandTextField.text = "run"
        }
        cabalRunConfiguration.options.command = commandTextField.text
        cabalRunConfiguration.options.testFramework = testFrameworkDropdown.selectedItem as TestFramework
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
