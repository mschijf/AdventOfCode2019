package adventofcode2019.december13Coroutines

data class Pos(val x: Int, val y: Int) {
    fun moveOneStep(dir: Direction): Pos {
        return when (dir) {
            Direction.RIGHT -> Pos(x+1, y)
            Direction.DOWN -> Pos(x, y-1)
            Direction.LEFT -> Pos(x-1, y)
            Direction.UP -> Pos(x, y+1)
        }
    }
    fun moveOneStep(dir: WindDirection): Pos {
        return when (dir) {
            WindDirection.EAST -> Pos(x+1, y)
            WindDirection.SOUTH -> Pos(x, y-1)
            WindDirection.WEST -> Pos(x-1, y)
            WindDirection.NORTH -> Pos(x, y+1)
        }
    }
}

enum class Direction(val dX: Int, val dY: Int, private val directionChar: Char) {
    RIGHT(0,1, '>') {
        override fun rotateRight() = DOWN
        override fun rotateLeft() = UP
    },
    DOWN(1,0, 'v') {
        override fun rotateRight() = LEFT
        override fun rotateLeft() = RIGHT
    },
    LEFT(0,-1, '<') {
        override fun rotateRight() = UP
        override fun rotateLeft() = DOWN
    },
    UP(-1,0, '^') {
        override fun rotateRight() = RIGHT
        override fun rotateLeft() = LEFT
    };


    abstract fun rotateRight(): Direction
    abstract fun rotateLeft(): Direction
    override fun toString() = directionChar.toString()
}

enum class WindDirection(val dX: Int, val dY: Int, private val directionChar: Char) {
    EAST(0,1, '>') {
        override fun rotateRight() = SOUTH
        override fun rotateLeft() = NORTH
    },
    SOUTH(1,0, 'v') {
        override fun rotateRight() = WEST
        override fun rotateLeft() = EAST
    },
    WEST(0,-1, '<') {
        override fun rotateRight() = NORTH
        override fun rotateLeft() = SOUTH
    },
    NORTH(-1,0, '^') {
        override fun rotateRight() = EAST
        override fun rotateLeft() = WEST
    };


    abstract fun rotateRight(): WindDirection
    abstract fun rotateLeft(): WindDirection
    override fun toString() = directionChar.toString()
}
