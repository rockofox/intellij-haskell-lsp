package boo.fox.haskelllsp.language

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.psi.codeStyle.CodeStyleManager

class HaskellFormatterTest : BasePlatformTestCase() {
    fun testFormatting() {
        myFixture.configureByText("test.hs", "main=do\nputStrLn\"Hello\"")
        CodeStyleManager.getInstance(project).reformat(myFixture.file)
        myFixture.checkResult("main=do\nputStrLn\"Hello\"")
    }
}