package boo.fox.haskelllsp.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HaskellLspSettingsConfigurableTest : BasePlatformTestCase() {
    
    fun testConfigurableCreation() {
        val configurable = HaskellLspSettingsConfigurable(project)
        assertEquals("Haskell LSP", configurable.displayName)
    }
    
    fun testConfigurableComponentCreation() {
        val configurable = HaskellLspSettingsConfigurable(project)
        
        // Create component to test initial state
        val component = configurable.createComponent()
        assertTrue(component != null)
    }
    
    fun testConfigurableApplyAndReset() {
        val configurable = HaskellLspSettingsConfigurable(project)
        
        // Create component to ensure UI is initialized
        configurable.createComponent()
        
        // Test basic functionality - these operations should not crash
        configurable.reset()
        configurable.apply()
        
        // Test passes if no exception is thrown
        assertTrue(true)
    }
}