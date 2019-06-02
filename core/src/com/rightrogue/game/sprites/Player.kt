package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rightrogue.game.states.PlayState
import kotlin.math.absoluteValue
import kotlin.math.floor


class Player (xPos: Float, yPos: Float, width: Float, height: Float, texture: TextureRegion) : Entity(xPos, yPos, width, height, texture){
    override val maxHealth = 10
    override var currentHealth = 10
    override var regeneration = 1
    private val spriteSheet = Texture("warrior_m.png")
    private val textures: Array<Array<TextureRegion>> = TextureRegion.split(spriteSheet, 24, 32)
    private var playerTexture: TextureRegion
    var previousState = "STILL"
    var currentState = "STILL"
    var stateTimer = 0f
    var direction = "RIGHT"
    var runRight: Animation<TextureRegion>
    var runLeft: Animation<TextureRegion>


    init {
        val frameHolder = emptyList<TextureRegion>().toMutableList()

        for ( i in 0 until textures.size ) {
            for ( j in 0 until textures[i].size) {
                textures[i][j].flip(false, true)
            }
        }
        for ( i in 0..2 ) {
            frameHolder.add(textures[0][i])

        }
        runRight = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])
        frameHolder.clear()

        for ( i in 0..2 ) {
            frameHolder.add(textures[1][i])

        }
        runLeft = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])

        playerTexture = textures[0][1]
        sprite = Sprite(playerTexture)
    }
    //todo add in movement and attack animations
    private fun handleInput(){

        val x0 = Gdx.input.getX(0) / Gdx.graphics.width.toFloat() * 960
        val x1 = Gdx.input.getX(1) / Gdx.graphics.width.toFloat() * 960
        val xVel0 = Gdx.input.getDeltaX(0) / Gdx.graphics.width.toFloat() * 960
        val xVel1 = Gdx.input.getDeltaX(1) / Gdx.graphics.width.toFloat() * 960
        val yVel0 = Gdx.input.getDeltaY(0) / Gdx.graphics.width.toFloat() * 480
        val yVel1 = Gdx.input.getDeltaY(1) / Gdx.graphics.width.toFloat() * 480

        val touchLeft = Gdx.input.isTouched(0) && x0 < 240 || Gdx.input.isTouched(1) && x1 < 240
        val touchRight = Gdx.input.isTouched(0) && x0 >= 240 && x0 < 480 || Gdx.input.isTouched(1) && x1 >= 240 && x1 < 480
        val touchJump = Gdx.input.isTouched(0) && x0 >= 480 && yVel0.absoluteValue >= 10 || Gdx.input.isTouched(1) && x1 >= 480 && yVel1.absoluteValue >= 10
        val touchAttack = Gdx.input.isTouched(0) && x0 >= 480 && xVel0.absoluteValue >= 10 || Gdx.input.isTouched(1) && x1 >= 480 && xVel1.absoluteValue >= 10

        if ( touchJump && grounded ) {
                velocity.y = -128f
                grounded = false
        }

        if ( touchAttack && attackCooldown >= 0.5f ) {
            attacking = true
        }

        //todo need to add some acceleration to smooth out movement. The player suddenly stops when they life their finger.
        when{
            touchRight -> {
                velocity.x = 128f
                previousState = currentState
                currentState = "RIGHT"
                direction = "RIGHT"
            }
            touchLeft -> {
                velocity.x = -128f
                previousState = currentState
                currentState = "LEFT"
                direction = "LEFT"
            }
            else -> {
                velocity.x  = 0f
                previousState = currentState
                currentState = "STILL"
            }
        }
    }

    override fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        super.update(state, allies, enemies, dt)
        handleInput()
        handleMovement(state, enemies, dt)
        attackCooldown += dt
        if ( attacking ) {
            println("swinging")
            attackDelay += dt
            if ( attackDelay > 0.25f) {
                attack( enemies )
            }
        }

        when (currentState) {
            "STILL" -> {
                if ( direction == "RIGHT" ){
                    sprite.setRegion(textures[0][1])

                }
                else sprite.setRegion(textures[1][1])
            }
            "RIGHT" -> {
                sprite.setRegion(runRight.getKeyFrame(stateTimer, true))
            }
            "LEFT" -> {
                sprite.setRegion(runLeft.getKeyFrame(stateTimer, true))
            }

        }
        if ( currentState != previousState) stateTimer = 0f
        stateTimer += dt
    }

}