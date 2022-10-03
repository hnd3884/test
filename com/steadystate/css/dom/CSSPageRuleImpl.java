package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSPageRule;

public class CSSPageRuleImpl extends AbstractCSSRuleImpl implements CSSPageRule
{
    private static final long serialVersionUID = -6007519872104320812L;
    private String pseudoPage_;
    private CSSStyleDeclaration style_;
    
    public CSSPageRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final String pseudoPage) {
        super(parentStyleSheet, parentRule);
        this.pseudoPage_ = pseudoPage;
    }
    
    public CSSPageRuleImpl() {
    }
    
    public short getType() {
        return 6;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        final String sel = this.getSelectorText();
        sb.append("@page ").append(sel);
        if (sel.length() > 0) {
            sb.append(" ");
        }
        sb.append("{");
        final CSSStyleDeclaration style = this.getStyle();
        if (null != style) {
            sb.append(style.getCssText());
        }
        sb.append("}");
        return sb.toString();
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
            if (r.getType() != 6) {
                throw new DOMExceptionImpl((short)13, 9);
            }
            this.pseudoPage_ = ((CSSPageRuleImpl)r).pseudoPage_;
            this.style_ = ((CSSPageRuleImpl)r).style_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public String getSelectorText() {
        if (null == this.pseudoPage_) {
            return "";
        }
        return this.pseudoPage_;
    }
    
    public void setSelectorText(final String selectorText) throws DOMException {
    }
    
    public CSSStyleDeclaration getStyle() {
        return this.style_;
    }
    
    public void setPseudoPage(final String pseudoPage) {
        this.pseudoPage_ = pseudoPage;
    }
    
    public void setStyle(final CSSStyleDeclarationImpl style) {
        this.style_ = style;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSPageRule)) {
            return false;
        }
        final CSSPageRule cpr = (CSSPageRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getSelectorText(), cpr.getSelectorText()) && LangUtils.equals(this.getStyle(), cpr.getStyle());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.pseudoPage_);
        hash = LangUtils.hashCode(hash, this.style_);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
