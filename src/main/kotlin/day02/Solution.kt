package day02

import readLines

fun main() {
    val lines = readLines("/day02/input.txt")

    val part1 = multiplyFinalPositionsPart1(lines)
    println("part1: $part1")

    val part2 = multiplyFinalPositionsPart2(lines)
    println("part2: $part2")
}

fun multiplyFinalPositionsPart1(submarineMoves: List<String>): Int {
    val position = mutableMapOf("forward" to 0, "up" to 0, "down" to 0)
    for(move in submarineMoves) {
        val direction = move.split(" ")[0]
        val value = move.split(" ")[1].toInt()
        position[direction] = position.getValue(direction).plus(value)
    }
    val depth = position.getValue("down") - position.getValue("up")
    return position.getValue("forward") * depth
}

fun multiplyFinalPositionsPart2(submarineMoves: List<String>): Long {
    val position = mutableMapOf("forward" to 0L, "depth" to 0L, "up" to 0L, "down" to 0L)
    for(move in submarineMoves) {
        val direction = move.split(" ")[0]
        val value = move.split(" ")[1].toLong()
        if(direction == "forward") {
            position[direction] = position.getValue(direction).plus(value)
            val currAim = position.getValue("down") - position.getValue("up")
            position["depth"] = position.getValue("depth") + currAim * value
        } else {
            position[direction] = position.getValue(direction).plus(value)
        }
    }
    return position.getValue("forward") * position.getValue("depth")
}