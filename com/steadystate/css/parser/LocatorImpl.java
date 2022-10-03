package com.steadystate.css.parser;

import com.steadystate.css.util.LangUtils;
import java.io.Serializable;
import org.w3c.css.sac.Locator;

public class LocatorImpl implements Locator, Serializable
{
    private static final long serialVersionUID = 2240824537064705530L;
    private String uri_;
    private int lineNumber_;
    private int columnNumber_;
    
    public LocatorImpl(final String uri, final int line, final int column) {
        this.uri_ = uri;
        this.lineNumber_ = line;
        this.columnNumber_ = column;
    }
    
    public String getURI() {
        return this.uri_;
    }
    
    public String getUri() {
        return this.uri_;
    }
    
    public void setUri(final String uri) {
        this.uri_ = uri;
    }
    
    public int getColumnNumber() {
        return this.columnNumber_;
    }
    
    public void setColumnNumber(final int column) {
        this.columnNumber_ = column;
    }
    
    public int getLineNumber() {
        return this.lineNumber_;
    }
    
    public void setLineNumber(final int line) {
        this.lineNumber_ = line;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Locator)) {
            return false;
        }
        final Locator l = (Locator)obj;
        return this.getColumnNumber() == l.getColumnNumber() && this.getLineNumber() == l.getLineNumber() && LangUtils.equals(this.getURI(), l.getURI());
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.columnNumber_);
        hash = LangUtils.hashCode(hash, this.lineNumber_);
        hash = LangUtils.hashCode(hash, this.uri_);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.getUri() + " (" + this.getLineNumber() + ':' + this.getColumnNumber() + ')';
    }
}
