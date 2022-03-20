package day22

import readLines
import kotlin.math.abs

fun main() {
    val regex = """-?\d+""".toRegex()
    val rebootSteps = ArrayList<Pair<String, List<Int>>>()
    for(line in readLines("/day22/input.txt")) {
        val operation = line.split(" ")[0]
        val numbers = regex.findAll(line).map { it.value.toInt() }.toList()
        rebootSteps.add(Pair(operation, numbers))
    }

    val boxes = rebootSteps.map { Box(Point(it.second[0], it.second[2], it.second[4]), Point(it.second[1], it.second[3], it.second[5]), it.first) }
    val boxesPart1 = boxes.mapNotNull { it.getIntersection(Box(Point(-50, -50, -50), Point(50, 50, 50)), it.operation) }

    val areaCalculatorPart1 = AreaCalculatorUsingBoxStructure(BoxStructureBuilder(boxesPart1))
    println("part1: ${areaCalculatorPart1.calculateArea()}")

    val areaCalculatorUsingBoxStructure = AreaCalculatorUsingBoxStructure(BoxStructureBuilder(boxes))
    println("part2: ${areaCalculatorUsingBoxStructure.calculateArea()}")

    val areaCalculatorUsingSlices = AreaCalculatorUsingSlices(boxes)
    println("part2: ${areaCalculatorUsingSlices.calculateArea()}")
}

class AreaCalculatorUsingBoxStructure(private val boxStructureBuilder: BoxStructureBuilder) {

    fun calculateArea(): Long {
        val boxStructure = boxStructureBuilder.build()
        return boxStructure?.getArea() ?: 0L
    }
}

class AreaCalculatorUsingSlices(private val boxes: List<Box>) {
    private lateinit var resultBoxes: MutableSet<Box>

    fun calculateArea(): Long {
        resultBoxes = HashSet()
        for(box in boxes) {
            val boxesThatIntersectWithCurrentBox = getBoxesThatIntersectWith(box)
            resultBoxes.removeAll(boxesThatIntersectWithCurrentBox)
            val slices = boxesThatIntersectWithCurrentBox.map { it.getSlices(box) }.flatten()
            resultBoxes.addAll(slices)
            if(box.isOperationOn()) {
                resultBoxes.add(box)
            }
        }
        return resultBoxes.sumOf { it.getArea() }
    }

    private fun getBoxesThatIntersectWith(box: Box): Set<Box> {
        return resultBoxes.filter { it.doesIntersect(box) }.toSet()
    }
}

class BoxStructureBuilder(private val boxes: List<Box>) {

    private lateinit var processedBoxes: MutableList<Box>
    private lateinit var boxesToProcess: List<Box>

    fun build(): BoxStructure? {
        if(boxes.isEmpty()) {
            return null
        }
        processedBoxes = ArrayList()
        boxesToProcess = removeContainedBoxes().reversed()

        val boxNodes = ArrayList<BoxNode>()
        for(box in boxesToProcess) {
            if(!box.isOperationOn()) {
                processedBoxes.add(box)
                continue
            }
            boxNodes.add(buildBoxNode(box))
            processedBoxes.add(box)
        }
        return BoxStructure(boxNodes)
    }

    private fun removeContainedBoxes(): List<Box> {
        val result = LinkedHashSet<Box>()
        for(box in boxes) {
            val boxesToDelete = HashSet<Box>()
            for(box1 in result) {
                if(box.contains(box1)) {
                    boxesToDelete.add(box1)
                }
            }
            result.removeAll(boxesToDelete)
            result.add(box)
        }
        return result.toList()
    }

    private fun buildBoxNode(box: Box): BoxNode {
        val intersections = getIntersections(box)
        return BoxNode(box, BoxStructureBuilder(intersections).build())
    }

    private fun getIntersections(box: Box): List<Box> {
        return processedBoxes.mapNotNull { box.getIntersection(it) }
    }
}

class BoxStructure(private var boxNodes: List<BoxNode>) {

    fun getArea(): Long {
        return boxNodes.map { it.getArea() }.sumOf { it }
    }
}

class BoxNode(private val box: Box, private val boxStructure: BoxStructure?) {

    fun getArea(): Long {
        val areaToSubtract = boxStructure?.getArea() ?: 0L
        return box.getArea() - areaToSubtract
    }
}

data class Box(val bottomLeft: Point, val topRight: Point) {

    var operation: String

