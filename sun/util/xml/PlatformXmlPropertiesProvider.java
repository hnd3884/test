package sun.util.xml;

import org.xml.sax.SAXParseException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import java.util.Iterator;
import java.util.Map;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.io.OutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.util.InvalidPropertiesFormatException;
import java.io.InputStream;
import java.util.Properties;
import sun.util.spi.XmlPropertiesProvider;

public class PlatformXmlPropertiesProvider extends XmlPropertiesProvider
{
    private static final String PROPS_DTD_URI = "http://java.sun.com/dtd/properties.dtd";
    private static final String PROPS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>";
    private static final String EXTERNAL_XML_VERSION = "1.0";
    
    @Override
    public void load(final Properties properties, final InputStream inputStream) throws IOException, InvalidPropertiesFormatException {
        Document loadingDoc;
        try {
            loadingDoc = getLoadingDoc(inputStream);
        }
        catch (final SAXException ex) {
            throw new InvalidPropertiesFormatException(ex);
        }
        final Element documentElement = loadingDoc.getDocumentElement();
        final String attribute = documentElement.getAttribute("version");
        if (attribute.compareTo("1.0") > 0) {
            throw new InvalidPropertiesFormatException("Exported Properties file format version " + attribute + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
        }
        importProperties(properties, documentElement);
    }
    
    static Document getLoadingDoc(final InputStream byteStream) throws SAXException, IOException {
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setIgnoringElementContentWhitespace(true);
        instance.setValidating(true);
        instance.setCoalescing(true);
        instance.setIgnoringComments(true);
        try {
            final DocumentBuilder documentBuilder = instance.newDocumentBuilder();
            documentBuilder.setEntityResolver(new Resolver());
            documentBuilder.setErrorHandler(new EH());
            return documentBuilder.parse(new InputSource(byteStream));
        }
        catch (final ParserConfigurationException ex) {
            throw new Error(ex);
        }
    }
    
    static void importProperties(final Properties properties, final Element element) {
        final NodeList childNodes = element.getChildNodes();
        for (int length = childNodes.getLength(), i = (length > 0 && childNodes.item(0).getNodeName().equals("comment")) ? 1 : 0; i < length; ++i) {
            final Element element2 = (Element)childNodes.item(i);
            if (element2.hasAttribute("key")) {
                final Node firstChild = element2.getFirstChild();
                properties.setProperty(element2.getAttribute("key"), (firstChild == null) ? "" : firstChild.getNodeValue());
            }
        }
    }
    
    @Override
    public void store(final Properties properties, final OutputStream outputStream, final String s, final String s2) throws IOException {
        try {
            Charset.forName(s2);
        }
        catch (final IllegalCharsetNameException | UnsupportedCharsetException ex) {
            throw new UnsupportedEncodingException(s2);
        }
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = instance.newDocumentBuilder();
        }
        catch (final ParserConfigurationException ex2) {
            assert false;
        }
        final Document document = documentBuilder.newDocument();
        final Element element = (Element)document.appendChild(document.createElement("properties"));
        if (s != null) {
            element.appendChild(document.createElement("comment")).appendChild(document.createTextNode(s));
        }
        synchronized (properties) {
            for (final Map.Entry entry : properties.entrySet()) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                if (key instanceof String && value instanceof String) {
                    final Element element2 = (Element)element.appendChild(document.createElement("entry"));
                    element2.setAttribute("key", (String)key);
                    element2.appendChild(document.createTextNode((String)value));
                }
            }
        }
        emitDocument(document, outputStream, s2);
    }
    
    static void emitDocument(final Document n, final OutputStream outputStream, final String s) throws IOException {
        final TransformerFactory instance = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = instance.newTransformer();
            transformer.setOutputProperty("doctype-system", "http://java.sun.com/dtd/properties.dtd");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("method", "xml");
            transformer.setOutputProperty("encoding", s);
        }
        catch (final TransformerConfigurationException ex) {
            assert false;
        }
        final DOMSource domSource = new DOMSource(n);
        final StreamResult streamResult = new StreamResult(outputStream);
        try {
            transformer.transform(domSource, streamResult);
        }
        catch (final TransformerException ex2) {
            throw new IOException(ex2);
        }
    }
    
    private static class Resolver implements EntityResolver
    {
        @Override
        public InputSource resolveEntity(final String s, final String s2) throws SAXException {
            if (s2.equals("http://java.sun.com/dtd/properties.dtd")) {
                final InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for properties --><!ELEMENT properties ( comment?, entry* ) ><!ATTLIST properties version CDATA #FIXED \"1.0\"><!ELEMENT comment (#PCDATA) ><!ELEMENT entry (#PCDATA) ><!ATTLIST entry  key CDATA #REQUIRED>"));
                inputSource.setSystemId("http://java.sun.com/dtd/properties.dtd");
                return inputSource;
            }
            throw new SAXException("Invalid system identifier: " + s2);
        }
    }
    
    private static class EH implements ErrorHandler
    {
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
}
