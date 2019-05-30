package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.RightRogue
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
    private fun handlePlayerMovement(state: PlayState, enemies: MutableList<Entity>, dt: Float){
        //gravity
        acceleration.y += 1440f * dt

        //if the player velocity is higher/lower than the max/min, set it to the max/min.
        if (velocity.x > RightRogue.MAX_VEL) velocity.x = RightRogue.MAX_VEL
        if (velocity.x < -RightRogue.MAX_VEL) velocity.x = -RightRogue.MAX_VEL
        if (velocity.y > RightRogue.MAX_VEL) velocity.y = RightRogue.MAX_VEL
        if (velocity.y < -RightRogue.MAX_VEL) velocity.y = -RightRogue.MAX_VEL

        //scales the velocity and acceleration based on the elapsed time
        velocity.add(acceleration.scl(dt))
        acceleration.scl(1/dt)
        velocity.scl(dt)

        //Checks collisions
        //adds the player's velocity to their position, then checks to see if that moved them into a block/enemy. If it did, then it moves them to the edge of the block/enemy.
        rectangle.y += velocity.y
        for ( i in 0 until state.map.layout.size ){
            for ( j in 0 until state.map.layout[i].size) {
                if (state.map.layout[i][j]?.rectangle != null && rectangle.overlaps(state.map.layout[i][j]?.rectangle)) {
                    if (velocity.y > 0) {
                        grounded = true
                        rectangle.y = state.map.layout[i][j]!!.rectangle.y - rectangle.height
                    }
                    else rectangle.y = state.map.layout[i][j]!!.rectangle.y + state.map.layout[i][j]!!.rectangle.height
                    velocity.y = 0f
                    acceleration.y = 0f
                }
            }
        }
        for ( enemy in enemies ) {
            if ( rectangle.overlaps(enemy.rectangle)) {
                if (velocity.y > 0) {
                    grounded = true
                    rectangle.y = enemy.rectangle.y - rectangle.height
                }
                else rectangle.y = enemy.rectangle.y + enemy.rectangle.height
                velocity.y = 0f
                acceleration.y = 0f
            }
        }

        rectangle.x += velocity.x
        for ( i in 0 until state.map.layout.size ){
            for ( j in 0 until state.map.layout[i].size) {
                if (state.map.layout[i][j]?.rectangle != null && rectangle.overlaps(state.map.layout[i][j]?.rectangle)) {
                    if ( velocity.y > 0 ) {
                        grounded = true
                        acceleration.y = 0f
                    }
                    if (velocity.x > 0) {
                        rectangle.x = state.map.layout[i][j]!!.rectangle.x - rectangle.width
                    }
                    else rectangle.x = state.map.layout[i][j]!!.rectangle.x + state.map.layout[i][j]!!.rectangle.width
                    velocity.x = 0f
                    acceleration.x = 0f
                }
            }
        }
        for ( enemy in enemies ) {
            if ( rectangle.overlaps(enemy.rectangle)) {
                if (velocity.x > 0) {
                    rectangle.x = enemy.rectangle.x - rectangle.width
                }
                else rectangle.x = enemy.rectangle.x + enemy.rectangle.width
                velocity.x = 0f
                acceleration.x = 0f
            }
        }

        //prevents the player from going off of the screen to the left.
        if (rectangle.x < state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f) rectangle.x = state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f

        //sets the player's sprite's location to the player's rectangle's location.
        sprite.x = rectangle.x
        sprite.y = rectangle.y

        velocity.scl(1/dt)
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
        handlePlayerMovement(state, enemies, dt)
        attackCooldown += dt
        if ( attacking ) {
            attack(this, enemies)
        }
    }


}