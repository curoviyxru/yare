package moe.yare.ui;

import javax.swing.*;

public class Frame extends JFrame {

    public Frame() {
        setContentPane(new Canvas());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 600);
    }

    public static void main(String[] args)
            throws UnsupportedLookAndFeelException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Frame().setVisible(true);
    }
}
