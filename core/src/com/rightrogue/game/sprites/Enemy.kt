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

        //this is where the enemy's decisions are determined.
        attackCooldown += dt
        if ( !attacking && attackCooldown > 0.5f ) {
            for (enemy in enemies) {
                if (rangeOfAttack.overlaps(enemy.rectangle)) {
                    attacking = true
                }
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
        val collidableBlocks = getCollidableBlocks(state, this)
        println(collidableBlocks)
        if (collidableBlocks[1][1] !is Block && collidableBlocks[1][0] !is Block) {
            if (direction == "RIGHT" && collidableBlocks[2][1] is Block && collidableBlocks[2][0] !is Block) {
                jumpState = "JUMPING"
            } else if (direction == "LEFT" && collidableBlocks[0][1] is Block && collidableBlocks[0][0] !is Block) {
                jumpState = "JUMPING"
            }
        }

        //If the enemy decides to attack, then stop moving and attack after 1 second.
        if ( attacking ) {
            currentMoveState = "STILL"
            attackDelay += dt
            if ( attackDelay > 1f) {
                attack( enemies )
            }
        }

        super.update(state, allies, enemies, dt)

        handleMovement(state, allies, enemies, dt)
    }
}