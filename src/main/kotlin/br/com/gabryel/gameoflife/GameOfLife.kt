import processing.core.PApplet
import processing.event.MouseEvent
import java.awt.event.KeyEvent

class GameOfLife(points: Array<Point> = emptyArray()): PApplet() {

    private val size = 1000

    private val scale = 10F

    private val range = -scale .. size.toFloat()

    private var setOfPoints = points.toSet()

    private var originX = 0

    private var originY = 0

    override fun settings() {
        size(size, size)
    }

    override fun setup() {
        frameRate(30F)

        redrawScreen()
    }

    override fun draw() {
        if (looping) {
            setOfPoints = getNewPoints()
        }

        redrawScreen()
    }

    override fun mousePressed(event: MouseEvent) {
        val x = event.x / scale
        val y = event.y / scale

        Point(originX + x.toInt(), originY + y.toInt()).toggle()
    }

    override fun keyPressed() {
        when (keyCode) {
            KeyEvent.VK_RIGHT -> originX++
            KeyEvent.VK_LEFT -> originX--
            KeyEvent.VK_DOWN -> originY++
            KeyEvent.VK_UP -> originY--
            KeyEvent.VK_SPACE -> togglePause()
        }
        redraw()
    }

    private fun getNewPoints(): MutableSet<Point> {
        val pointCount = mutableMapOf<Point, Int>()

        setOfPoints.flatMap(Point::getAdjacent).forEach { point ->
            pointCount.compute(point) { _, old -> old?.plus(1) ?: 1 }
        }

        val newPoints = setOfPoints.toMutableSet()

        newPoints.retainAll(pointCount.keys)

        pointCount.forEach { point, count ->
            if (point.isAlive() && (count < 2 || count > 3)) {
                newPoints -= point
            }

            if (!point.isAlive() && count == 3) {
                newPoints += point
            }
        }

        return newPoints
    }

    private fun redrawScreen() {
        clear()
        setOfPoints.forEach { it.render() }
    }

    private fun togglePause() = if (looping) noLoop() else loop()

    private fun Point.toggle() {
        val newPoints = setOfPoints.toMutableList()

        if (isAlive())
            newPoints -= this
        else
            newPoints += this

        setOfPoints = newPoints.toSet()
        redraw()
    }

    private fun Point.isAlive() = setOfPoints.contains(this)

    private fun Point.render() {
        val xScaled = (x - originX) * scale
        val yScaled = (y - originY) * scale

        if (isOutsideBounds(xScaled, yScaled)) {
            return
        }

        rect(xScaled, yScaled, scale, scale)
    }

    private fun isOutsideBounds(xScaled: Float, yScaled: Float) = (xScaled !in range) || (yScaled !in range)
}

data class Point(val x: Int, val y: Int) {

    fun getAdjacent() = listOf(
        Point(x + 1, y - 1),
        Point(x + 1, y),
        Point(x + 1, y + 1),
        Point(x - 1, y - 1),
        Point(x - 1, y),
        Point(x - 1, y + 1),
        Point(x, y + 1),
        Point(x, y - 1)
    )
}

fun main() {
    val points = arrayOf(
        Point(5, 5),
        Point(5, 6),
        Point(6, 5),
        Point(8, 8),
        Point(7, 8),
        Point(8, 7),
        Point(10, 7),
        Point(12, 7),
        Point(12, 6),
        Point(12, 8),
        Point(4, 8)
    )

    val gameOfLife = GameOfLife(points)

    PApplet.runSketch(arrayOf("Game Of Life"), gameOfLife)
}