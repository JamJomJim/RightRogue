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
    fun handleInput(state: PlayState, dt: Float){
        //todo separate the input handling and the actual update of player
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



        acceleration.y += 1500f * dt
        velocity.add(acceleration.scl(dt))
        acceleration.scl(1/dt)

        //if the player velocity is higher/lower than the max/min, set it to the max/min.
        if (velocity.x > MAX_VEL) velocity.x = MAX_VEL
        if (velocity.x < -MAX_VEL) velocity.x = -MAX_VEL
        if (velocity.y > MAX_VEL) velocity.y = MAX_VEL
        if (velocity.y < -MAX_VEL) velocity.y = -MAX_VEL

        velocity.scl(dt)

        //if the player is going to collide with something, set its velocity to 0
        //need to change this so that it moves the player as far as they can go, and then set the velocity to 0.
        if (this.sprite.x + this.velocity.x < state.cam.position.x - RightRogue.PIXEL_WIDTH / 2
                || state.entityCollides(this.velocity.x, 0f))
            this.velocity.x = 0f

        else this.sprite.x += this.velocity.x


        if (state.entityCollides(0f, this.velocity.y)) {
            if (this.velocity.y > 0)
                this.grounded = true

            this.acceleration.y = 0f
            this.velocity.y = 0f
        }
        else this.sprite.y += this.velocity.y

        this.rectangle.x = this.sprite.x + 1
        this.rectangle.y = this.sprite.y + 1

        this.velocity.scl(this.DAMP/dt)



    }

    fun update(dt: Float){

    }
}