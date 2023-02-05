
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.sign

fun main() {
    PuzzleSolver(test=false, 13).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resultPartOne(): String = runBlocking() {
        val intCodeProgram = IntCodeProgramCR( inputLines.first().split(",").map { it.toLong() } )
        intCodeProgram.runProgram()

        val numbers = mutableListOf<Long>()
        while (!intCodeProgram.output.isClosedForReceive) {
            numbers.add(intCodeProgram.output.receive())
        }
        numbers.chunked(3).count { it[2] == 2L }.toString()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resultPartTwo(): String = runBlocking() {
        val intCodeProgram = IntCodeProgramCR( inputLines.first().split(",").map { it.toLong() } )
        intCodeProgram.setMemoryFieldValue(0, 2)
        launch {
            intCodeProgram.runProgram()
        }

        var score = 0L
        var paddlePos = Pos(0,0)
        var ballPos = Pos(0,0)
        while (!intCodeProgram.output.isClosedForReceive) {
            val tile = Tile (intCodeProgram.output.receive(), intCodeProgram.output.receive(), intCodeProgram.output.receive())
            if (tile.pos.x == -1 && tile.pos.y == 0) {
                score = tile.type
            } else {
                when (tile.type) {
                    3L -> paddlePos = tile.pos
                    4L -> {
                        ballPos = tile.pos
                        intCodeProgram.input.send((ballPos.x - paddlePos.x).sign.toLong())
                    }
                }
            }
        }
        score.toString()
    }
}


class Tile (x: Long, y: Long, val type: Long) {
    val pos = Pos(x.toInt(), y.toInt())
}
