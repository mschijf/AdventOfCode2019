package adventofcode2019.december22Alternative

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {
    private val cardDeckSize = if (test) 10 else 10007L

    override fun resultPartOne(): Any {
        return indexOf(2019L)
    }

    private fun indexOf(value: Long): Long {
        return inputLines.fold(value) { index, instruction ->
            when {
                "cut" in instruction -> index.nextIndexAfterCut(instruction.getNumber())
                "increment" in instruction -> index.nextIndexAfterIncrement(instruction.getNumber())
                "new stack" in instruction -> index.nextIndexAfterNewStack()
                else -> throw IllegalArgumentException("unexpected $instruction")
            }
        }
    }

    private fun String.getNumber(): Long = this.substringAfterLast(" ").toLong()

    private fun Long.nextIndexAfterCut(cutNumber: Long)= (cardDeckSize + this - cutNumber) % cardDeckSize
    private fun Long.nextIndexAfterIncrement(increment: Long) = this*increment % cardDeckSize
    private fun Long.nextIndexAfterNewStack() = (cardDeckSize - 1) - this
}
