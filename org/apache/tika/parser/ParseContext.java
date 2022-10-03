package org.apache.tika.parser;

import javax.xml.transform.Transformer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.XMLReader;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ParseContext implements Serializable
{
    private static final long serialVersionUID = -5921436862145826534L;
    private final Map<String, Object> context;
    
    public ParseContext() {
        this.context = new HashMap<String, Object>();
    }
    
    public <T> void set(final Class<T> key, final T value) {
        if (value != null) {
            this.context.put(key.getName(), value);
        }
        else {
            this.context.remove(key.getName());
        }
    }
    
    public <T> T get(final Class<T> key) {
        return (T)this.context.get(key.getName());
    }
    
    public <T> T get(final Class<T> key, final T defaultValue) {
        final T value = this.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
    
    public XMLReader getXMLReader() throws TikaException {
        final XMLReader reader = this.get(XMLReader.class);
        if (reader != null) {
            return reader;
        }
        return XMLReaderUtils.getXMLReader();
    }
    
    public SAXParser getSAXParser() throws TikaException {
        final SAXParser parser = this.get(SAXParser.class);
        if (parser != null) {
            return parser;
        }
        return XMLReaderUtils.getSAXParser();
    }
    
    public SAXParserFactory getSAXParserFactory() {
        SAXParserFactory factory = this.get(SAXParserFactory.class);
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            try {
                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (final ParserConfigurationException | SAXNotSupportedException ex) {}
            catch (final SAXNotRecognizedException ex2) {}
        }
        return factory;
    }
    
    private DocumentBuilderFactory getDocumentBuilderFactory() {
        final DocumentBuilderFactory documentBuilderFactory = this.get(DocumentBuilderFactory.class);
        if (documentBuilderFactory != null) {
            return documentBuilderFactory;
        }
        return XMLReaderUtils.getDocumentBuilderFactory();
    }
    
    public DocumentBuilder getDocumentBuilder() throws TikaException {
        final DocumentBuilder documentBuilder = this.get(DocumentBuilder.class);
        if (documentBuilder != null) {
            return documentBuilder;
        }
        return XMLReaderUtils.getDocumentBuilder();
    }
    
    public XMLInputFactory getXMLInputFactory() {
        final XMLInputFactory factory = this.get(XMLInputFactory.class);
        if (factory != null) {
            return factory;
        }
        return XMLReaderUtils.getXMLInputFactory();
    }
    
    public Transformer getTransformer() throws TikaException {
        final Transformer transformer = this.get(Transformer.class);
        if (transformer != null) {
            return transformer;
        }
        return XMLReaderUtils.getTransformer();
    }
}
