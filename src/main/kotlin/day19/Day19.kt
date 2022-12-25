package day19

import java.io.File
import kotlin.math.ceil

fun main() {
    val data = parse("src/main/kotlin/day19/Day19.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 19 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private fun String.toBlueprints(): Blueprints {
    val tokens = split(": ", " ")
    return Blueprints(
        id = tokens[1].toInt(),
        oreRobot = RobotBlueprint(
            requiredOre = tokens[6].toInt(),
        ),
        clayRobot = RobotBlueprint(
            requiredOre = tokens[12].toInt(),
        ),
        obsidianRobot = RobotBlueprint(
            requiredOre = tokens[18].toInt(),
            requiredClay = tokens[21].toInt(),
        ),
        geodeRobot = RobotBlueprint(
            requiredOre = tokens[27].toInt(),
            requiredObsidian = tokens[30].toInt(),
        ),
    )
}

@Suppress("SameParameterValue")
private fun parse(path: String): List<Blueprints> =
    File(path).readLines().map(String::toBlueprints)

private data class RobotBlueprint(
    val requiredOre: Int = 0,
    val requiredClay: Int = 0,
    val requiredObsidian: Int = 0,
)

private data class BuildEffect(
    val builtOreRobots: Int = 0,
    val builtClayRobots: Int = 0,
    val builtGeodeRobots: Int = 0,
    val builtObsidianRobots: Int = 0,
)

private data class Blueprints(
    val id: Int,
    val oreRobot: RobotBlueprint,
    val clayRobot: RobotBlueprint,
    val geodeRobot: RobotBlueprint,
    val obsidianRobot: RobotBlueprint,
)

private data class State(
    val blueprints: Blueprints,
    val minutesLeft: Int = 0,

    val orePerMinute: Int = 1,
    val clayPerMinute: Int = 0,
    val geodePerMinute: Int = 0,
    val obsidianPerMinute: Int = 0,

    val oreCount: Int = 0,
    val clayCount: Int = 0,
    val geodeCount: Int = 0,
    val obsidianCount: Int = 0,
)

private val Blueprints.robots
    get() = listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot)

private val Blueprints.maxOre
    get() = robots.maxOf(RobotBlueprint::requiredOre)

private val Blueprints.maxClay
    get() = robots.maxOf(RobotBlueprint::requiredClay)

private val Blueprints.maxObsidian
    get() = robots.maxOf(RobotBlueprint::requiredObsidian)

private fun State.schedule(blueprint: RobotBlueprint, effect: BuildEffect): State {
    val (requiredOre, requiredClay, requiredObsidian) = blueprint

    val minutesForOre = if (requiredOre > oreCount)
        ceil((requiredOre - oreCount) / orePerMinute.toDouble()).toInt()
    else
        0

    val minutesForClay = if (requiredClay > clayCount)
        ceil((requiredClay - clayCount) / clayPerMinute.toDouble()).toInt()
    else
        0

    val minutesForObsidian = if (requiredObsidian > obsidianCount)
        ceil((requiredObsidian - obsidianCount) / obsidianPerMinute.toDouble()).toInt()
    else
        0

    val minutesRequired = 1 + maxOf(
        minutesForOre,
        minutesForClay,
        minutesForObsidian,
    )

    return this.copy(
        minutesLeft = minutesLeft - minutesRequired,

        oreCount = oreCount + orePerMinute * minutesRequired - requiredOre,
        clayCount = clayCount + clayPerMinute * minutesRequired - requiredClay,
        obsidianCount = obsidianCount + obsidianPerMinute * minutesRequired - requiredObsidian,
        geodeCount = geodeCount + geodePerMinute * minutesRequired,

        orePerMinute = orePerMinute + effect.builtOreRobots,
        clayPerMinute = clayPerMinute + effect.builtClayRobots,
        geodePerMinute = geodePerMinute + effect.builtGeodeRobots,
        obsidianPerMinute = obsidianPerMinute + effect.builtObsidianRobots,
    )
}

private fun State.scheduleOreRobot(): State =
    schedule(blueprints.oreRobot, BuildEffect(builtOreRobots = 1))

private fun State.scheduleClayRobot(): State =
    schedule(blueprints.clayRobot, BuildEffect(builtClayRobots = 1))

private fun State.scheduleObsidianRobot(): State =
    schedule(blueprints.obsidianRobot, BuildEffect(builtObsidianRobots = 1))

private fun State.scheduleGeodeRobot(): State =
    schedule(blueprints.geodeRobot, BuildEffect(builtGeodeRobots = 1))

private fun State.findTransitions(): List<State> {
    val transitions = mutableListOf<State>()
    if (orePerMinute < blueprints.maxOre) {
        transitions += scheduleOreRobot()
    }
    if (clayPerMinute < blueprints.maxClay) {
        transitions += scheduleClayRobot()
    }
    if (obsidianPerMinute < blueprints.maxObsidian && clayPerMinute > 0) {
        transitions += scheduleObsidianRobot()
    }
    if (obsidianPerMinute > 0) {
        transitions += scheduleGeodeRobot()
    }
    return transitions.filter { it.minutesLeft >= 0 }
}

private fun State.mayOutperform(maximumGeodes: Int): Boolean =
    geodeCount + minutesLeft * (minutesLeft + 1) / 2 + geodePerMinute * minutesLeft > maximumGeodes

private fun Blueprints.countGeodes(minutesLeft: Int): Int {
    val queue = ArrayDeque<State>()
    queue.add(State(this, minutesLeft))

    var maximumGeodes = 0
    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()
        if (state.mayOutperform(maximumGeodes)) {
            queue.addAll(state.findTransitions())
            maximumGeodes = maxOf(maximumGeodes, state.geodeCount + state.geodePerMinute * state.minutesLeft)
        }
    }

    return maximumGeodes
}

private fun part1(data: List<Blueprints>): Int =
    data.sumOf { it.countGeodes(minutesLeft = 24) * it.id }

private fun part2(data: List<Blueprints>): Int =
    data.take(3).map { it.countGeodes(minutesLeft = 32) }.reduce(Int::times)
