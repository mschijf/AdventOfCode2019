package com.adventofcode2019.december13

import adventofcode2019.PuzzleSolverAbstract
import kotlin.math.sign

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val arcadeGame = ArcadeGame( inputLines.first() )
        arcadeGame.run()
        return arcadeGame.allTiles.count { it.type == 2 }.toString()
    }

    override fun resultPartTwo(): String {
        val arcadeGame = ArcadeGameTwo( inputLines.first() )
        arcadeGame.run()
        return arcadeGame.score.toString()
    }

}


class Tile (x: Int, y: Int, val type: Int) {
    val pos = Pos(x, y)
}

class ArcadeGame(input: String): IntCodeProgramIO {
    private val intCodeProgram = IntCodeProgram( input.split(",").map { it.toLong() }, this )

    val allTiles = mutableListOf<Tile>()

    fun run() {
        intCodeProgram.runProgram()
    }

    override fun read(): Long {
        return 0
    }

    private var triple = Array(3){0}
    private var outputCount = 0
    override fun write(output: Long) {
        triple[outputCount] = output.toInt()
        outputCount++
        if (outputCount == 3) {
            val tile = Tile(triple[0], triple[1], triple[2])
            allTiles.add(tile)
            outputCount = 0
        }
    }
}

class ArcadeGameTwo(input: String): IntCodeProgramIO {
    private val intCodeProgram = IntCodeProgram( input.split(",").map { it.toLong() }, this )
    private var paddlePos = Pos(0,0)
    private var ballPos = Pos(0,0)

    var score = 0
        private set

    fun run() {
        intCodeProgram.setMemoryFieldValue(0, 2)
        intCodeProgram.runProgram()
    }

    override fun read(): Long {
        return (ballPos.x - paddlePos.x).sign.toLong()
    }

    private var triple = Array(3){0}
    private var outputCount = 0
    override fun write(output: Long) {
        triple[outputCount] = output.toInt()
        outputCount++
        if (outputCount == 3) {
            val tile = Tile(triple[0], triple[1], triple[2])
            if (tile.pos.x == -1 && tile.pos.y == 0) {
                score = tile.type
            } else {
                when (tile.type) {
                    3 -> paddlePos = tile.pos
                    4 -> ballPos = tile.pos
                }
            }
            outputCount = 0
        }
    }
}

