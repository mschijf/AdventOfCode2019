package adventofcode2019

abstract class PuzzleSolverAbstract (
    val test: Boolean) {

    private val dayOfMonth = getDayOfMonthFromSubClassName()
    protected var inputLines = Input(test, dayOfMonth).inputLines
        private set
    private var overriddenInput = false

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
        print("Result part $puzzlePart: $result (after ${timePassed / 1000}.${timePassed % 1000} sec)")
        if (overriddenInput) println("<== Overridden input") else println()
    }

    private fun getDayOfMonthFromSubClassName(): Int {
        val className = this.javaClass.name
        val monthName = "december"
        val dayOfMonth = className.substringAfter(monthName).take(2)
        return dayOfMonth.toInt()
    }

    fun setAlternativeInputSourcePostfix(postFix: String) {
        overriddenInput = true
        inputLines = Input(test, dayOfMonth, postFix).inputLines
    }
    fun setDefaultInput() {
        overriddenInput = false
        inputLines = Input(test, dayOfMonth).inputLines
    }
}