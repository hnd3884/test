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
import org.w3c.dom.css.CSSCharsetRule;

public class CSSCharsetRuleImpl extends AbstractCSSRuleImpl implements CSSCharsetRule
{
    private static final long serialVersionUID = -2472209213089007127L;
    private String encoding_;
    
    public CSSCharsetRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final String encoding) {
        super(parentStyleSheet, parentRule);
        this.encoding_ = encoding;
    }
    
    public CSSCharsetRuleImpl() {
    }
    
    public short getType() {
        return 2;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        sb.append("@charset \"");
        final String enc = this.getEncoding();
        if (null != enc) {
            sb.append(enc);
        }
        sb.append("\";");
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
            if (r.getType() != 2) {
                throw new DOMExceptionImpl((short)13, 5);
            }
            this.encoding_ = ((CSSCharsetRuleImpl)r).encoding_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public String getEncoding() {
        return this.encoding_;
    }
    
    public void setEncoding(final String encoding) throws DOMException {
        this.encoding_ = encoding;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSCharsetRule)) {
            return false;
        }
        final CSSCharsetRule ccr = (CSSCharsetRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getEncoding(), ccr.getEncoding());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.encoding_);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
