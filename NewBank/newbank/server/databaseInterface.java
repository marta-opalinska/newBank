package newbank.server;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;


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
    public static HashMap<String,Customer> readFile() throws IOException{
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

                    cust.addAccount(new Account("main", getBalance(eElement.getElementsByTagName("id").item(0).getTextContent())));
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
    }
    public static double getBalance(String accountID) {
        NodeList nodeList = getRootNodeObj("account");
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                if (eElement.getElementsByTagName("id").item(0).getTextContent().equals(accountID)) {
                    Element cElement = (Element) eElement.getElementsByTagName("main").item(0);
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
    public static void writeFile1() throws IOException {
        File fout = new File("database.xml");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < 10; i++) {
            bw.write("something");
            bw.newLine();
        }

        bw.close();
    }
}
