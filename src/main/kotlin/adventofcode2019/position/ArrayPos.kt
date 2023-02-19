package adventofcode2019.position

data class ArrayPos(val row: Int, val col: Int) {
    fun moveOneStep(dir: Direction): ArrayPos {
        return ArrayPos(row+dir.dRow(), col+dir.dCol())
    }

    fun moveOneStep(dir: WindDirection): ArrayPos {
        return ArrayPos(row+dir.dRow(), col+dir.dCol())
    }
}
