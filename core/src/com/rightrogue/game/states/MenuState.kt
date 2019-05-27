package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.rightrogue.game.RightRogue
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class MenuState(gsm: GameStateManager) : State(){
    private val stage = Stage(ExtendViewport(RightRogue.PIXEL_WIDTH.toFloat(), RightRogue.PIXEL_HEIGHT.toFloat()), gsm.game.batch)

    init {
        Gdx.input.inputProcessor = stage

        //set up skins and fonts
        val skin = Skin()
        skin.add("LS90", BitmapFont(Gdx.files.internal("fonts/LS90.fnt")))

        //set defaults for different ui elements
        val textButtonStyle = TextButtonStyle()
        textButtonStyle.font = skin.getFont("LS90")
        textButtonStyle.fontColor = Color.WHITE
        textButtonStyle.overFontColor = Color.GRAY
        textButtonStyle.downFontColor = Color.GRAY
        skin.add("default", textButtonStyle)


        val table = Table()
        table.debug = true
        table.setFillParent(true)


        val button = TextButton("Play", skin)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                gsm.setState(PlayState(gsm))
            }
        })

        table.add(button)
        stage.addActor(table)
    }

    override fun handleInput(dt: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(dt: Float) {
        stage.act(dt)
    }

    override fun render(sb: SpriteBatch) {
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}