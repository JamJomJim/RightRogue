package com.rightrogue.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.rightrogue.game.RightRogue

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = RightRogue.WIDTH
        config.height = RightRogue.HEIGHT
        LwjglApplication(RightRogue(), config)
    }
}
