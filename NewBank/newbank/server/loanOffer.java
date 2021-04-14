package newbank.server;

public class loanOffer extends PreLoan {
    //used in loading from database
    public loanOffer(Customer customer, double initialAmount, int days, double repaymentAmount){
        super(customer, initialAmount, days);
        this.repaymentAmount = repaymentAmount;
    }

    //used in first creation
    public loanOffer(Customer customer, double initialAmount, int days) {
        super(customer, initialAmount, days);
        if(canLoan()) {
            Customer temp = customer;
            withDrawForLoan(temp, initialAmount);
            databaseInterface.updateDatabase(temp);
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
        if(toAdd.loanStatus.equals(status.Paying)){
            System.out.println("OPEN2");
            this.changeStatus(status.Loaned);
            try{
                MicroFinanceDatabaseInterface.updateOffer(this);
            } catch(Exception e){
                e.printStackTrace();
            }
            return toAdd;
        } else {
            System.out.println("NULL");
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
        Customer temp = this.creator;
        temp.addMoney("main", this.amount);
        databaseInterface.updateDatabase(temp);
    }
}
