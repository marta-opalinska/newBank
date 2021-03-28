package newbank.client;

import newbank.server.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class ExampleClient extends Thread {

  private Socket server;
  private PrintWriter bankServerOut;
  private BufferedReader userInput;
  private Thread bankServerResponceThread;
  private boolean logout = false;

  public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
    server = new Socket(ip, port);
    userInput = new BufferedReader(new InputStreamReader(System.in));
    bankServerOut = new PrintWriter(server.getOutputStream(), true);


    bankServerResponceThread = new Thread() {
      private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

      public void run() {
        try {
          while (true) {
            String response = bankServerIn.readLine();
            if (response.equals("Logout finished.")) {
              server.close();
              Thread.currentThread().interrupt();
              break;
            }
            System.out.println(response);
          }
          // logout flag
          logout = true;
          System.out.println("Please click Enter to quit the app.");
        } catch (IOException e) {
          e.printStackTrace();
          return;
        }
      }
    };
    bankServerResponceThread.start();
  }


  public void run() {
    //  while (!logout) {
    try {
      while (!logout) {
        String command = userInput.readLine();
        bankServerOut.println(command);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//    }
  }

  public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
    new ExampleClient("localhost", 14002).start();
  }
}
