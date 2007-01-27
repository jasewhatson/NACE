import java.util.ArrayList;
/*
 * NaceAI.java
 *
 * Created on December 21, 2006, 1:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author jason
 */
public class NaceAI {
  
   private static int AIChossenRow;
   private static int AIChossenColumn;
   private static ArrayPositions ap = new ArrayPositions();
   
   public static int[] getChossenSpot(){
     return new int[]{AIChossenRow,AIChossenColumn};
   }
    
    /*Selects all the spots avaliable for the ai to choose*/
    public static ArrayList getNonSelectedSpots(int[][] Clicked){
        
        //Creates an arraylist a and a normal array
        ArrayList a = new ArrayList();
        int[] twolenghtarray;
        
        //Loop through the 2d array
        for (int R=0; R < Clicked.length; R++){
            for (int C=0; C < Clicked[0].length; C++){
                //If a spot hasnt been taken
                if (Clicked[R][C] == -1){
                    //New array with a length if 2
                    twolenghtarray = new int[2];
                    twolenghtarray[0] = R;   //Adds the row to zeroth index
                    twolenghtarray[1] = C; //Adds row to first index
                    a.add(twolenghtarray);
                }
            }
        }
        
        //int[] y = (int[])a.get(0); //Kept fot testing
        
        System.gc(); //Clean up all the crappy lost referanced twolenghtarray's because java is crappy
        return a; //Returnts the arraylist
    }
    
    public static int[] runEasyAI(ArrayList nonSelectedSpots){
         //Generates a random index
            int RandomIndex = 0 + (int) (Math.random() * nonSelectedSpots.size());
            return (int[]) nonSelectedSpots.get(RandomIndex);
    }
    
    public static void runMediumAi(int PlayerChosenRow,int PlayerChosenColumn,
    int ConnectionLength, int[][] Clicked){
      ap.setPosition(PlayerChosenRow,PlayerChosenColumn);
      ap.setConectionLenght(4);
      
         if (ap.checkNorth(Clicked,0) + ap.checkSouth(Clicked,0)  >= 3 + 1){
            //System.out.println("HERRRE");
            pickMediumAiSpot(PlayerChosenRow,
            PlayerChosenColumn,"NS",ConnectionLength,Clicked);
            return;

          } /*else if (ap.checkEast(Clicked,0) + ap.checkWest(Clicked,0)  >= 3){          
            pickMediumAiSpot(PlayerChosenRow,
            PlayerChosenColumn,"EW",ConnectionLength,Clicked);
            return;

          } else if (ap.checkNorthEast(Clicked,0) + ap.checkWestSouth(Clicked,0) >= 3){
            pickMediumAiSpot(PlayerChosenRow,
            PlayerChosenColumn,"NEWS",ConnectionLength,Clicked);
            return;

          } else if (ap.checkNorthWest(Clicked,0) + ap.checkEastSouth(Clicked,0) >= 3){
            pickMediumAiSpot(PlayerChosenRow,
            PlayerChosenColumn,"NWES",ConnectionLength,Clicked);
            return;

          }*/
      int[] RandomSpot  = runEasyAI(getNonSelectedSpots(Clicked));
      AIChossenRow = RandomSpot[0];
      AIChossenColumn = RandomSpot[1];
    }
    
    private static boolean findBlankSpotOnAxis(String Axis,int AxisPos,int StartPos,int[][] Clicked){
      if (Axis.equals("Y")){
        System.out.println("In the f axis");
        for (int Pos = StartPos;Pos > 0;Pos--){
          System.out.println("In the loop");
          if (Clicked[Pos][AxisPos] == -1){
            
            AIChossenRow = Pos;
            AIChossenColumn = AxisPos;
            return true;
          }
        }       
      }else if (Axis.equals("X")){
        
      }
      
      return false;
    }
    
