package adventofcode2019.december25

import adventofcode2019.Input
import adventofcode2019.IntCodeProgramCR
import adventofcode2019.PuzzleSolverAbstract
import kotlinx.coroutines.*
import java.util.*

fun main() {
    PuzzleSolver(test=false).showResult()
}

//   figure it out ;-)
//
//   Solution: create the program without 'execute command' function
//   Then you are able to do the adventure and walk through the space ship. You will find 11 items.
//   With these items, you go to 'secure checkpoint, where you will be weighted.
//   * three of them you can't pick up: molten lava, giant electromagnet and infinite loop
//   * you have to find out for the other eight what combination makes the right weight. (2^8 = 256 combinations)
//   * you can program this, but I started manually
//   *   I found out that with only manifold or only pointer or only loom, I was to heavy, so I could skip those.
//   *   still 5 items (2^5 = 32 combinations)
//   *   structurally, by trying combinations I found out that you need
//   *        klein bottle + mutex + hypercube
//   *   to have the right weight
//
//   After that, I added that to the set of commands, to have it 'saved'

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val computer = IntCodeProgramCR(inputLines.first())

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resultPartOne(): Any = runBlocking {
        launch {
            computer.runProgram()
        }

        launch {
//            executeCommandFile()
            while (!computer.output.isClosedForReceive) {
                showOutput()
                if (!computer.output.isClosedForReceive) {
                    val cmd = getConsoleInput()
                    sendInput(cmd)
                }
            }
        }

        super.resultPartOne()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun showOutput() {
        while (!computer.output.isEmpty && !computer.output.isClosedForReceive) {
            val ch = computer.output.receive().toInt()
            print(ch.toChar())
        }
    }

    private suspend fun sendInput(s: String) {
        s.forEach { ch -> computer.input.send(ch.code.toLong()) }
        computer.input.send(10L)
        delay(10)
    }



    private suspend fun executeCommandFile() {
        val cmdList = Input("data/december25/", "commands").inputLines
        cmdList.filter{!it.startsWith("#")}.forEach { cmd -> sendInput(cmd) }
    }

    private fun getConsoleInput(): String {
        return when (val consoleInput = readLine()) {
            "s" -> "south"
            "n" -> "north"
            "w" -> "west"
            "e" -> "east"
            "i" -> "inv"
            "tk" -> "take klein bottle"
            "tm" -> "take mutex"
            "tp" -> "take polygon"
            "th" -> "take hypercube"
            "tx" -> "take mutex"
            "dk" -> "drop klein bottle"
            "dm" -> "drop mutex"
            "dp" -> "drop polygon"
            "dh" -> "drop hypercube"
            "dx" -> "drop mutex"

            else -> consoleInput?:""
        }
    }
}


