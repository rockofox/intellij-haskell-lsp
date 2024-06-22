package boo.fox.haskelllsp.language

import boo.fox.haskelllsp.language.parser.HaskellParser
import boo.fox.haskelllsp.language.psi.HaskellTypes
import boo.fox.haskelllsp.language.psi.HaskellTypes.*
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class HaskellParserDefinition: ParserDefinition{
    private val FILE: IFileElementType = IFileElementType(HaskellLanguage.INSTANCE)
    override fun createLexer(project: Project?): Lexer {
        return HaskellLexerAdapter()
    }

    override fun createParser(project: Project?): PsiParser {
        return HaskellParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(HS_COMMENT, HS_NCOMMENT, HS_NOT_TERMINATED_COMMENT, HS_HADDOCK, HS_NHADDOCK)
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(HS_STRING_LITERAL)
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return HaskellTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return HaskellFile(viewProvider)
    }

}