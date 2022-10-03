package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import java.util.Iterator;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.StyleSheet;
import java.util.ArrayList;
import org.w3c.dom.css.CSSStyleSheet;
import java.util.List;
import org.w3c.dom.stylesheets.StyleSheetList;

public class CSSStyleSheetListImpl implements StyleSheetList
{
    private List<CSSStyleSheet> cssStyleSheets_;
    
    public List<CSSStyleSheet> getCSSStyleSheets() {
        if (this.cssStyleSheets_ == null) {
            this.cssStyleSheets_ = new ArrayList<CSSStyleSheet>();
        }
        return this.cssStyleSheets_;
    }
    
    public void setCSSStyleSheets(final List<CSSStyleSheet> cssStyleSheets) {
        this.cssStyleSheets_ = cssStyleSheets;
    }
    
    public int getLength() {
        return this.getCSSStyleSheets().size();
    }
    
    public StyleSheet item(final int index) {
        return this.getCSSStyleSheets().get(index);
    }
    
    public void add(final CSSStyleSheet cssStyleSheet) {
        this.getCSSStyleSheets().add(cssStyleSheet);
    }
    
    public StyleSheet merge() {
        final CSSStyleSheetImpl merged = new CSSStyleSheetImpl();
        final CSSRuleListImpl cssRuleList = new CSSRuleListImpl();
        for (final CSSStyleSheetImpl cssStyleSheet : this.getCSSStyleSheets()) {
            final CSSMediaRuleImpl cssMediaRule = new CSSMediaRuleImpl(merged, null, cssStyleSheet.getMedia());
            cssMediaRule.setRuleList((CSSRuleListImpl)cssStyleSheet.getCssRules());
            cssRuleList.add(cssMediaRule);
        }
        merged.setCssRules(cssRuleList);
        merged.setMediaText("all");
        return merged;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StyleSheetList)) {
            return false;
        }
        final StyleSheetList ssl = (StyleSheetList)obj;
        return this.equalsStyleSheets(ssl);
    }
    
    private boolean equalsStyleSheets(final StyleSheetList ssl) {
        if (ssl == null || this.getLength() != ssl.getLength()) {
            return false;
        }
        for (int i = 0; i < this.getLength(); ++i) {
            final StyleSheet styleSheet1 = this.item(i);
            final StyleSheet styleSheet2 = ssl.item(i);
            if (!LangUtils.equals(styleSheet1, styleSheet2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.cssStyleSheets_);
        return hash;
    }
}
