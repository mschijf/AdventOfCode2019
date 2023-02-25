package tool.position

import kotlin.math.absoluteValue

data class Coordinate(val x: Int, val y: Int) {
    fun plusXY(dx: Int, dy: Int) = Coordinate(x+dx, y+dy)

    fun plusX(dx: Int) = plusXY(dx, 0)
    fun plusY(dy: Int) = plusXY(0, dy)

    fun moveOneStep(dir: Direction) = plusXY(dir.dX, dir.dY)
    fun moveOneStep(dir: WindDirection) = plusXY(dir.dX, dir.dY)

    fun neighbors() = listOf(up(), down(), left(), right())
    fun allWindDirectionNeighbors() = listOf(north(), northeast(), east(), southeast(), south(), southwest(), west(), northwest())

    fun up() = moveOneStep(Direction.UP)
    fun down() = moveOneStep(Direction.DOWN)
    fun left() = moveOneStep(Direction.LEFT)
    fun right() = moveOneStep(Direction.RIGHT)

    fun north() = moveOneStep(WindDirection.NORTH)
    fun south() = moveOneStep(WindDirection.SOUTH)
    fun east() = moveOneStep(WindDirection.EAST)
    fun west() = moveOneStep(WindDirection.WEST)
    fun northeast() = moveOneStep(WindDirection.NORTHEAST)
    fun southeast() = moveOneStep(WindDirection.SOUTHEAST)
    fun northwest() = moveOneStep(WindDirection.NORTHWEST)
    fun southwest() = moveOneStep(WindDirection.SOUTHWEST)

    fun manhattanDistance(otherPos: Coordinate) = (otherPos.x - x).absoluteValue + (otherPos.y - y).absoluteValue
}

