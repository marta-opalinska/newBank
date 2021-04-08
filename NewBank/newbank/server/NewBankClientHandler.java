package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NewBankClientHandler extends Thread {

  //added a socket s to send out requests for more data to customers
  private NewBank bank;
  private BufferedReader in;
  private CustomerPrint out;

  //test arraylist
  private mfInterface mfinterface = new mfInterface();

  public NewBankClientHandler(Socket s) throws IOException {
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
    out.printInfo("Welcome " + userName);
    return bank.checkLogInDetails(userName, password);
  }

  public void run() {
    Customer customer = logIn();
    boolean exit = false;
    int i = 0;
    while (i < 4) {
      while (customer == null) {
        out.printError("Log In Failed");
        customer = logIn();
      }
      if (i == 3){
        out.printInfo("Maximum number of log-in attempts have been made. \n Please contact customer services on...");
        exit = true;
        // not sure how to perform the exit of the program, have used the terminology of 'exit' to represent the intent
        // insert customer services number in this text line above
      }
      i++;
    }
      // if the user is authenticated then get requests from the user and process them
      out.printInfo("Log In Successful.");
      out.printRequest("Please type in a command from the list...\n");
      // Changed from "What do you want to do?" to providing a list of options for the user to type into the command line
      out.printInfo("pay\nshowmyaccounts\nmoveaccounts\naddaccount\nlogout");
      // keep getting requests from the client and processing them
      requestsLoop(customer);
      return;

  }
  private void requestsLoop(Customer customer) {
    boolean exit = false;
    try {
      while (!exit) {
        String request = safeLineReading();
        System.out.println("Request from " + customer.getName());
        HashMap<String, String> customerInput = CommandParser.parseFullCommand(request);
        if (customerInput.get("commandName").equals(Constants.LOG_OUT) || customerInput.get("commandName").equals(Constants.EXIT)) {
          out.printRequest("Are you sure you want to log out? y/n");
          if (confirm()) {
            out.printInfo("Logging out...");
            out.printInfo("Logout finished.");
            exit = true;
          }
        }
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
    Thread.currentThread().interrupt();
    return;
  }

  private Boolean processRequest(Customer customer, HashMap<String, String> request) {
    if (request.isEmpty()) {
      out.printWarning("Not valid command format.");
      return false;
    }
    try {
      String commandName = request.get("commandName");

      // providing help for command
      if (request.containsKey("h")) {
        out.printHelp(commandName);
        return true;
      }

      HashMap<String, String> optionals = new HashMap<>();
      switch (commandName) {
        case Constants.PAY_COMMAND:
          optionals.put("t", request.get("t"));
          return sendExternalPayment(customer, request.get("s"), Integer.parseInt(request.get("d")), Double.parseDouble(request.get("a")), optionals);
        case Constants.SHOW_ACCOUNTS_COMMAND:
          return showMyAccounts(customer);
        case Constants.MOVE_ACCOUNTS_COMMAND:
          optionals.put("t", request.get("t"));
          return moveBetweenAccounts(customer, request.get("s"), request.get("d"), Double.parseDouble(request.get("a")), optionals);
        case Constants.ADD_ACCOUNT_COMMAND:
          return addAccount(customer, request.get("n"));
        case Constants.REQUEST_LOAN_COMMAND:
          return addRequest(customer, Double.parseDouble(request.get("a")), Integer.parseInt(request.get("d")));
        case Constants.GET_OPEN_REQUESTS:
          out.printInfo(mfinterface.getOpenRequestsAsString());
          return true;
        case Constants.GET_OPEN_OFFERS:
          out.printInfo(mfinterface.getOpenOffersAsString());
          return true;
        case Constants.OFFER_LOAN_COMMAND:
          return addOffer(customer, Double.parseDouble(request.get("a")), Integer.parseInt(request.get("d")));
        case Constants.CHOOSE_PRELOAN:
          return match(customer, Integer.parseInt(request.get("i")));
        case Constants.GET_LOANS:
          out.printInfo(mfinterface.getLoansAsString(customer));
          return true;
        case Constants.PAY_LOAN:
          return payLoan(customer, Double.parseDouble(request.get("a")), Integer.parseInt(request.get("i")));
        default:
          out.printWarning("Command name not found.");
          return false;
      }
    } catch (NumberFormatException n) {
      out.printWarning("Invalid argument for number value.");
    } catch (NullPointerException n) {
      out.printWarning("Invalid parameters.");
    }
    return false;
  }

  /**
   * Function that provides moving funds to the external account.
   *
   * @param customer    customer that runs the command
   * @param source      source account
   * @param amount      amount to transfer
   * @param destination destination account (external)
   * @param optionals   optional parameters
   * @return if the operation was successful
   */
  private Boolean sendExternalPayment(Customer customer, String source, int destination, double amount, HashMap<String, String> optionals) {
    // checking if account name is not null
    if (source == null) {
      out.printError("Incorrect source account name.");
      return false;
    }
    boolean isAccountAvailable = customer.isAccountAvailable(source);
    // checking if account name exists
    if (!isAccountAvailable) {
      out.printError("Account does not exist.");
      return false;
    }
    //checking if there is money on the account
    if (!customer.areFundsSufficient(source, amount)) {
      out.printError("Not sufficient founds on the account.");
      return false;
    }
    //optionally adding transaction title
    if (optionals.get("t") != null) {
      out.printTransferSummary("myAccount.", source, String.valueOf(destination), "", amount, optionals.get("t"));
    } else {
      out.printTransferSummary("myAccount.", source, String.valueOf(destination), "", amount);
    }
    boolean isDestinationBankCustomer = bank.isBankCustomer(destination);
    if (!isDestinationBankCustomer) {
      out.printWarning("The customer is not registered in our bank. Would you like to continue? y/n");
    }
    if (confirm()) {
      if (isDestinationBankCustomer) {
        if (!bank.updateCustomerBalance(destination, amount)) {
          return false;
        }
      } else {
        bank.requestExternalMoneyTransfer(destination);
      }
      customer.withdrawMoney(source, amount);
      bank.updateDatabase(customer);
      return true;
    }
    return false;
  }

  private Boolean showMyAccounts(Customer customer) {
    ArrayList<Account> accounts = customer.getAccounts();
    out.printAccounts(accounts);
    return true;
  }

  /**
   * Function that provides moving money between accounts of the same customer
   *
   * @param customer    customer that runs the command
   * @param source      source account
   * @param destination destination account
   * @param amount      amount to transfer
   * @param optionals   optional parameters
   * @return if the operation was successful
   */
  private Boolean moveBetweenAccounts(Customer customer, String source, String destination, double amount, HashMap<String, String> optionals) {
    // checking if source and destination is not a null
    if (source == null || destination == null) {
      out.printError("Incorrect parameter.");
      return false;
    }
    boolean areAccountsAvailable = customer.isAccountAvailable(source) && customer.isAccountAvailable(destination);
    // checking existence of accounts
    if (!areAccountsAvailable) {
      out.printError("Account does not exist.");
      return false;
    }
    // checking if account have sufficient funds available
    if (!customer.areFundsSufficient(source, amount)) {
      out.printError("Not sufficient founds on the account.");
      return false;
    }
    //optionally adding transaction title
    if (optionals.get("t") != null) {
      out.printTransferSummary("myAccount.", source, "myAccount.", destination, amount, optionals.get("t"));
    } else {
      out.printTransferSummary("myAccount.", source, "myAccount.", destination, amount);
    }
    out.printRequest("Do you want to confirm this action? y/n");
    if (confirm()) {
      customer.withdrawMoney(source, amount);
      customer.addMoney(destination, amount);
      bank.updateDatabase(customer);
      return true;
    }
    return false;
  }

  /**
   * Function for adding new account under the same customer
   *
   * @param customer customer
   * @param name     new account name
   * @return if the operation was succesful
   */
  private Boolean addAccount(Customer customer, String name) {
    if (name != null) {
      out.printError("Incorrect parameter.");
      return false;
    }
    if (customer.isAccountAvailable(name)) {
      out.printError("The account with that name already exists.");
      return false;
    }
    out.printRequest("Would you like to confirm creating new account named " + name + "? y/n");
    if (confirm()) {
      Account accountToAdd = new Account(name, 0);
      customer.addAccount(accountToAdd);
    }
    bank.updateDatabase(customer);
    return true;
  }

  /**
   * Function that provides confirmation for customer actions.
   *
   * @return if the customer confirm the process
   */
  private boolean confirm() {
    int attempt = 0;
    String customerInput;
    while (attempt < Constants.ATTEMPTS) {
      customerInput = safeLineReading();
      // confirming operation
      if (Constants.CONFIRM.contains(customerInput.toLowerCase())) {
        return true;
      }
      // exit the command
      if (customerInput.toLowerCase().equals("exit") || Constants.REJECT.contains(customerInput.toLowerCase())) {
        out.printWarning("Operation rejected.");
        return false;
      }
      attempt++;
      out.printInfo("Invalid input. There are " + (Constants.ATTEMPTS - attempt) + " attempts left.");
    }
    return false;
  }

  /**
   * Function that creates a loan request.
   *
   * @param customer    customer that runs the command
   * @param amount      amount to transfer
   * @param days        number of days for loan
   * @param //optionals  optional parameters
   * @return if the operation was successful
   */
  private boolean addRequest(Customer customer, double amount, int days){
    try {
      mfinterface.createRequest(customer, amount, days);
      return true;
    } catch (Exception e){
      return false;
    }
  }

  private boolean addOffer(Customer customer, double amount, int days){
    try {
      mfinterface.createOffer(customer, amount, days);
      return true;
    } catch (Exception e){
      return false;
    }
  }

  private boolean match(Customer customer, int id){
    try {
      preLoan temp = mfinterface.getPreLoan(id);
      Loan toBuild = temp.buildLoan(customer);
      if(!Objects.isNull(toBuild)) {
        mfinterface.getPreLoan(id).changeStatus(status.Loaned);
        mfinterface.addLoan(temp.buildLoan(customer));
        return true;
      } else { return false; }
      //have to remove preloan from system
    } catch (Exception e){
      out.printError("Could not create loan");
      return false;
    }
  }

  private boolean payLoan(Customer customer, double amount, int id){
    if(mfinterface.payLoan(amount, id)){
      return true;
    }
    return false;
  }
}
