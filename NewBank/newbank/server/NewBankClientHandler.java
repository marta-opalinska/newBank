package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class NewBankClientHandler extends Thread {

  //added a socket s to send out requests for more data to customers
  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private Socket s;


  public NewBankClientHandler(Socket s) throws IOException {
    this.s = s;
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  public void run() {
    // keep getting requests from the client and processing them
    try {
      // ask for user name
      out.println("Enter Username");
      String userName = in.readLine();
      // ask for password
      out.println("Enter Password");
      String password = in.readLine();
      out.println("Checking Details...");
      // authenticate user and get customer ID token from bank for use in subsequent requests
      CustomerID customer = bank.checkLogInDetails(userName, password);
      // if the user is authenticated then get requests from the user and process them
      if (customer != null) {
        out.println("Log In Successful. What do you want to do?");
        while (true) {
          String request = in.readLine();
          System.out.println("Request from " + customer.getKey());
          HashMap<String, String> customerInput = CommandParser.parse(request);
          Boolean isCommandDone = bank.processRequest(customer, customerInput, s);
          if (!isCommandDone){
            out.println(Constants.PRINT_RED + "The command was not successful. Please try again"+ Constants.RESET_COLOUR);
          }
          //added this so exiting doesn't look as though nothing is happening
          out.println("What do you want to do?");
        }
      } else {
        out.println("Log In Failed");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

}
