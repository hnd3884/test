package org.apache.xml.security.c14n.helper;

import org.w3c.dom.Attr;
import java.io.Serializable;
import java.util.Comparator;

public class AttrCompare implements Comparator, Serializable
{
    private static final long serialVersionUID = -7113259629930576230L;
    private static final int ATTR0_BEFORE_ATTR1 = -1;
    private static final int ATTR1_BEFORE_ATTR0 = 1;
    private static final String XMLNS = "http://www.w3.org/2000/xmlns/";
    
    public int compare(final Object o, final Object o2) {
        final Attr attr = (Attr)o;
        final Attr attr2 = (Attr)o2;
        final String namespaceURI = attr.getNamespaceURI();
        final String namespaceURI2 = attr2.getNamespaceURI();
        final boolean b = "http://www.w3.org/2000/xmlns/" == namespaceURI;
        final boolean b2 = "http://www.w3.org/2000/xmlns/" == namespaceURI2;
        if (b) {
            if (b2) {
                String localName = attr.getLocalName();
                String localName2 = attr2.getLocalName();
                if (localName.equals("xmlns")) {
                    localName = "";
                }
                if (localName2.equals("xmlns")) {
                    localName2 = "";
                }
                return localName.compareTo(localName2);
            }
            return -1;
        }
        else {
            if (b2) {
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
