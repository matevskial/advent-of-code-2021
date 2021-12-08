package day07

import readLines
import kotlin.math.abs
import kotlin.math.min

fun main() {
    val positions = readLines("/day07/input.txt")[0].split(",").map { it.toInt() }
    val part1 = calculateMinimumFuelToAlignPositions(positions, true)
    val part2 = calculateMinimumFuelToAlignPositions(positions, false)
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateMinimumFuelToAlignPositions(positions: List<Int>, isFuelAtConstantRate: Boolean): Long {
    val minPosition = positions.minOrNull()
    val maxPositions = positions.maxOrNull()

    if(minPosition == null || maxPositions == null) {
        return -1
    }

    var minFuel = Long.MAX_VALUE
    for(positionToAlign in minPosition..maxPositions) {
        var fuel = 0L
        for(position in positions) {
            if(isFuelAtConstantRate) {
                fuel += abs(position - positionToAlign).toLong()
            } else {
                fuel += calculateSum(abs(position - positionToAlign).toLong())
            }
        }
        minFuel = min(minFuel, fuel)
    }

    return minFuel
}

fun calculateSum(n: Long): Long {
    return (n*(n+1)) / 2
}