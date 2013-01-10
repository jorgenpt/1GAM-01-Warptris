package com.bitspatter;

import java.awt.FontFormatException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

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

    static java.awt.Font ttfFont = null;

    static java.awt.Font getTtfFont() {
        if (ttfFont == null) {
            try {
                ttfFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
                                ResourceLoader.getResourceAsStream("PressStart2P.ttf"));
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ttfFont;
    }

    static final String supportedGlyphs = "ABCDEFGHJIKLMNOPQRSTUVWXYZabcdefghjiklmnopqrstuvwxyz0123456789-_+ .,!;:'\"()";
    static final Map<Integer, Font> fontCache = new HashMap<Integer, Font>();

    public static Font getFont(int size) throws SlickException {
        Integer sizeInteger = new Integer(size);
        if (!fontCache.containsKey(sizeInteger)) {
            UnicodeFont font = new UnicodeFont(getTtfFont(), size, false, false);
            font.getEffects().add(new ColorEffect(java.awt.Color.white));
            font.addGlyphs(supportedGlyphs);
            font.loadGlyphs();
            fontCache.put(sizeInteger, font);
        }

        return fontCache.get(sizeInteger);
    }
}