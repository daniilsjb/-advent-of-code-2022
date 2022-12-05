package day05

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day05/Day05.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 05 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Move(
    val count: Int,
    val from: Int,
    val to: Int,
)

private data class Instructions(
    val stacks: List<List<Char>>,
    val moves: List<Move>,
)

@Suppress("SameParameterValue")
private fun parse(path: String): Instructions =
    File(path).readLines().let { lines ->
        val crates = lines
            .filter { it.contains('[') }
            .map { line -> line.chunked(4).map { it[1] } }

        val stackCount = crates[0].size
        val stacks = (0 until stackCount).map { i ->
            crates.asReversed()
                .map { it[i] }
                .filter { it.isLetter() }
        }

        val moves = lines
            .filter { it.startsWith("move") }
            .map { line ->
                val (count, from, to) = line
                    .split("""\D+""".toRegex())
                    .drop(1)
                    .map(String::toInt)

                Move(count, from - 1, to - 1)
            }

        Instructions(stacks, moves)
    }

private fun solve(instructions: Instructions, reverse: Boolean): String {
    val (_stacks, moves) = instructions
    val stacks = _stacks.map { it.toMutableList() }

    for ((count, source, target) in moves) {
        val crates = stacks[source].takeLast(count)
        stacks[target].addAll(if (reverse) crates.asReversed() else crates)
        repeat(count) {
            stacks[source].removeLast()
        }
    }

    return stacks
        .map(List<Char>::last)
        .joinToString(separator = "")
}

private fun part1(instructions: Instructions): String =
    solve(instructions, reverse = true)

private fun part2(instructions: Instructions): String =
    solve(instructions, reverse = false)
