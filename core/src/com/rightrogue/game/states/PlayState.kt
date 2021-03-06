package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.rightrogue.game.Map
import com.rightrogue.game.RightRogue
import com.rightrogue.game.rand
import com.rightrogue.game.sprites.Enemy
import com.rightrogue.game.sprites.Player
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.rightrogue.game.sprites.Entity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.Exception

class PlayState(private var gsm: GameStateManager) : State(){

    private val shapeRenderer = ShapeRenderer()
    private val stage = Stage(ExtendViewport(RightRogue.PIXEL_WIDTH.toFloat(), RightRogue.PIXEL_HEIGHT.toFloat()), gsm.game.batch)
    private val labelStyle = LabelStyle()
    private val skin = Skin()
    private val distLabel : Label
    private val playerTextures = Texture("player.png")
    private val enemyTextures = Texture("defaultEnemy.png")

    var player : Player
    private var allies = mutableListOf<Entity>()
    private var enemies = mutableListOf<Entity>()

    private var prevDistanceCompleted = 0
    private var currDistanceCompleted = 0

    var map = Map(RightRogue.BLOCK_WIDTH + 2, RightRogue.BLOCK_HEIGHT)

    private var save: Preferences = Gdx.app.getPreferences("My Preferences")
    private var moshi: Moshi = Moshi.Builder().build()
    private var mapJsonAdapter: JsonAdapter<Array<Array<String>>> = moshi.adapter(Array<Array<String>>::class.java)

    init {
        //sets this playState as the thing that handles input
        Gdx.input.inputProcessor = this
        //stops the phone from quitting the app when the back button is pressed by giving the command to the app instead
        Gdx.input.isCatchBackKey = true

        //initializes the camera
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())

        //todo get a font that suits the game better
        //sets up the font used throughout the game
        skin.add("LS90", BitmapFont(Gdx.files.internal("fonts/LS90.fnt")))
        skin.getFont("LS90").data.setScale(0.5f)
        labelStyle.font = skin.getFont("LS90")
        labelStyle.fontColor = Color.WHITE
        skin.add("LS90", labelStyle)

        //sets up the table that all of the UI elements sit in
        val table = Table()
        table.debug = true
        table.setFillParent(true)
        table.right().top()

        //adds the distance completed label to the top right
        distLabel = Label("Play", skin, "LS90")
        table.add(distLabel)

        stage.addActor(table)

        //adds an enemy right in front of the player for testing purposes
        // enemies.add(Enemy(1f, RightRogue.BLOCK_HEIGHT / 2f, 24f, 32f, enemyTextures))
        // enemies.add(Enemy(2f, RightRogue.BLOCK_HEIGHT / 2f, 24f, 32f, enemyTextures))

        //checks to see if there is a save, and loads it if there is. If the save is incompatible, it deletes it.
        if ( !save.getString("mapSave").isNullOrEmpty() ) {
            try {
                loadGame()
            }
            catch (e: Exception){
                save.clear()
            }
        }

        //initializes the player
        player = Player(0f, map.gameMap[0].lastIndexOf(null).toFloat(), 24f, 32f, playerTextures )
        allies.add(player)
    }

    private fun saveGame() {
        //saves the map.
        map.saveMap()
        save.putString("mapSave", mapJsonAdapter.toJson(map.layout.toTypedArray()))

        //saves the distance completed.
        val playerDistanceFromStartPoint = ((player.rectangle.x - map.gameMap[0][0]!!.position.x) / 48f).toInt()
        save.putInteger("distance", currDistanceCompleted + prevDistanceCompleted - playerDistanceFromStartPoint)
        save.flush()
    }

    private fun loadGame() {
        map.loadMap(mapJsonAdapter.fromJson(save.getString("mapSave"))!!.toMutableList())
        prevDistanceCompleted = save.getInteger("distance")
    }

    //if the back button is pushed, pause the game
    override fun keyDown(keycode: Int): Boolean {
        if ( keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            saveGame()
            gsm.pushState(PauseState(gsm))
            return true
        }
        return false
    }

    override fun handleInput(dt: Float) {

    }

    override fun update(dt: Float) {

        //updates the player and enemies
        player.update(this, allies, enemies, dt)
        for ( enemy in enemies ) {
            enemy.update(this, enemies, allies, dt)
        }
        //not sure this does anything since nothing within the stage moves?
        stage.act(dt)
        //updates the map when the player has gone far enough right.
        if (player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK > currDistanceCompleted && player.sprite.x / RightRogue.PIXELS_PER_BLOCK >= 3) {
            currDistanceCompleted = player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK
            map.updateMap(currDistanceCompleted)
            //has a 1 in 8 chance of spawning an enemy every time more map generates.
            if ( rand(1, 8) == 1)
                enemies.add(Enemy(RightRogue.BLOCK_WIDTH + player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK.toFloat() - 1, map.gameMap.last().indexOfLast { it == null }.toFloat(),24f,32f, enemyTextures))
        }

        //updates distance completed.
        else if ( player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK > currDistanceCompleted )
            currDistanceCompleted = player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK

        //updates the camera to follow the player as they move to the right.
        if (cam.position.x < player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 2 * RightRogue.PIXELS_PER_BLOCK){
            cam.position.x += player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 2 * RightRogue.PIXELS_PER_BLOCK - cam.position.x
            cam.update()
        }

        //removes enemies from the game if their health is 0
        val iter = enemies.iterator()
        while (iter.hasNext()) {
            if ( iter.next().currentHealth <= 0 ) {
                iter.remove()
            }
        }

        if ( player.currentHealth <= 0) {
            gsm.pushState(KilledState(gsm))
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()

        //draws all of the non-null blocks in map
        map.gameMap.flatMap{ it.toList() }
                .filterNotNull()
                .forEach { it.draw(sb) }

        //draws the player and enemies
        player.draw(sb)
        for ( enemy in enemies ) {
            enemy.draw(sb)
        }

        sb.end()

        //updates the distance completed label
        distLabel.setText("Distance Completed: " + (currDistanceCompleted + prevDistanceCompleted))
        stage.draw()
        shapeRenderer.projectionMatrix = cam.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        //Draws the player's health bar
        shapeRenderer.color = Color.RED
        shapeRenderer.rect(player.sprite.x + 2, player.sprite.y - 10, (player.sprite.width - 4), 5f)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(player.sprite.x + 2, player.sprite.y - 10, (player.currentHealth / player.maxHealth.toFloat() ) * (player.sprite.width - 4), 5f)

        //Draws the enemies' health bar if it isn't full.
        for ( enemy in enemies ) {
            if ( enemy.currentHealth != enemy.maxHealth) {
                shapeRenderer.color = Color.RED
                shapeRenderer.rect(enemy.sprite.x + 2, enemy.sprite.y - 10, ( enemy.sprite.width - 4 ), 5f)
                shapeRenderer.color = Color.GREEN
                shapeRenderer.rect(enemy.sprite.x + 2, enemy.sprite.y - 10, ( enemy.currentHealth / enemy.maxHealth.toFloat() ) * ( enemy.sprite.width - 4 ), 5f)
            }
        }
        shapeRenderer.end()
    }

    override fun dispose() {
        stage.dispose()
    }
}