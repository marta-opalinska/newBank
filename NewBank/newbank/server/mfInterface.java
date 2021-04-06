package newbank.server;

import java.util.ArrayList;

public class mfInterface {

    public boolean createOffer(String username, double amount, int days){
        loanOffer offer = new loanOffer(username, amount, days);
        offer.setID(getNextPreLoanID());
        if(offer.canLoan()){
            addOffer(offer);
            return true;
        } else {
            return false;
        }
    }

    public void addOffer(loanOffer offer){
        //method to add offer to database
    }

    public boolean createRequest(String username, double amount, int days){
        loanRequest request = new loanRequest(username, amount, days);
        request.setID(getNextPreLoanID());
        if(request.canBorrow()){
            addRequest(request);
            return true;
        } else {
            return false;
        }
    }

    public void addRequest(loanRequest request){
        //method to add request to same database as offers
    }

    public ArrayList<loanOffer> getOffersAsArrayList(){
        //method to return all loanoffers in db
        return new ArrayList<loanOffer>();
    }

    public ArrayList<loanOffer> getOpenOffers(){
        //method to return offers if their status is open
        return new ArrayList<loanOffer>();
    }

    public ArrayList<loanRequest> getRequestAsArrayList(){
        //method to return all loanrequests in db
        return new ArrayList<loanRequest>();
    }

    public ArrayList<loanRequest> getOpenRequests(){
        //method to return requests if their status is open
        return new ArrayList<loanRequest>();
    }


    //TODO: these bad boys
    public loanOffer getOffer(int id){
        //method to return offer based on id
        return null;
    }

    public loanRequest getRequest(int id){
        //method to return request based on id
        return null;
    }

    public void requestToLoan(int id, String username){
        loanRequest request = getRequest(id);
        Customer creditor = databaseInterface.getCustomer(username);
        Loan temp = new Loan(request, creditor);

    }

    public int getNextPreLoanID(){
        int i = getOffersAsArrayList().size() + getRequestAsArrayList().size();
        return i+1;
    }
}
