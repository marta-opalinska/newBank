package newbank.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Spliterator;


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

  public void updateDatabase(Customer customerToUpdate){
    databaseInterface.updateDatabase(customerToUpdate);
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

  public boolean isBankCustomer(int CustomerID) {
    return databaseInterface.customerExists(CustomerID);
  }

  //TODO updating only customer balance in the database
  public boolean updateCustomerBalance(int CustomerID, double amount){
    if(amount > 0){
      return true;
    }
    return false;
  }

  public void requestExternalMoneyTransfer(long destination) {
    System.out.println("Sending request for transferring money to account :" + destination);
  }
}
