package boo.fox.haskelllsp.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HaskellLspSettingsComponentTest : BasePlatformTestCase() {
    
    fun testComponentCreation() {
        val component = HaskellLspSettingsComponent()
        assertTrue(component.panel != null)
        assertEquals("", component.getHlsPath()) // Should return empty string for actual value
    }
    
    fun testHlsPathSetting() {
        val component = HaskellLspSettingsComponent()
        
        // Initially should be empty (default shown in UI but empty value)
        assertEquals("", component.getHlsPath())
        
        // Test setting path
        component.setHlsPath("/usr/bin/haskell-language-server")
        assertEquals("/usr/bin/haskell-language-server", component.getHlsPath())
        
        // Test setting empty path should return to default
        component.setHlsPath("")
        assertEquals("", component.getHlsPath())
        
        // Test setting complex path
        val complexPath = "/opt/custom/bin/haskell-language-server-wrapper"
        component.setHlsPath(complexPath)
        assertEquals(complexPath, component.getHlsPath())
    }
}