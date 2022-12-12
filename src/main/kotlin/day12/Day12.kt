package day12

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day12/Day12.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 12 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Heightmap(
    val nodes: String,
    val width: Int,
    val height: Int,
)

@Suppress("SameParameterValue")
private fun parse(path: String): Heightmap {
    val data = File(path).readLines()

    val width = data[0].length
    val height = data.size

    return Heightmap(data.joinToString(separator = ""), width, height)
}

private fun Char.toElevation(): Char =
    when (this) {
        'S' -> 'a'
        'E' -> 'z'
        else -> this
    }

private fun Char.reaches(other: Char): Boolean =
    (other.toElevation() - this.toElevation()) <= 1

private fun Heightmap.neighborsOf(index: Int): List<Int> {
    val x = index % width
    val y = index / width

    val current = nodes[index]

    val result = mutableListOf<Int>()
    if (x > 0 && current.reaches(nodes[index - 1])) {
        result.add(index - 1)
    }
    if (y > 0 && current.reaches(nodes[index - width])) {
        result.add(index - width)
    }
    if (x < width - 1 && current.reaches(nodes[index + 1])) {
        result.add(index + 1)
    }
    if (y < height - 1 && current.reaches(nodes[index + width])) {
        result.add(index + width)
    }

    return result
}

private fun Heightmap.solve(source: Int): Int? {
    val queue = ArrayDeque<Int>()
    val distances = Array(nodes.length) { Int.MAX_VALUE }

    queue.add(source)
    distances[source] = 0

    while (queue.isNotEmpty()) {
        val vertex = queue.removeFirst()
        for (neighbor in neighborsOf(vertex)) {
            if (nodes[neighbor] == 'E') {
                return distances[vertex] + 1
            }
            if (distances[neighbor] > distances[vertex] + 1) {
                distances[neighbor] = distances[vertex] + 1
                queue.add(neighbor)
            }
        }
    }

    return null
}

private fun part1(graph: Heightmap): Int {
    val source = graph.nodes.indexOfFirst { it == 'S' }
    return graph.solve(source) ?: error("Path not found.")
}

private fun part2(graph: Heightmap): Int {
    val distances = mutableListOf<Int>()
    for ((source, c) in graph.nodes.withIndex()) {
        if (c == 'a' || c == 'S') {
            distances.add(graph.solve(source) ?: continue)
        }
    }
    return distances.min()
}
