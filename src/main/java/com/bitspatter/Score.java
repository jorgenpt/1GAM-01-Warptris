package com.bitspatter;

import org.newdawn.slick.*;

public class Score {
    int score;

    Font font;
    int margin;

    public Score() throws SlickException {
        this.font = Warptris.getFont(16, false);
        this.margin = (getHeight() - font.getLineHeight()) / 2;
        this.score = 0;
    }

    public void clearRows(int level, int count) {
        switch (count) {
        case 0:
            break;
        case 1:
            score += 100 * level;
            break;
        case 2:
            score += 300 * level;
            break;
        case 3:
            score += 500 * level;
            break;
        case 4:
        default:
            score += 800 * level * count / 4;
            break;
        }
    }

    public int getWidth() {
        return 250;
    }

    public int getHeight() {
        return 32;
    }

    public void render(Graphics g, float x, float y) {
        g.setColor(Color.white);
        g.drawRect(x, y, getWidth(), getHeight());
        font.drawString(x + margin, y + margin, "Score: " + score);
    }
}
