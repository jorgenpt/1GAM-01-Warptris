package com.bitspatter;

import org.newdawn.slick.*;

public class Score {
    int score;

    Font font;
    final int margin = 10;

    public Score() throws SlickException {
        this.font = Warptris.getFont(16);
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
        return 150;
    }

    public int getHeight() {
        return font.getLineHeight() + font.getLineHeight() + margin * 3;
    }

    public void render(Graphics g, float x, float y) {
        g.setColor(Color.white);
        g.drawRect(x, y, getWidth(), getHeight());

        x += margin;
        y += margin;
        font.drawString(x, y, "Score:");

        y += margin + font.getLineHeight();
        font.drawString(x, y, "" + score);
    }
}
