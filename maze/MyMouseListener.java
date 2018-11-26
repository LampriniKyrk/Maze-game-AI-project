/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

/**
 *
 * @author ASUS
 */
public class MyMouseListener implements MouseListener{
    
    //might need maze?
    GameRules rules;
    
    public MyMouseListener(){
        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        JPanel cell = (JPanel) e.getSource(); //get clicked cell
        Coordinates cellCoordinates = GUI.cellsCoordinates.get(cell); //get its coordinates
        //MUST logic
        System.out.println("YAY you clicked on :"+cellCoordinates.y+" ,"+cellCoordinates.x);  
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
