package adventofcode2019.position

enum class WindDirection(val dX: Int, val dY: Int, val directionNumber: Int) {
    NORTH(0,1, 1) {
        override fun rotateRight() = EAST
        override fun rotateLeft() = WEST
    },
    SOUTH(0,-1, 2) {
        override fun rotateRight() = WEST
        override fun rotateLeft() = EAST
    },
    WEST(-1,0, 3) {
        override fun rotateRight() = NORTH
        override fun rotateLeft() = SOUTH
    },
    EAST(1,0, 4) {
        override fun rotateRight() = SOUTH
        override fun rotateLeft() = NORTH
    };

    abstract fun rotateRight(): WindDirection
    abstract fun rotateLeft(): WindDirection
    override fun toString() = directionNumber.toString()
    fun opposite() = rotateLeft().rotateLeft()

    fun dRow() = -dY
    fun dCol() = dX
}
