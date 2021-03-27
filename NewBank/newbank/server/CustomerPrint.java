package newbank.server;

import java.io.*;
import java.util.ArrayList;

public class CustomerPrint {
  private PrintWriter out;

  public CustomerPrint(OutputStream outStream) throws IOException {
    out = new PrintWriter(outStream, true);
  }

  public void printInfo(String info){
    out.println(info);
  }

  public void printRequest(String request){
    out.println(Constants.PRINT_BLUE + request + Constants.RESET_STYLE);
  }

  public void printWarning(String warning){
    out.println(Constants.PRINT_YELLOW + warning + Constants.RESET_STYLE);
  }

  public void printError(String error){
    out.println(Constants.PRINT_RED + error + Constants.RESET_STYLE);
  }

  public void close(){
    out.close();
  }

  public void printAccounts(ArrayList<Account> accounts) {
    printInfo("Available accounts:");
    for (Account a: accounts
         ) {
      printInfo(a.toString());
    }
  }

  public void printTransferSummary(String customerFrom, String accountFrom, String customerTo, String accountTo, double amount) {
  printWarning("** Transaction Summary **");
  printInfo("From: "+ customerFrom+accountFrom);
  printInfo("To: "+ customerTo+accountTo);
  printInfo("Amount: "+ amount);
  }

  public void printHelp(String commandName) {
    printInfo("Command Name: "+Constants.BOLD_TEXT + commandName + Constants.RESET_STYLE);
    switch (commandName) {
      case Constants.PAY_COMMAND:

      case Constants.SHOW_ACCOUNTS_COMMAND:

      case Constants.MOVE_ACCOUNTS_COMMAND:
        printCommandHelpArguments("a","amount to transfer");
        printCommandHelpArguments("s","source account name");
        printCommandHelpArguments("d","destination account name");
      default:
    }
  }

  private void printCommandHelpArguments(String arg, String description) {
    printInfo(Constants.BOLD_TEXT +"\t -"+arg+": "+Constants.RESET_STYLE +description);;
  }
}
