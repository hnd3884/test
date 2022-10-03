package org.apache.xerces.dom;

import java.util.ArrayList;
import org.w3c.dom.DOMImplementationList;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.w3c.dom.DOMImplementation;

public class DOMXSImplementationSourceImpl extends DOMImplementationSourceImpl
{
    public DOMImplementation getDOMImplementation(final String s) {
        final DOMImplementation domImplementation = super.getDOMImplementation(s);
        if (domImplementation != null) {
            return domImplementation;
        }
        final DOMImplementation domImplementation2 = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation2, s)) {
            return domImplementation2;
        }
        final DOMImplementation domImplementation3 = XSImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation3, s)) {
            return domImplementation3;
        }
        return null;
    }
    
    public DOMImplementationList getDOMImplementationList(final String s) {
        final ArrayList list = new ArrayList();
        final DOMImplementationList domImplementationList = super.getDOMImplementationList(s);
        for (int i = 0; i < domImplementationList.getLength(); ++i) {
            list.add(domImplementationList.item(i));
        }
        final DOMImplementation domImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation, s)) {
            list.add(domImplementation);
        }
        final DOMImplementation domImplementation2 = XSImplementationImpl.getDOMImplementation();
        if (this.testImpl(domImplementation2, s)) {
            list.add(domImplementation2);
        }
        return new DOMImplementationListImpl(list);
    }
}
