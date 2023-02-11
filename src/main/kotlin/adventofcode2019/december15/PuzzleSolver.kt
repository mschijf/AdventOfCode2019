package adventofcode2019.december15

import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    private val maze = initMaze()
    private val oxygenSystemLocation = maze.entries.first { it.value == 2 }.key

    private fun initMaze(): MutableMap<Pos, Int> = runBlocking{
        val robot = IntCodeProgramCR( inputLines.first().split(",").map { it.toLong() } )
        val job = launch {
            robot.runProgram()
        }
        val localmaze = mutableMapOf<Pos, Int>()
        createMaze(localmaze, robot, Pos(0,0)).also {
            job.cancel()
        }
        localmaze
    }

    private suspend fun createMaze(maze: MutableMap<Pos, Int>, robot: IntCodeProgramCR, currentPos: Pos) {
        for (direction in WindDirection.values()) {
            val newPos = currentPos.moveOneStep(direction)
            if (!maze.contains(newPos)) {
                val result = doMove(robot, direction)
                if (result == 2 || result == 1) {
                    maze[newPos] = result
                    createMaze(maze, robot, newPos)
                    undoMove(robot, direction)
                } else {
                    maze[newPos] = 0
                }
            }
        }
    }

    private suspend fun doMove(robot: IntCodeProgramCR, direction: WindDirection): Int {
        robot.input.send(direction.directionNumber.toLong())
        return robot.output.receive().toInt()
    }

    private suspend fun undoMove(robot: IntCodeProgramCR, direction: WindDirection): Int {
        return doMove(robot, direction.opposite())
    }

    //------------------------------------------------------------------------------------------------------------------

    override fun resultPartOne(): String  {
        return solve(Pos(0,0), emptySet()).toString()
    }

    override fun resultPartTwo(): String {
        fillMazeWithOxygen()
        return (maze.values.max() - 2).toString()
    }

    private fun solve(currentPos: Pos, nodesVisited: Set<Pos>): Int {
        if (currentPos == oxygenSystemLocation)
            return 0

        var shortestDistanceToEnd = 99999999
        for (direction in WindDirection.values()) {
            val newPos = currentPos.moveOneStep(direction)
            if (maze[newPos] != 0 && newPos !in nodesVisited) {
                val distanceToEnd = 1+solve(newPos, nodesVisited + currentPos)
                shortestDistanceToEnd = min(shortestDistanceToEnd, distanceToEnd)
            }
        }
        return shortestDistanceToEnd
    }

    private fun fillMazeWithOxygen() {
        val queue: Queue<Pos> = LinkedList()
        queue.add(oxygenSystemLocation)
        while (queue.isNotEmpty()) {
            val currentPos = queue.remove()
            for (direction in WindDirection.values()) {
                val newPos = currentPos.moveOneStep(direction)
                if (maze[newPos] == 1) {
                    maze[newPos] = maze[currentPos]!! + 1
                    queue.add(newPos)
                }
            }
        }
    }



}


