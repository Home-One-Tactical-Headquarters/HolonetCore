package dk.holonet.core

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import org.pf4j.ExtensionPoint

abstract class HoloNetModule() : ExtensionPoint, KoinComponent {
    private lateinit var _position: Position

    var position: Position
        get() = _position
        private set(value) {
            _position = value
        }

    @Composable
    abstract fun render()

    open fun configure(configuration: ModuleConfiguration?) {
        if (configuration == null) return // No configuration. Find a way to handle this

        // Basic configuration
        position = configuration.position
    }
}