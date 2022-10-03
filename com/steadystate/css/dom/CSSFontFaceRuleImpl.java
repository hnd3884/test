package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import org.w3c.dom.css.CSSStyleDeclaration;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSFontFaceRule;

public class CSSFontFaceRuleImpl extends AbstractCSSRuleImpl implements CSSFontFaceRule
{
    private static final long serialVersionUID = -3604191834588759088L;
    private CSSStyleDeclarationImpl style_;
    
    public CSSFontFaceRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule) {
        super(parentStyleSheet, parentRule);
    }
    
    public CSSFontFaceRuleImpl() {
    }
    
    public short getType() {
        return 5;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        sb.append("@font-face {");
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
            if (r.getType() != 5) {
                throw new DOMExceptionImpl((short)13, 8);
            }
            this.style_ = ((CSSFontFaceRuleImpl)r).style_;
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
    
    public void setStyle(final CSSStyleDeclarationImpl style) {
        this.style_ = style;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSFontFaceRule)) {
            return false;
        }
        final CSSFontFaceRule cffr = (CSSFontFaceRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getStyle(), cffr.getStyle());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.style_);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
