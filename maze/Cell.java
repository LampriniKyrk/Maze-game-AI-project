/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.io.Serializable;
import maze.gameItems.GameItem;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Cell implements Serializable{
    private GameItem item; //the item that may be contained on the cell
    //walls
    private boolean northWall;
    private boolean eastWall;
    private boolean southWall;
    private boolean westWall;
    // cell part of maze's path or not
    private boolean partOfPath;
    //is an enemy on the cell 
    private boolean enemy;
    //is the player on the cell
    private boolean player;
    // is the cell an end tile?
    private boolean endTile;
    //cell's coordinates on the grid
    private int xCoordinate, yCoordinate;
    
    public Cell(int x, int y){
        northWall = true;
        eastWall = true;
        southWall = true;
        westWall = true;
        
        partOfPath = false;
        enemy = false;
        player = false;
        
        xCoordinate = x;
        yCoordinate = y;
        
        item = null;
    }
    //Copy constructor
    public Cell(Cell c){
        this.northWall = c.getNorthWall();
        this.eastWall = c.getEastWall();
        this.southWall = c.getSouthWall();
        this.westWall = c.getWestWall();
        
        this.enemy = c.isEnemyOnCell();
        this.player = c.isPlayerOnCell();
        
        this.xCoordinate = c.getx();
        this.yCoordinate = c.gety();
        
        if(c.getItem()!=null){
            this.item = new GameItem(c.getItem());
        }
        else{
            item = null;    
        }
        
    }
    
    public Cell (){
        
    }
    
    public boolean isPartOfPath(){
        return partOfPath;
    }
    
    public void demolishNorthWall(){
        northWall = false;
    }
    
    public void demolishEastWall(){
        eastWall = false;
    }
    
    public void demolishSouthWall(){
        southWall = false;
    }
    
    public void demolishWestWall(){
        westWall = false;
    }
    
    public boolean isPlayerOnCell(){
        return player;
    }
    
    public boolean isEnemyOnCell(){
        return enemy;
    }
    
    /**
     * 
     * @returns a String with all the containing walls of the cell. E.G. "north east west"  
     */
    public String getwalls(){
        String tmp = "";
        if (northWall) {
            tmp = tmp + "north ";
        }
        if (eastWall) {
            tmp = tmp + "east ";
        }
        if (southWall) {
            tmp = tmp + "south ";
        }
        if (westWall) {
            tmp = tmp + "west ";
        }
        return tmp;
    }
    
    public int getx(){
        return xCoordinate;
    }
    public int gety(){
        return yCoordinate;
    }
    
    public boolean getNorthWall(){
        return northWall;
    }
    public boolean getEastWall(){
        return eastWall;
    }
    public boolean getSouthWall(){
        return southWall;
    }
    public boolean getWestWall(){
        return westWall;
    }
    
    public void setPlayerOnCell(boolean p){
        player = p;
    }
    public void setEnemyOnCell(boolean en){
        enemy = en;
    }
    
    /**
     * 
     * @param otherObject
     * @return true if cell1 = cell2 else false
     */
    @Override
    public boolean equals(Object otherObject){
        Cell cell = (Cell)otherObject;
        return xCoordinate == cell.getx() && yCoordinate==cell.gety();
    }
    
    public void setItem(GameItem item){
        this.item = new GameItem(item);
    }
    
    public void setItemToNull(){
        this.item = null;
    }
    
    public GameItem getItem(){
        return item;
    }
    
    public boolean isEndTile(){
        return endTile;
    }
    
    public void setEndTile(boolean et){
        endTile = et;
    }
    
}
