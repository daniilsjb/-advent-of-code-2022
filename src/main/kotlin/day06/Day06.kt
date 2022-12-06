package day06

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day06/Day06.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 06 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): String =
    File(path).readText().trim()

private fun solve(data: String, windowSize: Int): Int =
    data.withIndex().windowed(windowSize)
        .first { group -> group.distinctBy { it.value }.size == group.size }
        .last()
        .index + 1

private fun part1(data: String): Int =
    solve(data, windowSize = 4)

private fun part2(data: String): Int =
    solve(data, windowSize = 14)
