package com.sun.org.apache.xml.internal.resolver.readers;

import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import sun.reflect.misc.ReflectUtil;
import com.sun.org.apache.xml.internal.resolver.helpers.Namespaces;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import java.util.HashMap;
import java.util.Map;

public class DOMCatalogReader implements CatalogReader
{
    protected Map<String, String> namespaceMap;
    
    public void setCatalogParser(final String namespaceURI, final String rootElement, final String parserClass) {
        if (namespaceURI == null) {
            this.namespaceMap.put(rootElement, parserClass);
        }
        else {
            this.namespaceMap.put("{" + namespaceURI + "}" + rootElement, parserClass);
        }
    }
    
    public String getCatalogParser(final String namespaceURI, final String rootElement) {
        if (namespaceURI == null) {
            return this.namespaceMap.get(rootElement);
        }
        return this.namespaceMap.get("{" + namespaceURI + "}" + rootElement);
    }
    
    public DOMCatalogReader() {
        this.namespaceMap = new HashMap<String, String>();
    }
    
    @Override
    public void readCatalog(final Catalog catalog, final InputStream is) throws IOException, CatalogException {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        try {
            builder = factory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException pce) {
            throw new CatalogException(6);
        }
        Document doc = null;
        try {
            doc = builder.parse(is);
        }
        catch (final SAXException se) {
            throw new CatalogException(5);
        }
        final Element root = doc.getDocumentElement();
        final String namespaceURI = Namespaces.getNamespaceURI(root);
        final String localName = Namespaces.getLocalName(root);
        final String domParserClass = this.getCatalogParser(namespaceURI, localName);
        if (domParserClass == null) {
            if (namespaceURI == null) {
                catalog.getCatalogManager().debug.message(1, "No Catalog parser for " + localName);
            }
            else {
                catalog.getCatalogManager().debug.message(1, "No Catalog parser for {" + namespaceURI + "}" + localName);
            }
            return;
        }
        DOMCatalogParser domParser = null;
        try {
            domParser = (DOMCatalogParser)ReflectUtil.forName(domParserClass).newInstance();
        }
        catch (final ClassNotFoundException cnfe) {
            catalog.getCatalogManager().debug.message(1, "Cannot load XML Catalog Parser class", domParserClass);
            throw new CatalogException(6);
        }
        catch (final InstantiationException ie) {
            catalog.getCatalogManager().debug.message(1, "Cannot instantiate XML Catalog Parser class", domParserClass);
            throw new CatalogException(6);
        }
        catch (final IllegalAccessException iae) {
            catalog.getCatalogManager().debug.message(1, "Cannot access XML Catalog Parser class", domParserClass);
            throw new CatalogException(6);
        }
        catch (final ClassCastException cce) {
            catalog.getCatalogManager().debug.message(1, "Cannot cast XML Catalog Parser class", domParserClass);
            throw new CatalogException(6);
        }
        for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
            domParser.parseCatalogEntry(catalog, node);
        }
    }
    
    @Override
    public void readCatalog(final Catalog catalog, final String fileUrl) throws MalformedURLException, IOException, CatalogException {
        final URL url = new URL(fileUrl);
        final URLConnection urlCon = url.openConnection();
        this.readCatalog(catalog, urlCon.getInputStream());
    }
}
