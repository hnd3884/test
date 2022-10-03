package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import java.util.Date;
import java.io.PrintWriter;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import java.net.URLConnection;
import java.io.File;
import java.net.URLDecoder;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashMap;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import java.util.Map;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;

public final class DocumentCache implements DOMCache
{
    private int _size;
    private Map<String, CachedDocument> _references;
    private String[] _URIs;
    private int _count;
    private int _current;
    private SAXParser _parser;
    private XMLReader _reader;
    private XSLTCDTMManager _dtmManager;
    private static final int REFRESH_INTERVAL = 1000;
    
    public DocumentCache(final int size) throws SAXException {
        this(size, null);
        try {
            this._dtmManager = XSLTCDTMManager.createNewDTMManagerInstance();
        }
        catch (final Exception e) {
            throw new SAXException(e);
        }
    }
    
    public DocumentCache(final int size, final XSLTCDTMManager dtmManager) throws SAXException {
        this._dtmManager = dtmManager;
        this._count = 0;
        this._current = 0;
        this._size = size;
        this._references = new HashMap<String, CachedDocument>(this._size + 2);
        this._URIs = new String[this._size];
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
            }
            catch (final Exception e) {
                factory.setNamespaceAware(true);
            }
            this._parser = factory.newSAXParser();
            this._reader = this._parser.getXMLReader();
        }
        catch (final ParserConfigurationException e2) {
            BasisLibrary.runTimeError("NAMESPACES_SUPPORT_ERR");
        }
    }
    
    private final long getLastModified(final String uri) {
        try {
            final URL url = new URL(uri);
            final URLConnection connection = url.openConnection();
            long timestamp = connection.getLastModified();
            if (timestamp == 0L && "file".equals(url.getProtocol())) {
                final File localfile = new File(URLDecoder.decode(url.getFile()));
                timestamp = localfile.lastModified();
            }
            return timestamp;
        }
        catch (final Exception e) {
            return System.currentTimeMillis();
        }
    }
    
    private CachedDocument lookupDocument(final String uri) {
        return this._references.get(uri);
    }
    
    private synchronized void insertDocument(final String uri, final CachedDocument doc) {
        if (this._count < this._size) {
            this._URIs[this._count++] = uri;
            this._current = 0;
        }
        else {
            this._references.remove(this._URIs[this._current]);
            this._URIs[this._current] = uri;
            if (++this._current >= this._size) {
                this._current = 0;
            }
        }
        this._references.put(uri, doc);
    }
    
    private synchronized void replaceDocument(final String uri, final CachedDocument doc) {
        if (doc == null) {
            this.insertDocument(uri, doc);
        }
        else {
            this._references.put(uri, doc);
        }
    }
    
    @Override
    public DOM retrieveDocument(final String baseURI, final String href, final Translet trs) {
        String uri = href;
        if (baseURI != null && !baseURI.equals("")) {
            try {
                uri = SystemIDResolver.getAbsoluteURI(uri, baseURI);
            }
            catch (final TransformerException ex) {}
        }
        CachedDocument doc;
        if ((doc = this.lookupDocument(uri)) == null) {
            doc = new CachedDocument(uri);
            if (doc == null) {
                return null;
            }
            doc.setLastModified(this.getLastModified(uri));
            this.insertDocument(uri, doc);
        }
        else {
            final long now = System.currentTimeMillis();
            final long chk = doc.getLastChecked();
            doc.setLastChecked(now);
            if (now > chk + 1000L) {
                doc.setLastChecked(now);
                final long last = this.getLastModified(uri);
                if (last > doc.getLastModified()) {
                    doc = new CachedDocument(uri);
                    if (doc == null) {
                        return null;
                    }
                    doc.setLastModified(this.getLastModified(uri));
                    this.replaceDocument(uri, doc);
                }
            }
        }
        final DOM dom = doc.getDocument();
        if (dom == null) {
            return null;
        }
        doc.incAccessCount();
        final AbstractTranslet translet = (AbstractTranslet)trs;
        translet.prepassDocument(dom);
        return doc.getDocument();
    }
    
    public void getStatistics(final PrintWriter out) {
        out.println("<h2>DOM cache statistics</h2><center><table border=\"2\"><tr><td><b>Document URI</b></td><td><center><b>Build time</b></center></td><td><center><b>Access count</b></center></td><td><center><b>Last accessed</b></center></td><td><center><b>Last modified</b></center></td></tr>");
        for (int i = 0; i < this._count; ++i) {
            final CachedDocument doc = this._references.get(this._URIs[i]);
            out.print("<tr><td><a href=\"" + this._URIs[i] + "\"><font size=-1>" + this._URIs[i] + "</font></a></td>");
            out.print("<td><center>" + doc.getLatency() + "ms</center></td>");
            out.print("<td><center>" + doc.getAccessCount() + "</center></td>");
            out.print("<td><center>" + new Date(doc.getLastReferenced()) + "</center></td>");
            out.print("<td><center>" + new Date(doc.getLastModified()) + "</center></td>");
            out.println("</tr>");
        }
        out.println("</table></center>");
    }
    
    public final class CachedDocument
    {
        private long _firstReferenced;
        private long _lastReferenced;
        private long _accessCount;
        private long _lastModified;
        private long _lastChecked;
        private long _buildTime;
        private DOMEnhancedForDTM _dom;
        
        public CachedDocument(final String uri) {
            this._dom = null;
            final long stamp = System.currentTimeMillis();
            this._firstReferenced = stamp;
            this._lastReferenced = stamp;
            this._accessCount = 0L;
            this.loadDocument(uri);
            this._buildTime = System.currentTimeMillis() - stamp;
        }
        
        public void loadDocument(final String uri) {
            try {
                final long stamp = System.currentTimeMillis();
                (this._dom = (DOMEnhancedForDTM)DocumentCache.this._dtmManager.getDTM(new SAXSource(DocumentCache.this._reader, new InputSource(uri)), false, null, true, false)).setDocumentURI(uri);
                final long thisTime = System.currentTimeMillis() - stamp;
                if (this._buildTime > 0L) {
                    this._buildTime = this._buildTime + thisTime >>> 1;
                }
                else {
                    this._buildTime = thisTime;
                }
            }
            catch (final Exception e) {
                this._dom = null;
            }
        }
        
        public DOM getDocument() {
            return this._dom;
        }
        
        public long getFirstReferenced() {
            return this._firstReferenced;
        }
        
        public long getLastReferenced() {
            return this._lastReferenced;
        }
        
        public long getAccessCount() {
            return this._accessCount;
        }
        
        public void incAccessCount() {
            ++this._accessCount;
        }
        
        public long getLastModified() {
            return this._lastModified;
        }
        
        public void setLastModified(final long t) {
            this._lastModified = t;
        }
        
        public long getLatency() {
            return this._buildTime;
        }
        
        public long getLastChecked() {
            return this._lastChecked;
        }
        
        public void setLastChecked(final long t) {
            this._lastChecked = t;
        }
        
        public long getEstimatedSize() {
            if (this._dom != null) {
                return this._dom.getSize() << 5;
            }
            return 0L;
        }
    }
}
