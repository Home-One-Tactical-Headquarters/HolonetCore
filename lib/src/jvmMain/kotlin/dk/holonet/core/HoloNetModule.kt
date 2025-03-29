package dk.holonet.core

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import org.pf4j.ExtensionPoint

abstract class HoloNetModule() : ExtensionPoint, KoinComponent {
    private lateinit var _position: Position
    private var _priority: Int = 0

    var position: Position
        get() = _position
        private set(value) {
            _position = value
        }

    var priority: Int
        get() = _priority
        private set(value) {
            _priority = value
        }

    @Composable
    abstract fun render()

    open fun configure(configuration: ModuleConfiguration?) {
        if (configuration == null) return // No configuration. Find a way to handle this

        // Basic configuration
        position = configuration.position
        priority = configuration.priority
    }
}