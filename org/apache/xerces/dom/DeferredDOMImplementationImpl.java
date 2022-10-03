package org.apache.xerces.dom;

import org.w3c.dom.DOMImplementation;

public class DeferredDOMImplementationImpl extends DOMImplementationImpl
{
    static final DeferredDOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return DeferredDOMImplementationImpl.singleton;
    }
    
    static {
        singleton = new DeferredDOMImplementationImpl();
    }
}
