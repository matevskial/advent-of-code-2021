package day09

import readLines
import kotlin.math.min

// up, right, down, left
val dir = arrayOf(Pair(-1, 0), Pair(0, +1), Pair(+1, 0), Pair(0, -1))

fun main() {
    val heightMap = readLines("/day09/input.txt").map { it.map { c -> c.toString().toInt() } }
    val lowPointIndices = getIndicesOfLowPoints(heightMap)
    val part1 = lowPointIndices.sumOf { heightMap[it.first][it.second] } + lowPointIndices.size
    val part2 = multiplySizesOfLargestBasins(heightMap, lowPointIndices, 3)
    println("part1: $part1")
    println("part2: $part2")
}

fun multiplySizesOfLargestBasins(heightMap: List<List<Int>>, lowPointIndices: List<Pair<Int, Int>>, firstNLargest: Int): Int {
    return lowPointIndices.map { calculateBasinSizeForLowPoint(heightMap, it) }
        .sortedDescending().slice(0 until min(lowPointIndices.size, firstNLargest)).reduce { acc, v -> acc * v }
}

fun calculateBasinSizeForLowPoint(heightMap: List<List<Int>>, lowPoint: Pair<Int, Int>): Int {
    val visited = HashSet<Pair<Int, Int>>(setOf(lowPoint))
    val queue = ArrayDeque(listOf(lowPoint))
    while(queue.isNotEmpty()) {
        val point = queue.removeFirst()
        val value = heightMap[point.first][point.second]

        for(nextPoint in validPoints(point, heightMap)) {
            val nextValue = heightMap[nextPoint.first][nextPoint.second]
            if(nextValue != 9 && nextValue > value && nextPoint !in visited) {
                visited.add(nextPoint)
                queue.add(nextPoint)
            }
        }
    }

    return visited.size
}

fun validPoints(point: Pair<Int, Int>, heightMap: List<List<Int>>): List<Pair<Int, Int>> {
    val validPoints = ArrayList<Pair<Int, Int>>()
    for(d in dir) {
        val nextPoint = Pair(point.first + d.first, point.second + d.second)
        if(nextPoint.first in heightMap.indices && nextPoint.second in heightMap[0].indices) {
            validPoints.add(nextPoint)
        }
    }
    return validPoints
}

fun getIndicesOfLowPoints(heightMap: List<List<Int>>): List<Pair<Int, Int>> {
    val lowPoints = ArrayList<Pair<Int, Int>>()
    for (rowIndex in heightMap.indices) {
        for(colIndex in heightMap[rowIndex].indices) {
            var countLargerAdjacentPoint = 0
            val countAdjacentPoints = validPoints(Pair(rowIndex, colIndex), heightMap).size
            for(nextPoint in validPoints(Pair(rowIndex, colIndex), heightMap)) {
                if(heightMap[nextPoint.first][nextPoint.second] > heightMap[rowIndex][colIndex]) {
                    countLargerAdjacentPoint++
                }
            }

            if(countAdjacentPoints == countLargerAdjacentPoint) {
                lowPoints.add(Pair(rowIndex, colIndex))
            }
        }
    }
    return lowPoints
}
