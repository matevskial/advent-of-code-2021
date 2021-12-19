package day18

import readLines
import kotlin.math.max

fun main() {
    val lines = readLines("/day18/input.txt")
    val snailFishNumbers = getSnailFishNumbers(lines)

    val sum = calculateSumOfSnailFishNumbers(snailFishNumbers)
    val part1 = calculateMagnitudeOfSnailfishNumber(sum)
    val part2 = calculateMaximumMagnitude(lines)
    println("part1: $part1")
    println("part2: $part2")
}

fun calculateMaximumMagnitude(snailFishNumbersStr: List<String>): Long {
    var maxMagnitude = Long.MIN_VALUE
    for(i in snailFishNumbersStr.indices) {
        for(j in i+1 until snailFishNumbersStr.size) {
            var a = parseSnailFishNumber(snailFishNumbersStr[i], null)
            var b = parseSnailFishNumber(snailFishNumbersStr[j], null)
            var sum = getSum(a, b)
            maxMagnitude = max(maxMagnitude, calculateMagnitudeOfSnailfishNumber(sum))

            a = parseSnailFishNumber(snailFishNumbersStr[j], null)
            b = parseSnailFishNumber(snailFishNumbersStr[i], null)
            sum = getSum(a, b)
            maxMagnitude = max(maxMagnitude, calculateMagnitudeOfSnailfishNumber(sum))
        }
    }

    return maxMagnitude
}

fun getSum(a: Node, b: Node): Node {
    val sum = Node(null)
    sum.setAsNode(a, b)
    a.parent = sum
    b.parent = sum
    return reduceSnailFishNumber(sum)
}

fun calculateMagnitudeOfSnailfishNumber(snailFishNumber: Node?): Long {
    require(snailFishNumber != null) { "magnitude should be calculated of not null" }

    if(snailFishNumber.type == "value") {
        return snailFishNumber.value.toLong()
    }

    return (3 * calculateMagnitudeOfSnailfishNumber(snailFishNumber.leftNode)
            + 2 * calculateMagnitudeOfSnailfishNumber(snailFishNumber.rightNode))
}

fun reduceSnailFishNumber(snailFishNumber: Node): Node {
    var reducedSnailFishNumber = explodeReduceSnailFishNumber(snailFishNumber)

    var reduceSplitOperation = findFirstReduceOperation(reducedSnailFishNumber, "split")
    while(reduceSplitOperation != null) {
        splitNode(reduceSplitOperation.second)
        reducedSnailFishNumber = explodeReduceSnailFishNumber(reducedSnailFishNumber)
        reduceSplitOperation = findFirstReduceOperation(reducedSnailFishNumber, "split")
    }

    return reducedSnailFishNumber
}

fun explodeReduceSnailFishNumber(snailFishNumber: Node): Node {
    var reduceOperation = findFirstReduceOperation(snailFishNumber, "explode")
    while (reduceOperation != null) {
        explodeNode(reduceOperation.second)
        reduceOperation = findFirstReduceOperation(snailFishNumber, "explode")
    }
    return snailFishNumber
}

fun splitNode(node: Node) {
    require(node.type == "value") { "node of type value can split" }

    val value = node.value

    val leftNode = Node(node)
    leftNode.setAsValue(value / 2)

    val rightNode = Node(node)
    rightNode.setAsValue(value / 2 + (value % 2))

    node.setAsNode(leftNode, rightNode)
}

fun explodeNode(node: Node) {
    require(node.type == "node") { "node of type node can explode" }

    val firstLeftValueNode = findFirstLeftValueNode(node)
    val firstRightValueNode = findFirstRightValueNode(node)

    require(node.leftNode != null && node.rightNode != null) {"explode node should have non null children"}

    firstLeftValueNode?.setAsValue(firstLeftValueNode.value + node.leftNode!!.value)
    firstRightValueNode?.setAsValue(firstRightValueNode.value + node.rightNode!!.value)

    node.setAsValue(0)
}

fun findFirstLeftValueNode(node: Node): Node? {
    var searchNode = node.parent
    var childNode = node

    while(searchNode != null && searchNode.leftNode == childNode) {
        childNode = searchNode
        searchNode = searchNode.parent
    }

    if(searchNode == null) {
        return null
    }
    searchNode = searchNode.leftNode
    while(searchNode != null && searchNode.type == "node") {
        searchNode = searchNode.rightNode
    }

    return searchNode
}

fun findFirstRightValueNode(node: Node): Node? {
    var searchNode = node.parent
    var childNode = node

    while(searchNode != null && searchNode.rightNode == childNode) {
        childNode = searchNode
        searchNode = searchNode.parent
    }

    if(searchNode == null) {
        return null
    }
    searchNode = searchNode.rightNode

    while(searchNode != null && searchNode.type == "node") {
        searchNode = searchNode.leftNode
    }

    return searchNode
}

fun findFirstReduceOperation(snailFishNumber: Node, reduceOperation: String): Pair<String, Node>? {
    val q = ArrayDeque<Pair<Node?, Int>>()
    q.addFirst(Pair(snailFishNumber, 0))
    while(q.isNotEmpty()) {
        val node = q.first().first
        val level = q.first().second
        q.removeFirst()

        if (node == null) {
            continue
        }

        if(reduceOperation == "explode" && node.type == "node" && level == 4) {
            return Pair("explode", node)
        } else if (reduceOperation == "split" && node.type == "value" && node.value >= 10) {
            return Pair("split", node)
        }


        q.addFirst(Pair(node.rightNode, level + 1))
        q.addFirst(Pair(node.leftNode, level + 1))
    }
    return null
}

fun calculateSumOfSnailFishNumbers(snailFishNumbers: List<Node>): Node {
    var result = snailFishNumbers[0]
    for(n in 1 until snailFishNumbers.size) {
        result = getSum(result, snailFishNumbers[n])
    }
    return result
}

fun getSnailFishNumbers(lines: List<String>): List<Node> {
    val snailFishNumbers = ArrayList<Node>()
    for(l in lines) {
        snailFishNumbers.add(parseSnailFishNumber(l, null))
    }
    return snailFishNumbers
}

fun parseSnailFishNumber(l: String, parent: Node?): Node {
    val node = Node(parent)

    if(l.toIntOrNull() != null) {
        node.setAsValue(l.toInt())
        return node
    }

    val pairDelimiter = getIndexOfPairDelimiter(l)
    val left = l.substring(1 until pairDelimiter)
    val right = l.substring(pairDelimiter + 1 until l.length - 1)
    node.setAsNode(parseSnailFishNumber(left, node), parseSnailFishNumber(right, node))

    return node
}

fun getIndexOfPairDelimiter(l: String): Int {
    val stack = ArrayDeque<Char>()
    for(e in l.withIndex()) {
        if(e.value == '[') {
            stack.addFirst('[')
        } else if(e.value == ']') {
            stack.removeFirst()
        } else if(e.value == ',' && stack.size == 1) {
            return e.index
        }
    }
    return -1
}

class Node(var parent: Node?) {
    var value: Int = 0
    var leftNode: Node? = null
    var rightNode: Node? = null
    lateinit var type: String

    fun setAsValue(value: Int) {
        this.value = value
        this.type = "value"
    }

    fun setAsNode(leftNode: Node, rightNode: Node) {
        this.leftNode = leftNode
        this.rightNode = rightNode
        this.type = "node"
    }

    override fun toString(): String {
        if(type == "value") {
            return value.toString()
        }

        return "[${leftNode.toString()},${rightNode.toString()}]"
    }
}