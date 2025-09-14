package dk.holonet.core.services

import dk.holonet.core.HolonetConfiguration
import dk.holonet.core.HolonetSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class ConfigurationService {

    private val json = Json { ignoreUnknownKeys = true }

    private val _cachedConfig: MutableStateFlow<HolonetConfiguration?> = MutableStateFlow(null)
    val cachedConfig: StateFlow<HolonetConfiguration?> = _cachedConfig.asStateFlow()

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

    suspend fun fetchConfigurationSchema(): List<HolonetSchema> {
        return withContext(Dispatchers.IO) {
            val schemas = mutableListOf<HolonetSchema>()

            // Load all schema files from plugins folder if it exists
            val pluginDir = File(getPluginsFolder())

            if (pluginDir.exists() && pluginDir.isDirectory) {
                // Load all subdirectories
                pluginDir.listFiles { file -> file.isDirectory }?.forEach { dir ->
                    val classesDir = File(dir, "classes")
                    if (classesDir.exists() && classesDir.isDirectory) {
                        classesDir.listFiles { file -> file.extension == "json" }?.forEach { file ->
                            val jsonString = file.readText()
                            try {
                                val schema = json.decodeFromString<HolonetSchema>(jsonString)
                                schemas.add(schema)
                            } catch (e: Exception) {
                                println("Failed to parse schema file ${file.name}: ${e.message}")
                            }
                        }
                    }
                }
            }

            schemas
        }
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