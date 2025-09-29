package boo.fox.haskelllsp.language

import com.intellij.lexer.Lexer
import com.intellij.testFramework.LexerTestCase
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class HaskellLexerTest : LexerTestCase() {
    override fun createLexer(): Lexer {
        return HaskellLexerAdapter()
    }

    override fun getDirPath(): String {
        return ""
    }

    fun testKeywords() {
        doTest("case", "HaskellTokenType.CASE ('case')")
        doTest("class", "HaskellTokenType.CLASS ('class')")
        doTest("data", "HaskellTokenType.DATA ('data')")
        doTest("default", "HaskellTokenType.DEFAULT ('default')")
        doTest("deriving", "HaskellTokenType.DERIVING ('deriving')")
        doTest("do", "HaskellTokenType.DO ('do')")
        doTest("else", "HaskellTokenType.ELSE ('else')")
        doTest("if", "HaskellTokenType.IF ('if')")
        doTest("import", "HaskellTokenType.IMPORT ('import')")
        doTest("in", "HaskellTokenType.IN ('in')")
        doTest("infix", "HaskellTokenType.INFIX ('infix')")
        doTest("infixl", "HaskellTokenType.INFIXL ('infixl')")
        doTest("infixr", "HaskellTokenType.INFIXR ('infixr')")
        doTest("instance", "HaskellTokenType.INSTANCE ('instance')")
        doTest("let", "HaskellTokenType.LET ('let')")
        doTest("module", "HaskellTokenType.MODULE ('module')")
        doTest("newtype", "HaskellTokenType.NEWTYPE ('newtype')")
        doTest("of", "HaskellTokenType.OF ('of')")
        doTest("then", "HaskellTokenType.THEN ('then')")
        doTest("type", "HaskellTokenType.TYPE ('type')")
        doTest("where", "HaskellTokenType.WHERE ('where')")
    }
}
