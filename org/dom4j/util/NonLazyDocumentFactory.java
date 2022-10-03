package org.dom4j.util;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.DocumentFactory;

public class NonLazyDocumentFactory extends DocumentFactory
{
    protected static transient NonLazyDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return NonLazyDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        return new NonLazyElement(qname);
    }
    
    static {
        NonLazyDocumentFactory.singleton = new NonLazyDocumentFactory();
    }
}
