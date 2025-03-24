package dk.holonet.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class HolonetConfiguration(
    val modules: Map<String, ModuleConfiguration> = emptyMap()
)

@Serializable
data class ModuleConfiguration(
    @Serializable(with = PositionSerializer::class)
    val position: Position,
    val priority: Int = 0,
    val config: JsonObject = JsonObject(emptyMap()) // Dynamic properties
)

// Helper method to get modules to be loaded
fun HolonetConfiguration.getModulesToLoad(): List<String> = modules.keys.toList()

// Helper methods for extracting typed values
fun JsonElement.asString(): String = this.jsonPrimitive.content
fun JsonElement.asInt(): Int = this.jsonPrimitive.content.toInt()
fun JsonElement.asBoolean(): Boolean = this.jsonPrimitive.content.toBoolean()
