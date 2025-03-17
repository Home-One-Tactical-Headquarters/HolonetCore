package dk.holonet.core

import androidx.compose.runtime.Composable
import org.pf4j.ExtensionPoint

interface HoloNetModule : ExtensionPoint {
    val config: ModuleConfig

    @Composable
    fun render()
}