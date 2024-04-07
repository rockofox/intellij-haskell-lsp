package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.io.File

class CabalRunConfiguration(
    project: Project?,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<CabalRunConfigurationOptions?>(project!!, factory, name) {
    override fun getOptions(): CabalRunConfigurationOptions {
        return super.getOptions() as CabalRunConfigurationOptions
    }

    var scriptName: String?
        get() = options.scriptName
        set(scriptName) {
            options.scriptName = scriptName
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return CabalSettingsEditor()
    }

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState {
        return object : CommandLineState(environment) {
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val commandLine =
                    GeneralCommandLine("cabal", "run")
                commandLine.workDirectory = File(project.basePath!!)
                val processHandler = ProcessHandlerFactory.getInstance()
                    .createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }
}
