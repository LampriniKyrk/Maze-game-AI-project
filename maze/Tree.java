/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;
import java.util.List;
import maze.gameItems.GameItem;

/**
 *
 * @author Kyrkou Lamprini
 * 
 */
public class Tree {
    Enemy enemy; 
    Player player;
    Cell[][]maze;
    NodeData root;
    
    public Tree(int depth, int branching_factor, Enemy enemy, Player player,Cell[][]maze){
        this.enemy = enemy;
        this.player = player;       
        this.maze = maze;

        //build the tree
        //begin by initializing the root
        
        root = new NodeData(new Enemy(enemy),new Player(player));
        root.setRoot();
        root.setTurn(1); // 1 represents a node we want to maximize
        root.setMove("");
        
        //must check if an item in use?
        if(player.getUsedItem()!=null)
            System.out.println("PLAYER has "+player.getUsedItem().getName()+" his SPEED is "+player.getSpeed());
        
        if(enemy.getUsedItem()!=null)
            System.out.println("AGENT has "+enemy.getUsedItem().getName()+" his SPEED is "+enemy.getSpeed());
        
        if(root.getAgentSpeed()==0)
            root.setAgentSpeed(1);
        if(root.getPlayerSpeed()==0)
            root.setPlayerSpeed(1);
        
        //set the items in play
        //they remain null if none
        if(enemy.getItem()!=null)
            root.setAgentItem(new GameItem(enemy.getItem())); 
        if(player.getItem()!=null)
            root.setPlayerItem(new GameItem(player.getItem()));

        generateChildren(root);
        
        //1st level
        ArrayList<NodeData> parents = new ArrayList<>(); 
        parents.add(root);
        //2nd-Nth level
        for (int i = 0; i < depth; i++) {
            //The new parents will be the children of the old parents
            ArrayList<NodeData> newParents = new ArrayList<>();
            for (NodeData p : parents) {
                ArrayList<NodeData> children = p.getChildren();
                for (NodeData child : children) {
                    newParents.add(child);
                }
            }
            for (int j = 0; j < newParents.size(); j++) {
                newParents.set(j, generateChildren(newParents.get(j)));
            }
            
            parents = newParents;
   
        }
        
        
        System.out.println("DONE BUILDING TREE");
        
        ArrayList<NodeData> test = new ArrayList<>();
        test = getLeaves(root, test);
        System.out.println("leaves are "+test.size());
           
    }
    
    public Tree getTree(){
        return this;
    }
    
    /**
     * Prints the left branch of the tree. For testing purposes
     * @param startingNode the node from which the branch starts getting printed
     * @param curDepth the current depth of the node
     */
    public void printLeftBranch(NodeData startingNode, int curDepth){
        ArrayList<NodeData> nextGen = startingNode.getChildren();
        if(startingNode.getParent()!=null)
            
            System.out.println("Depth : "+curDepth+" my move "+startingNode.getMove()+" parent "+startingNode.getParent().getMove());
        else
            System.out.println("Depth : "+curDepth+" "+startingNode.getMove());
        
        if(!startingNode.isLeaf()){
            printLeftBranch(startingNode.getChildren().get(0), curDepth+1);
        }
    }
    
    /**
     * Prints the whole tree. For testing purposes.
     * @param startingNode node for which the function is called
     * @param curDepth depth of startingNode
     * @param maxDepth maximum depth of the tree
     */
    public void printTree(NodeData startingNode, int curDepth, int maxDepth){
        ArrayList<NodeData> nextGen = startingNode.getChildren();
        if(startingNode.getParent()!=null)
            System.out.println("Depth : "+curDepth+" my move "+startingNode.getMove()+" parent "+startingNode.getParent().getMove());
        else
            System.out.println("Depth : "+curDepth+" "+startingNode.getMove());
        if(curDepth<maxDepth){
            for (NodeData nextGen1 : nextGen) {
                printTree(nextGen1, curDepth+1,maxDepth);
            }
        }
    }
    
