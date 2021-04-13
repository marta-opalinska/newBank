package newbank.server;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class containing all the constants
 */
public final class Constants {

  private Constants() {
    // restrict instantiation
  }

  // confirmation options
  public static final ArrayList<String> CONFIRM = new ArrayList<>(Arrays.asList("yes", "y"));
  public static final ArrayList<String> REJECT = new ArrayList<>(Arrays.asList("no", "n"));

  //log out options
  public static final String LOG_OUT = "logout";
  public static final String EXIT = "exit";
  // number of attempts

  public static final int ATTEMPTS = 4;
  // Printing colour output

  public static final String PRINT_RED = "\u001B[31m";
  public static final String PRINT_YELLOW = "\u001b[33m";
  public static final String PRINT_BLUE = "\u001b[34m";
  public static final String RESET_STYLE = "\033[0m";
  public static final String BOLD_TEXT = "\033[1m";

  // for printing in transfers summary
  public static final String MY_ACCOUNT = "myAccount.";

  // Command names
  public static final String PAY_COMMAND = "pay";
  public static final String SHOW_ACCOUNTS_COMMAND = "showmyaccounts";
  public static final String MOVE_ACCOUNTS_COMMAND = "moveaccounts";
  public static final String ADD_ACCOUNT_COMMAND = "addaccount";
  //moveaccounts -a 400 -d savings -s main

  //microfinance commands
  public static final String REQUEST_LOAN_COMMAND = "requestloan";
  public static final String OFFER_LOAN_COMMAND = "offerloan";
  public static final String GET_OPEN_REQUESTS = "getrequests";
  public static final String GET_OPEN_OFFERS = "getoffers";
  public static final String CHOOSE_PRELOAN = "matchloan";
  public static final String PAY_LOAN = "payloan";
  public static final String GET_LOANS = "getloans";
  public static final String RETRACT_PRELOAN = "retract";

  //annual APR, in percent, for example 5.5% is 5.5
  public static final double ANNUALAPR = 50;

  //account threshold, since a loan can't make up more than a certain amount of the total funds a customer has, same rules as above
  public static final double ACCOUNT_THRESHOLD = 100;
  //in case of overdue payment, this is the late fee, and time added to pay
  public static final double LATE_FEE = 20;
  public static final int LATE_DAYS = 7;
}