package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.configurations.RunConfigurationOptions

class CabalRunConfigurationOptions : RunConfigurationOptions() {
    private val myCommand = string("").provideDelegate(this, "command")

    var command: String?
        get() = myCommand.getValue(this)
        set(command) {
            myCommand.setValue(this, command)
        }
}
