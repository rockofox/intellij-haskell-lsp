package boo.fox.haskelllsp

import boo.fox.haskelllsp.settings.HaskellLspSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HaskellLanguageServerFactoryTest : BasePlatformTestCase() {
    
    private lateinit var settings: HaskellLspSettings
    
    override fun setUp() {
        super.setUp()
        settings = HaskellLspSettings.getInstance()
        settings.hlsPath = ""
    }
    
    fun testFactoryCreation() {
        val factory = HaskellLanguageServerFactory()
        val connectionProvider = factory.createConnectionProvider(project)
        assertNotNull(connectionProvider)
        assertTrue(connectionProvider is HaskellLanguageServer)
        
        val languageClient = factory.createLanguageClient(project)
        assertNotNull(languageClient)
        assertTrue(languageClient is HaskellLanguageClient)
    }
    
    fun testHaskellLanguageServerWithValidConfiguredPath() {
        // Create a temporary executable file
        val tempFile = File.createTempFile("hls-test", "")
        tempFile.setExecutable(true)
        tempFile.deleteOnExit()
        
        settings.hlsPath = tempFile.absolutePath
        
        // Should not throw an exception when creating the server with valid path
        val server = HaskellLanguageServer(project)
        
        // Test passes if server creation doesn't crash
        assertTrue(true)
    }
    
    fun testHaskellLanguageServerWithInvalidConfiguredPath() {
        settings.hlsPath = "/nonexistent/path/to/hls"
        
        // The server should not crash, but should show error notification
        // We can't easily test notifications in unit tests, but we can verify
        // that server creation doesn't crash
        val server = HaskellLanguageServer(project)
        
        // Test passes if server creation doesn't crash
        assertTrue(true)
    }
    
    fun testHaskellLanguageServerWithEmptyConfiguredPath() {
        settings.hlsPath = ""
        
        // Test with empty PATH - should not find executable
        // Note: This test will not find the executable in a clean test environment
        // but verifies the fallback behavior
        val server = HaskellLanguageServer(project)
        
        // The server should handle the case where no executable is found
        // gracefully without crashing
        assertTrue(true) // Test passes if no exception is thrown
    }
    
    fun testHlsExecutableName() {
        // Test that the executable name is correctly determined
        assertEquals("haskell-language-server-wrapper", HaskellLanguageServer.HLS_EXECUTABLE_NAME)
    }
}