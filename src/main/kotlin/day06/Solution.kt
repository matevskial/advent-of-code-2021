package day06

import readLines

fun main() {
    val lanternfishes = readLines("/day06/input.txt")[0].split(",").map { it.toInt() }
    val part1 = simulateLanternfish(lanternfishes, 80)
    val part2 = simulateLanternfish(lanternfishes, 256)
    println("part1: $part1")
    println("part2: $part2")
}

fun simulateLanternfish(lanternfishes: List<Int>, days: Int): Long {
    val timerLanternfishCount = Array(9) { 0L }
    for(l in lanternfishes) {
        timerLanternfishCount[l]++
    }
    for(d in 1..days) {
        val oldZeroTimeCount = timerLanternfishCount[0]
        for(t in 0..7) {
            val larger = t + 1
            timerLanternfishCount[t] = timerLanternfishCount[larger]
        }
        timerLanternfishCount[8] = oldZeroTimeCount
        timerLanternfishCount[6] += oldZeroTimeCount
    }

    return timerLanternfishCount.sum()
}
