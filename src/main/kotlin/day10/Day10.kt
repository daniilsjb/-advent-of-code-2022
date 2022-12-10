package day10

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day10/Day10.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 10 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer:\n$answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<String> =
    File(path).readLines()

private fun List<String>.execute(onCycle: (x: Int, cycle: Int) -> Unit) {
    var x = 1
    var cycle = 0
    for (instruction in this) {
        val tokens = instruction.split(" ")
        when (tokens[0]) {
            "noop" -> onCycle(x, ++cycle)
            "addx" -> {
                onCycle(x, ++cycle)
                onCycle(x, ++cycle)
                x += tokens[1].toInt()
            }
        }
    }
}

private fun part1(program: List<String>): Int {
    var result = 0
    program.execute { x, cycle ->
        if ((cycle - 20) % 40 == 0) {
            result += x * cycle;
        }
    }
    return result
}

private fun part2(program: List<String>): String {
    val (w, h) = 40 to 6
    val crt = Array(h) { Array(w) { '.' } }

    program.execute { x, cycle ->
        val col = (cycle - 1) % w
        if (x - 1 <= col && col <= x + 1) {
            val row = (cycle - 1) / w
            crt[row][col] = '#'
        }
    }

    return crt.asIterable()
        .joinToString(separator = "\n") { line ->
            line.joinToString(separator = "")
        }
}
