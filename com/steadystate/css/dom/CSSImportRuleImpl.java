package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import com.steadystate.css.format.CSSFormat;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.css.CSSImportRule;

public class CSSImportRuleImpl extends AbstractCSSRuleImpl implements CSSImportRule
{
    private static final long serialVersionUID = 7807829682009179339L;
    private String href_;
    private MediaList media_;
    
    public void setHref(final String href) {
        this.href_ = href;
    }
    
    public void setMedia(final MediaList media) {
        this.media_ = media;
    }
    
    public CSSImportRuleImpl(final CSSStyleSheetImpl parentStyleSheet, final CSSRule parentRule, final String href, final MediaList media) {
        super(parentStyleSheet, parentRule);
        this.href_ = href;
        this.media_ = media;
    }
    
    public CSSImportRuleImpl() {
    }
    
    public short getType() {
        return 3;
    }
    
    @Override
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        sb.append("@import");
        final String href = this.getHref();
        if (null != href) {
            sb.append(" url(").append(href).append(")");
        }
        final MediaList ml = this.getMedia();
        if (null != ml && ml.getLength() > 0) {
            sb.append(" ").append(((MediaListImpl)this.getMedia()).getMediaText(format));
        }
        sb.append(";");
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
            if (r.getType() != 3) {
                throw new DOMExceptionImpl((short)13, 6);
            }
            this.href_ = ((CSSImportRuleImpl)r).href_;
            this.media_ = ((CSSImportRuleImpl)r).media_;
        }
        catch (final CSSException e) {
            throw new DOMExceptionImpl(12, 0, e.getMessage());
        }
        catch (final IOException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
    }
    
    public String getHref() {
        return this.href_;
    }
    
    public MediaList getMedia() {
        return this.media_;
    }
    
    public CSSStyleSheet getStyleSheet() {
        return null;
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
        if (!(obj instanceof CSSImportRule)) {
            return false;
        }
        final CSSImportRule cir = (CSSImportRule)obj;
        return super.equals(obj) && LangUtils.equals(this.getHref(), cir.getHref()) && LangUtils.equals(this.getMedia(), cir.getMedia());
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.href_);
        hash = LangUtils.hashCode(hash, this.media_);
        return hash;
    }
}
