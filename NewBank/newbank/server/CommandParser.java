package newbank.server;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Class that parses given input info command map of a format parameter: paramValue
 * If the parameter has no following value - it will be maped to null.
 */
public class CommandParser {
  /**
   * Helper Class. No constructor needed.
   */
  private CommandParser() {
  }

  /**
   * Function for parsing a signle command comming from a command line
   *
   * @param input command line input
   * @return map with in form: [arg: value]
   */
  public static HashMap<String, String> parse(String input) {
    HashMap<String, String> parsedCommand = new HashMap<>();
    // spliting command by blank space
    String[] splitCommand = input.split("\\s+");
    // adding command name to the map
    parsedCommand.put("commandName", splitCommand[0]);
    String commandParam, paramValue;
    for (int i = 1; i < splitCommand.length - 1; i++) {
      commandParam = splitCommand[i];
      paramValue = splitCommand[i + 1];
      // checking if that is the command parameter
      if (isParam(commandParam)) {
        // checking if the value follows that parameter
        if (!isParam(paramValue)) {
          parsedCommand.put(String.valueOf(commandParam.charAt(1)), paramValue);
          // skipping checking the next arg - that was the value of the parameter
          i++;
          continue;
        }
        // parameter without value
        parsedCommand.put(String.valueOf(commandParam.charAt(1)), null);
      } else {
        parsedCommand.clear();
        break;
      }
    }
    return parsedCommand;
  }

  private static Boolean isParam(String arg) {
    return arg.length() == 2 && arg.charAt(0) == '-';
  }
}
