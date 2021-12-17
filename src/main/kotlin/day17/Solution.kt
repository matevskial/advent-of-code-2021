package day17

import readLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val targetAreaRange = readLines("/day17/input.txt")[0].dropWhile { it != 'x' }.split(", ")
        .map { it.drop(2).split("..") }.map { Pair(it[0].toInt(), it[1].toInt()) }

    val result = solveBothParts(targetAreaRange)
    println("part1: ${result.first}")
    println("part2: ${result.second}")
}

fun solveBothParts(targetAreaRange: List<Pair<Int, Int>>): Pair<Int, Int> {
    val minX = min(targetAreaRange[0].first, targetAreaRange[0].second)
    val maxX = max(targetAreaRange[0].first, targetAreaRange[0].second)
    val minY = min(targetAreaRange[1].first, targetAreaRange[1].second)
    val maxY = max(targetAreaRange[1].first, targetAreaRange[1].second)
    val maxOfAll = maxOf(abs(minY), abs(maxY), abs(minX), abs(maxX))
    var count = 0
    var resultY = Int.MIN_VALUE
    for(x in 0..maxOfAll) {
        for(y in minY..maxOfAll) {
            var velocityX = x
            var velocityY = y
            var currX = 0
            var currY = 0
            while(true) {
                currX += velocityX
                currY += velocityY
                if(currX in minX..maxX && currY in minY..maxY) {
                    resultY = max(resultY, y)
                    count++
                    break
                } else if(currY < minY) {
                    break
                }

                velocityX = max(0, velocityX - 1)
                velocityY--
            }
        }
    }
    return Pair(sumOfArithmeticProgression(resultY, -1, resultY), count)
}

fun sumOfArithmeticProgression(firstTerm: Int, commonDifference: Int, numberOfTerms: Int): Int {
    return ((numberOfTerms / 2.0) * (2 * firstTerm + (numberOfTerms - 1) * commonDifference)).toInt()
}
