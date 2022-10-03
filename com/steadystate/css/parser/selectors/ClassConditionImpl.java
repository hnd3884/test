package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.AttributeCondition;
import com.steadystate.css.parser.LocatableImpl;

public class ClassConditionImpl extends LocatableImpl implements AttributeCondition, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -2216489300949054242L;
    private String value_;
    
    public void setValue(final String value) {
        this.value_ = value;
    }
    
    public ClassConditionImpl(final String value) {
        this.setValue(value);
    }
    
    public short getConditionType() {
        return 9;
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
    
    public String getCssText(final CSSFormat format) {
        final String value = this.getValue();
        if (value != null) {
            return "." + value;
        }
        return ".";
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
