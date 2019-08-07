package net.granseal.topDownRPGame

import net.granseal.koLambda.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.geom.GeneralPath
import java.io.File
import javax.imageio.ImageIO
import java.awt.geom.Point2D.Float as F2


object TopDownRPGame: ApplicationAdapter("TopDownRPGame", 1280, 720) {
    val lines = GeneralPath()
    val brown = Color(104, 72, 24)
    val green = Color(72, 180, 8)
    var dragging = false
    var dragOffset = F2()

    var img = ImageIO.read(File("granseal.png"))

    override fun init() {
        lines.moveTo(0f,0f)
        fixedFPS = 240
        setIcon(img)

        sceneRoot.apply {
            scale = 1.0
            props["color"] = Color.DARK_GRAY
            drawers.clear()
            drawers.add {
                val size = 4000
                it.color = props["color"] as Color
                //it.fillRect(-size/2, -size/2, size, size)
                //it.drawImage(img, 0, 0, null)
            }
            updaters.add {
                if (dragging) {
                    sceneRoot.pos = Input.mouse.point() - dragOffset
                }
            }
        }


        for (x in 1..8){
            for (y in 1..8){
                //sceneRoot.add(block(Color(Math.random().toFloat(),Math.random().toFloat(),Math.random().toFloat()),F2(x*32f,y*32f)))
            }
        }
        //sceneRoot.add(makePlayer("RogueShadow"))

        sceneRoot.add(generateArm(F2(32*12f,32*12f), randArmCfg(4)))

    }

    override fun mousePressed(e: MouseEvent) {
        if (e.button == 1 && !dragging) {
            dragging = true
            dragOffset = e.point - sceneRoot.pos
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (dragging) {
            dragging = false
        }
    }

    override fun draw(g: Graphics2D) {
        super.draw(g)
        g.color = Color.GREEN
        g.draw(lines)
    }

    override fun update(delta: Float) {
        super.update(delta)
        if (Input.keyHeld('q')) sceneRoot.scale += delta * 10
        if (Input.keyHeld('e')) sceneRoot.scale -= delta * 10
        val arm = sceneRoot.getAll().singleOrNull(){it.props["name"] == "arm"}
        if (arm != null) {
            val b = arm.getActualBounds()

            if (Input.keyHeld('r')) {
                lines.reset()
                lines.moveTo(b.centerX, b.centerY)
            }
            lines.lineTo(b.centerX, b.centerY)
        }
    }

    override fun keyPressed(e: KeyEvent) {
        super.keyPressed(e)
        if (e.keyCode == KeyEvent.VK_T){
            println(sceneRoot.getAll().size)
            println(sceneRoot.getAll().toSet().size)
        }
    }
}

fun getDir(d: Int): F2 {
    return when (d){
        0 -> F2(0f,-1f)
        1 -> F2(-1f,0f)
        2 -> F2(0f, 1f)
        3 -> F2(1f, 0f)
        else -> F2()
    }
}

fun randArmCfg(len: Int): Array<Pair<Float,Float>> {
    val cfg = mutableListOf<Pair<Float,Float>>()
    repeat(len){
        val rot = if (Math.random() > 0.5) Math.random() * 2f else -(Math.random()*2f)
        val length = 20 + Math.random() * 180f
        cfg.add(Pair(rot.toFloat(),length.toFloat()))
    }
    return cfg.toTypedArray()
}


fun generateArm(position: F2, axisCfg: Array<Pair<Float,Float>>): Entity {

    fun axis(size: Int): Entity.(Graphics2D) -> Unit = {
        it.color = props["color"] as Color
        it.fillRect(-size/2,-size/2,size,size)
        it.color = Color.BLACK
        it.drawRect(-size/2,-size/2,size,size)
    }
    fun arm(len: Int): Entity.(Graphics2D) -> Unit = {
        it.color = Color.WHITE
        it.fillRect(-2,-2,len+2,4)
        it.color = Color.BLACK
        it.drawRect(-2,-2,len+2,4)
    }
    fun rotator(rnd: Float): Entity.(Float) -> Unit = {
        val rnd = rnd
        val h = rnd/2f
        rotation += (-h + rnd) * it
    }
    var selected = Entity()
    val root = selected
    axisCfg.withIndex().forEach {
        if (it.index == 0)return@forEach
        selected.add {drawers.add(arm(it.value.second.toInt()))}.add{
            props["color"] = rColor()
            pos.x = it.value.second
            updaters.add(rotator(it.value.first))
            drawers.add(axis(10 + axisCfg.size - it.index))
        }
        selected = selected.children.first().children.first()
    }
    selected.props["name"] = "arm"
    return root.apply {
        pos = position
        props["color"] = rColor()
        updaters.add(rotator(axisCfg[0].first))
        drawers.add(axis(10 + axisCfg.size))
    }
}

fun makePlayer(name: String): Entity {
    val p = Entity().apply {
        // Initialize necessary properites.
        props["player"] = true
        props["name"] = name
        props["direction"] = 0 // 0-up, 1-left, 2-down, 3-right
        props["attacked"] = 0f
        props["color"] = Color.GREEN

        clickHandler = {
            props["color"] = rColor()
            true
        }
        bounds = rect(-8f,-8f,16f,16f)

        //Draws player body.
        drawers.add{
            it.color =  props["color"] as Color
            it.fillOval(-5,-5,10,10)
            it.color = Color.BLACK
            it.drawOval(-5,-5,10,10)
            it.scale(0.7,0.7)
            it.drawString(props["name"] as String,-it.fontMetrics.stringWidth(props["name"] as String)/2f,16f)

            // draws attack
            val timer = props.getOrDefault("attacked",0f) as Float
            if (timer > 0){
                val dir = getDir(props.getOrDefault("direction",0) as Int)
                it.color = Color.RED
                it.translate(dir.getX()*12,dir.getY()*12)
                it.fillOval(-4,-4,8,8)
            }
        }
        updaters.add{
            val solids = TopDownRPGame.sceneRoot.getAll().filter{
                it.props.getOrDefault("solid",false) as Boolean
            }
            solids.forEach { it.props["colliding"] = false }
            val collided = solids.filter{it.getActualBounds().intersects(this.getActualBounds())}
            collided.forEach{
                it.props["colliding"] = true
            }
        }
        updaters.add{
            var timer = props.getOrDefault("attacked",0f) as Float
            if (timer > 0){
                timer -= it
            }else{
                timer = 0f
            }
            props["attacked"] = timer
        }
        updaters.add{ delta ->
            if (props.getOrDefault("attacked",0f) == 0f) {
                if (keyDown('d')) {
                    pos.x += (100 * delta);props["direction"] = 3
                }
                if (keyDown('w')) {
                    pos.y -= (100 * delta);props["direction"] = 0
                }
                if (keyDown('a')) {
                    pos.x -= (100 * delta);props["direction"] = 1
                }
                if (keyDown('s')) {
                    pos.y += (100 * delta);props["direction"] = 2
                }
                if (keyDown(' ')) props["attacked"] = 0.25f
            }
        }
    }
    return p
}

fun block(color: Color, position: F2): Entity {
    val e = Entity()
    e.props["color"] = color
    e.props["solid"] = true
    e.pos = position

    e.bounds = rect(-16f,-16f,32f,32f)

    e.drawers.add{
        it.color = if (props.getOrDefault("colliding",false) as Boolean)  Color.RED else props["color"] as Color
        it.fillRect(-16,-16,32,32)
    }

    return e
}