package newbank.server;

public class loanOffer extends preLoan {
    public loanOffer(Customer cust, double initialAmount, double annualAPR, int days) {
        super(cust, initialAmount, annualAPR, days);
        if(canLoan(cust, initialAmount)){
            loanStatus = status.Open;
            withDrawForLoan(cust, initialAmount);
        } else {
            //if there is not enough money in the savings account, the offer is automatically retracted and not open
            loanStatus = status.Retracted;
        }
    }

    public boolean canLoan(Customer cust, double initialAmount){
        if(cust.getAccount("main").getBalance()>initialAmount){
            return true;
        } else {
            return false;
        }
    }

    public void withDrawForLoan(Customer cust, double initialAmount){
        cust.getAccount("main").withdraw(initialAmount);
    }


}
