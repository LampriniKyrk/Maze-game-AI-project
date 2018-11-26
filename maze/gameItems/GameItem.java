/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze.gameItems;

import java.io.Serializable;
import static java.lang.Math.abs;
import java.util.Random;
import maze.Cell;
import maze.Coordinates;
import maze.Enemy;
import maze.Player;

/**
 *
 * @author Kyrkou Lamprini
 */
public class GameItem implements Serializable{
    private int baseScore; //score to be part of the usability function for AI agents
    private int playerScore; //..indicates how valuable the item is for the player
    private String name;
    private String effectDescription;
    private int duration;
    private int range;
    
    private static int idCounter=0;
    private int id;
    
    public GameItem(int score,int playerScore, String name,int duration, int range){
        idCounter++;
        id = idCounter;
        this.playerScore = playerScore;
        baseScore = score;
        this.name = name;
        this.duration = duration;
        this.range = range;
    }
    //copy constructor
    public GameItem(GameItem i){
        id = i.getId();
        playerScore = i.getPlayerScore();
        baseScore = i.getScore();
        name = i.getName();
        duration = i.getDuration();
        range = i.getRange();
    }
    
    public int getId(){
        return id;
    }
    
    /**
     * Checks if a throw is valid in star shaped range
     * @param playerPosition position of the player that is throwing the item
     * @param landingPosition the position of the landing of the item
     * @param maze the labyrinth
     * @return true: throw is possible
     * false: throw impossible due to obstacles
     */
    public boolean withinRangeStar(Coordinates playerPosition, Coordinates landingPosition, Cell[][]maze){
        //check cross just like withinrangecross
        boolean validThrow = withinRangeCross(playerPosition, landingPosition, maze);
        if (validThrow) return true;
        
        //now check the diagonal positions from the center {playerPosition}
        for (int i = 1; i <= range; i++) {
           
           //check the left top side-if the field of view is not obstructed by walls
            //if not out of grid do : 
           if(playerPosition.y >=i && playerPosition.x>=i){
                if(!maze[playerPosition.y][playerPosition.x].getWestWall() && 
                        !maze[playerPosition.y][playerPosition.x].getNorthWall()){
                    //if the destined position is not obstructed
                    if(!maze[playerPosition.y-i][playerPosition.x-i].getEastWall() && 
                        !maze[playerPosition.y-i][playerPosition.x-i].getSouthWall()){
                        //if the opponent is in this position
                        if(playerPosition.y-i==landingPosition.y && playerPosition.x-i == landingPosition.x ){
                            return true;
                        }
                    }
                    
                }   
           }
           if (playerPosition.y >=i && playerPosition.x<=19-i){ 
               if(!maze[playerPosition.y][playerPosition.x].getEastWall()&& 
                        !maze[playerPosition.y][playerPosition.x].getNorthWall()){
                    //if the destined position is not obstructed by walls on the destinesd cell
                    if(!maze[playerPosition.y-i][playerPosition.x+i].getWestWall()&& 
                        !maze[playerPosition.y-i][playerPosition.x+i].getSouthWall()){
                        //if the opponent is in this position
                        if(playerPosition.y-i==landingPosition.y && playerPosition.x+i == landingPosition.x ){
                            return true;
                        }
                    }
                    
                }
           }
           //for south west corner 
           if(playerPosition.y<=9-i && playerPosition.x >=i ){
               if(!maze[playerPosition.y][playerPosition.x].getEastWall()&& 
                        !maze[playerPosition.y][playerPosition.x].getNorthWall()){
                    //if the destined position is not obstructed by walls on the destinesd cell
                    if(!maze[playerPosition.y+i][playerPosition.x-i].getWestWall()&& 
                        !maze[playerPosition.y+i][playerPosition.x-i].getSouthWall()){
                        //if the opponent is in this position
                        if(playerPosition.y+i==landingPosition.y && playerPosition.x-i == landingPosition.x ){
                            return true;
                        }
                    }
                    
                }
           }
           //for the south east corner check
           if(playerPosition.y<=9-i && playerPosition.x <=19-i ){
               if(!maze[playerPosition.y][playerPosition.x].getEastWall()&& 
                        !maze[playerPosition.y][playerPosition.x].getNorthWall()){
                    //if the destined position is not obstructed by walls on the destinesd cell
                    if(!maze[playerPosition.y+i][playerPosition.x+i].getWestWall()&& 
                        !maze[playerPosition.y+i][playerPosition.x+i].getSouthWall()){
                        //if the opponent is in this position
                        if(playerPosition.y+i==landingPosition.y && playerPosition.x+i == landingPosition.x ){
                            return true;
                        }
                    }
                    
                }
           }   
        }
        
        return false;
    }
    
