/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.HashMap;
import maze.gameItems.GameItem;

/**
 *
 * @author Lamprini Kyrkou
 */
public class Enemy extends IntelligentEntity{
    //not currently used, but exists for future versions of the game
    String enemyType; //three enemy types : quick : 4 speed, itemholder: first one to chose route, normal: 3 speed
    HashMap<Coordinates,Integer> visitedCells; //in order not to visit cells arleady visited multiple times and get stuck
    
    private boolean stunned; //stunned status
    
    public Enemy(int x, int y, String enemyType){
        super(x,y);
        this.enemyType = enemyType;
        visitedCells = new HashMap<>();
        
        setSpeed(1);
        stunned = false;
    }
    //copy constructor
    public Enemy(Enemy enemy){
        super(enemy.getPositionX(),enemy.getPositionY());
        enemyType = enemy.getEnemyType();
        
        setSpeed(enemy.getSpeed());
        
        if(enemy.getItem()!= null)
            this.setItem(new GameItem(enemy.getItem()));
        else
            this.setItemToNull();
        if(enemy.getUsedItem()!=null)
            this.setUsedItem(new GameItem(enemy.getUsedItem()));
        else
            this.setUsedItemToNull();
        if(enemy.getVisitedCells().size()>0)
            this.visitedCells = enemy.getVisitedCells();
        else
            this.visitedCells = new HashMap<>();
        
        stunned = enemy.getStunned();
        
        
    }
    //_______________________________SETTERS-GETTERS______________________________________________//
    public boolean getStunned(){
        return stunned;
    }
    
    public void setStunned(boolean s){
        stunned = s;
    }
    
    public HashMap<Coordinates,Integer> getVisitedCells(){
        return visitedCells;
    }
    
    public void addVisitedCell(Coordinates cellCoordinates){
        visitedCells.put(cellCoordinates, 1);     
    }

    public String getEnemyType(){
        return enemyType;
    }
    
    
    //_______________________OTHER FUNCTIONS__________________________________________________________//
     public void increaseVisitedFrequency(Coordinates cell){
        int freq = visitedCells.get(cell);
        freq++;
        visitedCells.replace(cell, freq);
    }
    /**
     * Updates the use of an item. E.g. reduces duration of an used item.  
     * @param player the human player
     * @param maze the maze grit
     * @param firstTimeItemInit indicates first time use of item
     * @return true if player was stunned/lost item, false otherwise
     */
    public boolean updateItemUses(Player player, Cell[][]maze, boolean firstTimeItemInit){
        
        GameItem item = this.getUsedItem();
        if(item==null) return false;
        
        switch(item.getName()){
            case "running shoes":
                this.setSpeed(3);
                if(firstTimeItemInit)
                    this.setItemDuration(item.getDuration());
                else
                    this.reduceItemDuration();
                if(this.getItemDuration()<=0)
                    this.setUsedItemToNull();
                break;
            case "sport shoes":
                this.setSpeed(2);
                if(firstTimeItemInit)
                    this.setItemDuration(item.getDuration());
                else
                    this.reduceItemDuration();
                if(this.getItemDuration()<=0)
                    this.setUsedItemToNull();
                
                break;
            case "hoodie":
                if(firstTimeItemInit)
                    this.setItemDuration(item.getDuration());
                else
                    this.reduceItemDuration();
                if(this.getItemDuration()<=0)
                    this.setUsedItemToNull();
                break;
            case "net":
                this.setUsedItemToNull();
                return item.withinRangeCross(this.getCoordinates(), player.getCoordinates(), maze);
            case "book":
                this.setUsedItemToNull();
                return item.withinRangeStar(this.getCoordinates(), player.getCoordinates(), maze);
            default:
                break;
        }
    return false;  
    }
    
    /**
     * 
     * @param direction of the movement (n, e, s, w )
     * @returns true if move was successful, else returns false
     */
    @Override
    public boolean move(String direction){
        int y,x;
        y = this.getPositionY();
        x = this.getPositionX();
                
        if(direction.equals("n")){ //north
            y = this.getPositionY() -1;
            this.setCoordinates(new Coordinates(x, y));
            
            //check if these coordinates are on the hashmap
            if(visitedCells.containsKey(new Coordinates(x, y)))
                increaseVisitedFrequency(new Coordinates(x, y));
            else
                visitedCells.put(new Coordinates(x, y), 1);
            
            return true;
        }
        if(direction.equals("s")){ //south
            y = this.getPositionY() +1;
            this.setCoordinates(new Coordinates(x, y));
            
            //check if these coordinates are on the hashmap
            if(visitedCells.containsKey(new Coordinates(x, y)))
                increaseVisitedFrequency(new Coordinates(x, y));
            else
                visitedCells.put(new Coordinates(x, y), 1);
            
            return true;
        }
        if(direction.equals("e")){ //east
            x = this.getPositionX()+1;
            this.setCoordinates(new Coordinates(x, y));
            
            //check if these coordinates are on the hashmap
            if(visitedCells.containsKey(new Coordinates(x, y)))
                increaseVisitedFrequency(new Coordinates(x, y));
            else
                visitedCells.put(new Coordinates(x, y), 1);
            
            return true;
        }
        if(direction.equals("w")){ //west
            x = this.getPositionX()-1;
            this.setCoordinates(new Coordinates(x, y));
            
            //check if these coordinates are on the hashmap
            if(visitedCells.containsKey(new Coordinates(x, y)))
                increaseVisitedFrequency(new Coordinates(x, y));
            else
                visitedCells.put(new Coordinates(x, y), 1);
            
            return true;
        }
        return false;
    }
}
