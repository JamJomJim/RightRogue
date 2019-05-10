package com.rightrogue.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.rightrogue.game.RightRogue

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = RightRogue.PIXEL_WIDTH
        config.height = RightRogue.PIXEL_HEIGHT
        LwjglApplication(RightRogue(), config)
    }
}
