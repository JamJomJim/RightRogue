package com.rightrogue.game.states

import java.util.Random

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.GameStateManager
import com.rightrogue.game.RightRogue
import com.rightrogue.game.sprites.Block
import com.rightrogue.game.sprites.Player


class PlayState(gsm: GameStateManager) : State(gsm){

    private var player: Player = Player(0f, 8f )
    private var map = MutableList(RightRogue.WIDTH /32) {arrayOfNulls<Block>(RightRogue.HEIGHT /32).toMutableList()}
    private var distanceCompleted = 0
    val random = Random()

    init {
        cam.setToOrtho(true, (RightRogue.WIDTH).toFloat(), (RightRogue.HEIGHT).toFloat())
        newMap()
    }

    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from + 1) + from
    }

    fun newMap(){
        for (i in 0 until map.size) {
            for (j in 0 until map[i].size) {
                map[i][j] = Block(i.toFloat(), j.toFloat())
            }
        }
        map[0][RightRogue.HEIGHT / 32 / 2] = null

        var x = 0
        var y = RightRogue.HEIGHT / 32 / 2

        while (x < RightRogue.WIDTH /32 - 1) {
              when(rand(1, 8)){
                  1,2,3,4 -> x += 1
                  5,6-> {
                      if (y < RightRogue.HEIGHT / 32 - 2) y += 1
                  }
                  7,8 -> {
                      if (y > 1) y -= 1
                  }
             }
        map[x][y] = null
        }
    }

    fun updateMap(){

        var newMapPiece = arrayOfNulls<Block>(RightRogue.HEIGHT /32).toMutableList()

        for (i in 0 until newMapPiece.size) {
                newMapPiece[i] = Block(RightRogue.WIDTH /32f - 1 + distanceCompleted, i.toFloat())
        }

        var x = RightRogue.WIDTH /32 - 1 + distanceCompleted
        var y = map[RightRogue.WIDTH /32 - 1].indexOf(null)
        when (rand(0, 1)) {
            0 -> y = map[RightRogue.WIDTH / 32 - 1].indexOf(null)
            1 -> y = map[RightRogue.WIDTH / 32 - 1].lastIndexOf(null)
        }

        while (x < RightRogue.WIDTH /32 + distanceCompleted) {
            when(rand(1, 8)){
                1,2,3,4 -> {
                    x += 1
                }
                5,6 -> {
                    if (y < RightRogue.HEIGHT / 32 - 2) y += 1
                }
                7,8 -> {
                    if (y > 1) y -= 1
                }
            }
            newMapPiece[y] = null
        }

        map = map.drop(1).toMutableList()
        map.add(newMapPiece)
    }

    override fun handleInput(dt: Float) {
        if(Gdx.input.isKeyPressed(Keys.LEFT)) player.position.x -= RightRogue.WIDTH * dt
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) player.position.x += RightRogue.WIDTH * dt
        if(Gdx.input.isKeyPressed(Keys.UP)) player.position.y -= RightRogue.HEIGHT * dt
        if(Gdx.input.isKeyPressed(Keys.DOWN)) player.position.y += RightRogue.HEIGHT * dt
    }

    override fun update(dt: Float) {
        //println(player.position.x)
        //println(distanceCompleted)
        handleInput(dt)

        if (player.position.x.toInt() / 32 > distanceCompleted) {
            updateMap()
        }

        if (cam.position.x < player.position.x + RightRogue.WIDTH / 2 - 64){
            cam.position.x += player.position.x + RightRogue.WIDTH / 2 - 64 - cam.position.x
            cam.update()
        }

        distanceCompleted = player.position.x.toInt() / 32

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