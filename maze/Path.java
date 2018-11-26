/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.util.ArrayList;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Path {
    
    private ArrayList<Cell> cellList;
    private ArrayList<String> dirList; //direction list
    
    /**
     * Constructor
     */
    public Path(){
        cellList = new ArrayList<>();
        dirList = new ArrayList<>();
    }
    
    /**
     *Constructor with starting position
     * @param c is a cell
     */
    public Path(Cell c){
        cellList = new ArrayList<>();
        cellList.add(c);
        dirList = new  ArrayList<>();
    }
    

    /**
     *copy constructor
     * @param p is a path
     */
        public Path (Path p){
       this.cellList = new ArrayList<>( p.getCellList());
       this.dirList = new ArrayList<>(p.getDirectionList());
    }

    /**
     *
     * @returns last cell of path
     */
    public Cell getLastCell(){
        if(cellList.size()>0)
            return cellList.get(cellList.size()-1);
        return null;
    }
    
    /**
     * Direction is either n <north> , e <east> , s <south> or w <west>.
     * @returns last direction of path
     */
    public String getLastDirection(){
        if (dirList.size()>0)
            return dirList.get(dirList.size()-1);
        return null;
    }
    
    /**
     *
     * @param c - cell to be added to path
     */
    public void addCellToPath(Cell c){
        //if cell not already in path
        if(!cellList.contains(c)){
            cellList.add(c);
        }
        else{
           return;
        }
    }

    /**
     *
     * @param dir - direction to be added to path
     */
    public void addDirectionToPath(String dir){
        if(dir.equals("n")||dir.equals("e")||dir.equals("s")||dir.equals("w"))
            dirList.add(dir);
        else System.out.println("WRONG DIRECTION");
    }
    
    /**
     *
     * @param c - the cell we want to see if it is part of the path
     * @return True if path contains c, false if not.
     */
    public boolean containsCell (Cell c){
        if(cellList.contains(c)) return true;
        return false;
    }
    
    /**
     *Prints path.
     */
    public void printPath(){
        System.out.println("path ");
        for (Cell cellList1 : cellList) {
            System.out.printf("  %d - %d",cellList1.gety(),cellList1.getx());
            
        }
        System.out.println("");
    }
    
    /**
     * 
     * @returns a list of the cells that make the path
     */
    public ArrayList<Cell> getCellList(){
        return cellList;
    }
    
    /**
     * 
     * @return a list of directions 
     */
    public ArrayList<String> getDirectionList(){
        return dirList;
    }
    
    /**
     *
     * @param x axis
     * @param y axis
     * @returns true if player is on this path, else false
     */
    public int playerOnPath(int x, int y){
        int i =0;
        for (Cell c : cellList) {
            if(c.getx() == x && c.gety()==y)
                return i;
            i++;
        }
        return -1;
    }
    
    /**
     *
     * @returns the length of the path
     */
    public int getPathLength(){
        return cellList.size();
    }
    
    /**
     * 
     * @return score of a cell in the path that contains an item
     */
    public int hasItem(){
        int score=0;
        for (Cell c : cellList) {
            if (c.getItem()!=null) {
                score = c.getItem().getScore()+ c.getItem().getPlayerScore();
                score = score/2;
            }
        }
        
        return score;
    }
    
    /**
     * 
     * @param x position
     * @param y position
     * @return Path that was cut till x,y
     */
    public Path cutPath(int x, int y){
        Path newPath = new Path();
        for (Cell c : cellList) {
            newPath.addCellToPath(new Cell(c));
            //if destination is in path
            if(c.getx()==x && c.gety()==y) return newPath; 
        }
        return null;
    }
    
    /**
     * 
     * @return coordinates of end tile that belongs to the path
     */
    public Coordinates hasEndTile(){
        for (Cell c : cellList) {
            if (c.isEndTile()) return new Coordinates(c.getx(), c.gety());
        }
        return null;
    }
    
    /**
     * 
     * @param p an other path
     * @return number of tiles that belong to both paths
     */
    public int intersects(Path p){
        ArrayList<Cell> cl = p.getCellList();
        int counter = 0;        
        for (int i = 0; i < cellList.size(); i++) {
            for (int j = 0; j < cl.size(); j++) {
                //if they have at least one cell in common then they intersect
                if(cellList.get(i).equals(cl.get(j))){
                   counter++;
                }
            }
        }
        return counter;
    }
    
    
    @Override
    public boolean equals(Object otherObject){

        Path path  = (Path) otherObject;
        if(this.cellList.size()!=path.getCellList().size()) return false;
        for (int i = 0; i < cellList.size(); i++) {
            //every cell must be the same , one by one
            
            if(!cellList.get(i).equals(path.getCellList().get(i))) return false;            
        }
        return true;
    }
   
}
