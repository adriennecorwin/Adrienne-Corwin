package com.company;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MazeView extends JPanel {

    private final int minRes = 10; //minimum 10x10 maze
    private final int maxRes = 60; //maximum 60x60

    private JPanel statsPanel; //holds maze status (solving, generating, etc.) and %visited
    private JButton generateButton; //generates a random maze
    private JCheckBox showGen; //show generation step by step
    private JButton solveButton; //solve maze
    private JCheckBox showSolve; //show solution step by step
    private JLabel speedLabel;
    private JSlider speedSlider; //conntrols the speed of the generation/solving
    private JLabel rowsLabel;
    private JSlider rowsSlider; //controls #rows in maze
    private JLabel colsLabel;
    private JSlider colsSlider; //controls #cols in maze
    private JButton startStopButton; //pauses and continues solving of a maze
    private JLabel doneLabel; //status of maze
    private JLabel visitedLabel; //% of elements visited

    //set up all buttons and sliders
    public MazeView(){
        this.setPreferredSize(new Dimension(500,500));
        GridLayout settingsLayout = new GridLayout(12,1);
        this.setLayout(settingsLayout);
        generateButton = new JButton("Generate");
        showGen = new JCheckBox("Show generation");
        solveButton = new JButton("Solve");
        showSolve = new JCheckBox("Show solver");
        speedLabel = new JLabel("Speed: ");
        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10000, 1);
        rowsSlider = new JSlider(SwingConstants.HORIZONTAL, minRes, maxRes, minRes);
        rowsLabel = new JLabel("Rows: "+rowsSlider.getValue());
        colsSlider = new JSlider(SwingConstants.HORIZONTAL, minRes, maxRes, minRes);
        colsLabel = new JLabel("Cols: "+colsSlider.getValue());
        startStopButton = new JButton("Stop");

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        rowsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rowsLabel.setText("Rows: "+rowsSlider.getValue());
            }
        });

        colsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                colsLabel.setText("Cols: "+colsSlider.getValue());
            }
        });

        statsPanel = new JPanel();
        doneLabel = new JLabel("");
        visitedLabel = new JLabel("Visited: 0.0%");
        statsPanel.add(doneLabel);
        statsPanel.add(visitedLabel);

        this.add(generateButton);
        this.add(showGen);
        this.add(solveButton);
        this.add(showSolve);
        this.add(speedLabel);
        this.add(speedSlider);
        this.add(rowsLabel);
        this.add(rowsSlider);
        this.add(colsLabel);
        this.add(colsSlider);
        this.add(startStopButton);
        this.add(statsPanel);
    }


    //GETTERS
    public boolean getShowGen(){
        if(showGen.isSelected()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean getShowSolve(){
        if(showSolve.isSelected()){
            return true;
        }
        else{
            return false;
        }
    }

    public int getRows(){
        return rowsSlider.getValue();
    }

    public int getCols(){
        return colsSlider.getValue();
    }

    public JButton getGenerateButton(){
        return generateButton;
    }

    public JButton getStartStopButton(){
        return startStopButton;
    }

    public JSlider getSpeedSlider(){
        return speedSlider;
    }

    public JButton getSolveButton(){
        return solveButton;
    }

    public JLabel getDoneLabel(){
        return doneLabel;
    }

    public JLabel getVisitedLabel(){
        return visitedLabel;
    }

    //disables some settings so user can't change things while generating or solving
    public void disableSettings(){
        showGen.setEnabled(false);
        solveButton.setEnabled(false);
        showSolve.setEnabled(false);
        rowsSlider.setEnabled(false);
        colsSlider.setEnabled(false);
        startStopButton.setEnabled(false);

    }

    //reenables settings once generating/solving are done
    public void enableSettings(){
        showGen.setEnabled(true);
        solveButton.setEnabled(true);
        showSolve.setEnabled(true);
        rowsSlider.setEnabled(true);
        colsSlider.setEnabled(true);
//        startStopButton.setEnabled(true);
    }

}
