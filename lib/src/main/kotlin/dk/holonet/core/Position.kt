package dk.holonet.core

enum class Position(s: String) {
    TOP_BAR("top_bar"),
    BOTTOM_BAR("bottom_bar"),
    TOP_LEFT("top_left"),
    BOTTOM_LEFT("bottom_left"),
    TOP_RIGHT("top_right"),
    BOTTOM_RIGHT("bottom_right"),
    TOP_CENTER("top_center"),
    BOTTOM_CENTER("bottom_center"),
    UPPER_THIRD("upper_third"),
    MIDDLE_THIRD("middle_third"),
    LOWER_THIRD("lower_third");

    companion object {
        fun fromString(s: String): Position {
            return when (s) {
                "top_bar" -> TOP_BAR
                "bottom_bar" -> BOTTOM_BAR
                "top_left" -> TOP_LEFT
                "bottom_left" -> BOTTOM_LEFT
                "top_right" -> TOP_RIGHT
                "bottom_right" -> BOTTOM_RIGHT
                "top_center" -> TOP_CENTER
                "bottom_center" -> BOTTOM_CENTER
                "upper_third" -> UPPER_THIRD
                "middle_third" -> MIDDLE_THIRD
                "lower_third" -> LOWER_THIRD
                else -> throw IllegalArgumentException("Unknown border position: $s")
            }
        }
    }
}

