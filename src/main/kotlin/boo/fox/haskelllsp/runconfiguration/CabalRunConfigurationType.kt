package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NotNullLazyValue

internal class CabalRunConfigurationType : ConfigurationTypeBase(
    ID, "Cabal", "Cabal run configuration type",
    NotNullLazyValue.createValue { AllIcons.Nodes.Console }) {
    init {
        addFactory(CabalConfigurationFactory(this))
    }

    companion object {
        const val ID: String = "CabalRunConfiguration"
    }
}
