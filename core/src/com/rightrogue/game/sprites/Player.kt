package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.rightrogue.game.RightRogue
import com.rightrogue.game.states.PlayState


//todo add generic entity class
class Player (x: Float, y: Float){
    private var velocity: Vector2 = Vector2(0f,0f)
    private var acceleration: Vector2 = Vector2(0f,0f)
    private var texture: Texture = Texture("placeholder.png")
    var sprite = Sprite(texture)
    var rectangle = Rectangle(sprite.x, sprite.y, 32f, 32f)
    private var grounded = true

    init {
        rectangle.x = x * 32
        rectangle.y = y * 32
        sprite.x = x * 32
        sprite.y = y * 32
    }

    //todo add in movement animations
    fun handleInput(state: PlayState, dt: Float){
        //todo separate the input handling and the actual update of player
        //todo change to be scaled values.

        //left and right movement
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> velocity.x = 128f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> velocity.x = -128f
            else -> velocity.x = 0f
        }

        //jumping
        if (Gdx.input.isKeyPressed(Input.Keys.UP) and grounded){
            velocity.y = -128f
            grounded = false
        }
        //lengthens jump if you hold down the jump button
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) and !grounded and (velocity.y < 0)){
            acceleration.y += -32f * dt
        }

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
        //adds the player's velocity to their position, then checks to see if that moved them into a block. If it did, then it moves them to the edge of the block.
        rectangle.y += velocity.y
        for ( i in 0..4 ){
            for ( j in 0 until state.map.layout[i].size) {
                if (state.map.layout[i][j]?.rectangle != null && rectangle.overlaps(state.map.layout[i][j]?.rectangle)) {
                    if (velocity.y > 0) {
                        grounded = true
                        rectangle.y = state.map.layout[i][j]!!.rectangle.y - 32f
                    }
                    else rectangle.y = state.map.layout[i][j]!!.rectangle.y + 32f
                    velocity.y = 0f
                    acceleration.y = 0f
                }
            }
        }

        rectangle.x += velocity.x
        for ( i in 0..4 ){
            for ( j in 0 until state.map.layout[i].size) {
                if (state.map.layout[i][j]?.rectangle != null && rectangle.overlaps(state.map.layout[i][j]?.rectangle)) {
                    if (velocity.x > 0) {
                        rectangle.x = state.map.layout[i][j]!!.rectangle.x - 32f
                    }
                    else rectangle.x = state.map.layout[i][j]!!.rectangle.x + 32f
                    velocity.x = 0f
                    acceleration.x = 0f
                }
            }
        }

        //prevents the player from going off of the screen to the left.
        if (rectangle.x < state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f) rectangle.x = state.cam.position.x - RightRogue.PIXEL_WIDTH / 2f


        //sets the player's sprite's location to the player's rectangle's location.
        sprite.x = rectangle.x
        sprite.y = rectangle.y

        velocity.scl(1/dt)
    }

    fun update(dt: Float){

    }
}