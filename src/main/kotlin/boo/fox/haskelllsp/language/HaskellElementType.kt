package boo.fox.haskelllsp.language

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls


class HaskellElementType(debugName: String) :
    IElementType(debugName, HaskellLanguage.INSTANCE)