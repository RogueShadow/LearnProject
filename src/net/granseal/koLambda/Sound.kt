package net.granseal.koLambda

import java.io.File
import javax.sound.sampled.AudioSystem

class Sound(file: String) {

    val ais = AudioSystem.getAudioInputStream(File(file))
    val clip = AudioSystem.getClip()
    init {
        clip.open(ais)
        ais.close()
    }

    fun play() {
        if (clip.isRunning || clip.isActive) clip.stop()
        clip.framePosition = 0
        clip.start()
    }
}