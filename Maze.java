package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//start and stop buttons are only for solving the maze (spec only said start/stop for automated solver, did not mention generation)
//once you start solving maze, I purposefully made it so you can only pause it and continue but you have to solve the maze before generating a new one
//this is easy since you can solve the maze quickly by changing the speed

public class Maze {
    private final static int windowWidth = 1250;
    private final static int windowHeight = 600;

    //set up a window with the maze (mazeModel) and settings/stats (mazeView)
    //set up a controller that will communicate between the two
    public static void main(String[] args) {
        MazeModel maze = new MazeModel();
        MazeView mazeView = new MazeView();
        MazeController controller = new MazeController(maze, mazeView);
        JFrame mazeFrame = new JFrame("Maze");
        mazeFrame.getContentPane().setLayout(new FlowLayout());
        mazeFrame.getContentPane().add(maze);
        mazeFrame.getContentPane().add(mazeView);
        mazeFrame.setSize(windowWidth, windowHeight);
        mazeFrame.setVisible(true);
        mazeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
