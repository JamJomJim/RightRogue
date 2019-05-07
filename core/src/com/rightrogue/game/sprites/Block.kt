package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

class Block (x: Float, y: Float){
    var position: Vector2 = Vector2(x * 32,y * 32)
    var texture: Texture = Texture("block32.png")

    fun update(dt: Float){

    }

    fun draw(sb: SpriteBatch){
        sb.draw(texture, position.x, position.y)
    }
}