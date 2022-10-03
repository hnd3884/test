package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMImplementationList;
import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import org.w3c.dom.DOMImplementation;

public class DOMXSImplementationSourceImpl extends DOMImplementationSourceImpl
{
    @Override
    public DOMImplementation getDOMImplementation(final String features) {
        DOMImplementation impl = super.getDOMImplementation(features);
        if (impl != null) {
            return impl;
        }
        impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(impl, features)) {
            return impl;
        }
        impl = XSImplementationImpl.getDOMImplementation();
        if (this.testImpl(impl, features)) {
            return impl;
        }
        return null;
    }
    
    @Override
    public DOMImplementationList getDOMImplementationList(final String features) {
        final Vector implementations = new Vector();
        final DOMImplementationList list = super.getDOMImplementationList(features);
        for (int i = 0; i < list.getLength(); ++i) {
            implementations.addElement(list.item(i));
        }
        DOMImplementation impl = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(impl, features)) {
            implementations.addElement(impl);
        }
        impl = XSImplementationImpl.getDOMImplementation();
        if (this.testImpl(impl, features)) {
            implementations.addElement(impl);
        }
        return new DOMImplementationListImpl(implementations);
    }
}
