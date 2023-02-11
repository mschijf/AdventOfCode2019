package adventofcode2019.december15

import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resultPartOne(): String = runBlocking() {
        val intCodeProgram = IntCodeProgramCR( inputLines.first().split(",").map { it.toLong() } )
        val job = async {
            intCodeProgram.runProgram()
        }
        val distance = solve(intCodeProgram, Pos(0,0), emptySet()).also {
            job.cancel()
        }

       distance.toString()
    }

    private suspend fun solve(robot: IntCodeProgramCR, currentPos: Pos, nodesVisited: Set<Pos>): Int {
        var shortestDistanceToEnd = 99999999
        for (direction in WindDirection.values()) {
            val result = doMove(robot, direction)
            if (result == 2) {
                shortestDistanceToEnd = 1
                undoMove(robot, direction)
                break
            } else if (result == 1) {
                val newPos = currentPos.moveOneStep(direction)
                if (newPos !in nodesVisited) {
                    val distanceToEnd = 1+solve(robot, newPos, nodesVisited + currentPos)
                    shortestDistanceToEnd = min(shortestDistanceToEnd, distanceToEnd)
                }
                undoMove(robot, direction)
            } else {
//                println("hit a wall")
            }
        }
        return shortestDistanceToEnd
    }

    private suspend fun doMove(robot: IntCodeProgramCR, direction: WindDirection): Int {
        robot.input.send(direction.directionNumber.toLong())
        return robot.output.receive().toInt()
    }

    private suspend fun undoMove(robot: IntCodeProgramCR, direction: WindDirection): Int {
        return doMove(robot, direction.opposite())
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resultPartTwo(): String = runBlocking() {
        "NIMP"
    }
}


