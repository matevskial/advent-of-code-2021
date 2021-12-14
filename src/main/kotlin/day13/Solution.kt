package day13

import readLines

fun main() {
    val lines = readLines("/day13/input.txt")
    val points = lines.filter { it.isNotBlank() && !it.startsWith("fold")}
        .map { it.split(",") }.map { Pair(it[0].toInt(), it[1].toInt()) }
    val folds = lines.filter { it.startsWith("fold") }
        .map { it.split(" ")[2] }.map { it.split("=") }.map { Pair(it[0], it[1].toInt()) }

    solveBothParts(points, folds)

}

fun solveBothParts(points: List<Pair<Int, Int>>, folds: List<Pair<String, Int>>) {
    val setOfPoints = HashSet(points)
    var pointsAfterFirstFold = -1
    for(fold in folds) {
        val pointsToRemove = ArrayList<Pair<Int, Int>>()
        val pointsToAdd = ArrayList<Pair<Int, Int>>()
        for(point in setOfPoints) {
            if(fold.first == "x" && point.first > fold.second) {
                val newPoint = Pair(point.first - (point.first - fold.second) * 2, point.second)
                pointsToAdd.add(newPoint)
                pointsToRemove.add(point)
            } else if(fold.first == "y" && point.second > fold.second) {
                val newPoint = Pair(point.first, point.second - (point.second - fold.second)*2)
                pointsToAdd.add(newPoint)
                pointsToRemove.add(point)
            }
        }
        setOfPoints.removeAll(pointsToRemove.toSet())
        setOfPoints.addAll(pointsToAdd)
        if(pointsAfterFirstFold == -1) {
            pointsAfterFirstFold = setOfPoints.size
        }
    }

    println("part1: $pointsAfterFirstFold")
    println("part2: ")

    val minX = setOfPoints.minOf { it.first }
    val maxX = setOfPoints.maxOf { it.first }
    val minY = setOfPoints.minOf { it.second }
    val maxY = setOfPoints.maxOf { it.second }

    for(y in minY..maxY) {
        for(x in minX..maxX) {
            val point = Pair(x, y)
            if(point in setOfPoints) {
                print(" # ")
            } else {
                print(" . ")
            }
        }
        println()
    }
}