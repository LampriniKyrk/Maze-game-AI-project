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

public abstract class IntelligentEntity {
    private int positionX, positionY; // coordinates on maze 
    private GameItem item; 
    private boolean reachedGoal;
    private int speed;
    private GameItem usedItem;
    private int itemDuration;
    
    public IntelligentEntity(int x, int y){
        positionX = x;
        positionY = y;
        reachedGoal = false;
        usedItem = null;
        itemDuration = 0;
        
    }
    //____________________________SETTERS GETTERS______________________________________//
    public int getPositionX(){
        return positionX;
    }
    
    public int getPositionY(){
        return positionY;
    }
    
    public void setItem(GameItem item){
        this.item = item;
    }
    
    public GameItem getItem(){
        return item;
    }
    
    public void setItemToNull(){
        this.item = null;
    }
    
    public void setReachedGoal(){
        reachedGoal = true;
    }
    
    public boolean getReachedGoal(){
        return reachedGoal;
    }
    
    public int getSpeed(){
        return speed;
    }
    
    public void setSpeed(int s){
        speed = s;
    }
    
    public Coordinates getCoordinates(){
        Coordinates c = new Coordinates(this.positionX, this.positionY);
        return c;
        
    }
    
    public GameItem getUsedItem(){
        return usedItem;
    }
    
    public void setUsedItem(GameItem item){
        usedItem = new GameItem(item);
    }
    
    public void setUsedItemToNull(){
        usedItem = null;
    }
    
    public void setItemDuration(int duration){
        itemDuration = duration;
    }
    
    public int getItemDuration(){
        return itemDuration;
    }
    
    public void setCoordinates(Coordinates c){
        positionX = c.x;
        positionY = c.y;
    }
    
    //____________________________OTHER FUNCTIONS_______________________________________________//
    
    /**
     * Changes the coordinates according to the direction of the movement
     * @param direction of movement
     * @return true if the move is valid, else false
     */
    public boolean move(String direction){
        if(direction.equals("n")){ //north
            positionY = positionY -1;
            return true;
        }
        if(direction.equals("s")){ //south
            positionY = positionY +1;
            return true;
        }
        if(direction.equals("e")){ //east
            positionX = positionX+1;
            return true;
        }
        if(direction.equals("w")){ //west
            positionX = positionX-1;
            return true;
        }
        return false;
    }
    
    
    
    public void reduceSpeed(){
        speed--;
    }
    
    
    
    public void reduceItemDuration(){
        if(itemDuration>0)
            itemDuration--;
    }
    
   /**
    * enables an item's effect.
    * @return indicator to show which item was enabled and what effect it has on the entity
    */ 
    public int enableItem(){
        //initialize used item and free the item slot
        this.setUsedItem(new GameItem(this.getItem()));
        
        
        if(this.getUsedItem().getName().equals("book")){
            this.setItem(null);
            return 1 ; //indicates within range star function
        }
        if(this.getUsedItem().getName().equals("net")){
            this.setItem(null);
            //usedItem = null;
            return 2 ; //indicates within range cross function
        }
        if(this.getUsedItem().getName().equals("sport shoes")){
            this.setItem(null);
            setItemDuration(this.getUsedItem().getDuration());
            return 3; //to indicate check for duration
        }
        if(this.getUsedItem().getName().equals("running shoes")){
            this.setItem(null);
            //this.setSpeed(this.getSpeed()+2);
            setItemDuration(this.getUsedItem().getDuration());
            return 3; //to indicate check for duration
        }
        if(this.getUsedItem().getName().equals("hoodie")){
            this.setItem(null);
            setItemDuration(this.getUsedItem().getDuration());
            return 4; //become invisible 
            //enemies remember your last position
        }
        if(this.getUsedItem().getName().equals("sandwich")){
            setItemToNull();
            setUsedItemToNull();
            return 5;
        }

        return 0;
    }
    
    /**
     * Ends the item's effect on the entity.
     */
    public void endItemEffect(){
        if(usedItem.getName().equals("sport shoes")) {
            this.setSpeed(this.getSpeed() -1);
        }
        if(usedItem.getName().equals("running shoes")) {
            this.setSpeed(this.getSpeed() -2);
        }
        usedItem = null;
    }
    
}
