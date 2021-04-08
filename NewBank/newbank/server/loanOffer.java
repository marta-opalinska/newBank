package newbank.server;

public class loanOffer extends preLoan {
    public loanOffer(Customer customer, double initialAmount, int days) {
        super(customer, initialAmount, days);
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

    public Loan buildLoan(Customer debtor){
        Loan toAdd = new Loan(this, debtor);
        //needs method to add Loan to database of loans
        this.changeStatus(status.Loaned);
        return toAdd;
    }

    public String makeString(){
        String toReturn = creator_username+ "   inAmount:" + String.valueOf(amount) + "     repayAmount:" + String.valueOf(repaymentAmount) + "   days:" + String.valueOf(daysToRepayment);
        return toReturn;
    }
}
