package com.adventofcode2019.december14

import adventofcode2019.PuzzleSolverAbstract
import kotlin.math.sign

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val reactionList = inputLines.map { it.toReaction() } + listOf(Reaction("1 ORE"))
        val reactionMap = reactionList.associateBy { it.right.second }
        return calculate(reactionMap, 1L).toString()
    }

    override fun resultPartTwo(): String {
        val reactionList = inputLines.map { it.toReaction() } + listOf(Reaction("1 ORE"))
        val reactionMap = reactionList.associateBy { it.right.second }

//        val trillion = 1_000_000_000_000L
//        val fuelNeededMax = (1L..1_000_000_000_000L).binarySearchBy { fuelProduced -> trillion.compareTo(calculate(reactionMap,fuelProduced )) }
        var fuelNeededMin = 1L
        var fuelNeededMax = 1_000_000_000_000L
        while (fuelNeededMin <= fuelNeededMax) {
            val fuelProduced = (fuelNeededMin + fuelNeededMax) / 2
            val oreNeeded = calculate(reactionMap, fuelProduced)
            if (oreNeeded <  1_000_000_000_000) {
                fuelNeededMin = fuelProduced + 1
            } else if (oreNeeded >  1_000_000_000_000) {
                fuelNeededMax = fuelProduced - 1
            } else {
                break
            }
        }
        return fuelNeededMax.toString()
    }

    private fun calculate(reactionMap: Map<String, Reaction>, fuelNeeded: Long): Long {
        val neededList = reactionMap.values.associate {it.right.second to 0L }.toMutableDefaultValueMap(0L)
        val producedList = reactionMap.values.associate {it.right.second to 0L }.toMutableDefaultValueMap(0L)

        neededList["FUEL"] = fuelNeeded
        while (producedList.keys.any { key -> producedList[key] < neededList[key] }) {
            val produceChemicalName = producedList.keys.first { key -> producedList[key] < neededList[key] }
            val missingAmount = neededList[produceChemicalName] - producedList[produceChemicalName]
            val reaction = reactionMap[produceChemicalName]!!
            val executeTimesReaction = (missingAmount / reaction.right.first) + if (missingAmount % reaction.right.first > 0L) 1L else 0L
            for (chemicalNeeded in reaction.left) {
                neededList[chemicalNeeded.second] += (chemicalNeeded.first * executeTimesReaction)
            }
            producedList[produceChemicalName] += executeTimesReaction * reaction.right.first
        }
        return neededList["ORE"]
    }


    private fun String.toReaction() = Reaction(this)
    private fun Map<String,Long>.toMutableDefaultValueMap(default: Long) = MutableDefaultValueMap<String,Long>(this, default = default)

    private fun LongRange.binarySearchBy(fn: (Long) -> Int): Long {
        var low = this.first
        var high = this.last
        while (low <= high) {
            val mid = (low + high) / 2
            when (fn(mid).sign) {
                -1 -> high = mid - 1
                1 -> low = mid + 1
                0 -> return mid // Exact match
            }
        }
        return low - 1 // Our next best guess
    }
}


class Reaction(inputString: String) {
    val left =
        if (inputString.contains("=>"))
            inputString.substringBefore(" => ")
                .split(", ")
                .map{ Pair(it.substringBefore(" ").toInt(), it.substringAfter(" ").trim())}
        else
            emptyList()

    val right = Pair(
        inputString.substringAfter("=> ").substringBefore(" ").toInt(),
        inputString.substringAfter("=> ").substringAfter(" "))
}


//---------------------------------------------------------------------------------------------------------------------


class MutableDefaultValueMap<K, V>(baseMap: Map<K, V>, private val default: V): HashMap<K,V>(baseMap) {
    override operator fun get(key: K): V {
        return super.get(key) ?: default
    }
    operator fun set(key: K, value: V) {
        super.put(key, value)
    }
}

