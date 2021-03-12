package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public String getAccountName(){
		return accountName;
	}

	public boolean canWithdraw(double withdrawal){
		if(openingBalance-withdrawal<0){
			return false;
		}
		return true;
	}

	//method always forces through a withdrawal, even if amount becomes negative, so best to guard any implementation with canwithdraw==true
	public void withdraw(double withdrawal){
		openingBalance = openingBalance - withdrawal;
		return;
	}
}
