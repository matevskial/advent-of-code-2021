package day01

import readLinesAsInt

fun main() {
    val measurement = readLinesAsInt("/day01/input.txt")

    val part1 = countIncreases(measurement)
    println("part1: $part1")

    val part2 = countIncreasesOfSlidingWindows(measurement)
    println("part2: $part2")
}

fun countIncreasesOfSlidingWindows(measurement: List<Int>): Int {
    var prevSlidingSum: Int? = null
    var increases = 0
    for(i in 0..measurement.size - 3) {
        val slidingSum  = measurement[i] + measurement[i + 1] + measurement[i + 2]
        if(prevSlidingSum != null && slidingSum > prevSlidingSum) {
            increases++
        }
        prevSlidingSum = slidingSum
    }
    return increases
}

fun countIncreases(measurement: List<Int>): Int {
    var increases = 0
    measurement.forEachIndexed { i, currMeasure ->
        if (i > 0 && currMeasure > measurement[i - 1]) {
            increases++
        }
    }
    return increases
}
