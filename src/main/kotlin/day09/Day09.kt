package day09

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val data = parse("src/main/kotlin/day09/Day09.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 09 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Pair<String, Int>> =
    File(path).readLines()
        .map { it.split(" ") }
        .map { (move, step) -> move to step.toInt() }

private data class Knot(
    var x: Int,
    var y: Int,
)

private fun MutableList<Knot>.move(direction: String) {
    when (direction) {
        "U" -> this[0].y += 1
        "D" -> this[0].y -= 1
        "R" -> this[0].x += 1
        "L" -> this[0].x -= 1
    }

    for ((head, tail) in this.zipWithNext()) {
        val dx = head.x - tail.x
        val dy = head.y - tail.y

        if (dx.absoluteValue < 2 && dy.absoluteValue < 2) {
            return
        }

        tail.x += dx.sign
        tail.y += dy.sign
    }
}

private fun solve(data: List<Pair<String, Int>>, count: Int): Int {
    val rope = generateSequence { Knot(0, 0) }
        .take(count)
        .toMutableList()

    val visited = mutableSetOf(Knot(0, 0))
    for ((direction, steps) in data) {
        repeat(steps) {
            rope.move(direction)
            visited += rope.last().copy()
        }
    }


    return visited.size
}

private fun part1(data: List<Pair<String, Int>>): Int =
    solve(data, count = 2)

private fun part2(data: List<Pair<String, Int>>): Int =
    solve(data, count = 10)
