package day17

import java.io.File
import kotlin.math.max

fun main() {
    val data = parse("src/main/kotlin/day17/Day17.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 17 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Int> =
    File(path).readText().trim()
        .map { if (it == '>') +1 else -1  }

private val shapes = listOf(
    setOf(0 to 0, 1 to 0, 2 to 0, 3 to 0),
    setOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2),
    setOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2),
    setOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
    setOf(0 to 0, 1 to 0, 0 to 1, 1 to 1),
)

private data class State(
    val shifts: List<Int>,
    val obstacles: MutableSet<Pair<Int, Int>> = mutableSetOf(),
    var shapeIndex: Int = 0,
    var shiftIndex: Int = 0,
    var height: Int = 0,
)

private data class Step(
    val ceiling: List<Int>,
    val shapeIndex: Int,
    val shiftIndex: Int,
)

private fun State.step() {
    var shape = shapes[shapeIndex++ % shapes.size]
        .map { (x, y) -> (2 + x) to (height + 3 + y) }
        .toSet()

    while (true) {
        val shift = shifts[shiftIndex++ % shifts.size]
        val shiftedShapeX = shape
            .map { (x, y) -> (x + shift) to y }
            .toSet()

        if (shiftedShapeX.all { (x, _) -> x in 0..6 }) {
            if ((shiftedShapeX intersect obstacles).isEmpty()) {
                shape = shiftedShapeX
            }
        }

        val shiftedShapeY = shape
            .map { (x, y) -> x to (y - 1) }
            .toSet()

        if (shiftedShapeY.all { (_, y) -> y >= 0 }) {
            if ((shiftedShapeY intersect obstacles).isEmpty()) {
                shape = shiftedShapeY
                continue
            }
        }

        break
    }

    obstacles += shape
    height = max(height, shape.maxOf { (_, y) -> y } + 1)
}

private fun solve(shifts: List<Int>, count: Long): Long {
    val state = State(shifts)
    val history = mutableMapOf<Step, Pair<Int, Int>>()
    while (true) {
        state.step()

        val highest = state.obstacles.maxOf { (_, y) -> y }
        val ceiling = state.obstacles
            .groupBy { (x, _) -> x }.entries
            .sortedBy { (x, _) -> x }
            .map { (_, points) -> points.maxOf { (_, y) -> y } }
            .let { ys -> ys.map { y -> highest - y } }

        val step = Step(
            ceiling = ceiling,
            shapeIndex = state.shapeIndex % shapes.size,
            shiftIndex = state.shiftIndex % shifts.size,
        )

        if (step !in history) {
            history[step] = state.shapeIndex to state.height
            continue
        }

        val (shapeCount, height) = history.getValue(step)
        val shapesPerCycle = state.shapeIndex - shapeCount
        val cycleCount = (count - shapeCount) / shapesPerCycle
        val cycleHeight = state.height - height
        val shapesRemain = (count - shapeCount) - (shapesPerCycle * cycleCount)

        repeat(shapesRemain.toInt()) {
            state.step()
        }

        return state.height + (cycleHeight * (cycleCount - 1))
    }
}

private fun part1(shifts: List<Int>): Long =
    solve(shifts, count = 2022L)

private fun part2(shifts: List<Int>): Long =
    solve(shifts, count = 1_000_000_000_000L)
