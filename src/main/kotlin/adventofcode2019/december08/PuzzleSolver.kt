package com.adventofcode2019.december08

import adventofcode2019.PuzzleSolverAbstract

fun main() {
    PuzzleSolver(test=false).showResult()
}

class PuzzleSolver(test: Boolean) : PuzzleSolverAbstract(test) {

    private val wide = if (test) 2 else 25
    private val tall = if (test) 2 else 6

    override fun resultPartOne(): String {
        val layerList = inputLines.first().chunked(wide*tall).map{Layer(it, wide, tall)}
        val minLayer = layerList.minBy { it.zeroCount }
        return (minLayer.oneCount * minLayer.twoCount).toString()
    }

    override fun resultPartTwo(): String {
        val layerList = inputLines.first().chunked(wide*tall).map{Layer(it, wide, tall)}
        val resultLayer = layerList.reduce { acc, layer -> acc.onTopOf(layer)}
        resultLayer.print()
        return ""
    }
}

class Layer(val input: String, private val wide: Int, private val tall: Int) {
    val zeroCount = input.count { it == '0' }
    val oneCount = input.count { it == '1' }
    val twoCount = input.count { it == '2' }

    fun onTopOf(lowerLayer: Layer): Layer {
        return Layer(input.mapIndexed{index, s -> if (s=='2') lowerLayer.input[index] else s}.joinToString(""), wide, tall)
    }

    fun print() {
        input.chunked(wide).map{line -> line.map{if (it == '0') ".." else "##"}.joinToString("")}.forEach { println(it) }
    }

}


