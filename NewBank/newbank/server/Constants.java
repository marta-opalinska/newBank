package newbank.server;

/**
 * Class containing all the constants
 */
public final class Constants {

  private Constants() {
    // restrict instantiation
  }

  // Printing colour output

  public static final String PRINT_RED = "\u001B[31m";
  public static final String RESET_COLOUR = "\033[0m";

  // Command names
  public static final String PAY_COMMAND = "pay";
  public static final String SHOW_ACCOUNTS_COMMAND = "showmyaccounts";
  public static final String MOVE_ACCOUNTS_COMMAND = "moveaccounts";

}