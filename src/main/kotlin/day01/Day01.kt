package day01

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day01/Day01.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 01 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private fun parse(path: String): List<List<Int>> =
    File(path).readText()
        .trim()
        .split("\n\n")
        .map { it.split("\n").map(String::toInt) }

private fun part1(data: List<List<Int>>): Int =
    data.maxOfOrNull(List<Int>::sum) ?: 0

private fun part2(data: List<List<Int>>): Int =
    data.map(List<Int>::sum)
        .sortedDescending()
        .take(3)
        .sum()
