package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.history.actions.AbstractImportTestsAction.ImportRunProfile
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import kotlin.io.path.Path

class CabalRunConfiguration(
    project: Project?, factory: ConfigurationFactory?, name: String?
) : RunConfigurationBase<CabalRunConfigurationOptions?>(project!!, factory, name) {
    public override fun getOptions(): CabalRunConfigurationOptions {
        return super.getOptions() as CabalRunConfigurationOptions
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

                val commandLine = GeneralCommandLine("cabal", options.command!!)
                if(options.command == "test" && options.testFramework == TestFramework.TastyAntXml) {
                    val tempFile = Path(FileUtilRt.getTempDirectory()).resolve("test.xml")
                    if(tempFile.toFile().exists()) {
                        tempFile.toFile().delete()
                    }
                    val testFile = FileUtilRt.createTempFile("test", ".xml", true)
                    commandLine.addParameters("--test-option=--xml=${testFile.absolutePath}")
                    commandLine.addParameters("--test-show-details=always")
                }
                commandLine.workDirectory = File(project.basePath!!)
                val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
            override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
                val processHandler = startProcess()
                val consoleView = if (options.command == "test") {
                    when (options.testFramework) {
                        TestFramework.TastyAntXml -> {
                            processHandler.addProcessListener(object : ProcessAdapter() {
                                override fun processTerminated(event: ProcessEvent) {
                                    val tempFile = File(Path(FileUtilRt.getTempDirectory()).resolve("test.xml").toString())
                                    if (!tempFile.exists()) {
                                        throw ExecutionException("Test output file not found")
                                    }

                                    val profile = ImportRunProfile(
                                        LocalFileSystem.getInstance().findFileByIoFile(tempFile),
                                        project,
                                        executor
                                    )
                                    val defaultExecutor = DefaultRunExecutor.getRunExecutorInstance()
                                    val builder = ExecutionEnvironmentBuilder.create(project, defaultExecutor, profile)
                                    builder.executor(executor)

                                    builder.target(profile.target)
                //                            if (executionId != null) {
                //                                builder.executionId(executionId)
                //                            }
                                    builder.buildAndExecute()

                                }
                            })
                            SMTestRunnerConnectionUtil.createAndAttachConsole(
                                "Cabal", processHandler, SMTRunnerConsoleProperties(
                                    this@CabalRunConfiguration,
                                    "Cabal",
                                    executor
                                )
                            )
                        }
                        TestFramework.Hspec -> {
                            val testConsoleProperties = CabalTestConsoleProperties(this@CabalRunConfiguration, executor)
                            SMTestRunnerConnectionUtil.createAndAttachConsole("Cabal", processHandler, testConsoleProperties)
                        }
                        TestFramework.None -> {
                            SMTestRunnerConnectionUtil.createAndAttachConsole("Cabal", processHandler, SMTRunnerConsoleProperties(this@CabalRunConfiguration, "Cabal", executor))
                        }
                    }
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
