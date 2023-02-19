package adventofcode2019.december17

import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean, monthDay: Int? = null) : PuzzleSolverAbstract(test, monthDay) {

    private val view = View(inputLines.first())

    override fun resultPartOne(): String {
        return view.calculateCrosspointsSum().toString()
    }

    override fun resultPartTwo(): String {
        val path = view.makePath()
        val movementFunctionList = view.makeBuckets(path, 0, 0)
        val mainMovementRoutine = view.makeMainMovementRoutine(path, movementFunctionList)
        println(path)
        println(mainMovementRoutine)
        movementFunctionList.forEachIndexed { index, movementFunction -> println("Function ${'A'+index}: $movementFunction") }
        secondPart(mainMovementRoutine, movementFunctionList)
        return "see ^^^"
    }

    private fun secondPart(mainMovementRoutine: String, movementFunctionList: List<String>) = runBlocking {
        val program = IntCodeProgramCR(inputLines.first().split(",").map { it.toLong() })
        program.setMemoryFieldValue(0, 2)
        launch {

            program.runProgram()
        }
        mainMovementRoutine.dropLast(1).map { it.code.toLong() }.forEach { program.input.send(it) }
        program.input.send(10L)
        movementFunctionList.forEach {movementFunction ->
            movementFunction.dropLast(1).map { it.code.toLong() }.forEach { program.input.send(it) }
            program.input.send(10L)
        }
        program.input.send(('n'.code).toLong())
        program.input.send(10L)
        while (!program.output.isClosedForReceive) {
            val ch = program.output.receive()
            print(if (ch < 255) ch.toInt().toChar() else ch)
        }
        println()
    }

}

class View(inputLine: String) {

    private val view: Array<CharArray>
    private var robotPos: FlippedPos
    private var robotDir: Direction

    init {
        robotPos = FlippedPos(0,0)
        robotDir = Direction.LEFT
        view = Array(50) { CharArray(50) {' '} }
        runBlocking {
            val intCodeProgram = IntCodeProgramCR( inputLine.split(",").map { it.toLong() } )
            launch {
                intCodeProgram.runProgram()
            }

            var row = 0
            var col = 0
            while (!intCodeProgram.output.isClosedForReceive) {
                val ch = intCodeProgram.output.receive().toInt()
                if (ch == 10) {
                    row++
                    col = 0
                } else  {
                    if (ch.toChar() in listOf('<', '>', '^', 'v')) {
                        robotPos = FlippedPos(col, row)
                        robotDir = Direction.values().first{dir -> dir.directionChar == ch.toChar()}
                    }
                    view[row][col] = ch.toChar()
                    col++
                }
                print(ch.toChar())
            }
        }
    }


    fun calculateCrosspointsSum(): Int {
        var sum = 0
        for (row in view.indices) {
            for (col in view[row].indices) {
                if (row in 2..49 && col in 2..49 && view[row][col] == '#') {
                    if (view[row - 1][col] == '#' && view[row + 1][col] == '#' && view[row][col - 1] == '#' && view[row][col + 1] == '#') {
                        sum += row * col
                    }
                }
            }
        }
        return sum
    }

    fun makePath(): String {
        var result = ""
        var rotateString = turnToScaffold()
        while (rotateString != "") {
            val scaffoldLength = walkScaffold()
            result = "$result$rotateString,$scaffoldLength,"
            rotateString = turnToScaffold()
        }
        return result
    }

    private fun turnToScaffold(): String {
        val turnLeftPos = robotPos.moveOneStep(robotDir.rotateLeft())
        if (turnLeftPos.y in view.indices && turnLeftPos.x in view[turnLeftPos.y].indices && view[turnLeftPos.y][turnLeftPos.x] == '#') {
            robotDir = robotDir.rotateLeft()
            return "L"
        }
        val turnRightPos = robotPos.moveOneStep(robotDir.rotateRight())
        if (turnRightPos.y in view.indices && turnRightPos.x in view[turnRightPos.y].indices && view[turnRightPos.y][turnRightPos.x] == '#') {
            robotDir = robotDir.rotateRight()
            return "R"
        }
        return ""
    }

    private fun walkScaffold(): Int {
        var steps = 0
        var nextPos = robotPos.moveOneStep(robotDir)
        while (nextPos.y in view.indices && nextPos.x in view[nextPos.y].indices && view[nextPos.y][nextPos.x] == '#') {
            robotPos = nextPos
            nextPos = robotPos.moveOneStep(robotDir)
            steps++
        }
        return steps
    }

    fun makeBuckets(wholeString: String, bucketTypesUsed: Int, bucketsUsed: Int, bucketsFilled: List<String> = emptyList()): List<String> {
        if (wholeString.isEmpty()) {
            if (bucketTypesUsed <= 3 && bucketsUsed <= 10)
                return bucketsFilled
            else
                return emptyList()
        }
        if (bucketTypesUsed > 3 || bucketsUsed > 10)
            return emptyList()

        val possibleBuckets = createPossibleBuckets(wholeString, 20)
        for (possibleBucket in possibleBuckets) {
            val newString = wholeString.replace(possibleBucket, "")
            val newBucketLength = (wholeString.length - newString.length) / possibleBucket.length
            val bf = makeBuckets(newString, bucketTypesUsed+1, bucketsUsed + newBucketLength, bucketsFilled+possibleBucket)
            if (bf.isNotEmpty())
                return bf
        }

        return emptyList()
    }

    private fun createPossibleBuckets(wholeString: String, maxLen: Int): List<String> {
        var result = mutableListOf<String>()
        for (i in 0 until min(wholeString.length, maxLen)) {
            if (wholeString[i] == ',')
                result.add(wholeString.substring(0,i+1))
        }
        return result
    }

    fun makeMainMovementRoutine(wholeString: String, movementFunctionList: List<String>): String {
        var result = ""
        var leftOver = wholeString
        while (leftOver.isNotEmpty()) {
            val index = movementFunctionList.indexOfFirst { leftOver.startsWith(it) }
            result = "$result${'A' + index},"
            leftOver = leftOver.substringAfter(movementFunctionList[index])
        }
        return result
    }
 }


