package day04

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day04/Day04.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 04 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Range(
    val start: Int,
    val end: Int
)

private fun String.toRange(): Range =
    this.split("-")
        .map(String::toInt)
        .let { (start, end) -> Range(start, end) }

@Suppress("SameParameterValue")
private fun parse(path: String): List<Pair<Range, Range>> =
    File(path).useLines { lines -> lines
        .map { it.split(",").map(String::toRange) }
        .map { (lhs, rhs) -> lhs to rhs }
        .toList()
    }

private fun Range.contains(other: Range): Boolean =
    start <= other.start && other.end <= end

private fun Range.overlaps(other: Range): Boolean =
    contains(other)
        || (other.start <= start && start <= other.end)
        || (other.start <= end && end <= other.end)

private fun part1(data: List<Pair<Range, Range>>): Int =
    data.count { (a, b) -> a.contains(b) || b.contains(a) }

private fun part2(data: List<Pair<Range, Range>>): Int =
    data.count { (a, b) -> a.overlaps(b) || b.overlaps(a) }
