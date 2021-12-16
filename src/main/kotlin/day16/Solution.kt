package day16

import readLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    val binary = hexadecimalToBinary(readLines("/day16/input.txt")[0])
    val packet = getPacketFromBinary(binary)

    val part1 = packet.getVersionsSum()
    val part2 = packet.evaluate()
    println("part1: $part1")
    println("part2: $part2")
}

fun getPacketFromBinary(binary: String): Packet {
    return getPacketFromBinaryRec(binary, 0)
}

fun getPacketFromBinaryRec(binary: String, pos: Int): Packet {
    val version = binary.slice(pos until pos + 3)
    val type = binary.slice(pos + 3 until pos + 6)
    var numberOfBits = 6
    if(type == "100") {
        val value = ArrayList<Char>()
        var isFirstValueGroup = true
        var valueGroupPrefix = '0'
        var valueGroupPosOffset = 0
        while(isFirstValueGroup || valueGroupPrefix == '1') {
            isFirstValueGroup = false
            val group = binary.slice(pos + 6 + valueGroupPosOffset until pos + 6 + valueGroupPosOffset + 5)
            valueGroupPosOffset += 5
            valueGroupPrefix = group[0]
            value.addAll(group.drop(1).toList())
            numberOfBits += 5
        }

        return ValuePacket(version, type, value.joinToString(""), numberOfBits)
    } else {
        val lengthType = binary[pos + 6]
        val lengthBits = if(lengthType == '0') 15 else 11
        numberOfBits += lengthBits + 1
        val length = binary.slice(pos + 7 until pos + 7 + lengthBits).toInt(2)
        var isFirstSubPacket = true
        var numberOfSubPackets = 0
        var subPacketOffset = 0
        val subPackages = ArrayList<Packet>()
        while(isFirstSubPacket || !isNumberOfSubPacketsReached(lengthType, length, numberOfSubPackets)
            || !isNumberOfBitsReached(lengthType, length, subPacketOffset)) {
            isFirstSubPacket = false

            val subPacket = getPacketFromBinaryRec(binary, pos + 7 + lengthBits + subPacketOffset)
            subPackages.add(subPacket)

            subPacketOffset += subPacket.getTotalNumberOfBits()
            numberOfSubPackets++
            numberOfBits += subPacket.getTotalNumberOfBits()
        }

        return OperatorPacket(version, type, lengthType, length, numberOfBits, subPackages)
    }

}

fun isNumberOfBitsReached(lengthType: Char, expectedNumberOfBits: Int, numberOfBits: Int): Boolean {
    return lengthType != '0' || expectedNumberOfBits == numberOfBits
}

fun isNumberOfSubPacketsReached(lengthType: Char, expectedNumberOfSubPackets: Int, numberOfSubPackets: Int): Boolean {
    return lengthType != '1' || expectedNumberOfSubPackets == numberOfSubPackets
}

fun hexadecimalToBinary(hexadecimal: String): String {
    return hexadecimal.map { it.digitToInt(16).toString(2) }
        .joinToString("") { it.padStart(4, '0') }
}

interface Packet {
    fun getVersionsSum(): Int
    fun getTotalNumberOfBits(): Int
    fun evaluate(): Long
}

class ValuePacket(val version: String, val type: String, val value: String, val numberOfBits: Int) : Packet {

    override fun getVersionsSum(): Int {
        return version.toInt(2)
    }

    override fun getTotalNumberOfBits(): Int {
        return numberOfBits
    }

    override fun evaluate(): Long {
        return value.toLong(2)
    }

}

class OperatorPacket(val version: String, val type: String, val lengthType: Char, val length: Int, val numberOfBits: Int, val subPackages: List<Packet>) : Packet {

    private val defaultValue = mapOf("+" to 0L, "*" to 1L, "min" to Long.MAX_VALUE, "max" to Long.MIN_VALUE)

    override fun getVersionsSum(): Int {
        var sum = version.toInt(2)
        for(p in subPackages) {
            sum += p.getVersionsSum()
        }

        return sum
    }

    override fun getTotalNumberOfBits(): Int {
        return numberOfBits
    }

    override fun evaluate(): Long {
        return when(type) {
            "000" -> multinaryOperation("+")
            "001" -> multinaryOperation("*")
            "010" -> multinaryOperation("min")
            "011" -> multinaryOperation("max")
            "101" -> binaryOperation("greaterThan")
            "110" -> binaryOperation("lessThan")
            "111" -> binaryOperation("equalTo")
            else -> -1L
        }
    }

    private fun multinaryOperation(op: String): Long {
        var result = defaultValue.getValue(op)
        for(p in subPackages) {
            when(op) {
                "+" -> result += p.evaluate()
                "*" -> result *= p.evaluate()
                "min" -> result = min(result, p.evaluate())
                "max" -> result = max(result, p.evaluate())
            }
        }
        return result
    }

    private fun binaryOperation(op: String): Long {
        val v1 = subPackages[0].evaluate()
        val v2 = subPackages[1].evaluate()
        var result = false
        when(op) {
            "greaterThan" -> result = v1 > v2
            "lessThan" -> result = v1 < v2
            "equalTo" -> result = v1 == v2
        }
        return if(result) 1L else 0L
    }
}
