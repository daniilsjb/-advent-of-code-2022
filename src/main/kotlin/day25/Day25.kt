package day25

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day25/Day25.txt")

    println("🎄 Day 25 🎄")
    println("Answer: ${solve(data)}")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<String> =
    File(path).readLines()

private fun String.toDecimal(): Long {
    var result = 0L
    for (digit in this) {
        when (digit) {
            '2' -> result = result * 5L + 2L
            '1' -> result = result * 5L + 1L
            '0' -> result = result * 5L + 0L
            '-' -> result = result * 5L - 1L
            '=' -> result = result * 5L - 2L
        }
    }
    return result
}

private fun Long.toSnafu(): String {
    if (this == 0L) {
        return "0"
    }

    val result = StringBuilder()
    var n = this
    while (n > 0L) {
        when (n % 5L) {
            0L -> result.append('0')
            1L -> result.append('1')
            2L -> result.append('2')
            3L -> {
                result.append('=')
                n += 5
            }
            4L -> {
                result.append('-')
                n += 5
            }
        }
        n /= 5
    }

    return result.reverse().toString()
}

private fun solve(data: List<String>): String =
    data.sumOf(String::toDecimal).toSnafu()
