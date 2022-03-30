package day19

import readLines
import kotlin.math.abs
import kotlin.math.max

const val MINIMUM_COMMON_BEACONS = 12

fun main() {
    val currentTimeMillis = System.currentTimeMillis()
    val input = readLines("day19/input.txt")
    val reports = getScannerReports(input)

    val result = solveBothParts(reports)
    println("part1: ${result.first}")
    println("part2: ${result.second}")
    println("time in seconds: ${(System.currentTimeMillis() - currentTimeMillis) / 1000.0}")
}

fun solveBothParts(reports: List<Set<Coordinate>>): Pair<Int, Int> {
    val firstScanner = 0
    val processedScanners = mutableSetOf(firstScanner)

    val uniqueBeacons = HashSet<Coordinate>()
    uniqueBeacons.addAll(reports[firstScanner])

    val bfsQueue = ArrayDeque<Int>()
    bfsQueue.addLast(0)

    val reportsByScannerRelativeToFirstScanner = mutableMapOf(firstScanner to reports[firstScanner])
    val scannerPositionsRelativeToFirstScanner = mutableSetOf(Coordinate(0, 0, 0))

    while(bfsQueue.isNotEmpty()) {
        val currentScanner = bfsQueue.removeFirst()
        val currentScannerReports = reportsByScannerRelativeToFirstScanner.getValue(currentScanner)

        for(scanner in reports.indices) {
            if(scanner in processedScanners) {
                continue
            }

            val scannerRegionCalculator = ScannerRegionCalculator(currentScannerReports, reports[scanner])
            scannerRegionCalculator.calculate()

            if(scannerRegionCalculator.haveMinimumOverlappingBeacons()) {
                processedScanners.add(scanner)
                bfsQueue.addLast(scanner)
                reportsByScannerRelativeToFirstScanner[scanner] = scannerRegionCalculator.getSecondScannerReportsRelativeToFirstScanner()
                uniqueBeacons.addAll(scannerRegionCalculator.getSecondScannerReportsRelativeToFirstScanner())
                scannerPositionsRelativeToFirstScanner.add(scannerRegionCalculator.getSecondScannerPositionRelativeToFirstScanner())
            }
        }
    }

    var largestManhattanDistance = 0
    for(coord1 in scannerPositionsRelativeToFirstScanner) {
        for(coord2 in scannerPositionsRelativeToFirstScanner) {
            largestManhattanDistance = max(largestManhattanDistance, coord1.calculateManhattanDistance(coord2))
        }
    }

    return Pair(uniqueBeacons.size, largestManhattanDistance)
}

class ScannerRegionCalculator(private val firstScannerReports: Set<Coordinate>, private val secondScannerReports: Set<Coordinate>) {

    private var numberOfOverlappingBeacons: Int? = null
    private lateinit var secondScannerReportsRelativeToFirstScanner: Set<Coordinate>
    private var secondScannerPositionRelativeToFirstScanner: Coordinate? = null

    fun calculate() {
        for(reportsWithOrientation in getScannerReportsWithAllOrientations(secondScannerReports)) {
            val relativePositionsOfSecondScanner = getAllRelativePositionsOfSecondScanner(reportsWithOrientation)
            for(relativePositionOfSecondScanner in relativePositionsOfSecondScanner) {
                val beaconsWithCurrentRelativePositionOfSecondScanner = getBeaconsWithRelativePositionOfSecondScanner(reportsWithOrientation, relativePositionOfSecondScanner)
                val overlappingBeaconsWithCurrentRelativePositionOfSecondScanner = beaconsWithCurrentRelativePositionOfSecondScanner.filter { it in firstScannerReports }.toSet()
                if(numberOfOverlappingBeacons == null || overlappingBeaconsWithCurrentRelativePositionOfSecondScanner.size > numberOfOverlappingBeacons!!) {
                    numberOfOverlappingBeacons = overlappingBeaconsWithCurrentRelativePositionOfSecondScanner.size
                    secondScannerReportsRelativeToFirstScanner = beaconsWithCurrentRelativePositionOfSecondScanner
                    secondScannerPositionRelativeToFirstScanner = relativePositionOfSecondScanner
                }
            }
        }
    }

