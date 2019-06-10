package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.states.PlayState

class Enemy(xPos: Float, yPos: Float, private val width: Float, height: Float, texture: Texture) : Entity(xPos, yPos, width, height, texture){
    override val maxHealth = 5
    override var currentHealth = 5
    override var regeneration = 0

    private var rangeOfAttack = Rectangle(rectangle.x - attackRange, rectangle.y, rectangle.width + 2 * attackRange, rectangle.height + 2 * attackRange)

    init{
        direction = "LEFT"
    }

    override fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        super.update(state, allies, enemies, dt)

        attackCooldown += dt
        if ( !attacking && attackCooldown > 0.5f ) {
            for (enemy in enemies) {
                if (rangeOfAttack.overlaps(enemy.rectangle)) {
                    attacking = true
                }
            }
        }

        if ( attacking ) {
            currentMoveState = "STILL"
            attackDelay += dt
            if ( attackDelay > 1f) {
                attack( enemies )
            }
        }

        when {
            state.player.rectangle.x >= rectangle.x + width + 4f -> {
                previousMoveState = currentMoveState
                currentMoveState = "RIGHT"
                direction = "RIGHT"
            }
            state.player.rectangle.x + state.player.width + 4f <= rectangle.x -> {
                previousMoveState = currentMoveState
                currentMoveState = "LEFT"
                direction = "LEFT"
            }
            else -> {
                previousMoveState = currentMoveState
                currentMoveState = "STILL"
            }
        }

        handleMovement(state, allies, enemies, dt)
    }
}