package day08

import readLines

val segmentToDigit = mapOf(
    "abcefg" to 0, "cf" to 1, "acdeg" to 2, "acdfg" to 3,
    "bcdf" to 4, "abdfg" to 5, "abdefg" to 6, "acf" to 7,
    "abcdefg" to 8, "abcdfg" to 9
)

val segments = "abcdefg".toCharArray().toList()
val  permutations = generatePermutations(segments)

fun main() {
    val displays = readLines("/day08/input.txt").map { it.split(" | ") }
        .map { Pair(it[0].split(" "), it[1].split(" ")) }

    val part1 = countDigitsWithUniqueNumberOfSegments(displays)
    val part2 = calculateSumOfDisplayedValues(displays)
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateSumOfDisplayedValues(displays: List<Pair<List<String>, List<String>>>): Int {
    var sum = 0
    for(display in displays) {
        sum += calculateDisplayValue(display)
    }
    return sum
}

fun calculateDisplayValue(display: Pair<List<String>, List<String>>): Int {
    for(permutation in permutations) {
        if(isValidPermutation(permutation, display.first)) {
            var displayedValue = 0
            for(digit in display.second) {
                val digitInSegmentForm = digit.map { permutation[segments.indexOf(it)] }.sorted().joinToString("")
                displayedValue = displayedValue * 10 + segmentToDigit.getValue(digitInSegmentForm)
            }
            return displayedValue
        }
    }

    return -1
}

fun isValidPermutation(permutation: List<Char>, digitsInSegmentForm: List<String>): Boolean {
    for(digit in digitsInSegmentForm) {
        val digitInSegmentForm = digit.map { permutation[segments.indexOf(it)] }.sorted().joinToString("")
        if(!segmentToDigit.contains(digitInSegmentForm)) {
            return false
        }
    }
    return true
}

fun <T> generatePermutations(elements: List<T>): List<List<T>> {
    val permutations = ArrayList<List<T>>()
    val usedElements = Array(elements.size) { false }
    val permutation = ArrayList<T>()
    generatePermutationsRec(permutations, elements, usedElements, permutation)
    return permutations
}

fun <T> generatePermutationsRec(permutations: ArrayList<List<T>>, elements: List<T>, usedElements: Array<Boolean>, permutation: ArrayList<T>) {
    if(permutation.size == elements.size) {
        permutations.add(ArrayList(permutation))
        return
    }

    for(i in elements.indices) {
        if(usedElements[i]) {
            continue
        }

        usedElements[i] = true
        permutation.add(elements[i])
        generatePermutationsRec(permutations, elements, usedElements, permutation)
        usedElements[i] = false
        permutation.removeLast()
    }
}

fun countDigitsWithUniqueNumberOfSegments(displays: List<Pair<List<String>, List<String>>>): Int {
    var count = 0
    val uniqueSegmentCount = setOf(2, 3, 4, 7)
    for(display in displays) {
        for(digit in display.second) {
            if(digit.length in uniqueSegmentCount) {
                count++
            }
        }
    }

    return count
}
