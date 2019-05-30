package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
import com.badlogic.gdx.utils.Logger
import com.rightrogue.game.sprites.Entity



class PlayState(private var gsm: GameStateManager) : State(){

    private val shapeRenderer = ShapeRenderer()
    private val stage = Stage(ExtendViewport(RightRogue.PIXEL_WIDTH.toFloat(), RightRogue.PIXEL_HEIGHT.toFloat()), gsm.game.batch)
    private val labelStyle = LabelStyle()
    private val skin = Skin()
    private val distLabel : Label
    private val spriteSheet = Texture("tileset32.png")
    private val textures: Array<Array<TextureRegion>> = TextureRegion.split(spriteSheet, 32, 32)
    private val playerTexture = textures[2][0]
    private val enemyTexture = textures[2][1]


    private var player : Player
    private var enemies = mutableListOf<Entity>()
    private var distanceCompleted = 0

    var map = Map(RightRogue.BLOCK_WIDTH + 2, RightRogue.BLOCK_HEIGHT)

    init {

        Gdx.input.inputProcessor = this
        Gdx.input.isCatchBackKey = true

        textures[2][0].flip(false, true)
        player = Player(0f, RightRogue.BLOCK_HEIGHT / 2f, 32f, 32f, playerTexture )

        textures[2][1].flip(false, true)

        skin.add("LS90", BitmapFont(Gdx.files.internal("fonts/LS90.fnt")))
        skin.getFont("LS90").data.setScale(0.5f)
        labelStyle.font = skin.getFont("LS90")

        labelStyle.fontColor = Color.WHITE
        skin.add("LS90", labelStyle)
        distLabel = Label("Play", skin, "LS90")

        val table = Table()
        table.debug = true
        table.setFillParent(true)
        table.right().top()

        table.add(distLabel)
        stage.addActor(table)

        enemies.add(Enemy(1f, RightRogue.BLOCK_HEIGHT / 2f, 32f, 32f, enemyTexture))
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())
    }

    override fun keyDown(keycode: Int): Boolean {
        println("Key $keycode")
        if ( keycode == Input.Keys.BACK ) {
            gsm.pushState(PauseState(gsm))
            return false
        }
        player.handleInput(keycode)
        return super.keyDown(keycode)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        println(pointer)
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun handleInput(dt: Float) {

        //pauses the game if ESC is pressed
        if ( Gdx.input.isKeyPressed(Input.Keys.ESCAPE) ) {
            gsm.pushState(PauseState(gsm))
        }
    }

    override fun update(dt: Float) {
        stage.act(dt)
        handleInput(dt)
        //updates the map when the player has gone far enough right.

        if (player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK > distanceCompleted && player.sprite.x / RightRogue.PIXELS_PER_BLOCK >= 3) {
            distanceCompleted = player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK

            map.updateMap(distanceCompleted)

            //has a 1 in 8 chance of spawning an enemy every time more map generates.
            if ( rand(1, 8) == 1)
                enemies.add(Enemy(RightRogue.BLOCK_WIDTH + player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK.toFloat() - 1, map.layout.last().indexOfLast { it == null }.toFloat(),32f,32f, enemyTexture))
            
        }
        else if ( player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK >= distanceCompleted )
            distanceCompleted = player.sprite.x.toInt() / RightRogue.PIXELS_PER_BLOCK

        //updates the camera to follow the player as they move to the right.
        if (cam.position.x < player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 2 * RightRogue.PIXELS_PER_BLOCK){
            cam.position.x += player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 2 * RightRogue.PIXELS_PER_BLOCK - cam.position.x
            cam.update()
        }


        for ( enemy in enemies ) {
            enemy.update(this, enemies, dt)
        }

        //removes enemies from the game once their health is 0
        val iter = enemies.iterator()
        while (iter.hasNext()) {
            if ( iter.next().currentHealth <= 0 ) {
                iter.remove()
            }
        }

        player.update(this, enemies, dt)

    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()

        //draws all of the non-null blocks in map
        map.layout.flatMap{ it.toList() }
                .filterNotNull()
                .forEach { it.draw(sb) }

        //draws the player and enemies
        player.draw(sb)
        for ( enemy in enemies ) {
            enemy.draw(sb)
        }

        sb.end()

        //updates the distance completed label
        distLabel.setText("Distance Completed: $distanceCompleted")
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