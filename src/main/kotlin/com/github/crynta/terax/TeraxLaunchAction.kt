package com.github.crynta.terax

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.icons.AllIcons
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class TeraxLaunchAction : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        val state = TeraxSettingsState.getInstance()
        val icon = when (state.launcherType) {
            LauncherType.NETCATTY -> IconLoader.findIcon("/icons/netcatty.png", TeraxLaunchAction::class.java)
            LauncherType.CMD -> IconLoader.findIcon("/icons/cmd.png", TeraxLaunchAction::class.java)
            LauncherType.CUSTOM -> IconLoader.findIcon("/icons/custom.png", TeraxLaunchAction::class.java)
            else -> IconLoader.findIcon("/icons/terax.png", TeraxLaunchAction::class.java)
        }
        if (icon != null) {
            e.presentation.icon = icon
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project = e.project ?: return
        val basePath = project.basePath ?: return
        val state = TeraxSettingsState.getInstance()

        when (state.launcherType) {
            LauncherType.CMD -> launchCmd(project, basePath)
            LauncherType.CUSTOM -> launchCustom(project, state, basePath)
            LauncherType.NETCATTY -> launchApp(project, state.netcattyPath, "Netcatty.exe", basePath)
            LauncherType.TERAX -> launchApp(project, state.teraxPath, "terax.exe", basePath)
        }
    }

    private fun launchCmd(project: Project, basePath: String) {
        try {
            val command = listOf("cmd", "/c", "start", "\"\"", "cmd.exe", "/k", "cd", "/d", basePath)
            ProcessBuilder(command).start()
        } catch (e: IOException) {
            Messages.showErrorDialog(project, "Failed to launch CMD: ${e.message}", "Error")
        }
    }

    private fun launchCustom(project: Project, state: TeraxSettingsState, basePath: String) {
        val configuredPath = state.cmdPath?.trim()?.trim('"')?.trim()
            ?.let { Paths.get(it) }
            ?.takeIf { Files.isExecutable(it) }

        var exePath = configuredPath
        if (exePath == null) {
            val manualPath = Messages.showInputDialog(project, "Executable not found.\nPlease enter the full path:", "Not Found", Messages.getQuestionIcon())
            if (manualPath.isNullOrBlank()) return
            val cleaned = manualPath.trim().trim('"').trim()
            state.cmdPath = cleaned
            exePath = Paths.get(cleaned) ?: return
        }

        val argsTemplate = state.cmdArgs ?: "{path}"
        val arg = argsTemplate.replace("{path}", basePath)

        try {
            ProcessBuilder(exePath.toString(), arg).start()
        } catch (e: IOException) {
            Messages.showErrorDialog(project, "Failed to launch: ${e.message}", "Error")
        }
    }

    private fun launchApp(project: Project, savedPath: String?, exeName: String, basePath: String) {
        val configuredPath = savedPath?.trim()?.trim('"')?.trim()
            ?.let { Paths.get(it) }
            ?.takeIf { Files.isExecutable(it) }

        var exePath = configuredPath ?: resolveFromPath(exeName)

        if (exePath == null) {
            val manualPath = Messages.showInputDialog(project, "$exeName not found in PATH.\nPlease enter the full path:", "Not Found", Messages.getQuestionIcon())
            if (manualPath.isNullOrBlank()) return
            val cleaned = manualPath.trim().trim('"').trim()
            if (exeName == "Netcatty.exe") {
                TeraxSettingsState.getInstance().netcattyPath = cleaned
            } else {
                TeraxSettingsState.getInstance().teraxPath = cleaned
            }
            exePath = Paths.get(cleaned) ?: return
        }

        try {
            ProcessBuilder(exePath.toString(), basePath).start()
        } catch (e: IOException) {
            Messages.showErrorDialog(project, "Failed to launch $exeName: ${e.message}", "Error")
        }
    }

    private fun resolveFromPath(exeName: String): java.nio.file.Path? {
        val pathEnv = System.getenv("PATH") ?: return null
        for (dir in pathEnv.split(java.io.File.pathSeparator)) {
            try {
                val candidate = Paths.get(dir, exeName)
                if (Files.isExecutable(candidate)) {
                    return candidate
                }
            } catch (e: java.nio.file.InvalidPathException) {
                // Skip malformed PATH entries.
            }
        }
        return null
    }
}