    /**
     * The function that produces the possible children of a node.
     * @param n - possible parent node for which children will be created
     * @param branching_factor of the tree
     * @return the parent n.
     */
    public NodeData addChildrenToNode(NodeData n, int branching_factor){   
        //check if the current parent is an end condition node 
        //if so , no children must be added
        if(n.getAgentCoordinates().equals(n.getPlayerCoordinates())){
            return n;      
        }
        if(maze[n.getPlayerCoordinates().y][n.getPlayerCoordinates().x].isEndTile()){
            return n;
        }
        
        //if it's a non end contition node then it has children. If so, it's not a leaf
        n.setNotLeaf();
        
        //Check if the parent node is a use node :        
        if(n.getTurn()== 2 || n.getTurn() == 3){
            //get the children of the use node
            ArrayList<NodeData> childrenOfUse = getDecisionNodes(n);
            //add them to n's children list
            for (NodeData child : childrenOfUse) {
                n.addChild(child);
            }
            return n; //return the parent node
        }
        //an array list that holds the possible children of n
        ArrayList<NodeData> tmpChildren = new ArrayList<>();
        //Producing the possible children of n.
        for (int i = 0; i < branching_factor; i++) {
            //the child canditate
            NodeData tmp = new NodeData(new Enemy(n.getAgent()),new Player(n.getPlayer())); 
            //if the player was stunned in the previous round, he should still be stunned
            if(n.playerWasStunned()) tmp.setPlayerStunned();
            //if he had the stunned effect active in the previous turn then in this turn he's no longer stunned
            if(!n.playerWasStunned() && n.getPlayer().stunned()) tmp.getPlayer().setStunned(false);
            
             //if the agent was stunned in the previous round, he should still be stunned
            if(n.agentWasStunned()) tmp.setAgentStunned();
            //if he had the stunned effect active in the previous turn then in this turn he's no longer stunned
            if(!n.agentWasStunned() && n.getAgent().getStunned()) tmp.getAgent().setStunned(false);

            //set the picked upo items list
            tmp.copyPickedUpItemList(n.getPickedupItemsList());

            //set the turn so that minimax will know when to maximize or minimize
            if(n.getTurn()==0)
                tmp.setTurn(1);
            else if(n.getTurn() == 1)
                tmp.setTurn(0);

            tmp.setParent(n); //candidate's parent is the current node

            if (i==0){ //north direction
                tmp.setMove("n");
                //if PC turn
                if(tmp.getTurn()==0)
                    tmp.setAgentCoordinates(getMoveCoordinates("n", n.getAgentCoordinates(),0,n.getAgentSpeed(),
                            n.getAgent(),n.getPlayer()));
                else //if player Turn
                    tmp.setPlayerCoordinates(getMoveCoordinates("n", n.getPlayerCoordinates(),1,n.getPlayerSpeed(),
                            n.getAgent(),n.getPlayer()));
            }
            else if(i==1){
                tmp.setMove("e"); //east
                //if PC turn
                if(tmp.getTurn()==0)
                    tmp.setAgentCoordinates(getMoveCoordinates("e", n.getAgentCoordinates(),0,n.getAgentSpeed(),
                            n.getAgent(),n.getPlayer()));
                else //if player Turn
                    tmp.setPlayerCoordinates(getMoveCoordinates("e", n.getPlayerCoordinates(),1,n.getPlayerSpeed(),
                            n.getAgent(),n.getPlayer()));
            }
            else if(i==2){
                tmp.setMove("s"); //south
                //if PC turn
                if(tmp.getTurn()==0)
                    tmp.setAgentCoordinates(getMoveCoordinates("s", n.getAgentCoordinates(),0,n.getAgentSpeed(),
                            n.getAgent(),n.getPlayer()));
                else //if player Turn
                    tmp.setPlayerCoordinates(getMoveCoordinates("s", n.getPlayerCoordinates(),1,n.getPlayerSpeed(),
                            n.getAgent(),n.getPlayer()));
            }
            else if(i==3){
                tmp.setMove("w"); //west
                //if PC turn
                if(tmp.getTurn()==0)
                    tmp.setAgentCoordinates(getMoveCoordinates("w", n.getAgentCoordinates(),0,n.getAgentSpeed(),
                            n.getAgent(),n.getPlayer()));
                else //if player Turn
                    tmp.setPlayerCoordinates(getMoveCoordinates("w", n.getPlayerCoordinates(),1,n.getPlayerSpeed(),
                            n.getAgent(),n.getPlayer()));
            }
            else if(i==4){
                tmp.setMove("use"); //use item

                //if its the player's turn to chose a move
                if(n.getTurn()==0){
                    tmp.setTurn(3);
                }else{ //it's the agent's turn
                    tmp.setTurn(2);
                }
                //if PC turn
                if(tmp.getTurn()==2){
                    tmp.setAgentCoordinates(getMoveCoordinates("use", n.getAgentCoordinates(),0,n.getAgentSpeed(),
                            n.getAgent(),n.getPlayer()));
                }
                else if(tmp.getTurn() == 3){//if player Turn
                    tmp.setPlayerCoordinates(getMoveCoordinates("use", n.getPlayerCoordinates(),1,n.getPlayerSpeed(),
                            n.getAgent(),n.getPlayer()));
                }
            }
            //if PC turn
            if(n.getTurn()==1)
                tmp.setPlayerCoordinates(new Coordinates(n.getPlayerCoordinates().x, n.getPlayerCoordinates().y)); //player wont have moved
            else //if player Turn
                tmp.setAgentCoordinates(new Coordinates(n.getAgentCoordinates().x, n.getAgentCoordinates().y)); //PC wont have moved



            //if the move is valid add the node as a child
            if(tmp.getAgentCoordinates().x!=(-1) && tmp.getPlayerCoordinates().x!=-1){
                //if the move will pick up an item 
                if(tmp.getTurn()==0){ //if agents turn

                    if(maze[tmp.getAgentCoordinates().y][tmp.getAgentCoordinates().x].getItem()!=null){
                        //if the item was not pichked up in a previous branch or node
                        if(!tmp.getPickedupItemsList().contains(maze[tmp.getAgentCoordinates().y][tmp.getAgentCoordinates().x].getItem().getId())){
                            //agent loses previous item - item is replaced by new
                            tmp.setAgentItem(new GameItem(maze[tmp.getAgentCoordinates().y][tmp.getAgentCoordinates().x].getItem()));
                            //add the item's id on the list
                            tmp.addItemIDtoList(maze[tmp.getAgentCoordinates().y][tmp.getAgentCoordinates().x].getItem().getId());
                        }

                    }
                }
                else{
                    if(maze[tmp.getPlayerCoordinates().y][tmp.getPlayerCoordinates().x].getItem()!=null){
                        //if the item was not picked up on aprevious branch or node
                        if(!tmp.getPickedupItemsList().contains(maze[tmp.getPlayerCoordinates().y][tmp.getPlayerCoordinates().x].getItem().getId())){
                            tmp.setPlayerItem(new GameItem(maze[tmp.getPlayerCoordinates().y][tmp.getPlayerCoordinates().x].getItem()));
                            //add the item's id on the list
                            tmp.addItemIDtoList(maze[tmp.getPlayerCoordinates().y][tmp.getPlayerCoordinates().x].getItem().getId());
                        }
                    }

                }
                //then add it to children's list
                tmpChildren.add(tmp);

            }

        }

        //when all possible children are produced and it's determined which
        //are valid and invalid , add the valid to the parent's node list
        for (NodeData tmpChildren1 : tmpChildren) {
            //remove branch that begins with a dead end
            if(n.isRoot()){
                //if the child is not a dead end tile
                //add it to the root's children
                if(!roadIsClosed(tmpChildren1)){
                    n.addChild(tmpChildren1);
                }

            }
            //for every other non root parent node the children will be added normally
            else{
                n.addChild(tmpChildren1);
            }

        }
        
        return n;
    }
    
