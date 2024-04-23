package boo.fox.haskelllsp.language

import com.intellij.psi.tree.IElementType

class HaskellTokenType(debugName: String) :
    IElementType(debugName, HaskellLanguage.INSTANCE) {
    override fun toString(): String {
        return "HaskellTokenType." + super.toString()
    }
}