package day03

import readLines

fun main() {
    val binarySequences = readLines("/day03/input.txt")
    val gammaRate = calculateRate(binarySequences, 0)
    val epsilonRate = calculateRate(binarySequences, 1)
    val part1 = gammaRate * epsilonRate
    println("part1: $part1")

    val oxygenRating = calculateRatePart2(binarySequences, 0)
    val co2Rating = calculateRatePart2(binarySequences, 1)
    val part2 = oxygenRating * co2Rating
    println("part2: $part2")
}

fun calculateRatePart2(_binarySequences: List<String>, rate: Int): Int {
    var binarySequences = _binarySequences
    val seqLen = binarySequences[0].length
    for(i in 0 until seqLen) {
        if(binarySequences.size < 2) {
            break
        }
        val digit = calculateDigit(binarySequences, i, rate)
        binarySequences = binarySequences.filter { seq -> seq[i] == digit }
    }
    return binarySequences[0].toInt(2)
}

fun calculateRate(binarySequences: List<String>, rate: Int): Int {
    val seqLen = binarySequences[0].length
    val rateSeq = CharArray(seqLen) {'x'}
    for(i in 0 until seqLen) {
        rateSeq[i] = calculateDigit(binarySequences, i, rate)
    }
    return rateSeq.concatToString().toInt(2)
}

fun calculateDigit(binarySequences: List<String>, position:Int, rate: Int): Char {
    val countDigits = mutableMapOf('0' to 0, '1' to 0)
    for(seq in binarySequences) {
        countDigits[seq[position]] = countDigits.getValue(seq[position]) + 1
    }

    return when(rate) {
        0 -> if(countDigits.getValue('1') >= countDigits.getValue('0')) '1' else '0'
        1 -> if(countDigits.getValue('0') <= countDigits.getValue('1')) '0' else '1'
        else -> 'x'
    }
}