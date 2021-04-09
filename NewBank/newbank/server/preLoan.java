package newbank.server;
import java.lang.Math;
public abstract class preLoan {
    //abstract class for loanoffers and loanrequests
    int id;
    status loanStatus;
    Customer creator;
    String creator_username;
    double amount;
    double repaymentAmount;
    int daysToRepayment;

    //annualAPR represents a percent APR, such as 5.5. this is then converted to the actual changes, such as
    //1.055, in the getrepaymentmethod
    public preLoan(Customer customer, double initialAmount, int days){
        //this.loanStatus = status.Open;
        this.creator_username = customer.getName();
        this.creator = customer;
        this.amount = initialAmount;
        this.daysToRepayment = days;
        this.repaymentAmount = getRepaymentAmount(initialAmount, days);
    }

    public boolean changeStatus(status newStatus){
        try{
            this.loanStatus = newStatus;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static double getRepaymentAmount(double amount, int days){
        double annualAPR = Constants.ANNUALAPR;
        double days_double = days;
        double years = days_double/365;
        double apr_modified = (annualAPR*0.01)+1;
        double repayment = amount * Math.pow(apr_modified, years);
        return repayment;
    }

    public void setID(int i){
        id = i;
    }

    public String getIDAsString(){
        String idString = String.valueOf(id);
        return idString;
    }

    public int getId(){
        return id;
    }

    abstract Loan buildLoan(Customer acceptor);

    public status getLoanStatus(){
        return loanStatus;
    }

    public String makeString(){
        double repayRounded= Math.round(repaymentAmount*100);
        repayRounded = repayRounded/100;
        String toReturn = loanStatus+" | " + getIDAsString() + " from  " + creator_username+ "   inAmount:" + amount + "     repayAmount:" + repayRounded + "   days:" + String.valueOf(daysToRepayment);
        return toReturn;
    }

}