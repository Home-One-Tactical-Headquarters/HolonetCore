package dk.holonet.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PositionSerializer : KSerializer<Position> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Position",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Position) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): Position {
        val position = decoder.decodeString().uppercase()
        return try {
            Position.valueOf(position)
        } catch (e: IllegalArgumentException) {
            Position.CENTER // Default fallback
        }
    }
}