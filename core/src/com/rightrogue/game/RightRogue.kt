package com.rightrogue.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils.random
import com.rightrogue.game.states.GameStateManager
import com.rightrogue.game.states.MenuState
import com.rightrogue.game.states.PauseState
import com.rightrogue.game.states.PlayState

fun rand(from: Int, to: Int) : Int {
    return random.nextInt(to - from + 1) + from
}

class RightRogue : ApplicationAdapter() {

    companion object {
        const val MAX_VEL = 256f
        const val PIXEL_WIDTH: Int = 960
        const val PIXEL_HEIGHT: Int = 480
        const val PIXELS_PER_BLOCK = 48
        const val BLOCK_WIDTH = PIXEL_WIDTH / PIXELS_PER_BLOCK
        const val BLOCK_HEIGHT = PIXEL_HEIGHT / PIXELS_PER_BLOCK
    }


    lateinit var batch: SpriteBatch
    lateinit var gsm: GameStateManager


    override fun create() {
        gsm = GameStateManager(this)
        batch = SpriteBatch()
        gsm.pushState(MenuState(gsm))

    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        gsm.update(Gdx.graphics.deltaTime)
        gsm.render(batch)
    }

    override fun dispose() {
        batch.dispose()
    }

    override fun pause() {
        if (gsm.currentState() is PlayState) gsm.pushState(PauseState(gsm))
        super.pause()
    }

}
