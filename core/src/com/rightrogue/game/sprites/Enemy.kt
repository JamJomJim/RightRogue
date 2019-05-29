package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.rightrogue.game.states.PlayState

class Enemy(xPos: Float, yPos: Float, width: Float, height: Float, texture: TextureRegion) : Entity(xPos, yPos, width, height, texture){

    override fun update(state: PlayState, enemies: MutableList<Entity>, dt: Float){
        handleMovement(state, dt)
    }


}