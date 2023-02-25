package adventofcode2019.december19

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import adventofcode2019.position.ArrayPos
import adventofcode2019.position.Direction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    private val gridMap = GridMap(inputLines.first())

    override fun resultPartOne(): String {
        gridMap.print()
        return gridMap.pointsAffected().toString()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class GridMap(inputLine: String) {

    private val gridSize = 100
    private val programCode = inputLine.split(",").map { it.toLong() }
//    private val grid = Array(gridSize){ y -> IntArray(gridSize) { x -> setGridValue(x, y)}  }

    private val grid: Array<IntArray> = Array(gridSize) { IntArray(gridSize) { 0 } }
    init {
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                setGridValue(x, y)
            }
        }
    }

    private fun setGridValue(x: Int, y: Int) = runBlocking {
        val intCodeProgram = IntCodeProgramCR(programCode)
        launch {
            intCodeProgram.runProgram()
        }
        intCodeProgram.input.send(x.toLong())
        intCodeProgram.input.send(y.toLong())
        grid[y][x] = intCodeProgram.output.receive().toInt()
//        intCodeProgram.output.receive().toInt()
    }

    fun pointsAffected() = grid.sumOf{ row -> row.sum() }

    fun print() {
        for (y in grid.indices) {
            for (x in grid[y].indices) {
                print(if (grid[y][x] == 1) '#' else '.')
            }
            println()
        }
    }
}
