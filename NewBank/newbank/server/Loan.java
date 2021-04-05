package newbank.server;
import java.time.LocalDate;

public class Loan {
    int id;
    int preloanId;
    Customer creditor;
    Customer debtor;
    double initialAmount;
    double repaymentAmount;
    LocalDate initialDate;
    LocalDate repaymentDate;
    double amountDue = repaymentAmount;
    Status status = Status.Paying;


    public Loan(loanOffer offer, Customer debtor){
        //this constructor builds the Loan if it comes from an offer
        this.debtor = debtor;
        this.creditor = offer.creator;
        this.initialAmount = offer.amount;
        this.repaymentAmount = offer.repaymentAmount;
        this.preloanId = offer.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(offer.daysToRepayment);
    }

    public Loan(loanRequest request, Customer creditor){
        //this constructor builds the Loan if it comes from an offer
        this.debtor = request.creator;
        this.creditor = creditor;
        this.initialAmount = request.amount;
        this.repaymentAmount = request.repaymentAmount;
        this.preloanId = request.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(request.daysToRepayment);
    }

    public void depositLoanFromOffer(Customer creditor, Customer debtor){
        //the money was already taken from the creditor when they created the offer, thus this only deposits
        debtor.addMoney("savings", initialAmount);
    }

    public boolean depositLoanFromRequest(Customer creditor, Customer debtor){
        //this adds a check so the creditor has sufficient funds to lend out
        if(creditor.areFundsSufficient("main", initialAmount)){
            creditor.withdrawMoney("main", initialAmount);
            debtor.addMoney("savings", initialAmount);
            return true;
        } else {
            return false;
        }
    }

    public boolean payLoan(Customer debtor,Customer creditor, double amount){
        if(debtor.areFundsSufficient("main", amount) && amount<=amountDue){
            debtor.withdrawMoney("main", amount);
            creditor.addMoney("main", amount);
            amountDue = amountDue - amount;
            if(amountDue == 0){
                status = Status.Paid;
            }
            return true;
        } else {
            return false;
        }
    }

    public double getAmountDue(){ return amountDue;}

    //checks for loan if overdue, adds late fee and postpones
    //should check every day
    public void ifOverdue(){
        if(isLoanOverdue()){
            repaymentAmount = repaymentAmount + Constants.LATE_FEE;
            repaymentDate = repaymentDate.plusDays(Constants.LATE_DAYS);
        }
    }

    public boolean isLoanOverdue() {
        if(LocalDate.now().isAfter(repaymentDate)){
            return true;
        } else {
            return false;
        }
    }

}

enum Status{
    Paying,
    Paid
}
