package boo.fox.haskelllsp.language

import com.intellij.psi.tree.IElementType

class HaskellCompositeElementType(debugName: String): IElementType(debugName, HaskellLanguage.INSTANCE) {
    override fun toString(): String {
        return "HaskellCompositeElementType." + super.toString()
    }
}