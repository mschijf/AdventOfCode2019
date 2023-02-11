package com.adventofcode2019.december11

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val baseIntCodeProgram = inputLines.first().split(",").map{it.toLong()}

    override fun resultPartOne(): String {
        val boostProgram = IntCodeProgram(baseIntCodeProgram)
        var currentPos = Pos(0,0)
        var direction = Direction.UP
        val panelSet = mutableSetOf<Pos>()
        val whitePanelSet = mutableSetOf<Pos>()
        while (!boostProgram.isFinished) {
            val currentPanelColor = if(currentPos in whitePanelSet) 1L else 0L
            val outputColor = boostProgram.runProgram(currentPanelColor)
            if (outputColor == 0L) { //black
                whitePanelSet.remove(currentPos)
            } else { //white
                whitePanelSet.add(currentPos)
            }
            val outputDirection = boostProgram.runProgram()

            panelSet.add(currentPos)

            direction = if (outputDirection == 0L) direction.rotateLeft() else direction.rotateRight()
            currentPos = currentPos.moveOneStep(direction)
        }
        return panelSet.size.toString()
    }

    override fun resultPartTwo(): String {
        val boostProgram = IntCodeProgram(baseIntCodeProgram)
        var currentPos = Pos(0,0)
        var direction = Direction.UP
        val panelSet = mutableSetOf<Pos>()
        val whitePanelSet = mutableSetOf(currentPos)
        while (!boostProgram.isFinished) {
            val currentPanelColor = if(currentPos in whitePanelSet) 1L else 0L
            val outputColor = boostProgram.runProgram(currentPanelColor)
            if (outputColor == 0L) { //black
                whitePanelSet.remove(currentPos)
            } else { //white
                whitePanelSet.add(currentPos)
            }
            val outputDirection = boostProgram.runProgram()

            panelSet.add(currentPos)

            direction = if (outputDirection == 0L) direction.rotateLeft() else direction.rotateRight()
            currentPos = currentPos.moveOneStep(direction)
        }

        printPanels(panelSet, whitePanelSet)

        return panelSet.size.toString()
    }

    private fun printPanels(panelSet: Set<Pos>, whitePanelSet: Set<Pos> ) {
        val minX = panelSet.minOf { it.x }
        val maxX = panelSet.maxOf { it.x }
        val minY = panelSet.minOf { it.y }
        val maxY = panelSet.maxOf { it.y }

        for (y in maxY downTo minY) {
            for (x in minX .. maxX) {
                if (Pos(x, y) in whitePanelSet)
                    print("##")
                else
                    print("..")
            }
            println()
        }
    }

}

