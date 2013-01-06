package com.bitspatter;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
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

    public static Font getFont(int size, boolean bold) throws SlickException {
        UnicodeFont font = new UnicodeFont("PressStart2P.ttf", size, bold, false);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        font.loadGlyphs();

        return font;
    }
}