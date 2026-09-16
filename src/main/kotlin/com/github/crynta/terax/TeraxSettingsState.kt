package com.github.crynta.terax

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

enum class LauncherType { TERAX, NETCATTY, CMD, CUSTOM }

@State(
    name = "TeraxLauncherSettings",
    storages = [Storage("terax-launcher.xml")]
)
class TeraxSettingsState : PersistentStateComponent<TeraxSettingsState> {

    var teraxPath: String? = null
    var netcattyPath: String? = null
    var cmdPath: String? = null
    var cmdArgs: String? = "{path}"
    var launcherType: LauncherType = LauncherType.TERAX

    override fun getState(): TeraxSettingsState = this

    override fun loadState(state: TeraxSettingsState) {
        this.teraxPath = state.teraxPath
        this.netcattyPath = state.netcattyPath
        this.launcherType = state.launcherType
    }

    companion object {
        fun getInstance(): TeraxSettingsState {
            return com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(TeraxSettingsState::class.java)
        }
    }
}
