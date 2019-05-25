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
    var rectangle = Rectangle(sprite.x, sprite.y, 30f, 30f)
    private var grounded = true

    init {
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

        //Checks collisions, and moves if possible.
        //if the player is going to collide with something, don't move, and set its velocity to 0.
        //todo need to change this so that if there is a collision, it moves the player as far as they can go, and then sets their velocity to 0.

        val verticalCollision = state.entityCollides(this, 0f, velocity.y)
        when{
            verticalCollision.first == "top" -> {
                acceleration.y = 0f
                velocity.y = 0f
            }
            verticalCollision.first == "bottom" -> {
                grounded = true
                acceleration.y = 0f
                velocity.y = 0f
                //non-null asserted because we can only collide if there is something to collide with.
                //this is necessary, because in the result that there isn't a collision, null is returned for the second object in the pair.
              //  println("1: " + (verticalCollision.second!!.y - rectangle.y))

                //println("2: " + (verticalCollision.second!!.y - rectangle.y))
            }
            else -> sprite.y += velocity.y
        }
        val horizontalCollision = state.entityCollides(this, velocity.x, 0f)



        //controls collision responses
        when {
            horizontalCollision.first == "left" -> {
                acceleration.x = 0f
                velocity.x = 0f
            }
            horizontalCollision.first == "right" -> {
                acceleration.x = 0f
                velocity.x = 0f
            }
            else -> sprite.x += velocity.x
        }

        //sets the player rectangle's bounds to the sprite's bounds less one due to the slightly different sizes.
        rectangle.x = sprite.x + 1
        rectangle.y = sprite.y + 1

        velocity.scl(1/dt)
    }

    fun update(dt: Float){

    }
}