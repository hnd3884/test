package org.apache.axiom.util.stax.xop;

import javax.xml.namespace.QName;

interface XOPConstants
{
    public static final String INCLUDE = "Include";
    public static final String NAMESPACE_URI = "http://www.w3.org/2004/08/xop/include";
    public static final String DEFAULT_PREFIX = "xop";
    public static final QName INCLUDE_QNAME = new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop");
    public static final String HREF = "href";
}
