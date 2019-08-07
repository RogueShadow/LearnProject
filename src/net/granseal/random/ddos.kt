package net.granseal.random

import net.granseal.koLambda.*
import java.awt.Color
import java.awt.geom.Point2D.Float as F2
import java.awt.event.KeyEvent.*

fun main() = ddos.start()

object ddos: ApplicationAdapter("DDOS WES", 800, 600){
    override fun init() {
        val player = Entity().apply {
            pos = F2(400f,300f)
            drawers.add{
                it.color = Color.RED
                it.fillRect(-8,-8,16,16)
            }
            addComp{
                object: ComponentAdapter() {
                    var speed = 500f
                    var vel = F2()
                    override fun update(delta: Float) {
                        if (keyDown(VK_A))vel.x = -speed * delta
                        if (keyDown(VK_D))vel.x = speed * delta
                        vel *= 0.997f
                        parent.pos = parent.pos + vel
                    }
                }
            }
        }
        sceneRoot.add(player)
    }
}