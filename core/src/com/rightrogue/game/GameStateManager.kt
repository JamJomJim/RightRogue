package com.rightrogue.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.states.State
import java.util.Stack



class GameStateManager(game: RightRogue){
    var game : RightRogue
        private set
    var states : Stack<State>

    init{
        this.game = game
        states = Stack()

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
        states.pop()
    }

    fun setState (state : State){
        states.pop()
        states.push(state)
    }
}