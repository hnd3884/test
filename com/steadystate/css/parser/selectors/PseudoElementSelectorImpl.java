package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.ElementSelector;
import com.steadystate.css.parser.LocatableImpl;

public class PseudoElementSelectorImpl extends LocatableImpl implements ElementSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 2913936296006875268L;
    private String localName_;
    private boolean doubleColon_;
    
    public void setLocaleName(final String localName) {
        this.localName_ = localName;
    }
    
    public PseudoElementSelectorImpl(final String localName) {
        this.setLocaleName(localName);
    }
    
    public short getSelectorType() {
        return 9;
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getLocalName() {
        return this.localName_;
    }
    
    public void prefixedWithDoubleColon() {
        this.doubleColon_ = true;
    }
    
    public String getCssText(final CSSFormat format) {
        if (this.localName_ == null) {
            return this.localName_;
        }
        return (this.doubleColon_ ? "::" : ":") + this.localName_;
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
