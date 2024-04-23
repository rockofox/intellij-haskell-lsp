//package boo.fox.haskelllsp.language
//
//import boo.fox.haskelllsp.Icons
//import boo.fox.haskelllsp.language.psi.HaskellVarid
//import com.intellij.extapi.psi.ASTWrapperPsiElement
//import com.intellij.lang.ASTNode
//import com.intellij.navigation.ItemPresentation
//import com.intellij.psi.*
//import com.intellij.psi.stubs.IStubElementType
//import com.intellij.psi.stubs.NamedStubBase
//import com.intellij.psi.stubs.StubElement
//import com.intellij.usageView.UsageViewUtil
//import com.intellij.util.io.StringRef
//import javax.swing.Icon
//
//
//open class HaskellNamedStub<T : HaskellNamedElement> : NamedStubBase<T> {
//    constructor(
//        parent: StubElement<*>?,
//        elementType: IStubElementType<*, *>,
//        name: String?
//    ) : super(parent, elementType, name)
//
//    constructor(
//        parent: StubElement<*>?,
//        elementType: IStubElementType<*, *>,
//        name: StringRef?
//    ) : super(parent, elementType, name)
//}
//
//
//interface HaskellCompositeElement: PsiElement  {
//}
//interface HaskellNamedElement : HaskellCompositeElement, NavigatablePsiElement, PsiNameIdentifierOwner
//
//class HaskellCompositeElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
//    HaskellCompositeElement {
//    override fun toString(): String {
//        return node.elementType.toString()
//    }
//
//    override fun getPresentation(): ItemPresentation {
//        val text = UsageViewUtil.createNodeText(this)
//        return object : ItemPresentation {
//
//            override fun getPresentableText(): String {
//                return text
//            }
//
//
//            override fun getLocationString(): String {
//                return containingFile.name
//            }
//
//            override fun getIcon(unused: Boolean): Icon {
//                return this@HaskellCompositeElementImpl.getIcon(0)
//            }
//        }
//    }
//
//    override fun getIcon(flags: Int): Icon {
//        return Icons.Haskell
//    }
//}
//
//class HaskellVaridStub : NamedStubBase<HaskellVarid> {
//    constructor(parent: StubElement<*>, elementType: IStubElementType<*, *>, name: StringRef) : super(
//        parent,
//        elementType, name
//    )
//
//    constructor(parent: StubElement<*>, elementType: IStubElementType<*, *>, name: String) : super(
//        parent,
//        elementType, name
//    )
//}