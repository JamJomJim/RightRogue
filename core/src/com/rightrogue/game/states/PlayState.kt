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
    private var map = Map(RightRogue.PIXEL_WIDTH /32 + 2, RightRogue.PIXEL_HEIGHT /32)
    private var player: Player = Player(3f, 8f )
    private var distanceCompleted = 0
    private var temp = 0

    init {
        cam.setToOrtho(true, (RightRogue.PIXEL_WIDTH).toFloat(), (RightRogue.PIXEL_HEIGHT).toFloat())
    }

    //todo make a version that works for the whole map not just the player

    fun entityCollides(entity: Player, x: Float, y: Float): Pair<String, Rectangle?>{
        //setup a temporary rectangle to use as a reference in overlap calculation. This can't be the best way of doing this
        val tempRectangle = Rectangle(entity.rectangle)
        tempRectangle.x += x
        tempRectangle.y += y

        for ( i in 0..4 ){
            for ( j in 0 until map.layout[i].size){
                if ( map.layout[i][j]?.rectangle != null && tempRectangle.overlaps(map.layout[i][j]?.rectangle)) {
                    val playerBottom = tempRectangle.y + tempRectangle.height
                    val tileTop = map.layout[i][j]?.rectangle!!.y + map.layout[i][j]?.rectangle!!.height
                    val playerRight = tempRectangle.x + tempRectangle.width
                    val tilesRight = map.layout[i][j]?.rectangle!!.x + map.layout[i][j]?.rectangle!!.width

                    val t = tileTop - tempRectangle.y
                    println("t =" + t)

                    val b = playerBottom - map.layout[i][j]?.rectangle!!.y
                    println("b =" + b)

                    val l = tilesRight - tempRectangle.x
                    //println(tempRectangle.x)
                    //println(tilesRight)
                    println("l =" + l)

                    val r = playerRight- map.layout[i][j]?.rectangle!!.x
                    println("r =" + r)


                    if (t < b && t < l && t < r ) {
                        println("top")
                        return Pair("top", map.layout[i][j]?.rectangle)
                    }

                    if (b < t && b < l && b < r) {
                        println("bottom")

                        return Pair("bottom", map.layout[i][j]?.rectangle)
                    }

                    if (l < r && l < t && l < b) {
                        println("left")

                        return Pair("left", map.layout[i][j]?.rectangle)
                    }

                    if (r < l && r < t && r < b ) {
                        println("right")

                        return Pair("right", map.layout[i][j]?.rectangle)
                    }

                    return Pair("error", null)
                }
            }
        }
        return Pair("none", null)
    }

    override fun handleInput(dt: Float) {
        player.handleInput(this, dt)
    }

    override fun update(dt: Float) {
        handleInput(dt)

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