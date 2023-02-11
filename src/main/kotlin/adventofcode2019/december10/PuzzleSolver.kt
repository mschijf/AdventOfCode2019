package com.adventofcode2019.december10

import adventofcode2019.PuzzleSolverAbstract
import com.tool.math.Fraction
import com.tool.math.XYCoordinate
import kotlin.math.absoluteValue

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {
    private val asteroidPosList = inputLines
        .mapIndexed{y, row ->
            row.toList().mapIndexedNotNull { x, ch -> if (ch == '#') XYCoordinate(x,y) else null } }
        .flatten()

    override fun resultPartOne(): String {
        return asteroidPosList
            .maxOf{thisPos -> canDetectFromPos(thisPos) }
            .toString()
    }

    override fun resultPartTwo(): String {
        val bestPos = asteroidPosList.maxBy{thisPos -> canDetectFromPos(thisPos)}
        val beamList = sortedBeamList(bestPos)
        val asteroidPos = beamList[199][0]
        return (100 * asteroidPos.x + asteroidPos.y).toString()
    }


    private fun canDetectFromPos(pos: XYCoordinate): Int {
        return asteroidPosList
            .filter{otherPos -> otherPos != pos}
            .map { otherPos -> pos.directionCoefficient(otherPos) }
            .toSet().size
    }

    private fun sortedBeamList(pos: XYCoordinate): List<List<XYCoordinate>> {
        val comp = BeamRaySortOrder()
        val beamMap = asteroidPosList
            .filter { otherPos -> otherPos != pos }
            .map { otherPos -> Pair(pos.directionCoefficient(otherPos), otherPos) }
            .groupBy( { it.first }, { it.second} )
            .toSortedMap(comp)
            .map { it.value.sortedBy { otherPos -> pos.manhattanDistance(otherPos) } }
        return beamMap
    }

}

class BeamRaySortOrder: Comparator<Fraction>{
    override fun compare(q1: Fraction?, q2: Fraction?): Int {
        return if (q1 == null || q2 == null || (q1 == q2)) {
            0
        } else if (beamRaySortValue(q1) < beamRaySortValue(q2))
            -1
        else
            1
    }

    private fun beamRaySortValue(quotient: Fraction): Double {
        val directionCoefficient = if (quotient.normalizedDenumerator == 0)
            quotient.normalizedNumerator.absoluteValue.toDouble() / 0.001
        else
            quotient.normalizedNumerator.absoluteValue.toDouble() / quotient.normalizedDenumerator.absoluteValue.toDouble()

        if (quotient.normalizedNumerator >= 0) {
            if (quotient.normalizedDenumerator < 0) {
                //quadrant upper right         dx > 0, dy < 0
                return -100_000_000 - 1/directionCoefficient
            } else {
                //quadrant lower right         dx > 0, dy >= 0
                return  -1_000_000 - directionCoefficient
            }
        } else {
            if (quotient.normalizedDenumerator > 0) {
                //quadrant lower left          dx <= 0, dy > 0
                return 1_000_000 - 1/directionCoefficient
            } else {
                //quadrant upper left          dx <= 0, dy <= 0
                return 100_000_000 - directionCoefficient
            }
        }
    }
}





