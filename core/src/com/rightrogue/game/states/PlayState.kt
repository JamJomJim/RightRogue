package com.rightrogue.game.states

import java.util.Random


import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.GameStateManager
import com.rightrogue.game.RightRogue
import com.rightrogue.game.sprites.Block
import com.rightrogue.game.sprites.Player


class PlayState(gsm: GameStateManager) : State(gsm){

//todo https://gist.github.com/williamahartman/5584f037ed2748f57432 use this to figure out how to add distance to top right
    var map = MutableList(RightRogue.PIXEL_WIDTH /32 + 2) {arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT /32).toMutableList()}
    private var player: Player = Player(3f, 8f )
    private var distanceCompleted = 0
    private var temp = 0
    private val random = Random()

    init {
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())
        newMap()

    }

    private fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from + 1) + from
    }

    fun entityCollides(x: Float, y: Float, width: Float, height: Float): Boolean{
        //todo change to check if it collides with map block rectangle
        println(temp)
        println(x.toInt() / 32 - temp )
        for ( i in x.toInt() / 32 - temp until (x / 32 - temp + width / 32).toInt()){
            for ( j in y.toInt() / 32 until (y /32 + height / 32).toInt()){
                println( " " + i + " " + j)
                if (map[i][j] != null) {
                    println("collides with " + i + ", " + j)
                    return true
                }
            }
        }
        return false
    }

    private fun newMap(){
        for (i in 0 until map.size) {
            for (j in 0 until map[i].size) {
                map[i][j] = Block(i.toFloat(), j.toFloat())
            }
        }
        map[0][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        map[1][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        map[2][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        map[3][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        var x = 0
        var y = RightRogue.PIXEL_HEIGHT / 32 / 2

        while (x < RightRogue.PIXEL_WIDTH /32 + 2 - 1) {
              when(rand(1, 8)){
                  1,2,3,4 -> x += 1
                  5,6-> {
                      if (y < RightRogue.PIXEL_HEIGHT / 32 - 2) y += 1
                  }
                  7,8 -> {
                      if (y > 1) y -= 1
                  }
             }
        map[x][y] = null
        }
    }

    private fun updateMap(){

        val newMapPiece = arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT /32).toMutableList()

        for (i in 0 until newMapPiece.size) {
                newMapPiece[i] = Block(RightRogue.PIXEL_WIDTH /32f + distanceCompleted - 1, i.toFloat())
        }

        var x = map.indexOf(map.last()) + distanceCompleted
        var y = map.last().indexOf(null)

        if (rand(0, 1) == 1) {
            y = map.last().lastIndexOf(null)
        }

        //println("default spawn at: " + newMapPiece[y]?.position?.x + ", " + newMapPiece[y]?.position?.y)
        newMapPiece[y] = null

        while (x == map.indexOf(map.last()) + distanceCompleted) {
            when(rand(1, 8)){
                1,2,3,4 -> {
                    x += 1
                }
                5,6 -> {
                    if (y < RightRogue.PIXEL_HEIGHT / 32 - 2) y += 1
                }
                7,8 -> {
                    if (y > 1) y -= 1
                }
            }
            //println("while spawn at: " + newMapPiece[y]?.position?.x + ", " + newMapPiece[y]?.position?.y)
            newMapPiece[y] = null

        }

        map = map.drop(1).toMutableList()
        map.add(newMapPiece)
    }

    override fun handleInput(dt: Float) {
        player.handleInput()
    }

    override fun update(dt: Float) {
        handleInput(dt)
        //entityCollides(player.sprite.x / 32 - temp, player.sprite.y / 32, 32f, 32f)

        if (player.sprite.x.toInt() / 32 > distanceCompleted && player.sprite.x / 32 > 3) {

            distanceCompleted = player.sprite.x.toInt() / 32
            temp += 1
            updateMap()

        }

        if (cam.position.x < player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 64){
            cam.position.x += player.sprite.x + RightRogue.PIXEL_WIDTH / 2 - 64 - cam.position.x
            cam.update()
        }

        player.update(cam.position.x, dt)

        if (player.sprite.x + player.velocity.x < cam.position.x - RightRogue.PIXEL_WIDTH / 2
                || entityCollides(player.rectangle.x, player.rectangle.y,32f, 32f))
            player.velocity.x = 0f

        else player.sprite.x += player.velocity.x


        when{
            player.sprite.y + player.velocity.y > RightRogue.PIXEL_HEIGHT - 33 -> player.sprite.y = RightRogue.PIXEL_HEIGHT - 32f
            player.sprite.y + player.velocity.y < 0 -> player.sprite.y = 0f
            else -> player.sprite.y += player.velocity.y
        }

        player.rectangle.x = player.sprite.x
        player.rectangle.y = player.sprite.y
        player.velocity.scl(player.DAMP/dt)
    }

    override fun render(sb: SpriteBatch) {

        sb.projectionMatrix = cam.combined
        sb.begin()

        map.flatMap{ it.toList() }
                .filterNotNull()
                .forEach { it.draw(sb) }

        sb.draw(player.sprite.texture, player.sprite.x, player.sprite.y)
        sb.end()

    }

}