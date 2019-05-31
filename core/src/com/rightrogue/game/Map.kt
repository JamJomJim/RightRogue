package com.rightrogue.game

import com.rightrogue.game.sprites.Block

class Map(width: Int, height: Int) {
    var layout = MutableList(width) {arrayOfNulls<Block>(height).toMutableList()}

    init {
        newMap()
    }

    private fun newMap(){
        //creates a map the size of the screen completely full of blocks.
        for (i in 0 until layout.size) {
            for (j in 0 until layout[i].size) {
                layout[i][j] = Block(i.toFloat(), j.toFloat())
            }
        }

        //makes a small road from the spawn point.
        layout[0][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        layout[1][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        layout[2][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null
        layout[3][RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK / 2] = null


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
            layout[x][y] = null
        }
    }

    //adds a new layer of blocks to the end of the map, and removes the first later.
    fun updateMap(distanceCompleted: Int){
        val newMapPiece = arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT / RightRogue.PIXELS_PER_BLOCK).toMutableList()

        for (i in 0 until newMapPiece.size) {
            newMapPiece[i] = Block(RightRogue.PIXEL_WIDTH / RightRogue.PIXELS_PER_BLOCK.toFloat() + distanceCompleted - 1, i.toFloat())
        }

        var x = layout.indexOf(layout.last()) + distanceCompleted
        var y = layout.last().indexOf(null)

        if (rand(0, 1) == 1) {
            y = layout.last().lastIndexOf(null)
        }

        newMapPiece[y] = null

        while (x == layout.indexOf(layout.last()) + distanceCompleted) {
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

        layout = layout.drop(1).toMutableList()
        layout.add(newMapPiece)
    }
}
