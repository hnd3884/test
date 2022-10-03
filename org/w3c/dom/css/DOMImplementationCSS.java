package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

public interface DOMImplementationCSS extends DOMImplementation
{
    CSSStyleSheet createCSSStyleSheet(final String p0, final String p1) throws DOMException;
}
