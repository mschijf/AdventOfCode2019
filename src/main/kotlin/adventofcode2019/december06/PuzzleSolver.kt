package com.adventofcode2019.december06

import adventofcode2019.PuzzleSolverAbstract
import java.util.*

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val nameNodeMap = inputLines.map { it.split(")") }.flatten().distinct().associateWith { Node(it) }

    override fun resultPartOne(): String {
        inputLines.map { it.split(")") }.forEach { toFromDuo ->
            if (nameNodeMap[toFromDuo[1]]!!.pointsTo != null) {
                throw Exception("Assumption is wrong: one node can point to more nodes")
            }
            nameNodeMap[toFromDuo[1]]!!.addOrbit(nameNodeMap[toFromDuo[0]]!!)
        }
        return nameNodeMap.values.sumOf { it.numberOfOrbits() }.toString()
    }

    override fun resultPartTwo(): String {
        return solver().toString()
    }

    private fun solver(): Int {
        val queue: Queue<Node> = LinkedList()

        val alreadyVisitedMap = mutableMapOf<Node, Int>()
        alreadyVisitedMap[nameNodeMap["YOU"]!!] = 0
        queue.add(nameNodeMap["YOU"]!!)
        val santaNode = nameNodeMap["SAN"]!!
        while (queue.isNotEmpty()) {
            val currentNode = queue.remove()
            if (currentNode == santaNode) {
                return alreadyVisitedMap[currentNode]!!-2
            }
            currentNode.pointedFromList.filter { node -> !alreadyVisitedMap.contains(node) }.forEach { node ->
                alreadyVisitedMap[node] = alreadyVisitedMap[currentNode]!! + 1
                queue.add(node)
            }
            if (currentNode.pointsTo != null && !alreadyVisitedMap.contains(currentNode.pointsTo)) {
                val x = alreadyVisitedMap[currentNode]!! + 1
                alreadyVisitedMap[currentNode.pointsTo!!] = x
                queue.add(currentNode.pointsTo)
            }
        }
        return 0
    }
}

class Node(val name: String) {
    var pointsTo: Node? = null
    val pointedFromList = mutableListOf<Node>()

    fun addOrbit(pointsTo: Node) {
        this.pointsTo = pointsTo
        pointsTo.pointedFromList.add(this)
    }

    fun numberOfOrbits() : Int {
        var count = 0
        var nextNode = pointsTo
        while (nextNode != null) {
            count++
            nextNode = nextNode.pointsTo
        }
        return count
    }

    override fun hashCode() = name.hashCode()
    override fun equals(other: Any?) = if (other is Node) name == other.name else super.equals(other)
}


