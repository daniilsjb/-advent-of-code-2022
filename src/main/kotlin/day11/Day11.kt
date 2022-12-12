package day11

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day11/Day11.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 11 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Monkey(
    val initialItems: List<Long>,
    val operation: (Long) -> Long,
    val condition: Long,
    val consequent: Int,
    val alternate: Int,
)

private fun List<String>.toMonkey(): Monkey {
    val lines = filter { it.startsWith("  ") }

    val initialItems = lines[0]
        .substringAfterLast(": ")
        .split(", ")
        .map(String::toLong)

    val (op, rhs) = lines[1]
        .split(" ")
        .takeLast(2)

    val operator = when (op) {
        "*" -> { a: Long, b: Long -> a * b }
        "+" -> { a: Long, b: Long -> a + b }
        else -> error("Invalid operator: '$op'")
    }

    val operation = if (rhs == "old") { n: Long ->
        operator(n, n)
    } else { n: Long ->
        operator(n, rhs.toLong())
    }

    val condition = lines[2]
        .substringAfterLast(" ")
        .toLong()

    val consequent = lines[3]
        .substringAfterLast(" ")
        .toInt()

    val alternate = lines[4]
        .substringAfterLast(" ")
        .toInt()

    return Monkey(
        initialItems, operation, condition, consequent, alternate
    )
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Monkey> =
    File(path)
        .readLines()
        .chunked(7)
        .map { it.toMonkey() }

private fun List<Monkey>.solve(rounds: Int, transform: (Long) -> Long): Long {
    val items = map { it.initialItems.toMutableList() }

    val interactions = Array(size) { 0L }
    repeat(rounds) {
        for ((i, monkey) in withIndex()) {
            for (item in items[i]) {
                val worry = transform(monkey.operation(item))
                val target = if (worry % monkey.condition == 0L) {
                    monkey.consequent
                } else {
                    monkey.alternate
                }

                items[target].add(worry)
            }

            interactions[i] += items[i].size.toLong()
            items[i].clear()
        }
    }

    return interactions
        .sortedArrayDescending()
        .take(2)
        .reduce(Long::times)
}

private fun part1(monkeys: List<Monkey>): Long {
    return monkeys.solve(rounds = 20) { it / 3L }
}

private fun part2(monkeys: List<Monkey>): Long {
    val test = monkeys
        .map(Monkey::condition)
        .reduce(Long::times)

    return monkeys.solve(rounds = 10_000) { it % test }
}
