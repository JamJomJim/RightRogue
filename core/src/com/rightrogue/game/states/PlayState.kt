package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import java.util.Random


import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.rightrogue.game.GameStateManager
import com.rightrogue.game.RightRogue
import com.rightrogue.game.sprites.Block
import com.rightrogue.game.sprites.Player


class PlayState(gsm: GameStateManager) : State(gsm){

//todo https://gist.github.com/williamahartman/5584f037ed2748f57432 use this to figure out how to add distance to top right
    private var world = World(Vector2(0f, -98f), true)
    private var player: Player = Player(world, 0f, 8f )
    private var map = MutableList(RightRogue.PIXEL_WIDTH /32 + 2) {arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT /32).toMutableList()}
    private var distanceCompleted = 0
    private val random = Random()

    init {
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())
        newMap()

    }

    private fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from + 1) + from
    }

    private fun newMap(){
        println("test ${1+12}")
        for (i in 0 until map.size) {
            for (j in 0 until map[i].size) {
                map[i][j] = Block(i.toFloat(), j.toFloat())
            }
        }
        map[0][RightRogue.PIXEL_HEIGHT / 32 / 2] = null

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
                newMapPiece[i] = Block(RightRogue.PIXEL_WIDTH /32f + distanceCompleted, i.toFloat())
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
        //todo add in bounds so cant fly off screen
        handleInput(dt)
        player.update(dt)

        if (player.position.x.toInt() / 32 > distanceCompleted && player.position.x / 32 > 3) {
            updateMap()
        }

        if (cam.position.x < player.position.x + RightRogue.PIXEL_WIDTH / 2 - 64){
            cam.position.x += player.position.x + RightRogue.PIXEL_WIDTH / 2 - 64 - cam.position.x
            cam.update()
        }

        if (player.position.x.toInt() / 32 > distanceCompleted) {
            distanceCompleted = player.position.x.toInt() / 32
        }
        world.step(Gdx.graphics.deltaTime, 2, 2)
    }

    override fun render(sb: SpriteBatch) {

        sb.projectionMatrix = cam.combined
        sb.begin()


        map.flatMap{ it.toList() }
                .filterNotNull()
                .forEach { it.draw(sb) }


        sb.draw(player.texture, player.position.x, player.position.y)
        sb.end()

    }

}