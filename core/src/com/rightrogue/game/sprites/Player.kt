package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

//todo add generic entity class
class Player (x: Float, y: Float){
    val MAX_VEL = 100f
    val DAMP = 0.9f

    var velocity: Vector2 = Vector2(0f,0f)
    var acceleration: Vector2 = Vector2(0f,0f)
    var texture: Texture = Texture("placeholder.png")
    var sprite = Sprite(texture)
    var rectangle = Rectangle(sprite.x, sprite.y, 30f, 30f)

    var grounded = true

    init {
        sprite.x = x * 32
        sprite.y = y * 32
    }



    //todo add in movement animations
    //todo add in collisions
    fun handleInput(dt: Float){
        //todo change to be scaled values.
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> velocity.x = 500f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> velocity.x = -500f
            else -> velocity.x = 0f
        }


        if (Gdx.input.isKeyPressed(Input.Keys.UP) and grounded){
            velocity.y += -12220f
            grounded = false
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) and !grounded and (velocity.y < 0)){
            velocity.y += -1020 * dt
        }

        //if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  acceleration.y = +100f
    }

    fun update(dt: Float){
        acceleration.y += 1500f * dt
        velocity.add(acceleration.scl(dt))
        acceleration.scl(1/dt)

        if (velocity.x > MAX_VEL) velocity.x = MAX_VEL
        if (velocity.x < -MAX_VEL) velocity.x = -MAX_VEL
        if (velocity.y > MAX_VEL) velocity.y = MAX_VEL
        if (velocity.y < -MAX_VEL) velocity.y = -MAX_VEL

        velocity.scl(dt)
        println(velocity.y)

    }
}