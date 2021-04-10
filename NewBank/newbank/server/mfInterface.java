package newbank.server;

import java.util.ArrayList;
import java.util.Objects;

public class mfInterface {

    //these are a temporary measure to test the marketplace
    private ArrayList<loanRequest> requests = new ArrayList<loanRequest>();
    private ArrayList<loanOffer> offers = new ArrayList<loanOffer>();
    private ArrayList<Loan> loans = new ArrayList<Loan>();


    public void createOffer(Customer customer, double amount, int days){
        loanOffer offer = new loanOffer(customer, amount, days);
        //offer.changeStatus(status.Open);
        offer.setID(getNextPreLoanID());
        offer.repaymentAmount = preLoan.getRepaymentAmount(amount, days);
        addOffer(offer);
    }

    private void addOffer(loanOffer offer){
        offers.add(offer);
    }

    public void createRequest(Customer customer, double amount, int days){
        loanRequest request = new loanRequest(customer, amount, days);
        //request.changeStatus(status.Open);
        request.setID(getNextPreLoanID());
        request.repaymentAmount = preLoan.getRepaymentAmount(amount, days);
        addRequest(request);
    }

    private void addRequest(loanRequest request){
        //method to add request to same database as offers
        //TODO
        requests.add(request);
    }

    private ArrayList<loanOffer> getOffersAsArrayList(){
        //method to return all loanoffers in db
        //TODO
        return offers;
    }

    public String getOpenOffersAsString(int days[], double amount[]){
        String toReturn = "--------------";
        ArrayList<preLoan> build = filterOorE(getOpenOffers(), days, amount);
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n\n--------------";
        return toReturn;
    }

    //filters by days and amount, where days[0] or amount[0] is min and [1] is max
    private ArrayList<preLoan> filterOorE(ArrayList<preLoan> preLoans, int days[], double amount[]){
        int minDays;
        if(days[0]==-1){
            minDays=0;
        } else {
            minDays = days[0];
        }
        int maxDays;
        if(days[1]==-1){
            maxDays=2147483600;
        } else {
            maxDays = days[1];
        }
        double minAmount;
        if(amount[0]==-1){
            minAmount=0;
        } else {
            minAmount = amount[0];
        }
        double maxAmount;
        if(amount[1]==-1){
            maxAmount = 2147483600;
        } else {
            maxAmount = amount[1];
        }
        ArrayList<preLoan> filteredReturn = new ArrayList<preLoan>();
        for(int i = 0;i<preLoans.size();i++){
            if(preLoans.get(i).amount<maxAmount && preLoans.get(i).amount>minAmount && preLoans.get(i).daysToRepayment>minDays && preLoans.get(i).daysToRepayment<maxDays){
                filteredReturn.add(preLoans.get(i));
            }
        }
        return filteredReturn;
    }

    private ArrayList<preLoan> getOpenOffers(){
        //method to return requests if their status is open
        ArrayList<preLoan> offers_cleaned = new ArrayList<preLoan>();
        for(int i = 0; i<offers.size(); i++){
            if(offers.get(i).getLoanStatus().equals(status.Open)){
                offers_cleaned.add(offers.get(i));
            }
        }
        return offers_cleaned;
    }

    public String getOpenRequestsAsString(int days[], double amount[]){
        String toReturn = "--------------";
        ArrayList<preLoan> build = filterOorE(getOpenRequests(), days, amount);
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n--------------";
        return toReturn;
    }

    private ArrayList<loanRequest> getRequestsAsArrayList(){
        //method to return all loanrequests in db
        //TODO from db
        return requests;
    }

    private ArrayList<preLoan> getOpenRequests(){
        //method to return requests if their status is open
        ArrayList<loanRequest> requestsArray = getRequestsAsArrayList();
        ArrayList<preLoan> requests_cleaned = new ArrayList<preLoan>();
        for(int i = 0; requestsArray.size()>i; i++){
            if(requestsArray.get(i).getLoanStatus().equals(status.Open)){
                requests_cleaned.add(requestsArray.get(i));
            }
        }
        return requests_cleaned;
    }

    private ArrayList<Loan> getLoansAsArrayList(){
        //TODO from db
        return loans;
    }

    //TODO: these bad boys
    private loanOffer getOffer(int id){
        //method to return offer based on id
        return null;
    }

    private int getNextPreLoanID(){
        int i = getOffersAsArrayList().size() + getRequestsAsArrayList().size();
        return i+1;
    }

    public preLoan getPreLoan(int id){
        ArrayList<preLoan> preLoans = new ArrayList<>();
        preLoans.addAll(getOffersAsArrayList());
        preLoans.addAll(getRequestsAsArrayList());
        for(int i = 0; i<preLoans.size(); i++){
            if(preLoans.get(i).getId()==id){
                return preLoans.get(i);
            }
        }
        return null;
    }

    public String getLoansAsString(Customer customer){
        ArrayList<Loan> loansArray = getLoansAsArrayList();
        try{
            String toDisp = "----------\nLoans you are owed:\n";
            for(int i = 0; i<loansArray.size(); i++){
                if(customer.equals(loansArray.get(i).creditor)) {
                    toDisp = toDisp + "\n" + loansArray.get(i).getLoanString() + "\n-----------";
                }
            }
            toDisp = toDisp + "\n----------\nLoans you owe:\n";
            for(int i = 0; i<loansArray.size(); i++){
                if(customer.equals(loansArray.get(i).debtor)) {
                    toDisp = toDisp + "\n" + loansArray.get(i).getLoanString() + "\n-----------";
                }
            }
            return toDisp;
        }
        catch (Exception e){
            e.printStackTrace();
            return "none";
        }
    }

    private int getNextLoanID(){
        ArrayList<Loan> loansArray = getLoansAsArrayList();
        return loansArray.size()+1;
    }

    public void createLoan(preLoan preloan){
        //
    }

    public void addLoan(Loan loan){
        //TODO method to add loan to db
        loans.add(loan);
    }

    public boolean payLoan(double amount, int loanID){
        ArrayList<Loan> loansArray = getLoansAsArrayList();
        for(Loan l:loansArray){
            if(l.id==loanID){
                l.payLoan(amount);
                return true;
            }
        }
        return false;
    }

    public String printLoans(){
        String toReturn = "";
        for(Loan l: loans){
            toReturn = toReturn+l.stringLoan() + "\n";
        }
        return toReturn;
    }
}
