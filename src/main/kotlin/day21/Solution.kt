package day21

import readLines
import kotlin.math.max

// key is sum of three dice rolls, value is how many times appears that sum
val diceOutcomes = arrayOf(Pair(3, 1), Pair(4, 3), Pair(5, 6), Pair(6, 7), Pair(7, 6), Pair(8, 3), Pair(9, 1))

fun main() {
    val starting = readLines("/day21/input.txt")
        .map { it.split(" ")[4].toInt() }

    val part1 = simulateGame(starting)
    val part2 = calculateMaximumWinnings(starting)
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateMaximumWinnings(starting: List<Int>): Long {
    val memo = HashMap<String, Pair<Long, Long>>()
    val winnings = f(starting[0], starting[1], 0, 0, 0, memo)
    return max(winnings.first, winnings.second)
}


fun simulateGame(starting: List<Int>): Long {
    var dice = 1
    val scores = Array(2) { 0L }
    val positions = Array(2) { i -> starting[i] }
    var rolls = 0
    var player = 0
    while(scores[0] < 1000 && scores[1] < 1000) {
        var total = 0
        for(times in 1..3) {
            total += dice++
            rolls++
        }
        positions[player] = (positions[player] + total) % 10
        if(positions[player] == 0) {
            positions[player] = 10
        }
        scores[player] = scores[player] + positions[player]
        player = 1 - player
    }
    val losingScore = if(scores[0] < 1000) scores[0] else scores[1]
    return losingScore * rolls
}

fun f(p1: Int, p2: Int, s1: Int, s2: Int, player: Int, memo: MutableMap<String, Pair<Long, Long>>): Pair<Long, Long> {
    val memoKey = p1.toString() + p2.toString() + s1.toString() + s2.toString() + player.toString()
    if(memoKey in memo) {
        return memo.getValue(memoKey)
    }
    if(s1 >= 21) {
        memo[memoKey] = Pair(1L, 0L)
        return Pair(1L, 0L)
    }
    if(s2 >= 21) {
        memo[memoKey] = Pair(0L, 1L)
        return Pair(0L, 1L)
    }

    var wins1 = 0L
    var wins2 = 0L
    for(e in diceOutcomes) {
        var wins: Pair<Long, Long>
        if(player == 0) {
            var newP1 = (p1 + e.first) % 10
            if(newP1 == 0) {
                newP1 = 10
            }
            wins = f(newP1, p2,s1 + newP1, s2, 1 - player, memo)
        } else {
            var newP2 = (p2 + e.first) % 10
            if(newP2 == 0) {
                newP2 = 10
            }
            wins = f(p1, newP2, s1, s2 + newP2, 1 - player, memo)
        }

        wins1 += e.second*wins.first
        wins2 += e.second*wins.second
    }
    memo[memoKey] = Pair(wins1, wins2)
    return Pair(wins1, wins2)
}