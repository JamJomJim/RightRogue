package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.states.PlayState

class Enemy(xPos: Float, yPos: Float, width: Float, height: Float, texture: TextureRegion) : Entity(xPos, yPos, width, height, texture){
    var rangeOfAttack = Rectangle(rectangle.x - attackRange, rectangle.y, rectangle.width + 2 * attackRange, rectangle.height + 2 * attackRange)
    override fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        handleMovement(state, enemies, dt)
        for ( enemy in enemies ) {
            if ( rangeOfAttack.overlaps(enemy.rectangle)) {
                attack(enemies)
            }
        }

    }


}