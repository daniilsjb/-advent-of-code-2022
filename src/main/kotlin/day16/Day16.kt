package day16

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day16/Day16-Sample.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 16 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Cave(
    val rates: Map<String, Int>,
    val edges: Map<String, List<String>>,
    val costs: Map<Pair<String, String>, Int>,
    val useful: Set<String>,
)

@Suppress("SameParameterValue")
private fun parse(path: String): Cave {
    val rates = mutableMapOf<String, Int>()
    val edges = mutableMapOf<String, List<String>>()
    val useful = mutableSetOf<String>()

    val pattern = Regex("""Valve (\w\w) has flow rate=(\d+); tunnels? leads? to valves? (.*)""")
    File(path).forEachLine { line ->
        val (name, rate, tunnels) = (pattern.matchEntire(line) ?: error("Couldn't parse: $line")).destructured
        rates[name] = rate.toInt()
        edges[name] = tunnels.split(", ")
        if (rate.toInt() != 0) {
            useful += name
        }
    }

    val costs = floydWarshall(edges)
    return Cave(rates, edges, costs, useful)
}

private fun floydWarshall(edges: Map<String, List<String>>): Map<Pair<String, String>, Int> {
    val nodes = edges.keys
    val costs = mutableMapOf<Pair<String, String>, Int>()

    for (source in nodes) {
        for (target in nodes) {
            costs[source to target] = nodes.size + 1
        }
    }
    for ((source, neighbors) in edges) {
        for (target in neighbors) {
            costs[source to target] = 1
        }
    }

    for (source in nodes) {
        costs[source to source] = 0
    }

    for (via in nodes) {
        for (source in nodes) {
            for (target in nodes) {
                if (costs[source to target]!! > costs[source to via]!! + costs[via to target]!!) {
                    costs[source to target] = costs[source to via]!! + costs[via to target]!!
                }
            }
        }
    }

    return costs
}

private fun Cave.dfs(
    source: String,
    minutesLeft: Int,
    candidates: Set<String> = useful,
    rate: Int = 0,
    pressure: Int = 0,
    elephantAllowed: Boolean = false,
): Int {
    var maxPressure = pressure + rate * minutesLeft

    // This solution is really slow and ugly, but I don't care ¯\_(ツ)_/¯
    if (elephantAllowed) {
        maxPressure += dfs(source = "AA", minutesLeft = 26, candidates = candidates, elephantAllowed = false)
    }

    for (target in candidates) {
        val cost = costs.getValue(source to target) + 1
        if (minutesLeft - cost <= 0) {
            continue
        }

        val newPressure = dfs(
            source = target,
            minutesLeft = minutesLeft - cost,
            candidates = candidates - target,
            rate = rate + rates.getValue(target),
            pressure = pressure + rate * cost,
            elephantAllowed = elephantAllowed
        )

        if (newPressure > maxPressure) {
            maxPressure = newPressure
        }
    }

    return maxPressure
}

private fun part1(cave: Cave): Int =
    cave.dfs(source = "AA", minutesLeft = 30)

private fun part2(cave: Cave): Int =
    cave.dfs(source = "AA", minutesLeft = 26, elephantAllowed = true)
