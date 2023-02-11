package com.adventofcode2019.december13

class IntCodeProgram(baseIntCodeProgram: List<Long>, private val handleIO: IntCodeProgramIO) {
    private val intCodeProgram = MutableMapLongCodeProgram(baseIntCodeProgram.mapIndexed { index, l ->  Pair(index.toLong(), l)}.toMap())
    private var lastOutput = -999999L

    private var currentIndex = 0L
    private var relativeBase = 0L

    fun setMemoryFieldValue(index: Long, value: Long) {
        intCodeProgram[index] = value
    }

    fun runProgram(): Long {
        while (true) {
            val opCode = intCodeProgram[currentIndex] % 100
            when (opCode.toInt()) {
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
                    intCodeProgram[index1] = handleIO.read()
                    currentIndex += 2
                }
                4 -> { // output
                    val index1 = getIndex(currentIndex, 1)
                    lastOutput = intCodeProgram[index1]
                    currentIndex += 2
                    handleIO.write(lastOutput)
                }
                5 -> { // jump if true
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    if (intCodeProgram[index1] != 0L)
                        currentIndex = intCodeProgram[index2]
                    else
                        currentIndex += 3
                }
                6 -> { // jump if false
                    val index1 = getIndex(currentIndex, 1)
                    val index2 = getIndex(currentIndex, 2)
                    if (intCodeProgram[index1] == 0L)
                        currentIndex = intCodeProgram[index2]
                    else
                        currentIndex += 3
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
                9 -> { // Opcode 9 adjusts the relative base by the value of its only parameter. The relative base increases (or decreases, if the value is negative) by the value of the parameter.
                    val index1 = getIndex(currentIndex, 1)
                    relativeBase += intCodeProgram[index1]
                    currentIndex += 2
                }
                99 -> { // halt
                    return lastOutput
                }
                else -> throw Exception("Hee...")
            }
        }
    }

    private fun getIndex(startIndex: Long, parameterNumber: Int): Long {
        val div = if (parameterNumber == 1) 100 else if (parameterNumber == 2) 1000 else 10000
        val paramType = (intCodeProgram[startIndex] / div) % 10
        return when (paramType.toInt()) {
            0 -> intCodeProgram[startIndex+parameterNumber]
            1 -> startIndex+parameterNumber
            else -> +relativeBase+intCodeProgram[startIndex+parameterNumber]
        }
    }
}

class MutableMapLongCodeProgram(baseMap: Map<Long, Long>) {
    private val myMap = baseMap.toMutableMap()
    operator fun get(index: Long): Long {
        return myMap[index] ?: 0L
    }
    operator fun set(index: Long, value: Long) {
        myMap[index] = value
    }
}

interface IntCodeProgramIO {
    fun write(output: Long)
    fun read(): Long
}
