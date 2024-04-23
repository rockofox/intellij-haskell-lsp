package boo.fox.haskelllsp.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class HaskellFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, HaskellLanguage.INSTANCE) {

    override fun getFileType() = HaskellFileType.INSTANCE

    override fun toString() = "Haskell File"
}