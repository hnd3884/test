package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import org.w3c.dom.css.CSSRule;
import java.util.List;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.CSSRuleList;

public class CSSRuleListImpl implements CSSRuleList, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -1269068897476453290L;
    private List<CSSRule> rules_;
    
    public List<CSSRule> getRules() {
        if (this.rules_ == null) {
            this.rules_ = new ArrayList<CSSRule>();
        }
        return this.rules_;
    }
    
    public void setRules(final List<CSSRule> rules) {
        this.rules_ = rules;
    }
    
    public int getLength() {
        return this.getRules().size();
    }
    
    public CSSRule item(final int index) {
        if (index < 0 || null == this.rules_ || index >= this.rules_.size()) {
            return null;
        }
        return this.rules_.get(index);
    }
    
    public void add(final CSSRule rule) {
        this.getRules().add(rule);
    }
    
    public void insert(final CSSRule rule, final int index) {
        this.getRules().add(index, rule);
    }
    
    public void delete(final int index) {
        this.getRules().remove(index);
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getLength(); ++i) {
            if (i > 0) {
                sb.append("\r\n");
            }
            final CSSRule rule = this.item(i);
            sb.append(((CSSFormatable)rule).getCssText(format));
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
        if (!(obj instanceof CSSRuleList)) {
            return false;
        }
        final CSSRuleList crl = (CSSRuleList)obj;
        return this.equalsRules(crl);
    }
    
    private boolean equalsRules(final CSSRuleList crl) {
        if (crl == null || this.getLength() != crl.getLength()) {
            return false;
        }
        for (int i = 0; i < this.getLength(); ++i) {
            final CSSRule cssRule1 = this.item(i);
            final CSSRule cssRule2 = crl.item(i);
            if (!LangUtils.equals(cssRule1, cssRule2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.rules_);
        return hash;
    }
}
