/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Search {
    private Cell[][]maze;
    
    public Search(Cell[][]maze){
        this.maze = maze;
    }
    
    /**
     * 
     * @param cell the cell for which the findNeighbours function is called to fine=d its neighbouring cells.
     * @return an ArrayList that contains the neighbouring cells
     */
    private ArrayList<Cell> findNeighbours(Cell cell){
        String rawwalls = cell.getwalls();
        String[]walls = rawwalls.split(" ");
        
        String []legitimateNeighbours={"north","east","south","west"};
        ArrayList<Cell> neighbours = new ArrayList<>();
        
        for (String wall : walls) {
            if(wall.equals("north")) legitimateNeighbours[0]="";
            if(wall.equals("east")) legitimateNeighbours[1]="";
            if(wall.equals("south")) legitimateNeighbours[2]="";
            if(wall.equals("west")) legitimateNeighbours[3]="";
        }
        
        // for every neighbour
        for (String ln : legitimateNeighbours) {
            if(!ln.equals("")){
                if(ln.equals("north")){
                    neighbours.add(new Cell(maze[cell.gety()-1][cell.getx()]));
                }
                if(ln.equals("east")){
                    neighbours.add(new Cell(maze[cell.gety()][cell.getx()+1]));
                }
                if(ln.equals("south")){
                    neighbours.add(new Cell(maze[cell.gety()+1][cell.getx()]));
                }
                if(ln.equals("west")){
                    neighbours.add(new Cell(maze[cell.gety()][cell.getx()-1]));
                }
                
            }
        }
        
        return neighbours; 
    }
    
    /**
     * Breadth First Search function.
     * @param startingPosition of the search
     * @return an ArrayList that contains the Path that the function produces.
     */
    public ArrayList<Path> bfs(Cell startingPosition){
        //a list of paths
        ArrayList<Path> pathsList = new ArrayList<>(); 
        pathsList.add(new Path(startingPosition));
        
        Queue<Path> queue;
        queue = new LinkedList<>();
        ArrayList<Cell> discovered = new ArrayList<>();
        
        queue.add(new Path(startingPosition));
        Path v;
        discovered.add(startingPosition);
        
        while(!queue.isEmpty()){
            
                v = queue.poll();

                ArrayList<Cell>neighbours = findNeighbours(v.getLastCell());

                for (Cell neighbour : neighbours) {
                    if (!discovered.contains(neighbour)){
                        
                        Path temp = new Path(v);
                        temp.addCellToPath(new Cell(neighbour));
                        queue.add(temp);
                        
                        pathsList.add(temp);
                        discovered.add(new Cell(neighbour));
                    }
                   
                }
            }
            
        return pathsList;
    }
    
    /**
     * Depth First Search Function.
     * @param startingPosition of the search
     * @return an ArrayList that contains the paths that are produced by the function.
     */
    private ArrayList<Path> dfs(Cell startingPosition){
        //a list of paths
        ArrayList<Path> pathsList = new ArrayList<>(); 
        pathsList.add(new Path(startingPosition));
        
        Stack<Path> stack = new Stack<>();
        ArrayList<Cell> discovered = new ArrayList<>();
        
        stack.push(new Path(startingPosition));
        Path v;
        discovered.add(startingPosition);
        
        while(!stack.isEmpty()){
            
            v = stack.pop();

            ArrayList<Cell>neighbours = findNeighbours(v.getLastCell());
            //changed so it adds randomixed the neighbors
            Collections.shuffle(neighbours);
            for (Cell neighbour : neighbours) {
                if (!discovered.contains(neighbour)){
                    //System.out.println("neighbour cordintes "+neighbour.gety() +"  "+neighbour.getx());
                    Path temp = new Path(v);
                    temp.addCellToPath(new Cell(neighbour));

                    stack.push(temp);

                    pathsList.add(temp);
                    discovered.add(new Cell(neighbour));
                }

            }
        }
        return pathsList;
    }

    
    
   /**
    * Function finds the Paths that contain the cell that the player is currently on.
    * @param playerPosition the cell that the player is on
    * @param endingPositions position of end tile
    * @return the paths of the player.
    */
    public ArrayList<Path> getPathsOfPlayer(Cell playerPosition, ArrayList<Cell> endingPositions){
        ArrayList<Path> allPathsOfPlayer = bfs(playerPosition); //get player's paths
        ArrayList<Path> pathsOfPlayer = new ArrayList<>();
        //for all the paths you found through bfs
        for (Path path : allPathsOfPlayer) {
            for (Cell endPos : endingPositions) {
                if(path.containsCell(endPos)){
                    //cut the path to where it should end (at end tile ) 
                    Path tmp = path.cutPath(endPos.getx(), endPos.gety());
                    pathsOfPlayer.add(new Path(tmp));
                }
            }
        }
        return pathsOfPlayer;
    }
    
    /**
     * Sorts paths according to length
     * @param paths to be sorted
     * @return ArrayList with the paths sorted from shortest to longest.
     */
    public ArrayList<Path> sortPaths(ArrayList<Path> paths){
        ArrayList<Path>sortedPaths = new ArrayList<>();
        paths.sort(new Comparator<Path>() {

            @Override
            public int compare(Path o1, Path o2) {
                return o1.getPathLength() - o2.getPathLength();
            }
        });
        
        return sortedPaths = new ArrayList<>(paths);
    }
    
    /**
     * Finds the paths of the agent towards the player
     * @param mainPlayer the player
     * @param enemyPos the position of the agent
     * @return the path towards the player
     */
    public Path findPathToPlayer(Player mainPlayer,Cell enemyPos){
        ArrayList<Path> allPaths = bfs(enemyPos);
        ArrayList<Path> pathsToPlayer = new ArrayList<>();
        //for every path found 
        for (Path path : allPaths) {
            //if path ends on the cell the player is on
            if(path.playerOnPath(mainPlayer.getPositionX(), mainPlayer.getPositionY())>=0){
                Path tmp = new Path(path.cutPath(mainPlayer.getPositionX(), mainPlayer.getPositionY()));
                if(!pathsToPlayer.contains(tmp))pathsToPlayer.add(tmp);
            }
        } 

        return sortPaths(pathsToPlayer).get(0);
    }
     
    
}
