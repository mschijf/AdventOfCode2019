package adventofcode2019.december18

import adventofcode2019.PuzzleSolverAbstract
import adventofcode2019.position.Coordinate
import java.util.*
import kotlin.math.min

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val maze = inputLines
        .mapIndexed{y, row -> row.mapIndexed {x, cell -> Coordinate(x,y) to cell}}
        .flatten()
        .toMap()
        .toMutableMap()
    private val entrance = maze.filterValues { it == '@' }.keys.first()

    private val doors = maze.filterValues { it.isUpperCase() }
    private val doorKeys = maze.filterValues { it.isLowerCase() }
    private val openSpaces = maze.filterValues { it != '#' }.map{it.key}.toSet()

    override fun resultPartOne(): String {
        maze.print()
        return solve1(entrance).toString()
    }

    private val cache = HashMap<Pair<Coordinate, Set<Char>>, Int>()
    private fun solve1(currentPos: Coordinate, distanceDone: Int = 0, bestSoFar: Int = 99999999, keysPicked :Set<Char> = emptySet()): Int {
        if (keysPicked.size == doorKeys.size) {
            return 0
        }

        val cacheKey = Pair(currentPos, keysPicked)
        val cacheValue = cache[cacheKey]?:-1
        if (cacheValue >= 0) {
            return cacheValue
        }

        val keysToCatch = determineKeysToCatchFrom2(currentPos, keysPicked)

//        val minDistanceToWalk = keysToCatch.maxOf{it.third}
//        val keyCount = keysToCatch.size
//        if (distanceDone+minDistanceToWalk+keyCount-1 >= bestSoFar) {
//            return 99999999
//        }
//
        var bestDistanceFromHere = 99999999
        for (key in keysToCatch) {
            val distanceFromHere = key.third + solve1(key.first, distanceDone+key.third, min(bestSoFar, distanceDone+bestDistanceFromHere), keysPicked+key.second)
            if (distanceFromHere < bestDistanceFromHere) {
                bestDistanceFromHere = distanceFromHere
            }
        }
        cache[cacheKey] = bestDistanceFromHere
        return bestDistanceFromHere
    }

    //
    // Onderstaande was mijn eerste poging. Conceptueel hetzelfde als de tweede, maar tweede was sneller
    // en daarmee was het wel haalbaar
    //
    private fun determineKeysToCatchFrom(aPos: Coordinate): List<Triple<Coordinate, Char, Int>> {
        val walkFields = maze.filterValues{ch -> ch in ".@abcdefghijklmnopqrstuvwxyz"}

        val result = mutableListOf<Triple<Coordinate, Char, Int>>()
        val queue = ArrayDeque<Pair<Coordinate, Int>>()

        val visited = mutableSetOf<Coordinate>()
        queue.add(Pair(aPos, 0))
        while (queue.isNotEmpty()) {
            val currentPos = queue.pop()
            visited.add(currentPos.first)
            val posList = currentPos.first.neighbors().intersect(walkFields.keys.toSet())
            (posList - visited).forEach {
                if (walkFields[it]!! == '.' || walkFields[it]!! == '@')
                    queue.add(Pair(it, currentPos.second + 1))
                else
                    result.add(Triple(it, walkFields[it]!!, currentPos.second + 1))
            }
        }
        return result
    }

    private fun determineKeysToCatchFrom2(aPos: Coordinate, keysPicked:Set<Char>): List<Triple<Coordinate, Char, Int>> {
        val result = mutableListOf<Triple<Coordinate, Char, Int>>()
        val queue = ArrayDeque<Pair<Coordinate, Int>>()

        val visited = mutableSetOf<Coordinate>()
        queue.add(Pair(aPos, 0))
        while (queue.isNotEmpty()) {
            val currentPos = queue.pop()
            visited.add(currentPos.first)
            val posList = currentPos.first.neighbors().filter { it in openSpaces }.filterNot { it in visited }
            posList.forEach {
                val door = doors[it]
                val key = doorKeys[it]
                if (door == null || door.lowercaseChar() in keysPicked) {
                    if (key != null && key !in keysPicked) {
                        result.add(Triple(it, key, currentPos.second + 1))
                    } else {
                        queue.add(Pair(it, currentPos.second + 1))
                    }
                }
            }
        }
        return result
    }


    private fun <T> Map<Coordinate, T>.print() {
        val maxX = this.keys.maxBy { it.x }.x
        val maxY = this.keys.maxBy { it.y }.y

        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                print(this.getOrDefault(Coordinate(x, y), ' '))
            }
            println()
        }
    }

}


