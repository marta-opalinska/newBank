package newbank.server;

public abstract class preLoan {
    //abstract class for loanoffers and loanrequests
    int id;
    status loanStatus;
    Customer creator;
    String creator_username;
    double amount;
    double repaymentAmount;
    int daysToRepayment;
    double annualAPR = Constants.ANNUALAPR;

    //annualAPR represents a percent APR, such as 5.5. this is then converted to the actual changes, such as
    //1.055, in the getrepaymentmethod
    public preLoan(Customer customer, double initialAmount, int days){
        //this.loanStatus = status.Open;
        this.creator_username = customer.getName();
        this.creator = customer;
        this.amount = initialAmount;
        this.daysToRepayment = days;
        this.repaymentAmount = getRepaymentAmount(initialAmount, annualAPR, days);
    }

    public boolean changeStatus(status newStatus){
        try{
            this.loanStatus = newStatus;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double getRepaymentAmount(double amount, double annualAPR, int days){
        double effectiveAPR = Math.pow(((annualAPR/100)+1), days/365);
        return amount*effectiveAPR;
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

    public Loan buildLoan(Customer customer){
        return null;
    }

    public status getLoanStatus(){
        return loanStatus;
    }

}