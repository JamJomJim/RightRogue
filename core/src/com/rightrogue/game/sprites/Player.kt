package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.states.PlayState
import kotlin.math.absoluteValue


class Player (xPos: Float, yPos: Float, width: Float, height: Float, texture: TextureRegion) : Entity(xPos, yPos, width, height, texture){

    private var attacking = false
    private var attackCooldown = 1f

    //todo add in movement and attack animations
    private fun attack(entity: Entity, enemies : MutableList<Entity>){
        attackCooldown = 0f
        val hitbox = Rectangle(entity.rectangle.x, entity.rectangle.y - 8, entity.rectangle.width + 16, entity.rectangle.height + 16)
        for ( enemy in enemies ) {
            if ( hitbox.overlaps(enemy.rectangle)) {
                enemy.currentHealth -= 5
                break
            }
        }
        attacking = false
    }

    private fun handleInput(){

        val x0 = Gdx.input.getX(0) / Gdx.graphics.width.toFloat() * 960
        val x1 = Gdx.input.getX(1) / Gdx.graphics.width.toFloat() * 960
        val xVel0 = Gdx.input.getDeltaX(0) / Gdx.graphics.width.toFloat() * 960
        val xVel1 = Gdx.input.getDeltaX(1) / Gdx.graphics.width.toFloat() * 960
        val yVel0 = Gdx.input.getDeltaY(0) / Gdx.graphics.width.toFloat() * 480
        val yVel1 = Gdx.input.getDeltaY(1) / Gdx.graphics.width.toFloat() * 480

        val touchLeft = Gdx.input.isTouched(0) && x0 < 240 || Gdx.input.isTouched(1) && x1 < 240
        val touchRight = Gdx.input.isTouched(0) && x0 >= 240 && x0 < 480 || Gdx.input.isTouched(1) && x1 >= 240 && x0 < 480
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
            touchRight -> velocity.x = 128f
            touchLeft -> velocity.x = -128f
            else -> velocity.x  = 0f
        }
    }

    override fun update(state: PlayState, enemies : MutableList<Entity>, dt: Float){
        handleInput()
        handleMovement(state, enemies, dt)
        attackCooldown += dt
        if ( attacking ) {
            attack(this, enemies)
        }
    }


}