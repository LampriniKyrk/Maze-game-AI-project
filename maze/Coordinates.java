/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

/**
 *
 * @author ASUS
 */
public class Coordinates {
    public int x, y;
    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    /**
     * 
     * @param otherObject
     * @returns true if the Coordinates are equal else returns false 
     */
    @Override
    public boolean equals(Object otherObject){
        Coordinates c = (Coordinates)otherObject;
        return x == c.x && y==c.y;
    }
    
}
