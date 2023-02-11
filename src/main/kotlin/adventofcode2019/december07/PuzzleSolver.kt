package com.adventofcode2019.december07

import adventofcode2019.PuzzleSolverAbstract
import tool.math.makeAllPermutations
import java.lang.Exception

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val baseIntCodeProgram = inputLines.first().split(",").map{it.toInt()}

    override fun resultPartOne(): String {
        val allPermutations = makeAllPermutations(listOf(0, 1, 2, 3, 4))
        val result = allPermutations.maxOf { permutation -> runSetting(permutation) }
        return result.toString()
    }

    private fun runSetting(phaseSettingList: List<Int>): Int {
        val amplifierList = phaseSettingList.map{phaseSetting -> Amplifier(baseIntCodeProgram.toMutableList(), phaseSetting) }
        var result = 0
        repeat(5) {amplifierIndex ->
            result = amplifierList[amplifierIndex].runProgram(result)
        }
        return result
    }

    override fun resultPartTwo(): String {
        val allPermutations = makeAllPermutations(listOf(5, 6, 7, 8, 9))
        val result = allPermutations.maxOf { permutation -> runSettingPart2(permutation) }
        return result.toString()
    }

    private fun runSettingPart2(phaseSettingList: List<Int>): Int {
        val amplifierList = phaseSettingList.map{phaseSetting -> Amplifier(baseIntCodeProgram.toMutableList(), phaseSetting) }

        var result = 0
        var amplifierIndex = 0
        while(!amplifierList[4].isFinished) {
            result = amplifierList[amplifierIndex].runProgram(result)
            amplifierIndex = (amplifierIndex + 1) % 5
        }
        return result
    }
}

class Amplifier(private val intCodeProgram: MutableList<Int>, phaseSetting: Int) {
    private val inputList = mutableListOf(phaseSetting)
    private var lastOutput = -999999

    var isFinished = false
        private set

    private var currentIndex = 0

    fun runProgram(inputValue: Int): Int {
        inputList.add(inputValue)
        while (currentIndex < intCodeProgram.size) {
            val opCode = intCodeProgram[currentIndex] % 100
            when (opCode) {
                1 -> { // add
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    val index3 = getIndex(currentIndex, 3)
                    intCodeProgram[index3] = intCodeProgram[index1] + intCodeProgram[index2]
                    currentIndex += 4
                }
                2 -> { //multiply
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    val index3 = getIndex(currentIndex, 3)
                    intCodeProgram[index3] = intCodeProgram[index1] * intCodeProgram[index2]
                    currentIndex += 4
                }
                3 -> { //get input
                    val index1 = getIndex(currentIndex, 1)
                    intCodeProgram[index1] = inputList.removeFirst()
                    currentIndex += 2
                }
                4 -> { // output
                    val index1 = getIndex(currentIndex, 1)
                    lastOutput = intCodeProgram[index1]
//                    println("Output: $lastOutput")
                    currentIndex += 2
                    return lastOutput
                }

                5 -> { // jump if true
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    if (intCodeProgram[index1] != 0)
                        currentIndex = intCodeProgram[index2]
                    else
                        currentIndex += 3
                }
                6 -> { // jump if false
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    val tester = intCodeProgram[index1]
                    val new = if (tester == 0)
                        intCodeProgram[index2]
                    else
                        currentIndex+3
                    currentIndex = new
                }
                7 -> { // less than
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    val index3 = getIndex(currentIndex, 3)
                    intCodeProgram[index3] = if (intCodeProgram[index1] < intCodeProgram[index2]) 1 else 0
                    currentIndex += 4
                }
                8 -> { // equals
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    val index3 = getIndex(currentIndex, 3)
                    intCodeProgram[index3] = if (intCodeProgram[index1] == intCodeProgram[index2]) 1 else 0
                    currentIndex += 4
                }
                99 -> { // halt
                    isFinished = true
                    return lastOutput
                }
                else -> throw Exception("Hee...")
            }
        }
        throw Exception("surprise")
    }

    private fun getIndex(startIndex: Int, parameterNumber: Int): Int {
        val div = if (parameterNumber == 1) 100 else if (parameterNumber == 2) 1000 else 10000
        val paramType = (intCodeProgram[startIndex] / div) % 10
        return if (paramType == 0) intCodeProgram[startIndex+parameterNumber] else startIndex+parameterNumber
    }
}



