package com.adventofcode2019.december04

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val start = inputLines.first().split("-")[0].toInt()
        val end = inputLines.first().split("-")[1].toInt()
        return (start..end).count { checkValidPassword(it.toString()) }.toString()
    }

    override fun resultPartTwo(): String {
        val start = inputLines.first().split("-")[0].toInt()
        val end = inputLines.first().split("-")[1].toInt()
        return (start..end).count { checkValidPasswordPart2(it.toString()) }.toString()
    }

    private fun checkValidPassword(pwd: String): Boolean {
        return (pwd.length == 6) &&
                (pwd.filterIndexed { index, c -> index >= 1 && pwd[index - 1] == c }.isNotEmpty()) &&
                (pwd.filterIndexed { index, c -> index >= 1 && pwd[index - 1] > c }.isEmpty())
    }

    //1490 too high
    //981 too low
    private fun checkValidPasswordPart2(pwd: String): Boolean {
        return (pwd.length == 6) &&
                (hasExactDouble(pwd)) &&
                (pwd.filterIndexed { index, c -> index >= 1 && pwd[index - 1] > c }.isEmpty())
    }

    private fun hasExactDouble(pwd: String) : Boolean {
        var count = 1
        for (i in 1  until pwd.length) {
            if (pwd[i - 1] == pwd[i]) {
                count++
            } else {
                if (count == 2)
                    return true
                count = 1
            }
        }
        return count == 2
    }
}


