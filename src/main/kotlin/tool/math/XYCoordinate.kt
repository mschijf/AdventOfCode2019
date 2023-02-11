package com.tool.math

import kotlin.math.absoluteValue

data class XYCoordinate(val x: Int, val y: Int) {
    fun directionCoefficient(otherPos: XYCoordinate) = Fraction(otherPos.x - x,otherPos.y - y)
    fun manhattanDistance(otherPos: XYCoordinate) = (otherPos.x - x).absoluteValue + (otherPos.y - y).absoluteValue
}