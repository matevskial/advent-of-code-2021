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

    val areaCalculatorPart1 = AreaCalculatorPart1(boxes)
    println("part1: ${areaCalculatorPart1.calculateArea()}")

    val areaCalculatorPart2 = AreaCalculatorPart2(BoxStructureBuilder(boxes))
    println("part2: ${areaCalculatorPart2.calculateArea()}")
}

class AreaCalculatorPart1(private val boxes: List<Box>) {
    fun calculateArea(): Int {
        val cuboids = HashSet<Triple<Int, Int, Int>>()
        val initializationProcedureRegion = -50..50
        for(box in boxes) {
            for(x in box.bottomLeft.x..box.topRight.x) {
                if(x !in initializationProcedureRegion) {
                    continue
                }
                for(y in box.bottomLeft.y..box.topRight.y) {
                    if(y !in initializationProcedureRegion) {
                        continue
                    }
                    for(z in box.bottomLeft.z..box.topRight.z) {
                        if(z !in initializationProcedureRegion) {
                            continue
                        }
                        if(box.isOperationOn()) {
                            cuboids.add(Triple(x, y, z))
                        } else {
                            cuboids.remove(Triple(x, y, z))
                        }
                    }
                }
            }
        }
        return cuboids.size
    }
}

class AreaCalculatorPart2(private val boxStructureBuilder: BoxStructureBuilder) {

    fun calculateArea(): Long {
        val boxStructure = boxStructureBuilder.build()
        return boxStructure?.getArea() ?: 0L
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
        boxesToProcess = boxes.reversed()
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

    private var operation: String

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

    fun getIntersection(box: Box): Box? {
        if(doesNotIntersect(box)) {
            return null
        }

        val bottomLeftX = if(bottomLeft.x < box.bottomLeft.x) box.bottomLeft.x else bottomLeft.x
        val bottomLeftY = if(bottomLeft.y < box.bottomLeft.y) box.bottomLeft.y else bottomLeft.y
        val bottomLeftZ = if(bottomLeft.z < box.bottomLeft.z) box.bottomLeft.z else bottomLeft.z

        val topRightX = if(topRight.x > box.topRight.x) box.topRight.x else topRight.x
        val topRightY = if(topRight.y > box.topRight.y) box.topRight.y else topRight.y
        val topRightZ = if(topRight.z > box.topRight.z) box.topRight.z else topRight.z

        return Box(Point(bottomLeftX, bottomLeftY, bottomLeftZ), Point(topRightX, topRightY, topRightZ))
    }

    private fun doesNotIntersect(box: Box): Boolean {
        return topRight.x < box.bottomLeft.x || bottomLeft.x > box.topRight.x
                || topRight.y < box.bottomLeft.y || bottomLeft.y > box.topRight.y
                || topRight.z < box.bottomLeft.z || bottomLeft.z > box.topRight.z
    }

    fun getArea(): Long {
        return (abs(topRight.x - bottomLeft.x).toLong() + 1L) * (abs(topRight.y - bottomLeft.y).toLong() + 1L) * (abs(topRight.z - bottomLeft.z).toLong() + 1L)
    }

    override fun toString(): String {
        return "Box[%s, %s]".format(bottomLeft.toString(), topRight.toString())
    }

    fun contains(box: Box): Boolean {
        return bottomLeft.x <= box.bottomLeft.x && topRight.x >= box.topRight.x
                && bottomLeft.y <= box.bottomLeft.y && topRight.y >= box.topRight.y
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
