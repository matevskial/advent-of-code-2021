package day10

import readLines

val errorScoresByClosingBrackets = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137, 'x' to 0)
val autocompleteScoresByClosingBrackets = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)
val matchingBracket = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')

fun main() {
    val lines = readLines("/day10/input.txt")
    val scores = lines.map { calculateScores(it) }
    val part1 = scores.sumOf { it.first }

    val autoCompleteScores = scores.map { it.second }.filter { it != 0L }.sorted()
    val part2 = autoCompleteScores[autoCompleteScores.size / 2]

    println("part1: $part1")
    println("part2: $part2")
}

fun calculateScores(line: String): Pair<Int, Long> {
    val currOpenedBrackets = ArrayDeque<Char>()
    for(c in line) {
        if (c in matchingBracket.keys) {
            currOpenedBrackets.addFirst(c)
        } else if(currOpenedBrackets.isEmpty() || c != matchingBracket.getValue(currOpenedBrackets.first())) {
            return Pair(errorScoresByClosingBrackets.getValue(c), 0L)
        } else {
            currOpenedBrackets.removeFirst()
        }
    }
    var autoCompleteScore = 0L
    while(currOpenedBrackets.isNotEmpty()) {
        val closingBracket = matchingBracket.getValue(currOpenedBrackets.first())
        autoCompleteScore = autoCompleteScore*5 + autocompleteScoresByClosingBrackets.getValue(closingBracket)
        currOpenedBrackets.removeFirst()
    }
    return Pair(0, autoCompleteScore)
}
