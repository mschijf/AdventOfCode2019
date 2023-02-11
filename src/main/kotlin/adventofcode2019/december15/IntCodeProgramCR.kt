package adventofcode2019.december15

import kotlinx.coroutines.channels.Channel

class IntCodeProgramCR(baseIntCodeProgram: List<Long>) {
    private val intCodeProgram = MutableMapLongCodeProgram2(baseIntCodeProgram.mapIndexed { index, l ->  Pair(index.toLong(), l)}.toMap())

    private var currentIndex = 0L
    private var relativeBase = 0L

    private var isRunning = true

    val input: Channel<Long> = Channel(Channel.UNLIMITED)
    val output: Channel<Long> = Channel(Channel.UNLIMITED)

    fun setMemoryFieldValue(index: Long, value: Long) {
        intCodeProgram[index] = value
    }

    suspend fun runProgram() {
        while (isRunning) {
            doStep()
        }
        input.close()
        output.close()
    }



    private suspend fun doStep() {
        val opCode = intCodeProgram[currentIndex] % 100
        when (opCode.toInt()) {
            1 -> { // add
                intCodeProgram[getIndex(3)] = readParam(1) + readParam(2)
                currentIndex += 4
            }
            2 -> { //multiply
                intCodeProgram[getIndex(3)] = readParam(1) * readParam(2)
                currentIndex += 4
            }
            3 -> { //get input
                intCodeProgram[getIndex(1)] = input.receive()
                currentIndex += 2
            }
            4 -> { // output
                output.send(readParam(1))
                currentIndex += 2
            }
            5 -> { // jump if true
                if (intCodeProgram[getIndex(1)] != 0L)
                    currentIndex = readParam(2)
                else
                    currentIndex += 3
            }
            6 -> { // jump if false
                if (readParam(1) == 0L)
                    currentIndex = readParam(2)
                else
                    currentIndex += 3
            }
            7 -> { // less than
                intCodeProgram[getIndex(3)] = if (readParam(1) < readParam(2)) 1 else 0
                currentIndex += 4
            }
            8 -> { // equals
                intCodeProgram[getIndex(3)] = if (readParam(1) == readParam(2)) 1 else 0
                currentIndex += 4
            }
            9 -> { // Opcode 9 adjusts the relative base by the value of its only parameter. The relative base increases (or decreases, if the value is negative) by the value of the parameter.
                relativeBase += readParam(1)
                currentIndex += 2
            }
            99 -> { // halt
                isRunning = false
            }
            else -> throw Exception("Hee...")
        }
    }

    private fun getIndex(parameterNumber: Int): Long {
        val div = if (parameterNumber == 1) 100 else if (parameterNumber == 2) 1000 else 10000
        val paramType = (intCodeProgram[currentIndex] / div) % 10
        return when (paramType.toInt()) {
            0 -> intCodeProgram[currentIndex+parameterNumber]
            1 -> currentIndex+parameterNumber
            else -> +relativeBase+intCodeProgram[currentIndex+parameterNumber]
        }
    }

    private fun readParam(parameterNumber: Int) = intCodeProgram[getIndex(parameterNumber)]

    inner class MutableMapLongCodeProgram2(baseMap: Map<Long, Long>) {
        private val myMap = baseMap.toMutableMap()
        operator fun get(index: Long): Long {
            return myMap[index] ?: 0L
        }
        operator fun set(index: Long, value: Long) {
            myMap[index] = value
        }
    }
}

