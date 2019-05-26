package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.rightrogue.game.states.PlayState

class Enemy(xPos: Float, yPos: Float, width: Float, height: Float, texture: Texture) : Entity(xPos, yPos, width, height, texture){
    fun handleInput(dt: Float){

    }
    fun update(state: PlayState, dt: Float){
        handleMovement(state, dt)
    }
}