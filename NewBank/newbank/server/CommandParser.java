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
  public static HashMap<String, String> parseFullCommand(String input) {
    HashMap<String, String> parsedCommand = new HashMap<>();
    // spliting command by blank space
    String[] splitCommand = input.split("\\s+");

    // adding command name to the map
    parsedCommand.put("commandName", splitCommand[0]);
    String commandParam, paramValue;
    for (int i = 1; i < splitCommand.length; i++) {
      commandParam = splitCommand[i];
      // last value from the string
      if(i == splitCommand.length -1){
        if(isParam(commandParam)){
          // parameter without value
          parsedCommand.put(String.valueOf(commandParam.charAt(1)),"-1");
        }
        break;
      }
      paramValue = splitCommand[i + 1];
      // checking if that is the string is a parameter
      if (isParam(commandParam)) {
        // checking if the value follows that parameter
        if (!isParam(paramValue)) {
          String value = "";
          // calling function for find full parameter value (value might be more that one string)
          value = parsedValue(splitCommand,i+1);
          parsedCommand.put(String.valueOf(commandParam.charAt(1)), value);
          // skipping checking the next arg - that was the value of the parameter
          i += value.split("\\s+").length;
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

  /**
   * Function that returns found parameter value
   * @param splitCommand full command
   * @param valueStartIndex value staring index
   * @return value
   */
  private static String parsedValue(String[] splitCommand, int valueStartIndex) {
    String newValue = splitCommand[valueStartIndex];
    String fullValue = "";
    int index = valueStartIndex;
    while(!isParam(newValue)){
      fullValue += newValue;
      if(index+1 >= splitCommand.length){
        break;
      }
      index ++;
      newValue = " "+ splitCommand[index];
    }
    return fullValue;
  }

  /**
   * Function checking if the string is a valud parameter
   * @param arg string that might be a command parameter
   * @return
   */
  private static Boolean isParam(String arg) {
    // removing all spaces if any
    arg = arg.replaceAll(" ", "");
    return arg.length() == 2 && arg.charAt(0) == '-';
  }
}
