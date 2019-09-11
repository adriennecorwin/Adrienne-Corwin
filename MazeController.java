package com.company;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MazeController {

    private MazeView view;
    private MazeModel maze;
    private final int delay = 1000; //animation occurs at one frame per 1/speed of a second
    private Timer timer; //to detect maze status
    private Timer genTimer; //to generate maze step by step
    private Timer solveTimer; //to solve maze step by step
    private float percentVisited;
    private boolean start; //if maze has started solving (true)
    private String genOrSolve; //if the maze is generating ("gen) or solving ("solve")

    public MazeController(MazeModel maze, MazeView view){
        this.maze = maze;
        this.view = view;
        start = false; //initially, not solving

        this.view.getStartStopButton().setEnabled(false); //don't allow user to stop/start until solving has started

        //detects when maze is generating, generated, solving, solved (checks on maze status every second)
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(maze.getGenDone()){ //when maze is done generating
                    view.getDoneLabel().setText("Maze Generated!");
                    view.getVisitedLabel().setText("");
                    view.enableSettings();
                    if(start) { //if maze has started solving
                        view.disableSettings();
                        view.getGenerateButton().setEnabled(false); //don't let user generate new maze until current one is solved
//                        percentVisited = 100 * (maze.getNumVisited() / (view.getCols() * view.getRows())); //get percent of cells
//                        view.getVisitedLabel().setText("Visited: " + percentVisited + "%");
                        if (maze.getMazeSolved()) { //if maze has been solved
                            view.getDoneLabel().setText("Solved!");
                            timer.stop();
                            solveTimer.stop();
                            view.getStartStopButton().setEnabled(false);
                            view.enableSettings();
                            view.getGenerateButton().setEnabled(true);
                            start = false;
                        } else { //if maze not solved yet
                            view.getStartStopButton().setEnabled(true);
                            view.getDoneLabel().setText("Solving...");
                            if(!maze.getMazeSolvable()){ //if maze is not solvable
                                view.getDoneLabel().setText("Maze unsolvable");
                                solveTimer.stop();
                                timer.stop();
                            }
                        }
                    }
                    else{ //if maze is stopped solving
                        view.enableSettings();
                        view.getGenerateButton().setEnabled(true);
                        if(maze.getMazeSolved()){
                            solveTimer.stop();
                        }
                    }
                }
                else{ //if maze is still generating
                    view.getDoneLabel().setText("Generating Maze...");
                    view.getVisitedLabel().setText("");
                    view.disableSettings();
                    view.getStartStopButton().setEnabled(false);
                }
            }
        });

        //generate maze step by step at given speed from slider
        genTimer = new Timer(delay/view.getSpeedSlider().getValue(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maze.generateMazeStep();
            }
        });


        //solve maze step by step at given speed from slider
        solveTimer = new Timer(delay/view.getSpeedSlider().getValue(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                percentVisited = 100 * (maze.getNumVisited() / (view.getCols() * view.getRows()));
                view.getVisitedLabel().setText("Visited: " + percentVisited + "%");
                maze.solveMazeStep();
            }
        });

        //when generate button pressed
        view.getGenerateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
                solveTimer.stop();
                genOrSolve = "gen";
                maze.initMazeOfSize(view.getCols(), view.getRows());
//                maze.initMazeOfSize(6, 6);

                if(!view.getShowGen()){
                    maze.generateTotalMaze();
                }
                else{
                    maze.initializeMazeGen();
                    genTimer.start();
                }
            }
        });

        //when speed slider changes value, reset solve and gen timers to be of the specified delay
        view.getSpeedSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                genTimer.stop();
                timer.start();
                genTimer = new Timer(delay / view.getSpeedSlider().getValue(), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        maze.generateMazeStep();
                    }
                });
                if(genOrSolve!=null) { //if slider is manipulated before generate or solve buttons pressed don't start either timer but set their delays
                    if (genOrSolve.equals("gen")) {
                        genTimer.start();
                    }
                }
                solveTimer.stop();
                solveTimer = new Timer(delay / view.getSpeedSlider().getValue(), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        percentVisited = 100 * (maze.getNumVisited() / (view.getCols() * view.getRows()));
                        view.getVisitedLabel().setText("Visited: " + percentVisited + "%");
                        maze.solveMazeStep();
                    }
                });
                if(genOrSolve!=null) {
                    if (genOrSolve.equals("solve")) {
                        timer.start();
                        solveTimer.start();
                    }
                }
            }
        });

        //if solve button is pressed
        view.getSolveButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genTimer.stop();
                genOrSolve = "solve";
                timer.start();
                start = true;
                if(!view.getShowSolve()){
                    maze.solveTotalMaze();
                }
                else{
                    maze.initSolveMaze();
                    solveTimer.start();
                }
            }
        });

        //when user wants to pause or continue the maze solving
        view.getStartStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!start){ //if maze is stopped solving
                    timer.start();
                    solveTimer.start();
                    start=true;
                    view.getStartStopButton().setText("Stop");
                }
                else{ //if maze is solving
                    timer.stop();
                    solveTimer.stop();
                    start=false;
                    view.getStartStopButton().setText("Start");
                }
            }
        });

        this.maze.initMazeOfSize(view.getCols(), view.getRows());

        this.maze.generateTotalMaze();

    }


}