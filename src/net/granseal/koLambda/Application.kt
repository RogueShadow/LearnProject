package net.granseal.koLambda

import java.awt.*
import java.awt.event.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JFrame


abstract class Application(val title: String, val width: Int, val height: Int) {
    private val timer = Timer()
    private val frameTimer = Timer()
    val logger = Logger.getLogger("koLambda")!!
    var started = false
    private var root = JFrame(title)
    private var window = Canvas()
    private var frames = 0
    private var framesTimer = 0.0
    private var updateTimer = 0.0
    var backgroundColor: Color = Color.BLACK

    var fixedFPS = 120
        set(value) {
            if (value <= 0){
                logger.severe("Cannot set negative or 0 fps.")
                throw Exception("Cannot set negative or 0 fps.")
            }
            field = value
            if (value != 0) {
                timePerFrame = 1f / fixedFPS
            }
        }
    private var timePerFrame = 1f / fixedFPS
    private lateinit var bufferGraphics: Graphics2D

    var mouseWheelDelta = 0.0
    var mouseX = 0
    var mouseY = 0

    private var heldKeysCode = mutableMapOf<Int,Boolean>()

    init {
        logger.log(Level.INFO,"Initializing")
        val mouseAdapter = object: MouseAdapter(){
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                mouseWheelDelta = e.preciseWheelRotation
            }
            override fun mouseDragged(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }

            override fun mouseClicked(e: MouseEvent) {
                this@Application.mouseClicked(e)
            }

            override fun mouseReleased(e: MouseEvent) {
                this@Application.mouseReleased(e)
            }

            override fun mousePressed(e: MouseEvent) {
                this@Application.mousePressed(e)
            }
        }
        val keyAdapter = object: KeyAdapter(){
            override fun keyPressed(e: KeyEvent) {
                heldKeysCode[e.extendedKeyCode] = true
                this@Application.keyPressed(e)
            }
            override fun keyReleased(e: KeyEvent) {
                heldKeysCode[e.extendedKeyCode] = false
                this@Application.keyReleased(e)
            }
            override fun keyTyped(e: KeyEvent) {
                this@Application.keyTyped(e)
            }
        }
        val gc = root.graphicsConfiguration
        root.isResizable = false
        window.size = Dimension(width,height)
        root.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        root.add(window)
        root.layout = GridLayout(1,1)
        root.pack()

        root.location = Point(gc.bounds.width/2 - root.width/2,gc.bounds.height/2 - root.height/2)

        window.addMouseMotionListener(mouseAdapter)
        window.addMouseListener(mouseAdapter)
        window.addMouseWheelListener(mouseAdapter)
        window.addKeyListener(keyAdapter)

        logger.info("Initialization complete")
    }

    fun start(){
        window.createBufferStrategy(2)
        bufferGraphics = window.bufferStrategy.drawGraphics as Graphics2D
        bufferGraphics.addRenderingHints(mapOf(
                Pair(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON),
                Pair(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC),
                Pair(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)))
        root.isVisible = true
        window.isVisible = true
        window.requestFocusInWindow()
        init()
        timer.start()
        frameTimer.start()
        started = true
        logger.info("Starting")

        while(started){
            val delta = timer.delta()
            framesTimer += delta
            updateTimer += delta
            if (updateTimer >= timePerFrame) {
                updateTimer -= timePerFrame
                val frameDelta = frameTimer.delta()
                doLoop(frameDelta)
                frames++
            }
            if (framesTimer >= 1){
                framesTimer -= 1
                root.title = "$title (FPS $frames)"
                frames = 0
            }
        }

        logger.info("Exiting")
        dispose()
    }

    private fun doLoop(delta: Float){
        update(delta)
        draw(bufferGraphics)
        window.bufferStrategy.show()
    }

    fun clear(){
        bufferGraphics.color = backgroundColor
        bufferGraphics.fillRect(0,0,width,height)
    }

    fun close(){
        logger.info("Closing")
        started = false
        root.dispose()
    }

    fun setIcon(img: Image){
        logger.info("Set Icon to $img")
        root.iconImage = img
    }

    class Timer{
        var time = 0L
        var lastDelta = 0L
        fun start() {
            time = System.nanoTime()
        }

        fun delta(): Float {
            lastDelta = System.nanoTime() - time
            time = System.nanoTime()
            return (lastDelta/1_000_000_000.0f)
        }
    }

    fun keyHeld(c: Char) = keyHeld(KeyEvent.getExtendedKeyCodeForChar(c.toInt()))
    fun keyHeld(code: Int) = heldKeysCode.getOrDefault(code, false)


    abstract fun init()
    abstract fun update(delta: Float)
    abstract fun draw(g: Graphics2D)
    abstract fun dispose()
    abstract fun mousePressed(e: MouseEvent)
    abstract fun mouseClicked(e: MouseEvent)
    abstract fun mouseReleased(e: MouseEvent)
    abstract fun keyPressed(e: KeyEvent)
    abstract fun keyReleased(e: KeyEvent)
    abstract fun keyTyped(e: KeyEvent)
}