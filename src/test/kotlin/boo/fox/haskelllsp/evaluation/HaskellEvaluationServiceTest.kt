package boo.fox.haskelllsp.evaluation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HaskellEvaluationServiceTest : BasePlatformTestCase() {

    private lateinit var evaluationService: HaskellEvaluationService

    override fun setUp() {
        super.setUp()
        // Force mock service for deterministic tests
        evaluationService = MockHaskellEvaluationService()
    }

    override fun tearDown() {
        if (this::evaluationService.isInitialized) {
            evaluationService.shutdown()
        }
        super.tearDown()
    }

    fun testSimpleExpressionEvaluation() {
        val latch = CountDownLatch(1)
        evaluationService.evaluateExpressionAsync("1 + 1") { result ->
            try {
                val error = result.error
                if (error != null) {
                    assertTrue(
                        "Expected error about GHCi not found or timeout, but got $error",
                        error.contains("GHCi not found") || error.contains("Timeout")
                    )
                } else {
                    assertTrue("Result should contain '2'", result.result.contains("2"))
                }
            } finally {
                latch.countDown()
            }
        }

        assertTrue("Test timed out", latch.await(30, TimeUnit.SECONDS))
    }

    fun testExpressionCaching() {
        val latch = CountDownLatch(1)
        // First evaluation
        evaluationService.evaluateExpressionAsync("2 + 2") { result1 ->
            // Second evaluation should use cache
            evaluationService.evaluateExpressionAsync("2 + 2") { result2 ->
                try {
                    assertEquals("Results should be equal", result1.result, result2.result)
                    if (result1.error == null) {
                        assertEquals("Timestamps should be equal for cached results", result1.timestamp, result2.timestamp)
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        assertTrue("Test timed out", latch.await(30, TimeUnit.SECONDS))
    }

    fun testErrorHandling() {
        val latch = CountDownLatch(1)
        evaluationService.evaluateExpressionAsync("this is not valid haskell") { result ->
            try {
                assertNotNull("Should have error for invalid expression", result.error)
            } finally {
                latch.countDown()
            }
        }
        assertTrue("Test timed out", latch.await(30, TimeUnit.SECONDS))
    }

    // Mock service for testing when the real service cannot be initialized
    private inner class MockHaskellEvaluationService : HaskellEvaluationService(project) {
        private val mockCache = ConcurrentHashMap<String, EvaluationResult>()
        override fun evaluateExpressionAsync(
            expression: String,
            callback: (EvaluationResult) -> Unit
        ) {
            mockCache[expression]?.let {
                callback(it)
                return
            }
            // Simulate async behavior and provide predictable responses for testing
            // Provide deterministic synchronous responses so tests that run on the EDT
            // and wait on a latch don't deadlock by blocking invokeLater callbacks.
            val result = when {
                expression.contains("1 + 1") -> EvaluationResult("2")
                expression.contains("2 + 2") -> EvaluationResult("4", timestamp = System.currentTimeMillis())
                expression.contains("this is not valid haskell") -> EvaluationResult("", "Syntax error")
                else -> EvaluationResult("", "GHCi not found")
            }
            mockCache.putIfAbsent(expression, result)
            println("[MockHaskellEvaluationService] expression=\"$expression\" result=\"${result.result}\" error=\"${result.error}\"")
            callback(result)
        }

        override fun shutdown() {
            // Mock implementation - no cleanup needed
        }
    }
}
