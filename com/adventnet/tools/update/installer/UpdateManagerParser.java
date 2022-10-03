package com.adventnet.tools.update.installer;

import java.util.Hashtable;
import org.xml.sax.ErrorHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;
import org.w3c.dom.Element;
import java.util.Properties;
import org.w3c.dom.Document;

public class UpdateManagerParser
{
    private String fileName;
    private Document smartXmlDoc;
    
    public UpdateManagerParser(final String confFileName) {
        this.fileName = null;
        this.smartXmlDoc = null;
        this.fileName = confFileName;
        this.loadFile();
    }
    
    public Properties getLogImplClassProps() {
        Properties logProps = null;
        try {
            final Element rootElem = this.smartXmlDoc.getDocumentElement();
            if (rootElem == null) {
                return null;
            }
            final Element logElem = this.getChildElementNamed("Log", rootElem);
            if (logElem == null) {
                return null;
            }
            logProps = this.getClassProps(logElem);
        }
        catch (final Exception excp) {
            return null;
        }
        return logProps;
    }
    
    public void dispose() {
        this.fileName = null;
        this.smartXmlDoc = null;
    }
    
    private Element getElementNamed(final String name) throws Exception {
        final Element rootElem = this.smartXmlDoc.getDocumentElement();
        return this.getChildElementNamed(name, rootElem);
    }
    
    public Properties getGeneralProps() {
        Properties genProps = null;
        try {
            final Element rootElem = this.smartXmlDoc.getDocumentElement();
            if (rootElem == null) {
                return null;
            }
            genProps = new Properties();
            final Properties otherProps = this.getPropertyValues(rootElem);
            if (otherProps != null) {
                genProps.putAll(otherProps);
            }
        }
        catch (final Exception excp) {
            System.err.println("Exception while getting General Properties");
            return null;
        }
        return genProps;
    }
    
    private Properties getClassProps(final Element classRootElem) throws Exception {
        final Element classElem = this.getChildElementNamed("Class", classRootElem);
        if (classElem == null) {
            return null;
        }
        final String className = this.getElementValue(classElem);
        if (className == null) {
            return null;
        }
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("className", className);
        final Properties classProps = this.getPropertyValues(classRootElem);
        if (classProps != null) {
            props.putAll(classProps);
        }
        return props;
    }
    
    private void loadFile() {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = dbFactory.newDocumentBuilder();
            this.smartXmlDoc = builder.parse(new File(this.fileName));
        }
        catch (final Error err) {
            System.err.println("Error while parsing update_conf.xml" + err.getMessage());
        }
        catch (final SAXParseException sxe) {
            Exception x = sxe;
            System.err.println("Exception while parsing update_conf.xml" + sxe.getMessage());
            System.err.println("The line number is " + sxe.getLineNumber());
            if (sxe.getException() != null) {
                x = sxe.getException();
            }
        }
        catch (final SAXException sxe2) {
            Exception x = sxe2;
            System.err.println("The SAXException while loading update_conf.xml " + sxe2);
            if (sxe2.getException() != null) {
                x = sxe2.getException();
            }
        }
        catch (final ParserConfigurationException pce) {
            System.err.println("The ParserConfigurationException while loading update_conf.xml " + pce);
        }
        catch (final FileNotFoundException fnfe) {
            System.err.println(" File not found exception " + fnfe);
        }
        catch (final IOException ioe) {
            System.err.println(" IOexception while parsing update_conf.xml " + ioe);
        }
    }
    
    private Properties getPropertyValues(final Element element) throws Exception {
        final NodeList list = element.getElementsByTagName("property");
        final int size = list.getLength();
        if (size == 0) {
            return null;
        }
        final Properties prop = new Properties();
        for (int i = 0; i < size; ++i) {
            final Node node = list.item(i);
            if (node.getNodeType() == 1) {
                final Element propertyElement = (Element)node;
                final String key = propertyElement.getAttribute("name");
                final String value = propertyElement.getAttribute("value");
                prop.setProperty(key, value);
            }
        }
        return prop;
    }
    
    private String getElementValue(final Element element) throws Exception {
        final Node node = element.getFirstChild();
        if (node.getNodeType() != 3) {
            throw new Exception("" + element);
        }
        return node.getNodeValue();
    }
    
    private Element getChildElementNamed(final String name, final Element element) throws Exception {
        final NodeList list = element.getElementsByTagName(name);
        if (list.getLength() == -1) {
            throw new Exception();
        }
        final Node node = list.item(0);
        if (node.getNodeType() != 1) {
            throw new Exception();
        }
        return (Element)node;
    }
    
    private void setErrorHandler(final DocumentBuilder builder) {
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
            }
            
            @Override
            public void error(final SAXParseException e) throws SAXParseException {
                throw e;
            }
            
            @Override
            public void warning(final SAXParseException err) throws SAXParseException {
                System.err.println("** Warning, line " + err.getLineNumber() + ", uri " + err.getSystemId());
                System.err.println("   " + err.getMessage());
            }
        });
    }
}
