package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.AttributeCondition;
import com.steadystate.css.parser.LocatableImpl;

public class AttributeConditionImpl extends LocatableImpl implements AttributeCondition, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 9035418830958954213L;
    private String localName_;
    private String value_;
    private boolean specified_;
    
    public void setLocaleName(final String localName) {
        this.localName_ = localName;
    }
    
    public void setValue(final String value) {
        this.value_ = value;
    }
    
    public void setSpecified(final boolean specified) {
        this.specified_ = specified;
    }
    
    public AttributeConditionImpl(final String localName, final String value, final boolean specified) {
        this.setLocaleName(localName);
        this.setValue(value);
        this.setSpecified(specified);
    }
    
    public short getConditionType() {
        return 4;
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getLocalName() {
        return this.localName_;
    }
    
    public boolean getSpecified() {
        return this.specified_;
    }
    
    public String getValue() {
        return this.value_;
    }
    
    public String getCssText(final CSSFormat format) {
        final String value = this.getValue();
        if (value != null) {
            return "[" + this.getLocalName() + "=\"" + value + "\"]";
        }
        return "[" + this.getLocalName() + "]";
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
