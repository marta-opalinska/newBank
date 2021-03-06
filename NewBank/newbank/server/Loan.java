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
    double amountDue;
    status loanStatus = status.Paying;

    public Loan(int id, Customer creditor, Customer debtor, double initialAmount, double amountDue, LocalDate initialDate, LocalDate repaymentDate, status loanStatus){
        super(String.valueOf(id), 0);
        this.debtor = debtor;
        this.creditor = creditor;
        this.id = id;
        this.initialAmount = initialAmount;
        this.amountDue = amountDue;
        this.initialDate = initialDate;
        this.repaymentDate = repaymentDate;
        this.loanStatus = loanStatus;
    }

    public Loan(loanOffer offer, Customer debtor, int id){
        super(offer.getIDAsString(), 0);
        //this constructor builds the Loan if it comes from an offer
        this.debtor = debtor;
        this.creditor = offer.creator;
        this.initialAmount = offer.amount;
        this.repaymentAmount = Math.ceil(offer.repaymentAmount);
        this.amountDue = repaymentAmount;
        this.preLoanId = offer.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(offer.daysToRepayment);
        //offer.changeStatus(status.Loaned);
        this.id = id;
        //delete old offer from database and insert updated one
        //add new loan to database
        depositLoanFromOffer(this.creditor, this.debtor);
    }

    public Loan(loanRequest request, Customer creditor, int id){
        //
        super(request.getIDAsString(), 0);
        this.id = id;
        //this constructor builds the Loan if it comes from an offer
        this.debtor = request.creator;
        this.creditor = creditor;
        this.initialAmount = request.amount;
        this.repaymentAmount = Math.ceil(request.repaymentAmount);
        this.amountDue = repaymentAmount;
        this.preLoanId = request.id;
        this.initialDate = LocalDate.now();
        this.repaymentDate = initialDate.plusDays(request.daysToRepayment);
        //this request change status has to be sent back to mfinterface
        //request.changeStatus(status.Loaned);
        //delete old request from database and insert updated one
        //add new loan to database
        if(depositLoanFromRequest(this.creditor, this.debtor)){
            this.loanStatus=status.Paying;
        } else {
            this.loanStatus=status.Retracted;
        }
        ;
    }

    public void depositLoanFromOffer(Customer creditor, Customer debtor){
        //the money was already taken from the creditor when they created the offer, thus this only deposits
        debtor.addMoney("savings", initialAmount);
        databaseInterface.updateDatabase(debtor);
    }

    public boolean depositLoanFromRequest(Customer creditor, Customer debtor){
        //this adds a check so the creditor has sufficient funds to lend out
        if(creditor.areFundsSufficient("main", initialAmount)){
            creditor.withdrawMoney("main", initialAmount);
            debtor.addMoney("savings", initialAmount);
            databaseInterface.updateDatabase(creditor);
            databaseInterface.updateDatabase(debtor);
            return true;
        } else {
            return false;
        }
    }

    public boolean payLoan(double amount){
        if(debtor.areFundsSufficient("main", amount) && amount<=amountDue){
            ifOverdue();
            debtor.withdrawMoney("main", amount);
            databaseInterface.updateDatabase(debtor);
            creditor.addMoney("main", amount);
            databaseInterface.updateDatabase(creditor);
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
        if(LocalDate.now().isAfter(repaymentDate) && this.amountDue>0){
            return true;
        } else {
            return false;
        }
    }

    public String getLoanString(){
        String toReturn = "ID:" + id + "  Creditor:" + creditor.getName() + "  Debtor:" + debtor.getName() + "  AmountDue:" + amountDue + "  DateDue:" + repaymentDate.toString();
        return toReturn;
    }

    public String stringLoan(){
        String toReturn = creditor.getName()+debtor.getName()+amountDue+"  " + initialAmount+"  " +amountDue+"  " +repaymentDate.toString()+loanStatus.toString();
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
