package org.htmlparser.filters;

import org.htmlparser.Text;
import org.htmlparser.Node;
import java.util.Locale;
import org.htmlparser.NodeFilter;

public class StringFilter implements NodeFilter
{
    protected String mPattern;
    protected String mUpperPattern;
    protected boolean mCaseSensitive;
    protected Locale mLocale;
    
    public StringFilter() {
        this("", false);
    }
    
    public StringFilter(final String pattern) {
        this(pattern, false);
    }
    
    public StringFilter(final String pattern, final boolean sensitive) {
        this(pattern, sensitive, null);
    }
    
    public StringFilter(final String pattern, final boolean sensitive, final Locale locale) {
        this.mPattern = pattern;
        this.mCaseSensitive = sensitive;
        this.mLocale = ((null == locale) ? Locale.getDefault() : locale);
        this.setUpperPattern();
    }
    
    protected void setUpperPattern() {
        if (this.getCaseSensitive()) {
            this.mUpperPattern = this.getPattern();
        }
        else {
            this.mUpperPattern = this.getPattern().toUpperCase(this.getLocale());
        }
    }
    
    public boolean getCaseSensitive() {
        return this.mCaseSensitive;
    }
    
    public void setCaseSensitive(final boolean sensitive) {
        this.mCaseSensitive = sensitive;
        this.setUpperPattern();
    }
    
    public Locale getLocale() {
        return this.mLocale;
    }
    
    public void setLocale(final Locale locale) {
        this.mLocale = locale;
        this.setUpperPattern();
    }
    
    public String getPattern() {
        return this.mPattern;
    }
    
    public void setPattern(final String pattern) {
        this.mPattern = pattern;
        this.setUpperPattern();
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (node instanceof Text) {
            String string = ((Text)node).getText();
            if (!this.getCaseSensitive()) {
                string = string.toUpperCase(this.getLocale());
            }
            ret = (-1 != string.indexOf(this.mUpperPattern));
        }
        return ret;
    }
}
