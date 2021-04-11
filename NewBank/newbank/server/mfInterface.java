package newbank.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import javax.print.Doc;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Date;
import java.util.Random;

public class mfInterface {
    int loans;
    //these are a temporary measure to test the marketplace
    private ArrayList<loanRequest> requests = new ArrayList<loanRequest>();
    private ArrayList<loanOffer> offers = new ArrayList<loanOffer>();
    //private ArrayList<Loan> loans = new ArrayList<Loan>();


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
        ArrayList<Loan> loans = new ArrayList<>();
        try {
            for (int i = 0; i<getNextPreLoanID()+4; i++){
                System.out.println(mf_dbinterface.loadLoan(i).stringLoan());
                loans.add(mf_dbinterface.loadLoan(i));
            }
        } catch(NullPointerException | ParserConfigurationException | IOException | SAXException | ParseException e){
            return loans;
        }
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
    //TODO update this?
    public static int getNextLoanID(){
        Random rand = new Random();
        int loans = rand.nextInt();
        return loans;
        //return loansArray.size()+1;
    }

    public void createLoan(preLoan preloan){
        //
    }

    public void addLoan(Loan loan){
        //TODO method to add loan to db
        try {
            mf_dbinterface.addLoan(loan);
        } catch (Exception e){
            e.printStackTrace();
        }
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



interface mf_dbinterface{
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    public static void addToDoc(Node n) throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml;
        try {
            File dbFile = new File("mf_db.xml");
            xml = dbuilder.parse(dbFile);
        } catch (Exception e){
            xml = dbuilder.parse("mf_db.xml");
        }
        Node importedNode = xml.importNode(n, true);
        xml.getDocumentElement().appendChild(importedNode);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(xml);
        StreamResult result = new StreamResult(new File("mf_db.xml"));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e){
            e.printStackTrace();
        }
    }

    public static Loan loadLoan(int id) throws ParserConfigurationException, IOException, SAXException, ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("loan");
        for(int i = 0; i<nlist.getLength();i++){
            Node nNode = nlist.item(i);
            Element node = (Element) nNode;
            if(Integer.parseInt(node.getAttribute("id"))==id){
                String user_creditor = node.getElementsByTagName("creditor").item(0).getTextContent();
                Customer creditor = databaseInterface.getCustomer(user_creditor);
                String user_debtor = node.getElementsByTagName("debtor").item(0).getTextContent();
                Customer debtor = databaseInterface.getCustomer(user_debtor);
                double initialAmount = Double.parseDouble(node.getElementsByTagName("initial_amount").item(0).getTextContent());
                double amount_due = Double.parseDouble(node.getElementsByTagName("amount_due").item(0).getTextContent());
                String init_date = node.getElementsByTagName("initial_date").item(0).getTextContent();
                LocalDate initial_date = dateformat.parse(init_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String due_date = node.getElementsByTagName("date_due").item(0).getTextContent();
                LocalDate repayment_date = dateformat.parse(due_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                status loan_status = status.valueOf(node.getAttribute("status"));
                Loan toReturn = new Loan(id, creditor, debtor, initialAmount, amount_due, initial_date, repayment_date, loan_status);
                System.out.println(toReturn.stringLoan());
                return toReturn;
            }
        }
        return null;
    }

    public static void addLoan(Loan loanToAdd) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document doc = dbuilder.newDocument();
        Element elem = doc.createElement("loan");
        elem.setAttribute("id", String.valueOf(mfInterface.getNextLoanID()));
        elem.setAttribute("status", String.valueOf(loanToAdd.loanStatus));
        elem.appendChild(createElement(doc, "debtor", loanToAdd.debtor.getName()));
        elem.appendChild(createElement(doc, "creditor", loanToAdd.creditor.getName()));
        elem.appendChild(createElement(doc, "initial_amount", String.valueOf(loanToAdd.initialAmount)));
        elem.appendChild(createElement(doc, "amount_due", String.valueOf(loanToAdd.amountDue)));
        int Date_day = loanToAdd.initialDate.getDayOfMonth();
        int Date_month = loanToAdd.initialDate.getMonthValue();
        int Date_year = loanToAdd.initialDate.getYear();
        String date = Date_year + "-" + Date_month + "-" + Date_day;
        elem.appendChild(createElement(doc, "initial_date", date));
        Date_day = loanToAdd.repaymentDate.getDayOfMonth();
        Date_month = loanToAdd.repaymentDate.getMonthValue();
        Date_year = loanToAdd.repaymentDate.getYear();
        date = Date_year + "-" + Date_month + "-" + Date_day;
        elem.appendChild(createElement(doc, "date_due", date));
        addToDoc(elem);
    }

    public default void updateLoan(Loan loan, int id){
        //
    }

    public default loanOffer loadOffer(int id){
        return null;
    }

    public default void addOffer(loanOffer offer){
        //
    }

    public default void updateOffer(loanOffer offer, int id){
        //
    }

    public default loanRequest loadRequest(int id){
        return null;
    }

    public default void addRequest(loanRequest request){
        //
    }

    public default void updateRequest(loanOffer offer, int id){
        //
    }

    public static Node createElement(Document doc, String name, String value){
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
