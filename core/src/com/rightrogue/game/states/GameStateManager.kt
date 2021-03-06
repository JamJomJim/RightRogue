package com.rightrogue.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.rightrogue.game.RightRogue
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