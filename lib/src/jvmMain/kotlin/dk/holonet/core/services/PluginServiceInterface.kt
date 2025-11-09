package dk.holonet.core.services

interface PluginServiceInterface {
    suspend fun unloadPlugin(pluginIds: List<String>)
}