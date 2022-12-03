package day03

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day03/Day03.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 03 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<String> =
    File(path).readLines()

private fun Char.toPriority(): Int =
    if (isUpperCase()) this - 'A' + 27 else this - 'a' + 1

private fun part1(data: List<String>): Int =
    data.map { line -> line.chunked(line.length / 2) }
        .map { (a, b) -> (a.toSet() intersect b.toSet()).first() }
        .sumOf(Char::toPriority)

private fun part2(data: List<String>): Int =
    data.windowed(size = 3, step = 3)
        .map { (a, b, c) -> (a.toSet() intersect b.toSet() intersect c.toSet()).first() }
        .sumOf(Char::toPriority)
