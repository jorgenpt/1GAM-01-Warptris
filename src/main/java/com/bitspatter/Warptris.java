package com.bitspatter;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.bitspatter.states.PlayingState;

public class Warptris extends StateBasedGame {
    public Warptris() {
        super("Warptris");
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new PlayingState());
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Warptris());

        app.setShowFPS(false);
        app.setDisplayMode(800, 600, false);
        app.start();
    }
}