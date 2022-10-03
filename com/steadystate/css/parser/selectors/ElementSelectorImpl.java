package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.ElementSelector;
import com.steadystate.css.parser.LocatableImpl;

public class ElementSelectorImpl extends LocatableImpl implements ElementSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 7507121069969409061L;
    private String localName_;
    
    public void setLocalName(final String localName) {
        this.localName_ = localName;
    }
    
    public ElementSelectorImpl(final String localName) {
        this.localName_ = localName;
    }
    
    public short getSelectorType() {
        return 4;
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getLocalName() {
        return this.localName_;
    }
    
    public String getCssText(final CSSFormat format) {
        final String localeName = this.getLocalName();
        if (localeName == null) {
            return "*";
        }
        return localeName;
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
