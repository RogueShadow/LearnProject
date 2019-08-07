package net.granseal.particlesGoUp

import net.granseal.koLambda.*
import java.awt.Color

// Program Entry Point.  Extend the ApplicationAdapter class, and call the .start() method to get going.
fun main() = particlesGoUp.start()
// Set the title, width and height here in the constructor of ApplicationAdapter
object particlesGoUp: ApplicationAdapter("Particles Go Up",800,600){
    override fun init() {
        fixedFPS = 120  // Set target FPS, 0 is invalid...   set it very high to attempt to go very high.
        backgroundColor = Color.BLACK  // set backgroundColor to set the clear color of the clear() function
        val maxVel = 1000f
        fun rColor() = Color(Color.HSBtoRGB(Math.random().toFloat(),Math.random().toFloat(),Math.random().toFloat()))
        repeat(200){
            val e = Entity().apply {        // Entities are the building block of all things.
                pos.x = Math.random().toFloat() * width        // pos is built into Entity for your convenience.
                pos.y = Math.random().toFloat() * height
                props["upSpeed"] = Math.random().toFloat() * maxVel  // for storing other information, use the props key map
                props["color"] = rColor()
                updaters.add{// updaters is a list of functions to call when updating. type is "Entity.(Float) -> Unit"
                    val spd = props["upSpeed"] as Float
                    pos.y -= spd * it
                    if (pos.y < -32){
                        pos.y = height + 8f
                        pos.x = Math.random().toFloat() * width
                        props["upSpeed"] = Math.random().toFloat() * maxVel
                        props["color"] = rColor()
                    }
                }
                drawers.add{// A list of functions to call upon drawing, of type "Entity.(Graphics2D) -> Unit"
                    val h = (64f * (props["upSpeed"] as Float)/maxVel).toInt()
                    val w = when (h){
                        in 0..16 -> 2
                        in 16..32 -> 4
                        in 32..48 -> 6
                        in 48..64 -> 8
                        else -> 2
                    }
                    it.color = props["color"] as Color
                    it.fillOval(-w/2,-(h/2),w,h)
                }
            }
            // sceneRoot is acting as a container entity for all the others. You can add whatever you want though.
            // there are two add methods for Entity, they both add a new child entity. This one adds one you already made
            // the other method allows you to add and create it at the same time.
            // add returns the child entity to allow easy chaining of children.
            sceneRoot.add(e)
        }
    }

    // Only an init function?! Where's the rest of the engine code!
    // ApplicationAdapter implements the engine methods
    // All it is doing is preventing you from needing to implement unused methods
    // it is adding the sceneRoot entity and updating it, and clearing the screen and drawing it. That is all.
    // No need to include anything else for simple scenes and hacking at random ideas.
}
