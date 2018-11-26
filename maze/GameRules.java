/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import maze.gameItems.GameItem;


/**
 *
 * @author Kyrkou Lamprini
 */
public class GameRules {
    Cell[][]maze; //the maze
    Player mainPlayer; 
    private Player dummyPlayer;//dummyplayer will hold player's old coordinates
    //and the n villains
    Enemy []enemy;
    
    //the items on play
    private GameItem []items;
    
    //coordinated for items and end tiles
    private Coordinates[] endTilesCoordinates;
    private Coordinates[] itemCoordinates;
    
    //a map of the items and their coordinates
    private HashMap<Coordinates,GameItem> coordinatesItemsMap;
    
    private boolean playerIsHidden;
    
    private boolean gameWasLoaded;
    
    
    //constructor

    /**
     *
     * @param maze
     * @param playerPosition
     * @param numOFEnemies
     * @param enemyPosition
     */
        public GameRules(Cell[][]maze, Coordinates playerPosition, int numOFEnemies, Coordinates []enemyPosition , boolean gameloaded){
        playerIsHidden = false;
        dummyPlayer = null;
        gameWasLoaded = gameloaded;
                
        coordinatesItemsMap = new HashMap<>();
        this.maze = maze;
        mainPlayer = new Player(playerPosition.x, playerPosition.y);
        enemy = new Enemy[numOFEnemies];
        for (int i = 0; i < numOFEnemies; i++) {
            enemy[i] = new Enemy(enemyPosition[i].x, enemyPosition[i].y, "normal");    
        }
        
        //init items
        items = new GameItem[6];
        itemCoordinates = new Coordinates[6];
        if(!gameWasLoaded){
            //value|name|duration|range ,,,-1 means not active (no range item)
            items[0]= new GameItem(7,7, "sport shoes", 6, -1);
            items[1]= new GameItem(8,6, "running shoes", 3, -1);
            items[2]= new GameItem(6,9, "sandwich", 5, -1);
            items[3]= new GameItem(9,8, "hoodie", 2, -1);
            items[4]= new GameItem(6,6, "book", 2, 2);
            items[5]= new GameItem(9,8, "net", 1, 4);
            
           //then we need to scatter the items in the area of play
            Random rand = new Random();
            for (int i = 0; i < items.length; i++) {
                boolean flag = false;
                while (!flag) {                
                    int x = rand.nextInt(20);
                    int y = rand.nextInt(10);

                    if(maze[y][x].getItem()== null){
                        maze[y][x].setItem(new GameItem(items[i]));
                        itemCoordinates[i]= new Coordinates(x, y);
                        //add the pair to the hashmap
                        coordinatesItemsMap.put(itemCoordinates[i],items[i]);
                        flag = true;
                    }
                }            
            }//items were scattered 
        }
        else{
            int k = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 20; j++) {
                    if (maze[i][j].getItem()!=null){
                        items[k] = new GameItem(maze[i][j].getItem());
                        itemCoordinates[k] = new Coordinates(j, i);
                        k++;
                    }
                }            
            }
        }
        
        
        //if the game was not loaded from file scatter the endtiles
        Random rand = new Random();
        //mark end tiles = always on last column 4 tiles
        endTilesCoordinates = new Coordinates[4];
        if(!gameWasLoaded){
            for (int i = 0; i < 4; i++) {
                boolean flag = false; // in order not to make the same tile an end tile twice
                while(!flag){
                    int y = rand.nextInt(10);
                    if(maze[y][19].isEndTile()==false) {
                        maze[y][19].setEndTile(true);
                        endTilesCoordinates[i] = new Coordinates(19,y );
                        flag = true;
                    }
                }   
            }  
        }
        else{
            int k =0;
            for (int i = 0; i < 10; i++) {
                if(maze[i][19].isEndTile()){
                    endTilesCoordinates[k]=new Coordinates(19,i);
                    k++;
                }
                
            }
        }
        
    }
       
    /**
     * Function that moves the player
     * @param newCoordinates : the coordinates of the new destination
     * @return true if the move was successful
     */
    public boolean movePlayer(Coordinates newCoordinates){
        //player coordinates
        int x, y;
        x = mainPlayer.getPositionX();
        y = mainPlayer.getPositionY();
        //if player still got moves left
        if(mainPlayer.getSpeed()>0){
            //player moves to the east
            if(x < newCoordinates.x && !maze[y][x].getEastWall() && !maze[newCoordinates.y][newCoordinates.x].getWestWall()){ 

                //reduces movement points left for this turn
                mainPlayer.reduceSpeed(); 

                maze[y][x].setPlayerOnCell(false); //remove player from previous cell
                mainPlayer.move("e");//moves east

                maze[newCoordinates.y][newCoordinates.x].setPlayerOnCell(true); //the player is now on the new cell
                if(maze[newCoordinates.y][newCoordinates.x].getItem()!= null ){
                    //the player gets the new item,while the old one dissappears
                    if(mainPlayer.getItem()!=null) mainPlayer.setItemToNull();
                    
                    mainPlayer.setItem(maze[newCoordinates.y][newCoordinates.x].getItem());
                    
                    //remove item from maze
                    maze[newCoordinates.y][newCoordinates.x].setItemToNull();
                }
                return true;

            }

            //player moves to the west
            if(x > newCoordinates.x && !maze[y][x].getWestWall() && !maze[newCoordinates.y][newCoordinates.x].getEastWall() ){ 
                mainPlayer.reduceSpeed(); //reduces movement points left for this turn

                maze[y][x].setPlayerOnCell(false); //remove player from previous cell
                mainPlayer.move("w");//moves west

                maze[newCoordinates.y][newCoordinates.x].setPlayerOnCell(true); //the player is now on the new cell
                if(maze[newCoordinates.y][newCoordinates.x].getItem()!= null ){
                    //the player gets the new item,while the old one dissappears
                    if(mainPlayer.getItem()!=null) mainPlayer.setItemToNull();
                    
                    mainPlayer.setItem(maze[newCoordinates.y][newCoordinates.x].getItem());
                    
                    //remove item from maze
                    maze[newCoordinates.y][newCoordinates.x].setItemToNull();
                }
                return true;
            }

            //player moves to the south
            if(y < newCoordinates.y && !maze[y][x].getSouthWall() && !maze[newCoordinates.y][newCoordinates.x].getNorthWall() ){ 
                mainPlayer.reduceSpeed(); //reduces movement points left for this turn

                maze[y][x].setPlayerOnCell(false); //remove player from previous cell
                mainPlayer.move("s");//moves south

                maze[newCoordinates.y][newCoordinates.x].setPlayerOnCell(true); //the player is now on the new cell
                
                if(maze[newCoordinates.y][newCoordinates.x].getItem()!= null ){
                    //the player gets the new item,while the old one dissappears
                    if(mainPlayer.getItem()!=null) mainPlayer.setItemToNull();
                    //get the new item
                    mainPlayer.setItem(maze[newCoordinates.y][newCoordinates.x].getItem());
                    
                    //remove item from maze
                    maze[newCoordinates.y][newCoordinates.x].setItemToNull();
                }
                return true;
            }

            //player moves to the north
            if(y > newCoordinates.y && !maze[y][x].getNorthWall() && !maze[newCoordinates.y][newCoordinates.x].getSouthWall() ){ 
                mainPlayer.reduceSpeed(); //reduces movement points left for this turn

                maze[y][x].setPlayerOnCell(false); //remove player from previous cell
                mainPlayer.move("n");//moves north

                maze[newCoordinates.y][newCoordinates.x].setPlayerOnCell(true); //the player is now on the new cell
                
                if(maze[newCoordinates.y][newCoordinates.x].getItem()!= null ){
                    //the player gets the new item,while the old one dissappears
                    if(mainPlayer.getItem()!=null) mainPlayer.setItemToNull();
                    //get the new item
                    mainPlayer.setItem(maze[newCoordinates.y][newCoordinates.x].getItem());
                    
                    //remove item from maze
                    maze[newCoordinates.y][newCoordinates.x].setItemToNull();
                }
                return true;
            }
            
        }
        return false;
    }
    
    /**
     * Enemy AI function.
     * @return true if the move was successful
     */
    public boolean EnemyAI(){
        //first of all update items info if needed
        for (Enemy en : enemy) {
            if(en.getUsedItem()!=null){
            en.updateItemUses(mainPlayer, maze, false);
            }
            if(en.getItem()!=null){
                System.out.println("ENEMY HAS ITEM : "+en.getItem().getName());
                System.out.println("ENEMY USED "+en.getItem().getName());
                //check if item worked 
                if(en.getItem().succeeded(0)){
                    en.enableItem(); //enable his item
                    boolean stun = en.updateItemUses(mainPlayer, maze, true);
                    if(stun){
                        mainPlayer.setStunned(true);
                    }
                }
                else{
                    en.setItemToNull();
                    System.out.println("ITEM DID NOT WORK");
                }
                return true;


            }
            else System.out.println("ENEMY HAs NO ITEM");


            //set depth of search
            int depthOfSearch = 9;
            Choice n = null;
            //check if the agent has knows where the player is(in case of hidden player)
            //if player hidden build the tree with old position
            if(playerIsHidden){
                Tree tree = new Tree(depthOfSearch, 5, en, dummyPlayer,this.maze);
                tree.calculateLeavesValue();

                n = new Choice();
                n = tree.expectiMiniMax(tree.root);
                System.out.println("Moves: " + n.getCommands());
            }
            else{

                Tree tree = new Tree(depthOfSearch, 5, en, mainPlayer,this.maze);
                tree.calculateLeavesValue();

                n = new Choice();
                n = tree.expectiMiniMax(tree.root);
                //n = tree.expectiMiniMax(tree.root);
                System.out.println("Moves: " + n.getCommands());
            }
            //if there is a valid move to do
            if(n!=null)  {  
                String moves[] = n.getCommands().split("-");
                String move=""; //CHANGED
                if(moves.length>0)
                    move = moves[moves.length-1]; //DECLARATION OF MOVE WAS HERE

                maze[en.getPositionY()][en.getPositionX()].setEnemyOnCell(false);
                boolean moved = en.move(move);
                //if it didnt move , it used an item
                if(!moved && en.getItem()!=null){
                    System.out.println("ENEMY USED "+en.getItem().getName());
                    //check if item worked 
                    if(en.getItem().succeeded(0)){
                        en.enableItem(); //enable his item
                        boolean stun = en.updateItemUses(mainPlayer, maze, true);
                        if(stun){
                            mainPlayer.setStunned(true);
                        }
                    }
                    else{
                        en.setItemToNull();
                        System.out.println("ITEM DID NOT WORK");
                    }
                }
                //IF ITEM ON NEW POSITION PICK IT UP
                if(maze[en.getPositionY()][en.getPositionX()].getItem()!=null){
                    //give agent the item
                    en.setItem(maze[en.getPositionY()][en.getPositionX()].getItem());
                    //remove item from maze
                    maze[en.getPositionY()][en.getPositionX()].setItemToNull();
                    System.out.println("ENEMY PICKED Up: "+en.getItem().getName());

                }
                //set enemy on new position
                maze[en.getPositionY()][en.getPositionX()].setEnemyOnCell(true);
            }
        
        }
             
        return true;
    }
    

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
    
    private ArrayList<Path> bfs(Cell startingPosition){
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
        
        //
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
    
    private ArrayList<Path> findPathsToPlayer(ArrayList<Path> allPaths,Coordinates lastKnownPos){
        ArrayList<Path> pathsToPlayer = new ArrayList<>();
        //for every path found 
        for (Path path : allPaths) {
            //if path ends on the cell the player is on
            if(path.playerOnPath(mainPlayer.getPositionX(), mainPlayer.getPositionY())>=0){
                Path tmp = new Path(path.cutPath(mainPlayer.getPositionX(), mainPlayer.getPositionY()));
                if(!pathsToPlayer.contains(tmp))pathsToPlayer.add(tmp);
            }
        }  
        return pathsToPlayer;
    }
    
    private ArrayList<Path> sortPaths(ArrayList<Path> paths){
        ArrayList<Path>sortedPaths = new ArrayList<>();
        paths.sort(new Comparator<Path>() {

            @Override
            public int compare(Path o1, Path o2) {
                return o1.getPathLength() - o2.getPathLength();
            }
        });
        
        return sortedPaths = new ArrayList<>(paths);
    }
    
    //evaluates paths and returns the best option
    private HashMap<Path,Integer> evaluatePaths(ArrayList<Path> paths){
        ArrayList<Path>p2p = findPathsToPlayer(paths,null);
        ArrayList<Path> interPaths = findIntersectingPaths(p2p);
        
        HashMap<Path,Integer> evaluatedPaths = new HashMap<>();
        for (Path path : p2p) {
            //calculate steps to target
            int lengthScore = path.getPathLength();
            
            int fullScore = 100;
            int itemscore = path.hasItem();
            //if the path contains an item
            //bigger score = better score
            fullScore = fullScore - lengthScore + itemscore;
            //if a path interects with a probable player chosen path
            for (Path interPath : interPaths) {
                if(path.equals(interPath)) fullScore+=5;
            }
            
            evaluatedPaths.put(path, fullScore);
        }
        
       
        return evaluatedPaths;
    }
    
    //Function to find best path from player to endTiles
    //check if it intersects with an enemy path
    private ArrayList<Path> findIntersectingPaths(ArrayList<Path>enemyPaths){
        
        ArrayList<Path> enPaths = findPathsToPlayer(enemyPaths,null);
        
        ArrayList<Path>paths = dfs(maze[mainPlayer.getPositionY()][mainPlayer.getPositionX()]);
        ArrayList<Path>truePaths = new ArrayList<>();
        //find accurate paths to end tiles from the main player position
        for (Path p : paths) {
            if (p.hasEndTile()!=null){
                Coordinates pc = p.hasEndTile();
                //cut paths
                Path tmp = new Path(p.cutPath(pc.x, pc.y));
                truePaths.add(new Path(tmp));
            }
        }
        //find wich of these paths intersect on multiple cells with the enemy paths
        //this is to give bonus to enemy paths that intersect with player paths
        ArrayList<Path> intersectingPaths = new ArrayList<>();
        
        for (Path truePath : truePaths) {
            for (Path enPath : enPaths) {
                if(truePath.intersects(enPath)> 1){ //>1 because all paths will have at least one cell in common(player position cell)
                    intersectingPaths.add(new Path(enPath));
                }
            }
        }
        return intersectingPaths;

    }
    
    
    public Coordinates[] getEndTilesCoordinates(){
        return endTilesCoordinates;
    }
    
    public Coordinates[] getItemCoordinates(){
        return itemCoordinates;
    }
    
    public HashMap<Coordinates,GameItem> getCoordinates_ItemsMap(){
        return coordinatesItemsMap;   
    }
    
    public boolean agentWon(){
        if(mainPlayer.getCoordinates().equals(enemy[0].getCoordinates())){
            return true;
        }
        return false;
    }
    
    public boolean playerWon(){
        for (Coordinates c : endTilesCoordinates) {
            if(mainPlayer.getCoordinates().equals(c))
                return true;
        }
        return false;
    }
    
    public void setPlayerHidden(boolean ph){
        playerIsHidden = ph;
    }
    
    public boolean playerIsHidden(){
        return playerIsHidden;
    }
    
    public void setDummyPlayer(Player player){
        dummyPlayer = new Player(player);
    }
    

}
