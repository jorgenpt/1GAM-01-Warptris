package com.bitspatter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class BoardMutation {
    final int MUTATION_DURATION = 400;

    public int y;
    public int fromX, toX;
    public Color color;

    int remainingDuration = MUTATION_DURATION;

    public BoardMutation(int y, int fromX, int toX, Color color) {
        this.y = y;
        this.fromX = fromX;
        this.toX = toX;
        this.color = color;
    }

    public boolean animate(int delta) {
        remainingDuration -= delta;
        return (remainingDuration > 0);
    }

    public void render(Graphics g, Rectangle rect, float blockSize) {
        float scale = 1.0f - Math.max(0.0f, remainingDuration / (float) MUTATION_DURATION);
        float x = fromX + (toX - fromX) * scale;
        g.setColor(color);
        g.fillRect(rect.getX() + x * blockSize, rect.getY() + y * blockSize, blockSize, blockSize);
    }
}
