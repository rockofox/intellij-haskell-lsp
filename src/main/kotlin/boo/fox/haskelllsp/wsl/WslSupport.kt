package boo.fox.haskelllsp.wsl

import com.intellij.execution.wsl.WSLDistribution
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.openapi.util.SystemInfo

object WslSupport {
    // Matches both backslash (\\wsl.localhost\Ubuntu\...) and forward-slash
    // (//wsl.localhost/Ubuntu/...) variants — IntelliJ's project.basePath may use either.
    private val WSL_UNC_PREFIX = Regex(
        """^(?:\\\\|//)(?:wsl\$|wsl\.localhost)[/\\]([^/\\]+)(.*)$""",
        RegexOption.IGNORE_CASE
    )

    data class WslPathInfo(
        val distroName: String,
        val linuxPath: String,
        val distribution: WSLDistribution
    )

    /**
     * Detects if the given path is a WSL UNC path (e.g. \\wsl.localhost\Ubuntu\home\...
     * or //wsl.localhost/Ubuntu/home/...) and returns parsed information including the
     * resolved WSL distribution.
     * Returns null if the path is not a WSL path, WSL is not available, or the distribution is not installed.
     */
    fun detectWsl(windowsPath: String?): WslPathInfo? {
        if (!SystemInfo.isWindows || windowsPath == null) return null

        val match = WSL_UNC_PREFIX.matchEntire(windowsPath) ?: return null
        val distroName = match.groupValues[1]
        val remainder = match.groupValues[2]
        val linuxPath = if (remainder.isEmpty()) "/" else remainder.replace('\\', '/')

        val distribution = try {
            WslDistributionManager.getInstance().installedDistributions.find {
                it.msId.equals(distroName, ignoreCase = true)
            }
        } catch (_: Exception) {
            null
        } ?: return null

        return WslPathInfo(distroName, linuxPath, distribution)
    }
}
