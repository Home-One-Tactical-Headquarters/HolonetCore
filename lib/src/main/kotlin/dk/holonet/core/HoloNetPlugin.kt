package dk.holonet.core

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.pf4j.Plugin
import org.pf4j.PluginWrapper

abstract class HoloNetPlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    /**
     * Load dependencies for the plugin module to use in the application.
     * For example, in the [start] method of the plugin.
     * @param module The Koin module to load dependencies for
     */
    fun loadDependencies(module: Module) {
        loadKoinModules(module)
    }
}