package com.steadystate.css.parser;

import java.lang.reflect.Method;
import com.steadystate.css.userdata.UserDataConstants;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.DOMException;
import com.steadystate.css.dom.Property;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSFontFaceRuleImpl;
import com.steadystate.css.dom.CSSPageRuleImpl;
import com.steadystate.css.dom.CSSMediaRuleImpl;
import org.w3c.dom.stylesheets.MediaList;
import com.steadystate.css.dom.CSSImportRuleImpl;
import com.steadystate.css.dom.MediaListImpl;
import com.steadystate.css.dom.CSSCharsetRuleImpl;
import com.steadystate.css.dom.CSSOMObject;
import com.steadystate.css.dom.CSSUnknownRuleImpl;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.CSSException;
import org.w3c.dom.css.CSSRuleList;
import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.sac.DocumentHandlerExt;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.LexicalUnit;
import com.steadystate.css.dom.CSSValueImpl;
import org.w3c.dom.css.CSSValue;
import java.util.Stack;
import org.w3c.dom.css.CSSRule;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import org.w3c.dom.css.CSSStyleDeclaration;
import java.io.IOException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.Node;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.ErrorHandler;
import java.util.logging.Logger;
import org.w3c.css.sac.helpers.ParserFactory;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import org.w3c.css.sac.Parser;

public class CSSOMParser
{
    private static final Object LOCK;
    private static final String DEFAULT_PARSER = "com.steadystate.css.parser.SACParserCSS21";
    private static String LastFailed_;
    private Parser parser_;
    private CSSStyleSheetImpl parentStyleSheet_;
    
    public CSSOMParser() {
        this(null);
    }
    
    public CSSOMParser(final Parser parser) {
        synchronized (CSSOMParser.LOCK) {
            if (null != parser) {
                System.setProperty("org.w3c.css.sac.parser", parser.getClass().getCanonicalName());
                this.parser_ = parser;
                return;
            }
            String currentParser = System.getProperty("org.w3c.css.sac.parser");
            try {
                if (null != CSSOMParser.LastFailed_ && CSSOMParser.LastFailed_.equals(currentParser)) {
                    this.parser_ = (Parser)new SACParserCSS21();
                }
                else {
                    if (null == currentParser) {
                        System.setProperty("org.w3c.css.sac.parser", "com.steadystate.css.parser.SACParserCSS21");
                        currentParser = "com.steadystate.css.parser.SACParserCSS21";
                    }
                    final ParserFactory factory = new ParserFactory();
                    this.parser_ = factory.makeParser();
                }
            }
            catch (final Exception e) {
                final Logger log = Logger.getLogger("com.steadystate.css");
                log.warning(e.toString());
                log.warning("using the default 'SACParserCSS21' instead");
                log.throwing("CSSOMParser", "consturctor", e);
                CSSOMParser.LastFailed_ = currentParser;
                this.parser_ = (Parser)new SACParserCSS21();
            }
        }
    }
    
    public void setErrorHandler(final ErrorHandler eh) {
        this.parser_.setErrorHandler(eh);
    }
    
    public CSSStyleSheet parseStyleSheet(final InputSource source, final Node ownerNode, final String href) throws IOException {
        final CSSOMHandler handler = new CSSOMHandler();
        handler.setOwnerNode(ownerNode);
        handler.setHref(href);
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        this.parser_.parseStyleSheet(source);
        final Object o = handler.getRoot();
        if (o instanceof CSSStyleSheet) {
            return (CSSStyleSheet)o;
        }
        return null;
    }
    
    public CSSStyleDeclaration parseStyleDeclaration(final InputSource source) throws IOException {
        final CSSStyleDeclarationImpl sd = new CSSStyleDeclarationImpl(null);
        this.parseStyleDeclaration(sd, source);
        return sd;
    }
    
    public void parseStyleDeclaration(final CSSStyleDeclaration sd, final InputSource source) throws IOException {
        final Stack<Object> nodeStack = new Stack<Object>();
        nodeStack.push(sd);
        final CSSOMHandler handler = new CSSOMHandler(nodeStack);
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        this.parser_.parseStyleDeclaration(source);
    }
    
    public CSSValue parsePropertyValue(final InputSource source) throws IOException {
        final CSSOMHandler handler = new CSSOMHandler();
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        final LexicalUnit lu = this.parser_.parsePropertyValue(source);
        if (null == lu) {
            return null;
        }
        return new CSSValueImpl(lu);
    }
    
