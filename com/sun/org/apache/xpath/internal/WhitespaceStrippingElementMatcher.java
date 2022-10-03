package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Element;

public interface WhitespaceStrippingElementMatcher
{
    boolean shouldStripWhiteSpace(final XPathContext p0, final Element p1) throws TransformerException;
    
    boolean canStripWhiteSpace();
}
