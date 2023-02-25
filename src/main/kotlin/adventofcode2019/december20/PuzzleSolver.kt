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
}

class Maze(
    private val mazeMap: Map<Coordinate, Set<Coordinate>>,
    private val start: Coordinate,
    private val end: Coordinate) {

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

    companion object {
        fun from (inputLines: List<String>): Maze {
            val passages = findPassages(inputLines)
            val entranceMap = findEntrancePorts(inputLines)
            val neighborMap = findNeighbors(passages, entranceMap.filterValues { it.size == 2 })
            return Maze(neighborMap, entranceMap["AA"]!!.first(), entranceMap["ZZ"]!!.first() )
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

        private fun findNeighbors(passages: List<Coordinate>, entranceMap: Map<String, List<Coordinate>>): Map<Coordinate, Set<Coordinate>> {
            val beamPortMap = (entranceMap.values.map { it[0] to it[1] } + entranceMap.values.map { it[1] to it[0] }).toMap()
            return passages.associateWith{ (it.neighbors() intersect passages.toSet()) + if (beamPortMap[it] != null) listOf(beamPortMap[it]!!) else emptyList() }
        }

    }
}


