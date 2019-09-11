package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;


public class MazeModel extends JPanel {

    //make integers to indicate directions
    private final int right = 0;
    private final int left = 1;
    private final int top = 2;
    private final int bottom = 3;

    private float numVisited; //number of visited maze elements
    private int numRows;
    private int numCols;
    private int size;

    private boolean solvable; //true if maze is solvable
    private int endX;  //x pos of ending maze element
    private int endY; //y pos of ending maze element
    private MazeElement N; //current maze element
    private MazeElement A; //adjacent maze element
    private Stack<MazeElement> genStack; //stack for generation
    private Stack<MazeElement> solveStack; //stack for solving
    private MazeElement[][] mazeElements; //array for holding all maze elements
    private ArrayList<Integer>solveDirections; //keeps track of directions taken while solving so program can know where it is coming from
    private boolean genDone; //if done generating (true)
    private boolean mazeSolved; //if maze solved (true)
    private ArrayList<MazeElement>visited; //stores of all maze elements solution path has visited

    public MazeModel() {
        mazeSolved = false;
        solvable = true;
    }

    //GETTERS

    public boolean getGenDone(){
        return genDone;
    }

    public float getNumVisited() {
        return numVisited;
    }

    public boolean getMazeSolved(){
        return mazeSolved;
    }

    public boolean getMazeSolvable(){
        return solvable;
    }


    //make a maze with specified number of rows and columns
    public void initMazeOfSize(int cols, int rows){
        numRows=rows;
        numCols=cols;
        endX = numCols-1;
        endY = numRows -1;
        this.removeAll();
        mazeElements = new MazeElement[cols][rows];
        GridLayout mazeLayout = new GridLayout(rows, cols);
        this.setLayout(mazeLayout);
        if(rows>cols){ //maze element panel size should be based on largest val for col or rows
            size=rows;
        }
        else{
            size=cols;
        }
        for(int i=0; i<rows; i++){ //create maze elements for maze of specified sized
            for(int j=0; j<cols; j++){
                MazeElement mazeElement = new MazeElement(size, endX, endY);
                mazeElement.setVisited(false);
                mazeElements[j][i]=mazeElement;
                mazeElement.setXY(j, i);
                this.add(mazeElement);
            }
        }

        repaint();
        revalidate();
    }


    //GENERATE MAZE HELPER FUNCTIONS

