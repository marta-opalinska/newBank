package newbank.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.util.Scanner;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private Socket socket;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request, Socket s) throws IOException {
		//connects newbank to the socket so client can be communicated with in the bank interface
		this.socket = s;
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "PAY" : return paymentSend(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	//pay feature implementation: added a getAccounts(which returns an arraylist) function to Customer class
	//maybe best to build entirely separate class for move/payment transactions?
	public String paymentSend(CustomerID customer) throws IOException {
		newbank.server.Account from;
		double amount = 0;
		newbank.server.Account to = null;
		boolean isPayeeInNewbank = false;
		//update when clarity about who/how to send money is clear
		String payee = null;
		ArrayList<newbank.server.Account> accountsAvailable = customers.get(customer.getKey()).getAccounts();
		printToUser("Select payment account:");
		for(newbank.server.Account a: accountsAvailable){
			printToUser(a.toString());
		}
		printToUser("Choose account by typing account name");
		accountChoosingLoop: while(true){
			String typed = userInput();
			if(typed.equalsIgnoreCase("EXIT")) {
				return "exited";
			}
			for(newbank.server.Account a: accountsAvailable){
				if(typed.equalsIgnoreCase(a.getAccountName())){
					from = a;
					printToUser("ACCOUNT CHOSEN: "+a.getAccountName());
					break accountChoosingLoop;
				}
			}
			printToUser("Account not found; try again\n(To return to main, type EXIT)");
		}
		payeeChoosingLoop:while(true) {
			printToUser("Is user a NewBank Customer?(Y/N)");
			String getIsNewbankCustomer = userInput();
			while(true){
				if(getIsNewbankCustomer.equals("N")){
					isPayeeInNewbank = false;
					printToUser("Type Payee:");
					payee = userInput();
					if(payee.equalsIgnoreCase("EXIT")){
						return "exited";
					} else {
						break payeeChoosingLoop;
					}
				} else if(getIsNewbankCustomer.equals("Y")){
					isPayeeInNewbank = true;
					printToUser("Type Name of Payee");
					payee = userInput();
					for(String c:customers.keySet()){
						if(payee.equalsIgnoreCase(c)){
							payee = c;
							ArrayList<Account> payeeAccounts = customers.get(c).getAccounts();
							for(newbank.server.Account a: payeeAccounts){
								to = a;
								break payeeChoosingLoop;
							}
						}
					}
					printToUser("Not a newBank user");
				} else {
					printToUser("Please type Y or N");
				}
			}

		}
		amountChoosingLoop: while(true){
			printToUser("Amount:");
			String typed = userInput();
			if(typed.equalsIgnoreCase("EXIT")){
				return "exited";
			}
			try {
				amount = Double.parseDouble(typed);
			} catch (Exception e){
				printToUser("Not a number");
				continue;
			}
			if(from.canWithdraw(amount)){
				break amountChoosingLoop;
			} else {
				printToUser("Insufficient Funds");
			}
		}
		printToUser("To Confirm:");
		printToUser("Money Transfer from: \n" +from.getAccountName() + "\nTo:" + payee);
		printToUser("\nTo Confirm Payment type Y:");
		while(true){
			String typed = userInput();
			if(typed.equalsIgnoreCase("y")){
				from.withdraw(amount);
				break;
			} else if(typed.equalsIgnoreCase("EXIT")){
				return "exited";
			} else {
				printToUser("type EXIT to cancel or Y to confirm the payment");
			}
		}
		if(isPayeeInNewbank = true){
			System.out.println(payee);
			to.deposit(amount);
			System.out.println(to);
		}
		//prints server side information about the transfer
		System.out.println("|NEWTRANSFER:"+"|FROM:"+customer.getKey()+"|"+from.getAccountName()+"| TO:"+payee+"| AMOUNT:"+amount+"|");
		printToUser("Account Updated:" + from);
		return "SUCCESS";
	}

	//function to send a String to the client
	private boolean printToUser(String s){
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e){
			return false;
		}
		out.println(s);
		return true;
	}

	//function that takes what a user types in and returns it as a string
	private String userInput() throws IOException {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		return inputStream.readLine();
	}
}