    public CSSRule parseRule(final InputSource source) throws IOException {
        final CSSOMHandler handler = new CSSOMHandler();
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        this.parser_.parseRule(source);
        return (CSSRule)handler.getRoot();
    }
    
    public SelectorList parseSelectors(final InputSource source) throws IOException {
        final HandlerBase handler = new HandlerBase();
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        return this.parser_.parseSelectors(source);
    }
    
    public SACMediaList parseMedia(final InputSource source) throws IOException {
        final HandlerBase handler = new HandlerBase();
        this.parser_.setDocumentHandler((DocumentHandler)handler);
        if (this.parser_ instanceof AbstractSACParser) {
            return ((AbstractSACParser)this.parser_).parseMedia(source);
        }
        return null;
    }
    
    public void setParentStyleSheet(final CSSStyleSheetImpl parentStyleSheet) {
        this.parentStyleSheet_ = parentStyleSheet;
    }
    
    protected CSSStyleSheetImpl getParentStyleSheet() {
        return this.parentStyleSheet_;
    }
    
    static {
        LOCK = new Object();
    }
    
    class CSSOMHandler implements DocumentHandlerExt
    {
        private Stack<Object> nodeStack_;
        private Object root_;
        private Node ownerNode_;
        private String href_;
        
        private Node getOwnerNode() {
            return this.ownerNode_;
        }
        
        private void setOwnerNode(final Node ownerNode) {
            this.ownerNode_ = ownerNode;
        }
        
        private String getHref() {
            return this.href_;
        }
        
        private void setHref(final String href) {
            this.href_ = href;
        }
        
        CSSOMHandler(final Stack<Object> nodeStack) {
            this.nodeStack_ = nodeStack;
        }
        
        CSSOMHandler() {
            this.nodeStack_ = new Stack<Object>();
        }
        
        Object getRoot() {
            return this.root_;
        }
        
        public void startDocument(final InputSource source) throws CSSException {
            if (this.nodeStack_.empty()) {
                final CSSStyleSheetImpl ss = new CSSStyleSheetImpl();
                CSSOMParser.this.setParentStyleSheet(ss);
                ss.setOwnerNode(this.getOwnerNode());
                ss.setBaseUri(source.getURI());
                ss.setHref(this.getHref());
                ss.setMediaText(source.getMedia());
                ss.setTitle(source.getTitle());
                final CSSRuleListImpl rules = new CSSRuleListImpl();
                ss.setCssRules(rules);
                this.nodeStack_.push(ss);
                this.nodeStack_.push(rules);
            }
        }
        
        public void endDocument(final InputSource source) throws CSSException {
            this.nodeStack_.pop();
            this.root_ = this.nodeStack_.pop();
        }
        
        public void comment(final String text) throws CSSException {
        }
        
        public void ignorableAtRule(final String atRule) throws CSSException {
            this.ignorableAtRule(atRule, null);
        }
        
