package adventofcode2019.december16

import adventofcode2019.PuzzleSolverAbstract
import kotlin.math.absoluteValue

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val basePattern = listOf(0, 1, 0, -1)

    override fun resultPartOne(): String {
        var signal = inputLines.first().map { ch -> ch - '0' }
        repeat(100) {
            signal = signal.fft()
        }
        return signal.take(8).joinToString("")
    }

    // start with analyze:
    // For i >= totalSize /2 the following goes up:
    //         on Xi, the number that we search will be equal to 1*Xi+1 + 1*Xi+2 + ... + 1*Xn
    //         (since we will have i-1 0's and then 1 1's)
    // instead of calculating each number as the sum of all numbers from Xi, we can also start cumulating from the back
    // and that is exactly what fft_sum is doing
    //
    // finally I checked the offset in the input (5_978_017) which is bigger than half of 650*10_000 = 6_500_000
    //

    override fun resultPartTwo(): String {
        val startString = inputLines.first()
        val msgOffset = startString.take(7).toInt()
        val totalSize = startString.length
        var signal = inputLines.first().repeat(10000).takeLast(totalSize * 10000 - msgOffset).map { ch -> ch - '0' }

        repeat(100) {
            signal = signal.fftSum()
        }

        return signal.take(8).joinToString("")
    }

    private fun List<Int>.fft(): List<Int> {
        return this.mapIndexed{ repeating, _ -> this.determineOneNumber(repeating) }
    }

    private fun List<Int>.fftSum(): List<Int> {
        val mut = MutableList(this.size){0}
        mut[mut.size-1] = this[mut.size-1]
        for (i in mut.size-2 downTo 0) {
            mut[i] = (mut[i+1] + this[i]) % 10
        }
        return mut
    }

    private fun List<Int>.determineOneNumber(repeatingFactor: Int): Int {
        return this
            .mapIndexed { index, v -> v * patternFactor(repeatingFactor+1, index) }
            .sum()
            .absoluteValue % 10
    }

    private fun patternFactor(repeat: Int, index: Int): Int {
        if (index < basePattern.size * repeat - 1) {
            return basePattern[ (index + 1) / repeat]
        } else {
            val offset = (index - (basePattern.size * repeat - 1)) % (basePattern.size * repeat)
            return basePattern[ offset / repeat]
        }
    }
 }


