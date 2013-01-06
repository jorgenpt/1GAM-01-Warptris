package com.bitspatter;

import org.newdawn.slick.*;

import com.bitspatter.renderers.BlockRenderer;

public class NextPieceBox {
    BlockRenderer renderer;
    Image nextPieceImage;
    float marginX, marginY;

    public NextPieceBox(BlockRenderer blockRenderer) {
        this.renderer = blockRenderer;
    }

    public void setNextPiece(Piece nextPiece) throws SlickException {
        int width = renderer.getBlockSize() * nextPiece.getWidth();
        int height = renderer.getBlockSize() * nextPiece.getHeight();
        marginX = (getWidth() - width) / 2f;
        marginY = (getHeight() - height) / 2f;

        BlockRenderer oldRenderer = nextPiece.renderer;
        nextPiece.renderer = renderer;

        nextPieceImage = Image.createOffscreenImage(width, height);
        Graphics localGraphics = nextPieceImage.getGraphics();
        nextPiece.render(localGraphics, false);
        localGraphics.flush();

        nextPiece.renderer = oldRenderer;
    }

    public int getWidth() {
        return 250;
    }

    public int getHeight() {
        return getWidth();
    }

    public void render(Graphics g, float x, float y) {
        g.setColor(Color.white);
        g.drawRect(x, y, getWidth(), getHeight());
        g.drawImage(nextPieceImage, x + marginX, y + marginY);
    }
}
