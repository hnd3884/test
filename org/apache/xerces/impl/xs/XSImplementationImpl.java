package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.LSInputListImpl;
import org.apache.xerces.xs.LSInputList;
import org.w3c.dom.ls.LSInput;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.XSException;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.StringList;
import org.w3c.dom.DOMImplementation;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.dom.PSVIDOMImplementationImpl;

public class XSImplementationImpl extends PSVIDOMImplementationImpl implements XSImplementation
{
    static final XSImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return XSImplementationImpl.singleton;
    }
    
    public boolean hasFeature(final String s, final String s2) {
        return (s.equalsIgnoreCase("XS-Loader") && (s2 == null || s2.equals("1.0"))) || super.hasFeature(s, s2);
    }
    
    public XSLoader createXSLoader(final StringList list) throws XSException {
        final XSLoaderImpl xsLoaderImpl = new XSLoaderImpl();
        if (list == null) {
            return xsLoaderImpl;
        }
        for (int i = 0; i < list.getLength(); ++i) {
            if (!list.item(i).equals("1.0")) {
                throw new XSException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { list.item(i) }));
            }
        }
        return xsLoaderImpl;
    }
    
    public StringList createStringList(final String[] array) {
        final int n = (array != null) ? array.length : 0;
        return (n != 0) ? new StringListImpl(array.clone(), n) : StringListImpl.EMPTY_LIST;
    }
    
    public LSInputList createLSInputList(final LSInput[] array) {
        final int n = (array != null) ? array.length : 0;
        return (n != 0) ? new LSInputListImpl(array.clone(), n) : LSInputListImpl.EMPTY_LIST;
    }
    
    public StringList getRecognizedVersions() {
        return new StringListImpl(new String[] { "1.0" }, 1);
    }
    
    static {
        singleton = new XSImplementationImpl();
    }
}
