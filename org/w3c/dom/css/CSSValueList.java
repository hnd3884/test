package org.w3c.dom.css;

public interface CSSValueList extends CSSValue
{
    int getLength();
    
    CSSValue item(final int p0);
}
