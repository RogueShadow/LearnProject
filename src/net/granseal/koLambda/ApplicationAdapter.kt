package net.granseal.koLambda

import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.geom.Point2D

open class ApplicationAdapter(title: String, width: Int, height: Int): Application(title,width,height) {
    var sceneRoot = Entity()
    override fun init() {}
    override fun update(delta: Float) {sceneRoot.update(delta)}
    override fun draw(g: Graphics2D) {clear();sceneRoot.draw(g)}
    override fun dispose() {}
    override fun mousePressed(e: MouseEvent) {}
    override fun mouseClicked(e: MouseEvent) {sceneRoot.click(e.point)}
    override fun mouseReleased(e: MouseEvent) {}
    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ESCAPE){
            close()
        }
    }
    override fun keyReleased(e: KeyEvent) {}
    override fun keyTyped(e: KeyEvent) {}
}