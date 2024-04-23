package boo.fox.haskelllsp.language

import com.intellij.lexer.FlexAdapter

class HaskellLexerAdapter() : FlexAdapter(HaskellLexer(null))