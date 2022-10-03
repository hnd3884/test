package jdk.internal.util.xml;

import java.io.Reader;
import java.io.StringReader;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.Attributes;
import java.util.Iterator;
import java.util.Map;
import jdk.internal.util.xml.impl.XMLStreamWriterImpl;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import jdk.internal.org.xml.sax.SAXException;
import java.util.InvalidPropertiesFormatException;
import jdk.internal.util.xml.impl.SAXParserImpl;
import java.io.InputStream;
import java.util.Properties;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

public class PropertiesDefaultHandler extends DefaultHandler
{
    private static final String ELEMENT_ROOT = "properties";
    private static final String ELEMENT_COMMENT = "comment";
    private static final String ELEMENT_ENTRY = "entry";
    private static final String ATTR_KEY = "key";
    private static final String PROPS_DTD_DECL = "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">";
    private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
    private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
    private static final String EXTERNAL_XML_VERSION = "1.0";
    private Properties properties;
    static final String ALLOWED_ELEMENTS = "properties, comment, entry";
    static final String ALLOWED_COMMENT = "comment";
    StringBuffer buf;
    boolean sawComment;
    boolean validEntry;
    int rootElem;
    String key;
    String rootElm;
    
    public PropertiesDefaultHandler() {
        this.buf = new StringBuffer();
        this.sawComment = false;
        this.validEntry = false;
        this.rootElem = 0;
    }
    
    public void load(final Properties properties, final InputStream inputStream) throws IOException, InvalidPropertiesFormatException, UnsupportedEncodingException {
        this.properties = properties;
        try {
            new SAXParserImpl().parse(inputStream, this);
        }
        catch (final SAXException ex) {
            throw new InvalidPropertiesFormatException(ex);
        }
    }
    
    public void store(final Properties properties, final OutputStream outputStream, final String s, final String s2) throws IOException {
        try {
            final XMLStreamWriterImpl xmlStreamWriterImpl = new XMLStreamWriterImpl(outputStream, s2);
            xmlStreamWriterImpl.writeStartDocument();
            xmlStreamWriterImpl.writeDTD("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
            xmlStreamWriterImpl.writeStartElement("properties");
            if (s != null && s.length() > 0) {
                xmlStreamWriterImpl.writeStartElement("comment");
                xmlStreamWriterImpl.writeCharacters(s);
                xmlStreamWriterImpl.writeEndElement();
            }
            synchronized (properties) {
                for (final Map.Entry entry : properties.entrySet()) {
                    final Object key = entry.getKey();
                    final Object value = entry.getValue();
                    if (key instanceof String && value instanceof String) {
                        xmlStreamWriterImpl.writeStartElement("entry");
                        xmlStreamWriterImpl.writeAttribute("key", (String)key);
                        xmlStreamWriterImpl.writeCharacters((String)value);
                        xmlStreamWriterImpl.writeEndElement();
                    }
                }
            }
            xmlStreamWriterImpl.writeEndElement();
            xmlStreamWriterImpl.writeEndDocument();
            xmlStreamWriterImpl.close();
        }
        catch (final XMLStreamException ex) {
            if (ex.getCause() instanceof UnsupportedEncodingException) {
                throw (UnsupportedEncodingException)ex.getCause();
            }
            throw new IOException(ex);
        }
    }
    
    @Override
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.rootElem < 2) {
            ++this.rootElem;
        }
        if (this.rootElm == null) {
            this.fatalError(new SAXParseException("An XML properties document must contain the DOCTYPE declaration as defined by java.util.Properties.", (Locator)null));
        }
        if (this.rootElem == 1 && !this.rootElm.equals(s3)) {
            this.fatalError(new SAXParseException("Document root element \"" + s3 + "\", must match DOCTYPE root \"" + this.rootElm + "\"", (Locator)null));
        }
        if (!"properties, comment, entry".contains(s3)) {
            this.fatalError(new SAXParseException("Element type \"" + s3 + "\" must be declared.", (Locator)null));
        }
        if (s3.equals("entry")) {
            this.validEntry = true;
            this.key = attributes.getValue("key");
            if (this.key == null) {
                this.fatalError(new SAXParseException("Attribute \"key\" is required and must be specified for element type \"entry\"", (Locator)null));
            }
        }
        else if (s3.equals("comment")) {
            if (this.sawComment) {
                this.fatalError(new SAXParseException("Only one comment element may be allowed. The content of element type \"properties\" must match \"(comment?,entry*)\"", (Locator)null));
            }
            this.sawComment = true;
        }
    }
    
    @Override
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.validEntry) {
            this.buf.append(array, n, n2);
        }
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        if (!"properties, comment, entry".contains(s3)) {
            this.fatalError(new SAXParseException("Element: " + s3 + " is invalid, must match  \"(comment?,entry*)\".", (Locator)null));
        }
        if (this.validEntry) {
            this.properties.setProperty(this.key, this.buf.toString());
            this.buf.delete(0, this.buf.length());
            this.validEntry = false;
        }
    }
    
    @Override
    public void notationDecl(final String rootElm, final String s, final String s2) throws SAXException {
        this.rootElm = rootElm;
    }
    
    @Override
    public InputSource resolveEntity(final String s, final String s2) throws SAXException, IOException {
        if (s2.equals("http://java.sun.com/dtd/properties.dtd")) {
            final InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
            inputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
            return inputSource;
        }
        throw new SAXException("Invalid system identifier: " + s2);
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        throw ex;
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
        throw ex;
    }
}
