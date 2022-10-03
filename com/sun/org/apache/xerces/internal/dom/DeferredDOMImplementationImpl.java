package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMImplementation;

public class DeferredDOMImplementationImpl extends DOMImplementationImpl
{
    static DeferredDOMImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return DeferredDOMImplementationImpl.singleton;
    }
    
    static {
        DeferredDOMImplementationImpl.singleton = new DeferredDOMImplementationImpl();
    }
}
