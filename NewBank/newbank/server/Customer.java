package newbank.server;

import java.util.ArrayList;

public class Customer {

	private ArrayList<Account> accounts;
	private String username;
	private String accountID;
	
	public Customer(String userName, String AccountID) {

		accounts = new ArrayList<Account>();
		username = userName;
		accountID = AccountID;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void setBalance(double value){

	}

	public String getName(){
		return username;
	}
	public String getAccountID(){return accountID;}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public ArrayList<Account> getAccounts(){
		return accounts;
	}
}
