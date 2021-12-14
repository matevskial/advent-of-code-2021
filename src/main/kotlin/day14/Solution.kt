package day14

import readLines

fun main() {
    val lines = readLines("/day14/input.txt")
    val polymerTemplate = lines[0]
    val rules = lines.drop(2).map { it.split(" -> ") }.associate { Pair(it[0], it[1][0]) }

    val part1 = simulateSteps(polymerTemplate, rules, 10)
    val part2 = simulateSteps(polymerTemplate, rules, 40)
    println("part1: $part1")
    println("part2: $part2")
}

fun simulateSteps(polymerTemplate: String, rules: Map<String, Char>, steps: Int): Long {
    val pairs = HashMap<String, Long>()
    for(i in 0 until polymerTemplate.length - 1) {
        val key = polymerTemplate[i].toString() + polymerTemplate[i + 1]
        pairs[key] = pairs.getOrDefault(key, 0L) + 1
    }

    for(s in 1..steps) {
        val newPairs = HashMap<String, Long>()
        for (entry in pairs) {
            val pair = entry.key
            val c = rules[pair]
            val count = entry.value
            val pair1 = pair[0].toString() + c
            val pair2 = c.toString() + pair[1]
            newPairs[pair1] = newPairs.getOrDefault(pair1, 0L) + count
            newPairs[pair2] = newPairs.getOrDefault(pair2, 0L) + count
            pairs[pair] = 0L
        }

        for(entry in newPairs) {
            pairs[entry.key] = pairs.getOrDefault(entry.key, 0L) + entry.value
        }
    }

    val countsByChar = pairs.keys.map { it.toList() }.flatten().distinct()
        .associateWith { countOccurrencesOfChar(it, pairs, polymerTemplate) }

    val maxCount = countsByChar.maxOf { it.value }
    val minCount = countsByChar.minOf { it.value }

    return maxCount - minCount
}

fun countOccurrencesOfChar(c: Char, pairs: java.util.HashMap<String, Long>, polymerTemplate: String): Long {
    var count = 0L
    for(entry in pairs) {
        if(entry.key[1] == c) {
            count += entry.value
        }
    }

    if(polymerTemplate.first() == c || polymerTemplate.last() == c) {
        count++
    }

    return count
}
