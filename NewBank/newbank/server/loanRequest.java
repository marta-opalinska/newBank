package newbank.server;

public class loanRequest extends preLoan {
    //double accountThreshold = from constants? single place to check total account threshold and annual APR?
    public loanRequest(Customer cust, double initialAmount, double annualAPR, int days) {
        super(cust, initialAmount, annualAPR, days);

    }
    //we need to add a check for borrowers that their total funds are within threshold needed(ie they can only borrow up to 0.2 of their total funds)
    public boolean canBorrow(Customer cust, double initialAmount, double accountThreshold){
            if(cust.getTotalFunds()*accountThreshold>initialAmount){
                return true;
            } else {
                return false;
            }
        }
}