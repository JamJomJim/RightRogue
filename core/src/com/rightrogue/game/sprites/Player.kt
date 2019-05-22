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
    val MAX_VEL = 1000f
    val DAMP = 0.9f

    var velocity: Vector2 = Vector2(0f,0f)
    var acceleration: Vector2 = Vector2(0f,0f)
    var texture: Texture = Texture("placeholder.png")
    var sprite = Sprite(texture)
    var rectangle = Rectangle(sprite.x, sprite.y, 32f, 32f)

    private var isJump = false

    init {
        sprite.x = x * 32
        sprite.y = y * 32
    }



    //todo add in movement animations
    //todo add in collisions
    fun handleInput(){
        //todo change to be scaled values.
        //todo add gravity - need collisions first...
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> acceleration.x = 100f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> acceleration.x = -100f
            else -> acceleration.x = 0f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) and !isJump){
            acceleration.y += -100f
            isJump = true
        }
        else {
            isJump = false
           // acceleration.y += 10f
        }

        //if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  acceleration.y = +100f
    }

    fun update(camLocationX: Float, dt: Float){
        velocity.add(acceleration)

        if (velocity.x > MAX_VEL) velocity.x = MAX_VEL
        if (velocity.x < -MAX_VEL) velocity.x = -MAX_VEL
        if (velocity.y > MAX_VEL) velocity.y = MAX_VEL
        if (velocity.y < -MAX_VEL) velocity.y = -MAX_VEL

        velocity.scl(dt)

    }
}