package day23

import readLines
import kotlin.math.min

val hallways = listOf(Pair(1, 1), Pair(1, 2), Pair(1, 4), Pair(1, 6), Pair(1, 8), Pair(1, 10), Pair(1, 11))

val roomColumns = 3..9 step 2
var roomRows = 1..2

val energyByPod = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
val numberByPod = mapOf('A' to 0, 'B' to 1, 'C' to 2, 'D' to 3)

// up, right, down, left
val dir = arrayOf(Pair(-1, 0), Pair(0, +1), Pair(+1, 0), Pair(0, -1))

fun main() {
    val grid = readLines("/day23/input.txt")
        .map { it.toCharArray().toTypedArray() }.toTypedArray()

    val gridCopy = readLines("/day23/input.txt").toMutableList()
    gridCopy.add(3, "  #D#C#B#A#")
    gridCopy.add(4, "  #D#B#A#C#")

    val gridPart2 = gridCopy.map { it.toCharArray().toTypedArray() }.toTypedArray()

    val part1 = calculateMinimumEnergyToOrganizePods(grid)
    val part2 = calculateMinimumEnergyToOrganizePods(gridPart2)
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateMinimumEnergyToOrganizePods(grid: Array<Array<Char>>): Int {
    roomRows = 2..grid.size - 2
    var minimumEnergy = Int.MAX_VALUE
    val states = HashMap<String, Int>()

    fun calculateMinimumEnergyToOrganizePodsRec(grid: Array<Array<Char>>, energy: Int) {

        val pods = getPods(grid)
        val key = pods.sortedBy { it.second }.sortedBy { it.first.first }.map { "" + it.first.first + it.first.second + it.second}.joinToString("")

        if(key in states && states.getValue(key) < energy) {
            return
        }

        states[key] = min(states.getOrDefault(key, Int.MAX_VALUE), energy)

        if(arePodsOrganized(pods)) {
            minimumEnergy = min(minimumEnergy, energy)
            states[key] = min(states.getOrDefault(key, Int.MAX_VALUE), energy)
            return
        }

        for(pod in pods) {
            if(isPodOrganized(pod, grid)) {
                continue
            }
            if(isPodInHallway(pod)) {
                val availableValidRoom = getAvailableValidRoomForPod(pod.second, grid)
                val distance = getDistanceToValidRoom(pod, availableValidRoom, grid)
                if(availableValidRoom != null && distance != -1) {
                    // organize pod
                    grid[pod.first.first][pod.first.second] = '.'
                    grid[availableValidRoom.first][availableValidRoom.second] = pod.second
                    // recurse
                    calculateMinimumEnergyToOrganizePodsRec(grid, energy + distance * energyByPod.getValue(pod.second))
                    // deorganize
                    grid[pod.first.first][pod.first.second] = pod.second
                    grid[availableValidRoom.first][availableValidRoom.second] = '.'
                    break
                }
            } else if(isPodInRoom(pod)) {
                val availableHallwayPositions = getAvailableHallwayPositions(pod, grid)
                for(h in availableHallwayPositions) {
                    // move pod to hallway
                    grid[pod.first.first][pod.first.second] = '.'
                    grid[h.first.first][h.first.second] = pod.second
                    // recurse
                    calculateMinimumEnergyToOrganizePodsRec(grid, energy + h.second * energyByPod.getValue(pod.second))
                    // move pod back to original pos
                    grid[pod.first.first][pod.first.second] = pod.second
                    grid[h.first.first][h.first.second] = '.'
                }
            }
        }
    }

    calculateMinimumEnergyToOrganizePodsRec(grid, 0)

    return minimumEnergy
}

fun getPods(grid: Array<Array<Char>>): List<Pair<Pair<Int, Int>, Char>> {
    val pods = ArrayList<Pair<Pair<Int, Int>, Char>>()
    for(r in grid.indices) {
        for(c in grid[r].indices) {
            if(grid[r][c] in numberByPod) {
                pods.add(Pair(Pair(r, c), grid[r][c]))
            }
        }
    }
    return pods
}

fun arePodsOrganized(pods: List<Pair<Pair<Int, Int>, Char>>): Boolean {
    for(pod in pods) {
        val validRoomColumn = 3 + numberByPod.getValue(pod.second)*2
        if(pod.first.first !in roomRows || pod.first.second != validRoomColumn) {
            return false
        }
    }
    return true
}

fun isPodInHallway(pod: Pair<Pair<Int, Int>, Char>): Boolean {
    return pod.first in hallways
}

fun isPodOrganized(pod: Pair<Pair<Int, Int>, Char>, grid: Array<Array<Char>>): Boolean {
    val validRoomColumn = 3 + numberByPod.getValue(pod.second) * 2
    if(pod.first.second != validRoomColumn) {
        return false
    }
    for(r in pod.first.first..roomRows.last) {
        if(grid[r][validRoomColumn] != pod.second) {
            return false
        }
    }

    return true
}

fun getDistanceToValidRoom(
    pod: Pair<Pair<Int, Int>, Char>,
    availableValidRoom: Pair<Int, Int>?,
    grid: Array<Array<Char>>
): Int {
    if(availableValidRoom == null) {
        return -1
    }
    val q = ArrayDeque<Pair<Pair<Int, Int>, Int>>()
    val visited = HashSet<Pair<Int, Int>>()
    q.addLast(Pair(pod.first, 0))
    visited.add(pod.first)

    while(q.isNotEmpty()) {
        val p = q.first().first
        val dist = q.first().second
        q.removeFirst()

        if(p == availableValidRoom) {
            return dist
        }

        for(d in dir) {
            val newP = Pair(p.first + d.first, p.second + d.second)
            val newDist = dist + 1
            if(newP in visited || isOutOfBounds(newP, grid)) {
                continue
            }
            if(grid[newP.first][newP.second] == '.') {
                q.addLast(Pair(newP, newDist))
                visited.add(newP)
            }
        }
    }

    return -1
}

fun getAvailableHallwayPositions(pod: Pair<Pair<Int, Int>, Char>, grid: Array<Array<Char>>): List<Pair<Pair<Int, Int>, Int>> {
    val q = ArrayDeque<Pair<Pair<Int, Int>, Int>>()
    val visited = HashSet<Pair<Int, Int>>()
    q.addLast(Pair(pod.first, 0))
    visited.add(pod.first)
    val availableHallways = ArrayList<Pair<Pair<Int, Int>, Int>>()

    while(q.isNotEmpty()) {
        val p = q.first().first
        val dist = q.first().second
        q.removeFirst()

        if(p in hallways) {
            availableHallways.add(Pair(p, dist))
        }

        for(d in dir) {
            val newP = Pair(p.first + d.first, p.second + d.second)
            val newDist = dist + 1
            if(newP in visited || isOutOfBounds(newP, grid)) {
                continue
            }
            if(grid[newP.first][newP.second] == '.') {
                q.addLast(Pair(newP, newDist))
                visited.add(newP)
            }
        }
    }

    return availableHallways
}

fun isOutOfBounds(newP: Pair<Int, Int>, grid: Array<Array<Char>>): Boolean {
    return newP.first !in grid.indices && newP.second !in grid[0].indices
}

fun isPodInRoom(pod: Pair<Pair<Int, Int>, Char>): Boolean {
    return pod.first.first in roomRows && pod.first.second in roomColumns
}

fun getAvailableValidRoomForPod(pod: Char, grid: Array<Array<Char>>): Pair<Int, Int>? {

    val validRoomColumn = 3 + numberByPod.getValue(pod) * 2
    for(r in roomRows.reversed()) {
        if(grid[r][validRoomColumn] == '.') {
            return Pair(r, validRoomColumn)
        } else if(grid[r][validRoomColumn] != pod) {
            return null
        }
    }

    return null
}