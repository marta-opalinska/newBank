package newbank.server;

public class loanRequest extends preLoan {
    String details;

    //double accountThreshold = from constants? single place to check total account threshold and annual APR?
    public loanRequest(Customer customer, double initialAmount, int days) {
        super(customer, initialAmount, days);
        if(canBorrow()){
            this.changeStatus(status.Open);
        } else {
            this.changeStatus(status.Retracted);
        }
    }
    //we need to add a check for borrowers that their total funds are within threshold needed(ie they can only borrow up to 0.2 of their total funds)
    public boolean canBorrow(){
            if(creator.getTotalFunds()*Constants.ACCOUNT_THRESHOLD*0.01>amount){
                return true;
            } else {
                return false;
            }
        }


    public Loan buildLoan(Customer creditor, int id){
        Loan toAdd = new Loan(this, creditor, id);
        //needs method to add Loan to database of loans
        this.changeStatus(status.Loaned);
        return toAdd;
        //needs method to update database of preloans so it becomes loaned
    }

    public String makeString(){
        String toReturn = getIDAsString() + " from  " + creator_username+ "   inAmount:" + String.valueOf(amount) + "     repayAmount:" + String.valueOf(repaymentAmount) + "   days:" + String.valueOf(daysToRepayment);
        return toReturn;
    }
}
