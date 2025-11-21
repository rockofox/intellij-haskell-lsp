package boo.fox.haskelllsp.language

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class HaskellSyntaxHighlighterFactory: SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        val isLiterateHaskell = virtualFile?.extension?.equals("lhs", ignoreCase = true) == true
        return if (isLiterateHaskell) {
            LiterateHaskellSyntaxHighlighter()
        } else {
            HaskellSyntaxHighlighter()
        }
    }
}
