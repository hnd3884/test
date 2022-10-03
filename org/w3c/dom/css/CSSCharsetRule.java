package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public interface CSSCharsetRule extends CSSRule
{
    String getEncoding();
    
    void setEncoding(final String p0) throws DOMException;
}
