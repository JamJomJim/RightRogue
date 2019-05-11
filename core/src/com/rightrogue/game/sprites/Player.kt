package com.rightrogue.game.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.rightrogue.game.RightRogue

class Player (world: World, x: Float, y: Float){
    val MAX_VEL = 1000f
    val DAMP = 0.9f

    var position: Vector2 = Vector2(x * 32,y * 32)
    var velocity: Vector2 = Vector2(0f,0f)
    var acceleration: Vector2 = Vector2(0f,0f)
    var texture: Texture = Texture("placeholder.png")
    var sprite = Sprite(texture, position.x.toInt(), position.y.toInt())
    var bodyDef = BodyDef()
    var body: Body
    var fixtureDef = FixtureDef()

    init {
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(position)
        body = world.createBody(bodyDef)
        val shape = PolygonShape()
        shape.setAsBox(sprite.width/2, sprite.height/2)

        fixtureDef.shape = shape
        body.createFixture(fixtureDef)
        shape.dispose()
    }

    //todo add in movement animations
    //todo add in collisions
    fun handleInput(){
        //todo change to be scaled values.
        //todo add gravity
        when {
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> acceleration.x = 100f
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> acceleration.x = -100f
            else -> acceleration.set(0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))  acceleration.y = -100f
    }

    fun update(dt: Float){
        velocity.add(acceleration)

        if (velocity.x > MAX_VEL) velocity.x = MAX_VEL
        if (velocity.x < -MAX_VEL) velocity.x = -MAX_VEL
        if (velocity.y > MAX_VEL) velocity.y = MAX_VEL
        if (velocity.y < -MAX_VEL) velocity.y = -MAX_VEL

        velocity.scl(dt)
        position.add(velocity)
        velocity.scl(DAMP/dt)
    }
}