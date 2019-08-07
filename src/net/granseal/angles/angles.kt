package net.granseal.angles

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.awt.geom.GeneralPath
import java.awt.geom.Point2D.Float as F2
import net.granseal.koLambda.*

fun main(){
    angles.start()
}
object angles: ApplicationAdapter("Angles", 800, 600){
    val grid = GeneralPath()
    val angles = Entity()
    override fun init() {
        fixedFPS = 120
        backgroundColor = Color.black

        for (x in 0..width step 32){
            grid.moveTo(x.toFloat(),0f)
            grid.lineTo(x.toFloat(),height.toFloat())
        }
        for (y in 0..height step 32){
            grid.moveTo(0f,y.toFloat())
            grid.lineTo(width.toFloat(),y.toFloat())
        }

        logger.info("Initialized Angles")
    }

    override fun mousePressed(e: MouseEvent) {
        sceneRoot.add(dot(e.point))
        if (sceneRoot.children.size > 1){
            val c = sceneRoot.children.takeLast(2)
            angles.add(Angle(c.first().pos,c.last().pos))
        }
    }

    override fun draw(g: Graphics2D) {
        clear()
        g.color = Color.darkGray
        g.draw(grid)
        angles.draw(g)
        sceneRoot.draw(g)
    }
}

fun dot(pos: Point): Entity {
    val size = 8
    val e = Entity()
    e.pos = pos.toFloat()
    e.bounds = Rectangle(-size/2,-size/2,size,size)
    e.drawers.add{
        it.color = Color.cyan
        it.fillOval(-size/2,-size/2,size,size)
    }
    return e
}

class Angle(val p1: F2, val p2: F2): Entity(){
    init {
        drawers.add{
            it.color = Color.red
            it.drawLine(p1.x.toInt(),p1.y.toInt(),p2.x.toInt(),p2.y.toInt())
            val ox = Math.abs(p1.x - p2.x)
            val oy = Math.abs(p1.y - p2.y)
            val sx = if (p1.x < p2.x) p1.x else p2.x
            val sy = if (p1.y < p2.y) p1.y else p2.y
            val angle =  Math.atan2(oy.toDouble(),ox.toDouble())
            val dist = Math.sqrt(ox*ox.toDouble() + oy * oy)
            val dx = sx + Math.cos(angle) * (dist/2)
            val dy = sy + Math.sin(angle) * (dist/2)
            it.color = Color.ORANGE
            it.drawString( (angle*(180/Math.PI)).toInt().toString(),dx.toInt(),dy.toInt())
        }
    }
}