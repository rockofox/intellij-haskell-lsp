package boo.fox.haskelllsp.evaluation

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.EnvironmentUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Service
open class HaskellEvaluationService(private val project: Project) {
    companion object {
        fun getInstance(project: Project): HaskellEvaluationService = project.service()

        private val GHCi_EXECUTABLE_NAME = if (SystemInfo.isWindows) "ghci.exe" else "ghci"
        private val PROMPT_REGEX = Regex("(?m)^.*>\\s?$")
    }

    internal val evaluationCache = ConcurrentHashMap<String, EvaluationResult>()
    private var ghciProcess: ProcessHandler? = null

    data class EvaluationResult(
        val result: String,
        val error: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    )

    open fun evaluateExpressionAsync(
        expression: String,
        filePath: String? = null,
        callback: (EvaluationResult) -> Unit
    ) {
        val projectRef = project
        val filePart = filePath?.let { " (file: ${it.substringAfterLast('/')})" } ?: ""
        Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Started", "Evaluating: $expression$filePart", NotificationType.INFORMATION), projectRef)

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val result = evaluateExpression(expression, filePath)
                ApplicationManager.getApplication().invokeLater {
                    Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Callback", "Got result for: $expression$filePart", NotificationType.INFORMATION), projectRef)
                    callback(result)
                }
            } catch (e: Exception) {
                ApplicationManager.getApplication().invokeLater {
                    Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Error", e.message ?: "Unknown error", NotificationType.ERROR), projectRef)
                    callback(EvaluationResult("", e.message ?: "Unknown error"))
                }
            }
        }
    }

    @Synchronized
    private fun evaluateExpression(expression: String, filePath: String? = null): EvaluationResult {
        val cacheKey = (filePath ?: "") + "|" + expression
        val cached = evaluationCache[cacheKey]
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 30000) {
            return cached
        }

        val result = runGhciExpression(expression, filePath)
        evaluationCache[cacheKey] = result
        return result
    }

    private fun runGhciExpression(expression: String, filePath: String? = null): EvaluationResult {
        val processHandler = getOrCreateGhciProcess()
        val loadFuture = CompletableFuture<String>()
        val evalFuture = CompletableFuture<String>()
        val errorFuture = CompletableFuture<String>()
        val MARKER = "__INTELLIJ_HASKELL_EVAL_MARKER__"
        val loadPending = AtomicBoolean(false)
        val evalPending = AtomicBoolean(false)

        val output = StringBuilder()
        val errorOutput = StringBuilder()

        val listener = object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                val text = event.text
                when (outputType) {
                    ProcessOutputType.STDOUT -> {
                        output.append(text)
                        // If we're waiting for eval result, prefer the marker
                        if (evalPending.get()) {
                            if (output.contains(MARKER)) {
                                evalFuture.complete(output.toString())
                                return
                            }
                            if (PROMPT_REGEX.containsMatchIn(output.toString())) {
                                evalFuture.complete(output.toString())
                                return
                            }
                        } else if (loadPending.get()) {
                            if (PROMPT_REGEX.containsMatchIn(output.toString())) {
                                loadFuture.complete(output.toString())
                                return
                            }
                        }
                    }
                    ProcessOutputType.STDERR -> {
                        errorOutput.append(text)
                        errorFuture.complete(errorOutput.toString())
                    }
                }
            }

            override fun processTerminated(event: ProcessEvent) {
                if (!evalFuture.isDone) {
                    evalFuture.complete(output.toString())
                }
                if (!loadFuture.isDone) {
                    loadFuture.complete(output.toString())
                }
                if (!errorFuture.isDone) {
                    errorFuture.complete(errorOutput.toString())
                }
            }
        }

        processHandler.addProcessListener(listener)

        try {
            // If a file path is provided, :load it in GHCi first and wait for prompt
            if (filePath != null) {
                loadPending.set(true)
                val loadCmd = ":load ${filePath.replace("\\", "/")}\n"
                processHandler.processInput?.write(loadCmd.toByteArray())
                processHandler.processInput?.flush()

                // Wait for GHCi to finish loading the module
                try {
                    loadFuture.get(20, java.util.concurrent.TimeUnit.SECONDS)
                } finally {
                    loadPending.set(false)
                }

                // Clear output buffer so evaluation reads only new output
                synchronized(output) { output.setLength(0) }
            }

            // Send the expression to GHCi, then a marker-print to reliably detect completion
            evalPending.set(true)
            val markerCmd = "putStrLn \"$MARKER\"\n"
            processHandler.processInput?.write(("$expression\n").toByteArray())
            processHandler.processInput?.write(markerCmd.toByteArray())
            processHandler.processInput?.flush()

            // Wait for result with timeout
            val result = try {
                evalFuture.get(30, java.util.concurrent.TimeUnit.SECONDS)
            } finally {
                evalPending.set(false)
            }
            val error = if (errorFuture.isDone) errorFuture.get() else null

            return EvaluationResult(parseGhciOutput(result, expression), error)
        } catch (e: Exception) {
            return EvaluationResult("", e.message ?: "Timeout or error occurred")
        } finally {
            processHandler.removeProcessListener(listener)
        }
    }

    private fun getOrCreateGhciProcess(): ProcessHandler {
        if (ghciProcess?.isProcessTerminated != false) {
            ghciProcess = createGhciProcess()
        }
        return ghciProcess!!
    }

    private fun createGhciProcess(): ProcessHandler {
        val ghciPath = findGhciExecutable() ?: throw IllegalStateException("GHCi not found")

        val commandLine = GeneralCommandLine(ghciPath)
        val workDir = File(project.basePath ?: ".")
        if (!workDir.exists()) {
            workDir.mkdirs()
        }
        commandLine.workDirectory = workDir

        val processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)

        val latch = CountDownLatch(1)
        val listener = object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                // Detect a generic GHCi-like prompt (e.g. Prelude>, λ>, etc.)
                if (PROMPT_REGEX.containsMatchIn(event.text)) {
                    latch.countDown()
                    processHandler.removeProcessListener(this)
                }
            }
        }
        processHandler.addProcessListener(listener)

        processHandler.startNotify()

        // Give GHCi a moment and send a newline to prompt if needed
        try {
            processHandler.processInput?.write("\n".toByteArray())
            processHandler.processInput?.flush()
        } catch (e: Exception) {
            // Ignore; some handlers may not expose input
        }

        // Wait for GHCi to start
        if (!latch.await(20, TimeUnit.SECONDS)) {
            processHandler.removeProcessListener(listener) // Make sure to remove listener on timeout
            throw IllegalStateException("Timeout: GHCi did not start in 20 seconds")
        }

        return processHandler
    }

    private fun findGhciExecutable(): String? {
        // For now, search in PATH
        return EnvironmentUtil.getValue("PATH")?.split(File.pathSeparator)?.firstNotNullOfOrNull { path ->
            File(path, GHCi_EXECUTABLE_NAME).takeIf { it.canExecute() }?.absolutePath
        }
    }

    private fun parseGhciOutput(output: String, sentExpression: String): String {
        val MARKER = "__INTELLIJ_HASKELL_EVAL_MARKER__"
        val lines = output.split("\n").map { it.trimEnd() }

        // If marker is present, cut output up to marker and analyze only before it
        val beforeMarker = if (output.contains(MARKER)) {
            output.substringBefore(MARKER).split("\n").map { it.trimEnd() }
        } else lines

        // Remove lines that are prompts, echoed expression, load messages, or empty
        val filtered = beforeMarker.filter { line ->
            val empty = line.trim().isEmpty()
            val isPrompt = PROMPT_REGEX.matches(line) || line.endsWith(">")
            val isEcho = line == sentExpression.trim()
            val isLoadMsg = line.startsWith("Ok, one module loaded") || line.startsWith("[1 of") || line.contains("loaded")
            val isLeaving = line.contains("Leaving GHCi")
            !empty && !isPrompt && !isEcho && !isLoadMsg && !isLeaving
        }

        // Try to find the last non-prompt line which is most likely the result
        val candidate = filtered.lastOrNull() ?: beforeMarker.filter { !PROMPT_REGEX.matches(it) && it.isNotEmpty() }.lastOrNull()
        return candidate?.trim() ?: output.trim()
    }

    fun clearCache() {
        evaluationCache.clear()
    }

    open fun shutdown() {
        ghciProcess?.destroyProcess()
    }
}
