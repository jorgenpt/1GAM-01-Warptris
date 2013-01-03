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

    public Piece(Color color, boolean[][] blocks) throws SlickException {
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.blocks = blocks;
    }

    public void render(Graphics g, float blockSize) {
        g.setColor(color);
        for (int y = 0; y < blocks.length; ++y) {
            for (int x = 0; x < blocks[y].length; ++x) {
                if (blocks[y][x]) {
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
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
}
