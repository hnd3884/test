package com.steadystate.css.sac;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;

public class DocumentHandlerAdapter implements DocumentHandlerExt, ErrorHandler
{
    private final DocumentHandler documentHanlder_;
    
    public DocumentHandlerAdapter(final DocumentHandler documentHanlder) {
        this.documentHanlder_ = documentHanlder;
    }
    
    public void startDocument(final InputSource source) throws CSSException {
        this.documentHanlder_.startDocument(source);
    }
    
    public void endDocument(final InputSource source) throws CSSException {
        this.documentHanlder_.endDocument(source);
    }
    
    public void comment(final String text) throws CSSException {
        this.documentHanlder_.comment(text);
    }
    
    public void ignorableAtRule(final String atRule) throws CSSException {
        this.documentHanlder_.ignorableAtRule(atRule);
    }
    
    public void ignorableAtRule(final String atRule, final Locator locator) throws CSSException {
        this.documentHanlder_.ignorableAtRule(atRule);
    }
    
    public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
        this.documentHanlder_.namespaceDeclaration(prefix, uri);
    }
    
    public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) throws CSSException {
        this.documentHanlder_.importStyle(uri, media, defaultNamespaceURI);
    }
    
    public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI, final Locator locator) throws CSSException {
        this.documentHanlder_.importStyle(uri, media, defaultNamespaceURI);
    }
    
    public void startMedia(final SACMediaList media) throws CSSException {
        this.documentHanlder_.startMedia(media);
    }
    
    public void startMedia(final SACMediaList media, final Locator locator) throws CSSException {
        this.documentHanlder_.startMedia(media);
    }
    
    public void endMedia(final SACMediaList media) throws CSSException {
        this.documentHanlder_.endMedia(media);
    }
    
    public void startPage(final String name, final String pseudoPage) throws CSSException {
        this.documentHanlder_.startPage(name, pseudoPage);
    }
    
    public void startPage(final String name, final String pseudoPage, final Locator locator) throws CSSException {
        this.documentHanlder_.startPage(name, pseudoPage);
    }
    
    public void endPage(final String name, final String pseudoPage) throws CSSException {
        this.documentHanlder_.endPage(name, pseudoPage);
    }
    
    public void startFontFace() throws CSSException {
        this.documentHanlder_.startFontFace();
    }
    
    public void startFontFace(final Locator locator) throws CSSException {
        this.documentHanlder_.startFontFace();
    }
    
    public void endFontFace() throws CSSException {
        this.documentHanlder_.endFontFace();
    }
    
    public void startSelector(final SelectorList selectors) throws CSSException {
        this.documentHanlder_.startSelector(selectors);
    }
    
    public void startSelector(final SelectorList selectors, final Locator locator) throws CSSException {
        this.documentHanlder_.startSelector(selectors);
    }
    
    public void endSelector(final SelectorList selectors) throws CSSException {
        this.documentHanlder_.endSelector(selectors);
    }
    
    public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
        this.documentHanlder_.property(name, value, important);
    }
    
    public void property(final String name, final LexicalUnit value, final boolean important, final Locator locator) {
        this.documentHanlder_.property(name, value, important);
    }
    
    public void charset(final String characterEncoding, final Locator locator) throws CSSException {
    }
    
    public void warning(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI()).append(" [").append(exception.getLineNumber()).append(":").append(exception.getColumnNumber()).append("] ").append(exception.getMessage());
        System.err.println(sb.toString());
    }
    
    public void error(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI()).append(" [").append(exception.getLineNumber()).append(":").append(exception.getColumnNumber()).append("] ").append(exception.getMessage());
        System.err.println(sb.toString());
    }
    
    public void fatalError(final CSSParseException exception) throws CSSException {
        final StringBuilder sb = new StringBuilder();
        sb.append(exception.getURI()).append(" [").append(exception.getLineNumber()).append(":").append(exception.getColumnNumber()).append("] ").append(exception.getMessage());
        System.err.println(sb.toString());
    }
}
