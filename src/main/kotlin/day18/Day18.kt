package day18

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day18/Day18.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 18 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Point(
    val x: Int,
    val y: Int,
    val z: Int,
)

@Suppress("SameParameterValue")
private fun parse(path: String): Set<Point> =
    File(path).readLines().mapTo(HashSet()) { line ->
        line.split(",")
            .map { it.toInt() }
            .let { (x, y, z) -> Point(x, y, z) }
    }

private fun Point.neighbors(): Set<Point> {
    return setOf(
        Point(x + 1, y + 0, z + 0),
        Point(x - 1, y + 0, z + 0),
        Point(x + 0, y + 1, z + 0),
        Point(x + 0, y - 1, z + 0),
        Point(x + 0, y + 0, z + 1),
        Point(x + 0, y + 0, z - 1),
    )
}

private fun part1(data: Set<Point>): Int =
    data.flatMap { it.neighbors() }
        .count { it !in data }

private fun part2(data: Set<Point>): Int {
    val xRange = (data.minOf { it.x } - 1)..(data.maxOf { it.x } + 1)
    val yRange = (data.minOf { it.y } - 1)..(data.maxOf { it.y } + 1)
    val zRange = (data.minOf { it.z } - 1)..(data.maxOf { it.z } + 1)

    val start = Point(
        xRange.first,
        yRange.first,
        zRange.first,
    )

    val queue = ArrayDeque<Point>().apply { add(start) }
    val visited = mutableSetOf<Point>()

    var area = 0
    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()
        if (point in visited) {
            continue
        }

        visited += point
        for (neighbor in point.neighbors()) {
            val (x, y, z) = neighbor
            if (x in xRange && y in yRange && z in zRange) {
                if (neighbor in data) {
                    area += 1
                } else if (neighbor !in visited) {
                    queue += neighbor
                }
            }
        }
    }

    return area
}
