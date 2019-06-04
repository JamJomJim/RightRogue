package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.RightRogue
import java.util.Stack
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter







class GameStateManager(game: RightRogue){
    var game : RightRogue
        private set
    var states : Stack<State>

    var save: Preferences = Gdx.app.getPreferences("My Preferences")


    init{

        this.game = game
        states = Stack()
    }
//
//    fun savePlayState(playState: PlayState) {
//
//        val json = jsonAdapter.toJson(playState)
//        save.putString("gameSave", json)
//        println(json)
//    }
//
//    fun loadPlayState(json: String) {
//        states.peek().dispose()
//        states.pop()
//        states.push(jsonAdapter.fromJson(json))
//    }

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
        states.push(state)
    }

    fun currentState() : State{
        return states.peek()
    }
}