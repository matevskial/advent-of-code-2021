package day04

import readLines

fun main() {
    val lines = readLines("/day04/input.txt")
    val drawnNumbers = lines[0].split(",").map { it.toInt() }
    val boards = ArrayList<ArrayList<ArrayList<Pair<Int, Boolean>>>>()
    for (i in 2 until lines.size step 6) {
        boards.add(ArrayList())
        val board = boards.last()
        for (j in 0 until 5) {
            val row = ArrayList(lines[i + j].trim().split(Regex("\\s+")).map { it.toInt() }
                .map { Pair(it, false) })
            board.add(row)
        }
    }

    val result = solveBothParts(boards, drawnNumbers)
    println("part1: ${result.first}")
    println("part2: ${result.second}")
}

fun solveBothParts(boards: ArrayList<ArrayList<ArrayList<Pair<Int, Boolean>>>>, drawnNumbers: List<Int>): Pair<Int, Int> {
    var part1 = -1
    var part2 = -1
    val prevWinningBoards = HashSet<Int>()
    for(n in drawnNumbers) {
        mark(boards, n)
        val winningBoards = getWinningBoards(boards, prevWinningBoards)
        prevWinningBoards.addAll(winningBoards)
        if(winningBoards.isEmpty()) {
            continue
        }
        if(part1 == -1) {
            part1 = n * getWinningBoardSum(boards[winningBoards.first()])
        }
        part2 = n * getWinningBoardSum(boards[winningBoards.last()])
    }

    return Pair(part1, part2)
}

fun mark(boards: ArrayList<ArrayList<ArrayList<Pair<Int, Boolean>>>>, n:Int) {
    for(board in boards) {
        for(row in board) {
            for(rowIndex in 0 until row.size) {
                val e = row[rowIndex]
                if(e.first == n) {
                    row[rowIndex] = Pair(e.first, true)
                }
            }
        }
    }
}

fun getWinningBoards(boards: ArrayList<ArrayList<ArrayList<Pair<Int, Boolean>>>>, prevWinningBoards: Set<Int>): List<Int> {
    val winningBoards = ArrayList<Int>()
    for(i in 0 until boards.size) {
        if(!prevWinningBoards.contains(i) && isWinningBoard(boards[i])) {
            winningBoards.add(i)
        }
    }

    return winningBoards
}

fun isWinningBoard(board: ArrayList<ArrayList<Pair<Int, Boolean>>>): Boolean {
    for(row in board) {
        if(row.count { it.second } == row.size) {
            return true
        }
    }

    for(colIndex in 0 until board[0].size) {
        val col = board.map { it[colIndex] }
        if(col.count { it.second } == col.size) {
            return true
        }
    }

    return false
}

fun getWinningBoardSum(board: ArrayList<java.util.ArrayList<Pair<Int, Boolean>>>): Int {
    return board.sumOf { row -> row.filter { e -> !e.second }.sumOf { it.first } }
}