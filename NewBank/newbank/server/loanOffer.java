package newbank.server;

public class loanOffer extends preLoan {
    public loanOffer(Customer customer, double initialAmount, int days) {
        super(customer, initialAmount, days);
        if(canLoan()) {
            withDrawForLoan(customer, initialAmount);
            this.changeStatus(status.Open);
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

    @Override
    Loan buildLoan(Customer debtor) {
        Loan toAdd = new Loan(this, debtor, id);
        //needs method to add Loan to database of loans
        if(toAdd.loanStatus.equals(status.Open)) {
            this.changeStatus(status.Loaned);
            return toAdd;
        } else {
            return null;
        }
    }

    public String makeString(){
        String toReturn = loanStatus+ getIDAsString() + creator_username+ "   inAmount:" + String.valueOf(amount) + "     repayAmount:" + String.valueOf(repaymentAmount) + "   days:" + String.valueOf(daysToRepayment);
        return toReturn;
    }
}
