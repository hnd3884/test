package com.me.tools.zcutil;

import java.util.Properties;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class LicenseReader
{
    private static Element userNode;
    private static Element productNode;
    private static NodeList componentList;
    
    public LicenseReader(final String licenseFile) {
        this.loadFile(licenseFile);
    }
    
    private void loadFile(final String fileName) {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = dbFactory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(new File(fileName));
            LicenseReader.userNode = (Element)xmlDoc.getElementsByTagName("UserInfo").item(0);
            LicenseReader.productNode = (Element)xmlDoc.getElementsByTagName("Product").item(0);
            LicenseReader.componentList = xmlDoc.getElementsByTagName("Component");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private NodeList getPropertyNodeList(final String compName) {
        if (compName != null) {
            Element compElement = null;
            for (int i = 0; i < LicenseReader.componentList.getLength(); ++i) {
                if (LicenseReader.componentList.item(i).getNodeType() == 1) {
                    compElement = (Element)LicenseReader.componentList.item(i);
                    if (compElement.getAttribute("Name").equals(compName)) {
                        return compElement.getElementsByTagName("Properties");
                    }
                }
            }
        }
        return null;
    }
    
    public Element getUserNode() {
        return LicenseReader.userNode;
    }
    
    public Element getProductNode() {
        return LicenseReader.productNode;
    }
    
    public NodeList getComponentList() {
        return LicenseReader.componentList;
    }
    
    public Properties getModuleProperties(final String compName) {
        final Properties prop = new Properties();
        try {
            final NodeList propNodeList = this.getPropertyNodeList(compName);
            Element propElement = null;
            if (propNodeList != null && propNodeList.getLength() > 0) {
                for (int i = 0; i < propNodeList.getLength(); ++i) {
                    propElement = (Element)propNodeList.item(i);
                    prop.setProperty(propElement.getAttribute("Name"), propElement.getAttribute("Value"));
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return prop;
    }
    
    public String getEvaluationExpiryDate() {
        if (LicenseReader.userNode.hasAttribute("NoOfDays")) {
            return LicenseReader.userNode.getAttribute("NoOfDays");
        }
        if (LicenseReader.userNode.hasAttribute("ExpiryDate")) {
            return LicenseReader.userNode.getAttribute("ExpiryDate");
        }
        if (!LicenseReader.userNode.hasAttribute("ExpiryDate") && !LicenseReader.userNode.hasAttribute("NoOfDays") && (LicenseReader.userNode.getAttribute("LicenseType").equals("Registered") || LicenseReader.userNode.getAttribute("LicenseType").equals("Free"))) {
            return "never";
        }
        return "unknown";
    }
    
    public String getUserType() {
        String userType = LicenseReader.userNode.getAttribute("LicenseType");
        if (userType.equals("Evaluation")) {
            userType = "Trial";
        }
        return userType;
    }
    
    static {
        LicenseReader.userNode = null;
        LicenseReader.productNode = null;
        LicenseReader.componentList = null;
    }
}
