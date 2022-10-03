package com.sun.org.apache.xpath.internal;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.XMLReaderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.XMLReader;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.URIResolver;
import java.util.Vector;

public class SourceTreeManager
{
    private Vector m_sourceTree;
    URIResolver m_uriResolver;
    
    public SourceTreeManager() {
        this.m_sourceTree = new Vector();
    }
    
    public void reset() {
        this.m_sourceTree = new Vector();
    }
    
    public void setURIResolver(final URIResolver resolver) {
        this.m_uriResolver = resolver;
    }
    
    public URIResolver getURIResolver() {
        return this.m_uriResolver;
    }
    
    public String findURIFromDoc(final int owner) {
        for (int n = this.m_sourceTree.size(), i = 0; i < n; ++i) {
            final SourceTree sTree = this.m_sourceTree.elementAt(i);
            if (owner == sTree.m_root) {
                return sTree.m_url;
            }
        }
        return null;
    }
    
    public Source resolveURI(final String base, final String urlString, final SourceLocator locator) throws TransformerException, IOException {
        Source source = null;
        if (null != this.m_uriResolver) {
            source = this.m_uriResolver.resolve(urlString, base);
        }
        if (null == source) {
            final String uri = SystemIDResolver.getAbsoluteURI(urlString, base);
            source = new StreamSource(uri);
        }
        return source;
    }
    
    public void removeDocumentFromCache(final int n) {
        if (-1 == n) {
            return;
        }
        for (int i = this.m_sourceTree.size() - 1; i >= 0; --i) {
            final SourceTree st = this.m_sourceTree.elementAt(i);
            if (st != null && st.m_root == n) {
                this.m_sourceTree.removeElementAt(i);
                return;
            }
        }
    }
    
    public void putDocumentInCache(final int n, final Source source) {
        final int cachedNode = this.getNode(source);
        if (-1 == cachedNode) {
            if (null != source.getSystemId()) {
                this.m_sourceTree.addElement(new SourceTree(n, source.getSystemId()));
            }
            return;
        }
        if (cachedNode != n) {
            throw new RuntimeException("Programmer's Error!  putDocumentInCache found reparse of doc: " + source.getSystemId());
        }
    }
    
    public int getNode(final Source source) {
        final String url = source.getSystemId();
        if (null == url) {
            return -1;
        }
        for (int n = this.m_sourceTree.size(), i = 0; i < n; ++i) {
            final SourceTree sTree = this.m_sourceTree.elementAt(i);
            if (url.equals(sTree.m_url)) {
                return sTree.m_root;
            }
        }
        return -1;
    }
    
    public int getSourceTree(final String base, final String urlString, final SourceLocator locator, final XPathContext xctxt) throws TransformerException {
        try {
            final Source source = this.resolveURI(base, urlString, locator);
            return this.getSourceTree(source, locator, xctxt);
        }
        catch (final IOException ioe) {
            throw new TransformerException(ioe.getMessage(), locator, ioe);
        }
    }
    
    public int getSourceTree(final Source source, final SourceLocator locator, final XPathContext xctxt) throws TransformerException {
        int n = this.getNode(source);
        if (-1 != n) {
            return n;
        }
        n = this.parseToNode(source, locator, xctxt);
        if (-1 != n) {
            this.putDocumentInCache(n, source);
        }
        return n;
    }
    
    public int parseToNode(final Source source, final SourceLocator locator, final XPathContext xctxt) throws TransformerException {
        try {
            final Object xowner = xctxt.getOwnerObject();
            DTM dtm;
            if (null != xowner && xowner instanceof DTMWSFilter) {
                dtm = xctxt.getDTM(source, false, (DTMWSFilter)xowner, false, true);
            }
            else {
                dtm = xctxt.getDTM(source, false, null, false, true);
            }
            return dtm.getDocument();
        }
        catch (final Exception e) {
            throw new TransformerException(e.getMessage(), locator, e);
        }
    }
    
    public static XMLReader getXMLReader(final Source inputSource, final SourceLocator locator) throws TransformerException {
        try {
            XMLReader reader = (inputSource instanceof SAXSource) ? ((SAXSource)inputSource).getXMLReader() : null;
            if (null == reader) {
                try {
                    final SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    final SAXParser jaxpParser = factory.newSAXParser();
                    reader = jaxpParser.getXMLReader();
                }
                catch (final ParserConfigurationException ex) {
                    throw new SAXException(ex);
                }
                catch (final FactoryConfigurationError ex2) {
                    throw new SAXException(ex2.toString());
                }
                catch (final NoSuchMethodError noSuchMethodError) {}
                catch (final AbstractMethodError abstractMethodError) {}
                if (null == reader) {
                    reader = XMLReaderFactory.createXMLReader();
                }
            }
            try {
                reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            catch (final SAXException ex3) {}
            return reader;
        }
        catch (final SAXException se) {
            throw new TransformerException(se.getMessage(), locator, se);
        }
    }
}
