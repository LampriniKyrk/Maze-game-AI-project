/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import maze.gameItems.GameItem;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Player extends IntelligentEntity{
    
    private boolean chanceToEscape;
    private boolean stunned;
    
    /**
     * Constructor
     * @param x coordinate
     * @param y coordinate
     */
    public Player(int x, int y){      
        super(x,y);
        setSpeed(1);
        chanceToEscape = false;
    }
    
    /**
     * Copy constructor
     * @param player
     */
    public Player(Player player){
        
        super(player.getPositionX(),player.getPositionY());
        this.setSpeed(player.getSpeed());
        
        if(player.getUsedItem()!=null)
            this.setUsedItem(new GameItem(player.getUsedItem()));
        else
            this.setUsedItemToNull();
        
        if(player.getItem()!=null)
            this.setItem(new GameItem(player.getItem()));
        else
            this.setItemToNull();
        
        this.chanceToEscape = player.chanceToEscape();
        this.setItemDuration(player.getItemDuration());

        this.stunned = player.stunned();

    }
    /**
     * 
     * @return true if the player has the stunned effect active, else it returns false
     */
    public boolean stunned(){
        return stunned;
    }
    
    public void setStunned(boolean st){
        stunned = st;
    }
    
    /**
     * Function sends a recognition to indicate if the player is able to use the item he's holding. 
     * 
     * @returns 0 = nothing happened | 1 = use withinRangeStar() | 2 = use withinRangeCross
     * 3 = check duration of item | 4 = make player invisible/last known position for enemy pathfinding
     */
    @Override
    public int enableItem(){
        //initialize used item and free the item slot
        this.setUsedItem(new GameItem(this.getItem()));
        
        if(this.getUsedItem().getName().equals("book")){
            this.setItemToNull(); 
            return 1 ; //indicates within range star function
        }
        if(this.getUsedItem().getName().equals("net")){
            this.setItemToNull(); 
            return 2 ; //indicates within range cross function
        }
        if(this.getUsedItem().getName().equals("sport shoes")){
            this.setItemToNull();
            setItemDuration(this.getUsedItem().getDuration());
            return 3;
            
        }
        if(this.getUsedItem().getName().equals("running shoes")){
            this.setItemToNull();
            setItemDuration(this.getUsedItem().getDuration());
            return 3; //to indicate check for duration
        }
        if(this.getUsedItem().getName().equals("hoodie")){
            this.setItemToNull();
            setItemDuration(this.getUsedItem().getDuration());
            return 4; //become invisible 
            //enemies remember your last position
        }
        if(this.getUsedItem().getName().equals("sandwich")){
            this.setItemToNull();
            setItemDuration(this.getUsedItem().getDuration());
            return 5;
        }

        return 0;
    }
    /**
     * Ends the effect of the item that was in use.
     */
    @Override
    public void endItemEffect(){
        if(this.getUsedItem().getName().equals("sport shoes")) {
            this.setSpeed(this.getSpeed() -1);
        }
        if(this.getUsedItem().getName().equals("running shoes")) {
            this.setSpeed(this.getSpeed() -2);
        }
        if(this.getUsedItem().getName().equals("sandwich")){
            this.chanceToEscape = false;
        }
        this.setUsedItemToNull();
    }

    public void setChanceToEscape(boolean chance){
        chanceToEscape = chance;
    }
    
    public boolean chanceToEscape(){
        return chanceToEscape;
    }
    
 
}
