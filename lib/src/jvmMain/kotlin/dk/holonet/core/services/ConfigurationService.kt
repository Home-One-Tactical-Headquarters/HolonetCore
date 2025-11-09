package dk.holonet.core.services

import dk.holonet.core.HolonetConfiguration
import dk.holonet.core.HolonetSchema
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.pf4j.util.FileUtils
import java.io.File
import java.util.jar.JarFile
import kotlin.collections.forEach

class ConfigurationService {

    private val json = Json { ignoreUnknownKeys = true }

    private val _cachedConfig: MutableStateFlow<HolonetConfiguration?> = MutableStateFlow(null)
    val cachedConfig: StateFlow<HolonetConfiguration?> = _cachedConfig.asStateFlow()

    private val _cachedModules = mutableMapOf<String, File>()

    private var pluginService: PluginServiceInterface? = null

    fun setPluginManager(manager: PluginServiceInterface) {
        this.pluginService = manager
    }

    suspend fun fetchConfiguration(): HolonetConfiguration {
        return withContext(Dispatchers.IO) {

            // Load configuration if it exists in user.home
            val file = File(getConfig())
            val configExists = file.exists() && file.isFile

            // If configuration exists, read it. Otherwise, use default configuration
            val jsonString: String = if (configExists) file.readText() else defaultConfigurationJson

            // Parse configuration
            val config = json.decodeFromString<HolonetConfiguration>(jsonString)

            // Cache configuration
            _cachedConfig.emit(config)

            // If configuration does not exist, write configuration
            if (!configExists) writeConfiguration(config)

            // Return configuration
            config
        }
    }

    suspend fun fetchConfigurationSchema(): Map<String, HolonetSchema> {
        return withContext(Dispatchers.IO) {
            val schemas = mutableMapOf<String, HolonetSchema>()

            // Load all schema files from plugins folder if it exists
            val pluginDir = File(getPluginsFolder())

            if (pluginDir.exists() && pluginDir.isDirectory) {
                // Load all subdirectories
                pluginDir.listFiles { file -> FileUtils.isJarFile(file.toPath()) }?.forEach { jarFile ->
                    try {
                        JarFile(jarFile).use { jar ->
                            jar.entries().asSequence()
                                .filter { entry ->
                                    !entry.isDirectory &&
                                            entry.name.endsWith(".json") &&
                                            !entry.name.contains("/")
                                }
                                .forEach { entry ->
                                    jar.getInputStream(entry).use { inputStream ->
                                        val jsonString = inputStream.bufferedReader().readText()
                                        try {
                                            val schema = json.decodeFromString<HolonetSchema>(jsonString)
                                            schemas[schema.pluginId] = schema
                                            _cachedModules[schema.pluginId] = jarFile
                                        } catch (e: Exception) {
                                            println("Failed to parse schema file ${entry.name}: ${e.message}")
                                        }
                                    }
                                }
                        }
                    } catch (e: Exception) {
                        println("Failed to read jar file ${jarFile.name}: ${e.message}")
                    }
                }
            }

            schemas
        }
    }

    suspend fun addModules(modules: List<PlatformFile>, overwrite: Boolean = false): List<String> {
        // Check and save if any modules are already present
        val existingModules = mutableListOf<String>()

        withContext(Dispatchers.IO) {
            val pluginDir = File(getPluginsFolder())
            if (!pluginDir.exists()) {
                pluginDir.mkdirs()
            }

            modules.forEach { module ->
                val destFile = PlatformFile("$pluginDir/${module.name}")
                if (destFile.exists() && !overwrite) {
                    existingModules.add(module.name)
                } else {
                    module.copyTo(destFile)
                }
            }
        }

        return existingModules
    }

    suspend fun deleteModules(pluginIds: List<String>) {
        withContext(Dispatchers.IO) {
            pluginService?.unloadPlugin(pluginIds)
            removeModulesFromConfiguration(pluginIds)
        }
    }

    private suspend fun removeModulesFromConfiguration(pluginIds: List<String>) {
        _cachedConfig.value?.let { config ->
            _cachedConfig.value = config.copy(
                modules = config.modules.filterKeys { it !in pluginIds }
            )
        }

        writeConfiguration(_cachedConfig.value!!)
    }

    private suspend fun writeConfiguration(configuration: HolonetConfiguration) {
        withContext(Dispatchers.IO) {
            val file = File(getConfig())
            file.parentFile.mkdirs()
            file.writeText(json.encodeToString(configuration))
        }
    }

    suspend fun updateConfiguration(newConfig: HolonetConfiguration) {
        writeConfiguration(newConfig)
        fetchConfiguration()
    }
}

private val defaultConfigurationJson: String = """
{
    "modules": {
        "clock": {
            "position": "top_left",
            "priority": 0
        },
        "calendar": {
            "position": "top_left",
            "priority": 1,
            "config": {
                "url": "https://ics.calendarlabs.com/226/06a1cf11/UN_Holidays.ics"
            }
        },
        "rss": {
			"position": "lower_third",
			"config": {
				"feeds": [
					"https://www.dr.dk/nyheder/service/feeds/senestenyt",
					"https://www.version2.dk/rss"
				],
				"frequency": 5000,
				"selectionStrategy": "random"
			}
		}
    }
}
""".trimIndent()