package com.company;

import java.awt.*;
import javax.swing.*;

public class MazeElement extends JPanel {
    private final int mazeSize = 500;
    private int x; //x position of the maze element in the grid of maze elements that make up the maze
    private int y; //y position of the maze element
    private int panelSize; //height and width of each individual maze element (since they will be square)
    private int pathSize; //size of the maze/solution path inside the maze element
    private int halfPathSize; //used for making sure path rectangles all overlap
    private int endX; //end x position of the maze
    private int endY; //end y position of the maze

    //set true if there is a right path from this maze element to a neighboring element
    //..same for other directions
    private boolean right;
    private boolean left;
    private boolean bottom;
    private boolean top;
    private boolean visited;

    //set true if the solve path traverses the right path of the maze element
    //...same for other directions
    private boolean traversedR;
    private boolean traversedL;
    private boolean traversedT;
    private boolean traversedB;

    //set true if solve path traverses the right path of the maze element twice (aka backtracked over the maze element)
    //... same for other directions
    private boolean backtrackedR;
    private boolean backtrackedL;
    private boolean backtrackedT;
    private boolean backtrackedB;

    //initialize each maze element with a size(the height and width of the element), and its ending element's x and y pos
    public MazeElement(int size, int endX, int endY){
        this.setBackground(Color.BLACK); //anywhere that is not a path will be black (walls)
        this.endX=endX;
        this.endY=endY;

        //adjust the panel size to the size of the maze (aka more rows/columns -> smaller panels)
        panelSize = mazeSize/size+1; //+1 to make up for the division of an int
        halfPathSize = panelSize/4+1;
        pathSize = halfPathSize*2;

        this.setPreferredSize(new Dimension(panelSize, panelSize));

        //initialize all maze elements to have not path, no traversal, and not backtracking
        right=false;
        left=false;
        bottom=false;
        top=false;
        traversedR=false;
        traversedL=false;
        traversedT=false;
        traversedB=false;
        backtrackedR = false;
        backtrackedL = false;
        backtrackedT = false;
        backtrackedB = false;

    }

    //whenever set a path to a neighbor, repaint to draw that path
    public void setRightNeighbor(){
        right = true;
        repaint();
    }

    public void setLeftNeighbor(){
        left=true;
        repaint();
    }

    public void setTopNeighbor(){
        top=true;
        repaint();
    }

    public void setBottomNeighbor(){
        bottom=true;
        repaint();
    }

    //paints maze path and solution path
    public void paintComponent(Graphics g){
        super.paintComponent(g);
//        g.setColor(Color.MAGENTA);
//        g.drawRect(0,0, panelSize, panelSize);
        g.setColor(Color.GRAY);

        //draw rectangles to make paths (make sure order you draw rectangles is correct and no unnecessary overlapping occurs)
        //draw gray rectangle for path, blue for solution, red for backtracked
        if(right) {
            if(traversedR){
                g.setColor(Color.BLUE);
            }
            if(backtrackedR){
                g.setColor(Color.RED);
            }
            if(!traversedR && !backtrackedR){
                g.setColor(Color.GRAY);
            }
            g.fillRect(pathSize-halfPathSize, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
        }
        if(left){
            if(traversedL){
                g.setColor(Color.BLUE);
            }
            if(backtrackedL){
                g.setColor(Color.RED);
            }
            if(!traversedL && !backtrackedL){
                g.setColor(Color.GRAY);
            }
            g.fillRect(0, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
            if(traversedR | backtrackedR && !backtrackedL){
                if(traversedR){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedR){
                    g.setColor(Color.RED);
                }
                g.fillRect(pathSize-halfPathSize, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);

            }
        }
        if(top) {
            if(traversedT){
                g.setColor(Color.BLUE);
            }
            if(backtrackedT){
                g.setColor(Color.RED);
            }
            if(!traversedT && !backtrackedT){
                g.setColor(Color.GRAY);
            }
            g.fillRect(pathSize-halfPathSize, 0, pathSize, pathSize+halfPathSize);
            if((traversedL || backtrackedL) && !backtrackedT){
                if(traversedL){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedL){
                    g.setColor(Color.RED);
                }
                g.fillRect(0, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
            }
            if((traversedR || backtrackedR) && !backtrackedT){
                if(traversedR){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedR){
                    g.setColor(Color.RED);
                }
                g.fillRect(pathSize-halfPathSize, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
            }
        }
        if(bottom) {
            if(traversedB){
                g.setColor(Color.BLUE);
            }
            if(backtrackedB){
                g.setColor(Color.RED);
            }
            if(!traversedB && !backtrackedB){
                g.setColor(Color.GRAY);
            }
            g.fillRect(pathSize - halfPathSize, pathSize-halfPathSize, pathSize, pathSize + halfPathSize);
            if((traversedT || backtrackedT) && !backtrackedB){
                if(traversedT){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedT){
                    g.setColor(Color.RED);
                }
                g.fillRect(pathSize-halfPathSize, 0, pathSize, pathSize+halfPathSize);
            }
            if((traversedL || backtrackedL) && !backtrackedB && !backtrackedT){
                if(traversedL){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedL){
                    g.setColor(Color.RED);
                }
                g.fillRect(0, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
            }
            if((traversedR || backtrackedR) && !backtrackedB && !backtrackedT){
                if(traversedR){
                    g.setColor(Color.BLUE);
                }
                if(backtrackedR){
                    g.setColor(Color.RED);
                }
                g.fillRect(pathSize-halfPathSize, pathSize-halfPathSize, pathSize+halfPathSize, pathSize);
            }
        }

        //make beginning and end white
        if(x==0 && y==0 || x==endX && y==endY){
            g.setColor(Color.WHITE);
            g.fillRect(halfPathSize,halfPathSize,pathSize,pathSize);
        }
    }

    //GETTERS AND SETTERS
    public void setVisited(boolean isVisited){
        visited=isVisited;
    }

    public boolean getVisited(){
        return visited;
    }

    public int getx(){
        return x;
    }

    public int gety(){
        return y;
    }

    public void setXY(int X, int Y){
        x=X;
        y=Y;
    }

    public void setTraversedR(){
        if(traversedR){
            backtrackedR = true;
        }
        else{
            traversedR=true;
        }
        repaint();
    }
    public void setTraversedL(){
        if(traversedL){
            backtrackedL = true;
        }
        else{
            traversedL=true;
        }
        repaint();
    }
    public void setTraversedT(){
        if(traversedT){
            backtrackedT = true;
        }
        else{
            traversedT=true;
        }
        repaint();
    }
    public void setTraversedB(){
        if(traversedB){
            backtrackedB = true;
        }
        else{
            traversedB=true;
        }
        repaint();
    }

    public boolean getRight(){
        return right;
    }
    public boolean getLeft(){
        return left;
    }
    public boolean getTop(){
        return top;
    }
    public boolean getBottom(){
        return bottom;
    }

    public void resetTraversed(){
        traversedR=false;
        traversedL=false;
        traversedT=false;
        traversedB=false;
        backtrackedR = false;
        backtrackedL = false;
        backtrackedT = false;
        backtrackedB = false;
    }


}