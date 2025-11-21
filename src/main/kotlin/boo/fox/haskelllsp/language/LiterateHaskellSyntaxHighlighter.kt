package boo.fox.haskelllsp.language

import boo.fox.haskelllsp.language.psi.HaskellTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Syntax highlighter for Literate Haskell (.lhs) files.
 * Uses LiterateHaskellLexerAdapter to properly handle Bird style formatting.
 */
class LiterateHaskellSyntaxHighlighter: SyntaxHighlighterBase() {
    companion object {
        val Illegal = createTextAttributesKey("HS_ILLEGAL", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)
        val Comment = createTextAttributesKey("HS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BlockComment = createTextAttributesKey("HS_NCOMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val DocComment = createTextAttributesKey("HS_HADDOCK", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val BlockDocComment = createTextAttributesKey("HS_NHADDOCK", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val String = createTextAttributesKey("HS_STRING", DefaultLanguageHighlighterColors.STRING)
        val Number = createTextAttributesKey("HS_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val Keyword = createTextAttributesKey("HS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val FunctionName = createTextAttributesKey("HS_FUNCTION_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val Parentheses = createTextAttributesKey("HS_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val Brace = createTextAttributesKey("HSL_BRACE", DefaultLanguageHighlighterColors.BRACES)
        val Bracket = createTextAttributesKey("HS_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
        val Variable = createTextAttributesKey("HS_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
        val PragmaContent = createTextAttributesKey("HS_PRAGMA_CONTENT", DefaultLanguageHighlighterColors.IDENTIFIER)
        val Constructor = createTextAttributesKey("HS_CONSTRUCTOR", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val Operator = createTextAttributesKey("HS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val ReservedSymbol = createTextAttributesKey("HS_SYMBOL", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)
        val Pragma = createTextAttributesKey("HS_PRAGMA", DefaultLanguageHighlighterColors.METADATA)
        val Quasiquote = createTextAttributesKey("HS_QUASI_QUOTES", DefaultLanguageHighlighterColors.METADATA)
        val Default = createTextAttributesKey("HS_DEFAULT", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
    }
    
    override fun getHighlightingLexer(): Lexer {
        return LiterateHaskellLexerAdapter(HaskellLexerAdapter())
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        // Reuse the same highlighting logic as regular Haskell files
        // The lexer will handle filtering out non-code lines
        return when(tokenType) {
            TokenType.BAD_CHARACTER -> arrayOf(Illegal)
            TokenType.WHITE_SPACE -> arrayOf() // Text lines in literate Haskell (no highlighting)
            HaskellTypes.HS_COMMENT -> arrayOf(Comment)
            HaskellTypes.HS_NCOMMENT -> arrayOf(BlockComment)
            HaskellTypes.HS_HADDOCK -> arrayOf(DocComment)
            HaskellTypes.HS_NHADDOCK -> arrayOf(BlockDocComment)
            HaskellTypes.HS_STRING_LITERAL -> arrayOf(String)
            HaskellTypes.HS_FLOAT -> arrayOf(Number)
            HaskellTypes.HS_IMPORT -> arrayOf(Keyword)
            HaskellTypes.HS_MODULE -> arrayOf(Keyword)
            HaskellTypes.HS_WHERE -> arrayOf(Keyword)
            HaskellTypes.HS_DO -> arrayOf(Keyword)
            HaskellTypes.HS_LET -> arrayOf(Keyword)
            HaskellTypes.HS_OF -> arrayOf(Keyword)
            HaskellTypes.HS_THEN -> arrayOf(Keyword)
            HaskellTypes.HS_ELSE -> arrayOf(Keyword)
            HaskellTypes.HS_CASE -> arrayOf(Keyword)
            HaskellTypes.HS_IF -> arrayOf(Keyword)
            HaskellTypes.HS_DATA -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE -> arrayOf(Keyword)
            HaskellTypes.HS_NEWTYPE -> arrayOf(Keyword)
            HaskellTypes.HS_CLASS -> arrayOf(Keyword)
            HaskellTypes.HS_INSTANCE -> arrayOf(Keyword)
            HaskellTypes.HS_DEFAULT -> arrayOf(Keyword)
            HaskellTypes.HS_EXPORT -> arrayOf(Keyword)
            HaskellTypes.HS_FORALL -> arrayOf(Keyword)
            HaskellTypes.HS_INFIX -> arrayOf(Keyword)
            HaskellTypes.HS_INFIXL -> arrayOf(Keyword)
            HaskellTypes.HS_INFIXR -> arrayOf(Keyword)
            HaskellTypes.HS_PRAGMA -> arrayOf(Pragma)
            HaskellTypes.HS_LEFT_PAREN -> arrayOf(Parentheses)
            HaskellTypes.HS_RIGHT_PAREN -> arrayOf(Parentheses)
            HaskellTypes.HS_LEFT_BRACE -> arrayOf(Brace)
            HaskellTypes.HS_RIGHT_BRACE -> arrayOf(Brace)
            HaskellTypes.HS_LEFT_BRACKET -> arrayOf(Bracket)
            HaskellTypes.HS_RIGHT_BRACKET -> arrayOf(Bracket)
            HaskellTypes.HS_DECIMAL -> arrayOf(Number)
            HaskellTypes.HS_Q_NAME -> arrayOf(FunctionName)
            HaskellTypes.HS_Q_NAMES -> arrayOf(FunctionName)
            HaskellTypes.HS_CONID -> arrayOf(Constructor)
            HaskellTypes.HS_VARID -> arrayOf(Variable)
            HaskellTypes.HS_CON_ID -> arrayOf(Constructor)
            HaskellTypes.HS_VAR_ID -> arrayOf(Variable)
            HaskellTypes.HS_VARSYM_ID -> arrayOf(Operator)
            HaskellTypes.HS_CONSYM_ID -> arrayOf(Operator)
            HaskellTypes.HS_VARSYM -> arrayOf(Operator)
            HaskellTypes.HS_CONSYM -> arrayOf(Operator)
            HaskellTypes.HS_CHARACTER_LITERAL -> arrayOf(String)
            HaskellTypes.HS_HEXADECIMAL -> arrayOf(Number)
            HaskellTypes.HS_OCTAL -> arrayOf(Number)
            else -> arrayOf(Default)
        }
    }
}
