package net.granseal.koLambda

import java.awt.Point
import java.awt.geom.Point2D

operator fun Point2D.Float.plusAssign(other: Point2D.Float) {
    x += other.x
    y += other.y
}
operator fun Point2D.Float.plus(other: Point2D.Float): Point2D.Float {
    return Point2D.Float(this.x + other.x,this.y + other.y)
}



operator fun Point2D.Float.times(other: Float): Point2D.Float {
    val p = Point2D.Float()
    p.setLocation(x * other.toDouble(), y * other.toDouble())
    return p
}

operator fun Point2D.minus(other: Point2D.Float): Point2D.Float {
    return Point2D.Float(x.toFloat() - other.x, y.toFloat() - other.y)
}
fun Point.toFloat(): Point2D.Float {
    return Point2D.Float(x.toFloat(),y.toFloat())
}

