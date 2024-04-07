package boo.fox.haskelllsp.runconfiguration

import boo.fox.haskelllsp.Icons
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.NotNullLazyValue

internal class CabalRunConfigurationType : ConfigurationTypeBase(
    ID, "Cabal", "Cabal run configuration type",
    NotNullLazyValue.createValue { Icons.Cabal }) {
    init {
        addFactory(CabalConfigurationFactory(this))
    }

    companion object {
        const val ID: String = "CabalRunConfiguration"
    }
}
