package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.AttributeCondition;
import com.steadystate.css.parser.LocatableImpl;

public class PseudoClassConditionImpl extends LocatableImpl implements AttributeCondition, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 1798016773089155610L;
    private String value_;
    private boolean doubleColon_;
    
    public void setValue(final String value) {
        this.value_ = value;
    }
    
    public PseudoClassConditionImpl(final String value) {
        this.setValue(value);
    }
    
    public short getConditionType() {
        return 10;
    }
    
    public String getNamespaceURI() {
        return null;
    }
    
    public String getLocalName() {
        return null;
    }
    
    public boolean getSpecified() {
        return true;
    }
    
    public String getValue() {
        return this.value_;
    }
    
    public void prefixedWithDoubleColon() {
        this.doubleColon_ = true;
    }
    
    public String getCssText(final CSSFormat format) {
        final String value = this.getValue();
        if (value == null) {
            return value;
        }
        return (this.doubleColon_ ? "::" : ":") + value;
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
