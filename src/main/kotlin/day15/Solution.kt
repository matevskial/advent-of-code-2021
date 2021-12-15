package day15

import readLines
import java.util.*
import kotlin.collections.ArrayList

// up, right, down, left
val dir = arrayOf(Pair(-1, 0), Pair(0, +1), Pair(+1, 0), Pair(0, -1))

fun main() {
    val grid = readLines("/day15/input.txt")
        .map { it.toList() }. map { row -> row.map { c -> c.toString().toInt() } }

    val part1 =  calculateMinimumRisk(grid)
    val fullGrid = getFullGrid(grid)
    val part2 = calculateMinimumRisk(fullGrid)
    println("part1: $part1")
    println("part2: $part2")
}

fun getFullGrid(grid: List<List<Int>>): List<List<Int>> {
    val fullGrid = IntRange(1, grid.size * 5)
        .map { IntRange(1, grid[0].size * 5).map { -1 }.toMutableList() }

    for(rowIndex in grid.indices) {
        for(colIndex in grid[0].indices) {
            var currValue = grid[rowIndex][colIndex]
            for(tileRow in 0 until 5) {
                var effectiveValue = currValue
                for(tileCol in 0 until 5) {
                    val effectiveRow = rowIndex + grid.size * tileRow
                    val effectiveCol = colIndex + grid[0].size * tileCol
                    fullGrid[effectiveRow][effectiveCol] = effectiveValue
                    effectiveValue += 1
                    if(effectiveValue > 9) {
                        effectiveValue = 1
                    }
                }
                currValue += 1
                if(currValue > 9) {
                    currValue = 1
                }
            }
        }
    }

    return fullGrid
}

fun calculateMinimumRisk(grid: List<List<Int>>): Int {
    val start = Pair(0, 0)
    val end = Pair(grid.size - 1, grid[0].size - 1)
    return dijkstra(grid, start, end)
}

fun dijkstra(grid: List<List<Int>>, start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    val distancesByNodes = hashMapOf(start to 0)
    val priorityQueue = PriorityQueue<Pair<Pair<Int, Int>, Int>>(compareBy { it.second })
    priorityQueue.add(Pair(start, 0))

    while(priorityQueue.isNotEmpty()) {
        val node = priorityQueue.peek().first
        val dist = priorityQueue.peek().second
        priorityQueue.remove()

        for(adjNode in validNodes(node, grid)) {
            val newDist = dist + grid[adjNode.first][adjNode.second]
            if(adjNode !in distancesByNodes || newDist < distancesByNodes.getValue(adjNode)) {
                distancesByNodes[adjNode] = newDist
                priorityQueue.add(Pair(adjNode, newDist))
            }
        }

    }

    return distancesByNodes.getValue(end)
}

fun validNodes(point: Pair<Int, Int>, grid: List<List<Int>>): List<Pair<Int, Int>> {
    val validNodes = ArrayList<Pair<Int, Int>>()
    for(d in dir) {
        val nextPoint = Pair(point.first + d.first, point.second + d.second)
        if(nextPoint.first in grid.indices && nextPoint.second in grid[0].indices) {
            validNodes.add(nextPoint)
        }
    }
    return validNodes
}