    init {
        if(bottomLeft.x > topRight.x) {
            throw IllegalArgumentException("Bottom left point x is larger than top right point x")
        }
        if(bottomLeft.y > topRight.y) {
            throw IllegalArgumentException("Bottom left point y is larger than top right point y")
        }
        if(bottomLeft.z > topRight.z) {
            throw IllegalArgumentException("Bottom left point z is larger than top right point z")
        }
        operation = "on"
    }

    constructor(bottomLeft: Point, topRight: Point, operation: String) : this(bottomLeft, topRight) {
        this.operation = operation
    }

    fun getIntersection(box: Box, operation: String): Box? {
        if(doesNotIntersect(box)) {
            return null
        }

        val bottomLeftX = if(bottomLeft.x < box.bottomLeft.x) box.bottomLeft.x else bottomLeft.x
        val bottomLeftY = if(bottomLeft.y < box.bottomLeft.y) box.bottomLeft.y else bottomLeft.y
        val bottomLeftZ = if(bottomLeft.z < box.bottomLeft.z) box.bottomLeft.z else bottomLeft.z

        val topRightX = if(topRight.x > box.topRight.x) box.topRight.x else topRight.x
        val topRightY = if(topRight.y > box.topRight.y) box.topRight.y else topRight.y
        val topRightZ = if(topRight.z > box.topRight.z) box.topRight.z else topRight.z

        return Box(Point(bottomLeftX, bottomLeftY, bottomLeftZ), Point(topRightX, topRightY, topRightZ), operation)
    }

    fun getIntersection(box: Box): Box? {
        return getIntersection(box, "on")
    }

    fun doesIntersect(box: Box): Boolean {
        return !doesNotIntersect(box)
    }

    private fun doesNotIntersect(box: Box): Boolean {
        return topRight.x < box.bottomLeft.x || bottomLeft.x > box.topRight.x
                || topRight.y < box.bottomLeft.y || bottomLeft.y > box.topRight.y
                || topRight.z < box.bottomLeft.z || bottomLeft.z > box.topRight.z
    }

    fun getArea(): Long {
        return (abs(topRight.x - bottomLeft.x).toLong() + 1L) * (abs(topRight.y - bottomLeft.y).toLong() + 1L) * (abs(topRight.z - bottomLeft.z).toLong() + 1L)
    }

    fun getSlices(box: Box): Set<Box> {
        val intersection = getIntersection(box) ?: return emptySet()
        val slices = HashSet<Box>()

        if(topRight.z > intersection.topRight.z) {
            slices.add(Box(Point(bottomLeft.x, bottomLeft.y, intersection.topRight.z + 1), Point(topRight.x, topRight.y, topRight.z)))
        }

        if(bottomLeft.z < intersection.bottomLeft.z) {
            slices.add(Box(Point(bottomLeft.x, bottomLeft.y, bottomLeft.z), Point(topRight.x, topRight.y, intersection.bottomLeft.z - 1)))
        }

        if(bottomLeft.y < intersection.bottomLeft.y) {
            slices.add(Box(Point(bottomLeft.x, bottomLeft.y, intersection.bottomLeft.z), Point(topRight.x, intersection.bottomLeft.y - 1, intersection.topRight.z)))
        }

        if(topRight.y > intersection.topRight.y) {
            slices.add(Box(Point(bottomLeft.x, intersection.topRight.y + 1, intersection.bottomLeft.z), Point(topRight.x, topRight.y, intersection.topRight.z)))
        }

        if(bottomLeft.x < intersection.bottomLeft.x) {
            slices.add(Box(Point(bottomLeft.x, intersection.bottomLeft.y, intersection.bottomLeft.z), Point(intersection.bottomLeft.x - 1, intersection.topRight.y, intersection.topRight.z)))
        }

        if(topRight.x > intersection.topRight.x) {
            slices.add(Box(Point(intersection.topRight.x + 1, intersection.bottomLeft.y, intersection.bottomLeft.z), Point(topRight.x, intersection.topRight.y, intersection.topRight.z)))
        }

        return slices
    }

    override fun toString(): String {
        return "Box[%s, %s]".format(bottomLeft.toString(), topRight.toString())
    }

    fun contains(box: Box): Boolean {
        return bottomLeft.x <= box.bottomLeft.x && topRight.x >= box.topRight.x
                && bottomLeft.y <= box.bottomLeft.y && topRight.y >= box.topRight.y
                && bottomLeft.z <= box.bottomLeft.z && topRight.z >= box.topRight.z
    }

    fun isOperationOn(): Boolean {
        return operation == "on"
    }
}

data class Point(val x: Int, val y: Int, val z:Int) {

    override fun toString(): String {
        return "(%s, %s, %s)".format(x, y, z)
    }
}
