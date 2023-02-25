package adventofcode2019.december17ToddGinsberg

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import tool.position.Coordinate
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val scaffold = mapScaffold()
        scaffold.print()
        return scaffold
            .filter { it.value == '#' }
            .keys
            .filter { it.neighbors().all { neighbor -> scaffold[neighbor] == '#' } }
            .map { it.x * it.y }
            .sum()
            .toString()
    }

    private suspend fun takePicture(computer: IntCodeProgramCR): Map<Coordinate, Char> =
        computer.output.consumeAsFlow()
            .map { it.toInt().toChar() }
            .toList()
            .joinToString("")
            .lines()
            .mapIndexed { y, row ->
                row.mapIndexed { x, c -> Coordinate(x, y) to c }
            }
            .flatten()
            .toMap()

    private fun mapScaffold(): Map<Coordinate, Char> = runBlocking {
        val computer = IntCodeProgramCR(inputLines.first().split(",").map { it.toLong() })
        launch {
            computer.runProgram()
        }
        takePicture(computer)
    }

    private fun <T> Map<Coordinate, T>.print() {
        val maxX = this.keys.maxBy { it.x }.x
        val maxY = this.keys.maxBy { it.y }.y

        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                print(this.getOrDefault(Coordinate(x, y), ' '))
            }
            println()
        }
    }
}


