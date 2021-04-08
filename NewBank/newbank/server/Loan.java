package newbank.server;
import java.time.LocalDate;

public class Loan extends Account{
    int id;
    int preLoanId;
    Customer creditor;
    Customer debtor;
    double initialAmount;
    double repaymentAmount;
    LocalDate initialDate;
    LocalDate repaymentDate;
    double amountDue = repaymentAmount;
    status loanStatus = status.Paying;


    public Loan(loanOffer offer, Customer debtor, int id){
        super(offer.getIDAsString(), offer.amount);
        //this constructor builds the Loan if it comes from an offer
        this.debtor = debtor;
        this.creditor = offer.creator;
        this.initialAmount = offer.amount;
        this.repaymentAmount = offer.repaymentAmount;
        this.preLoanId = offer.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(offer.daysToRepayment);
        offer.changeStatus(status.Loaned);
        this.id = id;
        //delete old offer from database and insert updated one
        //add new loan to database
        depositLoanFromOffer(creditor, debtor);
    }

    public Loan(loanRequest request, Customer creditor, int id){
        //
        super(request.getIDAsString(), request.amount);
        this.id = id;
        //this constructor builds the Loan if it comes from an offer
        this.debtor = request.creator;
        this.creditor = creditor;
        this.initialAmount = request.amount;
        this.repaymentAmount = request.repaymentAmount;
        this.preLoanId = request.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(request.daysToRepayment);
        //this request change status has to be sent back to mfinterface
        //request.changeStatus(status.Loaned);
        //delete old request from database and insert updated one
        //add new loan to database
        depositLoanFromRequest(creditor, debtor);
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
            ifOverdue();
            debtor.withdrawMoney("main", amount);
            creditor.addMoney("main", amount);
            amountDue = amountDue - amount;
            if(amountDue == 0){
                loanStatus = status.Paid;
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

    public String getLoanString(){
        String toReturn = "Creditor:" + creditor.getName() + "  Debtor:" + debtor.getName() + "  AmountDue:" + getAmountDue() + "  DateDue:" + repaymentDate.toString();
        return toReturn;
    }

}

enum status {
    //paying and paid is for loans
    Paying,
    Paid,
    //loaned, open, and retracted is for preloans
    Loaned,
    Open,
    Retracted,
}
