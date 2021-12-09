package day08

import readLines

val segmentToDigit = mapOf(
    "abcefg" to 0, "cf" to 1, "acdeg" to 2, "acdfg" to 3,
    "bcdf" to 4, "abdfg" to 5, "abdefg" to 6, "acf" to 7,
    "abcdefg" to 8, "abcdfg" to 9
)

val segmentsWithUniqueLength = listOf("cf", "acf", "bcdf", "abcdefg")

val segments = "abcdefg".toCharArray().toList()
val  permutations = generatePermutations(segments)

fun main() {
    val displays = readLines("/day08/input.txt").map { it.split(" | ") }
        .map { Pair(it[0].split(" "), it[1].split(" ")) }

    val part1 = countDigitsWithUniqueNumberOfSegments(displays)
    var currTimeMillis = System.currentTimeMillis()
    val part2 = calculateSumOfDisplayedValues(displays)
    println("part2 with bruteforce solved in: ${ (System.currentTimeMillis() - currTimeMillis) / 1000.0 }")
    currTimeMillis = System.currentTimeMillis()
    val part2WithDeduction = calculateSumOfDisplayedValuesWithDeduction(displays)
    println("part2 with deduction solved in: ${ (System.currentTimeMillis() - currTimeMillis) / 1000.0 }")
    println("part1: $part1")
    println("part2: $part2")
    println("part2 with deduction: $part2WithDeduction")
}

fun calculateSumOfDisplayedValuesWithDeduction(displays: List<Pair<List<String>, List<String>>>): Int {
    var sum = 0
    for(display in displays) {
        sum += calculateDisplayedValueWithDeduction(display)
    }
    return sum
}

fun calculateDisplayedValueWithDeduction(display: Pair<List<String>, List<String>>): Int {
    val patterns = display.first
    val outputs = display.second
    val patternsToProcess = ArrayList<String>()
    for (pattern in patterns) {
        if(pattern.length == 2 || pattern.length == 3 || pattern.length == 4 || pattern.length == 7) {
            patternsToProcess.add(pattern)
        }
    }
    patternsToProcess.sortWith { a, b -> a.length.compareTo(b.length) }
    val signalToSegment = HashMap<Char, Char>()
    decodeSignals(signalToSegment, patternsToProcess, 0, ArrayList(), patterns)
    var displayedValue = 0
    for(output in outputs) {
        val decodedOutput = output.toList().map { signalToSegment.getValue(it) }.sorted().joinToString("")
        if(!segmentToDigit.contains(decodedOutput)) {
            println(output)
        }
        displayedValue = displayedValue * 10 + segmentToDigit.getValue(decodedOutput)
    }
    return displayedValue
}

fun decodeSignals(signalToSegment: HashMap<Char, Char>, patternsToProcess: ArrayList<String>, pos: Int, foundValidDecoding: ArrayList<Boolean>, patterns: List<String>) {
    if(pos == patternsToProcess.size) {
        if(isValidDecoding(signalToSegment, patterns)) {
            foundValidDecoding.add(true)
        }

        return
    }

    if(foundValidDecoding.size > 0) {
        return
    }

    val signalsToDecode = patternsToProcess[pos].filter { it !in signalToSegment.keys }.toList()
    val possibleDecodedValues = segmentsWithUniqueLength[pos].filter { it !in signalToSegment.values }.toList()
    val permutations = generatePermutations(possibleDecodedValues)

    if(possibleDecodedValues.size != signalsToDecode.size) {
        return
    }

    for(p in permutations) {
        for(i in signalsToDecode.indices) {
            signalToSegment[signalsToDecode[i]] = p[i]
        }
        decodeSignals(signalToSegment, patternsToProcess, pos + 1, foundValidDecoding, patterns)
        if(foundValidDecoding.size > 0) {
            return
        }
        for(s in signalsToDecode) {
            signalToSegment.remove(s)
        }
    }
}

fun isValidDecoding(signalToSegment: HashMap<Char, Char>, patterns: List<String>): Boolean {
    for(segment in patterns) {
        val decodedSegment = segment.toList().map { signalToSegment.getValue(it) }.sorted().joinToString("")
        if(!segmentToDigit.containsKey(decodedSegment)) {
            return false
        }
    }
    return true
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
