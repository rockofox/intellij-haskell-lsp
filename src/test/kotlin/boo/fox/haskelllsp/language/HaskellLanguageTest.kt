package boo.fox.haskelllsp.language

import kotlin.test.Test
import kotlin.test.assertEquals

class HaskellLanguageTest {
    @Test
    fun `it should have the correct name`() {
        assertEquals("Haskell", HaskellLanguage.INSTANCE.id)
    }
}
