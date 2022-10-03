package com.adventnet.management.log;

import java.util.Hashtable;
import org.w3c.dom.NamedNodeMap;
import java.util.Enumeration;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import org.w3c.dom.Node;
import java.util.Properties;
import java.util.Vector;

public class LoggingXMLReader
{
    private Vector logUserList;
    private Properties logUserProperties;
    private Node sysoutNode;
    private Node syserrNode;
    private static LoggingXMLReader lxr;
    
    public static LoggingXMLReader getInstance(final InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        if (LoggingXMLReader.lxr == null) {
            LoggingXMLReader.lxr = new LoggingXMLReader(inputStream);
        }
        return LoggingXMLReader.lxr;
    }
    
    public LoggingXMLReader(final InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        this.logUserList = new Vector();
        this.logUserProperties = new Properties();
        this.sysoutNode = null;
        this.syserrNode = null;
        final NodeList childNodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item != null) {
                if (item.getNodeType() == 1) {
                    final String nodeName = item.getNodeName();
                    if (nodeName.equals("LOG_USER")) {
                        this.logUserList.addElement(item);
                    }
                    else if (nodeName.equals("SYS_OUT")) {
                        this.sysoutNode = item;
                    }
                    else if (nodeName.equals("SYS_ERR")) {
                        this.syserrNode = item;
                    }
                }
            }
        }
        this.parseAllNodes();
    }
    
    private void parseAllNodes() {
        final Enumeration elements = this.logUserList.elements();
        while (elements.hasMoreElements()) {
            final Node node = (Node)elements.nextElement();
            ((Hashtable<String, Properties>)this.logUserProperties).put(this.getKeyValues(node.getChildNodes()), this.getAttributeList(node));
        }
    }
    
    private String getKeyValues(final NodeList list) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.getLength(); ++i) {
            final Node item = list.item(i);
            if (item.getNodeName().equals("KEY") && item.getNodeType() == 1) {
                final NamedNodeMap attributes = item.getAttributes();
                sb.append(attributes.getNamedItem("Name").getNodeValue() + " " + attributes.getNamedItem("DisplayName").getNodeValue() + " " + attributes.getNamedItem("LogLevel").getNodeValue() + " " + attributes.getNamedItem("Logging").getNodeValue() + " ");
            }
        }
        return sb.toString();
    }
    
    private Properties getAttributeList(final Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        final Properties properties = new Properties();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node item = attributes.item(i);
            ((Hashtable<String, String>)properties).put(item.getNodeName(), item.getNodeValue());
        }
        return properties;
    }
    
    public Properties getSysoutAttributes() {
        return this.getAttributeList(this.sysoutNode);
    }
    
    public Properties getSyserrAttributes() {
        return this.getAttributeList(this.syserrNode);
    }
    
    public Properties getLogUserProperties() {
        return this.logUserProperties;
    }
    
    private static Properties convertIntoProperties(final Hashtable hashtable) {
        final Properties properties = new Properties();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            ((Hashtable<String, String>)properties).put(s, hashtable.get(s));
        }
        return properties;
    }
    
    static {
        LoggingXMLReader.lxr = null;
    }
}
