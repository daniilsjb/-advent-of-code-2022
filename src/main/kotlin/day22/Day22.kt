package day22

import java.io.File

fun main() {
    val data = parse("src/main/kotlin/day22/Day22.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 22 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Point(
    val x: Int,
    val y: Int,
)

private sealed interface Step {
    data class Move(val count: Int) : Step
    object TurnRight : Step
    object TurnLeft : Step
}

private data class Board(
    val walls: Set<Point>,
    val steps: List<Step>,
)

private fun String.toSteps(): List<Step> {
    val steps = mutableListOf<Step>()
    var count = 0
    for (c in this) {
        if (c.isDigit()) {
            count = count * 10 + c.digitToInt()
        } else {
            steps += Step.Move(count)
            steps += when (c) {
                'R' -> Step.TurnRight
                'L' -> Step.TurnLeft
                else -> error("Unknown turn: $c")
            }
            count = 0
        }
    }

    steps += Step.Move(count)
    return steps
}

private fun List<String>.toWalls(): Set<Point> {
    val walls = mutableSetOf<Point>()
    for ((y, row) in this.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == '#') {
                walls += Point(x, y)
            }
        }
    }
    return walls
}

@Suppress("SameParameterValue")
private fun parse(path: String): Board =
    File(path).readLines().let { lines ->
        val walls = lines.dropLast(2).toWalls()
        val steps = lines.last().toSteps()
        Board(walls, steps)
    }

private sealed interface Facing {
    fun turnLeft(): Facing
    fun turnRight(): Facing

    object E : Facing {
        override fun turnLeft(): Facing = N
        override fun turnRight(): Facing = S
        override fun toString() = "E"
    }

    object S : Facing {
        override fun turnLeft(): Facing = E
        override fun turnRight(): Facing = W
        override fun toString() = "S"
    }

    object W : Facing {
        override fun turnLeft(): Facing = S
        override fun turnRight(): Facing = N
        override fun toString() = "W"
    }

    object N : Facing {
        override fun turnLeft(): Facing = W
        override fun turnRight(): Facing = E
        override fun toString() = "N"
    }
}

private data class Transition(
    val sourceId: String,
    val targetId: String,
    val sourceFacing: Facing,
    val targetFacing: Facing,
)

private const val PLANE_SIZE = 50
private data class Plane(
    val topLeft: Point,
    val transitionToE: Transition,
    val transitionToS: Transition,
    val transitionToW: Transition,
    val transitionToN: Transition,
)

private fun Plane.selectTransition(facing: Facing): Transition =
    when (facing) {
        is Facing.E -> transitionToE
        is Facing.S -> transitionToS
        is Facing.W -> transitionToW
        is Facing.N -> transitionToN
    }

