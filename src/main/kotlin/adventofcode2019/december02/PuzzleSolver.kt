package com.adventofcode2019.december02

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    val baseIntCodeProgram = inputLines.first().split(",").map{it.toInt()}

    override fun resultPartOne(): String {
        val intCodeProgram = resetMemory(12, 2)
        runProgram(intCodeProgram)
        return intCodeProgram[0].toString()
    }

    override fun resultPartTwo(): String {
        for (noun in 0 .. 99) {
            for (verb in 0 .. 99) {
                val intCodeProgram = resetMemory(noun, verb)
                runProgram(intCodeProgram)
                if (intCodeProgram[0] == 19690720)
                    return (100*noun + verb).toString()
            }
        }
        return "NIKS"
    }

    private fun resetMemory(input1: Int, input2: Int): MutableList<Int> {
        val intCodeProgram = baseIntCodeProgram.toMutableList()
        if (!test) {
            intCodeProgram[1] = input1
            intCodeProgram[2] = input2
        }
        return intCodeProgram
    }

    private fun runProgram(intCodeProgram: MutableList<Int>) {
        for (i in 0 until intCodeProgram.size step 4) {
            when (intCodeProgram[i]) {
                1 -> intCodeProgram[intCodeProgram[i+3]] = intCodeProgram[intCodeProgram[i+1]] + intCodeProgram[intCodeProgram[i+2]]
                2 -> intCodeProgram[intCodeProgram[i+3]] = intCodeProgram[intCodeProgram[i+1]] * intCodeProgram[intCodeProgram[i+2]]
                99 -> return
            }
        }
    }
}


