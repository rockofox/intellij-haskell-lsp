package boo.fox.haskelllsp.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class HaskellLspSettingsTest : BasePlatformTestCase() {
    
    fun testSettingsDefaults() {
        val settings = HaskellLspSettings()
        assertEquals("", settings.hlsPath)
    }
    
    fun testSettingsPersistence() {
        val originalSettings = HaskellLspSettings()
        originalSettings.hlsPath = "/usr/bin/haskell-language-server-wrapper"
        assertEquals("/usr/bin/haskell-language-server-wrapper", originalSettings.hlsPath)
        
        // Test loading state (simulate what happens when settings are loaded)
        val newSettings = HaskellLspSettings()
        newSettings.loadState(originalSettings)
        assertEquals(originalSettings.hlsPath, newSettings.hlsPath)
    }
    
    fun testSettingsStateCopy() {
        val originalSettings = HaskellLspSettings()
        originalSettings.hlsPath = "/custom/path/hls"
        
        val copiedSettings = HaskellLspSettings()
        copiedSettings.loadState(originalSettings)
        
        assertEquals(originalSettings.hlsPath, copiedSettings.hlsPath)
        assertTrue(originalSettings !== copiedSettings)
    }
    
    fun testGetInstance() {
        val instance = HaskellLspSettings.getInstance()
        assertNotNull(instance)
        assertTrue(instance is HaskellLspSettings)
    }
}