/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;
import maze.gameItems.GameItem;

/**
 *
 * @author Kyrkou Lamprini
 */
public class NodeData {
    private int score;
    private String move;
    private ArrayList<Integer> pickedupItems;
    
    private int turn;
    private ArrayList<NodeData> children;
    private NodeData parent;
    private boolean isRoot;
    private boolean isLeaf;
    private boolean playerWasStunned;
    private boolean agentUsedItemOnBranch;
    private boolean agentWasStunned;
    
    private double probability;

    
    private Enemy enemy;
    private Player player;
    
    
    public NodeData(Enemy enemy, Player player){
        pickedupItems = new ArrayList<>();
        probability = 1;
        score = 0;
        move = null;
        
        turn = -1;
        children = new ArrayList<>();
        parent = null;
        isRoot = false;
        isLeaf = true;
        playerWasStunned = false;
        agentWasStunned = false;
        agentUsedItemOnBranch = false;
         
        this.enemy = new Enemy(enemy);
        this.player = new Player(player);
    }
       
    
   //___________________SETTERS GETTERS _________________________
    public ArrayList<Integer> getPickedupItemsList(){
        return pickedupItems;
    }
    
    public void addItemIDtoList(int id){
        pickedupItems.add(id);
    }
    
    public void copyPickedUpItemList(ArrayList<Integer> otherList){
        for (Integer itemID : otherList) {
            pickedupItems.add(itemID);
        }
    }
    
    
    public void setAgentHasUsedItemOnBranch(boolean b){
        agentUsedItemOnBranch = b;
    }
    
    public void setPlayerStunned(){
        player.setStunned(true);
    }
    
    public boolean playerWasStunned(){
        return playerWasStunned;
    }
    
    public boolean agentWasStunned(){
        return agentWasStunned;
    }
    
    public void setAgentStunned(){
        enemy.setStunned(true);
    }
    
    public void setProbability(double p){
        probability = p;
    }
    
    public double getProbability(){
        return probability;
    }
    
    public void setAgentItem(GameItem item){
        this.enemy.setItem(new GameItem(item));
    }
    
    public void setPlayerItem(GameItem item){
        this.player.setItem(new GameItem(item)); 
    }
    
    public void setAgentCoordinates(Coordinates c){
        enemy.setCoordinates(new Coordinates(c.x, c.y));
    }
    
    public void setPlayerCoordinates(Coordinates c){
        player.setCoordinates(new Coordinates(c.x, c.y));
    }
    
    public void setNotLeaf(){
        isLeaf = false;
    }
    
    public void setRoot(){
        isRoot = true;
    }
    
    public void setParent(NodeData p){
        parent = p;
    }
    
    public void addChild(NodeData child){
        children.add(child);
    }
    
    public void setScore(int score){
        this.score = score;
    }
    
    public boolean setMove(String move){
        if(move.equals("n")||move.equals("e")|| move.equals("s")||move.equals("w")||
                move.equals("use") || move.equals("useS") || move.equals("useF")){
            this.move = move;
            return true;
        }
        return false;
    }
    
    public boolean setTurn(int t ){
        if(t >=0 && t<=3){
            //0 = enemy , 1 = player
            turn = t;
            return true;
        }
        return false;
    }
    
    public GameItem getAgentItem(){
        return enemy.getItem();
    }
    
    public GameItem getPlayerItem(){
        return player.getItem();
    }
    public int getScore(){
        return score;
    }
    
    public int getTurn(){
        return turn;
    }
    
    public String getMove(){
        return move;
    }
    
    public ArrayList<NodeData> getChildren(){
        return children;
    }
    
    public NodeData getParent(){
        return parent;
    }
    
    public boolean isRoot(){
        return isRoot;
    }
    
    public boolean isLeaf(){
        return isLeaf;
    }
    
    public Coordinates getAgentCoordinates(){
        return enemy.getCoordinates();
    }
    
    public Coordinates getPlayerCoordinates(){
        return player.getCoordinates();
    }
    
    public int getAgentSpeed(){
        return enemy.getSpeed();
    }
    
    public void setAgentSpeed(int speed){
        enemy.setSpeed(speed);
    }
    
    public int getPlayerSpeed(){
        return player.getSpeed();
    }
    
    public void setPlayerSpeed(int speed){
        player.setSpeed(speed);
    }
    
    public Enemy getAgent(){
        return enemy;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public void setAgentUsedItem(GameItem item){
        enemy.setUsedItem(item);
    }
    
    public void setPlayerUsedItem(GameItem item){
        player.setUsedItem(item);
    }
    
    public GameItem getAgentUsedItem(){
        return enemy.getUsedItem();
    }
    
    public GameItem getPlayerUsedItem(){
        return player.getUsedItem();
    }
    
}
