package boo.fox.haskelllsp.language

import com.intellij.lang.Language

class HaskellLanguage: Language("Haskell"){
    companion object {
        val INSTANCE = HaskellLanguage()
    }
}
