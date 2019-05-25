package com.rightrogue.game.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.rightrogue.game.GameStateManager
import com.rightrogue.game.Map
import com.rightrogue.game.RightRogue
import com.rightrogue.game.sprites.Player


class PlayState(gsm: GameStateManager) : State(){

//todo https://gist.github.com/williamahartman/5584f037ed2748f57432 use this to figure out how to add distance to top right
    //var map = MutableList(RightRogue.PIXEL_WIDTH /32 + 2) {arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT /32).toMutableList()}
    var map = Map(RightRogue.PIXEL_WIDTH /32 + 2, RightRogue.PIXEL_HEIGHT /32)
    private var player: Player = Player(3f, 8f )
    private var distanceCompleted = 0
    private var temp = 0

    init {
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())
    }

    //todo make a version that works for the whole map not just the player

    fun entityCollides(entity: Player, x: Float, y: Float): Boolean{
        return false
    }

    override fun handleInput(dt: Float) {
        player.handleInput(this, dt)
    }

    override fun update(dt: Float) {
        handleInput(dt)
        //println("sprite : " + player.sprite.x + ", " + player.sprite.y)
       // println("rectangle : " + player.rectangle.x + ", " + player.rectangle.y)
        //updates the map when the player has gone far enough right.
        if (player.sprite.x.toInt() / 32 > distanceCompleted && player.sprite.x / 32 > 3) {

            distanceCompleted = player.sprite.x.toInt() / 32
            temp += 1
            map.updateMap(distanceCompleted)
        }

        //updates the camera to follow the player as they move to the right.
        if (cam.position.x < player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 64){
            cam.position.x += player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 64 - cam.position.x
            cam.update()
        }

        player.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        sb.projectionMatrix = cam.combined
        sb.begin()

        //draws all of the non-null blocks in map
        map.layout.flatMap{ it.toList() }
                .filterNotNull()
                .forEach { it.draw(sb) }

        sb.draw(player.sprite.texture, player.sprite.x, player.sprite.y)
        sb.end()

    }

}