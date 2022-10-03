package org.apache.xerces.dom;

import org.w3c.dom.DOMException;
import org.apache.xerces.dom3.as.DOMASWriter;
import org.apache.xerces.parsers.DOMASBuilderImpl;
import org.apache.xerces.dom3.as.DOMASBuilder;
import org.apache.xerces.dom3.as.ASModel;
import org.w3c.dom.DOMImplementation;
import org.apache.xerces.dom3.as.DOMImplementationAS;

public class ASDOMImplementationImpl extends DOMImplementationImpl implements DOMImplementationAS
{
    static final ASDOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return ASDOMImplementationImpl.singleton;
    }
    
    public ASModel createAS(final boolean b) {
        return new ASModelImpl(b);
    }
    
    public DOMASBuilder createDOMASBuilder() {
        return new DOMASBuilderImpl();
    }
    
    public DOMASWriter createDOMASWriter() {
        throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
    }
    
    static {
        singleton = new ASDOMImplementationImpl();
    }
}
