package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.StyleSheet;

public interface CSSStyleSheet extends StyleSheet
{
    CSSRule getOwnerRule();
    
    CSSRuleList getCssRules();
    
    int insertRule(final String p0, final int p1) throws DOMException;
    
    void deleteRule(final int p0) throws DOMException;
}
