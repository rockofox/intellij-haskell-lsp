package boo.fox.haskelllsp.evaluation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.psi.PsiFile
import org.junit.Assert.*

class EvalCommentAnnotatorTest : BasePlatformTestCase() {
    
    private lateinit var annotator: EvalCommentAnnotator
    
    override fun setUp() {
        super.setUp()
        annotator = EvalCommentAnnotator()
    }
    
    fun testDetectsEvalCommentWithArrow() {
        val file = myFixture.configureByText("Test.hs", """
            -- >>> reverseList [1,2,3]
            reverseList :: [a] -> [a]
            reverseList = foldl (flip (:)) []
        """.trimIndent())
        
        val annotations = myFixture.doHighlighting()
        assertTrue("Should find evaluation annotation", annotations.isNotEmpty())
    }
    
    fun testIgnoresRegularComments() {
        val file = myFixture.configureByText("Test.hs", """
            -- This is a regular comment
            main = putStrLn "hello"
        """.trimIndent())
        
        val annotations = myFixture.doHighlighting()
        assertTrue("Should not find evaluation annotation for regular comment", 
                   annotations.all { !it.toString().contains("Evaluate") })
    }
    
    fun testDetectsMultipleEvalComments() {
        val file = myFixture.configureByText("Test.hs", """
            -- >>> 1 + 1
            -- >>> map (+1) [1,2,3]
            main = putStrLn "hello"
        """.trimIndent())
        
        val annotations = myFixture.doHighlighting()
        val evalAnnotations = annotations.filter { it.toString().contains("Evaluate") }
        assertEquals("Should find 2 evaluation annotations", 2, evalAnnotations.size)
    }
    
    fun testHandlesComplexExpression() {
        val file = myFixture.configureByText("Test.hs", """
            -- >>> foldr (:) [] [1,2,3] ++ [4,5]
            main = putStrLn "hello"
        """.trimIndent())
        
        val annotations = myFixture.doHighlighting()
        assertTrue("Should find evaluation annotation for complex expression", 
                   annotations.any { it.toString().contains("Evaluate") })
    }
}