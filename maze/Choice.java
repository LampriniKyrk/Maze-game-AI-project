/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maze;

/**
 *
 * @author Kyrkou Lamprini
 */
public class Choice {
    int score; //the score of the choice
    String commandsToChoice = "";
    Path pathToChoice;
    
    public Choice(int score, String ctc, Path p){
        this.score = score;
        commandsToChoice = ctc;
        pathToChoice = new Path(p);
    }
    
    public Choice(){
        pathToChoice = new Path();
    }
    
    public Choice(Choice r) {
        score = r.getScore();
        commandsToChoice = r.getCommands();
        pathToChoice = new Path(r.getPath());
    }
    
    //________________________________________Setters-Getters_______________________________________//
    
    
    public void setScore(int score){
        this.score = score;
    }
    
    public void setCommands(String commands){
        commandsToChoice = commands;
    }
    
    public void setPath(Path p){
        pathToChoice = new Path(p);
    }
    
    public int getScore(){
        return score;
    }
    
    public String getCommands(){
        return commandsToChoice;
    }
    
    public Path getPath(){
        return pathToChoice;
    }
    
    //__________________OTHER FUNCTIONS___________________________//
    
    public Path addToPath(Cell c){
        pathToChoice.addCellToPath(c);
        return pathToChoice;
    }
    
    public void addtoCommands(String command){
        commandsToChoice = commandsToChoice+"-"+command;
    }
}
