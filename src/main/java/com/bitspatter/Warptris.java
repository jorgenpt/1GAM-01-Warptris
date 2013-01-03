package com.bitspatter;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import com.bitspatter.renderer.BlockRenderer;

public class Warptris extends BasicGame implements MouseListener {
    final int SECONDS_PER_STEP = 1;
    final int BOARD_MARGIN = 10;
    final int BOARD_WIDTH = 10, BOARD_HEIGHT = 22;

    // Board including landed pieces.
    Board board;
    // Currently dropping piece.
    Piece currentPiece;

    BlockRenderer blockRenderer;
    Rectangle boardRenderArea;

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
            board.renderWarping(g, boardRenderArea);
        }

        currentPiece.render(g);

        if (warping) {
            Input input = gc.getInput();
            g.setClip(boardRenderArea);
            currentPiece.renderDraggable(g, input.getMouseX() - dragOffsetX, input.getMouseY() - dragOffsetY);
            g.clearClip();
        }

        board.render(g, boardRenderArea, warping);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        initializeRenderer(gc);

        board = new Board(BOARD_WIDTH, BOARD_HEIGHT, blockRenderer);

        Piece.createPieces(blockRenderer);
        currentPiece = Piece.getRandomPiece();

        initializeInput(gc);

        msTillNextStep = SECONDS_PER_STEP * 1000;
    }

    private void initializeInput(GameContainer gc) {
        Input input = gc.getInput();
        input.enableKeyRepeat();
        input.addMouseListener(this);
    }

    private void initializeRenderer(GameContainer gc) {
        int boardHeight = gc.getHeight() - BOARD_MARGIN * 2;
        int blockSize = boardHeight / BOARD_HEIGHT;
        boardHeight = blockSize * BOARD_HEIGHT;
        float actualYMargin = (gc.getHeight() - boardHeight) / 2.0f;

        boardRenderArea = new Rectangle(BOARD_MARGIN, actualYMargin, blockSize * BOARD_WIDTH, boardHeight);
        blockRenderer = new BlockRenderer(boardRenderArea, blockSize);
    }

    private void toggleWarping() {
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

        board.update(delta);

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
        currentPiece.topLeftX += steps;
        if (board.pieceLanded(currentPiece)) {
            currentPiece.topLeftX -= steps;
        }
    }

    private boolean lowerPiece() throws SlickException {
        currentPiece.topLeftY++;
        if (board.pieceLanded(currentPiece)) {
            currentPiece.topLeftY--;
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

    @Override
    public void mousePressed(int button, int x, int y) {
        if (button != 0) {
            return;
        }

        int blockX = blockRenderer.getBlockX(x);
        int blockY = blockRenderer.getBlockX(y);
        if (!currentPiece.contains(blockX, blockY)) {
            return;
        }

        dragOffsetX = x - blockRenderer.getX(blockX);
        dragOffsetY = y - blockRenderer.getY(blockY);
        currentPiece.startDrag(blockX, blockY);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0) {
            int blockX = blockRenderer.getBlockX(x);
            int blockY = blockRenderer.getBlockX(y);
            if (board.contains(blockX, blockY)) {
                if (currentPiece.stopDrag(blockX, blockY)) {
                    currentPiece.warped = true;
                    warping = false;
                    board.mutate();
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