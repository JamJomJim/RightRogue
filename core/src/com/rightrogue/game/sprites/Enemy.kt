package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.states.PlayState

class Enemy(xPos: Float, yPos: Float, width: Float, height: Float, texture: Texture) : Entity(xPos, yPos, width, height, texture){
    override val maxHealth = 5
    override var currentHealth = 5
    override var regeneration = 0

    init{
        direction = "LEFT"
    }

    var rangeOfAttack = Rectangle(rectangle.x - attackRange, rectangle.y, rectangle.width + 2 * attackRange, rectangle.height + 2 * attackRange)
    override fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        super.update(state, allies, enemies, dt)
        handleMovement(state, enemies, dt)
        attackCooldown += dt
        if ( !attacking && attackCooldown > 0.5f ) {
            for (enemy in enemies) {
                if (rangeOfAttack.overlaps(enemy.rectangle)) {
                    attacking = true
                }
            }
        }
        if ( attacking ) {
            attackDelay += dt
            if ( attackDelay > 1f) {
                attack( enemies )
            }
        }

    }


}