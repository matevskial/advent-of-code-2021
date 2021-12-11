package day11

import readLines

// up, right, down, left, top-left, top-right, bottom-right, bottom-left
val dir = arrayOf(Pair(-1, 0), Pair(0, +1), Pair(+1, 0), Pair(0, -1),
    Pair(-1, -1), Pair(-1, +1), Pair(+1, +1), Pair(+1, -1)
)

fun main() {
    val energyLevels = readLines("/day11/input.txt")
        .map { row -> row.map { c -> c.toString().toInt() }.toMutableList() }

    val result = solveBothParts(energyLevels, 100)
    println("part1: ${result.first}")
    println("part2: ${result.second}")

}

fun solveBothParts(energyLevels: List<MutableList<Int>>, steps: Int): Pair<Int, Int> {
    var totalFlashes  = 0
    var stepAfterAllFlashed = -1
    val someUpperBound = 600
    for(i in 1..someUpperBound) {
        val flashed = HashSet<Pair<Int, Int>>()
        val positionsToFlash = ArrayDeque<Pair<Int, Int>>()
        val positionsToFlashSet = HashSet<Pair<Int, Int>>()
        for(rowIndex in energyLevels.indices) {
            for(colIndex in energyLevels[0].indices) {
                energyLevels[rowIndex][colIndex] = (energyLevels[rowIndex][colIndex] + 1) % 10
                if(energyLevels[rowIndex][colIndex] == 0) {
                    positionsToFlash.addLast(Pair(rowIndex, colIndex))
                    positionsToFlashSet.add(Pair(rowIndex, colIndex))
                }
            }
        }

        while(positionsToFlash.isNotEmpty()) {
            val toFlash = positionsToFlash.removeFirst()
            positionsToFlashSet.remove(toFlash)

            if(toFlash in flashed) {
                continue
            }

            flashed.add(toFlash)
            if(i <= steps) {
                totalFlashes++
            }

            if(didAllFlashed(energyLevels) && stepAfterAllFlashed == -1) {
                stepAfterAllFlashed = i
            }

            for(adjPosition in validAdjPositions(toFlash, energyLevels)) {
                if(adjPosition !in flashed) {
                    if(adjPosition !in positionsToFlashSet) {
                        energyLevels[adjPosition.first][adjPosition.second] = (energyLevels[adjPosition.first][adjPosition.second] + 1) % 10
                    }
                    if(adjPosition !in flashed && energyLevels[adjPosition.first][adjPosition.second] == 0) {
                        positionsToFlash.addLast(adjPosition)
                        positionsToFlashSet.add(adjPosition)
                    }
                }
            }

            if(didAllFlashed(energyLevels) && stepAfterAllFlashed == -1) {
                stepAfterAllFlashed = i
            }
        }
    }

    return Pair(totalFlashes, stepAfterAllFlashed)
}

fun didAllFlashed(energyLevels: List<List<Int>>): Boolean {
    var ok = true
    for(rowIndex in energyLevels.indices) {
        for(colIndex in energyLevels[0].indices) {
            if(energyLevels[rowIndex][colIndex] != 0) {
                ok = false
                break
            }
        }
    }

    return ok
}

fun validAdjPositions(point: Pair<Int, Int>, grid: List<List<Int>>): List<Pair<Int, Int>> {
    val validPoints = ArrayList<Pair<Int, Int>>()
    for(d in dir) {
        val nextPoint = Pair(point.first + d.first, point.second + d.second)
        if(nextPoint.first in grid.indices && nextPoint.second in grid[0].indices) {
            validPoints.add(nextPoint)
        }
    }
    return validPoints
}
