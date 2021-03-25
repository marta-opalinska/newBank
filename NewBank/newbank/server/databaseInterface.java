package newbank.server;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;



public class databaseInterface {
    public static NodeList getRootNodeObj(String root){
        try {
            File fIn = new File("database.xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fIn);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName(root);
            return nodeList;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static Document getDoc(String root){
        try {
            File fIn = new File("database.xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fIn);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static Customer getCustomer(String userName){
        NodeList nodeList = getRootNodeObj("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                //Make sure checking correct password
                if (eElement.getElementsByTagName("username").item(0).getTextContent().equals(userName)){
                    Customer customer = new Customer(eElement.getElementsByTagName("username").item(0).getTextContent().toString(),eElement.getElementsByTagName("id").item(0).getTextContent().toString());
                    String accountsList = eElement.getElementsByTagName("accounts").item(0).getTextContent().toString();
                    String[] accountArray = accountsList.split(",");
                    for (String account : accountArray) {
                        customer.addAccount(new Account(account, getBalance(account,eElement.getElementsByTagName("id").item(0).getTextContent())));
                    }
                    return customer;
                }
            }
        }
        return null;
    }
    /*public static HashMap<String,Customer> readFile() throws IOException{
        //ArrayList<Customer> customers = new ArrayList<Customer>();
        HashMap<String,Customer> customers = new HashMap<String, Customer>();
        try
        {
            NodeList nodeList = getRootNodeObj("account");
            for (int itr = 0; itr < nodeList.getLength(); itr++)
            {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    Customer cust = new Customer();
                    String accountsList = eElement.getElementsByTagName("accounts").item(0).getTextContent().toString();
                    String[] accountArray = accountsList.split(",");
                    for (String account : accountArray) {
                        cust.addAccount(new Account(account, getBalance(account,eElement.getElementsByTagName("id").item(0).getTextContent())));
                    }
                    customers.put(eElement.getElementsByTagName("username").item(0).getTextContent(), cust);
                    //System.out.println("Account id: "+ eElement.getElementsByTagName("id").item(0).getTextContent());
                    //System.out.println("Name: "+ eElement.getElementsByTagName("username").item(0).getTextContent());
                    //System.out.println("Password: "+ eElement.getElementsByTagName("password").item(0).getTextContent());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return customers;
    }*/
    public static double getBalance(String accountType,String accountID) {
        NodeList nodeList = getRootNodeObj("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                if (eElement.getElementsByTagName("id").item(0).getTextContent().equals(accountID)) {
                    Element cElement = (Element) eElement.getElementsByTagName(accountType).item(0);
                    System.out.println("Account Balance : " + cElement.getElementsByTagName("balance").item(0).getTextContent());
                    String balance = cElement.getElementsByTagName("balance").item(0).getTextContent().toString();
                    try {
                        return Double.parseDouble(balance);
                    }
                    catch (Exception e){
                        return 0;
                    }
                }
            }
        }
        return 0;
    }
    public static boolean customerExists(Integer accountID){
        NodeList nodeList = getRootNodeObj("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                //Make sure checking correct password
                if (eElement.getElementsByTagName("id").item(0).getTextContent().equals(accountID.toString())){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean checkPassword(String userName, String password){
        NodeList nodeList = getRootNodeObj("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                //Make sure checking correct password
                if (eElement.getElementsByTagName("username").item(0).getTextContent().equals(userName)){
                    if (eElement.getElementsByTagName("password").item(0).getTextContent().equals(password)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void updateDatabase(Customer customer){
        Document doc = getDoc("account");
        NodeList nodeList = doc.getElementsByTagName("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                //Make sure checking correct password
                if (eElement.getElementsByTagName("id").item(0).getTextContent().equals(customer.getAccountID())){
                    int i = 0;
                    for (Account acc : customer.getAccounts()) {
                        Element cElement = (Element) eElement.getElementsByTagName(acc.getAccountName()).item(0);
                        cElement.getElementsByTagName("balance").item(0).setNodeValue(acc.getBalance().toString());
                        i++;
                    }
                }
            }
        }
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("database.xml"));
            transformer.transform(source, result);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
