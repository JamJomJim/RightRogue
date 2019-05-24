package com.rightrogue.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.states.MenuState
import com.rightrogue.game.states.PlayState

class RightRogue : ApplicationAdapter() {
    companion object {
        const val MAX_VEL = 256f
        const val PIXEL_WIDTH: Int = 1024
        const val PIXEL_HEIGHT: Int = 512
        const val BLOCK_WIDTH = PIXEL_WIDTH / 32
        const val BLOCK_HEIGHT = PIXEL_HEIGHT / 32
    }


    lateinit var batch: SpriteBatch
    private lateinit var block: Texture
    lateinit var gsm: GameStateManager


    override fun create() {
        gsm = GameStateManager(this)
        batch = SpriteBatch()
        block = Texture("block32.png")
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
        block.dispose()
    }
}
