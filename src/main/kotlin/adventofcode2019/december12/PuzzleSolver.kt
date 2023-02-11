package com.adventofcode2019.december12

import adventofcode2019.PuzzleSolverAbstract
import com.tool.math.lcm
import kotlin.math.absoluteValue


fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val moonList = inputLines.map{ Moon(it) }
        val iterations = if (test) 10 else 1000
        repeat(iterations) {
            doOneStep(moonList)
//            println("after ${it+1} step(s)")
//            moonList.forEach { moon -> println(moon) }
        }

        return moonList.sumOf { moon -> moon.totalEnergy() }.toString()
    }

    override fun resultPartTwo(): String {
        val moonList = inputLines.map{ Moon(it) }
        var iteration = 0
        val repeatCycle = MutableList(3){ 999_999_999L }
        while (repeatCycle.any{ it >= 999_999_999L} ) {
            doOneStep(moonList)
            iteration++
            if (repeatCycle[0] >= 999_999_999L && moonList.all { moon -> moon.equalsBeginXPos() })
                repeatCycle[0] = iteration.toLong()
            if (repeatCycle[1] >= 999_999_999L && moonList.all { moon -> moon.equalsBeginYPos() })
                repeatCycle[1] = iteration.toLong()
            if (repeatCycle[2] >= 999_999_999L && moonList.all { moon -> moon.equalsBeginZPos() })
                repeatCycle[2] = iteration.toLong()


            if (iteration % 100_000_000 == 0) {
                println (iteration)
            }
        }

        return repeatCycle.reduce { acc, l -> lcm(acc, l) }.toString()
    }

    private fun doOneStep(moonList: List<Moon>) {
        moonList.forEach { moon1 ->
            moonList.forEach { moon2 ->
                if (moon1 != moon2) {
                    moon1.applyGravity(moon2)
                }
            }
        }
        moonList.forEach { moon -> moon.applyVelocity() }
    }

}

class Moon(inputString: String) {
    private var pos = XYZTriple(
        x = inputString.substringAfter("x=").substringBefore(",").trim().toInt(),
        y = inputString.substringAfter("y=").substringBefore(",").trim().toInt(),
        z = inputString.substringAfter("z=").substringBefore(">").trim().toInt())
    private var velocity = XYZTriple(0,0,0)

    private val startPos = pos
    private val startVelocity = velocity

    fun applyGravity(otherPlanet: Moon) {
        val dx = if (pos.x < otherPlanet.pos.x) +1 else if (pos.x > otherPlanet.pos.x) -1 else 0
        val dy = if (pos.y < otherPlanet.pos.y) +1 else if (pos.y > otherPlanet.pos.y) -1 else 0
        val dz = if (pos.z < otherPlanet.pos.z) +1 else if (pos.z > otherPlanet.pos.z) -1 else 0
        velocity = XYZTriple(velocity.x+dx, velocity.y+dy, velocity.z+dz)
    }

    fun applyVelocity() {
        pos = XYZTriple(pos.x+velocity.x, pos.y+velocity.y, pos.z+velocity.z)
    }

    private fun potentialEnergy() = pos.x.absoluteValue + pos.y.absoluteValue + pos.z.absoluteValue
    private fun kineticEnergy() = velocity.x.absoluteValue + velocity.y.absoluteValue + velocity.z.absoluteValue
    fun totalEnergy() = potentialEnergy() * kineticEnergy()

    override fun toString() = "pos = $pos, vel = $velocity"

    fun equalsBeginXPos() = (startPos.x == pos.x) && (startVelocity.x == velocity.x)
    fun equalsBeginYPos() = (startPos.y == pos.y) && (startVelocity.y == velocity.y)
    fun equalsBeginZPos() = (startPos.z == pos.z) && (startVelocity.z == velocity.z)
}

data class XYZTriple(val x: Int, val y: Int, val z: Int)

