package com.github.crynta.terax

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Detects whether Terax is currently running by reading its control descriptor.
 *
 * Terax writes a `control.json` to `%LOCALAPPDATA%\terax\control.json` (Windows) or
 * `~/Library/Caches/terax/control.json` (macOS) when it starts. The descriptor contains
 * the PID of the running process and the TCP address of its control server.
 */
object TeraxProcessDetector {

    private const val CONTROL_DIR = "terax"
    private const val CONTROL_FILE = "control.json"

    /**
     * Returns the path to Terax's control descriptor for the current platform.
     */
    fun controlDescriptorPath(): Path? {
        val baseDir = when (System.getProperty("os.name")?.lowercase()?.contains("windows")) {
            true -> System.getenv("LOCALAPPDATA")
                ?: return null
            else -> System.getProperty("user.home") + "/Library/Caches"
        }
        return Paths.get(baseDir, CONTROL_DIR, CONTROL_FILE)
    }

    /**
     * Reads the PID from Terax's control descriptor, or null if the file does not exist.
     */
    fun readTeraxPid(): Long? {
        val path = controlDescriptorPath() ?: return null
        if (!Files.exists(path)) return null
        return try {
            val json = JsonParser.parseString(Files.readString(path)).asJsonObject
            json.get("pid")?.asLong
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks whether the Terax process recorded in the control descriptor is still alive.
     */
    fun isTeraxRunning(): Boolean {
        val pid = readTeraxPid() ?: return false
        return try {
            ProcessHandle.of(pid).map { it.isAlive }.orElse(false)
        } catch (e: Exception) {
            false
        }
    }
}
