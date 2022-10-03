package org.w3c.dom.svg;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSStyleDeclaration;

public interface SVGStylable
{
    SVGAnimatedString getClassName();
    
    CSSStyleDeclaration getStyle();
    
    CSSValue getPresentationAttribute(final String p0);
}
