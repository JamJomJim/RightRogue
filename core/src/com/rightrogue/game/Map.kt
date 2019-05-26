package com.rightrogue.game

import com.rightrogue.game.sprites.Block

class Map(width: Int, height: Int) {
    var layout = MutableList(width) {arrayOfNulls<Block>(height).toMutableList()}

    init {
        newMap()
    }

    private fun newMap(){
        for (i in 0 until layout.size) {
            for (j in 0 until layout[i].size) {
                layout[i][j] = Block(i.toFloat(), j.toFloat())
            }
        }
        layout[0][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        layout[1][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        layout[2][RightRogue.PIXEL_HEIGHT / 32 / 2] = null
        layout[3][RightRogue.PIXEL_HEIGHT / 32 / 2] = null

        var x = 3
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
            layout[x][y] = null
        }
    }

    fun updateMap(distanceCompleted: Int){

        val newMapPiece = arrayOfNulls<Block>(RightRogue.PIXEL_HEIGHT /32).toMutableList()

        for (i in 0 until newMapPiece.size) {
            newMapPiece[i] = Block(RightRogue.PIXEL_WIDTH /32f + distanceCompleted - 1, i.toFloat())
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
                    if (y < RightRogue.PIXEL_HEIGHT / 32 - 2) y += 1
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
