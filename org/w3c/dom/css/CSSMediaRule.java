package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;

public interface CSSMediaRule extends CSSRule
{
    MediaList getMedia();
    
    CSSRuleList getCssRules();
    
    int insertRule(final String p0, final int p1) throws DOMException;
    
    void deleteRule(final int p0) throws DOMException;
}
