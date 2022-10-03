package org.dom4j.util;

import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.DocumentFactory;

public class IndexedDocumentFactory extends DocumentFactory
{
    protected static transient IndexedDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return IndexedDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        return new IndexedElement(qname);
    }
    
    public Element createElement(final QName qname, final int attributeCount) {
        return new IndexedElement(qname, attributeCount);
    }
    
    static {
        IndexedDocumentFactory.singleton = new IndexedDocumentFactory();
    }
}
