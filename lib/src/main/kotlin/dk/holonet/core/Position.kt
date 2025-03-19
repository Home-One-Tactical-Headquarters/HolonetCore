package dk.holonet.components

enum class Position(s: String) {
    TOP("top"),
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
    BOTTOM("bottom");

    companion object {
        fun fromString(s: String): Position {
            return when (s) {
                "top" -> TOP
                "left" -> LEFT
                "center" -> CENTER
                "right" -> RIGHT
                "bottom" -> BOTTOM
                else -> throw IllegalArgumentException("Unknown border position: $s")
            }
        }
    }
}

