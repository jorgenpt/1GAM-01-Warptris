package com.bitspatter;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Piece implements Cloneable {
    public static Piece[] pieces;

    public int x, y;
    public boolean[][] blocks;
    public Color color;

    // True if this piece has been mutated so far.
    public boolean warped = false;

    public Piece(Color color, boolean[][] blocks) throws SlickException {
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.blocks = blocks;
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
                        // I piece
                        new Piece(Color.cyan, new boolean[][] { { true, true, true, true } }),
                        // O piece
                        new Piece(Color.yellow, new boolean[][] { { true, true }, { true, true } }),
                        // T piece
                        new Piece(Color.decode("#9900CC"), new boolean[][] { { false, true, false },
                                        { true, true, true } }),
                        // S piece
                        new Piece(Color.green, new boolean[][] { { false, true, true }, { true, true, false } }),
                        // Z piece
                        new Piece(Color.red, new boolean[][] { { true, true, false }, { false, true, true } }),
                        // L piece
                        new Piece(Color.orange, new boolean[][] { { true, false }, { true, false }, { true, true } }),
                        // J piece
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

        draggingX = localX;
        draggingY = localY;
        dragging = true;
    }

    public void stopDrag() {
        dragging = false;
    }

    boolean[][] growBlocks(int offsetX, int offsetY, int growX, int growY) {
        if (offsetX > growX || offsetY > growY) {
            return null;
        }

        boolean[][] newBlocks = new boolean[getHeight() + growY][getWidth() + growX];
        for (int y = 0; y < getHeight(); ++y) {
            System.arraycopy(blocks[y], 0, newBlocks[y + offsetY], offsetX, getWidth());
        }

        return newBlocks;
    }

    public boolean stopDrag(int blockX, int blockY) {
        if (!dragging) {
            return false;
        }

        stopDrag();

        int localX = blockX - x;
        int localY = blockY - y;

        // If it's inside the current piece, just check if it's occupied.
        if (contains(blockX, blockY)) {
            // Was it dropped on top of an occupied space.
            if (blocks[localY][localX]) {
                return false;
            }

            blocks[localY][localX] = true;
            blocks[draggingY][draggingX] = false;
            return false;
        }

        // It has to be within one block of a current block.
        if (!hasAnyNonDraggedNeighbor(localX, localY)) {
            return false;
        }

        blocks[draggingY][draggingX] = false;

        int growX = 0, offsetX = 0;
        int growY = 0, offsetY = 0;

        if (localX < 0) {
            growX = 1;
            offsetX = 1;
        } else if (localX >= getWidth()) {
            growX = 1;
        }

        if (localY < 0) {
            growY = 1;
            offsetY = 1;
        } else if (localY >= getHeight()) {
            growY = 1;
        }

        blocks = growBlocks(offsetX, offsetY, growX, growY);
        blocks[localY + offsetY][localX + offsetX] = true;
        x -= offsetX;
        y -= offsetY;

        return true;
    }

    public boolean hasAnyNonDraggedNeighbor(int localX, int localY) {
        for (int xDelta = -1; xDelta <= 1; ++xDelta) {
            for (int yDelta = -1; yDelta <= 1; ++yDelta) {
                int neighborX = xDelta + localX;
                int neighborY = yDelta + localY;
                if (neighborY == draggingY && neighborX == draggingX)
                    continue;

                if (containsLocal(neighborX, neighborY) && blocks[neighborY][neighborX]) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsLocal(int localX, int localY) {
        if (localX < 0 || localY < 0) {
            return false;
        }

        if (localX >= getWidth() || localY >= getHeight()) {
            return false;
        }

        return true;
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
