package com.bitspatter;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.bitspatter.renderer.BlockRenderer;

public class Board {
    int width, height;
    Color[][] finalizedBlocks;
    Image warpCloud;
    BlockRenderer blockRenderer;
    BoardMutation pendingMutation;

    public Board(int width, int height, BlockRenderer blockRenderer) {
        this.width = width;
        this.height = height;
        this.blockRenderer = blockRenderer;
        this.finalizedBlocks = new Color[height][width];
        try {
            this.warpCloud = new Image("resources/warp_cloud.jpeg");
        } catch (SlickException se) {
        }
    }

    public void render(Graphics g, Rectangle rect, boolean warping) {
        g.setColor(Color.white);
        g.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

        if (warping)
            return;

        for (int y = 0; y < finalizedBlocks.length; ++y) {
            for (int x = 0; x < finalizedBlocks[y].length; ++x) {
                if (finalizedBlocks[y][x] == null) {
                    continue;
                }

                blockRenderer.render(g, x, y, finalizedBlocks[y][x]);
            }
        }

        if (pendingMutation != null) {
            pendingMutation.render(g);
        }
    }

    public void renderWarping(Graphics g, Rectangle rect) {
        g.setClip(rect);
        for (float x = rect.getX(); x <= rect.getWidth(); x += warpCloud.getWidth()) {
            for (float y = rect.getY(); y <= rect.getHeight(); y += warpCloud.getHeight()) {
                g.drawImage(warpCloud, x, y);
            }
        }
        g.clearClip();
    }

    public void update(int delta) {
        if (pendingMutation != null) {
            if (!pendingMutation.animate(delta)) {
                finalizedBlocks[pendingMutation.y][pendingMutation.toX] = pendingMutation.color;
                pendingMutation = null;
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
        for (int y = 0; y < piece.getHeight(); ++y) {
            for (int x = 0; x < piece.getWidth(); ++x) {
                if (!piece.blocks[y][x]) {
                    continue;
                }

                finalizedBlocks[piece.y + y][piece.x + x] = piece.color;
                if (piece.y + y == 0) {
                    gameEnded = true;
                }
            }
        }

        for (int y = piece.getHeight() - 1; y >= 0; --y) {
            int blockY = piece.y + y;
            if (blockY >= height) {
                continue;
            }

            boolean wholeRow = true;
            for (int x = 0; x < width; ++x) {
                if (finalizedBlocks[blockY][x] == null) {
                    wholeRow = false;
                    break;
                }
            }

            if (wholeRow) {
                clearRow(blockY);
                y++;
            }
        }

        return gameEnded;
    }

    private void clearRow(int row) {
        for (int y = row; y > 0; --y) {
            for (int x = 0; x < width; ++x) {
                finalizedBlocks[y][x] = finalizedBlocks[y - 1][x];
            }
        }
        for (int x = 0; x < width; ++x) {
            finalizedBlocks[0][x] = null;
        }
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

    Random random = new Random();

    public void mutate() {
        int numUnchecked = width * height;
        boolean checked[][] = new boolean[height][width];

        while (true) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (checked[y][x]) {
                continue;
            }

            checked[y][x] = true;
            numUnchecked--;

            if (finalizedBlocks[y][x] != null) {
                if ((y ^ x) % 2 == 0) {
                    if (swap(x, y, x + 1)) {
                        return;
                    } else if (swap(x, y, x - 1)) {
                        return;
                    }
                } else {
                    if (swap(x, y, x - 1)) {
                        return;
                    } else if (swap(x, y, x + 1)) {
                        return;
                    }
                }
            }

            if (numUnchecked <= 0) {
                System.out.println("Could not find a free block to mutate.");
                return;
            }
        }
    }

    public boolean swap(int x, int y, int newX) {
        if (newX < width && newX >= 0 && finalizedBlocks[y][newX] == null) {
            pendingMutation = new BoardMutation(blockRenderer, y, x, newX, finalizedBlocks[y][x]);
            finalizedBlocks[y][x] = null;
            // We use black so that it doesn't show anything, but so that it's still picked up by the collision
            // detection.
            finalizedBlocks[y][newX] = Color.black;
            return true;
        }

        return false;
    }

    public boolean contains(int blockX, int blockY) {
        if (blockX < 0 || blockX >= width) {
            return false;
        }

        if (blockY < 0 || blockY >= height) {
            return false;
        }

        return true;
    }
}