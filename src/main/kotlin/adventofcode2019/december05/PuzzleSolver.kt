package com.adventofcode2019.december05

import adventofcode2019.PuzzleSolverAbstract
import java.lang.Exception

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val baseIntCodeProgram = inputLines.first().split(",").map{it.toInt()}

    override fun resultPartOne(): String {
        val intCodeProgram = resetMemory()
        runProgram(intCodeProgram, listOf(1))
        return intCodeProgram[0].toString()
    }

    override fun resultPartTwo(): String {
        val intCodeProgram = resetMemory()
        runProgram(intCodeProgram, listOf(5))
        return intCodeProgram[0].toString()
    }


    private fun resetMemory(): MutableList<Int> {
        return baseIntCodeProgram.toMutableList()
    }

    private fun runProgram(intCodeProgram: MutableList<Int>, inputList: List<Int>) {
        var i=0
        while (i < intCodeProgram.size) {
            println("$i -> ${intCodeProgram[i]}")
            val opCode = intCodeProgram[i] % 100
            when (opCode) {
                1 -> { // add
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    val index3 = getIndex(intCodeProgram, i, 3)
                    intCodeProgram[index3] = intCodeProgram[index1] + intCodeProgram[index2]
                    i += 4
                }
                2 -> { //multiply
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    val index3 = getIndex(intCodeProgram, i, 3)
                    intCodeProgram[index3] = intCodeProgram[index1] * intCodeProgram[index2]
                    i += 4
                }
                3 -> { //get input
                    val index1 = getIndex(intCodeProgram, i, 1)
                    intCodeProgram[index1] = inputList.first()
                    i += 2
                }
                4 -> { // output
                    val index1 = getIndex(intCodeProgram, i, 1)
                    println("Output: ${intCodeProgram[index1]}")
                    i += 2
                }

                5 -> { // jump if true
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    if (intCodeProgram[index1] != 0)
                        i = intCodeProgram[index2]
                    else
                        i += 3
                }
                6 -> { // jump if false
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    val tester = intCodeProgram[index1]
                    val new = if (tester == 0)
                        intCodeProgram[index2]
                    else
                        i+3
                    i = new
                }
                7 -> { // less than
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    val index3 = getIndex(intCodeProgram, i, 3)
                    intCodeProgram[index3] = if (intCodeProgram[index1] < intCodeProgram[index2]) 1 else 0
                    i += 4
                }
                8 -> { // equals
                    val index1 = getIndex(intCodeProgram, i, 1)
                    val index2 = getIndex(intCodeProgram, i, 2)
                    val index3 = getIndex(intCodeProgram, i, 3)
                    intCodeProgram[index3] = if (intCodeProgram[index1] == intCodeProgram[index2]) 1 else 0
                    i += 4
                }
                99 -> { // halt
                    return
                }
                else -> throw Exception("Hee...")
            }
        }
    }

    private fun getIndex(intCodeProgram: MutableList<Int>, startIndex: Int, parameterNummer: Int): Int {
        val div = if (parameterNummer == 1) 100 else if (parameterNummer == 2) 1000 else 10000
        val paramType = (intCodeProgram[startIndex] / div) % 10
        return if (paramType == 0) intCodeProgram[startIndex+parameterNummer] else startIndex+parameterNummer
    }
}


