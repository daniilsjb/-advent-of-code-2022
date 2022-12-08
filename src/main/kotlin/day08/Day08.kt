package day08

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day08/Day08.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 08 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<List<Int>> =
    File(path).useLines { lines ->
        lines.map { it.map(Char::digitToInt) }.toList()
    }

private fun part1(data: List<List<Int>>): Int {
    var counter = 0
    for (y in data.indices) {
        for (x in data[0].indices) {
            val current = data[y][x]
            if ((x - 1 downTo 0).all { i -> data[y][i] < current }) {
                counter += 1
                continue
            }
            if ((x + 1..data[0].lastIndex).all { i -> data[y][i] < current }) {
                counter += 1
                continue
            }
            if ((y - 1 downTo 0).all { i -> data[i][x] < current }) {
                counter += 1
                continue
            }
            if ((y + 1..data.lastIndex).all { i -> data[i][x] < current }) {
                counter += 1
                continue
            }
        }
    }

    return counter
}

private fun part2(data: List<List<Int>>): Int {
    val scores = mutableListOf<Int>()
    for (y in data.indices) {
        for (x in data[0].indices) {
            val current = data[y][x]

            val distances = Array(4) { 0 }
            for (i in (x - 1) downTo 0) {
                distances[0] += 1
                if (data[y][i] >= current) {
                    break
                }
            }
            for (i in (x + 1)..data[0].lastIndex) {
                distances[1] += 1
                if (data[y][i] >= current) {
                    break
                }
            }
            for (i in (y - 1) downTo 0) {
                distances[2] += 1
                if (data[i][x] >= current) {
                    break
                }
            }
            for (i in (y + 1)..data.lastIndex) {
                distances[3] += 1
                if (data[i][x] >= current) {
                    break
                }
            }

            scores.add(distances.reduce(Int::times))
        }
    }

    return scores.max()
}
