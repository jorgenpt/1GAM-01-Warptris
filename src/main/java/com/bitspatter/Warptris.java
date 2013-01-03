package com.bitspatter;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

public class Warptris extends BasicGame {
    float blockSize;
    Rectangle boardRect;
    Board board;
    Piece currentPiece;
    int msTillNextStep;

    final int SECONDS_PER_STEP = 1;
    final int BOARD_MARGIN = 10;
    final int BOARD_WIDTH = 10, BOARD_HEIGHT = 22;

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        board.render(g, boardRect, blockSize);
        g.translate(boardRect.getX() + currentPiece.x * blockSize, boardRect.getY() + currentPiece.y * blockSize);
        currentPiece.render(g, blockSize);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        float boardHeight = gc.getHeight() - BOARD_MARGIN * 2;

        blockSize = boardHeight / BOARD_HEIGHT;
        boardRect = new Rectangle(BOARD_MARGIN, BOARD_MARGIN, blockSize * BOARD_WIDTH, boardHeight);

        board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
        currentPiece = generateNewPiece();

        msTillNextStep = SECONDS_PER_STEP * 1000;
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException {
        Input input = gc.getInput();
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

            currentPiece = generateNewPiece();
            return true;
        }

        return false;
    }

    private Piece generateNewPiece() throws SlickException {
        return Piece.createL();
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