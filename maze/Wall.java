/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Wall {
    
    private int verticalCoordinate;
    private int horizontalCoordinate;
    
    private boolean north, east, south, west;
    
    public Wall(int x, int y, boolean north, boolean east, boolean south, boolean west){
        
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        
        verticalCoordinate = y;
        horizontalCoordinate = x;           
    }
    
    public boolean isWallNorth(){
        return north;
    }
    
    public boolean isWallEast(){
        return east;
    }
    
    public boolean isWallSouth(){
        return south;
    }
    
    public boolean isWallWest(){
        return west;
    }
    
    public int getVerticalCoordinate(){
        return verticalCoordinate;
    }
    
    public int getHorizontalCoordinate(){
        return horizontalCoordinate;
    }
    
    /**
     * 
     * @return a String that represents the direction of the wall.
     * n : north, e: east, s: south, w: west.
     */
    public String getDirection(){
        if (north){
            return "n";
        }
        else if (east){
            return "e";
        }
        else if(south){
            return "s";
        }
        else if (west){
            return "w";
        }
        return null;
    }
    
    

}
