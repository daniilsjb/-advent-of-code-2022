package day23

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day23/Day23.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 23 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

typealias Point = Pair<Int, Int>

@Suppress("SameParameterValue")
private fun parse(path: String): Set<Point> {
    val elves = mutableSetOf<Pair<Int, Int>>()
    val lines = File(path).readLines()
    for ((y, row) in lines.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == '#') {
                elves += x to y
            }
        }
    }
    return elves
}

private fun Point.neighbors(): List<Point> {
    val (x, y) = this
    return listOf(
        x - 1 to y - 1,
        x + 0 to y - 1,
        x + 1 to y - 1,
        x - 1 to y + 0,
        x + 1 to y + 0,
        x - 1 to y + 1,
        x + 0 to y + 1,
        x + 1 to y + 1,
    )
}

private val directions = listOf(
    (+0 to -1) to listOf(-1 to -1, +0 to -1, +1 to -1), // North
    (+0 to +1) to listOf(-1 to +1, +0 to +1, +1 to +1), // South
    (-1 to +0) to listOf(-1 to -1, -1 to +0, -1 to +1), // West
    (+1 to +0) to listOf(+1 to -1, +1 to +0, +1 to +1), // East
)

private data class State(
    val elves: Set<Point>,
    val firstDirection: Int = 0,
)

private fun State.next(): State {
    val movements = mutableMapOf<Point, List<Point>>()
    for ((x, y) in elves) {
        if ((x to y).neighbors().all { it !in elves }) {
            continue
        }
        search@for (i in 0..3) {
            val (proposal, offsets) = directions[(firstDirection + i) % 4]
            if (offsets.all { (dx, dy) ->  (x + dx) to (y + dy) !in elves }) {
                val (px, py) = proposal
                val (nx, ny) = (x + px) to (y + py)
                movements.merge(nx to ny, listOf(x to y)) { a, b -> a + b }
                break@search
            }
        }
    }

    val newElves = elves.toMutableSet()
    for ((position, candidates) in movements) {
        if (candidates.size == 1) {
            newElves -= candidates.first()
            newElves += position
        }
    }

    val newFirstDirection = (firstDirection + 1) % 4
    return State(newElves, newFirstDirection)
}

private fun part1(data: Set<Point>): Int =
    generateSequence(State(data), State::next)
        .drop(10)
        .first()
        .let { (elves, _) ->
            val minX = elves.minOf { (x, _) -> x }
            val maxX = elves.maxOf { (x, _) -> x }
            val minY = elves.minOf { (_, y) -> y }
            val maxY = elves.maxOf { (_, y) -> y }

            (minY..maxY).sumOf { y ->
                (minX..maxX).sumOf { x ->
                    (if (x to y !in elves) 1 else 0).toInt()
                }
            }
        }

private fun part2(data: Set<Point>): Int =
    generateSequence(State(data), State::next)
        .zipWithNext()
        .withIndex()
        .dropWhile { (_, states) -> states.let { (s0, s1) -> s0.elves != s1.elves }  }
        .first()
        .let { (idx, _) -> idx + 1 }
