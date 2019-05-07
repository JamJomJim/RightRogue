package com.rightrogue.game.sprites

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

class Player (x: Float, y: Float){
    var position: Vector2 = Vector2(x * 32,y * 32)
    var texture: Texture = Texture("placeholder.png")

    fun update(dt: Float){

    }
}