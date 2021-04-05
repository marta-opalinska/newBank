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
    double amountPaid;

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

    public void depositLoanFromRequest(Customer creditor, Customer debtor){
        
    }



}
