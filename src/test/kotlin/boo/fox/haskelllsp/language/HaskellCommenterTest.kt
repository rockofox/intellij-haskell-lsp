package boo.fox.haskelllsp.language

import kotlin.test.Test
import kotlin.test.assertEquals

class HaskellCommenterTest {
    private val commenter = HaskellCommenter()

    @Test
    fun `it should return the correct line comment prefix`() {
        assertEquals("--", commenter.lineCommentPrefix)
    }

    @Test
    fun `it should return the correct block comment prefix`() {
        assertEquals("{-", commenter.blockCommentPrefix)
    }

    @Test
    fun `it should return the correct block comment suffix`() {
        assertEquals("-}", commenter.blockCommentSuffix)
    }

    @Test
    fun `it should return the correct commented block comment prefix`() {
        assertEquals("{-", commenter.commentedBlockCommentPrefix)
    }

    @Test
    fun `it should return the correct commented block comment suffix`() {
        assertEquals("-}", commenter.commentedBlockCommentSuffix)
    }
}
