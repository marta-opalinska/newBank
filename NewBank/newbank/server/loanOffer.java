package newbank.server;

public class loanOffer extends preLoan {
    public loanOffer(String username, double initialAmount, double annualAPR, int days) {
        super(username, initialAmount, days);
        Customer cust = databaseInterface.getCustomer(username);
        if(canLoan(cust, initialAmount)){
            loanStatus = status.Open;
            withDrawForLoan(cust, initialAmount);
        } else {
            //if there is not enough money in the savings account, the offer is automatically retracted and not open
            loanStatus = status.Retracted;
        }
    }

    public boolean canLoan(Customer cust, double initialAmount){
        if(cust.getAccount("main").getBalance()>initialAmount){
            return true;
        } else {
            return false;
        }
    }

    public void withDrawForLoan(Customer cust, double initialAmount){
        cust.getAccount("main").withdraw(initialAmount);
    }

    public void buildLoan(Customer debtor){
        Loan toAdd = new Loan(this, debtor);
        //needs method to add Loan to database of loans
        this.changeStatus(status.Loaned);
    }
}
