package com.brandon.window;

import com.brandon.ai.HillClimbing;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Window extends JFrame {

    private final JPanel panel;
    private JButton[][] grid;
    private final int gridDimension = 8;
    private int speed = 200;
    private HillClimbing hc;


    public Window() {
        setSize(new Dimension(600, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Hill climbing with random restarts AI (Brandon Rorie)");
        try {
            BufferedImage img = ImageIO.read(this.getClass().getResource("/images/queen_frame_icon.png"));
            setIconImage(img);
        } catch (IOException ignored){}
        setResizable(false);
        setupMenu();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 1));
        panel = new JPanel();
        panel.setLayout(new GridLayout(gridDimension, gridDimension));

        setupGrid();
        hc = new HillClimbing(grid, gridDimension, speed);

        mainPanel.add(panel);
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu start = new JMenu("Run");
        JMenu algorithmSpeed = new JMenu("Speed");

        JMenuItem runAi = new JMenuItem("Start / Restart AI");

        JMenuItem slow = new JMenuItem("Slow");
        JMenuItem medium = new JMenuItem("Medium");
        JMenuItem fast = new JMenuItem("Fast");
        JMenuItem veryFast = new JMenuItem("Very Fast");


        runAi.addActionListener(e -> {
            hc = new HillClimbing(grid, gridDimension, speed);
            hc.start();
        });

        slow.addActionListener(e -> {
            speed = 1500;
            hc.setSpeed(speed);
        });

        medium.addActionListener(e -> {
            speed = 500;
            hc.setSpeed(speed);
        });

        fast.addActionListener(e -> {
            speed = 200;
            hc.setSpeed(speed);
        });

        veryFast.addActionListener(e -> {
            speed = 0;
            hc.setSpeed(speed);
        });


        algorithmSpeed.add(slow);
        algorithmSpeed.add(medium);
        algorithmSpeed.add(fast);
        algorithmSpeed.add(veryFast);

        start.add(runAi);

        menuBar.add(start);
        menuBar.add(algorithmSpeed);

        setJMenuBar(menuBar);

    }

    public void setupGrid() {
        grid = new JButton[gridDimension][gridDimension];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                JButton button = new JButton();
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        button.setBackground(Color.decode("#F1DAB4"));
                    } else {
                        button.setBackground(Color.decode("#B88760"));
                    }
                }
                if (i % 2 != 0) {
                    if (j % 2 == 0) {
                        button.setBackground(Color.decode("#B88760"));
                    } else {
                        button.setBackground(Color.decode("#F1DAB4"));
                    }
                }

                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panel.add(button);
                grid[i][j] = button;
            }
        }
    }

}
