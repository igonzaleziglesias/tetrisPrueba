/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import tetris2.Piezas.PiezasTetris;

public class Tablero extends JPanel implements ActionListener {

    final int anchoTablero = 10;
    final int altoTablero = 22;

    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Piezas curPiece;
    PiezasTetris[] board;

    public Tablero(Tetris2 parent) {

        setFocusable(true);
        curPiece = new Piezas();
        timer = new Timer(400, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new PiezasTetris[anchoTablero * altoTablero];
        addKeyListener(new InteraccionTeclas());
        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            nuevaPieza();
        } else {
            oneLineDown();
        }
    }

    int squareWidth() {
        return (int) getSize().getWidth() / anchoTablero;
    }

    int squareHeight() {
        return (int) getSize().getHeight() / altoTablero;
    }

    PiezasTetris shapeAt(int x, int y) {
        return board[(y * anchoTablero) + x];
    }

    public void start() {
        if (isPaused) {
            return;
        }

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        nuevaPieza();
        timer.start();
    }

    private void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - altoTablero * squareHeight();

        for (int i = 0; i < altoTablero; ++i) {
            for (int j = 0; j < anchoTablero; ++j) {
                PiezasTetris shape = shapeAt(j, altoTablero - i - 1);
                if (shape != PiezasTetris.NoPieza) {
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getPieza() != PiezasTetris.NoPieza) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (altoTablero - y - 1) * squareHeight(),
                        curPiece.getPieza());
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    private void clearBoard() {
        for (int i = 0; i < altoTablero * anchoTablero; ++i) {
            board[i] = PiezasTetris.NoPieza;
        }
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * anchoTablero) + x] = curPiece.getPieza();
        }

        BorrarLineas();

        if (!isFallingFinished) {
            nuevaPieza();
        }
    }

    private void nuevaPieza() {
        curPiece.setPìezaAleatoria();
        curX = anchoTablero / 2 + 1;
        curY = altoTablero - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.establecerPieza(PiezasTetris.NoPieza);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
        }
    }

    private boolean tryMove(Piezas newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= anchoTablero || y < 0 || y >= altoTablero) {
                return false;
            }
            if (shapeAt(x, y) != PiezasTetris.NoPieza) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void BorrarLineas() {
        int numFullLines = 0;

        for (int i = altoTablero - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < anchoTablero; ++j) {
                if (shapeAt(j, i) == PiezasTetris.NoPieza) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < altoTablero - 1; ++k) {
                    for (int j = 0; j < anchoTablero; ++j) {
                        board[(k * anchoTablero) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.establecerPieza(PiezasTetris.NoPieza);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, PiezasTetris shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };

        Color color = colors[shape.ordinal()];
        
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    class InteraccionTeclas extends KeyAdapter {

        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getPieza() == PiezasTetris.NoPieza) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused) {
                return;
            }

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
//                    tryMove(curPiece.GirarDerecha(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.GirarDerecha(), curX, curY);
//                    tryMove(curPiece.GirarIzquierda(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
            }

        }
    }
}
