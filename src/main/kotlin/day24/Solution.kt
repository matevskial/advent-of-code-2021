package day24

import readLines
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// defaults, for my input
var firstConstantByStep = arrayOf<Long>(12, 12, 12, -9, -9, 14, 14, -10, 15, -2, 11, -15, -9, -3)
var secondConstantByStep = arrayOf<Long>(9, 4, 2, 5, 1, 6, 11, 15, 7, 12, 15, 9, 12, 12)
var divisorOfZByStep = arrayOf<Long>(1, 1, 1, 26, 26, 1, 1, 26, 1, 26, 1, 26, 26, 26)
var limitOfZByStep = arrayOf(7, 7, 7, 6, 5, 5, 5, 4, 4, 3, 3, 2, 1, 0)
var constantDivisorOfZ = 26.0

fun main() {
    val millis = System.currentTimeMillis()
    val monad = readLines("/day24/input.txt")

    val result = calculateMinimumAndMaximumModelNumber(monad)

    println("part1: ${result.first}")
    println("part2: ${result.second}")
    val timeInSeconds = (System.currentTimeMillis() - millis) / 1000.0
    println("Time: $timeInSeconds")
}

// assuming structure of monad is similar for all inputs
fun calculateConstantsOfMonad(monad: List<String>) {
    val groups = monad.chunked(18)
    for(e in groups.withIndex()) {
        val divisorOfZ = e.value[4].split(" ")[2].toLong()
        val constant = e.value[5].split(" ")[2].toLong()
        val secondConstant = e.value[15].split(" ")[2].toLong()

        divisorOfZByStep[e.index] = divisorOfZ
        constantDivisorOfZ = if(divisorOfZ != 1L) divisorOfZ.toDouble() else constantDivisorOfZ
        firstConstantByStep[e.index] = constant
        secondConstantByStep[e.index] = secondConstant
    }
    var count = 0
    for(i in divisorOfZByStep.indices.reversed()) {
        limitOfZByStep[i] = count
        if(divisorOfZByStep[i].toDouble() == constantDivisorOfZ) {
            count++
        }

    }
}

fun calculateMinimumAndMaximumModelNumber(monad: List<String>): Pair<Long, Long> {

    calculateConstantsOfMonad(monad)

    var minModelNumber = Long.MAX_VALUE
    var maxModelNumber = Long.MIN_VALUE

    fun calculateMinimumAndMaximumModelNumberRec(modelNumber: String, z: Long) {
        if(modelNumber.length == 14) {
            val modelNumberLong = modelNumber.toLong()
            if(z == 0L) {
                minModelNumber = min(minModelNumber, modelNumberLong)
                maxModelNumber = max(maxModelNumber, modelNumberLong)
            }
            return
        }
        val step = modelNumber.length
        var ok = false
        for(w in 9L downTo 1L) {
            val newZ = calculateStep(z, w, step)
            if(newZ in 0..25) {
                ok = true
                calculateMinimumAndMaximumModelNumberRec(modelNumber + w, newZ)
            }
        }
        if(!ok) {
            for(w in 9L downTo 1L) {
                val newZ = calculateStep(z, w, step)
                val limit = constantDivisorOfZ.pow(limitOfZByStep[step]).toLong()
                if(newZ <= limit) {
                    calculateMinimumAndMaximumModelNumberRec(modelNumber + w, newZ)
                }
            }
        }
    }

    calculateMinimumAndMaximumModelNumberRec("", 0)
    return Pair(maxModelNumber, minModelNumber)
}

fun calculateStep(z: Long, w: Long, step: Int): Long {
    val x = if(((z % 26) + firstConstantByStep[step]) != w) 1L else 0L
    return (z / divisorOfZByStep[step]) * (25 * x + 1) + (w + secondConstantByStep[step]) * x
}
