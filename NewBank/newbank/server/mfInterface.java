package newbank.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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

public interface mfInterface {
    //First part works with offers
    public static Customer createOffer(Customer customer, double amount, int days) {
        loanOffer offer = new loanOffer(customer, amount, days);
        offer.setID(getNextPreLoanID());
        offer.repaymentAmount = PreLoan.getRepaymentAmount(amount, days);
        addOffer(offer);
        return offer.creator;
    }

    private static void addOffer(loanOffer offer) {
        try {
            MicroFinanceDatabaseInterface.addOffer(offer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<loanOffer> getOffersAsArrayList() {
        ArrayList<loanOffer> offers = new ArrayList<>();
        for (int i = 0; i < getNextPreLoanID() + 3; i++) {
            try {
                loanOffer addOffer = MicroFinanceDatabaseInterface.loadOffer(i);
                System.out.println(addOffer.makeString());
                if (addOffer != null) {
                    offers.add(addOffer);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return offers;
    }

    public static String getOpenOffersAsString(int days[], double amount[]) {
        String toReturn = "--------------";
        ArrayList<PreLoan> build = filterOorR(getOpenOffers(), days, amount);
        for (int i = 0; i < build.size(); i++) {
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n--------------";
        return toReturn;
    }

    private static ArrayList<PreLoan> getOpenOffers() {
        //method to return requests if their status is open
        ArrayList<loanOffer> offers = getOffersAsArrayList();
        ArrayList<PreLoan> offers_cleaned = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            if (offers.get(i).getLoanStatus().equals(status.Open)) {
                offers_cleaned.add(offers.get(i));
            }
        }
        return offers_cleaned;
    }

    public static void createRequest(Customer customer, double amount, int days) {
        loanRequest request = new loanRequest(customer, amount, days);
        //request.changeStatus(status.Open);
        request.setID(getNextPreLoanID());
        request.repaymentAmount = PreLoan.getRepaymentAmount(amount, days);
        addRequest(request);
    }

    private static void addRequest(loanRequest request) {
        try {
            MicroFinanceDatabaseInterface.addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //filters by days and amount, where days[0] or amount[0] is min and [1] is max
    private static ArrayList<PreLoan> filterOorR(ArrayList<PreLoan> PreLoans, int days[], double amount[]) {
        int minDays;
        if (days[0] == -1) {
            minDays = 0;
        } else {
            minDays = days[0];
        }
        int maxDays;
        if (days[1] == -1) {
            maxDays = 2147483600;
        } else {
            maxDays = days[1];
        }
        double minAmount;
        if (amount[0] == -1) {
            minAmount = 0;
        } else {
            minAmount = amount[0];
        }
        double maxAmount;
        if (amount[1] == -1) {
            maxAmount = 2147483600;
        } else {
            maxAmount = amount[1];
        }
        ArrayList<PreLoan> filteredReturn = new ArrayList<PreLoan>();
        for (int i = 0; i < PreLoans.size(); i++) {
            if (PreLoans.get(i).amount < maxAmount && PreLoans.get(i).amount > minAmount && PreLoans.get(i).daysToRepayment > minDays && PreLoans.get(i).daysToRepayment < maxDays) {
                filteredReturn.add(PreLoans.get(i));
            }
        }
        return filteredReturn;
    }


    public static String getOpenRequestsAsString(int days[], double amount[]) {
        String toReturn = "--------------";
        ArrayList<PreLoan> build = filterOorR(getOpenRequests(), days, amount);
        for (int i = 0; i < build.size(); i++) {
            toReturn = toReturn + "\n" + build.get(i).makeString();
        }
        toReturn = toReturn + "\n--------------";
        return toReturn;
    }

    private static ArrayList<loanRequest> getRequestsAsArrayList() {
        ArrayList<loanRequest> requests = new ArrayList<>();
        for (int i = 0; i < getNextPreLoanID() + 3; i++) {
            try {
                loanRequest addRequest = MicroFinanceDatabaseInterface.loadRequest(i);
                if (addRequest != null) {
                    requests.add(addRequest);
                }
            } catch (Exception e) {
                //
            }
        }
        return requests;
    }

    private static ArrayList<PreLoan> getOpenRequests() {
        //method to return requests if their status is open
        ArrayList<loanRequest> requestsArray = getRequestsAsArrayList();
        ArrayList<PreLoan> requests_cleaned = new ArrayList<PreLoan>();
        for (int i = 0; requestsArray.size() > i; i++) {
            if (requestsArray.get(i).getLoanStatus().equals(status.Open)) {
                requests_cleaned.add(requestsArray.get(i));
            }
        }
        return requests_cleaned;
    }

    private static ArrayList<Loan> getLoansAsArrayList() {
        ArrayList<Loan> loansList = new ArrayList<>();
        try {
            for (int i = 0; i < getNextLoanID() + getNextPreLoanID() + 1; i++) {
                try {
                    Loan addLoan = MicroFinanceDatabaseInterface.loadLoan(i);
                    if (addLoan != null && addLoan.amountDue != 0) {
                        //System.out.println(addLoan.stringLoan());
                        loansList.add(addLoan);
                        //addLoan.ifOverdue();
                        //mf_dbinterface.updateLoan(addLoan);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
        return loansList;
    }

    private static int getNextPreLoanID() {
        try {
            return MicroFinanceDatabaseInterface.getNextPreLoanID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static PreLoan getPreLoan(int id) {
        ArrayList<PreLoan> PreLoans = new ArrayList<>();
        PreLoans.addAll(getOffersAsArrayList());
        PreLoans.addAll(getRequestsAsArrayList());
        for (int i = 0; i < PreLoans.size(); i++) {
            if (PreLoans.get(i).getId() == id) {
                return PreLoans.get(i);
            }
        }
        return null;
    }

    private static void updateOffer(loanOffer offer) throws IOException, SAXException, ParserConfigurationException {
        MicroFinanceDatabaseInterface.updateOffer(offer);
    }

    public static String getLoansAsString(Customer customer) {
        ArrayList<Loan> loansArray;
        loansArray = getLoansAsArrayList();
        try {
            String toDisp = "----------\nLoans you are owed:\n";
            for (int i = 0; i < loansArray.size(); i++) {
                if (customer.getName().equals(loansArray.get(i).creditor.getName())) {
                    toDisp = toDisp + "\n" + loansArray.get(i).getLoanString() + "\n-----------";
                }
            }
            toDisp = toDisp + "\n----------\nLoans you owe:\n";
            for (int i = 0; i < loansArray.size(); i++) {
                if (customer.getName().equals(loansArray.get(i).debtor.getName())) {
                    toDisp = toDisp + "\n" + loansArray.get(i).getLoanString() + "\n-----------";
                }
            }
            return toDisp;
        } catch (Exception e) {
            e.printStackTrace();
            return "none";
        }
    }

    public static int getNextLoanID() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dbuilder = factory.newDocumentBuilder();
            Document xml = dbuilder.parse("mf_db.xml");
            Element root = xml.getDocumentElement();
            NodeList loanList = root.getElementsByTagName("loan");
            int id = loanList.getLength() + 1;
            //System.out.println(id);
            return id;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void addLoan(Loan loan) {
        try {
            MicroFinanceDatabaseInterface.addLoan(loan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean payLoan(double amount, int loanID) {
        ArrayList<Loan> loansArray = getLoansAsArrayList();
        for (Loan l : loansArray) {
            if (l.id == loanID) {
                l.payLoan(amount);
                try {
                    MicroFinanceDatabaseInterface.updateLoan(l);
                } catch (ParserConfigurationException | SAXException | TransformerConfigurationException | IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public static double[] filter(HashMap<String, String> optionals) {
        int days[] = new int[2];
        double amount[] = new double[2];
        try {
            if (optionals.get("x") != null) {
                days[0] = Integer.parseInt(optionals.get("x"));
            } else {
                days[0] = -1;
            }
            if (optionals.get("y") != null) {
                days[1] = Integer.parseInt(optionals.get("y"));
            } else {
                days[1] = -1;
            }
            if (optionals.get("p") != null) {
                amount[0] = Double.parseDouble(optionals.get("p"));
            } else {
                amount[0] = -1;
            }
            if (optionals.get("q") != null) {
                amount[1] = Double.parseDouble(optionals.get("q"));
            } else {
                amount[1] = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        double filtered[] = new double[4];
        filtered[0] = days[0];
        filtered[1] = days[1];
        filtered[2] = amount[0];
        filtered[3] = amount[1];
        return filtered;
    }

    public static void retract(Customer customer, int id) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<loanOffer> offers = getOffersAsArrayList();
        ArrayList<loanRequest> requests = getRequestsAsArrayList();
        for (loanOffer o : offers) {
            if (o.id == id && customer.getName().equals(o.creator_username)) {
                o.retract();
                MicroFinanceDatabaseInterface.updateOffer(o);
                //return toReturn;
            }
        }
        for (loanRequest r : requests) {
            if (r.id == id && customer.getName().equals(r.creator_username)) {
                r.retract();
                MicroFinanceDatabaseInterface.updateRequest(r);
                //return toReturn;
            }
        }
        //return null;
    }

    public static Loan getLoan(int id) {
        ArrayList<Loan> loans = getLoansAsArrayList();
        for (Loan l : loans) {
            if (l.id == id) {
                return l;
            }
        }
        return null;
    }

    public static boolean isOffer(int id) {
        try {
            MicroFinanceDatabaseInterface.loadOffer(id);
            return true;
        } catch (NullPointerException | ParserConfigurationException | IOException | SAXException e) {
            return false;
        }
    }
}




interface MicroFinanceDatabaseInterface {
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

    public static void removeFromDoc(Node n, String type) throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml;
        try {
            File dbFile = new File("mf_db.xml");
            xml = dbuilder.parse(dbFile);
        } catch (Exception e){
            xml = dbuilder.parse("mf_db.xml");
        }
        NodeList nList = xml.getElementsByTagName(type);
        Element nElement = (Element)n;
        int nId = Integer.parseInt(nElement.getAttribute("id"));
        for(int i = 0; i<nList.getLength(); i++){
            Element node = (Element) nList.item(i);
            int nodeId = Integer.parseInt(node.getAttribute("id"));
            if(nodeId==nId){
                node.getParentNode().removeChild(node);
                break;
            }
        }
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
        //System.out.println("NLIST:" + nlist.getLength());
        for(int i = 0; i<nlist.getLength();i++){
            Node nNode = nlist.item(i);
            Element node = (Element) nNode;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
            if(Integer.parseInt(node.getAttribute("id"))==id) {
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
                return toReturn;
            }
            }
        }
        return null;
    }

    public static void addLoan(Loan loanToAdd) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document doc = dbuilder.newDocument();
        Element elem = doc.createElement("loan");
        elem.setAttribute("id", String.valueOf(loanToAdd.id));
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

    public static void updateLoan(Loan updatedLoan) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
        int id = updatedLoan.id;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("loan");
        int tries = nlist.getLength();
        try {
            for (int i = 0; i < tries; i++) {
                Element loan = (Element) nlist.item(i);
                int loanId = Integer.parseInt(loan.getAttribute("id"));
                if (loanId == id) {
                    removeFromDoc(loan, "loan");
                    addLoan(updatedLoan);
                }
            }
        } catch (Exception e){
            //
        }
    }

    public static loanOffer loadOffer(int id) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("offer");
        for (int i = 0; i < nlist.getLength(); i++) {
            Node nNode = nlist.item(i);
            Element node = (Element) nNode;
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (Integer.parseInt(node.getAttribute("id")) == id) {
                    String temp = node.getElementsByTagName("creator").item(0).getTextContent();
                    Customer creator = databaseInterface.getCustomer(temp);
                    temp = node.getElementsByTagName("initial_amount").item(0).getTextContent();
                    double initialAmount = Double.parseDouble(temp);
                    temp = node.getElementsByTagName("days").item(0).getTextContent();
                    int days = Integer.parseInt(temp);
                    temp = node.getElementsByTagName("amount_due").item(0).getTextContent();
                    double repaymentAmount = Double.parseDouble(temp);
                    loanOffer offer = new loanOffer(creator, initialAmount, days, repaymentAmount);
                    offer.changeStatus(status.valueOf(node.getAttribute("status")));
                    offer.id = id;
                    return offer;
                }
            }
        }
        return null;
    }

    public static void addOffer(loanOffer offer) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        addToDoc(createPreLoanElement(offer, "offer"));
    }

    public static void updateOffer(loanOffer updatedOffer) throws ParserConfigurationException, IOException, SAXException {
        int Id = updatedOffer.id;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("offer");
        int tries = nlist.getLength();
        try {
            for (int i = 0; i < tries; i++) {
                Element loan = (Element) nlist.item(i);
                int loanId = Integer.parseInt(loan.getAttribute("id"));
                if (loanId == Id) {
                    removeFromDoc(loan, "offer");
                    addOffer(updatedOffer);
                }
            }
        } catch (Exception e){
            //
        }
    }

    public static loanRequest loadRequest(int id) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("request");
        for (int i = 0; i < nlist.getLength(); i++) {
                Node nNode = nlist.item(i);
                Element node = (Element) nNode;
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (Integer.parseInt(node.getAttribute("id")) == id) {
                        String temp = node.getElementsByTagName("creator").item(0).getTextContent();
                        Customer creator = databaseInterface.getCustomer(temp);
                        temp = node.getElementsByTagName("initial_amount").item(0).getTextContent();
                        double initialAmount = Double.parseDouble(temp);
                        temp = node.getElementsByTagName("days").item(0).getTextContent();
                        int days = Integer.parseInt(temp);
                        temp = node.getElementsByTagName("amount_due").item(0).getTextContent();
                        double repaymentAmount = Double.parseDouble(temp);
                        loanRequest request = new loanRequest(creator, initialAmount, days, repaymentAmount);
                        request.changeStatus(status.valueOf(node.getAttribute("status")));
                        request.id = id;
                        return request;
                    }
                }
        }
        return null;
    }

    public static void addRequest(loanRequest request) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        addToDoc(createPreLoanElement(request, "request"));
    }

    public static void updateRequest(loanRequest updatedRequest) throws ParserConfigurationException, IOException, SAXException {
        int Id = updatedRequest.id;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlist = xml.getElementsByTagName("request");
        int tries = nlist.getLength();
        try {
            for (int i = 0; i < tries; i++) {
                Element loan = (Element) nlist.item(i);
                int loanId = Integer.parseInt(loan.getAttribute("id"));
                if (loanId == Id) {
                    removeFromDoc(loan, "request");
                    addRequest(updatedRequest);
                }
            }
        } catch (Exception e){
            //
        }
    }

    public static Node createElement(Document doc, String name, String value){
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    public static Element createPreLoanElement(PreLoan preloan, String type) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document doc = dbuilder.newDocument();
        Element elem = doc.createElement(type);
        elem.setAttribute("id", String.valueOf(preloan.id));
        elem.setAttribute("status", String.valueOf(preloan.getLoanStatus()));
        elem.appendChild(createElement(doc, "creator", preloan.creator_username));
        elem.appendChild(createElement(doc, "initial_amount", String.valueOf(preloan.amount)));
        elem.appendChild(createElement(doc, "amount_due", String.valueOf(preloan.repaymentAmount)));
        elem.appendChild(createElement(doc, "days", String.valueOf(preloan.daysToRepayment)));
        return elem;
    }

    static int getNextPreLoanID() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = factory.newDocumentBuilder();
        Document xml = dbuilder.parse("mf_db.xml");
        NodeList nlistRequests = xml.getElementsByTagName("request");
        NodeList nlistOffer = xml.getElementsByTagName("offer");
        return nlistRequests.getLength()+nlistOffer.getLength()+1;
    }

    static void updatePreLoan(loanOffer offer) throws IOException, SAXException, ParserConfigurationException {
        updateOffer(offer);
    }
    static void updatePreLoan(loanRequest request) throws IOException, SAXException, ParserConfigurationException {
        updateRequest(request);
    }
}