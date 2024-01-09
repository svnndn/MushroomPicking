package ru.itis.game.components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Victory extends JFrame {
    public static boolean isEnd;

    public Victory(int winner) {
        if (!isEnd) {
            isEnd = true;
            JLabel label = null;
            if (winner == 1) {
                label = new JLabel("Поздравляем, синий игрок победил!");
            } else if (winner == 2) {
                label = new JLabel("Поздравляем, красный игрок победил!");
            }

            label.setHorizontalAlignment(SwingConstants.CENTER);
            getContentPane().add(label);

            setSize(300, 100);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            setVisible(true);

            Timer timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    System.exit(0);
                }
            });

            timer.setRepeats(false);
            timer.start();
        }
    }
}