package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.configurations.RunConfigurationOptions

class CabalRunConfigurationOptions : RunConfigurationOptions() {
    private val myCommand = string("").provideDelegate(this, "command")
    private val myTestFramework = `enum`(TestFramework.TastyAntXml).provideDelegate(this, "testFramework")

    var command: String?
        get() = myCommand.getValue(this)
        set(command) {
            myCommand.setValue(this, command)
        }

    var testFramework: TestFramework
        get() = myTestFramework.getValue(this)
        set(testFramework) {
            myTestFramework.setValue(this, testFramework)
        }
}
