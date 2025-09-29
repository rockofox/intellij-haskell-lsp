package boo.fox.haskelllsp.language

import boo.fox.haskelllsp.Icons
import kotlin.test.Test
import kotlin.test.assertEquals

class HaskellFileTypeTest {
    private val fileType = HaskellFileType.INSTANCE

    @Test
    fun `it should have the correct name`() {
        assertEquals("Haskell", fileType.name)
    }

    @Test
    fun `it should have the correct description`() {
        assertEquals("Haskell language file", fileType.description)
    }

    @Test
    fun `it should have the correct default extension`() {
        assertEquals("hs", fileType.defaultExtension)
    }

    @Test
    fun `it should have the correct icon`() {
        assertEquals(Icons.Haskell, fileType.icon)
    }
}
