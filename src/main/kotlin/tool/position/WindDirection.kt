package tool.position

enum class WindDirection(val dX: Int, val dY: Int, val directionSymbol: String) {
    NORTH(0,1, "N") {
        override fun rotateRight() = NORTHEAST
        override fun rotateLeft() = NORTHWEST
    },
    SOUTH(0,-1, "S") {
        override fun rotateRight() = SOUTHWEST
        override fun rotateLeft() = SOUTHEAST
    },
    WEST(-1,0, "W") {
        override fun rotateRight() = NORTHWEST
        override fun rotateLeft() = SOUTHWEST
    },
    EAST(1,0, "E") {
        override fun rotateRight() = SOUTHEAST
        override fun rotateLeft() = NORTHEAST
    },
    NORTHEAST(1,1, "NE") {
        override fun rotateRight() = EAST
        override fun rotateLeft() = NORTH
    },
    NORTHWEST(-1,1, "NW") {
        override fun rotateRight() = NORTH
        override fun rotateLeft() = WEST
    },
    SOUTHEAST(1,-1, "SE") {
        override fun rotateRight() = SOUTH
        override fun rotateLeft() = EAST
    },
    SOUTHWEST(-1,-1, "SW") {
        override fun rotateRight() = WEST
        override fun rotateLeft() = SOUTH
    };

    abstract fun rotateRight(): WindDirection
    abstract fun rotateLeft(): WindDirection
    override fun toString() = directionSymbol
    fun opposite() = rotateLeft().rotateLeft().rotateLeft().rotateLeft()

    fun dRow() = -dY
    fun dCol() = dX
}
