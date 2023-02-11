package adventofcode2019

import com.adventofcode2019.Input

abstract class PuzzleSolverAbstract (
    val test: Boolean,
    monthDay: Int? = null) {
    private val dayOfMonth = monthDay ?: getDayOfMonthFromSubClassName()
    protected val inputLines = Input(test, dayOfMonth).inputLines

    open fun resultPartOne(): String = "NOT IMPLEMENTED"
    open fun resultPartTwo(): String = "NOT IMPLEMENTED"

    fun showResult() {


        println("Day          : $dayOfMonth")
        println("Version      : ${if (test) "test" else "real"} input")
        println("Input lines  : ${if (inputLines.isEmpty()) "NO INPUT!!" else inputLines.count()} ")
        println("---------------------------------")

        printResult(1) { resultPartOne() }
        printResult(2) { resultPartTwo() }
    }

    private fun printResult(puzzlePart: Int, getResult: () -> String ) {
        val startTime = System.currentTimeMillis()
        val result = getResult()
        val timePassed = System.currentTimeMillis() - startTime
        println("Result part $puzzlePart: $result (after ${timePassed / 1000}.${timePassed % 1000} sec)")
    }

    private fun getDayOfMonthFromSubClassName(): Int {
        val className = this.javaClass.name
        val monthName = "december"
        val dayOfMonth = className.substringAfter(monthName).substringBefore(".")
        return dayOfMonth.toInt()
    }
}