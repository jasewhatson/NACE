/**------------------------------------------
 *Author: Jason Whatson
 *Date: 29th June 2006
 *Purpose: Make a fun type board game where
 *the players have many options about the game
 *play. Gameplay can be single or mulitplayer
 *Version:0.6
 * -------------------------------------------*/
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.*;

//TODO: Fix bug that stops the winner being shown when IN GAME is set to AI

public class NoughtsAndCrossesElite extends JFrame implements ActionListener, 
                                            Runnable, KeyListener,Connectable{
    
    //The iteration we check for network messages
    private static final int CHECKFORMSGWAIT = 1000;
    //Default hosting port
    private static final int DEFAULTHOSTINGPORT = 4444;
    
     /*----------------------
     *Gameplay vars
     *---------------------*/
          
    //New instance of the form frmSettings
    private frmSettings s1 = new frmSettings();
    
    //Get a previous saved height from the settings
    private int ROWS = s1.getBoardHeight();
    private int COLUMNS = s1.getBoardWidth();
    
    //A 2d array of visual buttons, these are the board squares
    private JButton[][] buttons;
    
    //2d array of ints. Holds the status of the corresponding button
    // -1 = unclicked , 0 = player one , 1 = player 2
    private int[][] Clicked;
    private Boolean PlayerOne = true;
    
    //Player number that corresponds with which player
    private final static int PLAYERONE = 0; //RED
    private final static int PLAYERTWO = 1; //BLUE
    
    //New instance of ArrayPositionsClass
    private ArrayPositions ap = new ArrayPositions();
    
    //Holds the length that needs to be connected for a win
    private int ConectionLenght;
    
    /*Keeps track of how many turns are made and which player is makeing the turn.
    (TODO: Make Player and TurnsMade do the same job) <- These need to be seperate
    if I want it to alternate in which player has first move between games*/
    private int Player,TurnsMade;
    
    //Stores what type of game is being played, Networked, AI or multi local
    private String GameType ="";
    
    /*----------------------
     *GUI STUFF
     *---------------------*/
    //Chat send button
    private JButton send;
    //The menu bar
    private JMenuBar mnubarMainMnuBar = new JMenuBar();
    
    //The menu headings
    private JMenu mnuGame = new JMenu("Game");
        private JMenuItem itmGameNew = new JMenuItem("New");
        private JMenuItem itmExit = new JMenuItem("Exit"); 
        private JMenuItem itmHost = new JMenuItem("Host");
        private JMenuItem itmConnect = new JMenuItem("Connect");
        //Game settings: Sub menu of game
        private JMenu mnuPlayerSettings = new JMenu("Player settings");
            //Player settings options
            private String[] PlayerSettings = {"2 player","Easy AI"};
    
    private JMenu mnuSettings = new JMenu("Settings");
        private JMenuItem itmGameSettings = new JMenuItem("Game settings");
    
    private JMenu mnuHelp = new JMenu("Help");
        private JMenuItem itmHelpAbout = new JMenuItem("About");
        private JMenuItem itmHelpHowToPlay = new JMenuItem("How to play");
   
    private ButtonGroup bgpPlayerSettings = new ButtonGroup();
    
    private JRadioButtonMenuItem itmPlayerSettings[];
    
    private JLabel lblYourTurn = new JLabel("Opponents turn");
    
     /*----------------------
      *NET CODE STUFF
      *---------------------*/
    //Holds whether we are a server or client or neither.
    //Server = 1, Client =2 and Neither = 0
    private int server = 0;
    /*If its a networked game, this will be true when its your turn. If its not
     a network game its ilrevelent*/
    private boolean YourTurn = false;
    
    private Socket kkSocket;
    
    private ServerSocket serverSocket; //Socket to the server
    private Socket clientSocket = null;
    
    private PrintWriter ServerOut;
    private BufferedReader ServerIn;
    
    private PrintWriter ClientOut;
    private BufferedReader ClientIn;
    
    private JDialogCancel dc;
    
    /*----------------------
     *NET CHAT STUFF
     *---------------------*/
    private JTextField msg;
    private JTextArea chatbox;
    
    /*Constructor, sets up allot of menuing GUI mainly*/
    public NoughtsAndCrossesElite(){
      
      //Default game play type
      this.GameType = "AI";
      
      //Loads the play settings options
      this.itmPlayerSettings = new JRadioButtonMenuItem[PlayerSettings.length];
      
      //Player settings sub menu
      for (int i=0; i < PlayerSettings.length; i++){
        itmPlayerSettings[i] = new JRadioButtonMenuItem(PlayerSettings[i]);
        bgpPlayerSettings.add(itmPlayerSettings[i]);
        itmPlayerSettings[i].addActionListener(this);
        mnuPlayerSettings.add(itmPlayerSettings[i]);
      }
      
      //Allot of menu stuff
      this.mnubarMainMnuBar.add(mnuGame);
      this.mnuGame.add(itmGameNew);
      this.mnuGame.add(mnuPlayerSettings);
      
      this.mnuGame.add(itmHost);
      this.mnuGame.add(itmConnect);
      
      this.mnuGame.add(itmExit);
      
      //Set up listeners for the menus
      itmHost.addActionListener(this);
      itmConnect.addActionListener(this);
      
      itmGameNew.addActionListener(this);
      itmExit.addActionListener(this);
      
      itmHelpAbout.addActionListener(this);
      itmHelpHowToPlay.addActionListener(this);
      
      this.mnubarMainMnuBar.add(mnuSettings);
      this.mnuSettings.add(this.itmGameSettings);
      this.itmGameSettings.addActionListener(this);
      this.mnubarMainMnuBar.add(mnuHelp);
      this.mnuHelp.add(itmHelpAbout);
      this.mnuHelp.add(itmHelpHowToPlay);
      
      //Add the menu to us
      this.setJMenuBar(mnubarMainMnuBar);
      
      //Reset / start the game
      reset();
    }
    
    /**
     *===========================
     *|run()
     *===========================
     *While running looks for messages set to the application from a network.
     *Reacts to the message acordingly Eg, chat message would post to chat window.
     *Player move messsge would make the move etc
     */
    public void run(){
      
      //Run the thread while we are in a networked game
      while (this.server != 0){
        
        //Sleep the thread because we only want to check for msgs every so often
        try {
          Thread.currentThread().sleep(CHECKFORMSGWAIT);
        } catch (InterruptedException ex) {ex.printStackTrace();}
        
        //If we are the server
        if (this.server == 1){
          this.messagesToServer();
          //Else, we are the client
        }else if (server == 2){
          this.messageToClient();
        }
      }
      
    }
    
    /**
     *Called when a user fires a SWING/AWT event
     **/
    public void actionPerformed(ActionEvent e){
      //Send - Chat msg is clicked
     if (this.send == e.getSource()){
        sendChatMessage();
      //If a button is clicked, find that button and pass it to turn
     }else if (e.getSource().getClass().toString().equals("class javax.swing.JButton")){ //<- Worst hack ever
        for (int R=0; R < ROWS; R++){
          for (int C=0; C < COLUMNS; C++){
            if (e.getSource() == buttons[R][C]){
              turn(R,C);
              break;
            }
          }
        }
      //How to play is clicked
      }else if (this.itmHelpHowToPlay == e.getSource()){
        frmHowToPlay htp = new frmHowToPlay();
        htp.setVisible(true);
      //About is clicked
      }else if (this.itmHelpAbout == e.getSource()){
        frmAbout fa = new frmAbout();
        fa.setVisible(true);
      //Game settings is clicked
      }else if (this.itmGameSettings == e.getSource()){
        s1.setVisible(true);
      //Exit is clicked
      }else if (this.itmExit == e.getSource()){
        System.exit(0);
      //New game is clicked
      }else if (itmGameNew == e.getSource()){
        this.reset();
      //Host a server is clicked
      }else if (this.itmHost == e.getSource()){
        this.hostServer();
      //Connect is clicked
      } else if (this.itmConnect == e.getSource()){
        this.connectToServer();
      //Easy AI is clicked
      }else if (e.getActionCommand().equals("Easy AI")){
        this.server = 0;
        GameType = "AI";
        reset();
      //2 players is clicked
      }else if (e.getActionCommand().equals("2 player")){
        this.server = 0;
        GameType = "2Player";
        reset();
      }
    }
    
    /**
     *=======================
     *|turn(int R, int C)
     *========================
     *Called whenever a spot is clicked passing the Row (R) and Column (C)
     *values coresponding to the clicked position  
     */
    private void turn(int R, int C){
      //Only allow a turn to be made if the square is not already taken
      if (Clicked[R][C] == -1){
        
        //Set the ap position to the position clicked
        ap.setPosition(R,C);
        
        //String GameType = "AI"; //<--Testing
        
        //If the player is playing agains a computer
        if (GameType.equals("AI")){
          Clicked[R][C] = PLAYERONE; //Set where they clicked to them
          buttons[R][C].setBackground(Color.RED); // Change the button colour to red
          this.Player++; //Increment the player
          this.TurnsMade++; //Increment the turns
          calculateWinners(); //See if that move made was a winning move
          NaceAI.runMediumAi(R,C,this.ConectionLenght,this.Clicked);
          runAI("Easy"); //If it wasnt a winning move run the AI
   
        //else if the players are playing 2 player
        }else if (GameType.equals("2Player")){
          
          //If its player ones turn
          if (PlayerOne == true){
            Clicked[R][C] = PLAYERONE;
            buttons[R][C].setBackground(Color.RED);
            PlayerOne = false;
            //Else if its player twos turn
          }else{
            Clicked[R][C] = PLAYERTWO;
            buttons[R][C].setBackground(Color.BLUE);
            PlayerOne = true;   
          }
          
          Player++;
          this.TurnsMade++;
          //Check to see if the turn made was a winner
          calculateWinners();
        
        //Else if we are playing a networked game and it is our turn
        }else if (GameType.equals("Networked") && this.YourTurn == true){
          this.makeNetworkMove(R,C);         
        }
      } 
    }
    
    /**
     **=======================
     *|calculateWinners()
     *========================
     *Check to see if there is a winner by checking if
     *spots taken are in a row and of the player
     */
    private void calculateWinners(){
      int CP; //Current player
      //Work out the current player
      CP = Player % 2 == 0 ? 1 : 0;
      
      //Check of any vertical connections equal to or more than than the required lenght (connectionLength)
      if (ap.checkNorth(Clicked,CP) + ap.checkSouth(Clicked,CP)  >= ConectionLenght + 1){
        showWinner();
        reset();
        //Check of any Horizontal connections equal to or more than than the required lenght (connectionLength)
      } else if (ap.checkEast(Clicked,CP) + ap.checkWest(Clicked,CP)  >= ConectionLenght + 1){
        showWinner();
        reset();
        //Check of any '/' connections equal to or more than than the required lenght (connectionLength)
      } else if (ap.checkNorthEast(Clicked,CP) + ap.checkWestSouth(Clicked,CP) >= ConectionLenght + 1){
        showWinner();
        reset();
        //Check of any '\' connections equal to or more than than the required lenght (connectionLength)
      } else if (ap.checkNorthWest(Clicked,CP) + ap.checkEastSouth(Clicked,CP) >= ConectionLenght + 1){
        showWinner();
        reset();
      }
      
      //If all spots are taken the game is a draw
      if (TurnsMade >= COLUMNS * ROWS){
        JOptionPane.showMessageDialog(null,"Its a draw!");
        reset();
      }
    }
    
     /**
     **=======================
     *|calculateWinners()
     *========================
     *Check to see if there is a winner by checking if
     *spots taken are in a row and of the player
     */
    private void reset(){
      
      //Only allow changes to the game settings if we are not in a network game?
      if (this.server == 0){
        //Sets up the connection lenght
        ConectionLenght = s1.getBoardConnectionLength();
        ap.setConectionLenght(ConectionLenght - 1);//2
        
        //Sets the chosen ammount of rows and columns for the board
        ROWS = s1.getBoardHeight();
        COLUMNS = s1.getBoardWidth();
      }
      
      //Sets up the 2d array of clicked objects
      Clicked = new int[ROWS][COLUMNS];
      
      //Gets the container
      Container cn = this.getContentPane();
      //Removes anything existing in it
      cn.removeAll();
      
      //Creates a new border layout - Top most layout
      BorderLayout layout = new BorderLayout();
      //Creates a new text field - The spot where the player enters his/her chat msg
      msg = new JTextField();
      //Creates a new Send button - This will send the chat msg
      send = new JButton("Send message");
      
      msg.addKeyListener(this); //Donno if this is needed
      
      //Sets our container (windows) to use the borderlayout
      cn.setLayout(layout);

      //Creates a layout for our playing board - This is a grid layout
      GridLayout board = new GridLayout(ROWS,COLUMNS);
      
      //Creates a layout for our network chatting
      BoxLayout Chat_Area = new BoxLayout(cn,BoxLayout.Y_AXIS);
      
      //Creates a new panel - This will host our, network chat area
      JPanel pnlSouth = new JPanel();

      //If we are in a networked game
      if (this.server != 0){
        
        //Create a new a scroll pane - This will give us a view on the chat area
        JScrollPane chatpain = new JScrollPane();
        //Create a text area, this is the where the chat msg's (and network msgs go)
        chatbox = new JTextArea();
        
        chatbox.setEditable(false); //Make it not editable
        chatbox.setLineWrap(true);  //Make it line wrap
        chatbox.setWrapStyleWord(true); //Make it line wrap on words only
        
        
        //Set the view on the chatbox text area
        chatpain.setViewportView(chatbox);
        
        //Set to view 5 rows of text at once
        chatbox.setRows(5);
        
        //Set our south panel (chat stuff panel) to a vbox layout
        pnlSouth.setLayout(new BoxLayout(pnlSouth,BoxLayout.Y_AXIS));
        
        //Create a new borderlayout this is so the 'Send' button's width is the whole width
        BorderLayout bl = new BorderLayout();
        
        //bl.addLayoutComponent(send,null);
        
        //We need to make a new panel just for the send button now
        JPanel ct = new JPanel();
        JPanel ct2 = new JPanel();

        //Set the pannels layout to our border layout
        ct.setLayout(bl);
        ct2.setLayout(new BorderLayout());
        
        //Add the send button to the layout
        ct.add(send);
        ct2.add(this.lblYourTurn);
       
        //Add stuff to network chat area stuff
        pnlSouth.add(ct2);
        pnlSouth.add(chatpain); //Add chat windows
        pnlSouth.add(msg); //Add msg field
        pnlSouth.add(ct); //Add send button
      }
      
      //So we can listen to send events
      send.addActionListener(this);
      
      //Not to sure what this does
      Chat_Area.layoutContainer(cn);
      
      //Creates a new panel for our game board
      JPanel pnlCenter = new JPanel(board);
      
      //Adds the board to the center of our window
      cn.add(pnlCenter, BorderLayout.CENTER);
      //Adds the chat network stuff to the south of our window
      cn.add(pnlSouth, BorderLayout.SOUTH);
      
      
      buttons = new JButton[ROWS][COLUMNS];
      
      //Sets every thing to minus -1//TODO: delete this after fixing line 471
      for (int R=0; R < ROWS; R++){
        for (int C=0; C < COLUMNS; C++){
          Clicked[R][C] = -1;
        }
      }
      
      //Creates the buttons and adds them to action listener
      for (int R=0; R < ROWS; R++){
        for (int C=0; C < COLUMNS; C++){
          buttons[R][C] = new JButton();
          pnlCenter.add(buttons[R][C]);
          buttons[R][C].addActionListener(this);
        }
      }
      
      //Sets all the values in clicked to -1 TODO: delete this and make blank spot = 0, player1 = 1, player2 = 2
      for (int R=0; R < ROWS; R++){
        for (int C=0; C < COLUMNS; C++){
          buttons[R][C].setBackground(Color.WHITE);
          buttons[R][C].setText("");
          Clicked[R][C] = -1;
        }
      }
      
      TurnsMade = 0; //Resets turns made by both players to 0
      System.gc(); //Collects garbage - frees memory
      this.validate(); //Redraws the GUI
    }
    
    /* When called shows in a option pain whos won*/
    //TODO: If player is versing ai say computer wins
    private void showWinner(){
      if(this.GameType.equals("Networked")){
        if (this.YourTurn == false){
          JOptionPane.showMessageDialog(null,"You win!");
        }else{
          JOptionPane.showMessageDialog(null,"You loose.");
        }
        return;
      }
      
      if (Player % 2 == 0){
        String Winner = this.GameType.equals("2Player") ? "Player two" : "Computer";
        JOptionPane.showMessageDialog(null,Winner +" wins!");
      }else{
        
        JOptionPane.showMessageDialog(null,"Player one wins!");
      }
    }
    
    /*Runs the game ai*/
    private void runAI(String AiLevel) {
      
      //If the Ai is easy, the AI will pick a random spot
      if (AiLevel.equals("Easy")){
        
        //Makes a new arraylist and assigns an array of non selected spots on the board to it
        //ArrayList nonSelectedSpots = NaceAI.getNonSelectedSpots(this.Clicked);
        
        //int[] ArrayListRow = NaceAI.runEasyAI(nonSelectedSpots);
        
        //int[] ArrayListRow = NaceAI.runMediumAI(this.Clicked);
     
        int[] ArrayListRow = NaceAI.getChossenSpot();
        
        //Pulls out the int array at the index and uesings the first and second values
        Clicked[ArrayListRow[0]][ArrayListRow[1]] = PLAYERTWO; //Sets the clicked location to Computer
        buttons[ArrayListRow[0]][ArrayListRow[1]].setBackground(Color.BLUE); //Sets the location to blue
        Player++; //Increment player count
        this.TurnsMade++; //Increment turns made
        ap.setPosition(ArrayListRow[0],ArrayListRow[1]); //Set the position
        calculateWinners(); //Checks to see if the AI has won
        
        //TODO: make it only calculate winners when more or the ammount of the connecting length clicks have been made
      }
    }
    
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    
    //This is so if enter is pressed it sends a chat message
    public void keyPressed(KeyEvent e) {
      //If the key pressed is enter
      if (e.getKeyCode() == KeyEvent.VK_ENTER){
        sendChatMessage();
      }
    }
    
    /**
     *=======================
     *|sendChatMessage()
     *========================
     *Sends a chat text message to the players opponent. This is for network
     *games
     */
    private void sendChatMessage() {
      //Get the players network name
      String NetworkName = s1.getNetworkName();
      /*If the player hasnt specified a network name we will just call him 
      opponent by default*/
      if (NetworkName == ""){NetworkName = "Opponent";}
      
      //Send the message through our connection
      if (this.server == 1){
        this.ServerOut.println("0," + NetworkName + ": " + msg.getText());
      }else if (this.server ==2){
        this.ClientOut.println("0," + NetworkName + ": " + msg.getText());
      }
      
      //If we dont have a name we need to set the default name to 'you'
      //before we put the text of what the player said to their chat pain
      if (NetworkName.equals("Opponent")){NetworkName = "You";}
      
      //Show what the player said in the chat pain
      this.chatbox.setText(this.chatbox.getText() + NetworkName + ": " + msg.getText() + "\n");
      //Set the msg to send box to nothing, for next message
      this.msg.setText("");
    }
    
  
    private void connectToServer(){
      
      int HostingPort;
      
      if(this.s1.getHostingPort().equals("")){
        HostingPort = DEFAULTHOSTINGPORT;
      }else{
        HostingPort = Integer.parseInt(this.s1.getHostingPort());
      }
      
      String ipAdress = JOptionPane.showInputDialog(null, "Hosts ip");
      try {
        kkSocket = new Socket(ipAdress, HostingPort);
      } catch (UnknownHostException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      try {
        
        ClientOut = new PrintWriter(kkSocket.getOutputStream(), true);
        ClientIn = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        String fromServer;
        server = 2;
        
        ClientOut.println(NaceNetworkProtocol.MSGTYPEAPPMSG + ",Client has connected");
        
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      this.GameType = "Networked";
      
      Thread tc = new Thread(this);
      
      tc.start();
      
    }
    
    /**
     *=======================
     *|messageToServer()
     *========================
     *Reads a message to the server
     */
    private void messagesToServer(){
      //Holds a String message read from the network
      String NetworkMessage;
      
      try {
            //If there is a line sent in from the client for us to read
            if ((NetworkMessage = ServerIn.readLine()) != null) {
              
              //If the client has sent a chat type message
              if(NaceNetworkProtocol.getMessageType(NetworkMessage) ==
                      NaceNetworkProtocol.MSGTYPECHAT){
                //Send the clients chat message to the chatbox
                this.chatbox.setText(chatbox.getText()
                + NaceNetworkProtocol.getChatMsg(NetworkMessage) + "\n");
                
                chatbox.setCaretPosition(chatbox.getText().length());
            
                //Else if its a opposition move
              } else if(NaceNetworkProtocol.getMessageType(NetworkMessage)
              == NaceNetworkProtocol.MSGTYPEMOVE){
                //Holds the place where the client has moved to
                int[] Place = new int[2];
                //Retrives the client move for the NetworkMessage
                Place = NaceNetworkProtocol.getPlayerMove(NetworkMessage);
                //Sets the place where the client has chossen to theirs
                Clicked[Place[0]][Place[1]] = PLAYERTWO;
                //Visually makes it theirs
                buttons[Place[0]][Place[1]].setBackground(Color.BLUE);
                //Increment stuff
                this.Player++; this.TurnsMade++;
                ap.setPosition(Place[0],Place[1]);
                //The client has made their turn. Its now the servers turn
                YourTurn = true;
                
                this.lblYourTurn.setText("Your turn");
                
                //See if the client has made a winning move
                calculateWinners();
              }else if(NaceNetworkProtocol.getMessageType(NetworkMessage) == 
              NaceNetworkProtocol.MSGTYPEAPPMSG){
                this.chatbox.setText(chatbox.getText()
                + NaceNetworkProtocol.getApplicationMsg(NetworkMessage) + "\n");
                chatbox.setCaretPosition(chatbox.getText().length());
              }
            }
            
          } catch (IOException ex) { ex.printStackTrace();}
    }
    
    /**
     *=======================
     *|messageToClient()
     *========================
     *Sends a chat message to the client
     */
    private void messageToClient(){
      
      //Holds a String message read from the network
      String NetworkMessage;
      
      try {
            //If there is a line sent in from the server for us to read
            if ((NetworkMessage = ClientIn.readLine()) != null){
              //If the server has sent the playing board /game rules settings
              if(NaceNetworkProtocol.getMessageType(NetworkMessage) == 
              NaceNetworkProtocol.APPMSGSETSETINGS){
                //Holds the game settings sent by the server
                int[] Settings;
                //Retrives the game settings
                Settings = NaceNetworkProtocol.getPlayerSettings(NetworkMessage);
                this.ROWS = Settings[0];
                this.COLUMNS = Settings[1];
                this.ConectionLenght = Settings[2];
                reset();
                
                this.chatbox.setText(chatbox.getText() + 
                "Server has sent game settings: Board width = "
                + Settings[1] + ", Board height = " + Settings[0] + 
                " and Win connection length = " + Settings[2] + "\n");
                
                chatbox.setCaretPosition(chatbox.getText().length());
                
              }else if(NaceNetworkProtocol.getMessageType(NetworkMessage) == NaceNetworkProtocol.MSGTYPECHAT){
                this.chatbox.setText(chatbox.getText() + NaceNetworkProtocol.getChatMsg(NetworkMessage) + "\n");
                chatbox.setCaretPosition(chatbox.getText().length());
              
              }else if(NaceNetworkProtocol.getMessageType(NetworkMessage) == NaceNetworkProtocol.MSGTYPEMOVE){
                int[] Place = new int[2];
              
                Place = NaceNetworkProtocol.getPlayerMove(NetworkMessage);
              
                Clicked[Place[0]][Place[1]] = PLAYERONE;
                buttons[Place[0]][Place[1]].setBackground(Color.RED);
                Player++;
                this.TurnsMade++;
                ap.setPosition(Place[0],Place[1]);
                YourTurn = true;
                this.lblYourTurn.setText("Your turn");
                calculateWinners();
              
              }else if(NaceNetworkProtocol.getMessageType(NetworkMessage) == 
              NaceNetworkProtocol.MSGTYPEAPPMSG){
                this.chatbox.setText(chatbox.getText()
                + NaceNetworkProtocol.getApplicationMsg(NetworkMessage) + "\n");
                chatbox.setCaretPosition(chatbox.getText().length());
              }
            }
          } catch (IOException ex) {ex.printStackTrace();}
    }
    
     /**
     *=======================
     *|makeNetworkMove(int R, int C)
     *========================
     *Sends a players move accross a network
     */
    private void makeNetworkMove(int R, int C){
      //If we are the server send our move to the client
          if (this.server == 1){
            this.ServerOut.println(NaceNetworkProtocol.MSGTYPEMOVE + "," + R + "," + C);
            
            //Update ourself corresponding with the move we made
            Clicked[R][C] = PLAYERONE;
            buttons[R][C].setBackground(Color.RED);

          //Else if we are the client send our move to the server
          }else if (this.server ==2){
            this.ClientOut.println(NaceNetworkProtocol.MSGTYPEMOVE + "," + R + "," + C);
            
            Clicked[R][C] = PLAYERTWO;
            buttons[R][C].setBackground(Color.BLUE);
            
          }
          
          Player++;
          this.TurnsMade++;
          YourTurn = false;
          this.lblYourTurn.setText("Opponents turn");
          calculateWinners();      
    }
    
      /**
     *=======================
     *|hostServer()
     *=======================
     *Host a network game
     */
    private void hostServer(){
      int HostingPort;
      
      if(this.s1.getHostingPort().equals("")){
        HostingPort = DEFAULTHOSTINGPORT;
      }else{
        System.out.println(this.s1.getHostingPort());
        HostingPort = Integer.parseInt(this.s1.getHostingPort());
      }
      
      try {
        serverSocket = new ServerSocket(HostingPort);
      } catch (IOException d) {
        JOptionPane.showMessageDialog(null,"Couldnt host server on port " + HostingPort +
        ".\nThis may be because this port is already in use by another application.\nYou can try changing the port number\n" + 
        "(Settings>Game settings>Network>Hosting port) and trying again...");
        return;
      }

      SocketConnect sc = new SocketConnect(serverSocket,this);
    
      dc = new JDialogCancel(this,true,"on port " + HostingPort);
      dc.setVisible(true);
      
      sc.cancelConnecting();

    }
    

  public void madeConnection(NaceConnectionEvent event) {
    
    this.clientSocket = ((SocketConnect) event.getSource()).getConnection();
    this.dc.setVisible(false); this.dc = null;
    
    try {
        
        ServerOut = new PrintWriter(clientSocket.getOutputStream(), true);
        ServerIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      server = 1;
      this.reset();
      this.YourTurn = true;
      this.lblYourTurn.setText("Your turn");
      this.GameType = "Networked";
      
      ServerOut.println("3," + this.ROWS + "," + this.COLUMNS + "," + this.ConectionLenght);
      
      
      Thread th = new Thread(this);
      th.start(); 
  }
}