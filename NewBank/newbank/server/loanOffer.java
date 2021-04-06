package newbank.server;

public class loanOffer extends preLoan {
    public loanOffer(String username, double initialAmount, int days) {
        super(username, initialAmount, days);
        if(canLoan()) {
            //
        } else {
            this.changeStatus(status.Retracted);
        }
    }

    public boolean canLoan(){
        if(creator.getAccount("main").getBalance()>amount){
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
