package com.bitspatter;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Piece implements Cloneable {
    public static Piece[] pieces;

    public int x, y;
    public boolean[][] blocks;
    public Color color;
    public boolean warping;

    public Piece(Color color, boolean[][] blocks) throws SlickException {
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.blocks = blocks;
        this.warping = false;
    }

    boolean shouldRender(int x, int y) {
        if (dragging && (x == draggingX && y == draggingY)) {
            return false;
        }

        return blocks[y][x];
    }

    public void render(Graphics g, float blockSize) {
        g.setColor(color);
        for (int y = 0; y < blocks.length; ++y) {
            for (int x = 0; x < blocks[y].length; ++x) {
                if (shouldRender(x, y)) {
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }
    }

    public void renderDraggable(Graphics g, float x, float y, float blockSize) {
        if (dragging) {
            g.setColor(color);
            g.fillRect(x, y, blockSize, blockSize);
        }
    }

    public static void createPieces() throws SlickException {
        pieces = new Piece[] {
                        new Piece(Color.orange, new boolean[][] { { true, false }, { true, false }, { true, true } }),
                        new Piece(Color.cyan, new boolean[][] { { true, true, true, true } }),
                        new Piece(Color.yellow, new boolean[][] { { true, true }, { true, true } }),
                        new Piece(Color.decode("#9900CC"), new boolean[][] { { false, true, false },
                                        { true, true, true } }),
                        new Piece(Color.green, new boolean[][] { { false, true, true }, { true, true, false } }),
                        new Piece(Color.red, new boolean[][] { { true, true, false }, { false, true, true } }),
                        new Piece(Color.blue, new boolean[][] { { true, false, false }, { true, true, true } }) };
    }

    static Random random = new Random();

    public static Piece getRandomPiece() {
        if (pieces == null) {
            try {
                createPieces();
            } catch (SlickException se) {
                return null;
            }
        }

        Piece piece = pieces[random.nextInt(pieces.length)];
        try {
            return (Piece) piece.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }

    public Piece rotated(boolean clockwise) throws SlickException {
        boolean[][] newBlocks = new boolean[blocks[0].length][blocks.length];
        for (int y = 0; y < blocks.length; ++y) {
            for (int x = 0; x < blocks[0].length; ++x) {
                if (clockwise) {
                    newBlocks[x][y] = blocks[blocks.length - 1 - y][x];
                } else {
                    newBlocks[x][y] = blocks[y][blocks[0].length - 1 - x];
                }
            }
        }

        Piece newPiece = new Piece(color, newBlocks);
        newPiece.x = x;
        newPiece.y = y;
        return newPiece;
    }

    public void enableWarping(GameContainer gc, boolean warping) {
        this.warping = warping;
        if (!warping) {
            dragging = false;
        }
    }

    public int getMaxX() {
        return getWidth() + x;
    }

    public int getWidth() {
        return blocks[0].length;
    }

    public int getMaxY() {
        return getHeight() + y;
    }

    public int getHeight() {
        return blocks.length;
    }

    int draggingX, draggingY;
    boolean dragging = false;

    public void startDrag(int blockX, int blockY) {
        if (!contains(blockX, blockY)) {
            return;
        }

        int localX = blockX - x;
        int localY = blockY - y;
        if (!blocks[localY][localX]) {
            return;
        }

        draggingX = blockX;
        draggingY = blockY;
        dragging = true;
    }

    public void stopDrag(int blockX, int blockY) {
        dragging = false;

        int localX = blockX - x;
        if (localX < -1 || localX > getWidth()) {
            return;
        }

        int localY = blockY - y;
        if (localY < -1 || localY > getHeight()) {
            return;
        }

        // TODO: Place new block.
    }

    public boolean contains(int blockX, int blockY) {
        if (blockX < x || blockY < y) {
            return false;
        }

        if (blockX >= getMaxX() || blockY >= getMaxY()) {
            return false;
        }

        return true;
    }
}
