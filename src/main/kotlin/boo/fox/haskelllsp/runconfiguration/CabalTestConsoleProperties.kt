package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator

class CabalTestConsoleProperties(config: RunConfiguration, executor: Executor) : SMTRunnerConsoleProperties(
    config,
    "Cabal",
    executor
), SMCustomMessagesParsing {
    override fun getTestFrameworkName(): String = "Cabal"
    override fun getTestLocator(): SMTestLocator = CabalTestLocator()
    override fun createTestEventsConverter(
        testFrameworkName: String,
        consoleProperties: TestConsoleProperties
    ): OutputToGeneralTestEventsConverter {
        return CabalTestOutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties)
    }
}