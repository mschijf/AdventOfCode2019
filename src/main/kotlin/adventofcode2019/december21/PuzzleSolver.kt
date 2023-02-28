package adventofcode2019.december21

import adventofcode2019.Input
import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val options = listOf(0,1,2,3,4,12,13,14,23,24,34,123,124,134,234,1234)
        options.forEach {optionNumber ->
            val springdroid = Springdroid(inputLines.first(), optionNumber)
            val result = springdroid.walk()
            if (result >= 0) {
                println("program $optionNumber reaches the end! It returns $result")
            }
        }

        return "^^^^^"
    }

    // the answer is in droid_program5
    // haven't enterd it in the code, since it was copy-pasted.
    override fun resultPartTwo(): String {
        val options = listOf(5)
        options.forEach {optionNumber ->
            val springdroid = Springdroid(inputLines.first(), optionNumber)
            val result = springdroid.run(true)
            if (result >= 0) {
                println("program $optionNumber reaches the end! It returns $result")
            }
        }

        return "^^^^^"
    }

}

class Springdroid(inputLine: String, private val programNumber: Int) {

    private val intCodeProgram = IntCodeProgramCR( inputLine.split(",").map { it.toLong() } )
    private var result: Long = -1

    fun walk(printOutput: Boolean = false): Long = execute("WALK", printOutput)

    fun run(printOutput: Boolean = false): Long = execute("RUN", printOutput)

    private fun execute(goCommand: String, printOutput: Boolean): Long {
        runBlocking {
            launch {
                intCodeProgram.runProgram()
            }
            runProgram(goCommand)
            checkOutput(printOutput)
        }
        return result
    }

    private suspend fun runProgram(goCommand: String) {
        val program = Input("data/december21/", "droid_program$programNumber").inputLines
        program.dropLast(1).filter{it.isNotEmpty() && !it.startsWith("#")}.forEach { line -> sendInput(line) }
        sendInput(goCommand)
    }

    private suspend fun sendInput(s: String) {
        s.forEach { ch -> intCodeProgram.input.send(ch.code.toLong()) }
        intCodeProgram.input.send(10L)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun checkOutput(printOutput: Boolean) {
        while (!intCodeProgram.output.isClosedForReceive) {
            val output = intCodeProgram.output.receive()
            if (output > 255) {
                if (printOutput)
                    println(output)
                result = output
            } else {
                if (printOutput)
                    print(output.toInt().toChar())
            }
        }
    }


}