    private static void pickMediumAiSpot(int NearlyWonRow,int NearlyWonColumn,
    String NearlyWonDirection,int ConnectionLenght,int[][] Clicked){
      if (NearlyWonDirection != ""){
          System.out.println("Nearly won ns with: R " +  NearlyWonRow + " c " + NearlyWonColumn);
          
          
          if (NearlyWonDirection.equals("NS")){
            if (findBlankSpotOnAxis("Y",NearlyWonColumn,NearlyWonRow,Clicked) == true){
              return;
            }
          }
          /*if (NearlyWonDirection.equals("NS")){
            try{ 
              System.out.println("Checking is spot " + (NearlyWonRow + 1) + NearlyWonColumn);
              if (Clicked[NearlyWonRow + 1][NearlyWonColumn] == -1){
                AIChossenRow = NearlyWonRow + 1;
                AIChossenColumn = NearlyWonColumn;
                return;
              }
            }catch (java.lang.ArrayIndexOutOfBoundsException ex){};
            
            try{
              System.out.println("Checking is spot " + (NearlyWonRow - 3) + " "+ NearlyWonColumn);
              if (Clicked[NearlyWonRow - 3][NearlyWonColumn] == -1){
                AIChossenRow = NearlyWonRow - 3;
                AIChossenColumn = NearlyWonColumn;
                return;
              }
            }catch (java.lang.ArrayIndexOutOfBoundsException ex){};    
          }*/
      }
      System.out.println("Random");
      int[] RandomSpot  = runEasyAI(getNonSelectedSpots(Clicked));
      AIChossenRow = RandomSpot[0];
      AIChossenColumn = RandomSpot[1];
    }
    
    public static int[] runMediumAI2(int[][] Clicked){
      String NearlyWonDir = "";
      int NearlyWonR = 0;
      int NearlyWonC = 0;
      
      int[] ChossenSpot = new int[2];
      ArrayPositions ap = new ArrayPositions();
      //Sets every thing to minus -1//TODO: delete this after fixing line 471
      for (int R=0; R < Clicked.length; R++){
        for (int C=0; C < Clicked[0].length; C++){
          ap.setPosition(R,C);
          ap.setConectionLenght(4);
          
          if (R ==5 && C ==5){
            System.out.println("Bottom right");
            if (Clicked[R][C] == 0){
              System.out.println("It is yours");
            }
          }
          
          if (ap.checkNorth(Clicked,0) + ap.checkSouth(Clicked,0)  >= 3 + 1){
            //System.out.println("HERRRE");
            NearlyWonDir = "NS";
            NearlyWonR = R;
            NearlyWonC = C;
            break;
          } else if (ap.checkEast(Clicked,0) + ap.checkWest(Clicked,0)  >= 3){          
            NearlyWonDir = "EW";
            NearlyWonR = R;
            NearlyWonC = C;
            break;
          } else if (ap.checkNorthEast(Clicked,0) + ap.checkWestSouth(Clicked,0) >= 3){
            NearlyWonDir = "NEWS";
            NearlyWonR = R;
            NearlyWonC = C;
            break;
          } else if (ap.checkNorthWest(Clicked,0) + ap.checkEastSouth(Clicked,0) >= 3){
            NearlyWonDir = "NWES";
            NearlyWonR = R;
            NearlyWonC = C;
            break;
          }
        }
      }
      
      if (NearlyWonDir != ""){
          System.out.println("Nearly won ns with: R " +  NearlyWonR + " c " + NearlyWonC);
          if (NearlyWonDir.equals("NS")){
            try{ 
              System.out.println("Checking is spot " + (NearlyWonR + 1) + NearlyWonC);
              if (Clicked[NearlyWonR + 1][NearlyWonC] == -1){
                ChossenSpot[0] = NearlyWonR + 1;
                ChossenSpot[1] = NearlyWonC;

                return ChossenSpot;
              }
            }catch (java.lang.ArrayIndexOutOfBoundsException ex){};
            
            try{
              System.out.println("Checking is spot " + (NearlyWonR - 3) + " "+ NearlyWonC);
              if (Clicked[NearlyWonR - 3][NearlyWonC] == -1){
                ChossenSpot[0] = NearlyWonR - 3;
                ChossenSpot[1] = NearlyWonC;

                return ChossenSpot;
              }
            }catch (java.lang.ArrayIndexOutOfBoundsException ex){};
             
            /*}else if (Clicked[NearlyWonR][NearlyWonC - 3] == -1){
              ChossenSpot[0] = NearlyWonR;
              ChossenSpot[0] = NearlyWonC - 3;
              return ChossenSpot;
            }else{
              //Pick random spot on row
            }*/
          
          }
      }
      System.out.println("Returning Random");
      return runEasyAI(getNonSelectedSpots(Clicked));
      
    } 
    
}
