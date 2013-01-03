package com.bitspatter.renderer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class BlockRenderer {
    Rectangle renderArea;
    int blockSize;

    public BlockRenderer(Rectangle renderArea, int blockSize) {
        this.renderArea = renderArea;
        this.blockSize = blockSize;
    }

    public int getX(int blockX) {
        return getX((float) blockX);
    }

    public int getX(float blockX) {
        return (int) (renderArea.getX() + blockX * blockSize);
    }

    public int getY(int blockY) {
        return getY((float) blockY);
    }

    public int getY(float blockY) {
        return (int) (renderArea.getY() + blockY * blockSize);
    }

    public int getBlockX(int x) {
        return (x - (int) renderArea.getX()) / blockSize;
    }

    public int getBlockY(int y) {
        return (y - (int) renderArea.getY()) / blockSize;
    }

    public void renderAtPixel(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.setClip(renderArea);
        g.fillRect(x, y, blockSize, blockSize);
        g.clearClip();
    }

    public void render(Graphics g, int blockX, int blockY, Color color) {
        renderAtPixel(g, getX(blockX), getY(blockY), color);
    }
}
