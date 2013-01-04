package com.bitspatter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.bitspatter.renderers.BlockRenderer;

public class BoardMutation {
    final int MUTATION_DURATION = 400;

    public int y;
    public int fromX, toX;
    public Color color;

    BlockRenderer renderer;
    int remainingDuration = MUTATION_DURATION;

    public BoardMutation(BlockRenderer renderer, int y, int fromX, int toX, Color color) {
        this.renderer = renderer;
        this.y = y;
        this.fromX = fromX;
        this.toX = toX;
        this.color = color;
    }

    public boolean animate(int delta) {
        remainingDuration -= delta;
        return (remainingDuration > 0);
    }

    public void render(Graphics g) {
        float scale = 1.0f - Math.max(0.0f, remainingDuration / (float) MUTATION_DURATION);
        float x = fromX + (toX - fromX) * scale;
        renderer.renderAtPixel(g, renderer.getX(x), renderer.getY(y), color);
    }
}