    //reset all maze elements to be unvisited
    private void resetUnvisited(){
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numCols; j++){
                mazeElements[0][0].setVisited(false);
            }
        }
    }

    //first stop of every maze generation
    public void initializeMazeGen(){
        genDone=false;
        genStack = new Stack();
        resetUnvisited();
        N = mazeElements[0][0];
        N.setVisited(true);
        genStack.push(N);
    }

    //get all unvisited neighbors of N
    //if array[direction] has a maze element, then there is an unvisited neighbor in that direction
    //if array[direction] is null, no unvisited neighbor in that direction
    private MazeElement[] getUnvisited(MazeElement N){
        MazeElement unvisited[] = new MazeElement[4];
        if(N.getx()+1<numCols){
            if(!mazeElements[N.getx()+1][N.gety()].getVisited()){
                unvisited[0] = mazeElements[N.getx()+1][N.gety()];
            }
            else{
                unvisited[0] = null;
            }
        }
        if(N.getx()-1>=0){
            if(!mazeElements[N.getx()-1][N.gety()].getVisited()){
                unvisited[1]=(mazeElements[N.getx()-1][N.gety()]);
            }
            else{
                unvisited[1] = null;
            }
        }
        if(N.gety()-1>=0){
            if(!mazeElements[N.getx()][N.gety()-1].getVisited()){
                unvisited[2] = (mazeElements[N.getx()][N.gety()-1]);
            }
            else{
                unvisited[2] = null;
            }
        }
        if(N.gety()+1<numRows){
            if(!mazeElements[N.getx()][N.gety()+1].getVisited()){
                unvisited[3] = (mazeElements[N.getx()][N.gety()+1]);
            }
            else{
                unvisited[3] = null;
            }
        }
        return unvisited;
    }

    //picks random unvisited neighbor and draws a maze path
    //returns null if no unvisited neighbors
    private MazeElement getRandNeighbor(MazeElement N){
        MazeElement unvisited[] = getUnvisited(N); //get all unvisited neightbors of N
        if(unvisited[0]==null && unvisited[1]==null && unvisited[2]==null && unvisited[3]==null){ //if no unvisited neighbors
            return null;
        }
        else {//if at least one unvisited neighbor
            //randomly pick direction until that direction leads to an unvisited neighbor
            int neighbor = ThreadLocalRandom.current().nextInt(0, 4);
            while(unvisited[neighbor]==null){
                neighbor = ThreadLocalRandom.current().nextInt(0, 4);
            }
            MazeElement A = unvisited[neighbor];

            //make path between current element and neighboring element that was just selected
            //return that neighboring element
            if(neighbor==right){
                N.setRightNeighbor();
                A.setLeftNeighbor();
            }
            else if(neighbor==left){
                N.setLeftNeighbor();
                A.setRightNeighbor();
            }
            else if(neighbor==top){
                N.setTopNeighbor();
                A.setBottomNeighbor();
            }
            else if(neighbor==bottom){
                N.setBottomNeighbor();
                A.setTopNeighbor();
            }
            return A;
        }
    }

    //find an element in the stack with at least one unvisited neighbor
    private void findUnvisitedInStack(){
        N = getRandNeighbor(N);
        while (N == null && !genStack.isEmpty()) {
            N = genStack.pop();
            N = getRandNeighbor(N);
        }
        if(!genStack.empty()) { //once element is found go find a random unvisited neight and make a path
            N.setVisited(true);
            makePath();
        } //if no elements with unvisited neightbors do nothing
    }

    private void makePath(){
        A = getRandNeighbor(N); //makes path between N and A if A exists
        if(A==null) { //if no A (no unvisited neighbors, find one in the stack)
            findUnvisitedInStack();
        }
        else{
            N = A;
            genStack.push(N);
            N.setVisited(true);
        }
    }

    //generates maze instantly
    public void generateTotalMaze(){
        initializeMazeGen();
        while(!genStack.isEmpty()){
            makePath();
        }
        genDone=true;
    }

    //generates maze step
    public void generateMazeStep(){
        if (!genStack.empty()) {
            genDone = false;
            makePath();
        } else {
            genDone = true;
        }
    }


    //SOLVE MAZE HELPER FUNCTIONS

    //if there are unvisited neighboring paths return true
    private boolean checkPath(){
        boolean isPath = false;
        if(N.getx()-1>=0){
            if(N.getLeft() && !visited.contains(mazeElements[N.getx()-1][N.gety()])) {
                isPath = true;
            }
        }
        if(N.gety()+1<numRows) {
            if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                isPath = true;
            }
        }
        if(N.getx()+1<numCols) {
            if (N.getRight() && !visited.contains(mazeElements[N.getx()+1][N.gety()])) {
                isPath = true;
            }
        }
        if(N.gety()-1>=0) {
            if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety()-1])) {
                isPath = true;
            }
        }
        return isPath;
    }

    //if no unvisited neighboring paths return true
    private boolean checkBacktrack(){
        boolean left = false;
        boolean bottom = false;
        boolean right = false;
        boolean top = false;

        if(solveStack.isEmpty()){
            return false;
        }
        if(N.getx()-1>=0) {
            if (!N.getLeft() || visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
            }
            left = true;
        }
        else{
            left = true;
        }
        if(N.gety()+1<numRows) {
            if (!N.getBottom() || visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                bottom = true;
            }
        }
        else{
            bottom = true;
        }
        if(N.getx()+1<numCols) {
            if (!N.getRight() || visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                right = true;
            }
        }
        else{
            right = true;
        }
        if(N.gety()-1>=0) {
            if (!N.getTop() || visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                top = true;
            }
        }
        else{
            top = true;
        }
        if(left && bottom && right && top){
            return true;
        }
        else{
            return false;
        }
    }

    //draws solution path based on right hand rule
    //if coming from above (L,B,R,T) (if left path, take it, if no left path look for a bottom path, if bottom path take it, etc.)
    //if coming from right (B,R,T,L)
    //if coming from below (R,T,L,B)
    //if coming from left (T, W, B, R)
    private void makeSolvePath(){
        if(solveDirections.size()>0) {
            if (solveDirections.get(solveDirections.size() - 1) == bottom) {
                if (N.getLeft() && !visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
                    A = mazeElements[N.getx() - 1][N.gety()];
                    N.setTraversedL();
                    A.setTraversedR();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(left);
                } else if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                    A = mazeElements[N.getx()][N.gety() + 1];
                    N.setTraversedB();
                    A.setTraversedT();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(bottom);
                } else if (N.getRight() && !visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                    A = mazeElements[N.getx() + 1][N.gety()];
                    N.setTraversedR();
                    A.setTraversedL();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(right);
                } else if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                    A = mazeElements[N.getx()][N.gety() - 1];
                    N.setTraversedT();
                    A.setTraversedB();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(top);
                }
            } else if (solveDirections.get(solveDirections.size() - 1) == right) {
                if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                    A = mazeElements[N.getx()][N.gety() + 1];
                    N.setTraversedB();
                    A.setTraversedT();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(bottom);
                } else if (N.getRight() && !visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                    A = mazeElements[N.getx() + 1][N.gety()];
                    N.setTraversedR();
                    A.setTraversedL();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(right);
                } else if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                    A = mazeElements[N.getx()][N.gety() - 1];
                    N.setTraversedT();
                    A.setTraversedB();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(top);
                } else if (N.getLeft() && !visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
                    A = mazeElements[N.getx() - 1][N.gety()];
                    N.setTraversedL();
                    A.setTraversedR();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(left);
                }
            } else if (solveDirections.get(solveDirections.size() - 1) == top) {
                if (N.getRight() && !visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                    A = mazeElements[N.getx() + 1][N.gety()];
                    N.setTraversedR();
                    A.setTraversedL();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(right);
                } else if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                    A = mazeElements[N.getx()][N.gety() - 1];
                    N.setTraversedT();
                    A.setTraversedB();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(top);
                } else if (N.getLeft() && !visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
                    A = mazeElements[N.getx() - 1][N.gety()];
                    N.setTraversedL();
                    A.setTraversedR();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(left);
                } else if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                    A = mazeElements[N.getx()][N.gety() + 1];
                    N.setTraversedB();
                    A.setTraversedT();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(bottom);
                }
            } else if (solveDirections.get(solveDirections.size() - 1) == left) {
                if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                    A = mazeElements[N.getx()][N.gety() - 1];
                    N.setTraversedT();
                    A.setTraversedB();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(top);
                } else if (N.getLeft() && !visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
                    A = mazeElements[N.getx() - 1][N.gety()];
                    N.setTraversedL();
                    A.setTraversedR();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(left);
                } else if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                    A = mazeElements[N.getx()][N.gety() + 1];
                    N.setTraversedB();
                    A.setTraversedT();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(bottom);
                } else if (N.getRight() && !visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                    A = mazeElements[N.getx() + 1][N.gety()];
                    N.setTraversedR();
                    A.setTraversedL();
                    N = A;
                    solveStack.push(N);
                    visited.add(N);
                    solveDirections.add(right);
                }
            }
        }
        else{
            if (N.getLeft() && !visited.contains(mazeElements[N.getx() - 1][N.gety()])) {
                A = mazeElements[N.getx() - 1][N.gety()];
                N.setTraversedL();
                A.setTraversedR();
                N = A;
                solveStack.push(N);
                visited.add(N);
                solveDirections.add(left);
            } else if (N.getBottom() && !visited.contains(mazeElements[N.getx()][N.gety() + 1])) {
                A = mazeElements[N.getx()][N.gety() + 1];
                N.setTraversedB();
                A.setTraversedT();
                N = A;
                solveStack.push(N);
                visited.add(N);
                solveDirections.add(bottom);
            } else if (N.getRight() && !visited.contains(mazeElements[N.getx() + 1][N.gety()])) {
                A = mazeElements[N.getx() + 1][N.gety()];
                N.setTraversedR();
                A.setTraversedL();
                N = A;
                solveStack.push(N);
                visited.add(N);
                solveDirections.add(right);
            } else if (N.getTop() && !visited.contains(mazeElements[N.getx()][N.gety() - 1])) {
                A = mazeElements[N.getx()][N.gety() - 1];
                N.setTraversedT();
                A.setTraversedB();
                N = A;
                solveStack.push(N);
                visited.add(N);
                solveDirections.add(top);
            }
        }
    }

    //reset all maze elements to bot be traversed or backtracked
    private void resetTraversed(){
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numCols; j++){
                mazeElements[j][i].resetTraversed();
                mazeElements[j][i].repaint();
            }
        }
    }

    //backtracks
    private void backtrack(){
        A = solveStack.pop(); //A is last element visited before N
        if(N==A){
            A=solveStack.pop();
        }

        //find direction from N need to go in order to get to A
        //take that path
        if(N.getx()-1>=0) {
            if (mazeElements[N.getx() - 1][N.gety()] == A) {
                N.setTraversedL();
                A.setTraversedR();
            }
        }
        if(N.gety()+1<numRows){
            if(mazeElements[N.getx()][N.gety()+1]==A) {
                N.setTraversedB();
                A.setTraversedT();
            }
        }
        if(N.getx()+1<numCols){
            if(mazeElements[N.getx()+1][N.gety()]==A) {
                N.setTraversedR();
                A.setTraversedL();
            }
        }
        if(N.gety()-1>=0){
            if(mazeElements[N.getx()][N.gety()-1]==A) {
                N.setTraversedT();
                A.setTraversedB();
            }
        }
        N=A;
    }

    //first step of every solve
    public void initSolveMaze() {
//        solving = true;
        resetTraversed();
        mazeSolved = false;
        numVisited = 0;
        solveStack = new Stack();
        visited = new ArrayList<>();
        N = mazeElements[0][0];
        visited.add(N);
        solveStack.push(N);
        solveDirections = new ArrayList<>();
    }

    //solve maze instantly
    public void solveTotalMaze() {
        initSolveMaze();
        while (N.getx() != endX || N.gety() != endY) {
            if (checkPath()) { //if there are neighboring unvisited paths
                makeSolvePath(); //follow right hand rule
                numVisited++;
            } else { //no neighboring unvisited paths
                if (checkBacktrack()) { //backtrack until find element with an unvisited neighbroing path
                    backtrack();
                }
                if (!solveStack.isEmpty()) { //if there aren't any elements with unvisited neighboring paths and not at end element, maze is unsolvable
                    solveStack.push(N);
                } else if (N.getx() != endX || N.gety() != endY) {
                    mazeSolved = false;
                    solvable = false;
                }
            }
            if (N.getx() == endX && N.gety() == endY) {
                mazeSolved = true;
            }
        }
    }


    //solve maze step
    public void solveMazeStep(){
        if (N.getx() != endX || N.gety() != endY) {
            if (checkPath()) {
                makeSolvePath();
                numVisited++;
            } else {
                if (checkBacktrack()) {
                    backtrack();
                }
                if (!solveStack.isEmpty()) {
                    solveStack.push(N);
                } else if (N.getx() != endX || N.gety() != endY) {
                    mazeSolved = false;
                    solvable = false;
                }
            }
        }
        else{
            mazeSolved = true;
        }
    }

}
