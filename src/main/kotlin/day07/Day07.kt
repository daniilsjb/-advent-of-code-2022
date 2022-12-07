package day07

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day07/Day07.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 07 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Long> {
    var node = "/"
    val dirs = mutableMapOf(node to 0L)

    File(path).forEachLine { line ->
        val tokens = line.split(" ")
        if (tokens[0] == "$" && tokens[1] == "cd") {
            val name = tokens[2]
            node = when (name) {
                "/" -> "/"
                ".." -> node.substringBeforeLast('/')
                else -> "$node/$name"
            }
        } else {
            tokens[0].toLongOrNull()?.let { size ->
                var current = node
                while (current.isNotBlank()) {
                    dirs.merge(current, size, Long::plus)
                    current = current.substringBeforeLast('/')
                }
            }
        }
    }

    return dirs.map { it.value }
}

private fun part1(data: List<Long>): Long {
    return data.filter { it <= 100000L }.sum()
}

private fun part2(data: List<Long>): Long {
    val required = 30_000_000 - (70_000_000 - data.first())
    return data.sorted().first { it >= required }
}
