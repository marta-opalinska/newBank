package newbank.server;

import java.util.ArrayList;

public class mfInterface {
    public ArrayList<loanRequest> requests = new ArrayList<loanRequest>();
    public ArrayList<loanOffer> offers = new ArrayList<loanOffer>();
    public ArrayList<Loan> loans = new ArrayList<Loan>();
    public boolean createOffer(Customer customer, double amount, int days){
        loanOffer offer = new loanOffer(customer, amount, days);
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

    public boolean createRequest(Customer customer, double amount, int days){
        loanRequest request = new loanRequest(customer, amount, days);
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
        requests.add(request);
    }

    public ArrayList<loanOffer> getOffersAsArrayList(){
        //method to return all loanoffers in db
        return offers;
    }

    public ArrayList<loanOffer> getOpenOffers(){
        //method to return offers if their status is open
        return new ArrayList<loanOffer>();
    }

    public String getRequestsAsString(){
        String toReturn = "";
        ArrayList<loanRequest> build = getRequestAsArrayList();
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n \n" + build.get(i).makeString();
        }
        return toReturn;
    }

    public ArrayList<loanRequest> getRequestAsArrayList(){
        //method to return all loanrequests in db
        ArrayList<loanRequest> requests_cleaned = new ArrayList<loanRequest>();
        for(int i = 0; requests.size()>i; i++){
            if(requests.get(i).getLoanStatus()!=status.Loaned){
                requests_cleaned.add(requests.get(i));
            }
        }
        return requests_cleaned;
    }

    public ArrayList<loanRequest> getOpenRequests(){
        //method to return requests if their status is open
        return new ArrayList<loanRequest>();
    }

    public ArrayList<Loan> getLoansAsArrayList(){
        return loans;
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
        loans.add(temp);
    }

    public int getNextPreLoanID(){
        int i = getOffersAsArrayList().size() + getRequestAsArrayList().size();
        return i+1;
    }

    public preLoan getPreLoan(int id){
        ArrayList<preLoan> preLoans = new ArrayList<>();
        preLoans.addAll(getOffersAsArrayList());
        preLoans.addAll(getRequestAsArrayList());
        for(int i = 0; i<preLoans.size(); i++){
            if(preLoans.get(i).getId()==id){
                return preLoans.get(i);
            }
        }
        return null;
    }

    public String getLoansAsString(){
        try{
            String toDisp = "----------";
            for(int i = 0; i<loans.size(); i++){
                toDisp = toDisp + "\n" + loans.get(i).getLoanString() + "\n-----------";
            }
            return toDisp;
        }
        catch (Exception e){
            return "none";
        }
    }
}
