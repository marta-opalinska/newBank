package newbank.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class NewBank {

  private static final NewBank bank = new NewBank();
  //private final HashMap<String, Customer> customers;
  private  Customer activeCustomer;
  private Socket socket;
  private NewBank() {
        //customers = new HashMap<>();
        //addTestData();
        //customers = getCustomerData();
        //activeCustomer = initialiseSession();
  }

  public synchronized Customer checkLogInDetails(String userName, String password) {
      String hashPword;
      try {
          hashPword = toHexString(getSHA(password));
          System.out.println(toHexString(getSHA(password)));
          activeCustomer = databaseInterface.getCustomer(userName);
          if (databaseInterface.checkPassword(userName, hashPword)) {
              return activeCustomer;
          }
      } catch (NoSuchAlgorithmException e) {
          return null;
      }

      return null;
  }
  public static byte[] getSHA(String input) throws NoSuchAlgorithmException
  {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    return md.digest(input.getBytes(StandardCharsets.UTF_8));
  }
  public static String toHexString(byte[] hash)
  {
    BigInteger number = new BigInteger(1, hash);
    StringBuilder hexString = new StringBuilder(number.toString(16));
    while (hexString.length() < 32)
    {
      hexString.insert(0, '0');
    }
    return hexString.toString();
  }
  /*private HashMap<String, Customer> getCustomerData() {
      databaseInterface database = new databaseInterface();
      HashMap<String, Customer> customers1 = new HashMap<>();
      try {
          customers1 = databaseInterface.readFile();
      } catch (IOException e) {
          e.printStackTrace();
      }
      return customers1;
  }*/

  public static NewBank getBank() {
    return bank;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized Boolean processRequest(Customer customer, HashMap<String, String> request, Socket s) throws IOException {
    //connects newbank to the socket so client can be communicated with in the bank interface
    this.socket = s;
    // running the command
    if(request.isEmpty()){
      printToUser(Constants.PRINT_RED+ "Not valid command format."+ Constants.RESET_STYLE);
      return false;
    }
    String commandName = request.get("commandName").toLowerCase();
    switch (commandName) {
      case Constants.PAY_COMMAND:
        return paymentSend(customer);
      case Constants.SHOW_ACCOUNTS_COMMAND:
        //return showMyAccounts(customer);
      case Constants.MOVE_ACCOUNTS_COMMAND:
        //return moveAccounts(customer);
      default:
        printToUser(Constants.PRINT_RED+ "Command not found. "+ Constants.RESET_STYLE);
        return false;
    }
  }

  //add defensiveness(quit on EXIT)
  public Boolean moveAccounts(Customer customer) throws IOException {
    try {
      newbank.server.Account from;
      newbank.server.Account to;
      double amount = 0;
      ArrayList<Account> customerAccounts = customer.getAccounts();
      printToUser("Select Account to transfer from:");
      for (Account a : customerAccounts) {
        printToUser(a.toString());
      }
      fromLoop:
      while (true) {
        String fromString = userInput();
        for (Account a : customerAccounts) {
          if (fromString.equals(a.getAccountName())) {
            from = a;
            printToUser("Account Selected: " + a.toString());
            break fromLoop;
          }
        }
        printToUser("No Account selected");
      }
      printToUser("Select Account to transfer to:");
      for (Account a : customerAccounts) {
        printToUser(a.toString());
      }
      toLoop:
      while (true) {
        String toString = userInput();
        for (Account a : customerAccounts) {
          if (toString.equals(a.getAccountName())) {
            to = a;
            printToUser("Account Selected: " + a.toString());
            break toLoop;
          }
        }
        printToUser("No Account selected");
      }
      printToUser("Select Amount:");
      amountLoop:
      while (true) {
        String amountString = userInput();
        try {
          amount = Double.parseDouble(amountString);
          break amountLoop;
        } catch (NumberFormatException e) {
          printToUser("Invalid amount");
        }
      }
      if (from.canWithdraw(amount)) {
        from.withdraw(amount);
        to.deposit(amount);
        printToUser("Updated accounts:");
        printToUser(from.toString());
        printToUser(to.toString());
        return true;
      } else {
        printToUser(Constants.PRINT_RED+ "Insufficient Funds in origin account");
        return false;
      }
    } catch (RuntimeException e) {
      return false;
    }
  }

  //pay feature implementation: added a getAccounts(which returns an arraylist) function to Customer class
  //maybe best to build entirely separate class for move/payment transactions?
  public Boolean paymentSend(Customer customer) throws IOException {
    try {
      customer.getAccount("main").deposit(100000);
      databaseInterface.updateDatabase(customer);
    }
    catch(Exception e){}
      /*newbank.server.Account from;
      double amount = 0;
      newbank.server.Account to = null;
      boolean isPayeeInNewbank = false;
      //update when clarity about who/how to send money is clear
      String payee = null;
      ArrayList<newbank.server.Account> accountsAvailable = customers.get(customer.getKey()).getAccounts();
      printToUser("Select payment account:");
      for (newbank.server.Account a : accountsAvailable) {
        printToUser(a.toString());
      }
      printToUser("Choose account by typing account name");
      accountChoosingLoop:
      while (true) {
        String typed = userInput();
        for (newbank.server.Account a : accountsAvailable) {
          if (typed.equalsIgnoreCase(a.getAccountName())) {
            from = a;
            printToUser("ACCOUNT CHOSEN: " + a.getAccountName());
            break accountChoosingLoop;
          }
        }
        printToUser("Account not found; try again\n(To return to main, type EXIT)");
      }
      payeeChoosingLoop:
      while (true) {
        //need to add defenses against choosing wrong payee(could end in loop)
        printToUser("Is user a NewBank Customer?(Y/N)");
        String getIsNewbankCustomer = userInput();
        while (true) {
          if (getIsNewbankCustomer.equals("N")) {
            isPayeeInNewbank = false;
            printToUser("Type Payee:");
            payee = userInput();
            break payeeChoosingLoop;
          } else if (getIsNewbankCustomer.equals("Y")) {
            isPayeeInNewbank = true;
            printToUser("Type Name of Payee");
            payee = userInput();
            for (String c : customers.keySet()) {
              if (payee.equalsIgnoreCase(c)) {
                payee = c;
                ArrayList<Account> payeeAccounts = customers.get(c).getAccounts();
                for (newbank.server.Account a : payeeAccounts) {
                  to = a;
                  break payeeChoosingLoop;
                }
              }
            }
            printToUser("Not a newBank user");
          } else {
            printToUser("Please type Y or N");
          }
        }

      }
      amountChoosingLoop:
      while (true) {
        printToUser("Amount:");
        String typed = userInput();
        try {
          amount = Double.parseDouble(typed);
        } catch (Exception e) {
          printToUser("Not a number");
          continue;
        }
        if (from.canWithdraw(amount)) {
          break amountChoosingLoop;
        } else {
          printToUser("Insufficient Funds");
        }
      }
      printToUser("To Confirm:");
      printToUser("Money Transfer from: \n" + from.getAccountName() + "\nTo:" + payee);
      printToUser("\nTo Confirm Payment type Y:");
      while (true) {
        String typed = userInput();
        if (typed.equalsIgnoreCase("y")) {
          from.withdraw(amount);
          break;
        } else {
          printToUser("type EXIT to cancel or Y to confirm the payment");
        }
      }
      if (isPayeeInNewbank = true) {
        System.out.println(payee);
        to.deposit(amount);
        System.out.println(to);
      }
      activeCustomer.setBalance(200);
      databaseInterface.updateDatabase(activeCustomer);
      //prints server side information about the transfer
      System.out.println("|NEWTRANSFER:" + "|FROM:" + customer.getKey() + "|" + from.getAccountName() + "| TO:" + payee + "| AMOUNT:" + amount + "|");
      printToUser("Account Updated:" + from);
      return true;
    } catch (RuntimeException e) {
      return false;
    }

       */
      return true;
  }

  //function to send a String to the client
  private boolean printToUser(String s) {
    PrintWriter out;
    try {
      out = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      return false;
    }
    out.println(s);
    return true;
  }

  //function that takes what a user types in and returns it as a string
  private String userInput() throws RuntimeException, IOException {
    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    String temp = inputStream.readLine();
    if (temp.equals("EXIT")) {
      throw new RuntimeException("Exited");
    }
    return temp;
  }

}
