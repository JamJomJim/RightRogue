package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.rightrogue.game.states.PlayState


class Player (xPos: Float, yPos: Float, width: Float, height: Float, texture: Texture) : Entity(xPos, yPos, width, height, texture){

    //todo add in movement animations
    fun handleInput(dt: Float){

        //left and right movement
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)-> velocity.x = 128f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) -> velocity.x = -128f
            else -> velocity.x = 0f
        }

        //jumping
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) && grounded){
            velocity.y = -128f
            grounded = false
        }

        //lengthens jump if you hold down the jump button
        else if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) && !grounded && velocity.y < 0)
            acceleration.y += -960f * dt
    }

    fun update(state: PlayState, dt: Float){
        handleMovement(state, dt)
    }
}