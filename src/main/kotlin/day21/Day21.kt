package day21

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day21/Day21.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 21 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private sealed interface Monkey {
    data class Yelling(
        val number: Long,
    ) : Monkey

    data class Operation(
        val lhs: String,
        val rhs: String,
        val op: String,
    ) : Monkey
}

@Suppress("SameParameterValue")
private fun parse(path: String): Map<String, Monkey> {
    val data = mutableMapOf<String, Monkey>()
    File(path).forEachLine { line ->
        val (name, job) = line.split(": ")
        val monkey = job.toLongOrNull()?.let { Monkey.Yelling(it) } ?: run {
            val (lhs, op, rhs) = job.split(" ")
            Monkey.Operation(lhs, rhs, op)
        }
        data[name] = monkey
    }
    return data
}

private fun Monkey.evaluate(ctx: Map<String, Monkey>): Long =
    when (this) {
        is Monkey.Yelling -> number
        is Monkey.Operation -> {
            val lhsNumber = ctx.getValue(lhs).evaluate(ctx)
            val rhsNumber = ctx.getValue(rhs).evaluate(ctx)
            when (op) {
                "+" -> lhsNumber + rhsNumber
                "-" -> lhsNumber - rhsNumber
                "*" -> lhsNumber * rhsNumber
                "/" -> lhsNumber / rhsNumber
                else -> error("Unknown operation: $op")
            }
        }
    }

private fun Monkey.evaluateOrNull(ctx: Map<String, Monkey>): Long? {
    return when (this) {
        is Monkey.Yelling -> number
        is Monkey.Operation -> {
            if (lhs == "humn" || rhs == "humn") {
                return null
            }

            val lhsNumber = ctx.getValue(lhs).evaluateOrNull(ctx) ?: return null
            val rhsNumber = ctx.getValue(rhs).evaluateOrNull(ctx) ?: return null
            when (op) {
                "+" -> lhsNumber + rhsNumber
                "-" -> lhsNumber - rhsNumber
                "*" -> lhsNumber * rhsNumber
                "/" -> lhsNumber / rhsNumber
                else -> error("Unknown operation: $op")
            }
        }
    }
}

private fun Monkey.coerce(ctx: Map<String, Monkey>, expected: Long): Long {
    return when (this) {
        is Monkey.Yelling -> expected
        is Monkey.Operation -> {
            ctx.getValue(rhs).evaluateOrNull(ctx)?.let { value ->
                val next = ctx.getValue(lhs)
                return when (op) {
                    "+" -> next.coerce(ctx, expected - value)
                    "-" -> next.coerce(ctx, expected + value)
                    "*" -> next.coerce(ctx, expected / value)
                    "/" -> next.coerce(ctx, expected * value)
                    else -> error("Unknown operation: $op")
                }
            }
            ctx.getValue(lhs).evaluateOrNull(ctx)?.let { value ->
                val next = ctx.getValue(rhs)
                return when (op) {
                    "+" -> next.coerce(ctx, expected - value)
                    "-" -> next.coerce(ctx, value - expected)
                    "*" -> next.coerce(ctx, expected / value)
                    "/" -> next.coerce(ctx, value / expected)
                    else -> error("Unknown operation: $op")
                }
            }
            error("Cannot evaluate either branch.")
        }
    }
}

private fun part1(data: Map<String, Monkey>): Long =
    data.getValue("root").evaluate(data)

private fun part2(data: Map<String, Monkey>): Long {
    val (lhs, rhs, _) = data["root"] as Monkey.Operation
    data.getValue(lhs).evaluateOrNull(data)?.let { expected ->
        return data.getValue(rhs).coerce(data, expected)
    }
    data.getValue(rhs).evaluateOrNull(data)?.let { expected ->
        return data.getValue(lhs).coerce(data, expected)
    }
    error("Cannot evaluate either branch.")
}
