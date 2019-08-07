@file:JvmName("ThirtyMain")
package net.granseal.random

import net.granseal.koLambda.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.geom.Point2D



const val scale = 12


fun main(args: Array<String>){
    thirty.start()
}

val font = loadPixelFont()

object thirty: ApplicationAdapter("30x30",30*scale,30*scale,false) {
    val lvl = Level()
    val plr = Player()
    var hue = 0f
    val tint = Color(1f,0f,0f,0.20f)
    override fun init() {
        sceneRoot = lvl
        backgroundColor = hsb(0f,.1f,.1f)
        sceneRoot.scale = scale.toDouble()
        plr.drawers.add{
            drawText(plr.gold.toString(),-14,-14,it)
        }
        sceneRoot.add(plr)
    }

    override fun keyTyped(e: KeyEvent) {
        if (e.keyChar == 's' && !lvl.onWall(plr.pos.x, plr.pos.y + 1)) plr.pos.y += 1
        if (e.keyChar == 'd' && !lvl.onWall(plr.pos.x + 1, plr.pos.y)) plr.pos.x += 1
        if (e.keyChar == 'a' && !lvl.onWall(plr.pos.x - 1, plr.pos.y)) plr.pos.x -= 1
        if (e.keyChar == 'w' && !lvl.onWall(plr.pos.x, plr.pos.y - 1)) plr.pos.y -= 1
        if (lvl.getGold(plr.pos)){
            plr.gold ++
            println("Gold ${plr.gold}")
        }
        lvl.pos = ((plr.pos - point(14f,14f)) * -scale.toFloat())
    }

    override fun update(delta: Float) {
        super.update(delta)
        hue += delta * 0.1f
        backgroundColor = hsb(hue,.5f,.5f)
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = tint
        g.fillRect(0,0,30*scale,30*scale)
    }
}

class Level: Entity() {

    val walls = arrayOf('1')
    var gold = '2'
    val map = getReader("thirty.map").readLines().map{it.toMutableList()}

    init {
        drawers.add {
            drawLevel(it)
        }
    }

    fun List<MutableList<Char>>.get(point: Point2D.Float): Char {
        return this[point.y.toInt()][point.x.toInt()]
    }
    fun List<MutableList<Char>>.set(point: Point2D.Float, c: Char): Boolean {
        return if (this.get(point) == c){
            false
        }else{
            this[point.y.toInt()][point.x.toInt()] = c
            true
        }
    }
    
    fun bColor(b: Char) = when (b) {
        '0' -> Color.LIGHT_GRAY
        '1' -> Color.BLACK
        '2' -> Color.ORANGE
        else -> hsb(thirty.hue,0.5f,0.5f)
    }

    fun isGold(point: Point2D.Float) = map[point.y.toInt()][point.x.toInt()] == gold
    fun getGold(point: Point2D.Float):Boolean {
        return if (isGold(point)){
            map.set(point,'0')
            true
        }else false
    }

    fun onWall(point: Point2D.Float) = onWall(point.x,point.y)
    fun onWall(x: Float,y: Float) = onWall(x.toInt(),y.toInt())
    fun onWall(x: Int, y: Int): Boolean {
        return map[y][x] in walls
    }
    
    fun drawLevel(g: Graphics2D){
        map.withIndex().forEach {y ->
            y.value.withIndex().forEach { x ->
                g.color = bColor(x.value)
                g.fillRect(x.index,y.index,1,1)
            }
        }
    }
}

class Player: Entity() {
    var gold = 0

    init{
        pos = point(14f,14f)
        drawers.add{
            it.color = Color.BLUE
            it.fillRect(0,0,1,1)
        }
    }
}

fun drawText(text: String,x: Int, y: Int, g: Graphics2D, c: Color = Color.BLUE){
    g.color = c
    text.toLowerCase().withIndex().forEach {
       font[it.value]?.withIndex()?.forEach {fy ->
           fy.value.withIndex().forEach{fx ->
               if (fx.value != '.')g.fillRect(x + fx.index + it.index * 5,y + fy.index,1,1)
           }
       }
    }
}

fun loadPixelFont(): Map<Char,List<String>> {
    val chars = listOf(
            '0','1','2','3','4','5','6','7','8','9',
            'a','b','c','d','e','f','g','h','i','j',
            'k','l','m','n','o','p','q','r','s','t',
            'u','v','w','x','y','z'
    )
    var map = mutableMapOf<Char,List<String>>()
    val file = getReader("thirty.fnt").readText()
    val chunks = file.split(",").map{it.trim()}
    chunks.withIndex().forEach{
        map[chars[it.index]] = it.value.split("\n").map{it.trim()}
    }
    return map
}
