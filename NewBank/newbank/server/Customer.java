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
	public Account getAccount(String name){
		for (Account acc:accounts){
			if (name.equals(acc.getAccountName()))	{
				return acc;
			}
		}
		return null;
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

	public boolean areFundsSufficient(String accountName, double amount){
		Account account = getAccount(accountName);
		return account.canWithdraw(amount);
	}

	public void withdrawMoney(String accountName, double amount){
		// protection against negative money transfers
		if(amount >= 0) {
			Account account = getAccount(accountName);
			account.withdraw(amount);
		}
	}

	public void addMoney(String accountName, double amount){
		// protection against negative money transfers
		if(amount >= 0){
			Account account = getAccount(accountName);
			account.deposit(amount);
		}
	}

	public boolean isAccountAvailable(String source) {
		return getAccount(source) == null? false: true;
	}
}
