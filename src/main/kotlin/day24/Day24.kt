package day24

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day24/Day24.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 24 🎄")

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
)

private data class Blizzard(
    val origin: Point,
    val offset: Point,
)

private data class World(
    val source: Point,
    val target: Point,
    val blizzards: Set<Blizzard>,
)

@Suppress("SameParameterValue")
private fun parse(path: String): World {
    val lines = File(path).readLines()
    val source = Point(lines.first().indexOf('.'), 0)
    val target = Point(lines.last().indexOf('.'), lines.lastIndex)
    val blizzards = mutableSetOf<Blizzard>()
    for ((y, row) in lines.withIndex()) {
        for ((x, col) in row.withIndex()) {
            when (col) {
                '>' -> blizzards += Blizzard(Point(x, y), Point(+1, +0))
                '<' -> blizzards += Blizzard(Point(x, y), Point(-1, +0))
                '^' -> blizzards += Blizzard(Point(x, y), Point(+0, -1))
                'v' -> blizzards += Blizzard(Point(x, y), Point(+0, +1))
            }
        }
    }
    return World(source, target, blizzards)
}

private val World.minX
    get() = source.x

private val World.maxX
    get() = target.x

private val World.minY
    get() = source.y + 1

private val World.maxY
    get() = target.y - 1

private fun World.next(): World {
    val newBlizzards = mutableSetOf<Blizzard>()
    for ((origin, offset) in blizzards) {
        var nx = origin.x + offset.x
        var ny = origin.y + offset.y

        if (nx < minX) nx = maxX
        if (nx > maxX) nx = minX
        if (ny < minY) ny = maxY
        if (ny > maxY) ny = minY

        newBlizzards += Blizzard(Point(nx, ny), offset)
    }

    return copy(blizzards = newBlizzards)
}

private fun Point.neighbors(): List<Point> =
    listOf(
        Point(x + 0, y + 1),
        Point(x + 0, y - 1),
        Point(x + 1, y + 0),
        Point(x - 1, y + 0),
    )

private fun World.withinBounds(point: Point): Boolean =
    (point.x in minX..maxX) && (point.y in minY..maxY)

private fun World.safe(point: Point): Boolean =
    blizzards.find { it.origin == point } == null

private fun solve(world: World, source: Point, target: Point, stepCount: Int = 0): Pair<Int, World> {
    val worlds = mutableMapOf(stepCount to world)
    val queue = ArrayDeque<Pair<Int, Point>>().apply { add(stepCount to source) }
    val visited = mutableSetOf<Pair<Int, Point>>()

    while (queue.isNotEmpty()) {
        val attempt = queue.removeFirst()
        if (attempt in visited) {
            continue
        }

        val (steps, position) = attempt
        val nextWorld = worlds.computeIfAbsent(steps + 1) {
            worlds.getValue(steps).next()
        }

        val neighbors = position.neighbors()
        if (target in neighbors) {
            return steps + 1 to nextWorld
        }

        for (neighbor in position.neighbors()) {
            if (nextWorld.withinBounds(neighbor) && nextWorld.safe(neighbor)) {
                queue += steps + 1 to neighbor
            }
        }

        visited += attempt
        if (nextWorld.safe(position)) {
            queue += steps + 1 to position
        }
    }

    error("Could not find the path.")
}

private fun part1(data: World): Int =
    solve(data, data.source, data.target).let { (steps, _) -> steps }

private fun part2(w0: World): Int  {
    val (s1, w1) = solve(w0, w0.source, w0.target)
    val (s2, w2) = solve(w1, w0.target, w0.source, s1)
    val (s3,  _) = solve(w2, w0.source, w0.target, s2)
    return s3
}
