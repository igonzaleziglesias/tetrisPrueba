/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris2 extends JFrame {

    JLabel marcador;

    public Tetris2() {

        marcador = new JLabel(" 0");
        add(marcador, BorderLayout.SOUTH);
        Tablero board = new Tablero(this);
        add(board);
        board.start();

        setSize(400, 800);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel getStatusBar() {
        return marcador;
    }

    public static void main(String[] args) {

        Tetris2 game = new Tetris2();
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}
