package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.rightrogue.game.RightRogue

class Block (x: Float, y: Float, private var texture: Texture){
    var position = Vector2(x * RightRogue.PIXELS_PER_BLOCK,y * RightRogue.PIXELS_PER_BLOCK)
    var rectangle = Rectangle(position.x, position.y, RightRogue.PIXELS_PER_BLOCK.toFloat(), RightRogue.PIXELS_PER_BLOCK.toFloat())

    fun draw(sb: SpriteBatch){
        sb.draw(texture, position.x, position.y)
    }
}