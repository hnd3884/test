package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSValue;
import com.steadystate.css.format.CSSFormatable;

public class Property extends CSSOMObjectImpl implements CSSFormatable
{
    private static final long serialVersionUID = 8720637891949104989L;
    private String name_;
    private CSSValue value_;
    private boolean important_;
    
    public Property(final String name, final CSSValue value, final boolean important) {
        this.name_ = name;
        this.value_ = value;
        this.important_ = important;
    }
    
    public Property() {
    }
    
    public String getName() {
        return this.name_;
    }
    
    public void setName(final String name) {
        this.name_ = name;
    }
    
    public CSSValue getValue() {
        return this.value_;
    }
    
    public boolean isImportant() {
        return this.important_;
    }
    
    public void setValue(final CSSValue value) {
        this.value_ = value;
    }
    
    public void setImportant(final boolean important) {
        this.important_ = important;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name_);
        if (null != this.value_) {
            sb.append(": ");
            sb.append(((CSSValueImpl)this.value_).getCssText(format));
        }
        if (this.important_) {
            sb.append(" !important");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Property)) {
            return false;
        }
        final Property p = (Property)obj;
        return super.equals(obj) && this.important_ == p.important_ && LangUtils.equals(this.name_, p.name_) && LangUtils.equals(this.value_, p.value_);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.important_);
        hash = LangUtils.hashCode(hash, this.name_);
        hash = LangUtils.hashCode(hash, this.value_);
        return hash;
    }
}
