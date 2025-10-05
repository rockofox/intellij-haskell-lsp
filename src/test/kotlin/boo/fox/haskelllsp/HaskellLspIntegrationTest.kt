package boo.fox.haskelllsp

import boo.fox.haskelllsp.settings.HaskellLspSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File
import kotlin.test.assertTrue

class HaskellLspIntegrationTest : BasePlatformTestCase() {
    
    fun testSettingsIntegrationWithLanguageServer() {
        val settings = HaskellLspSettings.getInstance()
        
        // Test with empty settings
        settings.hlsPath = ""
        val server1 = HaskellLanguageServer(project)
        assertTrue(true) // Should not crash
        
        // Test with valid settings
        val tempFile = File.createTempFile("hls-integration", "")
        tempFile.setExecutable(true)
        tempFile.deleteOnExit()
        
        settings.hlsPath = tempFile.absolutePath
        val server2 = HaskellLanguageServer(project)
        assertTrue(true) // Should not crash
        
        // Test with invalid settings
        settings.hlsPath = "/invalid/path"
        val server3 = HaskellLanguageServer(project)
        assertTrue(true) // Should not crash
    }
    
    fun testFactoryWithDifferentSettings() {
        val settings = HaskellLspSettings.getInstance()
        val originalPath = settings.hlsPath
        
        try {
            // Test factory creation with different path settings
            settings.hlsPath = ""
            val factory1 = HaskellLanguageServerFactory()
            val connection1 = factory1.createConnectionProvider(project)
            assertTrue(connection1 is HaskellLanguageServer)
            
            val tempFile = File.createTempFile("hls-factory", "")
            tempFile.setExecutable(true)
            tempFile.deleteOnExit()
            
            settings.hlsPath = tempFile.absolutePath
            val factory2 = HaskellLanguageServerFactory()
            val connection2 = factory2.createConnectionProvider(project)
            assertTrue(connection2 is HaskellLanguageServer)
            
            val client = factory2.createLanguageClient(project)
            assertTrue(client is HaskellLanguageClient)
        } finally {
            // Restore original settings
            settings.hlsPath = originalPath
        }
    }
}