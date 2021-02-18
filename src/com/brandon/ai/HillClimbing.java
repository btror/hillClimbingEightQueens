package com.brandon.ai;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HillClimbing {

    private static int[][] grid;
    private static JButton[][] buttonGrid;
    private static int gridDimension;
    private static int restarts = 0;
    private static int stateChanges = 0;
    private static int speed;

    /*
     * Constructor
     */
    public HillClimbing(JButton[][] buttonGridFrame, int dimension, int animationSpeed) {
        gridDimension = dimension;
        speed = animationSpeed;
        buttonGrid = buttonGridFrame;
    }


    /*
     * method that starts the animation
     */
    public void start() {
        Thread thread = new Thread(() -> {

            setupGrid(gridDimension);
            while (true) {
                int currentHeuristic = findCurrentStateHeuristic(grid, 0);
                System.out.println("\nCurrent Heuristic: " + currentHeuristic);
                System.out.println("Current State: ");
                printCurrentState();

                ArrayList<ArrayList<Integer>> possibleHeuristics = new ArrayList<>();
                for (int i = 0; i < grid.length; i++) {
                    ArrayList<Integer> temp = getColumnHeuristicStates(i);
                    possibleHeuristics.add(temp);
                }

                ArrayList<Integer> columnHeuristics = new ArrayList<>();
                for (ArrayList<Integer> temp : possibleHeuristics) {
                    int min = Collections.min(temp);
                    columnHeuristics.add(min);
                }

                int betterNeighbors = 0;
                for (Integer columnHeuristic : columnHeuristics) {
                    if (columnHeuristic < currentHeuristic) {
                        betterNeighbors++;
                    }
                }

                System.out.println("Neighbors With Lower Heuristic: " + betterNeighbors);
                if (betterNeighbors != 0) {
                    System.out.println("Setting New Current State");
                }

                int min = Collections.min(columnHeuristics);
                int minIndex = 0;
                for (int i = 0; i < columnHeuristics.size(); i++) {
                    if (columnHeuristics.get(i) == min) {
                        minIndex = i;
                    }
                }

                stateChanges++;
                changeGridState(minIndex);
                changeButtonGrid();

                try {
                    Thread.sleep(speed);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }

                if (min >= currentHeuristic) {
                    // change the spaces with queens on them to red
                    for (int i = 0; i < gridDimension; i++) {
                        for (int j = 0; j < gridDimension; j++) {
                            buttonGrid[i][j].setBackground(Color.RED);
                        }
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }


                    restarts++;
                    initializeStartState();
                    for (int i = 0; i < grid.length; i++) {
                        for (int j = 0; j < grid[0].length; j++) {
                            JButton button = buttonGrid[i][j];
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
                        }
                    }

                    System.out.println("RESTART");
                }

                if (min == 0) {
                    System.out.println("\nCurrent Heuristic: " + 0);
                    System.out.println("Current State: ");
                    printCurrentState();
                    System.out.println("Solution Found!");
                    System.out.println("State Changes: " + stateChanges);
                    System.out.println("Restarts: " + restarts);

                    String message = "State changes: " + stateChanges + "\nRestarts: " + restarts;
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), message, "Solution Found!", JOptionPane.INFORMATION_MESSAGE);
                    restarts = 0;
                    stateChanges = 0;
                    break;
                }


            }
        });
        thread.start();
    }


    /*
     * method that sets the speed of the animation
     */
    public void setSpeed(int s) {
        speed = s;
    }


    /*
     * method that sets the dimension of the grid
     */
    public void setDimension(int d) {
        gridDimension = d;
        setupGrid(d);
        initializeStartState();

    }


    /*
     * method that changes the button grid state
     */
    public void changeButtonGrid() {
        for (int i = 0; i < buttonGrid.length; i++) {
            for (int j = 0; j < buttonGrid[i].length; j++) {
                if (grid[i][j] == 0) {
                    buttonGrid[i][j].setText("");
                    buttonGrid[i][j].setIcon(null);
                } else {
                    try {
                        BufferedImage img = ImageIO.read(this.getClass().getResource("/images/queen_icon_v2.png"));
                        Image newImg = img.getScaledInstance(buttonGrid[i][j].getWidth(), buttonGrid[i][j].getHeight(), Image.SCALE_SMOOTH);
                        ImageIcon newIcon = new ImageIcon(newImg);
                        buttonGrid[i][j].setIcon(newIcon);
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }


    /*
     * method that generates a new grid
     */
    public static void changeGridState(int col) {
        ArrayList<Integer> stateValues = new ArrayList<>();

        ArrayList<int[][]> copies = new ArrayList<>();
        // create the copies
        for (int i = 0; i < grid.length; i++) {
            int[][] temp;
            temp = generateCopy();
            copies.add(temp);
        }

        // change the place of the queen in each copy
        for (int i = 0; i < copies.size(); i++) {
            int[][] temp = copies.get(i);
            for (int x = 0; x < temp.length; x++) {
                if (temp[x][col] == 1) {
                    temp[x][col] = 0;
                }
            }
            temp[i][col] = 1;
            copies.set(i, temp);
        }

        // find the heuristics of each copy
        for (int[][] copy : copies) {
            int value = findCurrentStateHeuristic(copy, 0);
            stateValues.add(value);
        }

        // change the grid if there isn't a local minima
        int currentStateValue = findCurrentStateHeuristic(grid, 0);
        int bestHeuristicValue = Collections.min(stateValues);

        if (bestHeuristicValue < currentStateValue) {
            // get the index of the best value
            for (int i = 0; i < stateValues.size(); i++) {
                if (stateValues.get(i) == bestHeuristicValue) {
                    grid = copies.get(i);
                    break;
                }
            }
        }
    }


    /*
     * method that gets the heuristic values of each row in a specified column
     */
    public static ArrayList<Integer> getColumnHeuristicStates(int col) {
        ArrayList<Integer> stateValues = new ArrayList<>();

        ArrayList<int[][]> copies = new ArrayList<>();
        // create the copies
        for (int i = 0; i < grid.length; i++) {
            int[][] temp;
            temp = generateCopy();
            copies.add(temp);
        }

        // change the place of the queen in each copy
        for (int i = 0; i < copies.size(); i++) {
            int[][] temp = copies.get(i);
            for (int x = 0; x < temp.length; x++) {
                if (temp[x][col] == 1) {
                    temp[x][col] = 0;
                }
            }
            temp[i][col] = 1;
            copies.set(i, temp);
        }

        // find the heuristics of each copy
        for (int[][] copy : copies) {
            int value = findCurrentStateHeuristic(copy, 0);
            stateValues.add(value);
        }

        return stateValues;
    }


    /*
     * method that makes copies of grids
     */
    public static int[][] generateCopy() {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int i = 0; i < copy.length; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, copy[i].length);
        }
        return copy;
    }


    /*
     * method to find the current heuristic state
     */
    public static int findCurrentStateHeuristic(int[][] tempGrid, int h) {
        int queen = 0;
        // check each column: locate the queen of each col
        for (int i = 0; i < tempGrid.length; i++) {
            if (tempGrid[i][0] == 1) {
                queen = i;
            }
        }

        // figure out the value of the queen at that spot
        h += measureHeuristic(tempGrid, queen, 0);
        // break down the temp grid
        if (tempGrid[0].length > 1) {
            int[][] modGrid = new int[tempGrid.length][tempGrid[0].length - 1];
            for (int i = 0; i < tempGrid.length; i++) {
                if (tempGrid[i].length - 1 >= 0)
                    System.arraycopy(tempGrid[i], 1, modGrid[i], 0, tempGrid[i].length - 1);
            }
            return findCurrentStateHeuristic(modGrid, h);
        }
        return h;
    }


    /*
     * method to setup a custom dimension of the grid
     */
    public static void setupGrid(int dimension) {
        grid = new int[dimension][dimension];
        initializeStartState();
    }


    /*
     * method to setup a random start state
     */
    public static void initializeStartState() {
        // text grid
        for (int[] ints : grid) {
            Arrays.fill(ints, 0);
        }
        for (int i = 0; i < grid.length; i++) {
            int random = (int) (Math.random() * grid.length);
            grid[random][i] = 1;
        }


    }


    /*
     * method to print the current state
     */
    public static void printCurrentState() {
        for (int[] ints : grid) {
            for (int j = 0; j < grid.length; j++) {
                System.out.print(ints[j] + "  ");
            }
            System.out.println();
        }
    }


    /*
     * method to measure the heuristic value of a given coordinate
     */
    public static int measureHeuristic(int[][] grid, int i, int j) {
        int points = 0;
        // check for horizontal queens
        for (int x = 0; x < grid[0].length; x++) {
            if (j == x) {
                continue;
            }
            if (grid[i][x] == 1) {
                points += 1;
            }
        }

        // check diagonal right up
        int iNew = i;
        int jNew = j;
        for (int ru = 0; ru < grid.length; ru++) {
            if (iNew == 0 || jNew == grid[0].length - 1) {
                break;
            }
            if (grid[iNew - 1][jNew + 1] == 1) {
                points += 1;
            }
            iNew--;
            jNew++;
        }

        // check diagonal right down
        iNew = i;
        jNew = j;
        for (int rd = 0; rd < grid.length; rd++) {
            if (iNew == grid.length - 1 || jNew == grid[0].length - 1) {
                break;
            }
            if (grid[iNew + 1][jNew + 1] == 1) {
                points += 1;
            }
            iNew++;
            jNew++;
        }

        // check diagonal left up
        iNew = i;
        jNew = j;
        for (int lu = 0; lu < grid.length; lu++) {
            if (iNew == 0 || jNew == 0) {
                break;
            }
            if (grid[iNew - 1][jNew - 1] == 1) {
                points += 1;
            }
            iNew--;
            jNew--;
        }

        // check diagonal left down
        iNew = i;
        jNew = j;
        for (int ld = 0; ld < grid.length; ld++) {
            if (iNew == grid.length - 1 || jNew == 0) {
                break;
            }
            if (grid[iNew + 1][jNew - 1] == 1) {
                points += 1;
            }
            iNew++;
            jNew--;
        }
        return points;
    }

}
