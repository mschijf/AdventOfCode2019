package adventofcode2019

import java.io.File

class Input(test: Boolean, dayOfMonth: Int, postFix:String = "") {
    private val fileName = if (test) "example" else "input"
    private val path = String.format("data/december%02d", dayOfMonth)
    private val file = File("$path/$fileName$postFix")

    val inputLines = if (file.exists()) file.bufferedReader().readLines() else emptyList()
}