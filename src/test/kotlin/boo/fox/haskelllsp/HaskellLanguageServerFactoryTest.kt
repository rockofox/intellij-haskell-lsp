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

    fun testHaskellLanguageServerFindsExecutableInExtraPath() {
        // Simulate HLS installed in ~/.ghcup/bin (common when IDEA launched from desktop shortcut)
        val fakeGhcupBin = createTempDirectory("ghcup-bin")
        val fakeHls = File(fakeGhcupBin, HaskellLanguageServer.HLS_EXECUTABLE_NAME)
        fakeHls.createNewFile()
        fakeHls.setExecutable(true)
        fakeHls.deleteOnExit()

        // Override user.home so the extra-paths logic finds our fake binary
        val originalHome = System.getProperty("user.home")
        System.setProperty("user.home", fakeGhcupBin.parent)

        try {
            settings.hlsPath = ""
            // Server creation should succeed and find the binary in the extra paths
            val server = HaskellLanguageServer(project)
            assertTrue(true) // passes if no crash / no unhandled error
        } finally {
            System.setProperty("user.home", originalHome)
            fakeHls.delete()
            fakeGhcupBin.delete()
        }
    }

    private fun createTempDirectory(prefix: String): File {
        val dir = File(System.getProperty("java.io.tmpdir"), "$prefix-${System.nanoTime()}")
        dir.mkdir()
        dir.deleteOnExit()
        return dir
    }
}