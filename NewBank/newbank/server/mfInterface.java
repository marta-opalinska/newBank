package newbank.server;

import java.util.ArrayList;

public class mfInterface {
    public ArrayList<loanRequest> requests = new ArrayList<loanRequest>();
    public ArrayList<loanOffer> offers = new ArrayList<loanOffer>();
    public ArrayList<Loan> loans = new ArrayList<Loan>();


    public void createOffer(Customer customer, double amount, int days){
        loanOffer offer = new loanOffer(customer, amount, days);
        offer.changeStatus(status.Open);
        offer.setID(getNextPreLoanID());
        addOffer(offer);
    }

    public void addOffer(loanOffer offer){
        offers.add(offer);
    }

    public void createRequest(Customer customer, double amount, int days){
        loanRequest request = new loanRequest(customer, amount, days);
        request.setID(getNextPreLoanID());
        addRequest(request);
    }

    public void addRequest(loanRequest request){
        //method to add request to same database as offers
        requests.add(request);
    }

    public ArrayList<loanOffer> getOffersAsArrayList(){
        //method to return all loanoffers in db
        return offers;
    }

    public String getOpenOffersAsString(){
        String toReturn = "";
        ArrayList<loanOffer> build = getOpenOffers();
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n\n--------------";
        return toReturn;
    }

    public ArrayList<loanOffer> getOpenOffers(){
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
        String toReturn = "";
        ArrayList<loanRequest> build = getOpenRequests();
        for(int i=0; i<build.size();i++){
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n\n--------------";
        return toReturn;
    }

    public ArrayList<loanRequest> getRequestAsArrayList(){
        //method to return all loanrequests in db
        return requests;
    }

    public ArrayList<loanRequest> getOpenRequests(){
        //method to return requests if their status is open
        ArrayList<loanRequest> requests_cleaned = new ArrayList<loanRequest>();
        for(int i = 0; requests.size()>i; i++){
            if(requests.get(i).getLoanStatus().equals(status.Open)){
                requests_cleaned.add(requests.get(i));
            }
        }
        return requests_cleaned;
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
        Loan temp = new Loan(request, creditor, id);
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

    public int getNextLoanID(){
        return loans.size()+1;
    }
}
