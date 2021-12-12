package day12

import readLines

fun main() {
    val lines = readLines("/day12/input.txt")
    val graph = buildGraph(lines)
    val part1 = calculateDistinctPaths(graph, "start", "end", "first")
    val part2 = calculateDistinctPaths(graph, "start", "end", "second")
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateDistinctPaths(graph: Map<String, List<String>>, start: String, end: String, part: String): Int {
    val path = arrayListOf(start)
    return dfs(graph, path, start, end, part)
}

fun dfs(graph: Map<String, List<String>>, path: ArrayList<String>, start: String, end: String, part: String): Int {
    if(start == end) {
        return 1
    }

    var result = 0
    for(nextNode in graph.getValue(start)) {
        if(canVisit(nextNode, path, part)) {
            path.add(nextNode)
            result += dfs(graph, path, nextNode, end, part)
            path.remove(nextNode)
        }
    }

    return result
}

fun canVisit(node: String, path: ArrayList<String>, part: String): Boolean {
    return  isBigCave(node) || node !in path || (part != "first" && canVisitTwice(node, path))
}

fun canVisitTwice(node: String, path: ArrayList<String>): Boolean {
    val isSmallCave = isSmallCave(node)
    val isLessThanTwice = path.count { it == node } < 2
    return isSmallCave && isLessThanTwice && !hasSmallCaveVisitedTwice(path)
}

fun hasSmallCaveVisitedTwice(path: ArrayList<String>): Boolean {
    val smallCaves = path.filter { it.all { c -> c.isLowerCase() } && it != "start" && it != "end" }
    val uniqueSmallCaves = HashSet(smallCaves)
    return uniqueSmallCaves.size != smallCaves.size
}

fun isSmallCave(nextNode: String): Boolean {
    return nextNode != "start" && nextNode != "end" && nextNode.all { it.isLowerCase() }
}

fun isBigCave(nextNode: String): Boolean {
    return nextNode.all { it.isUpperCase() }
}

fun buildGraph(lines: List<String>): Map<String, List<String>> {
    val graph = HashMap<String, ArrayList<String>>()
    for(line in lines) {
        val parts = line.split("-")
        graph.getOrPut(parts[0]) { ArrayList() }.add(parts[1])
        graph.getOrPut(parts[1]) { ArrayList() }.add(parts[0])
    }

    return graph
}
