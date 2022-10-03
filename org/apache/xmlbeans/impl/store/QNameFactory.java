package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;

public interface QNameFactory
{
    QName getQName(final String p0, final String p1);
    
    QName getQName(final String p0, final String p1, final String p2);
    
    QName getQName(final char[] p0, final int p1, final int p2, final char[] p3, final int p4, final int p5);
    
    QName getQName(final char[] p0, final int p1, final int p2, final char[] p3, final int p4, final int p5, final char[] p6, final int p7, final int p8);
}
