package boo.fox.haskelllsp.language

import com.intellij.lang.Commenter

class HaskellCommenter: Commenter {
    override fun getLineCommentPrefix(): String {
        return "--"
    }

    override fun getBlockCommentPrefix(): String {
        return "{-"
    }

    override fun getBlockCommentSuffix(): String {
        return "-}"
    }

    override fun getCommentedBlockCommentPrefix(): String {
        return "{-"
    }

    override fun getCommentedBlockCommentSuffix(): String {
        return "-}"
    }

}