package tool.position

enum class Direction(val dX: Int, val dY: Int, val directionChar: Char) {
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
    fun opposite() = rotateLeft().rotateLeft()

    fun dRow() = -dY
    fun dCol() = dX
}
