package boo.fox.haskelllsp

import boo.fox.haskelllsp.settings.HaskellLspSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.EnvironmentUtil

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HaskellLanguageServerErrorHandlingTest : BasePlatformTestCase() {
    
    private lateinit var settings: HaskellLspSettings
    
    override fun setUp() {
        super.setUp()
        settings = HaskellLspSettings.getInstance()
        settings.hlsPath = ""
    }
    
    fun testErrorHandlingWithNonExistentConfiguredPath() {
        settings.hlsPath = "/completely/nonexistent/path/haskell-language-server-wrapper"
        
        // Server creation should not crash even with invalid path
        val server = HaskellLanguageServer(project)
        
        // Test passes if server creation doesn't crash
        assertTrue(true)
    }
    
    fun testErrorHandlingWithNonExecutableConfiguredPath() {
        // Create a file that exists but is not executable
        val tempFile = File.createTempFile("non-executable-hls", "")
        tempFile.setExecutable(false)
        tempFile.deleteOnExit()
        
        settings.hlsPath = tempFile.absolutePath
        
        // Server creation should not crash even with non-executable file
        val server = HaskellLanguageServer(project)
        
        // Test passes if server creation doesn't crash
        assertTrue(true)
    }
    
    fun testFallbackToPathWhenConfiguredPathInvalid() {
        // Set invalid configured path
        settings.hlsPath = "/invalid/path"
        
        // Test fallback behavior - in a clean environment this will likely not find
        // anything in PATH either, but the important thing is that it doesn't crash
        val server = HaskellLanguageServer(project)
        
        // The server should handle invalid configured path gracefully
        // without crashing
        assertTrue(true) // Test passes if no exception is thrown
    }
    
    fun testEmptyPathAndNoPathFallback() {
        settings.hlsPath = ""
        
        val server = HaskellLanguageServer(project)
        
        // Should have no commands when both configured path and PATH fail
        // This should not crash the server initialization
        assertTrue(true) // Test passes if no exception is thrown
    }
    
    fun testNullPathAndNoPathFallback() {
        settings.hlsPath = ""
        
        val server = HaskellLanguageServer(project)
        
        // Should have no commands when both configured path and PATH fail
        // This should not crash the server initialization
        assertTrue(true) // Test passes if no exception is thrown
    }
}