private val planes = mapOf(
    // == Part 1 ==============================================================
    "A1" to Plane(
        topLeft = Point(50, 0),
        transitionToE = Transition(sourceId = "A1", targetId = "A2", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A1", targetId = "A3", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A1", targetId = "A2", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A1", targetId = "A5", Facing.N, Facing.N),
    ),
    "A2" to Plane(
        topLeft = Point(100, 0),
        transitionToE = Transition(sourceId = "A2", targetId = "A1", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A2", targetId = "A2", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A2", targetId = "A1", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A2", targetId = "A2", Facing.N, Facing.N),
    ),
    "A3" to Plane(
        topLeft = Point(50, 50),
        transitionToE = Transition(sourceId = "A3", targetId = "A3", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A3", targetId = "A5", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A3", targetId = "A3", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A3", targetId = "A1", Facing.N, Facing.N),
    ),
    "A4" to Plane(
        topLeft = Point(0, 100),
        transitionToE = Transition(sourceId = "A4", targetId = "A5", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A4", targetId = "A6", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A4", targetId = "A5", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A4", targetId = "A6", Facing.N, Facing.N),
    ),
    "A5" to Plane(
        topLeft = Point(50, 100),
        transitionToE = Transition(sourceId = "A5", targetId = "A4", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A5", targetId = "A1", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A5", targetId = "A4", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A5", targetId = "A3", Facing.N, Facing.N),
    ),
    "A6" to Plane(
        topLeft = Point(0, 150),
        transitionToE = Transition(sourceId = "A6", targetId = "A6", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "A6", targetId = "A4", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "A6", targetId = "A6", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "A6", targetId = "A4", Facing.N, Facing.N),
    ),
    // == Part 2 ==============================================================
    "B1" to Plane(
        topLeft = Point(50, 0),
        transitionToE = Transition(sourceId = "B1", targetId = "B2", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "B1", targetId = "B3", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "B1", targetId = "B4", Facing.W, Facing.E),
        transitionToN = Transition(sourceId = "B1", targetId = "B6", Facing.N, Facing.E),
    ),
    "B2" to Plane(
        topLeft = Point(100, 0),
        transitionToE = Transition(sourceId = "B2", targetId = "B5", Facing.E, Facing.W),
        transitionToS = Transition(sourceId = "B2", targetId = "B3", Facing.S, Facing.W),
        transitionToW = Transition(sourceId = "B2", targetId = "B1", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "B2", targetId = "B6", Facing.N, Facing.N),
    ),
    "B3" to Plane(
        topLeft = Point(50, 50),
        transitionToE = Transition(sourceId = "B3", targetId = "B2", Facing.E, Facing.N),
        transitionToS = Transition(sourceId = "B3", targetId = "B5", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "B3", targetId = "B4", Facing.W, Facing.S),
        transitionToN = Transition(sourceId = "B3", targetId = "B1", Facing.N, Facing.N),
    ),
    "B4" to Plane(
        topLeft = Point(0, 100),
        transitionToE = Transition(sourceId = "B4", targetId = "B5", Facing.E, Facing.E),
        transitionToS = Transition(sourceId = "B4", targetId = "B6", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "B4", targetId = "B1", Facing.W, Facing.E),
        transitionToN = Transition(sourceId = "B4", targetId = "B3", Facing.N, Facing.E),
    ),
    "B5" to Plane(
        topLeft = Point(50, 100),
        transitionToE = Transition(sourceId = "B5", targetId = "B2", Facing.E, Facing.W),
        transitionToS = Transition(sourceId = "B5", targetId = "B6", Facing.S, Facing.W),
        transitionToW = Transition(sourceId = "B5", targetId = "B4", Facing.W, Facing.W),
        transitionToN = Transition(sourceId = "B5", targetId = "B3", Facing.N, Facing.N),
    ),
    "B6" to Plane(
        topLeft = Point(0, 150),
        transitionToE = Transition(sourceId = "B6", targetId = "B5", Facing.E, Facing.N),
        transitionToS = Transition(sourceId = "B6", targetId = "B2", Facing.S, Facing.S),
        transitionToW = Transition(sourceId = "B6", targetId = "B1", Facing.W, Facing.S),
        transitionToN = Transition(sourceId = "B6", targetId = "B4", Facing.N, Facing.N),
    ),
)

private val Plane.minX: Int
    get() = topLeft.x

private val Plane.maxX: Int
    get() = topLeft.x + PLANE_SIZE - 1

private val Plane.minY: Int
    get() = topLeft.y

private val Plane.maxY: Int
    get() = topLeft.y + PLANE_SIZE - 1

private operator fun Plane.contains(point: Point): Boolean =
    (point.x in minX..maxX) && (point.y in minY..maxY)

private fun Point.toLocal(plane: Plane): Point =
    Point(x - plane.topLeft.x, y - plane.topLeft.y)

private fun Point.toGlobal(plane: Plane): Point =
    Point(x + plane.topLeft.x, y + plane.topLeft.y)

private fun Point.flip(): Point =
    Point(y, x)

private val Transition.source: Plane
    get() = planes[sourceId] ?: error("Plane '$sourceId' doesn't exist.")

private val Transition.target: Plane
    get() = planes[targetId] ?: error("Plane '$targetId' doesn't exist.")

private fun Transition.findPosition(start: Point): Point =
    when (sourceFacing to targetFacing) {
        Facing.E to Facing.E -> Point(target.minX, start.toLocal(source).toGlobal(target).y)
        Facing.W to Facing.W -> Point(target.maxX, start.toLocal(source).toGlobal(target).y)
        Facing.S to Facing.S -> Point(start.toLocal(source).toGlobal(target).x, target.minY)
        Facing.N to Facing.N -> Point(start.toLocal(source).toGlobal(target).x, target.maxY)
        Facing.N to Facing.E -> Point(target.minX, start.toLocal(source).flip().toGlobal(target).y)
        Facing.S to Facing.W -> Point(target.maxX, start.toLocal(source).flip().toGlobal(target).y)
        Facing.W to Facing.E -> Point(target.minX, target.maxY - start.toLocal(source).y)
        Facing.E to Facing.W -> Point(target.maxX, target.maxY - start.toLocal(source).y)
        Facing.W to Facing.S -> Point(start.toLocal(source).flip().toGlobal(target).x, target.minY)
        Facing.E to Facing.N -> Point(start.toLocal(source).flip().toGlobal(target).x, target.maxY)
        else -> error("Transition from $sourceFacing to $targetFacing isn't defined.")
    }

private fun Facing.toOffset(): Point =
    when (this) {
        is Facing.E -> Point(+1, +0)
        is Facing.S -> Point(+0, +1)
        is Facing.W -> Point(-1, +0)
        is Facing.N -> Point(+0, -1)
    }

private fun Facing.toValue(): Int =
    when (this) {
        is Facing.E -> 0
        is Facing.S -> 1
        is Facing.W -> 2
        is Facing.N -> 3
    }

private fun solve(data: Board, start: String): Int {
    val (walls, steps) = data

    var plane = planes[start] ?: error("Plane '$start' doesn't exist.")
    var facing: Facing = Facing.E
    var position = plane.topLeft

    for (step in steps) {
        when (step) {
            is Step.TurnLeft -> facing = facing.turnLeft()
            is Step.TurnRight -> facing = facing.turnRight()
            is Step.Move -> {
                for (i in 1..step.count) {
                    val (dx, dy) = facing.toOffset()

                    var newFacing = facing
                    var newPosition = Point(position.x + dx, position.y + dy)
                    var newPlane = plane

                    if (newPosition !in plane) {
                        val transition = plane.selectTransition(facing)
                        newFacing = transition.targetFacing
                        newPosition = transition.findPosition(position)
                        newPlane = transition.target
                    }
                    if (newPosition in walls) {
                        break
                    }

                    facing = newFacing
                    position = newPosition
                    plane = newPlane
                }
            }
        }
    }

    return (1000 * (position.y + 1)) + (4 * (position.x + 1)) + facing.toValue()
}

private fun part1(data: Board): Int =
    solve(data, start = "A1")

private fun part2(data: Board): Int =
    solve(data, start = "B1")
