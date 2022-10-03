package com.steadystate.css.dom;

import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.Counter;

public class CounterImpl implements Counter, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 7996279151817598904L;
    private String identifier_;
    private String listStyle_;
    private String separator_;
    
    public void setIdentifier(final String identifier) {
        this.identifier_ = identifier;
    }
    
    public void setListStyle(final String listStyle) {
        this.listStyle_ = listStyle;
    }
    
    public void setSeparator(final String separator) {
        this.separator_ = separator;
    }
    
    public CounterImpl(final boolean separatorSpecified, final LexicalUnit lu) throws DOMException {
        LexicalUnit next = lu;
        this.identifier_ = next.getStringValue();
        next = next.getNextLexicalUnit();
        if (next != null) {
            if (next.getLexicalUnitType() != 0) {
                throw new DOMException((short)12, "Counter parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (separatorSpecified && next != null) {
                this.separator_ = next.getStringValue();
                next = next.getNextLexicalUnit();
                if (next != null) {
                    if (next.getLexicalUnitType() != 0) {
                        throw new DOMException((short)12, "Counter parameters must be separated by ','.");
                    }
                    next = next.getNextLexicalUnit();
                }
            }
            if (next != null) {
                this.listStyle_ = next.getStringValue();
                next = next.getNextLexicalUnit();
                if (next != null) {
                    throw new DOMException((short)12, "Too many parameters for counter function.");
                }
            }
        }
    }
    
    public CounterImpl() {
    }
    
    public String getIdentifier() {
        return this.identifier_;
    }
    
    public String getListStyle() {
        return this.listStyle_;
    }
    
    public String getSeparator() {
        return this.separator_;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (this.separator_ == null) {
            sb.append("counter(");
        }
        else {
            sb.append("counters(");
        }
        sb.append(this.identifier_);
        if (this.separator_ != null) {
            sb.append(", \"").append(this.separator_).append("\"");
        }
        if (this.listStyle_ != null) {
            sb.append(", ").append(this.listStyle_);
        }
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
