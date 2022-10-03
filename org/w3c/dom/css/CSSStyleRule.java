package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSStyleRule extends CSSRule
{
    String getSelectorText();
    
    void setSelectorText(final String p0) throws DOMException;
    
    CSSStyleDeclaration getStyle();
}
