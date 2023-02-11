package adventofcode2019.december15

data class Pos(val x: Int, val y: Int) {
    fun moveOneStep(dir: Direction): Pos {
        return Pos(x+dir.dX, y+dir.dY)
    }

    fun moveOneStep(dir: WindDirection): Pos {
        return Pos(x+dir.dX, y+dir.dY)
    }
}

enum class Direction(val dX: Int, val dY: Int, private val directionChar: Char) {
    UP(0,1, '^') {
        override fun rotateRight() = RIGHT
        override fun rotateLeft() = LEFT
    },
    DOWN(0,-1, 'v') {
        override fun rotateRight() = LEFT
        override fun rotateLeft() = RIGHT
    },
    RIGHT(1,0, '>') {
        override fun rotateRight() = DOWN
        override fun rotateLeft() = UP
    },
    LEFT(-1,0, '<') {
        override fun rotateRight() = UP
        override fun rotateLeft() = DOWN
    };

    abstract fun rotateRight(): Direction
    abstract fun rotateLeft(): Direction
    override fun toString() = directionChar.toString()
}

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
}
