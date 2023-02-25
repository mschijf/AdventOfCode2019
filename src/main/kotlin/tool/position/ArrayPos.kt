package tool.position

data class ArrayPos(val row: Int, val col: Int) {
    fun moveOneStep(dir: Direction): ArrayPos {
        return ArrayPos(row+dir.dRow(), col+dir.dCol())
    }

    fun moveOneStep(dir: WindDirection): ArrayPos {
        return ArrayPos(row+dir.dRow(), col+dir.dCol())
    }

    fun neighbors() = Direction.values().map { dir -> Coordinate(row+dir.dRow(), col+dir.dCol()) }
}
