package java.util.prefs;

import org.xml.sax.SAXParseException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.w3c.dom.DOMImplementation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.IOException;
import org.w3c.dom.Document;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.OutputStream;

class XmlSupport
{
    private static final String PREFS_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";
    private static final String PREFS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >";
    private static final String EXTERNAL_XML_VERSION = "1.0";
    private static final String MAP_XML_VERSION = "1.0";
    
    static void export(final OutputStream outputStream, final Preferences preferences, final boolean b) throws IOException, BackingStoreException {
        if (((AbstractPreferences)preferences).isRemoved()) {
            throw new IllegalStateException("Node has been removed");
        }
        final Document prefsDoc = createPrefsDoc("preferences");
        final Element documentElement = prefsDoc.getDocumentElement();
        documentElement.setAttribute("EXTERNAL_XML_VERSION", "1.0");
        final Element element = (Element)documentElement.appendChild(prefsDoc.createElement("root"));
        element.setAttribute("type", preferences.isUserNode() ? "user" : "system");
        final ArrayList list = new ArrayList();
        for (Preferences preferences2 = preferences, preferences3 = preferences2.parent(); preferences3 != null; preferences3 = preferences2.parent()) {
            list.add(preferences2);
            preferences2 = preferences3;
        }
        Element element2 = element;
        for (int i = list.size() - 1; i >= 0; --i) {
            element2.appendChild(prefsDoc.createElement("map"));
            element2 = (Element)element2.appendChild(prefsDoc.createElement("node"));
            element2.setAttribute("name", ((Preferences)list.get(i)).name());
        }
        putPreferencesInXml(element2, prefsDoc, preferences, b);
        writeDoc(prefsDoc, outputStream);
    }
    
    private static void putPreferencesInXml(final Element element, final Document document, final Preferences preferences, final boolean b) throws BackingStoreException {
        Preferences[] array = null;
        String[] childrenNames = null;
        synchronized (((AbstractPreferences)preferences).lock) {
            if (((AbstractPreferences)preferences).isRemoved()) {
                element.getParentNode().removeChild(element);
                return;
            }
            final String[] keys = preferences.keys();
            final Element element2 = (Element)element.appendChild(document.createElement("map"));
            for (int i = 0; i < keys.length; ++i) {
                final Element element3 = (Element)element2.appendChild(document.createElement("entry"));
                element3.setAttribute("key", keys[i]);
                element3.setAttribute("value", preferences.get(keys[i], null));
            }
            if (b) {
                childrenNames = preferences.childrenNames();
                array = new Preferences[childrenNames.length];
                for (int j = 0; j < childrenNames.length; ++j) {
                    array[j] = preferences.node(childrenNames[j]);
                }
            }
        }
        if (b) {
            for (int k = 0; k < childrenNames.length; ++k) {
                final Element element4 = (Element)element.appendChild(document.createElement("node"));
                element4.setAttribute("name", childrenNames[k]);
                putPreferencesInXml(element4, document, array[k], b);
            }
        }
    }
    
    static void importPreferences(final InputStream inputStream) throws IOException, InvalidPreferencesFormatException {
        try {
            final Document loadPrefsDoc = loadPrefsDoc(inputStream);
            final String attribute = loadPrefsDoc.getDocumentElement().getAttribute("EXTERNAL_XML_VERSION");
            if (attribute.compareTo("1.0") > 0) {
                throw new InvalidPreferencesFormatException("Exported preferences file format version " + attribute + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
            }
            final Element element = (Element)loadPrefsDoc.getDocumentElement().getChildNodes().item(0);
            ImportSubtree(element.getAttribute("type").equals("user") ? Preferences.userRoot() : Preferences.systemRoot(), element);
        }
        catch (final SAXException ex) {
            throw new InvalidPreferencesFormatException(ex);
        }
    }
    
    private static Document createPrefsDoc(final String s) {
        try {
            final DOMImplementation domImplementation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
            return domImplementation.createDocument(null, s, domImplementation.createDocumentType(s, null, "http://java.sun.com/dtd/preferences.dtd"));
        }
        catch (final ParserConfigurationException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    private static Document loadPrefsDoc(final InputStream byteStream) throws SAXException, IOException {
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
            throw new AssertionError((Object)ex);
        }
    }
    
    private static final void writeDoc(final Document n, final OutputStream outputStream) throws IOException {
        try {
            final TransformerFactory instance = TransformerFactory.newInstance();
            try {
                instance.setAttribute("indent-number", new Integer(2));
            }
            catch (final IllegalArgumentException ex) {}
            final Transformer transformer = instance.newTransformer();
            transformer.setOutputProperty("doctype-system", n.getDoctype().getSystemId());
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(new DOMSource(n), new StreamResult(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))));
        }
        catch (final TransformerException ex2) {
            throw new AssertionError((Object)ex2);
        }
    }
    
    private static void ImportSubtree(final Preferences preferences, final Element element) {
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        final Preferences[] array;
        synchronized (((AbstractPreferences)preferences).lock) {
            if (((AbstractPreferences)preferences).isRemoved()) {
                return;
            }
            ImportPrefs(preferences, (Element)childNodes.item(0));
            array = new Preferences[length - 1];
            for (int i = 1; i < length; ++i) {
                array[i - 1] = preferences.node(((Element)childNodes.item(i)).getAttribute("name"));
            }
        }
        for (int j = 1; j < length; ++j) {
            ImportSubtree(array[j - 1], (Element)childNodes.item(j));
        }
    }
    
    private static void ImportPrefs(final Preferences preferences, final Element element) {
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Element element2 = (Element)childNodes.item(i);
            preferences.put(element2.getAttribute("key"), element2.getAttribute("value"));
        }
    }
    
    static void exportMap(final OutputStream outputStream, final Map<String, String> map) throws IOException {
        final Document prefsDoc = createPrefsDoc("map");
        final Element documentElement = prefsDoc.getDocumentElement();
        documentElement.setAttribute("MAP_XML_VERSION", "1.0");
        for (final Map.Entry entry : map.entrySet()) {
            final Element element = (Element)documentElement.appendChild(prefsDoc.createElement("entry"));
            element.setAttribute("key", (String)entry.getKey());
            element.setAttribute("value", (String)entry.getValue());
        }
        writeDoc(prefsDoc, outputStream);
    }
    
    static void importMap(final InputStream inputStream, final Map<String, String> map) throws IOException, InvalidPreferencesFormatException {
        try {
            final Element documentElement = loadPrefsDoc(inputStream).getDocumentElement();
            final String attribute = documentElement.getAttribute("MAP_XML_VERSION");
            if (attribute.compareTo("1.0") > 0) {
                throw new InvalidPreferencesFormatException("Preferences map file format version " + attribute + " is not supported. This java installation can read versions " + "1.0" + " or older. You may need to install a newer version of JDK.");
            }
            final NodeList childNodes = documentElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Element element = (Element)childNodes.item(i);
                map.put(element.getAttribute("key"), element.getAttribute("value"));
            }
        }
        catch (final SAXException ex) {
            throw new InvalidPreferencesFormatException(ex);
        }
    }
    
    private static class Resolver implements EntityResolver
    {
        @Override
        public InputSource resolveEntity(final String s, final String s2) throws SAXException {
            if (s2.equals("http://java.sun.com/dtd/preferences.dtd")) {
                final InputSource inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >"));
                inputSource.setSystemId("http://java.sun.com/dtd/preferences.dtd");
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
