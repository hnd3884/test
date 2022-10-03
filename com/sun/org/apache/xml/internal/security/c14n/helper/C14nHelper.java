package com.sun.org.apache.xml.internal.security.c14n.helper;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import org.w3c.dom.Attr;

public final class C14nHelper
{
    private C14nHelper() {
    }
    
    public static boolean namespaceIsRelative(final Attr attr) {
        return !namespaceIsAbsolute(attr);
    }
    
    public static boolean namespaceIsRelative(final String s) {
        return !namespaceIsAbsolute(s);
    }
    
    public static boolean namespaceIsAbsolute(final Attr attr) {
        return namespaceIsAbsolute(attr.getValue());
    }
    
    public static boolean namespaceIsAbsolute(final String s) {
        return s.length() == 0 || s.indexOf(58) > 0;
    }
    
    public static void assertNotRelativeNS(final Attr attr) throws CanonicalizationException {
        if (attr == null) {
            return;
        }
        final String nodeName = attr.getNodeName();
        final boolean equals = "xmlns".equals(nodeName);
        final boolean startsWith = nodeName.startsWith("xmlns:");
        if ((equals || startsWith) && namespaceIsRelative(attr)) {
            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace", new Object[] { attr.getOwnerElement().getTagName(), nodeName, attr.getValue() });
        }
    }
    
    public static void checkTraversability(final Document document) throws CanonicalizationException {
        if (!document.isSupported("Traversal", "2.0")) {
            throw new CanonicalizationException("c14n.Canonicalizer.TraversalNotSupported", new Object[] { document.getImplementation().getClass().getName() });
        }
    }
    
    public static void checkForRelativeNamespace(final Element element) throws CanonicalizationException {
        if (element != null) {
            final NamedNodeMap attributes = element.getAttributes();
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                assertNotRelativeNS((Attr)attributes.item(i));
            }
            return;
        }
        throw new CanonicalizationException("Called checkForRelativeNamespace() on null");
    }
}
