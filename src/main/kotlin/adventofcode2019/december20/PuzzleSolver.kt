package adventofcode2019.december20

import adventofcode2019.PuzzleSolverAbstract
import tool.position.Coordinate

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val maze = Maze.from(inputLines)

    override fun resultPartOne(): String {
        return maze.shortestPath().toString()
    }

    override fun resultPartTwo(): String {
        return maze.shortestPathLeveled().toString()
    }
}

class Maze(
    private val passageMap: Map<Coordinate, Set<Coordinate>>,
    private val beamPortMap: Map<String, Pair<Coordinate, Coordinate>>,
    private val start: Coordinate,
    private val end: Coordinate) {

    private val mazeMap = findAllNeighbors(passageMap, beamPortMap)

    private val minX = mazeMap.keys.minOf { it.x }
    private val maxX = mazeMap.keys.maxOf { it.x }
    private val minY = mazeMap.keys.minOf { it.y }
    private val maxY= mazeMap.keys.maxOf { it.y }
    private fun outsideBorder(c: Coordinate) = (c.x == minX || c.x == maxX || c.y == minY || c.y == maxY)

    private fun findAllNeighbors(passages: Map<Coordinate, Set<Coordinate>>, beamPorts: Map<String, Pair<Coordinate, Coordinate>>): Map<Coordinate, Set<Coordinate>> {
        val beamPortMap = (beamPorts.values.map { it.first to it.second } + beamPorts.values.map { it.second to it.first }).toMap()
        return passages.keys.associateWith{ passages[it]!! + if (beamPortMap[it] != null) listOf(beamPortMap[it]!!) else emptyList() }
    }

    fun shortestPath(): Int {
        val visited = mutableSetOf<Coordinate>()
        val queue = ArrayDeque<Pair<Coordinate, Int>>()
        queue.add(Pair(start, 0))
        while (queue.isNotEmpty()) {
            val (currentPos, stepsDone) = queue.removeFirst()
            if (currentPos == end) {
                return stepsDone
            }
            visited += currentPos
            mazeMap[currentPos]!!
                .filter {it !in visited}
                .forEach {
                    queue.add(Pair(it, stepsDone+1))
                }
        }
        return -1
    }

    fun shortestPathLeveled(): Int {
        val visited = mutableSetOf<Pair<Int,Coordinate>>()
        val queue = ArrayDeque<Triple<Int, Coordinate, Int>>()
        queue.add(Triple(0, start, 0))
        while (queue.isNotEmpty()) {
            val (level, currentPos, stepsDone) = queue.removeFirst()
            if (level == 0 && currentPos == end) {
                return stepsDone
            }
            visited += Pair(level, currentPos)
            mazeMap[currentPos]!!
                .forEach {
                    val newLevel = level+levelDelta(currentPos, it)
                    if (newLevel >= 0 && newLevel <= beamPortMap.size && Pair(newLevel, it) !in visited)
                        queue.add(Triple(newLevel, it, stepsDone+1))
                }
        }
        return -1
    }

    private fun levelDelta(from: Coordinate, to: Coordinate): Int {
        if (from.manhattanDistance(to) == 1) //regular passage
            return 0
        if (outsideBorder(to))       //from->to goes from inside to out
            return 1
        return -1                    //from->to goes from outside to in
    }


    companion object {
        fun from (inputLines: List<String>): Maze {
            val passagesSet = findPassages(inputLines).toSet()
            val entranceMap = findEntrancePorts(inputLines)
            val passageMap = passagesSet.associateWith{ (it.neighbors() intersect passagesSet) }
            val beamPortMap = entranceMap.filterValues { it.size == 2 }.map{it.key to Pair(it.value[0], it.value[1])}.toMap()
            return Maze(passageMap, beamPortMap, entranceMap["AA"]!!.first(), entranceMap["ZZ"]!!.first() )
        }

        private fun findPassages(inputLines: List<String>):List<Coordinate> {
            return inputLines
                .mapIndexed { y, row -> row
                    .mapIndexed { x, value -> if (value == '.') Coordinate(x,y) else null }
                    .filterNotNull() }
                .flatten()
        }

        private fun findEntrancePorts(inputLines: List<String>) : Map<String, List<Coordinate>> {
            val beamNameMap = mutableMapOf<String, MutableList<Coordinate>>()
            for (y in inputLines.indices) {
                for (x in inputLines[y].indices) {
                    if (inputLines[y][x].isUpperCase()) {
                        if (y+1 < inputLines.size && x < inputLines[y+1].length && inputLines[y+1][x].isUpperCase()) {
                            if (y+2 < inputLines.size && x < inputLines[y+2].length && inputLines[y+2][x] == '.') {
                                beamNameMap.addBeamPort("${inputLines[y][x]}${inputLines[y+1][x]}", Coordinate(x, y+2))
                            } else {
                                beamNameMap.addBeamPort("${inputLines[y][x]}${inputLines[y+1][x]}", Coordinate(x, y-1))
                            }
                        } else if (x+1 < inputLines[y].length && inputLines[y][x+1].isUpperCase()) {
                            if (x+2 < inputLines[y].length && inputLines[y][x+2] == '.') {
                                beamNameMap.addBeamPort("${inputLines[y][x]}${inputLines[y][x+1]}", Coordinate(x+2, y))
                            } else {
                                beamNameMap.addBeamPort("${inputLines[y][x]}${inputLines[y][x+1]}", Coordinate(x-1, y))
                            }
                        } else {
                            //ignore
                        }
                    }
                }
            }
            return beamNameMap
        }

        private fun MutableMap<String, MutableList<Coordinate>>.addBeamPort(name:String, coord: Coordinate) {
            if (name !in this)
                this[name] = mutableListOf()
            this[name]!!.add(coord)
        }

    }
}


