package net.granseal.random

import net.granseal.koLambda.*
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.Point2D.Float as F2

fun main() = Tiles.start()

object Tiles: ApplicationAdapter("Tiles Rendering", 1280,720){
    override fun init() {
        backgroundColor = Color.CYAN
        sceneRoot.add{
            addComp(Draggable())
            bounds = Rectangle(0,0,Assets.pics1.width,Assets.pics1.height)
            drawers.add{
                it.drawImage(Assets.pics1,0,0,null)
            }
        }.add{
            addComp(Draggable())
            bounds = Rectangle(0,0,Assets.granseal.width,Assets.granseal.height)
            drawers.add{
                it.drawImage(Assets.granseal,0,0,null)
            }
        }
    }
}

object Assets {
    val pics1 = ImageIO.read(File("dusty_pics1.png"))
    val granseal = ImageIO.read(File("granseal.png"))
}

class Draggable: ComponentAdapter(){
    var dragging = false
    var offset = F2()

    override fun update(delta: Float) {
        if (!dragging){
            if (parent.getActualBounds().contains(Input.mouse.point())) {
                if (Input.mouse.buttonsHeld[MouseEvent.BUTTON1] == true) {
                    dragging = true
                    offset = Input.mouse.point() - parent.pos
                }
            }
        }else
        if (dragging){
            if (Input.mouse.buttonsHeld[MouseEvent.BUTTON1] == false)dragging = false
            if (dragging){
                parent.pos.x = Input.mouse.x - offset.x
                parent.pos.y = Input.mouse.y - offset.y
            }
        }
    }
}