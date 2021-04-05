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

  //annual APR, in percent, example set at 5.5
  public static final double ANNUALAPR = 5.5;

  //in case of overdue payment, this is the late fee, and time added to pay
  public static final double LATE_FEE = 20;
  public static final int LATE_DAYS = 7;
}