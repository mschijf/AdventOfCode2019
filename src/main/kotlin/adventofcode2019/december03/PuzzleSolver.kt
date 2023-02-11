package com.adventofcode2019.december03

import adventofcode2019.PuzzleSolverAbstract
import kotlin.math.absoluteValue

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val wireList = inputLines.map { Wire(it) }
        val intersectionPointSet = wireList[0].getIntersectionPoints(wireList[1])
        return intersectionPointSet.minOf { it.manhattanDistance(Pos(0,0) ) }.toString()
    }

    override fun resultPartTwo(): String {
        val wireList = inputLines.map { Wire(it) }
        val intersectionPointSet = wireList[0].getIntersectionPoints(wireList[1])
        val distanceList = intersectionPointSet.map{wireList[0].getDistanceToPoint(it) + wireList[1].getDistanceToPoint(it)}
        return distanceList.min().toString()
    }

}

class Wire(input: String) {
    private val lineList = mapToLines(input.split(","))

    private fun mapToLines(wireList: List<String>): List<Line> {
        val result = mutableListOf<Line>()
        var startPos = Pos(0,0)
        for (wireStr in wireList) {
            result.add(toLine(startPos, wireStr))
            startPos = result.last().secondPoint
        }
        return result
    }

    private fun toLine(startPos: Pos, wireDirectionAndLength: String): Line {
        val wireLength = wireDirectionAndLength.substring(1).toInt()
        val endPos = when (wireDirectionAndLength[0]) {
            'D' -> Pos(startPos.x, startPos.y-wireLength)
            'U' -> Pos(startPos.x, startPos.y+wireLength)
            'L' -> Pos(startPos.x-wireLength, startPos.y)
            'R' -> Pos(startPos.x+wireLength, startPos.y)
            else -> throw Exception("Unexpected direction")
        }
        return Line(startPos, endPos)
    }

    fun getIntersectionPoints(otherWire: Wire): Set<Pos> {
        val intersectionPointSet = mutableSetOf<Pos>()
        lineList.forEach { thisLine ->
            otherWire.lineList.forEach { otherLine ->
                val isp = thisLine.intersectionPoint(otherLine)
                if (isp != null) {
                    if (isp == Pos(0,0))
                        println("")
                    intersectionPointSet.add(isp)
                }
            }
        }
        return intersectionPointSet
    }

    fun getDistanceToPoint(point: Pos): Int {
        var totalLength = 0
        lineList.forEach { linePiece ->
            if (linePiece.contains(point)) {
                totalLength += linePiece.distanceToPoint(point)
                return totalLength
            } else {
                totalLength += linePiece.length
            }
        }
        return -1
    }
}

data class Line(val firstPoint: Pos, val secondPoint: Pos) {
    private val start = if (firstPoint.x < secondPoint.x || firstPoint.y < secondPoint.y) firstPoint else secondPoint
    private val end = if (firstPoint.x < secondPoint.x || firstPoint.y < secondPoint.y) secondPoint else firstPoint

    val length = start.manhattanDistance(end)

    private fun isHorizontal() = start.y == end.y && start.x != end.x
    private fun isVertical() = start.x == end.x && start.y != end.y

    fun intersectionPoint(other: Line): Pos? {
        if (this.isHorizontal() && other.isVertical()) {
            if (other.start.x in (this.start.x+1 .. this.end.x-1) && this.start.y in (other.start.y+1 .. other.end.y-1))
                return Pos(other.start.x, this.start.y)
        } else if (this.isVertical() && other.isHorizontal()) {
            if (other.start.y in (this.start.y+1 .. this.end.y-1) && this.start.x in (other.start.x+1 .. other.end.x-1))
                return Pos(this.start.x, other.start.y)
        } else {
            //ignore, both vertical or horizontal. They still can hove aoverlap and therefore a lot of intersectionpoints
            //but we safely assume that that cannot happen in this puzzle
        }
        return null
    }

    fun contains(aPoint: Pos): Boolean {
        if (isVertical() && aPoint.x == start.x)
            return aPoint.y in start.y+1..end.y-1
        if (isHorizontal() && aPoint.y == start.y)
            return aPoint.x in start.x+1..end.x-1
        return false
    }

    fun distanceToPoint(aPoint: Pos): Int {
        return firstPoint.manhattanDistance(aPoint)
    }
}

data class Pos(val x: Int, val y: Int) {
    fun manhattanDistance(otherPos: Pos) = (x-otherPos.x).absoluteValue + (y-otherPos.y).absoluteValue
}


