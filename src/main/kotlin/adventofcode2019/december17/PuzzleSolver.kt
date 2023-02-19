package adventofcode2019.december17

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

    private val scaffoldMap = ScaffoldMap(inputLines.first())

    override fun resultPartOne(): String {
        return scaffoldMap.calculateCrosspointsSum().toString()
    }

    override fun resultPartTwo(): String {
        val path = scaffoldMap.makePath()
        val movementFunctionList = scaffoldMap.makeMovementFunctionList(path, 3, 10)
        val mainMovementRoutine = scaffoldMap.makeMainMovementRoutine(path, movementFunctionList)
        
        println(path)
        println(mainMovementRoutine)
        movementFunctionList.forEachIndexed { index, movementFunction -> println("Function ${'A'+index}: $movementFunction") }
        
        val robot = Robot(inputLines.first())
        val dustCollected = robot.visitAllScaffoldParts(mainMovementRoutine, movementFunctionList)
        return dustCollected.toString()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ScaffoldMap(inputLine: String) {

    private val view: Array<CharArray>
    private var robotPos: ArrayPos
    private var robotDir: Direction

    init {
        robotPos = ArrayPos(0,0)
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
                        robotPos = ArrayPos(row, col)
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
            val scaffoldLength = walkStraightLine()
            result = "$result$rotateString,$scaffoldLength,"
            rotateString = turnToScaffold()
        }
        return result
    }

    private fun turnToScaffold(): String {
        val turnLeftPos = robotPos.moveOneStep(robotDir.rotateLeft())
        if (turnLeftPos.row in view.indices && turnLeftPos.col in view[turnLeftPos.row].indices && view[turnLeftPos.row][turnLeftPos.col] == '#') {
            robotDir = robotDir.rotateLeft()
            return "L"
        }
        val turnRightPos = robotPos.moveOneStep(robotDir.rotateRight())
        if (turnRightPos.row in view.indices && turnRightPos.col in view[turnRightPos.row].indices && view[turnRightPos.row][turnRightPos.col] == '#') {
            robotDir = robotDir.rotateRight()
            return "R"
        }
        return ""
    }

    private fun walkStraightLine(): Int {
        var steps = 0
        var nextPos = robotPos.moveOneStep(robotDir)
        while (nextPos.row in view.indices && nextPos.col in view[nextPos.row].indices && view[nextPos.row][nextPos.col] == '#') {
            robotPos = nextPos
            nextPos = robotPos.moveOneStep(robotDir)
            steps++
        }
        return steps
    }

    fun makeMovementFunctionList(path: String,
                                 uniqueFunctionsToUse: Int,
                                 totalFunctionsToUse: Int,
                                 functionsList: List<String> = emptyList()): List<String> {

        if (uniqueFunctionsToUse <= 0 || totalFunctionsToUse <= 0) {
            if (path.isEmpty()) {
                return functionsList
            }
            return emptyList()
        }         

        val possibleFunctionList = createPossibleFunctions(path, 20)
        for (possibleFunction in possibleFunctionList) {
            val newString = path.replace(possibleFunction, "")
            val possibleFunctionOccurences = (path.length - newString.length) / possibleFunction.length
            val result = makeMovementFunctionList(
                newString,
                uniqueFunctionsToUse-1,
                totalFunctionsToUse - possibleFunctionOccurences,
                functionsList+possibleFunction)
            if (result.isNotEmpty())
                return result
        }

        return emptyList()
    }

    private fun createPossibleFunctions(path: String, maxLen: Int): List<String> {
        val result = mutableListOf<String>()
        for (i in 0 until min(path.length, maxLen)) {
            if (path[i] == ',')
                result.add(path.substring(0,i+1))
        }
        return result
    }

    fun makeMainMovementRoutine(path: String, movementFunctionList: List<String>): String {
        var result = ""
        var leftOver = path
        while (leftOver.isNotEmpty()) {
            val index = movementFunctionList.indexOfFirst { leftOver.startsWith(it) }
            result = "$result${'A' + index},"
            leftOver = leftOver.substringAfter(movementFunctionList[index])
        }
        return result
    }
}

class Robot(inputLine: String) {
    private val program = IntCodeProgramCR(inputLine.split(",").map { it.toLong() })
    init {
        program.setMemoryFieldValue(0, 2)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun visitAllScaffoldParts(mainMovementRoutine: String, movementFunctionList: List<String>):Long = runBlocking {
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
        var lastOutput = 0L
        while (!program.output.isClosedForReceive) {
            lastOutput = program.output.receive()
            if (lastOutput < 255)
                print(lastOutput.toInt().toChar())
        }
        lastOutput
    }
}