        public void ignorableAtRule(final String atRule, final Locator locator) throws CSSException {
            final CSSUnknownRuleImpl ir = new CSSUnknownRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), atRule);
            this.addLocator(locator, ir);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(ir);
            }
            else {
                this.root_ = ir;
            }
        }
        
        public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
        }
        
        public void charset(final String characterEncoding, final Locator locator) throws CSSException {
            final CSSCharsetRuleImpl cr = new CSSCharsetRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), characterEncoding);
            this.addLocator(locator, cr);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(cr);
            }
            else {
                this.root_ = cr;
            }
        }
        
        public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) throws CSSException {
            this.importStyle(uri, media, defaultNamespaceURI, null);
        }
        
        public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI, final Locator locator) throws CSSException {
            final CSSImportRuleImpl ir = new CSSImportRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), uri, new MediaListImpl(media));
            this.addLocator(locator, ir);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(ir);
            }
            else {
                this.root_ = ir;
            }
        }
        
        public void startMedia(final SACMediaList media) throws CSSException {
            this.startMedia(media, null);
        }
        
        public void startMedia(final SACMediaList media, final Locator locator) throws CSSException {
            final MediaListImpl ml = new MediaListImpl(media);
            final CSSMediaRuleImpl mr = new CSSMediaRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), ml);
            this.addLocator(locator, mr);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(mr);
            }
            final CSSRuleListImpl rules = new CSSRuleListImpl();
            mr.setRuleList(rules);
            this.nodeStack_.push(mr);
            this.nodeStack_.push(rules);
        }
        
        public void endMedia(final SACMediaList media) throws CSSException {
            this.nodeStack_.pop();
            this.root_ = this.nodeStack_.pop();
        }
        
        public void startPage(final String name, final String pseudoPage) throws CSSException {
            this.startPage(name, pseudoPage, null);
        }
        
        public void startPage(final String name, final String pseudoPage, final Locator locator) throws CSSException {
            final CSSPageRuleImpl pr = new CSSPageRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), pseudoPage);
            this.addLocator(locator, pr);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(pr);
            }
            final CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(pr);
            pr.setStyle(decl);
            this.nodeStack_.push(pr);
            this.nodeStack_.push(decl);
        }
        
        public void endPage(final String name, final String pseudoPage) throws CSSException {
            this.nodeStack_.pop();
            this.root_ = this.nodeStack_.pop();
        }
        
        public void startFontFace() throws CSSException {
            this.startFontFace(null);
        }
        
        public void startFontFace(final Locator locator) throws CSSException {
            final CSSFontFaceRuleImpl ffr = new CSSFontFaceRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule());
            this.addLocator(locator, ffr);
            if (!this.nodeStack_.empty()) {
                this.nodeStack_.peek().add(ffr);
            }
            final CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(ffr);
            ffr.setStyle(decl);
            this.nodeStack_.push(ffr);
            this.nodeStack_.push(decl);
        }
        
        public void endFontFace() throws CSSException {
            this.nodeStack_.pop();
            this.root_ = this.nodeStack_.pop();
        }
        
        public void startSelector(final SelectorList selectors) throws CSSException {
            this.startSelector(selectors, null);
        }
        
        public void startSelector(final SelectorList selectors, final Locator locator) throws CSSException {
            final CSSStyleRuleImpl sr = new CSSStyleRuleImpl(CSSOMParser.this.getParentStyleSheet(), this.getParentRule(), selectors);
            this.addLocator(locator, sr);
            if (!this.nodeStack_.empty()) {
                final Object o = this.nodeStack_.peek();
                ((CSSRuleListImpl)o).add(sr);
            }
            final CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(sr);
            sr.setStyle(decl);
            this.nodeStack_.push(sr);
            this.nodeStack_.push(decl);
        }
        
        public void endSelector(final SelectorList selectors) throws CSSException {
            this.nodeStack_.pop();
            this.root_ = this.nodeStack_.pop();
        }
        
        public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
            this.property(name, value, important, null);
        }
        
        public void property(final String name, final LexicalUnit value, final boolean important, final Locator locator) {
            final CSSStyleDeclarationImpl decl = this.nodeStack_.peek();
            try {
                final Property property = new Property(name, new CSSValueImpl(value), important);
                this.addLocator(locator, property);
                decl.addProperty(property);
            }
            catch (final DOMException e) {
                if (CSSOMParser.this.parser_ instanceof AbstractSACParser) {
                    final AbstractSACParser parser = (AbstractSACParser)CSSOMParser.this.parser_;
                    parser.getErrorHandler().error(parser.toCSSParseException(e));
                }
            }
        }
        
        private CSSRule getParentRule() {
            if (!this.nodeStack_.empty() && this.nodeStack_.size() > 1) {
                final Object node = this.nodeStack_.get(this.nodeStack_.size() - 2);
                if (node instanceof CSSRule) {
                    return (CSSRule)node;
                }
            }
            return null;
        }
        
        private void addLocator(Locator locator, final CSSOMObject cssomObject) {
            if (locator == null) {
                final Parser parser = CSSOMParser.this.parser_;
                try {
                    final Method getLocatorMethod = parser.getClass().getMethod("getLocator", (Class<?>[])null);
                    locator = (Locator)getLocatorMethod.invoke(parser, (Object[])null);
                }
                catch (final SecurityException ex) {}
                catch (final NoSuchMethodException ex2) {}
                catch (final IllegalArgumentException ex3) {}
                catch (final IllegalAccessException ex4) {}
                catch (final InvocationTargetException ex5) {}
            }
            if (locator != null) {
                cssomObject.setUserData(UserDataConstants.KEY_LOCATOR, locator);
            }
        }
    }
}
