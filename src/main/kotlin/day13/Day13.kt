package day13

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day13/Day13.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 13 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private sealed class Packet : Comparable<Packet> {
    data class Integer(val value: Int) : Packet() {
        override fun toString() = value.toString()

        override operator fun compareTo(other: Packet): Int =
            when (other) {
                is Integer -> value.compareTo(other.value)
                is List -> compareContents(listOf(this), other.values)
            }
    }

    data class List(val values: kotlin.collections.List<Packet>) : Packet() {
        override fun toString() = values.toString()

        override operator fun compareTo(other: Packet): Int =
            when (other) {
                is Integer -> compareContents(values, listOf(other))
                is List -> compareContents(values, other.values)
            }
    }

    companion object {
        fun of(n: Int) =
            Integer(n)

        fun of(vararg elements: Packet) =
            List(listOf(*elements))
    }
}

private fun compareContents(lhs: List<Packet>, rhs: List<Packet>): Int {
    for ((a, b) in lhs.zip(rhs)) {
        val result = a.compareTo(b)
        if (result != 0) {
            return result
        }
    }
    return lhs.size.compareTo(rhs.size)
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Packet> =
    File(path).readLines()
        .filterNot { it.isBlank() }
        .map { it.toPacket() }

private fun String.toPacket(): Packet =
    ParserState(source = this).packet()

private data class ParserState(
    val source: String,
    var current: Int = 0,
)

private fun ParserState.peek(): Char =
    source[current]

private fun ParserState.next(): Char =
    source[current++]

/*
 * packet ::= list | integer
 */
private fun ParserState.packet(): Packet {
    return if (peek() == '[') {
        list()
    } else {
        integer()
    }
}

/*
 * list ::= '[' (packet ',')* packet? ']'
 */
private fun ParserState.list(): Packet.List {
    val values = mutableListOf<Packet>()

    next() // skip '['
    while (peek() != ']') {
        values.add(packet())
        if (peek() == ',') {
            next()
        }
    }

    next() // skip ']'
    return Packet.List(values)
}

/*
 * integer ::= [0-9]+
 */
private fun ParserState.integer(): Packet.Integer {
    var result = 0
    while (peek().isDigit()) {
        result = result * 10 + next().digitToInt()
    }
    return Packet.Integer(result)
}

private fun part1(data: List<Packet>): Int {
    val indices = mutableListOf<Int>()
    for ((i, packet) in data.chunked(2).withIndex()) {
        val (lhs, rhs) = packet
        if (lhs < rhs) {
            indices.add(i + 1)
        }
    }
    return indices.sum()
}

private fun part2(data: List<Packet>): Int {
    val divider2 = Packet.of(Packet.of(2))
    val divider6 = Packet.of(Packet.of(6))
    val sorted = (data + listOf(divider2, divider6)).sorted()
    return (sorted.indexOf(divider2) + 1) * (sorted.indexOf(divider6) + 1)
}
