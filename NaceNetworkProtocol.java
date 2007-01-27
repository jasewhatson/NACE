/*
 * File: NaceNetworkProtocol.java
 * Created on: July 20, 2006, 12:37 PM
 * Author: Jason Whatson
 * Description: Parses diffrent types of Nace network messages. 
 */

public class NaceNetworkProtocol {
    
    public static final int MSGTYPECHAT = 0; //Player chat msg prefix
    public static final int MSGTYPEMOVE = 1; //Player move msg prefix
    public static final int MSGTYPEAPPMSG = 2; //Application message prefix
    public static final int APPMSGSETSETINGS = 3; //Game settings msg prefix
    
    //Takes any kind of Nace network msg and returns the type of message it is
    //see above
    public static int getMessageType(String msg){
        return Integer.parseInt(msg.split(",")[0]);
    }
    
    //Returns the text from a player chat msg
    public static String getChatMsg(String msg){
        return msg.split(",")[1];
    }
    
    //Returns the spot where the opposition was made their move to
    public static int[] getPlayerMove(String msg){
        int[] position = new int[2];
        position[0] = Integer.parseInt(msg.split(",")[1]);
        position[1] = Integer.parseInt(msg.split(",")[2]);
        return position;
    }
    
    /*
     *@return An array of game settings from the server.
     *First index - Board width
     *Second index - Board height
     *Third index - Connection length 
     */
    public static int[] getPlayerSettings(String msg){
        int[] settings = new int[3];
        settings[0] = Integer.parseInt(msg.split(",")[1]);
        settings[1] = Integer.parseInt(msg.split(",")[2]);
        settings[2] = Integer.parseInt(msg.split(",")[3]);
        return settings;
    }
    
    public static String getApplicationMsg(String msg){
      System.out.println("returning " + msg.split(",")[1]);
      return msg.split(",")[1];
    }
}
