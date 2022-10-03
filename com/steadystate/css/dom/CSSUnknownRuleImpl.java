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
import org.w3c.dom.css.CSSUnknownRule;

public class CSSUnknownRuleImpl extends AbstractCSSRuleImpl implements CSSUnknownRule
{
    private static final long serialVersionUID = -268104019127675990L;
    private String text_;
    
    public String getText() {
        return this.text_;
    }
    
    public void setText(final String text) {
        this.text_ = text;
    }
    
    public CSSUnknownRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final String text) {
        super(parentStyleSheet, parentRule);
        this.text_ = text;
    }
    
    public CSSUnknownRuleImpl() {
    }
    
    public short getType() {
        return 0;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        if (null == this.text_) {
            return "";
        }
        return this.text_;
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
            if (r.getType() != 0) {
                throw new DOMExceptionImpl((short)13, 8);
            }
            this.text_ = ((CSSUnknownRuleImpl)r).text_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
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
        if (!(obj instanceof CSSUnknownRule)) {
            return false;
        }
        final CSSUnknownRule cur = (CSSUnknownRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getCssText(), cur.getCssText());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.text_);
        return hash;
    }
}
