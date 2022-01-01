package day20

import readLines

// top-left, up, top-right, left, self, right, bottom-left, down, bottom-right
val dir = arrayOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, +1), Pair(0, -1), Pair(0, 0),
    Pair(0, +1), Pair(+1, -1), Pair(+1, 0),  Pair(+1, +1)
)

fun main() {
    val lines = readLines("/day20/input2.txt")
    val enhanceLookUp = lines[0]
    val image = lines.drop(2)

    val part1 = enhanceImage(enhanceLookUp, image, 2)
    val part2 = enhanceImage(enhanceLookUp, image, 50)
    println("part1: $part1")
    println("part2: $part2")
}

fun enhanceImage(enhanceLookUp: String, image: List<String>, times: Int): Int {
    var grid = buildGrid(image, '.')

    for(t in 1..times) {
        val newGrid = HashMap<Pair<Int, Int>, Char>()
        var defaultPixelValue = '.'
        var nextDefaultPixelValue = '.'

        if(enhanceLookUp.first() == '#' && enhanceLookUp.last() == '.') {
            defaultPixelValue = if(t % 2 == 0) '#' else '.'
            nextDefaultPixelValue = if(t % 2 == 0) '.' else '#'
        } else if(enhanceLookUp.first() == '#' && enhanceLookUp.last() == '#') {
            defaultPixelValue = if(t == 2 && defaultPixelValue == '.') '#' else defaultPixelValue
            nextDefaultPixelValue = '#'
        }

        for(p in grid.keys) {
            val binaryString = getBinaryStringForPixel(p, grid, defaultPixelValue)
                .map { if(it == '#') 1 else 0 }.joinToString("")
            val newPixelValue = enhanceLookUp[binaryString.toInt(2)]
            newGrid[p] = newPixelValue
        }
        grid = newGrid
        expandGrid(grid, nextDefaultPixelValue)
    }

    return grid.values.count { it == '#' }
}

fun expandGrid(pixels: MutableMap<Pair<Int, Int>, Char>, defaultPixelValue: Char) {
    val minRow = pixels.keys.minOf { it.first } - 1
    val maxRow = pixels.keys.maxOf { it.first } + 1
    val minCol = pixels.keys.minOf { it.second } - 1
    val maxCol = pixels.keys.maxOf { it.second } + 1
    for(row in minRow..maxRow) {
        for(col in minCol..maxCol) {
            val p = Pair(row, col)
            pixels[p] = pixels.getOrDefault(p, defaultPixelValue)
        }
    }
}

fun getBinaryStringForPixel(
    p: Pair<Int, Int>,
    pixels: MutableMap<Pair<Int, Int>, Char>,
    defaultPixelValue: Char
): String {
    return dir.map { Pair(p.first + it.first, p.second + it.second) }
        .map { pixels.getOrDefault(it, defaultPixelValue) }.joinToString("")
}

fun buildGrid(image: List<String>, defaultChar: Char): MutableMap<Pair<Int, Int>, Char> {
    val pixels = HashMap<Pair<Int, Int>, Char>()
    for(i in image.indices) {
        for(j in image[i].indices) {
            if(image[i][j] != defaultChar) {
                pixels[Pair(i, j)] = image[i][j]
            }
        }
    }
    expandGrid(pixels, defaultChar)
    return pixels
}
