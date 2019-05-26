package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

abstract class Entity(xPos: Float, yPos: Float, width: Float, height: Float, texture : Texture = Texture("placeholder.png")) {
    var velocity: Vector2 = Vector2(0f,0f)
    var acceleration: Vector2 = Vector2(0f,0f)
    var sprite = Sprite(texture)
    var rectangle = Rectangle(sprite.x, sprite.y, width, height)
    var grounded = true

    init {
        rectangle.x = xPos * 32
        rectangle.y = yPos * 32
        sprite.x = xPos * 32
        sprite.y = yPos * 32
    }

}