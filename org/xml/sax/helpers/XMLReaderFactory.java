package org.xml.sax.helpers;

import java.io.InputStream;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.xml.sax.XMLReader;

public final class XMLReaderFactory
{
    private static final String property = "org.xml.sax.driver";
    private static final int DEFAULT_LINE_LENGTH = 80;
    
    private XMLReaderFactory() {
    }
    
    public static XMLReader createXMLReader() throws SAXException {
        String s = null;
        final ClassLoader classLoader = NewInstance.getClassLoader();
        try {
            s = SecuritySupport.getSystemProperty("org.xml.sax.driver");
        }
        catch (final Exception ex) {}
        if (s == null || s.length() == 0) {
            final String s2 = "META-INF/services/org.xml.sax.driver";
            s = null;
            final ClassLoader contextClassLoader = SecuritySupport.getContextClassLoader();
            InputStream inputStream;
            if (contextClassLoader != null) {
                inputStream = SecuritySupport.getResourceAsStream(contextClassLoader, s2);
                if (inputStream == null) {
                    inputStream = SecuritySupport.getResourceAsStream(XMLReaderFactory.class.getClassLoader(), s2);
                }
            }
            else {
                inputStream = SecuritySupport.getResourceAsStream(XMLReaderFactory.class.getClassLoader(), s2);
            }
            if (inputStream != null) {
                BufferedReader bufferedReader;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
                }
                catch (final UnsupportedEncodingException ex2) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
                }
                try {
                    s = bufferedReader.readLine();
                }
                catch (final Exception ex3) {}
                finally {
                    try {
                        bufferedReader.close();
                    }
                    catch (final IOException ex4) {}
                }
            }
        }
        if (s == null) {
            s = "org.apache.xerces.parsers.SAXParser";
        }
        if (s != null) {
            return loadClass(classLoader, s);
        }
        try {
            return new ParserAdapter(ParserFactory.makeParser());
        }
        catch (final Exception ex5) {
            throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
        }
    }
    
    public static XMLReader createXMLReader(final String s) throws SAXException {
        return loadClass(NewInstance.getClassLoader(), s);
    }
    
    private static XMLReader loadClass(final ClassLoader classLoader, final String s) throws SAXException {
        try {
            return (XMLReader)NewInstance.newInstance(classLoader, s);
        }
        catch (final ClassNotFoundException ex) {
            throw new SAXException("SAX2 driver class " + s + " not found", ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new SAXException("SAX2 driver class " + s + " found but cannot be loaded", ex2);
        }
        catch (final InstantiationException ex3) {
            throw new SAXException("SAX2 driver class " + s + " loaded but cannot be instantiated (no empty public constructor?)", ex3);
        }
        catch (final ClassCastException ex4) {
            throw new SAXException("SAX2 driver class " + s + " does not implement XMLReader", ex4);
        }
    }
}
