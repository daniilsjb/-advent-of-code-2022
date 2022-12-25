package day20

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day20/Day20.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 20 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Int> =
    File(path).readLines().map(String::toInt)

private fun solve(data: List<Int>, times: Int = 1, key: Long = 1L): Long {
    val numbers = data.map { it * key }
    val mixed = (0..data.lastIndex).toMutableList()
    repeat(times) {
        for ((i, number) in numbers.withIndex()) {
            val oldMixedIndex = mixed.indexOf(i)
            mixed.removeAt(oldMixedIndex)

            val newMixedIndex = (oldMixedIndex + number).mod(mixed.size)
            mixed.add(newMixedIndex, i)
        }
    }

    val zeroIndex = mixed.indexOf(data.indexOf(0))
    return listOf(1000, 2000, 3000).sumOf { offset ->
        numbers[mixed[(zeroIndex + offset) % mixed.size]]
    }
}

private fun part1(data: List<Int>): Long =
    solve(data)

private fun part2(data: List<Int>): Long =
    solve(data, times = 10, key = 811589153L)
