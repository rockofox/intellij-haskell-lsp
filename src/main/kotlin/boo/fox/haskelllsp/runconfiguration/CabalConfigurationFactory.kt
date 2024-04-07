package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.configurations.*
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class CabalConfigurationFactory(type: ConfigurationType?) : ConfigurationFactory(
    type!!
) {
    override fun getId(): String {
        return CabalRunConfigurationType.ID
    }

    override fun createTemplateConfiguration(
        project: Project
    ): RunConfiguration {
        return CabalRunConfiguration(project, this, "Cabal")
    }

    override fun getOptionsClass(): Class<out BaseState?> {
        return CabalRunConfigurationOptions::class.java
    }
}
