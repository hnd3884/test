package com.sun.org.apache.xml.internal.security.c14n.helper;

import java.io.Serializable;
import org.w3c.dom.Attr;
import java.util.Comparator;

public class AttrCompare implements Comparator<Attr>, Serializable
{
    private static final long serialVersionUID = -7113259629930576230L;
    private static final int ATTR0_BEFORE_ATTR1 = -1;
    private static final int ATTR1_BEFORE_ATTR0 = 1;
    private static final String XMLNS = "http://www.w3.org/2000/xmlns/";
    
    @Override
    public int compare(final Attr attr, final Attr attr2) {
        final String namespaceURI = attr.getNamespaceURI();
        final String namespaceURI2 = attr2.getNamespaceURI();
        final boolean equals = "http://www.w3.org/2000/xmlns/".equals(namespaceURI);
        final boolean equals2 = "http://www.w3.org/2000/xmlns/".equals(namespaceURI2);
        if (equals) {
            if (equals2) {
                String localName = attr.getLocalName();
                String localName2 = attr2.getLocalName();
                if ("xmlns".equals(localName)) {
                    localName = "";
                }
                if ("xmlns".equals(localName2)) {
                    localName2 = "";
                }
                return localName.compareTo(localName2);
            }
            return -1;
        }
        else {
            if (equals2) {
                return 1;
            }
            if (namespaceURI == null) {
                if (namespaceURI2 == null) {
                    return attr.getName().compareTo(attr2.getName());
                }
                return -1;
            }
            else {
                if (namespaceURI2 == null) {
                    return 1;
                }
                final int compareTo = namespaceURI.compareTo(namespaceURI2);
                if (compareTo != 0) {
                    return compareTo;
                }
                return attr.getLocalName().compareTo(attr2.getLocalName());
            }
        }
    }
}
