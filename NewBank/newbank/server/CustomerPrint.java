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
        printCommandHelpArguments("d", "destination account number");
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
        return;
      case Constants.REQUEST_LOAN_COMMAND:
        printInfo("Command to request a loan, annual APR(Currently at "+ Constants.PRINT_RED + Constants.ANNUALAPR +"%" + Constants.RESET_STYLE+ ") applied by New Bank");
        printCommandHelpArguments("a", "amount");
        printCommandHelpArguments("d", "days until repayment");
        return;
      case Constants.OFFER_LOAN_COMMAND:
        printInfo("Command to offer a loan, annual APR(Currently at "+ Constants.PRINT_RED + Constants.ANNUALAPR +"%"+ Constants.RESET_STYLE+  ") applied by New Bank\nLoan amount withdrawn from main account upon offer creation");
        printCommandHelpArguments("a", "amount");
        printCommandHelpArguments("d", "days until repayment");
        return;
      case Constants.GET_OPEN_OFFERS:
        printInfo("Command to return available open loan offers");
        printInfo("Optional parameters:");
        printCommandHelpArguments("x", "(Optional) filter minimum days");
        printCommandHelpArguments("y", "(Optional) filter maximum days");
        printCommandHelpArguments("p", "(Optional) filter minimum amount");
        printCommandHelpArguments("q", "(Optional) filter maximum amount");
        return;
      case Constants.GET_OPEN_REQUESTS:
        printInfo("Command to return available open loan requests");
        printInfo("Optional parameters:");
        printCommandHelpArguments("x", "(Optional) filter minimum days");
        printCommandHelpArguments("y", "(Optional) filter maximum days");
        printCommandHelpArguments("p", "(Optional) filter minimum amount");
        printCommandHelpArguments("q", "(Optional) filter maximum amount");
        return;
      case Constants.CHOOSE_PRELOAN:
        printInfo("Command to select an available offer or request and create a loan, amount withdrawn from main account and deposited into savings");
        printCommandHelpArguments("i", "choose ID of request or offer");
        return;
      case Constants.PAY_LOAN:
        printInfo("Command to pay off whole or part of an existing loan");
        printCommandHelpArguments("i", "choose ID of loan to repay");
        return;
      case Constants.GET_LOANS:
        printInfo("Command to retrieve active(unpaid) loans, whether you are debtor or creditor");
        return;
      case Constants.RETRACT_PRELOAN:
        printInfo("Retract active offer or loan, upon retracting offer you are debited the amount offered");
        printCommandHelpArguments("i", "select ID of offer or request to repay");
        return;
      default:
        printWarning("No help function implemented for this command.");
        return;
    }
  }

  private void printCommandHelpArguments(String arg, String description) {
    printInfo(Constants.BOLD_TEXT + "\t -" + arg + ": " + Constants.RESET_STYLE + description);
    ;
  }
}
