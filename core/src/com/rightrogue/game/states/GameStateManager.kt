package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Json
import com.rightrogue.game.RightRogue
import java.util.Stack

class GameStateManager(game: RightRogue){
    var game : RightRogue
        private set
    var states : Stack<State>
    var save: Preferences = Gdx.app.getPreferences("My Preferences")

    private val json = Json()

    init{
        this.game = game
        states = Stack()
        if ( save.getString("gameSave").isNullOrEmpty() ){
            println("asdasdasd")
        }
    save.clear()
    }

    fun update(dt : Float){
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch){
        states.peek().render(sb)
    }

    fun pushState(state : State) {
        states.push(state)
    }

    fun popState() {
        states.peek().dispose()
        states.pop()
        Gdx.input.inputProcessor = states.peek()

    }

    fun setState (state : State){
        states.peek().dispose()
        states.pop()
//        if ( state is PlayState && !save.getString("gameSave").isNullOrEmpty() ) {
//            states.push(state)
//            state.map.serializableLayout = (json.fromJson(state.map.serializableLayout::class.java, save.getString("gameSave")).toMutableList())
//            var temp = json.fromJson(state.map.serializableLayout::class.java, save.getString("gameSave"))
//
//            println(temp[0])
//            state.map.loadLayout(temp)
//            println("Load")
//        }
//        else states.push(state)
        states.push(state)
    }

    fun currentState() : State{
        return states.peek()
    }
}