package com.steadystate.css.dom;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.steadystate.css.util.LangUtils;
import org.w3c.css.sac.ErrorHandler;
import com.steadystate.css.util.ThrowCssExceptionErrorHandler;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.css.CSSMediaRule;

public class CSSMediaRuleImpl extends AbstractCSSRuleImpl implements CSSMediaRule
{
    private static final long serialVersionUID = 6603734096445214651L;
    private MediaList media_;
    private CSSRuleList cssRules_;
    
    public void setMedia(final MediaList media) {
        this.media_ = media;
    }
    
    public void setCssRules(final CSSRuleList cssRules) {
        this.cssRules_ = cssRules;
    }
    
    public CSSMediaRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final MediaList media) {
        super(parentStyleSheet, parentRule);
        this.media_ = media;
    }
    
    public CSSMediaRuleImpl() {
    }
    
    public short getType() {
        return 4;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder("@media ");
        sb.append(((MediaListImpl)this.getMedia()).getMediaText(format));
        sb.append(" {");
        for (int i = 0; i < this.getCssRules().getLength(); ++i) {
            final CSSRule rule = this.getCssRules().item(i);
            sb.append(rule.getCssText()).append(" ");
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
            if (r.getType() != 4) {
                throw new DOMExceptionImpl((short)13, 7);
            }
            this.media_ = ((CSSMediaRuleImpl)r).media_;
            this.cssRules_ = ((CSSMediaRuleImpl)r).cssRules_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public MediaList getMedia() {
        return this.media_;
    }
    
    public CSSRuleList getCssRules() {
        if (this.cssRules_ == null) {
            this.cssRules_ = new CSSRuleListImpl();
        }
        return this.cssRules_;
    }
    
    public int insertRule(final String rule, final int index) throws DOMException {
        final CSSStyleSheetImpl parentStyleSheet = this.getParentStyleSheetImpl();
        if (parentStyleSheet != null && parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            final InputSource is = new InputSource((Reader)new StringReader(rule));
            final CSSOMParser parser = new CSSOMParser();
            parser.setParentStyleSheet(parentStyleSheet);
            parser.setErrorHandler((ErrorHandler)ThrowCssExceptionErrorHandler.INSTANCE);
            final CSSRule r = parser.parseRule(is);
            ((CSSRuleListImpl)this.getCssRules()).insert(r, index);
        }
        catch (final IndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(1, 1, e.getMessage());
        }
        catch (final CSSException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
        catch (final IOException e3) {
            throw new DOMExceptionImpl(12, 0, e3.getMessage());
        }
        return index;
    }
    
    public void deleteRule(final int index) throws DOMException {
        final CSSStyleSheetImpl parentStyleSheet = this.getParentStyleSheetImpl();
        if (parentStyleSheet != null && parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            ((CSSRuleListImpl)this.getCssRules()).delete(index);
        }
        catch (final IndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(1, 1, e.getMessage());
        }
    }
    
    public void setRuleList(final CSSRuleListImpl rules) {
        this.cssRules_ = rules;
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
        if (!(obj instanceof CSSMediaRule)) {
            return false;
        }
        final CSSMediaRule cmr = (CSSMediaRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getMedia(), cmr.getMedia()) && LangUtils.equals(this.getCssRules(), cmr.getCssRules());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.media_);
        hash = LangUtils.hashCode(hash, this.cssRules_);
        return hash;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(this.cssRules_);
        out.writeObject(this.media_);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.cssRules_ = (CSSRuleList)in.readObject();
        if (this.cssRules_ != null) {
            for (int i = 0; i < this.cssRules_.getLength(); ++i) {
                final CSSRule cssRule = this.cssRules_.item(i);
                if (cssRule instanceof AbstractCSSRuleImpl) {
                    ((AbstractCSSRuleImpl)cssRule).setParentRule(this);
                    ((AbstractCSSRuleImpl)cssRule).setParentStyleSheet(this.getParentStyleSheetImpl());
                }
            }
        }
        this.media_ = (MediaList)in.readObject();
    }
}
