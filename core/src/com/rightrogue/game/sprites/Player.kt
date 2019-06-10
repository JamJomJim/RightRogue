package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.rightrogue.game.RightRogue
import com.rightrogue.game.states.PlayState
import kotlin.math.absoluteValue

class Player (xPos: Float, yPos: Float, val width: Float, height: Float, texture: Texture) : Entity(xPos, yPos, width, height, texture){
    override val maxHealth = 10
    override var currentHealth = 10
    override var regeneration = 1

    init{
        currentHealth = maxHealth
    }

    //todo add attack animations
    private fun handleInput(){

        val x0 = Gdx.input.getX(0) / Gdx.graphics.width.toFloat() * 960
        val x1 = Gdx.input.getX(1) / Gdx.graphics.width.toFloat() * 960
        val xVel0 = Gdx.input.getDeltaX(0) / Gdx.graphics.width.toFloat() * 960
        val xVel1 = Gdx.input.getDeltaX(1) / Gdx.graphics.width.toFloat() * 960
        val yVel0 = Gdx.input.getDeltaY(0) / Gdx.graphics.width.toFloat() * 480
        val yVel1 = Gdx.input.getDeltaY(1) / Gdx.graphics.width.toFloat() * 480

        val touchLeft = Gdx.input.isTouched(0) && x0 < 240 || Gdx.input.isTouched(1) && x1 < 240
        val touchRight = Gdx.input.isTouched(0) && x0 >= 240 && x0 < 480 || Gdx.input.isTouched(1) && x1 >= 240 && x1 < 480
        val touchJump = Gdx.input.isTouched(0) && x0 >= 480 && yVel0 <= -10 || Gdx.input.isTouched(1) && x1 >= 480 && yVel1 <= -10
        val touchAttack = Gdx.input.isTouched(0) && x0 >= 480 && xVel0.absoluteValue >= 10 || Gdx.input.isTouched(1) && x1 >= 480 && xVel1.absoluteValue >= 10

        if ( touchJump && jumpState == "GROUNDED" ) {
            jumpState = "JUMPING"
        }

        if ( touchAttack && attackCooldown >= 0.5f ) {
            attacking = true
        }

        //todo need to add some acceleration to smooth out movement. The player suddenly stops when they life their finger.
        when{
            touchRight -> {
                previousMoveState = currentMoveState
                currentMoveState = "RIGHT"
                direction = "RIGHT"
            }
            touchLeft -> {
                previousMoveState = currentMoveState
                currentMoveState = "LEFT"
                direction = "LEFT"
            }
            else -> {
                previousMoveState = currentMoveState
                currentMoveState = "STILL"
            }
        }
    }

    override fun handleMovement(state: PlayState, allies: MutableList<Entity>, enemies: MutableList<Entity>, dt: Float){
        //prevents the player from going off of the screen to the left.
        if (rectangle.x < state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f) rectangle.x = state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f
        super.handleMovement(state, allies, enemies, dt)
    }

    override fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        super.update(state, allies, enemies, dt)
        handleInput()
        handleMovement(state, allies, enemies, dt)
        attackCooldown += dt
        if ( attacking ) {
            attackDelay += dt
            if ( attackDelay > 0.25f) {
                attack( enemies )
            }
        }
    }
}