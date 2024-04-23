package boo.fox.haskelllsp.language

import boo.fox.haskelllsp.language.psi.HaskellTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class HaskellSyntaxHighlighter: SyntaxHighlighterBase() {
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
        return HaskellLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        //  IElementType HS_CCONTEXT = new IElementType("HS_CCONTEXT", null);
        //  IElementType HS_CDECL = new IElementType("HS_CDECL", null);
        //  IElementType HS_CDECLS = new IElementType("HS_CDECLS", null);
        //  IElementType HS_CDECL_DATA_DECLARATION = new IElementType("HS_CDECL_DATA_DECLARATION", null);
        //  IElementType HS_CIDECL = new IElementType("HS_CIDECL", null);
        //  IElementType HS_CIDECLS = new IElementType("HS_CIDECLS", null);
        //  IElementType HS_CLASS_DECLARATION = new IElementType("HS_CLASS_DECLARATION", null);
        //  IElementType HS_CLAZZ = new IElementType("HS_CLAZZ", null);
        //  IElementType HS_COMMENTS = new IElementType("HS_COMMENTS", null);
        //  IElementType HS_CONID = new IElementType("HS_CONID", null);
        //  IElementType HS_CONSTR = new IElementType("HS_CONSTR", null);
        //  IElementType HS_CONSTR_1 = new IElementType("HS_CONSTR_1", null);
        //  IElementType HS_CONSTR_2 = new IElementType("HS_CONSTR_2", null);
        //  IElementType HS_CONSTR_3 = new IElementType("HS_CONSTR_3", null);
        //  IElementType HS_CONSYM = new IElementType("HS_CONSYM", null);
        //  IElementType HS_DATA_DECLARATION = new IElementType("HS_DATA_DECLARATION", null);
        //  IElementType HS_DATA_DECLARATION_DERIVING = new IElementType("HS_DATA_DECLARATION_DERIVING", null);
        //  IElementType HS_DEFAULT_DECLARATION = new IElementType("HS_DEFAULT_DECLARATION", null);
        //  IElementType HS_DERIVING_DECLARATION = new IElementType("HS_DERIVING_DECLARATION", null);
        //  IElementType HS_DERIVING_VIA = new IElementType("HS_DERIVING_VIA", null);
        //  IElementType HS_DOT_DOT = new IElementType("HS_DOT_DOT", null);
        //  IElementType HS_EXPORT = new IElementType("HS_EXPORT", null);
        //  IElementType HS_EXPORTS = new IElementType("HS_EXPORTS", null);
        //  IElementType HS_EXPRESSION = new IElementType("HS_EXPRESSION", null);
        //  IElementType HS_FIELDDECL = new IElementType("HS_FIELDDECL", null);
        //  IElementType HS_FILE_HEADER = new IElementType("HS_FILE_HEADER", null);
        //  IElementType HS_FIXITY_DECLARATION = new IElementType("HS_FIXITY_DECLARATION", null);
        //  IElementType HS_FOREIGN_DECLARATION = new IElementType("HS_FOREIGN_DECLARATION", null);
        //  IElementType HS_GENERAL_PRAGMA_CONTENT = new IElementType("HS_GENERAL_PRAGMA_CONTENT", null);
        //  IElementType HS_GTYCON = new IElementType("HS_GTYCON", null);
        //  IElementType HS_IMPORT_DECLARATION = new IElementType("HS_IMPORT_DECLARATION", null);
        //  IElementType HS_IMPORT_DECLARATIONS = new IElementType("HS_IMPORT_DECLARATIONS", null);
        //  IElementType HS_IMPORT_EMPTY_SPEC = new IElementType("HS_IMPORT_EMPTY_SPEC", null);
        //  IElementType HS_IMPORT_HIDING = new IElementType("HS_IMPORT_HIDING", null);
        //  IElementType HS_IMPORT_HIDING_SPEC = new IElementType("HS_IMPORT_HIDING_SPEC", null);
        //  IElementType HS_IMPORT_ID = new IElementType("HS_IMPORT_ID", null);
        //  IElementType HS_IMPORT_IDS_SPEC = new IElementType("HS_IMPORT_IDS_SPEC", null);
        //  IElementType HS_IMPORT_PACKAGE_NAME = new IElementType("HS_IMPORT_PACKAGE_NAME", null);
        //  IElementType HS_IMPORT_QUALIFIED = new IElementType("HS_IMPORT_QUALIFIED", null);
        //  IElementType HS_IMPORT_QUALIFIED_AS = new IElementType("HS_IMPORT_QUALIFIED_AS", null);
        //  IElementType HS_IMPORT_SPEC = new IElementType("HS_IMPORT_SPEC", null);
        //  IElementType HS_INST = new IElementType("HS_INST", null);
        //  IElementType HS_INSTANCE_DECLARATION = new IElementType("HS_INSTANCE_DECLARATION", null);
        //  IElementType HS_INSTVAR = new IElementType("HS_INSTVAR", null);
        //  IElementType HS_KIND_SIGNATURE = new IElementType("HS_KIND_SIGNATURE", null);
        //  IElementType HS_LIST_TYPE = new IElementType("HS_LIST_TYPE", null);
        //  IElementType HS_MODID = new IElementType("HS_MODID", null);
        //  IElementType HS_MODULE_BODY = new IElementType("HS_MODULE_BODY", null);
        //  IElementType HS_MODULE_DECLARATION = new IElementType("HS_MODULE_DECLARATION", null);
        //  IElementType HS_NEWCONSTR = new IElementType("HS_NEWCONSTR", null);
        //  IElementType HS_NEWCONSTR_FIELDDECL = new IElementType("HS_NEWCONSTR_FIELDDECL", null);
        //  IElementType HS_NEWTYPE_DECLARATION = new IElementType("HS_NEWTYPE_DECLARATION", null);
        //  IElementType HS_PRAGMA = new IElementType("HS_PRAGMA", null);
        //  IElementType HS_QUALIFIER = new IElementType("HS_QUALIFIER", null);
        //  IElementType HS_QUASI_QUOTE = new IElementType("HS_QUASI_QUOTE", null);
        //  IElementType HS_Q_CON = new IElementType("HS_Q_CON", null);
        //  IElementType HS_Q_CON_QUALIFIER = new IElementType("HS_Q_CON_QUALIFIER", null);
        //  IElementType HS_Q_CON_QUALIFIER_1 = new IElementType("HS_Q_CON_QUALIFIER_1", null);
        //  IElementType HS_Q_CON_QUALIFIER_2 = new IElementType("HS_Q_CON_QUALIFIER_2", null);
        //  IElementType HS_Q_CON_QUALIFIER_3 = new IElementType("HS_Q_CON_QUALIFIER_3", null);
        //  IElementType HS_Q_CON_QUALIFIER_4 = new IElementType("HS_Q_CON_QUALIFIER_4", null);
        //  IElementType HS_Q_NAME = new IElementType("HS_Q_NAME", null);
        //  IElementType HS_Q_NAMES = new IElementType("HS_Q_NAMES", null);
        //  IElementType HS_Q_VAR_CON = new IElementType("HS_Q_VAR_CON", null);
        //  IElementType HS_SCONTEXT = new IElementType("HS_SCONTEXT", null);
        //  IElementType HS_SHEBANG_LINE = new IElementType("HS_SHEBANG_LINE", null);
        //  IElementType HS_SIMPLECLASS = new IElementType("HS_SIMPLECLASS", null);
        //  IElementType HS_SIMPLETYPE = new IElementType("HS_SIMPLETYPE", null);
        //  IElementType HS_TEXT_LITERAL = new IElementType("HS_TEXT_LITERAL", null);
        //  IElementType HS_TOP_DECLARATION = new IElementType("HS_TOP_DECLARATION", null);
        //  IElementType HS_TTYPE = new IElementType("HS_TTYPE", null);
        //  IElementType HS_TYPE_DECLARATION = new IElementType("HS_TYPE_DECLARATION", null);
        //  IElementType HS_TYPE_EQUALITY = new IElementType("HS_TYPE_EQUALITY", null);
        //  IElementType HS_TYPE_FAMILY_DECLARATION = new IElementType("HS_TYPE_FAMILY_DECLARATION", null);
        //  IElementType HS_TYPE_FAMILY_TYPE = new IElementType("HS_TYPE_FAMILY_TYPE", null);
        //  IElementType HS_TYPE_INSTANCE_DECLARATION = new IElementType("HS_TYPE_INSTANCE_DECLARATION", null);
        //  IElementType HS_TYPE_SIGNATURE = new IElementType("HS_TYPE_SIGNATURE", null);
        //  IElementType HS_VARID = new IElementType("HS_VARID", null);
        //  IElementType HS_VARSYM = new IElementType("HS_VARSYM", null);
        //  IElementType HS_VAR_CON = new IElementType("HS_VAR_CON", null);
        //
        //  IElementType HS_AT = new IElementType("AT", null);
        //  IElementType HS_BACKQUOTE = new IElementType("BACKQUOTE", null);
        //  IElementType HS_BACKSLASH = new IElementType("BACKSLASH", null);
        //  IElementType HS_CASE = new IElementType("CASE", null);
        //  IElementType HS_CHARACTER_LITERAL = new IElementType("CHARACTER_LITERAL", null);
        //  IElementType HS_CLASS = new IElementType("CLASS", null);
        //  IElementType HS_COLON_COLON = new IElementType("COLON_COLON", null);
        //  IElementType HS_COMMA = new IElementType("COMMA", null);
        //  IElementType HS_COMMENT = new IElementType("COMMENT", null);
        //  IElementType HS_CONSYM_ID = new IElementType("CONSYM_ID", null);
        //  IElementType HS_CON_ID = new IElementType("CON_ID", null);
        //  IElementType HS_DASH = new IElementType("DASH", null);
        //  IElementType HS_DATA = new IElementType("DATA", null);
        //  IElementType HS_DECIMAL = new IElementType("DECIMAL", null);
        //  IElementType HS_DEFAULT = new IElementType("DEFAULT", null);
        //  IElementType HS_DERIVING = new IElementType("DERIVING", null);
        //  IElementType HS_DIRECTIVE = new IElementType("DIRECTIVE", null);
        //  IElementType HS_DO = new IElementType("DO", null);
        //  IElementType HS_DOT = new IElementType("DOT", null);
        //  IElementType HS_DOUBLE_QUOTES = new IElementType("DOUBLE_QUOTES", null);
        //  IElementType HS_DOUBLE_RIGHT_ARROW = new IElementType("DOUBLE_RIGHT_ARROW", null);
        //  IElementType HS_ELSE = new IElementType("ELSE", null);
        //  IElementType HS_EQUAL = new IElementType("EQUAL", null);
        //  IElementType HS_FLOAT = new IElementType("FLOAT", null);
        //  IElementType HS_FORALL = new IElementType("FORALL", null);
        //  IElementType HS_FOREIGN_EXPORT = new IElementType("FOREIGN_EXPORT", null);
        //  IElementType HS_FOREIGN_IMPORT = new IElementType("FOREIGN_IMPORT", null);
        //  IElementType HS_HADDOCK = new IElementType("HADDOCK", null);
        //  IElementType HS_HASH = new IElementType("HASH", null);
        //  IElementType HS_HEXADECIMAL = new IElementType("HEXADECIMAL", null);
        //  IElementType HS_IF = new IElementType("IF", null);
        //  IElementType HS_IMPORT = new IElementType("IMPORT", null);
        //  IElementType HS_IN = new IElementType("IN", null);
        //  IElementType HS_INCLUDE_DIRECTIVE = new IElementType("INCLUDE_DIRECTIVE", null);
        //  IElementType HS_INFIX = new IElementType("INFIX", null);
        //  IElementType HS_INFIXL = new IElementType("INFIXL", null);
        //  IElementType HS_INFIXR = new IElementType("INFIXR", null);
        //  IElementType HS_INSTANCE = new IElementType("INSTANCE", null);
        //  IElementType HS_LEFT_ARROW = new IElementType("LEFT_ARROW", null);
        //  IElementType HS_LEFT_BRACE = new IElementType("LEFT_BRACE", null);
        //  IElementType HS_LEFT_BRACKET = new IElementType("LEFT_BRACKET", null);
        //  IElementType HS_LEFT_PAREN = new IElementType("LEFT_PAREN", null);
        //  IElementType HS_LET = new IElementType("LET", null);
        //  IElementType HS_LIST_COMPREHENSION = new IElementType("LIST_COMPREHENSION", null);
        //  IElementType HS_MODULE = new IElementType("MODULE", null);
        //  IElementType HS_NCOMMENT = new IElementType("NCOMMENT", null);
        //  IElementType HS_NEWLINE = new IElementType("NEWLINE", null);
        //  IElementType HS_NEWTYPE = new IElementType("NEWTYPE", null);
        //  IElementType HS_NHADDOCK = new IElementType("NHADDOCK", null);
        //  IElementType HS_NOT_TERMINATED_COMMENT = new IElementType("NOT_TERMINATED_COMMENT", null);
        //  IElementType HS_NOT_TERMINATED_QQ_EXPRESSION = new IElementType("NOT_TERMINATED_QQ_EXPRESSION", null);
        //  IElementType HS_OCTAL = new IElementType("OCTAL", null);
        //  IElementType HS_OF = new IElementType("OF", null);
        //  IElementType HS_ONE_PRAGMA = new IElementType("ONE_PRAGMA", null);
        //  IElementType HS_PRAGMA_END = new IElementType("PRAGMA_END", null);
        //  IElementType HS_PRAGMA_SEP = new IElementType("PRAGMA_SEP", null);
        //  IElementType HS_PRAGMA_START = new IElementType("PRAGMA_START", null);
        //  IElementType HS_QUASIQUOTE = new IElementType("QUASIQUOTE", null);
        //  IElementType HS_QUOTE = new IElementType("QUOTE", null);
        //  IElementType HS_RIGHT_ARROW = new IElementType("RIGHT_ARROW", null);
        //  IElementType HS_RIGHT_BRACE = new IElementType("RIGHT_BRACE", null);
        //  IElementType HS_RIGHT_BRACKET = new IElementType("RIGHT_BRACKET", null);
        //  IElementType HS_RIGHT_PAREN = new IElementType("RIGHT_PAREN", null);
        //  IElementType HS_SEMICOLON = new IElementType("SEMICOLON", null);
        //  IElementType HS_STRING_LITERAL = new IElementType("STRING_LITERAL", null);
        //  IElementType HS_THEN = new IElementType("THEN", null);
        //  IElementType HS_TILDE = new IElementType("TILDE", null);
        //  IElementType HS_TYPE = new IElementType("TYPE", null);
        //  IElementType HS_TYPE_FAMILY = new IElementType("TYPE_FAMILY", null);
        //  IElementType HS_TYPE_INSTANCE = new IElementType("TYPE_INSTANCE", null);
        //  IElementType HS_UNDERSCORE = new IElementType("UNDERSCORE", null);
        //  IElementType HS_VARSYM_ID = new IElementType("VARSYM_ID", null);
        //  IElementType HS_VAR_ID = new IElementType("VAR_ID", null);
        //  IElementType HS_VERTICAL_BAR = new IElementType("VERTICAL_BAR", null);
        //  IElementType HS_WHERE = new IElementType("WHERE", null);
        return when(tokenType) {
            TokenType.BAD_CHARACTER -> arrayOf(Illegal)
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
            HaskellTypes.HS_IMPORT_QUALIFIED -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_QUALIFIED_AS -> arrayOf(Keyword)
            HaskellTypes.HS_CCONTEXT -> arrayOf(Keyword)
            HaskellTypes.HS_CDECL -> arrayOf(Keyword)
            HaskellTypes.HS_CDECLS -> arrayOf(Keyword)
            HaskellTypes.HS_CDECL_DATA_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_CIDECL -> arrayOf(Keyword)
            HaskellTypes.HS_CIDECLS -> arrayOf(Keyword)
            HaskellTypes.HS_CLASS_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_CLAZZ -> arrayOf(Keyword)
            HaskellTypes.HS_COMMENTS -> arrayOf(Comment)
            HaskellTypes.HS_CONSTR -> arrayOf(Constructor)
            HaskellTypes.HS_CONSTR_1 -> arrayOf(Constructor)
            HaskellTypes.HS_CONSTR_2 -> arrayOf(Constructor)
            HaskellTypes.HS_CONSTR_3 -> arrayOf(Constructor)
            HaskellTypes.HS_CONSYM -> arrayOf(Operator)
            HaskellTypes.HS_DATA_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_DATA_DECLARATION_DERIVING -> arrayOf(Keyword)
            HaskellTypes.HS_DEFAULT_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_DERIVING_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_DERIVING_VIA -> arrayOf(Keyword)
            HaskellTypes.HS_DOT_DOT -> arrayOf(Operator)
            HaskellTypes.HS_EXPORTS -> arrayOf(Keyword)
            HaskellTypes.HS_EXPRESSION -> arrayOf(Default)
            HaskellTypes.HS_FIELDDECL -> arrayOf(Default)
            HaskellTypes.HS_FILE_HEADER -> arrayOf(Default)
            HaskellTypes.HS_FIXITY_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_FOREIGN_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_GENERAL_PRAGMA_CONTENT -> arrayOf(PragmaContent)
            HaskellTypes.HS_GTYCON -> arrayOf(Constructor)
            HaskellTypes.HS_IMPORT_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_DECLARATIONS -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_EMPTY_SPEC -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_HIDING -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_HIDING_SPEC -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_ID -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_IDS_SPEC -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT_PACKAGE_NAME -> arrayOf(Keyword)
            HaskellTypes.HS_INST -> arrayOf(Keyword)
            HaskellTypes.HS_INSTANCE_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_INSTVAR -> arrayOf(Keyword)
            HaskellTypes.HS_KIND_SIGNATURE -> arrayOf(Keyword)
            HaskellTypes.HS_LIST_TYPE -> arrayOf(Keyword)
            HaskellTypes.HS_MODID -> arrayOf(Keyword)
            HaskellTypes.HS_MODULE_BODY -> arrayOf(Default)
            HaskellTypes.HS_MODULE_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_NEWCONSTR -> arrayOf(Constructor)
            HaskellTypes.HS_NEWCONSTR_FIELDDECL -> arrayOf(Default)
            HaskellTypes.HS_NEWTYPE_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_QUALIFIER -> arrayOf(Keyword)
            HaskellTypes.HS_QUASI_QUOTE -> arrayOf(Quasiquote)
            HaskellTypes.HS_Q_CON -> arrayOf(Constructor)
            HaskellTypes.HS_Q_CON_QUALIFIER -> arrayOf(Keyword)
            HaskellTypes.HS_Q_CON_QUALIFIER_1 -> arrayOf(Keyword)
            HaskellTypes.HS_Q_CON_QUALIFIER_2 -> arrayOf(Keyword)
            HaskellTypes.HS_Q_CON_QUALIFIER_3 -> arrayOf(Keyword)
            HaskellTypes.HS_Q_CON_QUALIFIER_4 -> arrayOf(Keyword)
            HaskellTypes.HS_Q_VAR_CON -> arrayOf(Constructor)
            HaskellTypes.HS_SCONTEXT -> arrayOf(Keyword)
            HaskellTypes.HS_SHEBANG_LINE -> arrayOf(Default)
            HaskellTypes.HS_SIMPLECLASS -> arrayOf(Keyword)
            HaskellTypes.HS_SIMPLETYPE -> arrayOf(Keyword)
            HaskellTypes.HS_TEXT_LITERAL -> arrayOf(Default)
            HaskellTypes.HS_TOP_DECLARATION -> arrayOf(Default)
            HaskellTypes.HS_TTYPE -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_EQUALITY -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_FAMILY_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_FAMILY_TYPE -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_INSTANCE_DECLARATION -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_SIGNATURE -> arrayOf(Keyword)
            HaskellTypes.HS_VARSYM -> arrayOf(Operator)
            HaskellTypes.HS_VAR_CON -> arrayOf(Constructor)
            HaskellTypes.HS_AT -> arrayOf(Operator)
            HaskellTypes.HS_BACKQUOTE -> arrayOf(Operator)
            HaskellTypes.HS_BACKSLASH -> arrayOf(Operator)
            HaskellTypes.HS_CASE -> arrayOf(Keyword)
            HaskellTypes.HS_CHARACTER_LITERAL -> arrayOf(String)
            HaskellTypes.HS_CLASS -> arrayOf(Keyword)
            HaskellTypes.HS_COLON_COLON -> arrayOf(Operator)
            HaskellTypes.HS_COMMA -> arrayOf(Operator)
            HaskellTypes.HS_COMMENT -> arrayOf(Comment)
            HaskellTypes.HS_CONSYM_ID -> arrayOf(Operator)
            HaskellTypes.HS_CON_ID -> arrayOf(Constructor)
            HaskellTypes.HS_DASH -> arrayOf(Operator)
            HaskellTypes.HS_DATA -> arrayOf(Keyword)
            HaskellTypes.HS_DECIMAL -> arrayOf(Number)
            HaskellTypes.HS_DEFAULT -> arrayOf(Keyword)
            HaskellTypes.HS_DERIVING -> arrayOf(Keyword)
            HaskellTypes.HS_DIRECTIVE -> arrayOf(Default)
            HaskellTypes.HS_DO -> arrayOf(Keyword)
            HaskellTypes.HS_DOT -> arrayOf(Operator)
            HaskellTypes.HS_DOUBLE_QUOTES -> arrayOf(String)
            HaskellTypes.HS_DOUBLE_RIGHT_ARROW -> arrayOf(Operator)
            HaskellTypes.HS_ELSE -> arrayOf(Keyword)
            HaskellTypes.HS_EQUAL -> arrayOf(Operator)
            HaskellTypes.HS_FLOAT -> arrayOf(Number)
            HaskellTypes.HS_FORALL -> arrayOf(Keyword)
            HaskellTypes.HS_FOREIGN_EXPORT -> arrayOf(Keyword)
            HaskellTypes.HS_FOREIGN_IMPORT -> arrayOf(Keyword)
            HaskellTypes.HS_HADDOCK -> arrayOf(DocComment)
            HaskellTypes.HS_HASH -> arrayOf(Operator)
            HaskellTypes.HS_HEXADECIMAL -> arrayOf(Number)
            HaskellTypes.HS_IF -> arrayOf(Keyword)
            HaskellTypes.HS_IMPORT -> arrayOf(Keyword)
            HaskellTypes.HS_IN -> arrayOf(Keyword)
            HaskellTypes.HS_INCLUDE_DIRECTIVE -> arrayOf(Default)
            HaskellTypes.HS_INFIX -> arrayOf(Keyword)
            HaskellTypes.HS_INSTANCE -> arrayOf(Keyword)
            HaskellTypes.HS_LEFT_ARROW -> arrayOf(Operator)
            HaskellTypes.HS_LEFT_BRACE -> arrayOf(Brace)
            HaskellTypes.HS_LEFT_BRACKET -> arrayOf(Bracket)
            HaskellTypes.HS_LEFT_PAREN -> arrayOf(Parentheses)
            HaskellTypes.HS_LET -> arrayOf(Keyword)
            HaskellTypes.HS_LIST_COMPREHENSION -> arrayOf(Default)
            HaskellTypes.HS_MODULE -> arrayOf(Keyword)
            HaskellTypes.HS_NCOMMENT -> arrayOf(BlockComment)
            HaskellTypes.HS_NEWLINE -> arrayOf(Default)
            HaskellTypes.HS_NEWTYPE -> arrayOf(Keyword)
            HaskellTypes.HS_NHADDOCK -> arrayOf(BlockDocComment)
            HaskellTypes.HS_NOT_TERMINATED_COMMENT -> arrayOf(Illegal)
            HaskellTypes.HS_NOT_TERMINATED_QQ_EXPRESSION -> arrayOf(Illegal)
            HaskellTypes.HS_OCTAL -> arrayOf(Number)
            HaskellTypes.HS_OF -> arrayOf(Keyword)
            HaskellTypes.HS_ONE_PRAGMA -> arrayOf(Pragma)
            HaskellTypes.HS_PRAGMA_END -> arrayOf(Pragma)
            HaskellTypes.HS_PRAGMA_SEP -> arrayOf(Pragma)
            HaskellTypes.HS_PRAGMA_START -> arrayOf(Pragma)
            HaskellTypes.HS_QUASIQUOTE -> arrayOf(Quasiquote)
            HaskellTypes.HS_QUOTE -> arrayOf(Operator)
            HaskellTypes.HS_RIGHT_ARROW -> arrayOf(Operator)
            HaskellTypes.HS_RIGHT_BRACE -> arrayOf(Brace)
            HaskellTypes.HS_RIGHT_BRACKET -> arrayOf(Bracket)
            HaskellTypes.HS_RIGHT_PAREN -> arrayOf(Parentheses)
            HaskellTypes.HS_SEMICOLON -> arrayOf(Default)
            HaskellTypes.HS_STRING_LITERAL -> arrayOf(String)
            HaskellTypes.HS_THEN -> arrayOf(Keyword)
            HaskellTypes.HS_TILDE -> arrayOf(Operator)
            HaskellTypes.HS_TYPE -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_FAMILY -> arrayOf(Keyword)
            HaskellTypes.HS_TYPE_INSTANCE -> arrayOf(Keyword)
            HaskellTypes.HS_UNDERSCORE -> arrayOf(Default)
            HaskellTypes.HS_VARSYM_ID -> arrayOf(Operator)
            HaskellTypes.HS_VAR_ID -> arrayOf(Variable)
            HaskellTypes.HS_VERTICAL_BAR -> arrayOf(Operator)
            HaskellTypes.HS_WHERE -> arrayOf(Keyword)
            HaskellTypes.HS_CONSYM -> arrayOf(Operator)
            HaskellTypes.HS_VARID -> arrayOf(Variable)
            HaskellTypes.HS_CONID -> arrayOf(Constructor)
            HaskellTypes.HS_VARSYM -> arrayOf(Operator)
            HaskellTypes.HS_VAR_CON -> arrayOf(Constructor)
            else -> arrayOf(Default)
        }
    }
}