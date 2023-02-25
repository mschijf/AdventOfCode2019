package adventofcode2019.december19

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import adventofcode2019.position.Coordinate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    override fun resultPartOne(): String {
        val drone = Drone(inputLines.first())
        return drone.pointsAffected(50).toString()
    }

    override fun resultPartTwo(): String {
        val drone = Drone(inputLines.first())
//        drone.print(115)
        val upperLeft = drone.findUpperLeftSquare100()
        println(upperLeft)
        return (upperLeft.x * 10_000 + upperLeft.y).toString()
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class Drone(inputLine: String) {
    private val programCode = inputLine.split(",").map { it.toLong() }

    private fun getDroneOutput(pos: Coordinate) = getDroneOutput(pos.x, pos.y)

    private fun getDroneOutput(x: Int, y: Int) = runBlocking {
        val intCodeProgram = IntCodeProgramCR(programCode)
        launch {
            intCodeProgram.runProgram()
        }
        intCodeProgram.input.send(x.toLong())
        intCodeProgram.input.send(y.toLong())
        intCodeProgram.output.receive().toInt()
    }

    fun pointsAffected(squareSize: Int): Int {
        return (0 until squareSize)
            .sumOf{ x -> (0 until squareSize)
                .sumOf { y ->  getDroneOutput(x, y)}  }
    }

    fun print(squareSize: Int) {
        for (y in 0 until squareSize) {
            print("%2d  ".format(y))
            for (x in 0 until squareSize) {
                val ch = if (getDroneOutput(x,y) == 1) '#' else '.'
                print(ch)
            }
            println()
        }
    }

    private fun findFirstXNextLine(pos: Coordinate): Coordinate {
        for (y in pos.y + 1 .. pos.y+100) {
            for (x in min(0, pos.x-1)..pos.x + 100) {
                if (getDroneOutput(x, y) == 1) {
                    return Coordinate(x, y)
                }
            }
        }
        throw Exception("next affected point found within square of 10x10 from $pos")
    }

    private fun findFirstYLineWith100(pos: Coordinate): Coordinate {
        var nextPos = pos
        while (getDroneOutput(nextPos.x+(SQUARESIZE-1), nextPos.y) != 1) {
            nextPos = findFirstXNextLine(nextPos)
        }
        return nextPos
    }

    private fun findBottomYLine(pos: Coordinate): Coordinate {
        var nextPos = pos
        while (getDroneOutput(nextPos.x, nextPos.y-(SQUARESIZE-1)) != 1 || getDroneOutput(nextPos.x+(SQUARESIZE-1), nextPos.y-(SQUARESIZE-1)) != 1) {
            nextPos = findFirstXNextLine(nextPos)
        }
        return nextPos
    }

    fun findUpperLeftSquare100(): Coordinate {
        val startPos= Coordinate(0,0)
        var nextPos = findFirstYLineWith100(startPos)
        nextPos = findBottomYLine(nextPos)
        return Coordinate(nextPos.x, nextPos.y-(SQUARESIZE-1))
    }

    companion object {
        const val SQUARESIZE = 100
    }

}
