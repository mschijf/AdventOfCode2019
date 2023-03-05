package adventofcode2019.december24

import adventofcode2019.PuzzleSolverAbstract
import tool.position.Coordinate
import kotlin.math.max

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {
    private val grid =
        inputLines.mapIndexed {rowIndex, row ->
            row.mapIndexed { colIndex, ch -> Coordinate(colIndex, rowIndex) to ch }
        }.flatten().toMap().filterValues { ch -> ch == '#' }.keys

    override fun resultPartOne(): Any {
        var newGrid = grid
        var bdr = newGrid.toBiodiversityRating()
        val biodiversityRatingSet = mutableSetOf<Int>()
        while (bdr !in biodiversityRatingSet) {
            biodiversityRatingSet.add(bdr)
            newGrid = newGrid.nextGen()
            bdr = newGrid.toBiodiversityRating()
        }
        return bdr
    }

    override fun resultPartTwo(): Any {
        var levelGridMap = mapOf(0 to grid)
        repeat(if (test) 10 else 200) {
            levelGridMap = levelGridMap.nextGen()
        }
        return levelGridMap.values.sumOf { it.size }
    }

    private fun Set<Coordinate>.nextGen(): Set<Coordinate> {
        val stayAlive = this.filter{ livingBug -> livingBug.livingNeighbors(this) == 1}.toSet()
        val newLive = (0 until GRID_WIDTH* GRID_WIDTH).map{it.toCoordinate()}
            .filter {it !in this}
            .filter { it.livingNeighbors(this) in 1..2}
            .toSet()
        return stayAlive + newLive
    }

    private fun Coordinate.livingNeighbors(currentLivingPopulation: Set<Coordinate>): Int {
        return this.neighbors()
            .filterInsideGrid()
            .count { neighbor -> neighbor in currentLivingPopulation }
    }

    private fun Map<Int, Set<Coordinate>>.nextGen(): Map<Int, Set<Coordinate>> {
        val result = mutableMapOf<Int, Set<Coordinate>>()
        val minLevel = this.keys.min()-1
        val maxLevel = this.keys.max()+1
        for (level in minLevel..maxLevel) {
            val levelSet = this[level]?: emptySet()
            val stayAlive = levelSet.filterInsideGrid().filter{ livingBug -> this.countLivingNeighbors(level, livingBug) == 1}.toSet()
            val newLive = (0 until GRID_WIDTH* GRID_WIDTH).map{it.toCoordinate()}
                .filter {it !in levelSet}
                .filter { emptyTile -> this.countLivingNeighbors(level, emptyTile) in 1..2}
                .toSet()
            result[level] = stayAlive + newLive
        }
        return result
    }

    private fun Map<Int, Set<Coordinate>>.countLivingNeighbors(level: Int, coordinate: Coordinate): Int {
        if (coordinate.normalField()) {
            return coordinate.neighbors().count { neighbor -> this.isLivingTile(level, neighbor) }
        } else if (coordinate.innerRing()) { // inner ring
            val inLevel = coordinate.neighbors().count { neighbor -> this.isLivingTile(level, neighbor) }
            val inLevelPlus1 = outToInMap[coordinate.toGridPosition()]!!.count { levelNeighbor -> this.isLivingTile(level+1, levelNeighbor.toCoordinate()) }
            return inLevel + inLevelPlus1
        } else if (coordinate.centerField()) {
            return -1
        } else { // outer ring
            val inLevel = coordinate.neighbors().count { neighbor -> this.isLivingTile(level, neighbor) }
            val inLevelMin1 = inToOutMap[coordinate.toGridPosition()]!!.count { levelNeighbor -> this.isLivingTile(level-1, levelNeighbor.toCoordinate()) }
            return inLevel + inLevelMin1
        }
    }

    private fun Map<Int, Set<Coordinate>>.isLivingTile(level: Int, coordinate:Coordinate): Boolean {
        return this[level]?.contains(coordinate) ?: false
    }

    private fun Coordinate.normalField() = this.toGridPosition() in normalIndexes
    private fun Coordinate.innerRing() = this.toGridPosition() in innerRingIndexes
    private fun Coordinate.centerField() = this.toGridPosition() == centerFieldIndex

    private fun Collection<Coordinate>.filterInsideGrid(): List<Coordinate> = this.filter{it.inGrid()}
    private fun Coordinate.inGrid() = this.toGridPosition() in GRID_RANGE
    private fun Coordinate.toGridPosition() = GRID_WIDTH*this.y + this.x
    private fun Set<Coordinate>.toBiodiversityRating() = this.sumOf{1 shl (it.toGridPosition())}
    private fun Int.toCoordinate() = Coordinate(this % GRID_WIDTH, this/GRID_WIDTH)

    fun Set<Coordinate>.print() {
        val maxX = max(GRID_WIDTH-1, this.maxOf {it.x})
        val maxY = max(GRID_WIDTH-1, this.maxOf {it.y})

        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                print(if (Coordinate(x,y) in this) '#' else '.')
            }
            println()
        }
    }

    companion object {
        const val GRID_WIDTH = 5
        val GRID_RANGE = 0 until GRID_WIDTH * GRID_WIDTH

        val outToInMap = mapOf(
            7 to setOf(0,1,2,3,4), 11 to setOf(0,5,10,15,20), 13 to setOf(4,9,14,19,24), 17 to setOf(20,21,22,23,24))

        val inToOutMap = mapOf(
            0 to setOf(7,11), 1 to setOf(7), 2 to setOf(7), 3 to setOf(7), 4 to setOf(7, 13),
            5 to setOf(11),10 to setOf(11),15 to setOf(11),20 to setOf(11, 17),
            9 to setOf(13), 14 to setOf(13), 19 to setOf(13), 24 to setOf(13, 17),
            21 to setOf(17), 22 to setOf(17), 23 to setOf(17)
        )

        val normalIndexes = listOf(6, 8, 16, 18)
        val innerRingIndexes = listOf(7,11, 13, 17)
        val centerFieldIndex = 12

    }
}


