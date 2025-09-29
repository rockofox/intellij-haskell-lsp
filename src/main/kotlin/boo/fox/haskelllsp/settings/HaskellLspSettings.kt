package boo.fox.haskelllsp.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "boo.fox.haskelllsp.settings.HaskellLspSettings",
    storages = [Storage("HaskellLspSettings.xml")]
)
class HaskellLspSettings : PersistentStateComponent<HaskellLspSettings> {
    var hlsPath: String = ""

    override fun getState(): HaskellLspSettings = this

    override fun loadState(state: HaskellLspSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): HaskellLspSettings = ApplicationManager.getApplication().getService(HaskellLspSettings::class.java)
    }
}