package day05

import readLines
import kotlin.math.abs

fun main() {
    val lines = readLines("/day05/input.txt")
    val startingPoints = lines
        .map { it.split(" -> ")[0].split(",").map { c -> c.toInt() } }
    val endingPoints = lines
        .map { it.split(" -> ")[1].split(",").map { c -> c.toInt() } }

    val (part1, part2) = countPointsOfOverlap(startingPoints, endingPoints)
    println("part1: $part1")
    println("part2: $part2")
}

fun countPointsOfOverlap(startingPoints: List<List<Int>>, endingPoints: List<List<Int>>): Pair<Int, Int> {
    val pointsPart1 = HashMap<Pair<Int, Int>, Int>()
    val pointsPart2 = HashMap<Pair<Int, Int>, Int>()
    val numOfPoints = startingPoints.size
    for (i in 0 until numOfPoints) {
        val start = Pair(startingPoints[i][0], startingPoints[i][1])
        val end = Pair(endingPoints[i][0], endingPoints[i][1])

        if (start.first == end.first || start.second == end.second
            || abs(start.first - end.first) == abs(start.second - end.second)) {
            var current = start
            var pointsMeet = current == end
            while (!pointsMeet) {
                if (start.first == end.first || start.second == end.second) {
                    pointsPart1.merge(current, 1) { _, v -> v + 1 }
                }
                pointsPart2.merge(current, 1) { _, v -> v + 1 }
                pointsMeet = current == end
                current = getNextPoint(current, start, end)
            }
        }
    }

    return Pair(pointsPart1.count { entry -> entry.value > 1 }, pointsPart2.count { entry -> entry.value > 1 })
}

fun getNextPoint(current: Pair<Int, Int>, start: Pair<Int, Int>, end: Pair<Int, Int>): Pair<Int, Int> {
    val change = mapOf(true to 1, false to -1)
    if (start.first == end.first) {
        return Pair(start.first, current.second + change.getValue(start.second < end.second))
    } else if (start.second == end.second) {
        return Pair(current.first + change.getValue(start.first < end.first), start.second)
    } else if (abs(start.first - end.first) == abs(start.second - end.second)) {
        return Pair(
            current.first + change.getValue(start.first < end.first),
            current.second + change.getValue(start.second < end.second)
        )
    }
    return Pair(-1, -1)
}