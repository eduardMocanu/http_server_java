import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage
    //

          try(ServerSocket serverSocket = new ServerSocket(4221)) {
              serverSocket.setReuseAddress(true);
              while(true){
                  Socket client = serverSocket.accept();
                  client.setSoTimeout(10000);
                  Thread thread = new Thread(new HandleClients(client));
                  System.out.println(thread.getName());
                  thread.start();

              }
            }catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
          }


  }
}
