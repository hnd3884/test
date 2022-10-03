package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.transform.SourceLocator;

public interface XPathFactory
{
    XPath create(final String p0, final SourceLocator p1, final PrefixResolver p2, final int p3);
}
