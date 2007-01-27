import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnect implements Runnable {
  
  ServerSocket connection;
  Socket clientSocket;
  Connectable listner;
  /** Creates a new instance of SocketConnect */
  public SocketConnect(ServerSocket connection,Connectable listner) {
    this.connection = connection;
    this.listner = listner;
    new Thread(this).start();
  }

  public void run() {
    try {
      clientSocket = connection.accept();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    listner.madeConnection(new NaceConnectionEvent(this));
  }
  
  public Socket getConnection(){
    return this.clientSocket;
  }
   
  public void cancelConnecting(){
    try {
      this.connection.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  } 
  
}
