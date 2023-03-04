package adventofcode2019.december22

import adventofcode2019.PuzzleSolverAbstract
import com.tool.math.gcd
import kotlin.math.absoluteValue

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {
    private val shuffleActionList = inputLines.map {actionLine -> ShuffleAction.from(actionLine) }

    override fun resultPartOne(): String {
        val cardDeckSize = if (test) 10 else 10007
        val cardDeck = Array(cardDeckSize) {i -> i}
        cardDeck.forEach { print("$it, ") }
        println()

        shuffleActionList.forEach { it.executeAction(cardDeck) }
        cardDeck.forEach { print("$it, ") }
        println()
        return cardDeck.indexOf(2019).toString()
    }

//    override fun resultPartTwo(): String {
//        val cardDeckSize = if (test) 10 else 119315717514047
//        val repSameList = shuffleActionList.map { it.repetitionTillSame(cardDeckSize)}
//        return repSameList.toString()
//    }
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

class DealWithIncrement(private val increment: Int): ShuffleAction() {
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

class Cut(private val cutNumber: Int): ShuffleAction() {
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

