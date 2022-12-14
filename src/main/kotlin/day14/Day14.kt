package day14

import java.io.File
import kotlin.math.min
import kotlin.math.max

fun main() {
    val data = parse("src/main/kotlin/day14/Day14.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 14 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Segment(
    val points: List<Pair<Int, Int>>,
)

private fun String.toSegment(): Segment =
    split(" -> ")
        .map { it.split(",").let { (a, b) -> a.toInt() to b.toInt() } }
        .let { points -> Segment(points) }

@Suppress("SameParameterValue")
private fun parse(path: String): Set<Pair<Int, Int>> =
    File(path).readLines().map { it.toSegment() }.toObstacles()

private fun List<Segment>.toObstacles(): Set<Pair<Int, Int>> {
    val obstacles = mutableSetOf<Pair<Int, Int>>()
    for ((points) in this) {
        for ((a, b) in points.zipWithNext()) {
            val (ax, ay) = a
            val (bx, by) = b

            if (ax == bx) {
                for (dy in min(ay, by)..max(ay, by)) {
                    obstacles += ax to dy
                }
            } else {
                for (dx in min(ax, bx)..max(ax, bx)) {
                    obstacles += dx to ay
                }
            }
        }
    }
    return obstacles
}

private fun part1(data: Set<Pair<Int, Int>>): Int {
    val obstacles = data.toMutableSet()
    val maxy = obstacles.maxBy { (_, y) -> y }.second

    var sandCount = 0
    outer@while (true) {
        var sx = 500
        var sy = 0

        inner@while (sx to sy !in obstacles) {
            sy += 1

            if (sx to sy in obstacles) {
                if ((sx - 1) to sy !in obstacles) {
                    sx -= 1
                } else if ((sx + 1) to sy !in obstacles) {
                    sx += 1
                } else {
                    sy -= 1
                    break@inner
                }
            }

            if (sy >= maxy) {
                break@outer
            }
        }

        obstacles += sx to sy
        sandCount += 1
    }

    return sandCount
}

private fun part2(data: Set<Pair<Int, Int>>): Int {
    val obstacles = data.toMutableSet()
    val maxy = obstacles.maxBy { (_, y) -> y }.second

    var sandCount = 0
    outer@while (true) {
        var sx = 500
        var sy = 0

        inner@while (sx to sy !in obstacles) {
            sy += 1

            if (sx to sy in obstacles) {
                if ((sx - 1) to sy !in obstacles) {
                    sx -= 1
                } else if ((sx + 1) to sy !in obstacles) {
                    sx += 1
                } else {
                    sy -= 1
                    break@inner
                }
            }

            if (sy == maxy + 2) {
                sy -= 1
                break@inner
            }
        }

        obstacles += sx to sy
        sandCount += 1

        if (sx == 500 && sy == 0) {
            break@outer
        }
    }

    return sandCount
}
