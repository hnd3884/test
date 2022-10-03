package org.dom4j.util;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.DocumentFactory;

public class UserDataDocumentFactory extends DocumentFactory
{
    protected static transient UserDataDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return UserDataDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        return new UserDataElement(qname);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return new UserDataAttribute(qname, value);
    }
    
    static {
        UserDataDocumentFactory.singleton = new UserDataDocumentFactory();
    }
}
