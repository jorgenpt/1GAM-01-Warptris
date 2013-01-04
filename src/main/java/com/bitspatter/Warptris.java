package com.bitspatter;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import com.bitspatter.states.*;

public class Warptris extends StateBasedGame {
    public Warptris() {
        super("Warptris");
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MenuState());
        addState(new PlayingState());
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Warptris());

        app.setShowFPS(false);
        app.setDisplayMode(800, 600, false);
        app.start();
    }
}