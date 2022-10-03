package org.apache.tika.sax;

import org.apache.tika.metadata.TikaCoreProperties;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.Attributes;
import java.util.Set;

public class XHTMLContentHandler extends SafeContentHandler
{
    public static final String XHTML = "http://www.w3.org/1999/xhtml";
    public static final Set<String> ENDLINE;
    private static final char[] NL;
    private static final char[] TAB;
    private static final Set<String> HEAD;
    private static final Set<String> AUTO;
    private static final Set<String> INDENT;
    private static final Attributes EMPTY_ATTRIBUTES;
    private final Metadata metadata;
    private boolean documentStarted;
    private boolean headStarted;
    private boolean headEnded;
    private boolean useFrameset;
    
    public XHTMLContentHandler(final ContentHandler handler, final Metadata metadata) {
        super(handler);
        this.documentStarted = false;
        this.headStarted = false;
        this.headEnded = false;
        this.useFrameset = false;
        this.metadata = metadata;
    }
    
    private static Set<String> unmodifiableSet(final String... elements) {
        return Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(elements)));
    }
    
    @Override
    public void startDocument() throws SAXException {
        if (!this.documentStarted) {
            this.documentStarted = true;
            super.startDocument();
            this.startPrefixMapping("", "http://www.w3.org/1999/xhtml");
        }
    }
    
    private void lazyStartHead() throws SAXException {
        if (!this.headStarted) {
            this.headStarted = true;
            final AttributesImpl htmlAttrs = new AttributesImpl();
            final String lang = this.metadata.get("Content-Language");
            if (lang != null) {
                htmlAttrs.addAttribute("", "lang", "lang", "CDATA", lang);
            }
            super.startElement("http://www.w3.org/1999/xhtml", "html", "html", htmlAttrs);
            this.newline();
            super.startElement("http://www.w3.org/1999/xhtml", "head", "head", XHTMLContentHandler.EMPTY_ATTRIBUTES);
            this.newline();
        }
    }
    
    private void lazyEndHead(final boolean isFrameset) throws SAXException {
        this.lazyStartHead();
        if (!this.headEnded) {
            this.headEnded = true;
            this.useFrameset = isFrameset;
            for (final String name : this.metadata.names()) {
                if (!name.equals("title")) {
                    for (final String value : this.metadata.getValues(name)) {
                        if (value != null) {
                            final AttributesImpl attributes = new AttributesImpl();
                            attributes.addAttribute("", "name", "name", "CDATA", name);
                            attributes.addAttribute("", "content", "content", "CDATA", value);
                            super.startElement("http://www.w3.org/1999/xhtml", "meta", "meta", attributes);
                            super.endElement("http://www.w3.org/1999/xhtml", "meta", "meta");
                            this.newline();
                        }
                    }
                }
            }
            super.startElement("http://www.w3.org/1999/xhtml", "title", "title", XHTMLContentHandler.EMPTY_ATTRIBUTES);
            final String title = this.metadata.get(TikaCoreProperties.TITLE);
            if (title != null && title.length() > 0) {
                final char[] titleChars = title.toCharArray();
                super.characters(titleChars, 0, titleChars.length);
            }
            else {
                super.characters(new char[0], 0, 0);
            }
            super.endElement("http://www.w3.org/1999/xhtml", "title", "title");
            this.newline();
            super.endElement("http://www.w3.org/1999/xhtml", "head", "head");
            this.newline();
            if (this.useFrameset) {
                super.startElement("http://www.w3.org/1999/xhtml", "frameset", "frameset", XHTMLContentHandler.EMPTY_ATTRIBUTES);
            }
            else {
                super.startElement("http://www.w3.org/1999/xhtml", "body", "body", XHTMLContentHandler.EMPTY_ATTRIBUTES);
            }
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.lazyEndHead(this.useFrameset);
        if (this.useFrameset) {
            super.endElement("http://www.w3.org/1999/xhtml", "frameset", "frameset");
        }
        else {
            super.endElement("http://www.w3.org/1999/xhtml", "body", "body");
        }
        super.endElement("http://www.w3.org/1999/xhtml", "html", "html");
        this.endPrefixMapping("");
        super.endDocument();
    }
    
    @Override
    public void startElement(final String uri, final String local, final String name, final Attributes attributes) throws SAXException {
        if (name.equals("frameset")) {
            this.lazyEndHead(true);
        }
        else if (!XHTMLContentHandler.AUTO.contains(name)) {
            if (XHTMLContentHandler.HEAD.contains(name)) {
                this.lazyStartHead();
            }
            else {
                this.lazyEndHead(false);
            }
            if ("http://www.w3.org/1999/xhtml".equals(uri) && XHTMLContentHandler.INDENT.contains(name)) {
                this.ignorableWhitespace(XHTMLContentHandler.TAB, 0, XHTMLContentHandler.TAB.length);
            }
            super.startElement(uri, local, name, attributes);
        }
    }
    
    @Override
    public void endElement(final String uri, final String local, final String name) throws SAXException {
        if (!XHTMLContentHandler.AUTO.contains(name)) {
            super.endElement(uri, local, name);
            if ("http://www.w3.org/1999/xhtml".equals(uri) && XHTMLContentHandler.ENDLINE.contains(name)) {
                this.newline();
            }
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.lazyEndHead(this.useFrameset);
        super.characters(ch, start, length);
    }
    
    public void startElement(final String name) throws SAXException {
        this.startElement("http://www.w3.org/1999/xhtml", name, name, XHTMLContentHandler.EMPTY_ATTRIBUTES);
    }
    
    public void startElement(final String name, final String attribute, final String value) throws SAXException {
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", attribute, attribute, "CDATA", value);
        this.startElement("http://www.w3.org/1999/xhtml", name, name, attributes);
    }
    
    public void startElement(final String name, final AttributesImpl attributes) throws SAXException {
        this.startElement("http://www.w3.org/1999/xhtml", name, name, attributes);
    }
    
    public void endElement(final String name) throws SAXException {
        this.endElement("http://www.w3.org/1999/xhtml", name, name);
    }
    
    public void characters(final String characters) throws SAXException {
        if (characters != null && characters.length() > 0) {
            this.characters(characters.toCharArray(), 0, characters.length());
        }
    }
    
    public void newline() throws SAXException {
        this.ignorableWhitespace(XHTMLContentHandler.NL, 0, XHTMLContentHandler.NL.length);
    }
    
    public void element(final String name, final String value) throws SAXException {
        if (value != null && value.length() > 0) {
            this.startElement(name);
            this.characters(value);
            this.endElement(name);
        }
    }
    
    @Override
    protected boolean isInvalid(final int ch) {
        return super.isInvalid(ch) || (127 <= ch && ch <= 159);
    }
    
    static {
        ENDLINE = unmodifiableSet("p", "h1", "h2", "h3", "h4", "h5", "h6", "div", "ul", "ol", "dl", "pre", "hr", "blockquote", "address", "fieldset", "table", "form", "noscript", "li", "dt", "dd", "noframes", "br", "tr", "select", "option", "link", "script");
        NL = new char[] { '\n' };
        TAB = new char[] { '\t' };
        HEAD = unmodifiableSet("title", "link", "base", "meta", "script");
        AUTO = unmodifiableSet("head", "frameset");
        INDENT = unmodifiableSet("li", "dd", "dt", "td", "th", "frame");
        EMPTY_ATTRIBUTES = new AttributesImpl();
    }
}
