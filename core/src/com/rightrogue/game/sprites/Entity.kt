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
    private var regenTimer = 0f

    private val textures: Array<Array<TextureRegion>> = TextureRegion.split(spriteSheet, 24, 32)
    private var runRight: Animation<TextureRegion>
    private var runLeft: Animation<TextureRegion>

    private var acceleration: Vector2 = Vector2(0f,0f)
    var velocity: Vector2 = Vector2(0f,0f)
    var sprite = Sprite(textures[0][1])
    var rectangle = Rectangle(sprite.x, sprite.y, width, height)

    private var attackDamage = 1
    var attackRange = 16f
    var attacking = false
    var attackCooldown = 1f
    var attackDelay = 0f

    private var stateTimer = 0f
    var currentMoveState = "STILL"
    var previousMoveState = "STILL"
    var jumpState = "GROUNDED"
    var direction = "RIGHT"

    init {
        val frameHolder = emptyList<TextureRegion>().toMutableList()

        //orients all of the textures to be the right way up.
        textures.flatMap{ it.toList() }
                .forEach { it.flip(false, true) }

        //creates the animations.
        for ( i in 0..2 ) frameHolder.add(textures[0][i])
        runRight = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])
        frameHolder.clear()
        for ( i in 0..2 ) frameHolder.add(textures[1][i])
        runLeft = Animation(0.1f, frameHolder[0], frameHolder[1], frameHolder[2], frameHolder[1])

        //moves the entity to its starting position.
        rectangle.x = xPos * RightRogue.PIXELS_PER_BLOCK.toFloat()
        rectangle.y = yPos * RightRogue.PIXELS_PER_BLOCK.toFloat()
        sprite.x = rectangle.x
        sprite.y = rectangle.y
    }

    fun getCollidableBlocks(state: PlayState, entity: Entity) : MutableList<MutableList<Block?>>{
        val relativePositionX = ((entity.rectangle.x - state.map.gameMap[0][0]!!.position.x) / 48f).toInt()
        val relativePositionY = (entity.rectangle.y / 48f).toInt()
        val collidableBlocks = MutableList(3) {arrayOfNulls<Block>(3).toMutableList()}

        for ( i in 0..2 ) {
            for ( j in 0..2 ) {
                val x = relativePositionX - 1 +  i
                if ( x < 0 || x > 21) break
                collidableBlocks[i][j] = state.map.gameMap[x][relativePositionY - 1 + j]
            }
        }
        return collidableBlocks
    }

    open fun update(state: PlayState, allies : MutableList<Entity>, enemies : MutableList<Entity>, dt: Float){
        //processes health regeneration.
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

        //processes animations and sets the sprite to the right frame.
        when (currentMoveState) {
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
        if ( currentMoveState != previousMoveState) stateTimer = 0f
        stateTimer += dt
    }

    fun attack(enemies : MutableList<Entity>){
        attackCooldown = 0f
        attackDelay = 0f
        val hitbox = Rectangle(rectangle.x - attackRange, rectangle.y, rectangle.width + 2 * attackRange, rectangle.height + 2 * attackRange)
        for ( enemy in enemies ) {
            if ( hitbox.overlaps(enemy.rectangle)) {
                enemy.currentHealth -= attackDamage
                break
            }
        }
        attacking = false
    }

    open fun handleMovement(state: PlayState, allies: MutableList<Entity>, enemies: MutableList<Entity>, dt: Float){
        if ( jumpState == "JUMPING" ) {
            velocity.y = -128f
            jumpState = "FALLING"
        }

        when( currentMoveState ) {
            "RIGHT" -> velocity.x = 128f
            "LEFT" -> velocity.x = -128f
            else -> velocity.x = 0f
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
        //adds the player's velocity to their position, then checks to see if that moved them into a block/enemy. If it did, then it moves them to the edge of the block/enemy.
        rectangle.y += velocity.y
        for ( i in 0 until state.map.gameMap.size ){
            for ( j in 0 until state.map.gameMap[i].size) {
                if (state.map.gameMap[i][j]?.rectangle != null && rectangle.overlaps(state.map.gameMap[i][j]?.rectangle)) {
                    if (velocity.y > 0) {
                        jumpState = "GROUNDED"
                        rectangle.y = state.map.gameMap[i][j]!!.rectangle.y - rectangle.height
                    }
                    else rectangle.y = state.map.gameMap[i][j]!!.rectangle.y + state.map.gameMap[i][j]!!.rectangle.height
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
        for ( i in 0 until state.map.gameMap.size ){
            for ( j in 0 until state.map.gameMap[i].size) {
                if (state.map.gameMap[i][j]?.rectangle != null && rectangle.overlaps(state.map.gameMap[i][j]?.rectangle)) {
                    if ( velocity.y > 0 ) {
                        jumpState = "GROUNDED"
                        acceleration.y = 0f
                    }
                    if (velocity.x > 0) {
                        rectangle.x = state.map.gameMap[i][j]!!.rectangle.x - rectangle.width
                    }
                    else rectangle.x = state.map.gameMap[i][j]!!.rectangle.x + state.map.gameMap[i][j]!!.rectangle.width
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

        //sets the player's sprite's location to the player's rectangle's location.
        sprite.x = rectangle.x
        sprite.y = rectangle.y

        velocity.scl(1/dt)
    }

    fun draw(sb: SpriteBatch) {
        sb.draw(sprite, sprite.x, sprite.y)
    }
}