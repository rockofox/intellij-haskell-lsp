package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File

class CabalRunConfiguration(
    project: Project?, factory: ConfigurationFactory?, name: String?
) : RunConfigurationBase<CabalRunConfigurationOptions?>(project!!, factory, name) {
    override fun getOptions(): CabalRunConfigurationOptions {
        return super.getOptions() as CabalRunConfigurationOptions
    }

    var command: String?
        get() = options.command
        set(command) {
            options.command = command
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return CabalSettingsEditor(project)
    }

    override fun getState(
        executor: Executor, environment: ExecutionEnvironment
    ): RunProfileState {
        return object : CommandLineState(environment) {
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val commandLine = GeneralCommandLine("cabal", options.command!!, "--test-show-details=always")
                commandLine.workDirectory = File(project.basePath!!)
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }

            override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
                val processHandler = startProcess()

                val consoleView = if (options.command == "test") {
                    val testConsoleProperties = CabalTestConsoleProperties(this@CabalRunConfiguration, executor)
                    SMTestRunnerConnectionUtil.createAndAttachConsole("Cabal", processHandler, testConsoleProperties)
                } else {
                    createConsole(executor).also { console ->
                        console!!.attachToProcess(processHandler)
                    }
                }

                return DefaultExecutionResult(consoleView, processHandler)
            }
        }
    }
}
