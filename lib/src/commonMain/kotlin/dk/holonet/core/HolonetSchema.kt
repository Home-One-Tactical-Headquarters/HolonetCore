package dk.holonet.core

import kotlinx.serialization.Serializable

@Serializable
data class HolonetSchema(
    val name: String,
    val description: String? = null,
    val author: String? = null,
    val version: String,
    val config: Map<String, ConfigField>,
    var instance: ModuleConfiguration? = null // Specific instance of the module configuration. Defined later
)

@Serializable
data class ConfigField(
    val type: String,
    val description: String? = null,
    val default: String? = null,
    val required: Boolean? = null,
    val values: List<String>? = null
)