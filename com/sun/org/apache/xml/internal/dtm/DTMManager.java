package com.sun.org.apache.xml.internal.dtm;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import org.w3c.dom.Node;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;

public abstract class DTMManager
{
    protected XMLStringFactory m_xsf;
    private boolean _overrideDefaultParser;
    public boolean m_incremental;
    public boolean m_source_location;
    public static final int IDENT_DTM_NODE_BITS = 16;
    public static final int IDENT_NODE_DEFAULT = 65535;
    public static final int IDENT_DTM_DEFAULT = -65536;
    public static final int IDENT_MAX_DTMS = 65536;
    
    protected DTMManager() {
        this.m_xsf = null;
        this.m_incremental = false;
        this.m_source_location = false;
    }
    
    public XMLStringFactory getXMLStringFactory() {
        return this.m_xsf;
    }
    
    public void setXMLStringFactory(final XMLStringFactory xsf) {
        this.m_xsf = xsf;
    }
    
    public static DTMManager newInstance(final XMLStringFactory xsf) throws DTMException {
        final DTMManager factoryImpl = new DTMManagerDefault();
        factoryImpl.setXMLStringFactory(xsf);
        return factoryImpl;
    }
    
    public abstract DTM getDTM(final Source p0, final boolean p1, final DTMWSFilter p2, final boolean p3, final boolean p4);
    
    public abstract DTM getDTM(final int p0);
    
    public abstract int getDTMHandleFromNode(final Node p0);
    
    public abstract DTM createDocumentFragment();
    
    public abstract boolean release(final DTM p0, final boolean p1);
    
    public abstract DTMIterator createDTMIterator(final Object p0, final int p1);
    
    public abstract DTMIterator createDTMIterator(final String p0, final PrefixResolver p1);
    
    public abstract DTMIterator createDTMIterator(final int p0, final DTMFilter p1, final boolean p2);
    
    public abstract DTMIterator createDTMIterator(final int p0);
    
    public boolean getIncremental() {
        return this.m_incremental;
    }
    
    public void setIncremental(final boolean incremental) {
        this.m_incremental = incremental;
    }
    
    public boolean getSource_location() {
        return this.m_source_location;
    }
    
    public void setSource_location(final boolean sourceLocation) {
        this.m_source_location = sourceLocation;
    }
    
    public boolean overrideDefaultParser() {
        return this._overrideDefaultParser;
    }
    
    public void setOverrideDefaultParser(final boolean flag) {
        this._overrideDefaultParser = flag;
    }
    
    public abstract int getDTMIdentity(final DTM p0);
    
    public int getDTMIdentityMask() {
        return -65536;
    }
    
    public int getNodeIdentityMask() {
        return 65535;
    }
}
