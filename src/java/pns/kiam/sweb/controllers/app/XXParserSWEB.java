/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.app;

import java.io.File;
import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import pns.xmlUtils.SXParser;

/**
 *
 * @author User
 */
@Stateless
public class XXParserSWEB extends SXParser {

    @EJB
    private SsessionControl ssessionControl;

    private Attributes atts;
    private String login;
    private String thisElement = "";
    private String password;
    private String archivePath = "--";
    private int maxDaysFileLive = 100;

    /**
     * Get the value of login
     *
     * @return the value of login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Set the value of login
     *
     * @param login new value of login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public SsessionControl getSsessionControl() {
        return ssessionControl;
    }

    public int getMaxDaysFileLive() {
        return maxDaysFileLive;
    }

    public void setMaxDaysFileLive(int maxDaysFileLive) {
        this.maxDaysFileLive = maxDaysFileLive;
    }

    public void build() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        parser.parse(new File(docUrl), this);

    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        thisElement = qName;
        this.atts = atts;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // The value of the element
        String elementValue = new String(ch, start, length);
//	System.out.println("thisElement :  " + thisElement + ";  Value:    " + elementValue + "  start " + start + "   length " + length + " "
//		+ "           thisElement.length()  " + thisElement.length() + " // " + thisElement.trim().length() + "  "
//		+ "   atts.getValue(0) " + atts.getValue(0));
// archivePath
        if (thisElement.trim().equals("login")) {
            login = elementValue;
        }
        if (thisElement.trim().equals("password")) {
            password = elementValue;
        }
        if (thisElement.trim().equals("archivePath")) {
            archivePath = elementValue;
        }
        if (thisElement.trim().equals("maxDaysFileLive")) {
            maxDaysFileLive = Integer.parseInt(elementValue);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        thisElement = "";
    }

    @Override
    public void endDocument() {
        System.out.println("Stop parse XML...");
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void startDocument() throws SAXException {
        System.out.println("Start parse XML...");
    }
}
