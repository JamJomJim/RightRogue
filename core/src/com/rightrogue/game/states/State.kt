package com.rightrogue.game.states

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.GameStateManager
import com.rightrogue.game.RightRogue

abstract class State(gsm : GameStateManager) {
    open val cam = OrthographicCamera()

    abstract fun handleInput(dt: Float)
    abstract fun update(dt: Float)
    abstract fun render(sb: SpriteBatch)
}
