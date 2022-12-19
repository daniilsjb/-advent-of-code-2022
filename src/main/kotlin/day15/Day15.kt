package day15

import java.io.File
import kotlin.math.abs

fun main() {
    val data = parse("src/main/kotlin/day15/Day15.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 15 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Sensor(
    val sx: Int,
    val sy: Int,
    val bx: Int,
    val by: Int,
)

@Suppress("SameParameterValue")
private fun parse(path: String): List<Sensor> {
    val pattern = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
    return File(path).readLines().map { line ->
        val (sx, sy, bx, by) = pattern.matchEntire(line)!!.groupValues
            .drop(1)
            .map { it.toInt() }

        Sensor(sx, sy, bx, by)
    }
}

private data class Range(
    val from: Int,
    val to: Int,
)

private fun findCoverage(data: List<Sensor>, y: Int): List<Range> {
    val ranges = mutableListOf<Range>()
    for ((sx, sy, bx, by) in data) {
        val distance = abs(sx - bx) + abs(sy - by)
        val dx = distance - abs(sy - y)
        if (dx >= 0) {
            ranges += Range(sx - dx, sx + dx)
        }
    }

    ranges.sortWith(compareBy(Range::from, Range::to))
    val mergedRanges = mutableListOf<Range>()

    var merged = ranges[0]
    for (i in 1..ranges.lastIndex) {
        val range = ranges[i]
        if (merged.to >= range.to) {
            continue
        }
        if (merged.to >= range.from) {
            merged = Range(merged.from, range.to)
            continue
        }

        mergedRanges += merged
        merged = range
    }

    mergedRanges += merged
    return mergedRanges
}

private fun part1(data: List<Sensor>, target: Int = 2_000_000): Int {
    val covered = findCoverage(data, target)
        .sumOf { (from, to) -> abs(to) + abs(from) + 1 }

    val beacons = data.mapTo(HashSet()) { it.by }
        .count { it == target }

    return covered - beacons
}

private fun part2(data: List<Sensor>, size: Int = 4_000_000): Long {
    for (y in 0..size) {
        val ranges = findCoverage(data, y)
        if (ranges.size > 1) {
            val x = ranges[0].to + 1
            return x.toLong() * 4_000_000L + y.toLong()
        }
    }
    error("Beacon not found!")
}
