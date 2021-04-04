package newbank.server;

public abstract class preLoan {
    status loanStatus;
    Customer creator;
    double amount;
    double repaymentAmount;
    int daysToRepayment;

    public preLoan(Customer cust, double initialAmount, double annualAPR, int days){
        this.creator = cust;
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

    public double getRepaymentAmount(double Amount, double annualAPR, int days){
        double effectiveAPR = Math.pow(((annualAPR/100)+1), days/365);
        return Amount*effectiveAPR;
    }

}
enum status{
    Open,
    Retracted,
    Loaned
}