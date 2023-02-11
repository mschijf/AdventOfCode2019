package com.tool.mylambdas


fun CharSequence.distinct(): String {
    val result = StringBuilder()
    this.forEach { ch -> if (!result.contains(ch)) result.append(ch) }
    return result.toString()
}

/**
 * Returns a substring between two delimeters.
 * If string doesn't contain beforeDelimeter after the afterDelimter, the result is equal to using the substringAfter (with afterDelimeter)
 * If string doesn't contain afterDelimeter, the result is equal to using the substringBefore (with beforeDelimeter)
 * If string doesn't contain both delimeters, the original string is returend
 *
 */
fun String.substringBetween(afterDelimeter: String, beforeDelimter: String): String {
    return this.substringAfter(afterDelimeter).substringBefore(beforeDelimter)
}

fun CharSequence.hasOnlyDigits(): Boolean {
    return this.all{ch -> ch.isDigit()}
}