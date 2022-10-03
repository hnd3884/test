package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import javax.xml.transform.Source;

public final class XSLTCSource implements Source
{
    private String _systemId;
    private Source _source;
    private ThreadLocal _dom;
    
    public XSLTCSource(final String systemId) {
        this._systemId = null;
        this._source = null;
        this._dom = new ThreadLocal();
        this._systemId = systemId;
    }
    
    public XSLTCSource(final Source source) {
        this._systemId = null;
        this._source = null;
        this._dom = new ThreadLocal();
        this._source = source;
    }
    
    @Override
    public void setSystemId(final String systemId) {
        this._systemId = systemId;
        if (this._source != null) {
            this._source.setSystemId(systemId);
        }
    }
    
    @Override
    public String getSystemId() {
        if (this._source != null) {
            return this._source.getSystemId();
        }
        return this._systemId;
    }
    
    protected DOM getDOM(XSLTCDTMManager dtmManager, final AbstractTranslet translet) throws SAXException {
        SAXImpl idom = this._dom.get();
        if (idom != null) {
            if (dtmManager != null) {
                idom.migrateTo(dtmManager);
            }
        }
        else {
            Source source = this._source;
            if (source == null) {
                if (this._systemId == null || this._systemId.length() <= 0) {
                    final ErrorMsg err = new ErrorMsg("XSLTC_SOURCE_ERR");
                    throw new SAXException(err.toString());
                }
                source = new StreamSource(this._systemId);
            }
            DOMWSFilter wsfilter = null;
            if (translet != null && translet instanceof StripFilter) {
                wsfilter = new DOMWSFilter(translet);
            }
            final boolean hasIdCall = translet != null && translet.hasIdCall();
            if (dtmManager == null) {
                dtmManager = XSLTCDTMManager.newInstance();
            }
            idom = (SAXImpl)dtmManager.getDTM(source, true, wsfilter, false, false, hasIdCall);
            final String systemId = this.getSystemId();
            if (systemId != null) {
                idom.setDocumentURI(systemId);
            }
            this._dom.set(idom);
        }
        return idom;
    }
}
