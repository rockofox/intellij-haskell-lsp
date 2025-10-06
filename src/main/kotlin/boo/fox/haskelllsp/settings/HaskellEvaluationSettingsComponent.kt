package boo.fox.haskelllsp.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import javax.swing.*

class HaskellEvaluationSettingsComponent {
    val panel: JPanel = JPanel()
    private val ghciPathField = JTextField(20)
    private val autoEvaluateCheckBox = JCheckBox("Auto-evaluate expressions")
    private val timeoutSpinner = JSpinner(SpinnerNumberModel(10, 1, 60, 1))

    init {
        val layout = GroupLayout(panel)
        panel.layout = layout
        
        layout.autoCreateContainerGaps = true
        layout.autoCreateGaps = true
        
        val ghciPathLabel = JLabel("GHCi Path:")
        val timeoutLabel = JLabel("Timeout (seconds):")
        
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(ghciPathLabel)
                        .addComponent(ghciPathField)
                )
                .addComponent(autoEvaluateCheckBox)
                .addGroup(
                    layout.createSequentialGroup()
                        .addComponent(timeoutLabel)
                        .addComponent(timeoutSpinner)
                )
        )
        
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(ghciPathLabel)
                        .addComponent(ghciPathField)
                )
                .addComponent(autoEvaluateCheckBox)
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(timeoutLabel)
                        .addComponent(timeoutSpinner)
                )
        )
    }

    var ghciPath: String
        get() = ghciPathField.text
        set(value) { ghciPathField.text = value }

    var autoEvaluate: Boolean
        get() = autoEvaluateCheckBox.isSelected
        set(value) { autoEvaluateCheckBox.isSelected = value }

    var evaluationTimeout: Int
        get() = timeoutSpinner.value as Int
        set(value) { timeoutSpinner.value = value }
}