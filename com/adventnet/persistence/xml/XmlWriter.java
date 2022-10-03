package com.adventnet.persistence.xml;

import java.util.Hashtable;
import java.io.IOException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import com.zoho.mickey.api.TransformerFactoryUtil;
import java.io.OutputStream;
import org.w3c.dom.Element;
import java.util.logging.Logger;

class XmlWriter
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    String xmlVersion;
    
    XmlWriter() {
        this.xmlVersion = "1.0";
    }
    
    void setXmlVersion(final String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }
    
    void write(final Element element, final OutputStream os, final String encodingString) throws TransformerException, TransformerFactoryConfigurationError, IllegalArgumentException, IOException {
        XmlWriter.LOGGER.entering(XmlWriter.CLASS_NAME, "write", new Object[] { element });
        final Transformer transformer = TransformerFactoryUtil.newInstance().newTransformer();
        final DOMSource source = new DOMSource(element);
        final StreamResult result = new StreamResult(os);
        String es = encodingString;
        if (this.isNullOrEmpty(es)) {
            es = PersistenceInitializer.getConfigurationValue("xml-encoding-string");
            if (this.isNullOrEmpty(es)) {
                es = "iso-8859-1";
            }
        }
        XmlWriter.LOGGER.log(Level.FINE, "es :: [{0}]", es);
        final Properties prop = new Properties();
        prop.setProperty("encoding", es);
        prop.setProperty("indent", "yes");
        prop.setProperty("method", "xml");
        ((Hashtable<String, String>)prop).put("{http://xml.apache.org/xslt}indent-amount", "4");
        prop.setProperty("version", this.xmlVersion);
        transformer.setOutputProperties(prop);
        transformer.transform(source, result);
        XmlWriter.LOGGER.exiting(XmlWriter.CLASS_NAME, "write");
    }
    
    private boolean isNullOrEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }
    
    static {
        CLASS_NAME = XmlWriter.class.getName();
        LOGGER = Logger.getLogger(XmlWriter.CLASS_NAME);
    }
}
