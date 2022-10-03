package com.steadystate.css.parser;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.ErrorHandler;
import com.steadystate.css.sac.DocumentHandlerExt;

public class HandlerBase implements DocumentHandlerExt, ErrorHandler
{
    public void startDocument(final InputSource source) throws CSSException {
    }
    
    public void endDocument(final InputSource source) throws CSSException {
    }
    
    public void comment(final String text) throws CSSException {
    }
    
    public void ignorableAtRule(final String atRule) throws CSSException {
    }
    
    public void ignorableAtRule(final String atRule, final Locator locator) throws CSSException {
    }
    
    public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
    }
    
    public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) throws CSSException {
    }
    
    public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI, final Locator locator) throws CSSException {
    }
    
    public void startMedia(final SACMediaList media) throws CSSException {
    }
    
    public void startMedia(final SACMediaList media, final Locator locator) throws CSSException {
    }
    
    public void endMedia(final SACMediaList media) throws CSSException {
    }
    
    public void startPage(final String name, final String pseudoPage) throws CSSException {
    }
    
    public void startPage(final String name, final String pseudoPage, final Locator locator) throws CSSException {
    }
    
    public void endPage(final String name, final String pseudoPage) throws CSSException {
    }
    
    public void startFontFace() throws CSSException {
    }
    
    public void startFontFace(final Locator locator) throws CSSException {
    }
    
    public void endFontFace() throws CSSException {
    }
    
    public void startSelector(final SelectorList selectors) throws CSSException {
    }
    
    public void startSelector(final SelectorList selectors, final Locator locator) throws CSSException {
    }
    
    public void endSelector(final SelectorList selectors) throws CSSException {
    }
    
    public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
    }
    
    public void property(final String name, final LexicalUnit value, final boolean important, final Locator locator) {
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
