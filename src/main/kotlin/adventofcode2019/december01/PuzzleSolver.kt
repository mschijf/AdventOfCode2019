package com.adventofcode2019.december01

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        return inputLines.sumOf { it.toInt() / 3 - 2 }.toString()
    }

    override fun resultPartTwo(): String {
        return inputLines.sumOf { fuelNeeded(it.toInt() / 3 - 2) }.toString()
    }

    private fun fuelNeeded(module: Int): Int {
        return if (module <= 0)
            0
        else
            module + fuelNeeded(module / 3 - 2)
    }
}


