package com.rightrogue.game.states

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch


abstract class State : InputAdapter() {
    open val cam = OrthographicCamera()

    abstract fun handleInput(dt: Float)
    abstract fun update(dt: Float)
    abstract fun render(sb: SpriteBatch)
    abstract fun dispose()

}
