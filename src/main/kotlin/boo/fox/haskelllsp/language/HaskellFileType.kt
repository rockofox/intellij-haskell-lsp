package boo.fox.haskelllsp.language

import boo.fox.haskelllsp.Icons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class HaskellFileType : LanguageFileType(HaskellLanguage.INSTANCE) {
    companion object {
        val INSTANCE = HaskellFileType()
    }

    override fun getName(): String = "Haskell"

    override fun getDescription(): String = "Haskell language file"

    override fun getDefaultExtension(): String = "hs"

    override fun getIcon(): Icon = Icons.Haskell
}