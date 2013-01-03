package com.bitspatter;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Board {
    int width, height;
    Color[][] finalizedBlocks;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.finalizedBlocks = new Color[height][width];
    }

    public void render(Graphics g, Rectangle rect, float blockSize) {
        g.setColor(Color.white);
        g.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

        for (int y = 0; y < finalizedBlocks.length; ++y) {
            for (int x = 0; x < finalizedBlocks[y].length; ++x) {
                if (finalizedBlocks[y][x] == null) {
                    continue;
                }

                g.setColor(finalizedBlocks[y][x]);
                g.fillRect(rect.getX() + x * blockSize, rect.getY() + y * blockSize, blockSize, blockSize);
            }
        }
    }

    public boolean pieceLanded(Piece piece) {
        for (int y = 0; y < piece.blocks.length; ++y) {
            for (int x = 0; x < piece.blocks[y].length; ++x) {
                if (!piece.blocks[y][x]) {
                    continue;
                }

                if (piece.x + x >= width) {
                    return true;
                }

                if (piece.y + y >= height) {
                    return true;
                }

                if (finalizedBlocks[piece.y + y][piece.x + x] != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean finalizePiece(Piece piece) {
        boolean gameEnded = false;
        for (int y = 0; y < piece.blocks.length; ++y) {
            for (int x = 0; x < piece.blocks[y].length; ++x) {
                if (!piece.blocks[y][x]) {
                    continue;
                }

                finalizedBlocks[piece.y + y][piece.x + x] = piece.color;
                if (piece.y + y == 0) {
                    gameEnded = true;
                }
            }
        }
        
        return gameEnded;
    }

    public boolean hasValidX(Piece piece) {
        for (int y = 0; y < piece.blocks.length; ++y) {
            for (int x = 0; x < piece.blocks[y].length; ++x) {
                if (!piece.blocks[y][x]) {
                    continue;
                }
                if (piece.y + y >= height) {
                    continue;
                }
            
                if (piece.x + x < 0)
                    return false;
                if (piece.x + x >= width)
                    return false;

                if (finalizedBlocks[piece.y + y][piece.x + x] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean nudgeToValid(Piece piece) {
        if (!hasValidX(piece)) {
            piece.x++;
            if (hasValidX(piece)) {
                return true;
            } else {
                piece.x -= 2;
                return hasValidX(piece);
            }
        }

        if (pieceLanded(piece)) {
            piece.y--;
            return pieceLanded(piece);
        }

        return true;
    }
}