package newbank.server;

import java.util.ArrayList;

public class Customer {

	private databaseInterface database;
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}
	public double getBalance(String accountID){
		return 0.0;
	}
	public void setBalance(double value){

	}


	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public ArrayList getAccounts(){
		return accounts;
	}
}
