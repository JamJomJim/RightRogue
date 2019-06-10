package com.rightrogue.game

import com.rightrogue.game.sprites.Block

class Map(width: Int, height: Int) {
    var gameMap = MutableList(width) {arrayOfNulls<Block>(height).toMutableList()}
    var layout = MutableList(width) { Array(height) {"nothing"}}

    init {
        newMap()
    }

    fun saveMap(){
        for (i in 0 until gameMap.size) {
            for (j in 0 until gameMap[i].size) {
                if ( gameMap[i][j] != null ) layout[i][j] = gameMap[i][j]!!.blockType
            }
        }
    }

    fun loadMap(mapLayout: MutableList<Array<String>>){
        for (i in 0 until mapLayout.size) {
            for (j in 0 until mapLayout[i].size) {
                if ( mapLayout[i][j] == "nothing" ) gameMap[i][j] = null
                else gameMap[i][j] = Block(i.toFloat(), j.toFloat(), mapLayout[i][j])
            }
        }
    }

    private fun newMap(){
        //creates a map the size of the screen completely full of blocks.
        for (i in 0 until gameMap.size) {
            for (j in 0 until gameMap[i].size) {
                gameMap[i][j] = Block(i.toFloat(), j.toFloat(), "default")
            }
        }

        //makes a small road from the spawn point.
        gameMap[0][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        gameMap[1][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        gameMap[2][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        gameMap[3][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null

        //creates a randomly generates path from the end of the spawn path.
        var x = 3
        var y = RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2

        while (x < RightRogue.PIXEL_WIDTH / RightRogue.PIXELS_PER_BLOCK + 2 - 1) {
            when(rand(1, 8)){
                1,2,3,4 -> x += 1
                5,6-> {
                    if (y < RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK - 2) y += 1
                }
                7,8 -> {
                    if (y > 1) y -= 1
                }
            }
            gameMap[x][y] = null
        }
    }

    //adds a new layer of blocks to the end of the map, and removes the first later.
    fun updateMap(distanceCompleted: Int){
        val newMapPiece = arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK).toMutableList()

        for (i in 0 until newMapPiece.size) {
            newMapPiece[i] = Block(RightRogue.PIXEL_WIDTH / RightRogue.PIXELS_PER_BLOCK.toFloat() + distanceCompleted - 1, i.toFloat(), "default")
        }

        var x = gameMap.indexOf(gameMap.last()) + distanceCompleted
        var y = gameMap.last().indexOf(null)

        if (rand(0, 1) == 1) {
            y = gameMap.last().lastIndexOf(null)
        }

        newMapPiece[y] = null

        while (x == gameMap.indexOf(gameMap.last()) + distanceCompleted) {
            when(rand(1, 8)){
                1,2,3,4 -> {
                    x += 1
                }
                5,6 -> {
                    if (y < RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK - 2) y += 1
                }
                7,8 -> {
                    if (y > 1) y -= 1
                }
            }
            newMapPiece[y] = null
        }

        gameMap = gameMap.drop(1).toMutableList()
        gameMap.add(newMapPiece)
    }
}
