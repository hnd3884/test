package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.SAXException;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import org.xml.sax.XMLReader;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import java.util.HashMap;

public class XMLReaderManager
{
    private static final XMLReaderManager m_singletonManager;
    private static final String property = "org.xml.sax.driver";
    private ThreadLocal<ReaderWrapper> m_readers;
    private boolean m_overrideDefaultParser;
    private HashMap m_inUse;
    private boolean _secureProcessing;
    private String _accessExternalDTD;
    private XMLSecurityManager _xmlSecurityManager;
    
    private XMLReaderManager() {
        this._accessExternalDTD = "all";
    }
    
    public static XMLReaderManager getInstance(final boolean overrideDefaultParser) {
        XMLReaderManager.m_singletonManager.setOverrideDefaultParser(overrideDefaultParser);
        return XMLReaderManager.m_singletonManager;
    }
    
    public synchronized XMLReader getXMLReader() throws SAXException {
        if (this.m_readers == null) {
            this.m_readers = new ThreadLocal<ReaderWrapper>();
        }
        if (this.m_inUse == null) {
            this.m_inUse = new HashMap();
        }
        final ReaderWrapper rw = this.m_readers.get();
        final boolean threadHasReader = rw != null;
        XMLReader reader = threadHasReader ? rw.reader : null;
        final String factory = SecuritySupport.getSystemProperty("org.xml.sax.driver");
        if (threadHasReader && this.m_inUse.get(reader) != Boolean.TRUE && rw.overrideDefaultParser == this.m_overrideDefaultParser && (factory == null || reader.getClass().getName().equals(factory))) {
            this.m_inUse.put(reader, Boolean.TRUE);
        }
        else {
            reader = JdkXmlUtils.getXMLReader(this.m_overrideDefaultParser, this._secureProcessing);
            if (!threadHasReader) {
                this.m_readers.set(new ReaderWrapper(reader, this.m_overrideDefaultParser));
                this.m_inUse.put(reader, Boolean.TRUE);
            }
        }
        try {
            reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
        }
        catch (final SAXException se) {
            XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", se);
        }
        String lastProperty = "";
        try {
            if (this._xmlSecurityManager != null) {
                for (final XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                    lastProperty = limit.apiProperty();
                    reader.setProperty(lastProperty, this._xmlSecurityManager.getLimitValueAsString(limit));
                }
                if (this._xmlSecurityManager.printEntityCountInfo()) {
                    lastProperty = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
                    reader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
                }
            }
        }
        catch (final SAXException se2) {
            XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, se2);
        }
        return reader;
    }
    
    public synchronized void releaseXMLReader(final XMLReader reader) {
        final ReaderWrapper rw = this.m_readers.get();
        if (rw.reader == reader && reader != null) {
            this.m_inUse.remove(reader);
        }
    }
    
    public boolean overrideDefaultParser() {
        return this.m_overrideDefaultParser;
    }
    
    public void setOverrideDefaultParser(final boolean flag) {
        this.m_overrideDefaultParser = flag;
    }
    
    public void setFeature(final String name, final boolean value) {
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this._secureProcessing = value;
        }
    }
    
    public Object getProperty(final String name) {
        if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
            return this._accessExternalDTD;
        }
        if (name.equals("http://apache.org/xml/properties/security-manager")) {
            return this._xmlSecurityManager;
        }
        return null;
    }
    
    public void setProperty(final String name, final Object value) {
        if (name.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
            this._accessExternalDTD = (String)value;
        }
        else if (name.equals("http://apache.org/xml/properties/security-manager")) {
            this._xmlSecurityManager = (XMLSecurityManager)value;
        }
    }
    
    static {
        m_singletonManager = new XMLReaderManager();
    }
    
    class ReaderWrapper
    {
        XMLReader reader;
        boolean overrideDefaultParser;
        
        public ReaderWrapper(final XMLReader reader, final boolean overrideDefaultParser) {
            this.reader = reader;
            this.overrideDefaultParser = overrideDefaultParser;
        }
    }
}
