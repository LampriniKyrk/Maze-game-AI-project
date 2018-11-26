/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Kyrkou Lamprini
 */
public class MazeGenerator {
    private int verticalSize; 
    private int horizontalSize;
    private Cell [][]maze;
    private int beginningX, beginningY;
    
    //constructor
    public MazeGenerator(int x, int y, int beginX, int beginY){
        verticalSize = y;
        horizontalSize = x;
        beginningX = beginX; //offset x
        beginningY = beginY; //offset y
        //initialize maze (every cell has walls in each side , 
        //no players, no enemies and not part of the path
        maze = new Cell[y][x];
        
        int yCoordinate = beginningY;
        int xCoordinate = beginningX;
        for (int i = 0; i < y; i++) {
            xCoordinate = beginningX;
            for (int j = 0; j < x; j++) {
                maze[i][j] = new Cell(j,i); 
                xCoordinate = xCoordinate + 32;
            }
            yCoordinate = yCoordinate + 32; 
            
        }
    }
    
    public Cell[][] getMaze(){
        return maze;
    }
    
    public int getVerticalSize(){
        return verticalSize;
    }
    
    public int getHorizontalSize(){
        return horizontalSize;
    }
    //prim algorithm
    public void generateMaze(){
        ArrayList<Cell> path = new ArrayList<>();
        ArrayList<Wall> wallList = new ArrayList<>();
        //add the fist cell in the pathlist
        path.add(maze[2][0]); //paradoxh
        wallList.add(new Wall(0, 2, true, false, false, false));  //north 
        wallList.add(new Wall(0, 2, false, true, false, false));  //east
        wallList.add(new Wall(0, 2, false, false, true, false)); //south
        wallList.add(new Wall(0, 2, false, false, false, true)); //west
        //while the path of the maze to be generated is smaller than the number of all cells
        while(path.size()<(verticalSize*horizontalSize)){ 
            Random rng = new Random();
            int choice = rng.nextInt(wallList.size());
            int x,y;
            
            //pick random wall from list
            Wall wall = wallList.get(choice);
            String direction = wall.getDirection();
            x = wall.getHorizontalCoordinate();
            y = wall.getVerticalCoordinate();
            if (direction.equals("n")&& y>0) { //north               
                if (!path.contains(maze[y-1][x]) ) { // if the cell is not already part of the path
                    maze[y][x].demolishNorthWall();
                    maze[y-1][x].demolishSouthWall();
                    wallList.remove(wall);
                    
                    //add the new walls
                    wallList.add(new Wall(x, y-1, true, false, false, false));  //north 
                    wallList.add(new Wall(x, y-1, false, true, false, false));  //east
                    wallList.add(new Wall(x, y-1, false, false, false, true)); //west
                    //add the new cell on path
                    path.add(maze[y-1][x]);    
                }
            }
            else if (direction.equals("e")&& x<horizontalSize-1){ //east
                if (!path.contains(maze[y][x+1])) { // if the cell is not already part of the path
                    maze[y][x].demolishEastWall();
                    maze[y][x+1].demolishWestWall();
                    wallList.remove(wall);
                    //add the new walls
                    wallList.add(new Wall(x+1, y, true, false, false, false));  //north 
                    wallList.add(new Wall(x+1, y, false, true, false, false));  //east
                    wallList.add(new Wall(x+1, y, false, false, true, false)); //south
                    //add the new cell on path
                    path.add(maze[y][x+1]);    
                }
            }
            else if (direction.equals("s")&& y <verticalSize-1){ //south
                if (!path.contains(maze[y+1][x]) ) { // if the cell is not already part of the path
                    maze[y][x].demolishSouthWall();
                    maze[y+1][x].demolishNorthWall();
                    wallList.remove(wall);
                    //add the new walls
                    wallList.add(new Wall(x, y+1, false, true, false, false));  //east
                    wallList.add(new Wall(x, y+1, false, false, true, false)); //south
                    wallList.add(new Wall(x, y+1, false, false, false, true)); //west
                    //add the new cell on path
                    path.add(maze[y+1][x]);    
                }
            }
            else if (direction.equals("w") && x > 0){ //west
                if (!path.contains(maze[y][x-1])) { // if the cell is not already part of the path
                    maze[y][x].demolishWestWall();
                    maze[y][x-1].demolishEastWall();
                    wallList.remove(wall);
                    //add the new walls
                    wallList.add(new Wall(x-1, y, true, false, false, false));  //north 
                    wallList.add(new Wall(x-1, y, false, false, true, false)); //south
                    wallList.add(new Wall(x-1, y, false, false, false, true)); //west
                    //add the new cell on path
                    path.add(maze[y][x-1]);    
                }
            }
        }
        System.out.println("MAZE BUILD");
        //remove random walls [column 1-18 , row 1-8]
        Random rand = new Random();
        ArrayList<Wall> deleted_walls_list = new  ArrayList<>();
        for (int i = 0; i < 25; i++) { //removes up to 25 random walls
            int randX= rand.nextInt(18)+1;
            int randY= rand.nextInt(8) +1;
            
            String walls = maze[randY][randX].getwalls();
            String []w = walls.split(" ");
            //get random wall
            int r = rand.nextInt(w.length);
            boolean deletion = false;
            //do while deletion of a wall was not successfull
            if(!w[r].equals("")){ //if there is a wall to delete on current cell do : 
                while (!deletion){
                    if(w[r].equals("north")){
                        Wall wall = new Wall(randX, randY, true, false, false, false);
                        if (!deleted_walls_list.contains(wall)){
                            //demolish the acording walls
                            maze[randY][randX].demolishNorthWall();
                            maze[randY-1][randX].demolishSouthWall();
                            deleted_walls_list.add(wall);
                            wallList.remove(wall);
                            //deletion was successfull
                            deletion = true;
                        }   
                    }
                    if(w[r].equals("east")){
                        Wall wall = new Wall(randX, randY, false, true, false, false);
                        if (!deleted_walls_list.contains(wall)){
                            //demolish the acording walls
                            maze[randY][randX].demolishEastWall();
                            maze[randY][randX+1].demolishWestWall();
                            deleted_walls_list.add(wall);
                            wallList.remove(wall);
                            //deletion was successfull
                            deletion = true;
                        }   
                    }
                    if(w[r].equals("south")){
                        Wall wall = new Wall(randX, randY, false, false, true, false);
                        if (!deleted_walls_list.contains(wall)){
                            //demolish the acording walls
                            maze[randY][randX].demolishSouthWall();
                            maze[randY+1][randX].demolishNorthWall();
                            deleted_walls_list.add(wall);
                            wallList.remove(wall);
                            //deletion was successfull
                            deletion = true;
                        }   
                    }
                    if(w[r].equals("west")){
                        Wall wall = new Wall(randX, randY, false, false, false, true);
                        if (!deleted_walls_list.contains(wall)){
                            //demolish the acording walls
                            maze[randY][randX].demolishWestWall();
                            maze[randY][randX-1].demolishEastWall();
                            deleted_walls_list.add(wall);
                            wallList.remove(wall);
                            //deletion was successfull
                            deletion = true;
                        }   
                    }
                }
            }
        }
        System.out.println("DONE DELETING WALLS");
        
    }
    
    public void printMaze(){
        
        for (int i = 0; i < verticalSize; i++) {
            for (int j = 0; j < horizontalSize; j++) {
                System.out.print(maze[i][j].getwalls());   
            }
            System.out.println("");  
        }
        
    }
        
    
}
