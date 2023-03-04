package adventofcode2019.december22

import adventofcode2019.PuzzleSolverAbstract
import com.tool.math.gcd
import java.math.BigInteger
import kotlin.math.absoluteValue

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {
    private val shuffleActionList = inputLines.map {actionLine -> ShuffleAction.from(actionLine) }

    override fun resultPartOne(): String {
        val cardDeckSize = if (test) 10 else 10007
        val cardDeck = Array(cardDeckSize) {i -> i}
//        cardDeck.forEach { print("$it, ") }
//        println()

        shuffleActionList.forEach { it.executeAction(cardDeck) }

//        cardDeck.forEach { print("$it, ") }
//        println()
        return cardDeck.indexOf(2019).toString()
    }

    fun resultPartOneAlternative(): String {
        val cardDeckSize = if (test) 10 else 10007L
        var index = 2019L
        shuffleActionList.forEach {
            index = it.nextIndex(index, cardDeckSize)
        }
        return index.toString()
    }

    override fun resultPartTwo(): String {
        return modularArithmeticVersion(2020.toBigInteger()).toString()
    }

    companion object {
        val NUMBER_OF_CARDS = 119315717514047.toBigInteger()
        val SHUFFLES = 101741582076661.toBigInteger()
        val ZERO = 0.toBigInteger()
        val ONE = 1.toBigInteger()
        val TWO = 2.toBigInteger()
    }

    private fun String.getBigInteger(): BigInteger =
        this.split(" ").last().toBigInteger()

    private fun String.getLong(): Long =
        this.split(" ").last().toLong()

    // result: 49174686993380
    //
    // Mmm, no clue what's happening here.
    // It's clear, you have to do something with modular arithmetic, but this is a beyond my algebra knowledge
    // Nevertheless: interesting!! See also https://en.wikipedia.org/wiki/Modular_arithmetic#Properties
    // check the part where Mod_pow and mod_mul are explained. Alos check my algebra dictaat (RUL, university) about
    // "RestKlassen" and the calculation of a^b mod c.
    //
    private fun modularArithmeticVersion(find: BigInteger): BigInteger {
        val memory = arrayOf(ONE, ZERO)
        inputLines.reversed().forEach { instruction ->
            when {
                "cut" in instruction ->
                    memory[1] += instruction.getBigInteger()
                "increment" in instruction -> {
                    val increment = instruction.getBigInteger()
                    val modPower = increment.modPow(NUMBER_OF_CARDS - TWO, NUMBER_OF_CARDS)
                    memory[0] *= modPower
                    memory[1] *= modPower
                }
                "stack" in instruction -> {
                    memory[0] = -memory[0]
                    memory[1] = -(memory[1] + ONE)
                }
            }
            memory[0] %= NUMBER_OF_CARDS
            memory[1] %= NUMBER_OF_CARDS
        }
        val power = memory[0].modPow(SHUFFLES, NUMBER_OF_CARDS)
        return ((power * find) +
                ((memory[1] * (power + (NUMBER_OF_CARDS - ONE))) *
                        ((memory[0] - ONE).modPow(NUMBER_OF_CARDS - TWO, NUMBER_OF_CARDS))))
            .mod(NUMBER_OF_CARDS)
    }

}

abstract class ShuffleAction {
    companion object {
        fun from (actionLine: String): ShuffleAction {
            if (actionLine == "deal into new stack") {
                return DealIntoNewStack()
            } else if (actionLine.startsWith("cut ")) {
                return Cut(actionLine.substringAfter("cut ").toInt())
            } else if (actionLine.startsWith("deal with increment ")) {
                return DealWithIncrement(actionLine.substringAfter("deal with increment ").toInt())
            } else {
                throw Exception("Unexpected shuffle action")
            }
        }
    }

    abstract fun executeAction(cardDeck: Array<Int>)
    abstract fun repetitionTillSame(cardDeckSize: Long): Long
    abstract fun nextIndex(index: Long, cardDeckSize: Long): Long
}

class DealIntoNewStack: ShuffleAction() {
    override fun executeAction(cardDeck: Array<Int>) {
        cardDeck.reverse()
    }

    override fun nextIndex(index: Long, cardDeckSize: Long): Long {
        return (cardDeckSize - 1) - index
    }

    override fun repetitionTillSame(cardDeckSize: Long): Long {
        return 2
    }
}

class DealWithIncrement(val increment: Int): ShuffleAction() {
    override fun executeAction(cardDeck: Array<Int>) {
        var newPos = 0
        val tmp = cardDeck.copyOf()
        for (i in tmp.indices) {
            cardDeck[newPos] = tmp[i]
            newPos = (newPos + increment) % cardDeck.size
        }
    }

    override fun nextIndex(index: Long, cardDeckSize: Long): Long {
        return index*increment % cardDeckSize
    }

    override fun repetitionTillSame(cardDeckSize: Long): Long {
        return logModulo(increment.toLong(), cardDeckSize)
    }

    private fun logModulo(base: Long, mod:Long): Long {
        var power = 1L
        var x = base
        while (x != 1L) {
             x = base*x % mod
            power ++
        }
        return power
    }

}

class Cut(val cutNumber: Int): ShuffleAction() {
    override fun executeAction(cardDeck: Array<Int>) {
        if (cutNumber > 0) {
            val tmp = cardDeck.take(cutNumber)
            for (i in cutNumber until  cardDeck.size)
                cardDeck[i-cutNumber] = cardDeck[i]
            tmp.forEachIndexed {index, value -> cardDeck[cardDeck.size-cutNumber+index] = value}
        } else {
            val absCutNumber = cutNumber.absoluteValue
            val tmp = cardDeck.takeLast(absCutNumber)
            for (i in cardDeck.size-1  downTo absCutNumber)
                cardDeck[i] = cardDeck[i-absCutNumber]
            tmp.forEachIndexed {index, value -> cardDeck[index] = value}
        }
    }

    override fun nextIndex(index: Long, cardDeckSize: Long): Long {
        return (cardDeckSize + index - cutNumber) % cardDeckSize
    }

    override fun repetitionTillSame(cardDeckSize: Long): Long {
        val gcd = gcd(cardDeckSize, cutNumber.toLong())
        return cardDeckSize / gcd
    }

}

