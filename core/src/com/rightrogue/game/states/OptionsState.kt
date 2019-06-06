package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.rightrogue.game.RightRogue

class OptionsState(gsm: GameStateManager) : State() {
    private val stage = Stage(ExtendViewport(RightRogue.PIXEL_WIDTH.toFloat(), RightRogue.PIXEL_HEIGHT.toFloat()), gsm.game.batch)
    var save: Preferences = Gdx.app.getPreferences("My Preferences")

    init {
        Gdx.input.inputProcessor = stage

        //set up skins and fonts
        val skin = Skin()
        skin.add("LS90", BitmapFont(Gdx.files.internal("fonts/LS90.fnt")))

        //set defaults for different ui elements
        val textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.font = skin.getFont("LS90")
        textButtonStyle.fontColor = Color.WHITE
        textButtonStyle.overFontColor = Color.GRAY
        textButtonStyle.downFontColor = Color.GRAY
        skin.add("default", textButtonStyle)

        val table = Table()
        table.debug = true
        table.setFillParent(true)

        val deleteSaveButton = TextButton("Delete Save", skin)
        deleteSaveButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                save.clear()
            }
        })

        val backButton = TextButton("Back", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                gsm.setState(MenuState(gsm))
            }
        })

        table.add(deleteSaveButton).space(10f)
        table.row()
        table.add(backButton).space(10f)

        stage.addActor(table)
    }
    override fun handleInput(dt: Float) {

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