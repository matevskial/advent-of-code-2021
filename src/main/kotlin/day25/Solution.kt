package day25

import readLines

fun main() {
    val seaFloor = readLines("day25/input.txt")
        .map { it.toCharArray() }.toTypedArray()

    val part1 = calculateStepOnWhichNoMoveOccurs(seaFloor)
    println("part1: $part1")
}

fun calculateStepOnWhichNoMoveOccurs(seaFloor: Array<CharArray>): Int {
    var step = 1
    while(true) {
        val movedEast = move(seaFloor, '>')
        val movedSouth = move(seaFloor, 'v')

        if(!movedEast && !movedSouth) {
            return step
        }
        step++
    }
}

fun move(seaFloor: Array<CharArray>, c: Char): Boolean {
    var moved = false
    val cellsToChange = ArrayList<Pair<Int, Int>>()
    for(row in seaFloor.indices) {
        for(col in seaFloor[row].indices) {
            if(seaFloor[row][col] == c) {
                val nextRow = if(c == '>') row else (row + 1) % seaFloor.size
                val nextCol = if(c == '>') (col + 1) % seaFloor[0].size else col
                if(seaFloor[nextRow][nextCol] == '.') {
                    cellsToChange.add(Pair(row, col))
                    moved = true
                }
            }
        }
    }
    for(cell in cellsToChange) {
        val row = cell.first
        val col = cell.second
        val nextRow = if(c == '>') row else (row + 1) % seaFloor.size
        val nextCol = if(c == '>') (col + 1) % seaFloor[0].size else col
        seaFloor[row][col] = '.'
        seaFloor[nextRow][nextCol] = c
    }
    return moved
}