    /**
     * In this function the effects of the items are passed through the turns and 
     * the addChildrenToNode function is called for @param node.
     * @param node -parent node
     * @return node
     */
    public NodeData generateChildren(NodeData node){
        //set player and enemy speed to normal values
        node.setAgentSpeed(1);
        node.setPlayerSpeed(1);
                    
        boolean agentsDurationReduced = false;
        boolean playersDurationReduced = false;
        //first check if an item is already in use by whoever
        //____________________________________________________
        if(node.getAgent().getUsedItem()!=null || node.getPlayer().getUsedItem()!=null){
            //if only the agent item exists in use
            if(node.getAgent().getUsedItem()!=null && node.getPlayer().getUsedItem()==null){
                //depending on the item the tree will be different
                //if it's a speed enhancing item
                switch (node.getAgent().getUsedItem().getName()) {
                    case "running shoes":
                        if(node.getAgent().getItemDuration()>0){
                            node.setAgentSpeed(3);
                            if(node.getTurn()==0)
                                node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);
                            return addChildrenToNode(node, 5);
                        }
                        else node.getAgent().setUsedItemToNull();
                        break;
                    case "sport shoes":
                        if(node.getAgent().getItemDuration()>0){
                            node.setAgentSpeed(2);
                            if(node.getTurn()==0)
                                node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);
                            return addChildrenToNode(node, 5);
                            
                        }
                        else node.getAgent().setUsedItemToNull();
                        break;
                    default:
                        if(node.getAgent().getItemDuration()>0)
                            node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);
                        return addChildrenToNode(node, 5);
                        
                }
            }
            //only player uses item
            else if(node.getAgent().getUsedItem()==null && node.getPlayer().getUsedItem()!=null){
                //depending on the item the tree will be different
                //if it's a speed enhancing item
                switch (node.getPlayer().getUsedItem().getName()) {
                    case "running shoes":
                        if(node.getPlayer().getItemDuration()>0){
                            node.setPlayerSpeed(3);
                            if(node.getTurn()==1) //player's turn to move
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);
                            return addChildrenToNode(node, 5);
                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                    case "sport shoes":
                        if(node.getPlayer().getItemDuration()>0){
                            node.setPlayerSpeed(2);
                            if(node.getTurn()==1)
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);
                            return addChildrenToNode(node, 5);
                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                    case "sandwich":
                        if(node.getPlayer().getItemDuration()>0){
                            if(node.getTurn()==1)
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);

                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                    default:
                        if(node.getPlayer().getItemDuration()>0)
                            node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);
                        return addChildrenToNode(node, 5);
                        
                }
                
            }

            //if both are using an item
            else if(node.getAgent().getUsedItem()!=null && node.getPlayer().getUsedItem()!=null){
                //for agent
                switch (node.getAgent().getUsedItem().getName()) {
                    case "running shoes":
                        if(node.getAgent().getItemDuration()>0){
                            agentsDurationReduced = true;
                            node.setAgentSpeed(3);
                            if(node.getTurn()==0)
                                node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);
                            
                        }
                        else node.getAgent().setUsedItemToNull();
                        break;
                    case "sport shoes":
                        if(node.getAgent().getItemDuration()>0){
                            agentsDurationReduced = true;
                            node.setAgentSpeed(2);
                            if(node.getTurn()==0)
                                node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);

                        }
                        else node.getAgent().setUsedItemToNull();
                        break;
                }
                //for player
                switch (node.getPlayer().getUsedItem().getName()) {
                    case "running shoes":
                        if(node.getPlayer().getItemDuration()>0){
                            playersDurationReduced = true;
                            node.setPlayerSpeed(3);
                            if(node.getTurn()==1) //player's turn to move
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);

                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                    case "sport shoes":
                        if(node.getPlayer().getItemDuration()>0){
                            playersDurationReduced = true;
                            node.setPlayerSpeed(2);
                            if(node.getTurn()==1)
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);

                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                    case "sandwich":
                        if(node.getPlayer().getItemDuration()>0){
                            playersDurationReduced = true;
                            if(node.getTurn()==1)
                                node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);

                        }
                        else node.getPlayer().setUsedItemToNull();
                        break;
                }
                
                if(node.getAgent().getItemDuration()>0  && !agentsDurationReduced)
                        node.getAgent().setItemDuration(node.getAgent().getItemDuration()-1);
                if(node.getPlayer().getItemDuration()>0 && !playersDurationReduced)
                        node.getPlayer().setItemDuration(node.getPlayer().getItemDuration()-1);
                return addChildrenToNode(node, 5);
                
            }
           
        }

        //add children normally
        return addChildrenToNode(node, 5);

    }
    /**
     * Alternate function for evaluating the leaves of the tree.
     * This function is more complex than the one that is currently used by the program.
     */
    public void calculateLeavesValueAlternate(){
        ArrayList<Cell> endPos = new ArrayList<>();
        //find the ending positions
        for (int y = 0; y < 10; y++) { //y
            for (int x = 0; x < 20; x++) { //x 
                //if the cell is an end tile
                if(maze[y][x].isEndTile()){
                    endPos.add(new Cell(maze[y][x]));
                }      
            }
        }
        //get the paths that the player will most likely follow
        Search searchPaths = new Search(maze);
        ArrayList<Path> pathsOfPlayer = searchPaths.getPathsOfPlayer(new Cell(maze[player.getPositionY()][player.getPositionX()]), endPos);
        //get the shortest possible path for the player
        Path pathOfPlayer_shortest = searchPaths.sortPaths(pathsOfPlayer).get(0);
        
        Coordinates goalOfPlayer = new Coordinates(pathOfPlayer_shortest.getLastCell().getx(),pathOfPlayer_shortest.getLastCell().gety() );
        
        ArrayList<NodeData> leaves = new ArrayList<>();
        leaves = getLeaves(root, leaves);
        //for every existing leaf calculate the value of the state infavor of the agent
        for (NodeData leaf : leaves) {
            //we want the best value for a good position
            int itemscore = 0;
            int fullscore=0; 

            //We must consider that a branch that picks up an item is considered better 
            //than an empty branch.That is why we must give a bonus to that state
            if(leaf.getAgentItem()!=null){
                itemscore =(leaf.getAgentItem().getScore()+leaf.getAgentItem().getPlayerScore());
                fullscore+=itemscore*1000;
            }
            
            //if the current state intersects with the chosen path of the player
            //it has to get a bonus
            int bonus = 0;
            //see how close the move is to the perfect path of the player
            int distanceFromShortestPath = Integer.MAX_VALUE;
            
            //for every cell on the shortest path of the player to the closest end tile
            for (Cell c: pathOfPlayer_shortest.getCellList()) {
                int tmpDist = Math.abs(leaf.getAgentCoordinates().x-c.getx())+Math.abs(leaf.getAgentCoordinates().y -c.gety());
                if(tmpDist<distanceFromShortestPath){
                    distanceFromShortestPath = tmpDist;
                }
            }
            if(distanceFromShortestPath<5){
                bonus+=5; //may need more push
            }
            if(pathOfPlayer_shortest.containsCell(maze[leaf.getAgentCoordinates().y][leaf.getAgentCoordinates().x])){
                bonus+=15;
            }
            //bonus points have been calculated 
            
            //calculate the distance the player has from his goal
            int distanceOfPlayerFromGoal = Math.abs(leaf.getPlayerCoordinates().x-goalOfPlayer.x) +
                    Math.abs(leaf.getPlayerCoordinates().y-goalOfPlayer.y);
            fullscore+=distanceOfPlayerFromGoal*100;
            
            
            //substract points from a state that is further from the target
            int dist;  //distance             
            //calculate distance between agent and player
            dist = Math.abs(leaf.getAgentCoordinates().x-leaf.getPlayerCoordinates().x) +
                    Math.abs(leaf.getAgentCoordinates().y-leaf.getPlayerCoordinates().y);

            //check the distance between the opponents via Manhattan dist
            //smaller distance in favor for agent/ai
            //if the distance is >2 then give a big reward
            if(dist<2){
                if(dist==0){
                    fullscore+=1000000; //end state for minimax
                }
            }
            
            //calculate points substracted for stalling in the same place
            int stalling = 0;
            if(leaf.getAgent().getVisitedCells().size()>0){
                if(leaf.getAgent().getVisitedCells().containsKey(leaf.getAgentCoordinates())) 
                    stalling = leaf.getAgent().getVisitedCells().get(leaf.getAgentCoordinates()) * 2;
            }

            fullscore-= (dist+stalling);            
            fullscore = fullscore - 50*dist;
            //give bonus if it picks up an item (item is on dedicated space)
            if(maze[leaf.getAgentCoordinates().y][leaf.getAgentCoordinates().x].getItem()!=null){
                fullscore = fullscore + 15;
                //pick up item? not here?
                GameItem item = maze[leaf.getAgentCoordinates().y][leaf.getAgentCoordinates().x].getItem();
                int newItemScore = (item.getScore()+item.getPlayerScore())/2;
                //if the new item is better than the old one 
                //give a bonus to the state that the agent will pick it up
                if(newItemScore > itemscore){
                    fullscore+=newItemScore; 
                }      
            }
            
            fullscore = fullscore - 50*dist;
            
            //if the state has more that 2 walls and it's not that close to the player
            //give a minus points to it so that the agent does not find himself in a deadlock
            String tmp = maze[leaf.getAgentCoordinates().y][leaf.getAgentCoordinates().x].getwalls();
            int numOfWalls = tmp.split("-").length;
            
            //if the leaf is a deadend and the agent is not close to the player
            //fullscore gets a penalty
            if(numOfWalls > 2 && dist > 3){
                fullscore-= 300;
            }
            
            if(maze[leaf.getPlayerCoordinates().y][leaf.getPlayerCoordinates().x].isEndTile()) 
                fullscore = Integer.MIN_VALUE; //least favorable outcome
            
            //if the player was stunned during this branch
            if(leaf.playerWasStunned()) fullscore+=10;
            
            leaf.setScore(fullscore+bonus);    
        }
      
    }
    /**
     * The function that evaluates the states the leaves represent.
     */
    public void calculateLeavesValue(){
        
        ArrayList<Cell> endPos = new ArrayList<>();
        //find the ending positions?
        for (int y = 0; y < 10; y++) { //y
            for (int x = 0; x < 20; x++) { //x 
                //if the cell is an end tile
                if(maze[y][x].isEndTile()){
                    endPos.add(new Cell(maze[y][x]));
                }
                
            }
        }
        
        //get the paths that the player will most likely follow
        Search searchPaths = new Search(maze);
        ArrayList<Path> pathsOfPlayer = searchPaths.getPathsOfPlayer(new Cell(maze[player.getPositionY()][player.getPositionX()]), endPos);
        //get the shortest possible path for the player
        Path pathOfPlayer_shortest = searchPaths.sortPaths(pathsOfPlayer).get(0);
        
        Coordinates goalOfPlayer = new Coordinates(pathOfPlayer_shortest.getLastCell().getx(),pathOfPlayer_shortest.getLastCell().gety() );
        
        ArrayList<NodeData> leaves = new ArrayList<>();
        leaves = getLeaves(root, leaves);
        //for every existing leaf calculate the value of the state infavor of the agent
        for (NodeData leaf : leaves) {
            //we want the best value for a good position
            int itemscore = 0;
            int fullscore=0;
            
            //We must consider that a branch that picks up an item is considered better 
            //than an empty branch.That is why we must give a bonus to that state
            if(leaf.getAgentItem()!=null){
                itemscore =(leaf.getAgentItem().getScore()+leaf.getAgentItem().getPlayerScore());
                fullscore+=itemscore*1000;
            }
            
            
            //if the current state intersects with the chosen path of the player
            //it has to get a bonus
            int bonus = 0;
            
            //calculate the distance the player has from his goal
            int distanceOfPlayerFromGoal = Math.abs(leaf.getPlayerCoordinates().x-goalOfPlayer.x) +
                    Math.abs(leaf.getPlayerCoordinates().y-goalOfPlayer.y);
            fullscore+=distanceOfPlayerFromGoal*100;
            
            
            //substract points from a state that is further from the target
            int dist;  //distance             
            //calculate distance between agent and player
            dist = Math.abs(leaf.getAgentCoordinates().x-leaf.getPlayerCoordinates().x) +
                    Math.abs(leaf.getAgentCoordinates().y-leaf.getPlayerCoordinates().y);

            //check the distance between the opponents via Manhattan dist
            //smaller distance in favor for agent/ai
            //if the distance is >2 then give a big reward
            if(dist<2){
                if(dist==0){
                    fullscore+=1000000; //end state for minimax
                }
            }
        
            fullscore = fullscore - 50*dist;

            leaf.setScore(fullscore+bonus);

        }
    }
    
    /**
     * Function that gathers all the leaves of the tree in a list and returns them. the function 
     * is recursive.
     * @param startingNode node for which the function is called
     * @param leaves the list that will contain the leaves.
     * @return an ArralyList that contains all the leaves.
     */
    public ArrayList<NodeData> getLeaves(NodeData startingNode,ArrayList<NodeData> leaves){
        
        //if the node is not a leaf , call the function for each of his children
        if(!startingNode.isLeaf()){
            ArrayList<NodeData> children = startingNode.getChildren();
            for (NodeData child : children) {
                 leaves = getLeaves(child,leaves);
            }
        }
        else{
            leaves.add(startingNode);
        }
        return leaves;
    }
    
    /**
     * @param move n , e, s, w or use
     * @param currentCoordinates current coordinates for agent AI
     * @param turn 0 for PC , 1 for human
     * @param speed of the player or agent
     * @param enemy the agent
     * @param player the player
     * @return the coordinates of the move , if the given move is not obstructed.Otherwise it will return (-1,-1)
     */
    public Coordinates getMoveCoordinates(String move , Coordinates currentCoordinates , int turn, int speed, Enemy enemy, Player player){
        //this function will find the move coordinates and will determine if the move is possible
        //if not it will return (-1, -1) 
        if(turn==1 && player.stunned() && !move.equals("use")) return currentCoordinates;
        if(turn == 0 && enemy.getStunned() && !move.equals("use")) return currentCoordinates;
        
        if(turn == 1 && move.equals("use")&& player.getItem()== null) return new Coordinates(-1, -1);
        else if (turn == 1 && move.equals("use")&& player.getItem()!= null && player.stunned()) return new Coordinates(-1, -1);
        
        if(turn == 0 && move.equals("use")&& enemy.getItem()== null) return new Coordinates(-1, -1);
        else if (turn == 1 && move.equals("use")&& enemy.getItem()!= null && enemy.getStunned()) return new Coordinates(-1, -1);
        
        //check if move is valid
        switch (move) {
            case "n":

                //if the path is not obstructed by a wall
                if(!maze[currentCoordinates.y][currentCoordinates.x].getNorthWall() && currentCoordinates.y>0){ 
                    //steps emulates movement behavior for speed >1
                    int steps = 0;
                    for (int i = 1; i <= speed; i++) {
                        if(currentCoordinates.y>=i) //not out of bounds
                            if(!maze[currentCoordinates.y-i][currentCoordinates.x].getSouthWall()) {
                                steps++;
                            }                       
                    }
                     return (new Coordinates(currentCoordinates.x,currentCoordinates.y-steps));
                }

                break;
            case "e":
                //if the path is not obstructed by a wall
                if(!maze[currentCoordinates.y][currentCoordinates.x].getEastWall() && currentCoordinates.x <19){ 
                    int steps = 0;
                    for (int i = 1; i <= speed; i++) {
                        if(currentCoordinates.x+i<19)
                            if(!maze[currentCoordinates.y][currentCoordinates.x+i].getWestWall()){
                                steps++;
                            }
                    }
                    return new Coordinates(currentCoordinates.x+steps,currentCoordinates.y);
                }
                break;
            case "s":
                //if the path is not obstructed by a wall
                if(!maze[currentCoordinates.y][currentCoordinates.x].getSouthWall() && currentCoordinates.y < 9){
                    int steps = 0;
                    
                    for (int i = 1; i <= speed; i++) {
                        if(currentCoordinates.y+i<9)
                            if(!maze[currentCoordinates.y+i][currentCoordinates.x].getNorthWall()){
                                steps++;
                            }
                    }
                    return new Coordinates( currentCoordinates.x,currentCoordinates.y+steps);
                }
                break;
            case "w":
                //if the path is not obstructed by a wall
                if(!maze[currentCoordinates.y][currentCoordinates.x].getWestWall() && currentCoordinates.x >0){
                    int steps = 0; //num of steps forward the curr player can make
                    for (int i = 1; i <= speed; i++) {
                        if(currentCoordinates.x>=i)
                            if(!maze[currentCoordinates.y][currentCoordinates.x-i].getEastWall()){
                                steps++;
                            }
                    }
                    return new Coordinates( currentCoordinates.x-steps,currentCoordinates.y);
                }
                break;
            case "use":
                //if there is an item
                if(turn==0){
                    //check if there is an item in agent's inventory
                    //if there isn't the move-state has no meaning and will not be added to children
                    if(enemy.getItem()!=null){
                        if(enemy.getItem().canBeUsed(enemy, player, true, maze)){
                            return new Coordinates(currentCoordinates.x, currentCoordinates.y);
                        }  
                    }
                    return new Coordinates(-1, -1);
                }
                else{
                    //check if there is an item in player's inventory
                    //if there isn't the move-state has no meaning and will not be added to children
                    if(player.getItem()!=null){
                        if(player.getItem().canBeUsed(enemy, player, false, maze)){
                            return new Coordinates(currentCoordinates.x, currentCoordinates.y);
                        }
                    }
                    return new Coordinates(-1, -1);
                    
                }
                //break;
            default:
                break;
        }
        //if everything else has failed return coordinates below, 
        //so that the brach of the tree will not continue bearing branches
        return new Coordinates(-1, -1);
    }
    
    /**
     * The simple minimax function. Does not support decision nodes.
     * @param node for which function is called
     * @param depth 
     * @param rightChoice the choice 
     * @return the choice that was chosen
     */
    public Choice miniMax(NodeData node, int depth, Choice rightChoice){
        Choice finalChoice = new Choice();
        
        //minimize
        if(node.getTurn()== 0){
            //init 
            int bestVal = Integer.MAX_VALUE;
            String commandToBeAdded="";
            Choice choice = new Choice();
            
            ArrayList<NodeData> children = node.getChildren();
            //if leaf node
            if(children.isEmpty()){
                  
                bestVal = node.getScore();
                finalChoice.setScore(bestVal);
                finalChoice.addtoCommands("");

            }
            else{//if inbetween node
                for (NodeData child : children) {
                    choice = miniMax(child, depth-1,rightChoice);
                    
                    if(bestVal>choice.getScore()){
                        finalChoice = new Choice(choice);
                        bestVal = choice.getScore();
                        commandToBeAdded = child.getMove();   
                    }   
                }
                finalChoice.addtoCommands(commandToBeAdded);
            }

            return finalChoice;
        }
        //maximize
        else{
            //init
            int bestVal = Integer.MIN_VALUE;
            String commandToBeAdded="";
            Choice choice = new Choice();
            
            ArrayList<NodeData> children = node.getChildren();
            //if it is a leaf node
            if(children.isEmpty()){
                  
                bestVal = node.getScore();
                finalChoice.setScore(bestVal);
                finalChoice.addtoCommands("");
                    
            }
            else{//for every child call minimax
                for (NodeData child : children) {

                    choice = miniMax(child, depth-1,rightChoice);
                    //if the child has a better value
                    if(bestVal<choice.getScore()){
                        finalChoice = new Choice(choice);
                        bestVal = choice.getScore();
                        commandToBeAdded = child.getMove();
                    }
                }
                finalChoice.addtoCommands(commandToBeAdded);
            }
            return finalChoice;
        }
    }
    
    /**
     * The minimax function that supports decision nodes. 
     * @param node for which the function is called
     * @return the choice that will be made (best choice)
     */
    public Choice expectiMiniMax(NodeData node){
        
        Choice finalChoice = new Choice();
        
        //minimize
        if(node.getTurn()== 0){
            //init 
            int bestVal = Integer.MAX_VALUE;

            String commandToBeAdded="";
            Choice choice = new Choice();
            
            //get the node's children
            ArrayList<NodeData> children = node.getChildren();
            //if leaf node
            if(children.isEmpty()){

                bestVal = node.getScore();
                finalChoice.setScore(bestVal);
                finalChoice.addtoCommands("");
            }
            else{//if inbetween node
                
                for (NodeData child : children) {
                    choice = expectiMiniMax(child);

                    if(bestVal>choice.getScore()){
                        finalChoice = new Choice(choice);
                        bestVal = choice.getScore();

                        commandToBeAdded = child.getMove();   
                    }   
                }
                finalChoice.addtoCommands(commandToBeAdded);

            }

            return finalChoice;
        }
        //maximize
        else if (node.getTurn()== 1){
            //init
            int bestVal = Integer.MIN_VALUE;

            String commandToBeAdded="";
            Choice choice = new Choice();
            
            ArrayList<NodeData> children = node.getChildren();
            //if it is a leaf node
            if(children.isEmpty()){
                
                bestVal = node.getScore();
                finalChoice.setScore(bestVal);
                finalChoice.addtoCommands("");
            }
            else{//for every child call minimax

                for (NodeData child : children) {

                    choice = expectiMiniMax(child);
                    //if the child has a better value
                    if(bestVal<choice.getScore()){
                        finalChoice = new Choice(choice);
                        bestVal = choice.getScore();
                        commandToBeAdded = child.getMove();
                    }
                }
                finalChoice.addtoCommands(commandToBeAdded);               
            }

            return finalChoice;
        }
        else{ //node is a probability node!

            Choice choice = new Choice();
            
            ArrayList<NodeData> children = node.getChildren(); //children never be empty on use nodes
            double sum = 0; //the weighted sum of the children 
            for(NodeData child : children){
                choice = expectiMiniMax(child);  //minimax foreach child              
                sum+= choice.getScore()*child.getProbability();
            }
            finalChoice = new Choice(choice);
            finalChoice.addtoCommands("use");
            finalChoice.setScore((int)Math.ceil(sum));

            return finalChoice;

        }
    }
    
    /**
     * Checks if the agent is about to go towards a dead end.
     * @param n node
     * @return true: the cell is either not a dead end or something of value is there. 
     * false: dead end.
     */
    public boolean roadIsClosed(NodeData n){
        if(n.getTurn()==0){ //agent's playing
            String tmp = maze[n.getAgentCoordinates().y][n.getAgentCoordinates().x].getwalls();
            String []walls = tmp.split(" ");
            
            //check if player on deadend
            boolean playerOnDeadEnd = false;
            if(n.getAgentCoordinates().equals(n.getPlayerCoordinates()))
                playerOnDeadEnd = true;
            if(!playerOnDeadEnd && walls.length>2){
                //check if there is an item in the deadend
                //the program must not return true if there is an item
                if(maze[n.getAgentCoordinates().y][n.getAgentCoordinates().x].getItem()!=null) return false;
                     
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Creates nodes for the "use" case action.
     * @param startingNode the node for which the decision nodes are made
     * @return the decision nodes that were created
     */
    private ArrayList<NodeData> getDecisionNodes(NodeData startingNode){
        ArrayList<NodeData> decisionNodesList = new ArrayList<>();
        int turn = startingNode.getTurn();
        //if it's the turn of the agent :
        if(turn == 2){
            //get the item
            GameItem item = new GameItem(startingNode.getAgentItem()); 
            
            //make a node for the success case 
            NodeData successNode = new NodeData(new Enemy(startingNode.getAgent()), new Player(startingNode.getPlayer()));
            successNode.setTurn(0);
            successNode.setMove("useS");
            successNode.setParent(startingNode);
            successNode.copyPickedUpItemList(startingNode.getPickedupItemsList());
            
            //make node for the fail case
            NodeData failNode = new NodeData(new Enemy(startingNode.getAgent()), new Player(startingNode.getPlayer()));
            failNode.setTurn(0);
            failNode.setMove("useF");
            failNode.setParent(startingNode);
            failNode.copyPickedUpItemList(startingNode.getPickedupItemsList());
            
            //depending on the item the probabilities of success differ
            switch(item.getName()){
                case "net": 
                    successNode.setProbability(0.7);
                    successNode.getPlayer().setStunned(true);
                    successNode.setPlayerStunned();
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0.3);
                    failNode.getAgent().setItemToNull();
                    break;
                case "book":
                    successNode.setProbability(0.75);
                    successNode.getPlayer().setItemToNull();
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0.25);
                    failNode.getAgent().setItemToNull();
                    break;
                case "hoodie":
                    successNode.setProbability(0.8);
                    successNode.getAgent().enableItem();
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0.2);
                    failNode.getAgent().setItemToNull();
                    break;
                case "sport shoes":
                    successNode.setProbability(0.8);
                    successNode.getAgent().enableItem();
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0.2);
                    failNode.getAgent().setItemToNull();
                    break;
                case "running shoes":
                    successNode.setProbability(0.5);
                    successNode.getAgent().enableItem();
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0.5);
                    failNode.getAgent().setItemToNull();
                    break;
                case "sandwich":
                    successNode.setProbability(1);
                    successNode.getAgent().setItemToNull();
                    
                    failNode.setProbability(0);
                    failNode.getAgent().setItemToNull();
      
            }
            //add the nodes to the list
            decisionNodesList.add(successNode);
            decisionNodesList.add(failNode);
            
        }
        else if (turn == 3){ //use node belongs to player
            //get the item
            GameItem item = new GameItem(startingNode.getPlayerItem()); 
            //make a node for the success case
            NodeData successNode = new NodeData(new Enemy(startingNode.getAgent()), new Player(startingNode.getPlayer()));
            successNode.setTurn(1);
            successNode.setMove("useS");
            successNode.setParent(startingNode);
            successNode.copyPickedUpItemList(startingNode.getPickedupItemsList());
            
            NodeData failNode = new NodeData(new Enemy(startingNode.getAgent()), new Player(startingNode.getPlayer()));
            failNode.setTurn(1);
            failNode.setMove("useF");
            failNode.setParent(startingNode);
            failNode.copyPickedUpItemList(startingNode.getPickedupItemsList());
            
            //depending on the item the probabilities of success differ
            switch(item.getName()){
                case "net": 
                    successNode.setProbability(0.7);
                    successNode.getAgent().setStunned(true);
                    successNode.setAgentStunned();
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0.3);
                    failNode.getPlayer().setItemToNull();
                    break;
                case "book":
                    successNode.setProbability(0.75);
                    successNode.getAgent().setItemToNull();
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0.25);
                    failNode.getPlayer().setItemToNull();
                    break;
                case "hoodie":
                    successNode.setProbability(0.8);
                    successNode.getPlayer().enableItem();
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0.2);
                    failNode.getPlayer().setItemToNull();
                    break;
                case "sport shoes":
                    successNode.setProbability(0.8);
                    successNode.getPlayer().enableItem();
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0.2);
                    failNode.getPlayer().setItemToNull();
                    break;
                case "running shoes":
                    successNode.setProbability(0.5);
                    successNode.getPlayer().enableItem();
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0.5);
                    failNode.getPlayer().setItemToNull();
                    break;
                case "sandwich":
                    successNode.setProbability(1);
                    successNode.getPlayer().enableItem();
                    successNode.getPlayer().setChanceToEscape(true);
                    successNode.getPlayer().setItemToNull();
                    
                    failNode.setProbability(0);
                    failNode.getPlayer().setItemToNull();
      
            }
            //add the nodes to the list
            decisionNodesList.add(successNode);
            decisionNodesList.add(failNode);
        }
        
        return decisionNodesList;
    }
    
}
