package com.steadystate.css.dom;

import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSRule;
import com.steadystate.css.format.CSSFormatable;

public abstract class AbstractCSSRuleImpl extends CSSOMObjectImpl implements CSSFormatable
{
    private static final long serialVersionUID = 7829784704712797815L;
    private CSSStyleSheetImpl parentStyleSheet_;
    private CSSRule parentRule_;
    
    protected CSSStyleSheetImpl getParentStyleSheetImpl() {
        return this.parentStyleSheet_;
    }
    
    public void setParentStyleSheet(final CSSStyleSheetImpl parentStyleSheet) {
        this.parentStyleSheet_ = parentStyleSheet;
    }
    
    public void setParentRule(final CSSRule parentRule) {
        this.parentRule_ = parentRule;
    }
    
    public AbstractCSSRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule) {
        this.parentStyleSheet_ = parentStyleSheet;
        this.parentRule_ = parentRule;
    }
    
    public AbstractCSSRuleImpl() {
    }
    
    public CSSStyleSheet getParentStyleSheet() {
        return this.parentStyleSheet_;
    }
    
    public CSSRule getParentRule() {
        return this.parentRule_;
    }
    
    public abstract String getCssText(final CSSFormat p0);
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof CSSRule && super.equals(obj));
    }
    
    @Override
    public int hashCode() {
        final int hash = super.hashCode();
        return hash;
    }
}
