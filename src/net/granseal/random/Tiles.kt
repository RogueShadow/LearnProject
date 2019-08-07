package net.granseal.random

import net.granseal.koLambda.*
import java.awt.Color
import javax.imageio.ImageIO

fun main() = Tiles.start()

object Tiles: ApplicationAdapter("Tiles Rendering", 1280,720){
    override fun init() {
        backgroundColor = Color.CYAN
        sceneRoot.add{
            addComp(Draggable())
            bounds = rect(0f,0f,Assets.pics1.width.toFloat(),Assets.pics1.height.toFloat())
            drawers.add{
                it.drawImage(Assets.pics1,0,0,null)
            }
        }.add{
            addComp(Draggable())
            bounds = rect(0f,0f,Assets.granseal.width.toFloat(),Assets.granseal.height.toFloat())
            drawers.add{
                it.drawImage(Assets.granseal,0,0,null)
            }
        }
    }
}

object Assets {
    val pics1 = ImageIO.read(getStream("dusty_pics1.png"))
    val granseal = ImageIO.read(getStream("granseal.png"))
}

class Draggable: ComponentAdapter(){
    var dragging = false
    var offset = point()

    override fun update(delta: Float) {
        if (!dragging){
            if (parent.getActualBounds().contains(mousePos())) {
                if (mouseButton(MOUSE_B1) == true) {
                    dragging = true
                    offset = mousePos() - parent.pos
                }
            }
        }else
        if (dragging){
            if (mouseButton(MOUSE_B1) == false)dragging = false
            if (dragging){
                parent.pos = mousePos() - offset
            }
        }
    }
}