package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.process.ProcessOutputType
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.history.actions.AbstractImportTestsAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import jetbrains.buildServer.messages.serviceMessages.*

class CabalTestOutputToGeneralTestEventsConverter(testFrameworkName: String, consoleProperties: TestConsoleProperties) :
    OutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties) {
    private val log: Logger = Logger.getInstance(CabalTestOutputToGeneralTestEventsConverter::class.java)
    private var previousTestSuite: String? = null
    private var testSuiteRunning = false
    private var expectingNewline = false
    private var unfinishedTestSuites = mutableListOf<String>()

    override fun processServiceMessages(text: String, outputType: Key<*>, visitor: ServiceMessageVisitor): Boolean {
        if(outputType == ProcessOutputType.STDERR) {
            return super.processServiceMessages(text, outputType, visitor)
        }
        Regex("""^\s+$""").find(text)?.let { matches ->
            if(expectingNewline) {
                expectingNewline = false
                testSuiteRunning = true
            } else {
                testSuiteRunning = false
            }
            if(unfinishedTestSuites.isNotEmpty()) {
                log.info("Unfinished test suites: $unfinishedTestSuites")
                unfinishedTestSuites.forEach {
                    visitor.visitTestSuiteFinished(TestSuiteFinished(it))
                }
                return super.processServiceMessages(text, outputType, visitor)
            } else {
                log.info("No unfinished test suites")
            }
            return super.processServiceMessages(text, outputType, visitor)
        }
        Regex("""Test suite (.+): RUNNING\.\.\.""").find(text)?.let { matches ->
            testSuiteRunning = true
            expectingNewline = true
            visitor.visitTestSuiteStarted(TestSuiteStarted(matches.groupValues[1]))
            return super.processServiceMessages(text, outputType, visitor)
        }
        Regex("""Test suite (.+): PASS""").find(text)?.let { matches ->
            testSuiteRunning = false
            expectingNewline = false
            visitor.visitTestSuiteFinished(TestSuiteFinished(matches.groupValues[1]))

            return super.processServiceMessages(text, outputType, visitor)
        }
        Regex("""( )(.+) \[([✘✔])]""").find(text)?.let { matches ->
            visitor.visitTestStarted(TestStarted(matches.groupValues[2], true, null))
            if (matches.groupValues[3] == "✘") {
                visitor.visitTestFailed(TestFailed(matches.groupValues[2], "Test failed"))
            }
            visitor.visitTestFinished(TestFinished(matches.groupValues[2], 1))
            return super.processServiceMessages(text, outputType, visitor)
        }
        if (testSuiteRunning) {
            Regex("""( +)?([\w-]+)""").find(text)?.let { matches ->
                if (previousTestSuite != null) {
                    visitor.visitTestSuiteFinished(TestSuiteFinished(previousTestSuite!!))
                    unfinishedTestSuites.remove(previousTestSuite!!)
                }
                visitor.visitTestSuiteStarted(TestSuiteStarted(matches.groupValues[2]))
                unfinishedTestSuites.add(matches.groupValues[2])
                previousTestSuite = matches.groupValues[2]
            }
            return super.processServiceMessages(text, outputType, visitor)
        }

        return super.processServiceMessages(text, outputType, visitor)
    }
}