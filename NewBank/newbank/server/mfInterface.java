package newbank.server;

import java.util.ArrayList;

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

    public String getOpenOffersAsString(){
        String toReturn = "--------------";
        ArrayList<loanOffer> build = getOpenOffers();
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n\n--------------";
        return toReturn;
    }

    private ArrayList<loanOffer> getOpenOffers(){
        //method to return requests if their status is open
        ArrayList<loanOffer> offers_cleaned = new ArrayList<loanOffer>();
        for(int i = 0; i<offers.size(); i++){
            if(offers.get(i).getLoanStatus().equals(status.Open)){
                offers_cleaned.add(offers.get(i));
            }
        }
        return offers_cleaned;
    }

    public String getOpenRequestsAsString(){
        String toReturn = "--------------";
        ArrayList<loanRequest> build = getOpenRequests();
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

    private ArrayList<loanRequest> getOpenRequests(){
        //method to return requests if their status is open
        ArrayList<loanRequest> requestsArray = getRequestsAsArrayList();
        ArrayList<loanRequest> requests_cleaned = new ArrayList<loanRequest>();
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