    public boolean escaped(){
        if(name.equals("sandwich")){
            Random random = new Random();
            int num = random.nextInt(100)+1; //from 1 to 100
            return num >= 49; //50% chance  
        }
        else{
            System.out.println("WRONG METHOD");
            return false;
        }
    }
    //************//
    //checks if throwing is within range
    public boolean withinRangeCross(Coordinates playerPosition, Coordinates landingPosition, Cell[][]maze){
        //for  illegal throws on the north or south
        if(playerPosition.x == landingPosition.x && (abs(playerPosition.y - landingPosition.y))>range){ 
            return false;
        }
        //for  illegal throws to the west or east
        if(playerPosition.y == landingPosition.y && (abs(playerPosition.x - landingPosition.x))>range){ 
            return false;
        }
        //check if path obstructed between player and landing position SOUTH
        if(playerPosition.y < landingPosition.y){
            for (int i = playerPosition.y; i < landingPosition.y; i++) {
                if(maze[i][playerPosition.x].getNorthWall() || maze[i][playerPosition.x].getSouthWall()){
                    return false;
                }
            } 
        }
        //check if path obstructed between player and landing position NORTH
        if(playerPosition.y > landingPosition.y){
            for (int i = playerPosition.y; i > landingPosition.y; i--) {
                if(maze[i][playerPosition.x].getNorthWall() || maze[i][playerPosition.x].getSouthWall()){
                    return false;
                }
            } 
        }
        //check if path obstructed between player and landing position EAST
        if(playerPosition.x < landingPosition.x){
            for (int i = playerPosition.x; i < landingPosition.x; i++) {
                if(maze[playerPosition.y][i].getEastWall()|| maze[playerPosition.y][i].getWestWall()){
                    return false;
                }
            } 
        }
        //check if path obstructed between player and landing position WEST
        if(playerPosition.x > landingPosition.x){
            for (int i = playerPosition.x; i > landingPosition.x; i--) {
                if(maze[playerPosition.y][i].getEastWall()|| maze[playerPosition.y][i].getWestWall()){
                    return false;
                }
            } 
        }
        
        return true;
    }
    
    public void setScore(int score){
        baseScore = score;
    }
    
    public int getScore(){
        return baseScore;
    }
    
    public void setName(String n){
        name = n;
    }
    
    public String getName(){
        return name;
    }
    
    private void setDescripition(String des){
        effectDescription = des;
    }
    
    private String getDescription(){
        return effectDescription;
    }
    
    public void setDuration(int duration){
        this.duration = duration;
    }
    
    public int getDuration(){
        return duration;
    }
    
    public void setRange(int r){
        range = r;
    }
    
    public int getRange(){
        return range;
    }
    
    public int getPlayerScore(){
        return playerScore;
    }
    /**
     * checks if the item can be used.
     * @param enemy
     * @param player
     * @param enemyUsesItem
     * @param maze
     * @return true : can be used, else false
     */
    public boolean canBeUsed(Enemy enemy, Player player, boolean enemyUsesItem, Cell[][] maze){
        //depending on the item type
        switch(name){
            case "book":
                if(enemyUsesItem)
                    return withinRangeStar(enemy.getCoordinates(), player.getCoordinates(), maze); //implemented might not work correctly
                else
                    return withinRangeStar(player.getCoordinates(), enemy.getCoordinates(), maze);
            case "net":
                if(enemyUsesItem){ //if the agent is using the item
                    return withinRangeCross(enemy.getCoordinates(), player.getCoordinates(), maze);
                }
                else{
                    return withinRangeCross(player.getCoordinates(), enemy.getCoordinates(), maze);
                }
            case "sport shoes":
                return true;
                //break;
            case "running shoes":
                return true;
            case "hoodie":
                return true;
            case "sandwich":
                return true;
            default:
                break;
        }
        return false;
        
    }
    /** 
     * 
     * @param turn 0 for agent , 1 for player
     * @return true- the item worked, false- the item didn't work
     */
    public boolean succeeded(int turn){
        Random rand = new Random(); 
        int result;
        switch(name){
            case "net": //70 % success
                result  = rand.nextInt(100);
                if(result>70) return false;
                else return true;
            case "book": //75%
                result  = rand.nextInt(100);
                if(result>75) return false;
                else return true;
            case "hoodie": //80%
                result  = rand.nextInt(100);
                if(result>80) return false;
                else return true;
            case "sport shoes":
                return true;
            case "running shoes":
                result  = rand.nextInt(100);
                if(result>50) return false;
                else return true;
            case "sandwich":
                if(turn==1){ //only player can use this item
                    result = rand.nextInt();
                    if(result > 50) return true;
                    else return false;
                }
                else return false;
            
            default: 
                return true;
        }
    }
}
