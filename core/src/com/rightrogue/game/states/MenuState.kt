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
        skin.add("LS90", BitmapFont(Gdx.files.internal("fonts/manaspace.fnt")))

        //set defaults for different ui elements
        val textButtonStyle = TextButtonStyle()
        textButtonStyle.font = skin.getFont("LS90")
        textButtonStyle.fontColor = Color.WHITE
        textButtonStyle.overFontColor = Color.GRAY
        textButtonStyle.downFontColor = Color.GRAY
        skin.add("default", textButtonStyle)

        //sets up the table that all of the buttons are sitting in.
        val table = Table()
        table.debug = true
        table.setFillParent(true)

        //sets up the play button
        val playButton = TextButton("Play", skin)
        playButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                gsm.setState(PlayState(gsm))
            }
        })

        val optionsButton = TextButton("Options", skin)
        optionsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                gsm.setState(OptionsState(gsm))
            }
        })

        val quitButton = TextButton("Quit", skin)
        quitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        //adds all of the various buttons to the initial table.
        table.add(playButton).space(10f)
        table.row()
        table.add(optionsButton).space(10f)
        table.row()
        table.add(quitButton).space(10f)

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