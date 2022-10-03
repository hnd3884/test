package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.output.TXWSerializer;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

public abstract class TXW
{
    private TXW() {
    }
    
    static QName getTagName(final Class<?> c) {
        String localName = "";
        String nsUri = "##default";
        final XmlElement xe = c.getAnnotation(XmlElement.class);
        if (xe != null) {
            localName = xe.value();
            nsUri = xe.ns();
        }
        if (localName.length() == 0) {
            localName = c.getName();
            final int idx = localName.lastIndexOf(46);
            if (idx >= 0) {
                localName = localName.substring(idx + 1);
            }
            localName = Character.toLowerCase(localName.charAt(0)) + localName.substring(1);
        }
        if (nsUri.equals("##default")) {
            final Package pkg = c.getPackage();
            if (pkg != null) {
                final XmlNamespace xn = pkg.getAnnotation(XmlNamespace.class);
                if (xn != null) {
                    nsUri = xn.value();
                }
            }
        }
        if (nsUri.equals("##default")) {
            nsUri = "";
        }
        return new QName(nsUri, localName);
    }
    
    public static <T extends TypedXmlWriter> T create(final Class<T> rootElement, final XmlSerializer out) {
        if (out instanceof TXWSerializer) {
            final TXWSerializer txws = (TXWSerializer)out;
            return txws.txw._element(rootElement);
        }
        final Document doc = new Document(out);
        final QName n = getTagName(rootElement);
        return new ContainerElement(doc, null, n.getNamespaceURI(), n.getLocalPart())._cast(rootElement);
    }
    
    public static <T extends TypedXmlWriter> T create(final QName tagName, final Class<T> rootElement, final XmlSerializer out) {
        if (out instanceof TXWSerializer) {
            final TXWSerializer txws = (TXWSerializer)out;
            return txws.txw._element(tagName, rootElement);
        }
        return new ContainerElement(new Document(out), null, tagName.getNamespaceURI(), tagName.getLocalPart())._cast(rootElement);
    }
}
