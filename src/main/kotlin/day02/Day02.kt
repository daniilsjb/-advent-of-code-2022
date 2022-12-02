package day02

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day02/Day02.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 02 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Rule(
    val points: Int,
    val beats: String,
    val loses: String,
)

private val rules = mapOf(
    "A" to Rule(points = 1, beats = "C", loses = "B"), // Rock
    "B" to Rule(points = 2, beats = "A", loses = "C"), // Paper
    "C" to Rule(points = 3, beats = "B", loses = "A"), // Scissors
)

private fun assignPoints(elf: String, you: String): Int = when {
    rules[you]?.beats == elf -> 6
    rules[elf]?.beats == you -> 0
    else -> 3
}

private fun playRound(elf: String, you: String): Int =
    rules[you]!!.points + assignPoints(elf, you)

@Suppress("SameParameterValue")
private fun parse(path: String): List<Pair<String, String>> =
    File(path).useLines { lines ->
        lines.map { it.split(" ").let { (a, b) -> a to b } }.toList()
    }

private fun part1(data: List<Pair<String, String>>): Int =
    data.sumOf { (elf, you) ->
        playRound(elf, when (you) {
            "X" -> "A"
            "Y" -> "B"
            "Z" -> "C"
            else -> throw IllegalArgumentException()
        })
    }

private fun part2(data: List<Pair<String, String>>): Int =
    data.sumOf { (elf, you) ->
        playRound(elf, when (you) {
            "X" -> rules[elf]!!.beats
            "Y" -> elf
            "Z" -> rules[elf]!!.loses
            else -> throw IllegalArgumentException()
        })
    }
