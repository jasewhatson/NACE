/**------------------------------------------
 *Author: Jason Whatson
 *Date: 29th June 2006
 *Purpose:Provide functionality that checks if there are any connections in a 2d array. 
 *It then returns the length of the connection in the particular direction.
 -------------------------------------------*/
public class ArrayPositions {
    
    /*The length that needs to be connected, the checking
    the direction will be made for the length of the connection*/ 
    int ConnectionLenght; 
   
    int x; //The horizontal position clicked
    int y; //The Vertical position clicked
    
    /** Creates a new instance of ArrayPositions */
    public ArrayPositions(){}
        
    public ArrayPositions (int length, int x, int y) {
        this.ConnectionLenght = length;
        this.x = x;
        this.y = y;         
    }
    
    //Sets connection length
    public void setConectionLenght(int length){
        this.ConnectionLenght = length;
    }
    
    //Sets the position to check from
    public void setPosition(int y, int x){
        this.x = x;
        this.y = y;
    }
    
    /**
     *Checks for connections in the north
     */
    public int checkNorth(int[][] ArraytoCheck, int Player){
        
        //The place to go to is going down on the y axis so we take away the connection length neeeded
        int PlaceToGoTo = this.y - (this.ConnectionLenght);
        int CurrentPos = this.y;
        int Connected=0;
        
        //While where we want to go to is less than our current pos
        while (PlaceToGoTo <= CurrentPos){
            //Needed if we check a position not on the board
            //TODO: think about returning if its of the board
            try{
                //If the cell isnt taken or dosnt belong to the current player return the ammount of positions connected
                if (ArraytoCheck[CurrentPos][this.x] == -1 || ArraytoCheck[CurrentPos][this.x] != Player){
                    return Connected;        
                //Else increment the connection
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               //Move up the y axis
               CurrentPos--;
           }
        }
        //If we get to the end return connected
        return Connected;
    }
    
    public int checkSouth(int[][] ArraytoCheck, int Player){

        int PlaceToGoTo = this.y + (this.ConnectionLenght);
        int CurrentPos = this.y;
        int Connected=0;
     
        while (PlaceToGoTo >= CurrentPos){

            try{
            
                if (ArraytoCheck[CurrentPos][this.x] == -1 || ArraytoCheck[CurrentPos][this.x] != Player){
                    
                    return Connected;
                }else{
                    Connected++;
                }
            }
            catch (ArrayIndexOutOfBoundsException ex){
            }finally{
                CurrentPos++;
            }
        }
        return Connected;
    }
     
    public int checkWest(int[][] ArraytoCheck, int Player){

        int PlaceToGoTo = this.x - (this.ConnectionLenght);
        int CurrentPos = this.x;
        int Connected=0;
     
        while (PlaceToGoTo <= CurrentPos){

            try{
            
                if (ArraytoCheck[this.y][CurrentPos] == -1 || ArraytoCheck[this.y][CurrentPos] != Player){
                    return Connected;        
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               CurrentPos--;
           }
        }
        return Connected;
    }
    
    public int checkEast(int[][] ArraytoCheck, int Player){

        int PlaceToGoTo = this.x + (this.ConnectionLenght);
        int CurrentPos = this.x;
        int Connected=0;
     
        while (PlaceToGoTo >= CurrentPos){

            try{
            
                if (ArraytoCheck[this.y][CurrentPos] == -1 || ArraytoCheck[this.y][CurrentPos] != Player){
                    return Connected;
                }else{
                    Connected++;
                }
            }
            catch (ArrayIndexOutOfBoundsException ex){
            }finally{
                CurrentPos++;
            }
        }
        return Connected;
        
    }
    //TODO: See if CurrentPos is actually needed and you can just use this.y instead
    public int checkNorthEast(int[][] ArraytoCheck, int Player){
        int PlaceToGoToXPos = this.x + this.ConnectionLenght;
        int PlaceToGoToYPos = this.y - this.ConnectionLenght;
        int CurrentXPos = this.x;
        int CurrentYPos = this.y;
          
        int Connected=0;
     
        while (PlaceToGoToXPos >= CurrentXPos && PlaceToGoToYPos <= CurrentYPos){

            try{
                if (ArraytoCheck[CurrentYPos][CurrentXPos] == -1 || ArraytoCheck[CurrentYPos][CurrentXPos] != Player){
                    return Connected;        
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               CurrentXPos++;
               CurrentYPos--;
           }
        }
        return Connected;
    }
    
    public int checkWestSouth(int[][] ArraytoCheck, int Player){
        int PlaceToGoToXPos = this.x - this.ConnectionLenght;
        int PlaceToGoToYPos = this.y + this.ConnectionLenght;
        int CurrentXPos = this.x;
        int CurrentYPos = this.y;
        int Connected=0;
     
        while (PlaceToGoToXPos <= CurrentXPos && PlaceToGoToYPos >= CurrentYPos){

            try{
                if (ArraytoCheck[CurrentYPos][CurrentXPos] == -1 || ArraytoCheck[CurrentYPos][CurrentXPos] != Player){
                    return Connected;        
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               CurrentXPos--;
               CurrentYPos++;
           }
        }
        return Connected;
    }
    
    public int checkNorthWest(int[][] ArraytoCheck, int Player){
        int PlaceToGoToXPos = this.x - this.ConnectionLenght;
        int PlaceToGoToYPos = this.y - this.ConnectionLenght;
        int CurrentXPos = this.x;
        int CurrentYPos = this.y;
        int Connected=0;
     
        while (PlaceToGoToXPos <= CurrentXPos && PlaceToGoToYPos <= CurrentYPos){

            try{
                if (ArraytoCheck[CurrentYPos][CurrentXPos] == -1 || ArraytoCheck[CurrentYPos][CurrentXPos] != Player){
                    return Connected;        
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               CurrentXPos--;
               CurrentYPos--;
           }
        }
        return Connected;
    }
    
    public int checkEastSouth(int[][] ArraytoCheck, int Player){
        int PlaceToGoToXPos = this.x + this.ConnectionLenght;
        int PlaceToGoToYPos = this.y + this.ConnectionLenght;
        int CurrentXPos = this.x;
        int CurrentYPos = this.y;

        int Connected=0;
     
        while (PlaceToGoToXPos >= CurrentXPos && PlaceToGoToYPos >= CurrentYPos){

            try{
                if (ArraytoCheck[CurrentYPos][CurrentXPos] == -1 || ArraytoCheck[CurrentYPos][CurrentXPos] != Player){
                    return Connected;        
                }else{
                    Connected++;  
                }
           }
           catch (ArrayIndexOutOfBoundsException ex){
           }finally{
               CurrentXPos++;
               CurrentYPos++;
           }
        }
        return Connected;
    }
}
