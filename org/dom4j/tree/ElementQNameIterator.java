package org.dom4j.tree;

import org.dom4j.Element;
import java.util.Iterator;
import org.dom4j.QName;

public class ElementQNameIterator extends FilterIterator
{
    private QName qName;
    
    public ElementQNameIterator(final Iterator proxy, final QName qName) {
        super(proxy);
        this.qName = qName;
    }
    
    protected boolean matches(final Object object) {
        if (object instanceof Element) {
            final Element element = (Element)object;
            return this.qName.equals(element.getQName());
        }
        return false;
    }
}
