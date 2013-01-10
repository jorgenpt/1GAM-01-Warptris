package com.bitspatter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class EntropyMeter {
    public interface EntropyListener {
        void onEntropyOverflown();
    }

    final int OUTER_MARGIN = 10;
    final int ENTROPY_BOX_SIZE = 30;
    final int ENTROPY_BOX_MARGIN = 10;
    final int ENTROPY_BOX_BORDER_WIDTH = 3;
    final int MAX_ENTROPY = 4;

    int entropy = 0;
    EntropyListener listener;
    Font font;

    public EntropyMeter(EntropyListener listener) throws SlickException {
        this.listener = listener;
        this.font = Warptris.getFont(16);
    }

    public void render(Graphics g, float x, float y) {
        g.setColor(Color.white);
        g.drawRect(x, y, getWidth(), getHeight());

        x += OUTER_MARGIN;
        y += OUTER_MARGIN;

        font.drawString(x, y, "Entropy:");
        y += font.getLineHeight() + ENTROPY_BOX_MARGIN;

        for (int i = 0; i < MAX_ENTROPY; ++i) {
            g.setColor(Color.white);
            g.fillRect(x, y, ENTROPY_BOX_SIZE, ENTROPY_BOX_SIZE);

            if (entropy > i) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.black);
            }
            g.fillRect(x + ENTROPY_BOX_BORDER_WIDTH, y + ENTROPY_BOX_BORDER_WIDTH, ENTROPY_BOX_SIZE - 2
                            * ENTROPY_BOX_BORDER_WIDTH, ENTROPY_BOX_SIZE - 2 * ENTROPY_BOX_BORDER_WIDTH);

            x += ENTROPY_BOX_SIZE + ENTROPY_BOX_MARGIN;
        }
    }

    public int getWidth() {
        return ENTROPY_BOX_SIZE * MAX_ENTROPY + ENTROPY_BOX_MARGIN * (MAX_ENTROPY - 1) + 2 * OUTER_MARGIN;
    }

    public int getHeight() {
        return font.getLineHeight() + ENTROPY_BOX_MARGIN + ENTROPY_BOX_SIZE + 2 * OUTER_MARGIN;
    }

    public void addEntropy() {
        entropy++;
        if (entropy >= MAX_ENTROPY) {
            entropy = 0;
            listener.onEntropyOverflown();
        }
    }
}
