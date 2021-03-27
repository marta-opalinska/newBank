package newbank.server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.invoke.LambdaMetafactory;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NewBankClientHandler extends Thread {

  //added a socket s to send out requests for more data to customers
  private NewBank bank;
  private BufferedReader in;
  private CustomerPrint out;
  private Socket s;


  public NewBankClientHandler(Socket s) throws IOException {
    this.s = s;
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new CustomerPrint(s.getOutputStream());
  }

  private String safeLineReading() {
    String customerInput = null;

    //trying until input is valid
    while (customerInput == null) {
      try {
        customerInput = in.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return customerInput;
  }

  private Customer logIn() {
    // ask for user name
    out.printRequest("Enter Username");
    String userName = safeLineReading();
    // ask for password
    out.printRequest("Enter Password");
    String password = safeLineReading();
    out.printInfo("Checking Details...");
    // authenticate user and get customer ID token from bank for use in subsequent requests
    return bank.checkLogInDetails(userName, password);
  }

  public void run() {
    Customer customer = logIn();
    // if the user is authenticated then get requests from the user and process them
    if (customer != null) {
      out.printInfo("Log In Successful.");
      out.printRequest("What do you want to do?");
      // keep getting requests from the client and processing them
      requestsLoop(customer);
    } else {
      out.printError("Log In Failed");
    }
  }

  private void requestsLoop(Customer customer) {
    try {
      while (true) {
        String request = safeLineReading();
        System.out.println("Request from " + customer.getName());
        HashMap<String, String> customerInput = CommandParser.parse(request);
        Boolean isCommandDone = processRequest(customer, customerInput);
        if (!isCommandDone) {
          out.printInfo("The command was not successful.");
        }
        //added this so exiting doesn't look as though nothing is happening
        out.printRequest("What do you want to do?");
      }
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

  private Boolean processRequest(Customer customer, HashMap<String, String> request) {
    if (request.isEmpty()) {
      out.printWarning("Not valid command format.");
      return false;
    }
    try {
      String commandName = request.get("commandName").toLowerCase();

//      for (String name : request.keySet()) {
//        String key = name;
//        String value = request.get(name);
//        System.out.println("_" + key + "_" + value + "_");
//      }
      // providing help for command
      if(request.keySet().contains("h")){
        out.printHelp(commandName);
        return true;
      }

      switch (commandName) {
        case Constants.PAY_COMMAND:
          return sendPayment();
        case Constants.SHOW_ACCOUNTS_COMMAND:
          return showMyAccounts(customer);
        case Constants.MOVE_ACCOUNTS_COMMAND:
          return moveBetweenAccounts(customer, request.get("s"), request.get("d"), Double.valueOf(request.get("a")));
        default:
          return false;
      }
    } catch (NumberFormatException n) {
      out.printWarning("Invalid argument for number value.");
    }
    return false;
  }

  private Boolean sendPayment() {
    return true;
  }

  private Boolean showMyAccounts(Customer customer) {
    ArrayList<Account> accounts = customer.getAccounts();
    out.printAccounts(accounts);
    return true;
  }

  /**
   * Function that provides movinf money betweeen accounts of the same customer
   * @param customer customer that runs the command
   * @param source source account
   * @param destination destinatin account
   * @param amount amount to transfer
   * @return if the operation was successful
   */
  private Boolean moveBetweenAccounts(Customer customer, String source, String destination, double amount) {
    Boolean areAccountsAvailable = customer.isAccountAvailable(source) && customer.isAccountAvailable(destination);
    if (!areAccountsAvailable) {
      out.printError("Account does not exist.");
      return false;
    }
    if (!customer.areFundsSufficient(source, amount)) {
      out.printError("Not sufficient founds on the account.");
      return false;
    }
    out.printTransferSummary("myAccount.", source, "myAccount.", destination, amount);
    if (confirm()) {
      customer.withdrawMoney(source, amount);
      customer.addMoney(destination, amount);
      return true;
    }
    return false;
  }

  /**
   * Funtion that provides confirmation for cutomer actions.
   * @return if the customer confirm the process
   */
  private boolean confirm() {
    int atempt = 0;
    String customerInput;
    out.printRequest("Do you want to confirm this action? y/n");
    while (atempt < Constants.ATTEMPTS) {
      customerInput = safeLineReading();
      // confirming operation
      if (Constants.CONFIRM.contains(customerInput.toLowerCase())) {
        return true;
      }
      // exit the command
      if (customerInput.toLowerCase() == "exit" || Constants.REJECT.contains(customerInput.toLowerCase())) {
        out.printWarning("Operation rejected.");
        return false;
      }
      atempt++;
      out.printInfo("Invalid input. There are " + (Constants.ATTEMPTS - atempt) + " attempts left.");
    }
    return false;
  }
}
