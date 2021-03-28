package newbank.server;

import java.io.*;
import java.util.ArrayList;

public class CustomerPrint {
  private PrintWriter out;

  public CustomerPrint(OutputStream outStream) throws IOException {
    out = new PrintWriter(outStream, true);
  }

  public void printInfo(String info) {
    out.println(info);
  }

  public void printRequest(String request) {
    out.println(Constants.PRINT_BLUE + request + Constants.RESET_STYLE);
  }

  public void printWarning(String warning) {
    out.println(Constants.PRINT_YELLOW + warning + Constants.RESET_STYLE);
  }

  public void printError(String error) {
    out.println(Constants.PRINT_RED + error + Constants.RESET_STYLE);
  }

  public void close() {
    out.close();
  }

  public void printAccounts(ArrayList<Account> accounts) {
    printInfo("Available accounts:");
    for (Account a : accounts
    ) {
      printInfo(a.toString());
    }
  }

  public void printTransferSummary(String customerFrom, String accountFrom, String customerTo, String accountTo, double amount) {
    printWarning("** Transaction Summary **");
    printInfo("From: " + customerFrom + accountFrom);
    printInfo("To: " + customerTo + accountTo);
    printInfo("Amount: " + amount);
  }

  public void printTransferSummary(String customerFrom, String accountFrom, String customerTo, String accountTo, double amount, String title) {
    printTransferSummary(customerFrom, accountFrom, customerTo, accountTo, amount);
    printInfo("Title: " + title);
  }

  public void printHelp(String commandName) {
    printInfo("Command Name: " + Constants.BOLD_TEXT + commandName + Constants.RESET_STYLE);
    switch (commandName) {
      case Constants.PAY_COMMAND:
        printInfo("Command for transferring money to an EXTERNAL customer.");
        printCommandHelpArguments("a", "amount to transfer");
        printCommandHelpArguments("s", "source account name");
        printCommandHelpArguments("d", "destination account name");
        printInfo("Optional parameters:");
        printCommandHelpArguments("t", "transaction title");
        return;
      case Constants.SHOW_ACCOUNTS_COMMAND:
        printInfo("Command for showing available accounts and its balance.");
        return;
      case Constants.MOVE_ACCOUNTS_COMMAND:
        printInfo("Command for transferring money between accounts of the same customer.");
        printCommandHelpArguments("a", "amount to transfer");
        printCommandHelpArguments("s", "source account name");
        printCommandHelpArguments("d", "destination account name");
        printInfo("Optional parameters:");
        printCommandHelpArguments("t", "transaction title");
        return;
      case Constants.ADD_ACCOUNT_COMMAND:
        printInfo("Command for creating new account under the same customer.");
        printCommandHelpArguments("n", "account name");
      default:
        printWarning("No help function implemented for this command.");
    }
  }

  private void printCommandHelpArguments(String arg, String description) {
    printInfo(Constants.BOLD_TEXT + "\t -" + arg + ": " + Constants.RESET_STYLE + description);
    ;
  }
}
