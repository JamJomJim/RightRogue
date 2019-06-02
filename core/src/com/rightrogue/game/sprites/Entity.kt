package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.rightrogue.game.RightRogue
import com.rightrogue.game.states.PlayState

abstract class Entity(xPos: Float, yPos: Float, width: Float, height: Float, spriteSheet : Texture){
    abstract val maxHealth: Int
    abstract var currentHealth: Int
    abstract var regeneration: Int
    //private val spriteSheet = Texture("warrior_m.png")
    var previousState = "STILL"


    val textures: Array<Array<TextureRegion>> = TextureRegion.split(spriteSheet, 24, 32)

    var regenTimer = 0f
    var velocity: Vector2 = Vector2(0f,0f)
    var acceleration: Vector2 = Vector2(0f,0f)
    var sprite = Sprite(textures[0][1])
    var rectangle = Rectangle(sprite.x, sprite.y, width, height)
    var grounded = true
    var attackDamage = 1
    var attackRange = 16f
    var attacking = false
    var attackCooldown = 1f
    var attackDelay = 0f

    var currentState = "STILL"
    var stateTimer = 0f
    var direction = "RIGHT"
    var runRight: Animation<TextureRegion>
    var runLeft: Animation<TextureRegion>

    init {
        val frameHolder = emptyList<TextureRegion>().toMutableList()

        for ( i in 0 until textures.size ) {
            for ( j in 0 until textures[i].size) {
                textures[i][j].flip(false, true)
            }
        }
        for ( i in 0..2 ) {
            frameHolder.add(textures[0][i])

        }
        runRight = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])
        frameHolder.clear()

        for ( i in 0..2 ) {
            frameHolder.add(textures[1][i])
        }
        runLeft = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])
        rectangle.x = xPos * RightRogue.PIXELS_PER_BLOCK.toFloat()
        rectangle.y = yPos * RightRogue.PIXELS_PER_BLOCK.toFloat()
        sprite.x = rectangle.x
        sprite.y = rectangle.y
    }

    open fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        regenTimer += dt
        if ( regenTimer >= 3 ) {
            regenTimer = 0f
            if ( currentHealth < maxHealth ) {
                currentHealth += regeneration
                if ( currentHealth > maxHealth ) {
                    currentHealth = maxHealth
                }
            }
        }


        when (currentState) {
            "STILL" -> {
                if ( direction == "RIGHT" ){
                    sprite.setRegion(textures[0][1])

                }
                else sprite.setRegion(textures[1][1])
            }
            "RIGHT" -> {
                sprite.setRegion(runRight.getKeyFrame(stateTimer, true))
            }
            "LEFT" -> {
                sprite.setRegion(runLeft.getKeyFrame(stateTimer, true))
            }

        }
        if ( currentState != previousState) stateTimer = 0f
        stateTimer += dt
    }

    fun attack(enemies : MutableList<Entity>){
        attackCooldown = 0f
        attackDelay = 0f
        println("attack")
        val hitbox = Rectangle(rectangle.x - attackRange, rectangle.y, rectangle.width + 2 * attackRange, rectangle.height + 2 * attackRange)
        for ( enemy in enemies ) {
            if ( hitbox.overlaps(enemy.rectangle)) {
                enemy.currentHealth -= attackDamage
                break
            }
        }
        attacking = false
    }

    fun handleMovement(state: PlayState, enemies: MutableList<Entity>, dt: Float){
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
            if ( enemy != this && rectangle.overlaps(enemy.rectangle)) {
                if (velocity.y > 0) {
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
            if ( enemy != this && rectangle.overlaps(enemy.rectangle)) {
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

    fun draw(sb: SpriteBatch) {
        sb.draw(sprite, sprite.x, sprite.y)
    }
}