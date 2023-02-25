package adventofcode2019.december19

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import tool.position.Coordinate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.max

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
        val upperLeft = drone.findUpperLeftSquare(100)
        println(upperLeft)
        return (upperLeft.x * 10_000 + upperLeft.y).toString()
    }

}

class Drone(inputLine: String) {
    private val programCode = inputLine.split(",").map { it.toLong() }

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

    //
    // assuming we have a pos, which is the most left '#' on a line
    // go down 1, and check if there is alos a '#' (start with one position to th eleftm to be sure.
    //            if not, keep wlaking to the right until we found one.
    // if we don't found one after 1000 attempts (highly overrated), go down another line
    // safety net after checking 1000 lines
    private fun findFirstPosNextLine(pos: Coordinate): Coordinate {
        for (y in pos.y + 1 .. pos.y+1000) {
            for (x in max(0, pos.x-1)..pos.x + 1000) {
                if (getDroneOutput(x, y) == 1) {
                    return Coordinate(x, y)
                }
            }
        }
        throw Exception("next affected point not found within square of 1000x1000 from $pos")
    }


    //
    // go down, to find most left '#' on a line = bottomLeft
    // check if upperRight is also '#'. If so, then we have a 100x100 square
    // return the upperLeft
    //
    fun findUpperLeftSquare(squareSize: Int): Coordinate {
        var bottomLeft = Coordinate(0, 0)
        while (bottomLeft.y < squareSize-1 || getDroneOutput(bottomLeft.x+(squareSize-1), bottomLeft.y-(squareSize-1)) != 1) {
            bottomLeft = findFirstPosNextLine(bottomLeft)
        }
        return Coordinate(bottomLeft.x, bottomLeft.y-(squareSize-1))
    }

}