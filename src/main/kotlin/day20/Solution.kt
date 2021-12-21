package day20

import readLines

// top-left, up, top-right, left, self, right, bottom-left, down, bottom-right
val dir = arrayOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, +1), Pair(0, -1), Pair(0, 0),
    Pair(0, +1), Pair(+1, -1), Pair(+1, 0),  Pair(+1, +1)
)

fun main() {
    val lines = readLines("/day20/input.txt")
    val enhanceLookUp = lines[0]
    val image = lines.drop(2)

    val part1 = enhanceImage(enhanceLookUp, image, 2)
    val part2 = enhanceImage(enhanceLookUp, image, 50)
    println("part1: $part1")
    println("part2: $part2")
}

fun enhanceImage(enhanceLookUp: String, image: List<String>, times: Int): Int {
    var grid = getImageGrid(image)
    val rows = grid.size
    val cols = grid[0].size

    for(t in 1..times) {
        val newGrid = Array(rows) { Array(cols) { '.' } }
        for(i in 1 until rows - 1) {
            for(j in 1 until cols - 1) {
                val binaryString = getThreeRowsForPixel1(Pair(i, j), grid).map { if(it == '.') '0' else '1' }.joinToString("")
                val newPixelValue = enhanceLookUp[binaryString.toInt(2)]
                newGrid[i][j] = newPixelValue
            }
        }
        grid = newGrid
        setBorders(grid, t + 1)
    }

    return grid.sumOf { it.count { c -> c == '#' } }
}

fun setBorders(grid: Array<Array<Char>>, t: Int) {
    val char = if(t % 2 == 0) '#' else '.'
    for(col in grid[0].indices) {
        grid[0][col] = char
        grid.last()[col] = char
    }
    for(row in grid.indices) {
        grid[row][0] = char
        grid[row][grid[row].lastIndex] = char
    }
}

fun getImageGrid(image: List<String>): Array<Array<Char>> {
    val rows = image.size
    val cols = image[0].length
    val grid = Array(600) { Array(600) { '.' } }
    for(i in grid.indices) {
        for(j in grid[i].indices) {
            if(i >= 300 && i < rows + 300 && j >= 300 && j < cols + 300) {
                grid[i][j] = image[i - 300][j - 300]
            }
        }
    }
    return grid
}

fun getThreeRowsForPixel1(p: Pair<Int, Int>, grid: Array<Array<Char>>): String {
    return dir.map { Pair(p.first + it.first, p.second + it.second) }
        .map { grid[it.first][it.second] }.joinToString("")
}
