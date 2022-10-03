package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import com.steadystate.css.format.CSSFormatable;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.css.sac.SelectorList;
import org.w3c.dom.css.CSSStyleRule;

public class CSSStyleRuleImpl extends AbstractCSSRuleImpl implements CSSStyleRule
{
    private static final long serialVersionUID = -697009251364657426L;
    private SelectorList selectors_;
    private CSSStyleDeclaration style_;
    
    public SelectorList getSelectors() {
        return this.selectors_;
    }
    
    public void setSelectors(final SelectorList selectors) {
        this.selectors_ = selectors;
    }
    
    public CSSStyleRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final SelectorList selectors) {
        super(parentStyleSheet, parentRule);
        this.selectors_ = selectors;
    }
    
    public CSSStyleRuleImpl() {
    }
    
    public short getType() {
        return 1;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final CSSStyleDeclaration style = this.getStyle();
        if (null == style) {
            return "";
        }
        final String selectorText = ((CSSFormatable)this.selectors_).getCssText(format);
        final String styleText = ((CSSFormatable)style).getCssText(format);
        if (null == styleText || styleText.length() == 0) {
            return selectorText + " { }";
        }
        if (format != null && format.getPropertiesInSeparateLines()) {
            return selectorText + " {" + styleText + "}";
        }
        return selectorText + " { " + styleText + " }";
    }
    
    public void setCssText(final String cssText) throws DOMException {
        final CSSStyleSheetImpl parentStyleSheet = this.getParentStyleSheetImpl();
        if (parentStyleSheet != null && parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            final InputSource is = new InputSource((Reader)new StringReader(cssText));
            final CSSOMParser parser = new CSSOMParser();
            final CSSRule r = parser.parseRule(is);
            if (r.getType() != 1) {
                throw new DOMExceptionImpl((short)13, 4);
            }
            this.selectors_ = ((CSSStyleRuleImpl)r).selectors_;
            this.style_ = ((CSSStyleRuleImpl)r).style_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public String getSelectorText() {
        return this.selectors_.toString();
    }
    
    public void setSelectorText(final String selectorText) throws DOMException {
        final CSSStyleSheetImpl parentStyleSheet = this.getParentStyleSheetImpl();
        if (parentStyleSheet != null && parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            final InputSource is = new InputSource((Reader)new StringReader(selectorText));
            final CSSOMParser parser = new CSSOMParser();
            this.selectors_ = parser.parseSelectors(is);
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public CSSStyleDeclaration getStyle() {
        return this.style_;
    }
    
    public void setStyle(final CSSStyleDeclaration style) {
        this.style_ = style;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSStyleRule)) {
            return false;
        }
        final CSSStyleRule csr = (CSSStyleRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getSelectorText(), csr.getSelectorText()) && LangUtils.equals(this.getStyle(), csr.getStyle());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.selectors_);
        hash = LangUtils.hashCode(hash, this.style_);
        return hash;
    }
}
