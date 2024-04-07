package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.configurations.RunConfigurationOptions

class CabalRunConfigurationOptions : RunConfigurationOptions() {
    private val myScriptName = string("").provideDelegate(this, "scriptName")

    var scriptName: String?
        get() = myScriptName.getValue(this)
        set(scriptName) {
            myScriptName.setValue(this, scriptName)
        }
}
