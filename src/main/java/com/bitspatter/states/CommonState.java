package com.bitspatter.states;

import java.io.File;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class CommonState extends BasicGameState {
    @Override
    public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
        Input input = gc.getInput();

        if (input.isKeyPressed(Input.KEY_S)) {
            int number = 0;
            String screenShotFileName;

            do {
                number++;
                screenShotFileName = "screenshot_" + number + ".png";
            } while (new File(screenShotFileName).exists());

            System.out.println("Screenshot outputting to: " + screenShotFileName);

            Image target = new Image(gc.getWidth(), gc.getHeight());
            Graphics g = gc.getGraphics();
            g.copyArea(target, 0, 0);
            ImageOut.write(target, screenShotFileName);
        }
    }
}
