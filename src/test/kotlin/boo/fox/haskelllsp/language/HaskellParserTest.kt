package boo.fox.haskelllsp.language

import com.intellij.testFramework.ParsingTestCase

class HaskellParserTest : ParsingTestCase("parser", "hs", HaskellParserDefinition()) {
    override fun getTestDataPath() = System.getProperty("user.dir") + "/src/test/testData"

    fun testFunctionDefinition() = doTest(true)
}