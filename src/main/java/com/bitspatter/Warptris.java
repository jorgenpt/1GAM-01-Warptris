package com.bitspatter;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public class Warptris extends BasicGame implements MouseListener {
    final int SECONDS_PER_STEP = 1;
    final int BOARD_MARGIN = 10;
    final int BOARD_WIDTH = 10, BOARD_HEIGHT = 22;

    // Board including landed pieces.
    Board board;
    // Currently dropping piece.
    Piece currentPiece;

    // Where we render the board in the window.
    Rectangle boardRect;

    // Sides of a block.
    float blockSize;

    // Number of ms until we do a "soft drop" (i.e. move the current piece one step down)
    int msTillNextStep;

    // Whether or not we're currently in "warp" mode, i.e. game paused and allowing player to move blocks.
    boolean warping = false;

    // This is the delta between the mouse pointer and the top left of the currently dragged block, to make dragging
    // feel natural.
    int dragOffsetX, dragOffsetY;

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (warping) {
            board.renderWarping(g, boardRect);
        }

        g.translate(boardRect.getX() + currentPiece.x * blockSize, boardRect.getY() + currentPiece.y * blockSize);
        currentPiece.render(g, blockSize);
        g.resetTransform();

        if (warping) {
            Input input = gc.getInput();
            g.setClip(boardRect);
            currentPiece.renderDraggable(g, input.getMouseX() - dragOffsetX, input.getMouseY() - dragOffsetY, blockSize);
            g.clearClip();
        }

        board.render(g, boardRect, blockSize, warping);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        float boardHeight = gc.getHeight() - BOARD_MARGIN * 2;
        blockSize = boardHeight / BOARD_HEIGHT;
        boardRect = new Rectangle(BOARD_MARGIN, BOARD_MARGIN, blockSize * BOARD_WIDTH, boardHeight);

        msTillNextStep = SECONDS_PER_STEP * 1000;

        board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
        currentPiece = Piece.getRandomPiece();

        Input input = gc.getInput();
        input.enableKeyRepeat();
        input.addMouseListener(this);
    }

    void toggleWarping() {
        if (!warping && currentPiece.warped)
            return;

        warping = !warping;
        if (!warping) {
            currentPiece.stopDrag();
        }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
        if (input.isKeyPressed(Input.KEY_SPACE)) {
            toggleWarping();
        }

        if (warping) {
            return;
        }

        if (input.isKeyPressed(Input.KEY_LEFT)) {
            movePieceHorizontally(-1);
        } else if (input.isKeyPressed(Input.KEY_RIGHT)) {
            movePieceHorizontally(1);
        }

        if (input.isKeyPressed(Input.KEY_Z)) {
            rotatePiece(false);
        } else if (input.isKeyPressed(Input.KEY_X)) {
            rotatePiece(true);
        }

        if (input.isKeyPressed(Input.KEY_DOWN)) {
            msTillNextStep = SECONDS_PER_STEP * 1000;
            lowerPiece();
        } else if (input.isKeyPressed(Input.KEY_UP)) {
            while (!lowerPiece())
                ;
        } else {
            msTillNextStep -= delta;
            if (msTillNextStep < 0) {
                msTillNextStep += SECONDS_PER_STEP * 1000;
                lowerPiece();
            }
        }
    }

    private void rotatePiece(boolean clockwise) throws SlickException {
        Piece newPiece = currentPiece.rotated(clockwise);
        if (board.nudgeToValid(newPiece)) {
            currentPiece = newPiece;
        }
    }

    private void movePieceHorizontally(int steps) {
        currentPiece.x += steps;
        if (!board.hasValidX(currentPiece)) {
            currentPiece.x -= steps;
        }
    }

    private boolean lowerPiece() throws SlickException {
        currentPiece.y++;
        if (board.pieceLanded(currentPiece)) {
            currentPiece.y--;
            if (board.finalizePiece(currentPiece)) {
                System.exit(0);
            }

            currentPiece = Piece.getRandomPiece();
            return true;
        }

        return false;
    }

    @Override
    public boolean isAcceptingInput() {
        return warping;
    }

    int blockXFromPixel(int pixelX) {
        return (int) ((pixelX - boardRect.getX()) / blockSize);
    }

    int blockYFromPixel(int pixelY) {
        return (int) ((pixelY - boardRect.getY()) / blockSize);
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (button != 0) {
            return;
        }

        if (boardRect.contains(x, y)) {
            int blockX = blockXFromPixel(x);
            int blockY = blockYFromPixel(y);
            if (!currentPiece.contains(blockX, blockY)) {
                return;
            }

            dragOffsetX = (int) (x - (blockX * blockSize + boardRect.getX()));
            dragOffsetY = (int) (y - (blockY * blockSize + boardRect.getY()));

            currentPiece.startDrag(blockX, blockY);
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            if (boardRect.contains(x, y)) {
                if (currentPiece.stopDrag(blockXFromPixel(x), blockYFromPixel(y))) {
                    currentPiece.warped = true;
                    warping = false;
                }
            } else {
                currentPiece.stopDrag();
            }
        }
    }

    public Warptris() {
        super("Warptris");
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Warptris());

        app.setShowFPS(false);
        app.setDisplayMode(800, 600, false);
        app.start();
    }
}