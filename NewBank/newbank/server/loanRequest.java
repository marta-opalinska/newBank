package newbank.server;

public class loanRequest extends preLoan {
    String details;

    //double accountThreshold = from constants? single place to check total account threshold and annual APR?
    public loanRequest(String username, double initialAmount, int days) {
        super(username, initialAmount, days);
    }
    //we need to add a check for borrowers that their total funds are within threshold needed(ie they can only borrow up to 0.2 of their total funds)
    public boolean canBorrow(){
            if(creator.getTotalFunds()*Constants.ACCOUNT_THRESHOLD*0.01>amount){
                return true;
            } else {
                return false;
            }
        }

    public void buildLoan(Customer creditor){
        Loan toAdd = new Loan(this, creditor);
        //needs method to add Loan to database of loans
        this.changeStatus(status.Loaned);
        //needs method to update database of preloans so it becomes loaned
    }
}
