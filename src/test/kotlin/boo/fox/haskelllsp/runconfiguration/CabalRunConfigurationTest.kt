package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.testFramework.HeavyPlatformTestCase

class CabalRunConfigurationTest : HeavyPlatformTestCase() {
    fun testConfigurationCreation() {
        val factory = CabalRunConfigurationType().configurationFactories[0]
        val config = factory.createTemplateConfiguration(project)
        assertTrue("Configuration should be CabalRunConfiguration", config is CabalRunConfiguration)
        (config as CabalRunConfiguration).options.command = "build"
        assertEquals("build", (config as CabalRunConfiguration).options.command)
    }
}