    private fun getAllRelativePositionsOfSecondScanner(secondScannerReports: Set<Coordinate>): Set<Coordinate> {
        val relativePositions = HashSet<Coordinate>()
        for(firstScannerBeacon in firstScannerReports) {
            for(secondScannerBeacon in secondScannerReports) {
                relativePositions.add(firstScannerBeacon.subtract(secondScannerBeacon))
            }
        }
        return relativePositions
    }

    private fun getScannerReportsWithAllOrientations(reports: Set<Coordinate>): List<Set<Coordinate>> {
        val reportsInOrientations = reports.map { getCoordinateInAllOrientations(it) }
        val result = ArrayList<MutableSet<Coordinate>>()
        for(orientationIndex in 0 until 24) {
            result.add(HashSet())
            for(i in reports.indices) {
                result.last().add(reportsInOrientations[i][orientationIndex])
            }
        }
        return result
    }

    private fun getCoordinateInAllOrientations(coordinate: Coordinate): List<Coordinate> {
        val result = ArrayList<Coordinate>()
        var currentCoordinate = coordinate
        for(cycle in 1..2) {
            for(step in 1..3) {
                currentCoordinate = roll(currentCoordinate)
                result.add(currentCoordinate)
                for(i in 1..3) {
                    currentCoordinate = turn(currentCoordinate)
                    result.add(currentCoordinate)
                }
            }
            currentCoordinate = roll(turn(roll(currentCoordinate)))
        }
        return result
    }

    private fun roll(coordinate: Coordinate): Coordinate {
        return Coordinate(coordinate.x, coordinate.z, -coordinate.y)
    }

    private fun turn(coordinate: Coordinate): Coordinate {
        return Coordinate(-coordinate.y, coordinate.x, coordinate.z)
    }

    private fun getBeaconsWithRelativePositionOfSecondScanner(secondScannerReports: Set<Coordinate>, relativePositionOfSecondScanner: Coordinate): Set<Coordinate> {
        return secondScannerReports.map { it.add(relativePositionOfSecondScanner) }.toSet()
    }

    fun haveMinimumOverlappingBeacons(): Boolean {
        return  numberOfOverlappingBeacons != null && numberOfOverlappingBeacons!! >= MINIMUM_COMMON_BEACONS
    }

    fun getSecondScannerReportsRelativeToFirstScanner(): Set<Coordinate> {
        return secondScannerReportsRelativeToFirstScanner
    }

    fun getSecondScannerPositionRelativeToFirstScanner(): Coordinate {
        return secondScannerPositionRelativeToFirstScanner ?: Coordinate(0, 0, 0)
    }
}

fun getScannerReports(input: List<String>): List<Set<Coordinate>> {
    val reports = ArrayList<Set<Coordinate>>()
    var currentScanner = HashSet<Coordinate>()
    input.forEachIndexed { index, element ->
        if(!element.startsWith("---") && element.trim().isNotBlank()) {
            val numbers = element.split(",").map { it.toInt() }
            currentScanner.add(Coordinate(numbers[0], numbers[1], numbers[2]))
        }
        if(element.trim().isBlank() || index == input.size - 1) {
            reports.add(currentScanner)
            currentScanner = HashSet()
        }
    }
    return reports
}

data class Coordinate(val x: Int, val y: Int, val z: Int) {
    fun subtract(other: Coordinate): Coordinate {
        return Coordinate(x - other.x, y - other.y, z - other.z)
    }

    fun add(other: Coordinate): Coordinate {
        return Coordinate(x + other.x, y + other.y, z + other.z)
    }

    fun calculateManhattanDistance(other: Coordinate): Int {
        return abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
    }
}
