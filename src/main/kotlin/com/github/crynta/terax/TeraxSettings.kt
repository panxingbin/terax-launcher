package com.github.crynta.terax

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextField

class TeraxSettings(private val project: Project) : Configurable {

    private var mainPanel: JPanel? = null
    private var teraxPathField: TextFieldWithBrowseButton? = null
    private var netcattyPathField: TextFieldWithBrowseButton? = null
    private var customPathField: TextFieldWithBrowseButton? = null
    private var customArgsField: JTextField? = null
    private var teraxRadio: JRadioButton? = null
    private var netcattyRadio: JRadioButton? = null
    private var cmdRadio: JRadioButton? = null
    private var customRadio: JRadioButton? = null
    private var statusLabel: JLabel? = null

    override fun getDisplayName(): String = "Terax Launcher"

    override fun createComponent(): JComponent {
        val headerPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyBottom(10)
            add(JLabel("<html><b>Terax Launcher</b> &mdash; Launch Terax, Netcatty, CMD, or a custom app from the IDE toolbar.</html>"), BorderLayout.NORTH)
        }

        val teraxRadio = JRadioButton("Terax")
        val netcattyRadio = JRadioButton("Netcatty")
        val cmdRadio = JRadioButton("CMD")
        val customRadio = JRadioButton("Custom")
        val group = ButtonGroup()
        group.add(teraxRadio)
        group.add(netcattyRadio)
        group.add(cmdRadio)
        group.add(customRadio)
        this.teraxRadio = teraxRadio
        this.netcattyRadio = netcattyRadio
        this.cmdRadio = cmdRadio
        this.customRadio = customRadio

        val teraxPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                com.intellij.openapi.ui.TextBrowseFolderListener(
                    com.intellij.openapi.fileChooser.FileChooserDescriptor(true, false, false, false, false, false)
                        .withTitle("Select Terax Executable")
                        .withDescription("Choose the Terax executable file")
                )
            )
        }
        this.teraxPathField = teraxPathField

        val netcattyPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                com.intellij.openapi.ui.TextBrowseFolderListener(
                    com.intellij.openapi.fileChooser.FileChooserDescriptor(true, false, false, false, false, false)
                        .withTitle("Select Netcatty Executable")
                        .withDescription("Choose the Netcatty executable file")
                )
            )
        }
        this.netcattyPathField = netcattyPathField

        val customPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                com.intellij.openapi.ui.TextBrowseFolderListener(
                    com.intellij.openapi.fileChooser.FileChooserDescriptor(true, false, false, false, false, false)
                        .withTitle("Select Executable")
                        .withDescription("Choose any executable file")
                )
            )
        }
        this.customPathField = customPathField

        val customArgsField = JTextField("{path}")
        this.customArgsField = customArgsField

        val statusLabel = JLabel(" ")
        statusLabel.foreground = Color.GRAY
        this.statusLabel = statusLabel

        val testButton = JButton("Test Launch")
        testButton.addActionListener {
            val path = when {
                teraxRadio.isSelected -> teraxPathField.text
                netcattyRadio.isSelected -> netcattyPathField.text
                customRadio.isSelected -> customPathField.text
                else -> ""
            }
            if (path.isNotBlank() && !File(path).exists()) {
                statusLabel.text = "File not found: $path"
                statusLabel.foreground = Color.RED
            } else {
                statusLabel.text = "Configuration saved."
                statusLabel.foreground = Color(0, 128, 0)
            }
        }

        val formPanel = JPanel(GridBagLayout()).apply {
            val gbc = GridBagConstraints()
            gbc.insets = Insets(5, 5, 5, 5)
            gbc.anchor = GridBagConstraints.WEST

            gbc.gridx = 0; gbc.gridy = 0
            add(teraxRadio, gbc)
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
            add(teraxPathField, gbc)

            gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
            add(netcattyRadio, gbc)
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
            add(netcattyPathField, gbc)

            gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
            add(cmdRadio, gbc)
            gbc.gridx = 1
            add(JLabel("<html><span color='gray' size='-1'>Opens a command prompt in the project directory.</span></html>"), gbc)

            gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
            add(customRadio, gbc)
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
            add(customPathField, gbc)

            gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
            add(JLabel("<html><span color='gray' size='-1'>Arguments: use {path} as placeholder for project directory.</span></html>"), gbc)

            gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
            add(customArgsField, gbc)

            gbc.gridx = 1; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
            add(testButton, gbc)

            gbc.gridx = 1; gbc.gridy = 7
            add(statusLabel, gbc)
        }

        val mainPanel = JPanel(BorderLayout()).apply {
            add(headerPanel, BorderLayout.NORTH)
            add(formPanel, BorderLayout.CENTER)
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }
        this.mainPanel = mainPanel

        reset()
        return mainPanel
    }

    override fun isModified(): Boolean {
        val state = TeraxSettingsState.getInstance()
        return teraxRadio?.isSelected != (state.launcherType == LauncherType.TERAX) ||
            netcattyRadio?.isSelected != (state.launcherType == LauncherType.NETCATTY) ||
            cmdRadio?.isSelected != (state.launcherType == LauncherType.CMD) ||
            customRadio?.isSelected != (state.launcherType == LauncherType.CUSTOM) ||
            teraxPathField?.text != (state.teraxPath ?: "") ||
            netcattyPathField?.text != (state.netcattyPath ?: "") ||
            customPathField?.text != (state.cmdPath ?: "") ||
            customArgsField?.text != (state.cmdArgs ?: "{path}")
    }

    override fun apply() {
        val state = TeraxSettingsState.getInstance()
        state.launcherType = when {
            netcattyRadio?.isSelected == true -> LauncherType.NETCATTY
            cmdRadio?.isSelected == true -> LauncherType.CMD
            customRadio?.isSelected == true -> LauncherType.CUSTOM
            else -> LauncherType.TERAX
        }
        state.teraxPath = teraxPathField?.text?.takeIf { it.isNotBlank() }
        state.netcattyPath = netcattyPathField?.text?.takeIf { it.isNotBlank() }
        state.cmdPath = customPathField?.text?.takeIf { it.isNotBlank() }
        state.cmdArgs = customArgsField?.text?.takeIf { it.isNotBlank() } ?: "{path}"
    }

    override fun reset() {
        val state = TeraxSettingsState.getInstance()
        teraxRadio?.isSelected = state.launcherType == LauncherType.TERAX
        netcattyRadio?.isSelected = state.launcherType == LauncherType.NETCATTY
        cmdRadio?.isSelected = state.launcherType == LauncherType.CMD
        customRadio?.isSelected = state.launcherType == LauncherType.CUSTOM
        teraxPathField?.text = state.teraxPath ?: ""
        netcattyPathField?.text = state.netcattyPath ?: ""
        customPathField?.text = state.cmdPath ?: ""
        customArgsField?.text = state.cmdArgs ?: "{path}"
    }

    override fun disposeUIResources() {
        mainPanel = null
        teraxPathField = null
        netcattyPathField = null
        customPathField = null
        customArgsField = null
        teraxRadio = null
        netcattyRadio = null
        cmdRadio = null
        customRadio = null
        statusLabel = null
    }
}
