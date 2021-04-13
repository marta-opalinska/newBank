package newbank.server;

public class loanRequest extends PreLoan {
    String details;

    //used in loading from database
    public loanRequest(Customer customer, double initialAmount, int days, double repaymentAmount){
        super(customer, initialAmount, days);
        this.repaymentAmount = repaymentAmount;
    }

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

    @Override
    Loan buildLoan(Customer creditor) {
        Loan toAdd = new Loan(this, creditor, id);
        //needs method to add Loan to database of loans
        if(toAdd.loanStatus.equals(status.Open)) {
//            this.changeStatus(status.Loaned);
//           try {
//                MicroFinanceDatabaseInterface.updateRequest(this);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            return toAdd;
        } else {
            return null;
        }
    }

    public String makeString(){
        double repayRounded= Math.round(repaymentAmount*100);
        repayRounded = repayRounded/100;
        String toReturn = "ID: " + getIDAsString() + "  Status:" + loanStatus + ":  from  " + creator_username+ "   inAmount:" + amount + "     repayAmount:" + repayRounded + "   days:" + String.valueOf(daysToRepayment);
        return toReturn;
    }

    public void retract(){
        this.changeStatus(status.Retracted);
    }
}
