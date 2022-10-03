package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.w3c.dom.DOMImplementation;
import com.sun.org.apache.xerces.internal.xs.XSImplementation;
import com.sun.org.apache.xerces.internal.dom.CoreDOMImplementationImpl;

public class XSImplementationImpl extends CoreDOMImplementationImpl implements XSImplementation
{
    static XSImplementationImpl singleton;
    
    public static DOMImplementation getDOMImplementation() {
        return XSImplementationImpl.singleton;
    }
    
    @Override
    public boolean hasFeature(final String feature, final String version) {
        return (feature.equalsIgnoreCase("XS-Loader") && (version == null || version.equals("1.0"))) || super.hasFeature(feature, version);
    }
    
    @Override
    public XSLoader createXSLoader(final StringList versions) throws XSException {
        final XSLoader loader = new XSLoaderImpl();
        if (versions == null) {
            return loader;
        }
        for (int i = 0; i < versions.getLength(); ++i) {
            if (!versions.item(i).equals("1.0")) {
                final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { versions.item(i) });
                throw new XSException((short)1, msg);
            }
        }
        return loader;
    }
    
    @Override
    public StringList getRecognizedVersions() {
        final StringListImpl list = new StringListImpl(new String[] { "1.0" }, 1);
        return list;
    }
    
    static {
        XSImplementationImpl.singleton = new XSImplementationImpl();
    }
}
