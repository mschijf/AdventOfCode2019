package adventofcode2019.position

data class Coordinate(val x: Int, val y: Int) {
    fun moveOneStep(dir: Direction): Coordinate {
        return Coordinate(x+dir.dX, y+dir.dY)
    }

    fun moveOneStep(dir: WindDirection): Coordinate {
        return Coordinate(x+dir.dX, y+dir.dY)
    }
}

