package org.owasp.esapi.configuration;

import java.util.Hashtable;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ConfigurationException;

public class XmlEsapiPropertyLoader extends AbstractPrioritizedPropertyLoader
{
    public XmlEsapiPropertyLoader(final String filename, final int priority) {
        super(filename, priority);
    }
    
    @Override
    public int getIntProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + " not found in default configuration");
        }
        try {
            return Integer.parseInt(property);
        }
        catch (final NumberFormatException e) {
            throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to integer", e);
        }
    }
    
    @Override
    public byte[] getByteArrayProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + " not found in default configuration");
        }
        try {
            return ESAPI.encoder().decodeFromBase64(property);
        }
        catch (final IOException e) {
            throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to byte array", e);
        }
    }
    
    @Override
    public Boolean getBooleanProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + " not found in default configuration");
        }
        if (property.equalsIgnoreCase("true") || property.equalsIgnoreCase("yes")) {
            return true;
        }
        if (property.equalsIgnoreCase("false") || property.equalsIgnoreCase("no")) {
            return false;
        }
        throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to boolean");
    }
    
    @Override
    public String getStringProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + " not found in default configuration");
        }
        return property;
    }
    
    @Override
    protected void loadPropertiesFromFile(final File file) throws ConfigurationException {
        try {
            this.validateAgainstXSD(new FileInputStream(file));
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            final NodeList nodeList = doc.getElementsByTagName("property");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == 1) {
                    final Element element = (Element)node;
                    final String propertyKey = element.getAttribute("name");
                    final String propertyValue = element.getTextContent();
                    ((Hashtable<String, String>)this.properties).put(propertyKey, propertyValue);
                }
            }
        }
        catch (final Exception e) {
            throw new ConfigurationException("Configuration file : " + this.filename + " has invalid schema." + e.getMessage(), e);
        }
    }
    
    private void validateAgainstXSD(final InputStream xml) throws Exception {
        final InputStream xsd = this.getClass().getResourceAsStream("/ESAPI-properties.xsd");
        final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final Schema schema = factory.newSchema(new StreamSource(xsd));
        final Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
    }
}
