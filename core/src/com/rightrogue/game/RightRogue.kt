package com.rightrogue.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.states.PlayState



class RightRogue : ApplicationAdapter() {
    companion object {
        val WIDTH: Int = 1024
        val HEIGHT: Int = 512
    }


    private lateinit var batch: SpriteBatch
    private lateinit var block: Texture
    private lateinit var gsm: GameStateManager


    override fun create() {
        gsm = GameStateManager(this)
        batch = SpriteBatch()
        block = Texture("block32.png")
        gsm.pushState(PlayState(gsm))
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
