package org.htmlparser.sax;

import org.htmlparser.util.NodeList;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.Remark;
import org.htmlparser.Node;
import java.io.IOException;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserFeedback;
import org.htmlparser.util.ParserException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.NamespaceSupport;
import org.htmlparser.Parser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;

public class XMLReader implements org.xml.sax.XMLReader
{
    protected boolean mNameSpaces;
    protected boolean mNameSpacePrefixes;
    protected EntityResolver mEntityResolver;
    protected DTDHandler mDTDHandler;
    protected ContentHandler mContentHandler;
    protected ErrorHandler mErrorHandler;
    protected Parser mParser;
    protected NamespaceSupport mSupport;
    protected String[] mParts;
    
    public XMLReader() {
        this.mNameSpaces = true;
        this.mNameSpacePrefixes = false;
        this.mEntityResolver = null;
        this.mDTDHandler = null;
        this.mContentHandler = null;
        this.mErrorHandler = null;
        (this.mSupport = new NamespaceSupport()).pushContext();
        this.mSupport.declarePrefix("", "http://www.w3.org/TR/REC-html40");
        this.mParts = new String[3];
    }
    
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        boolean ret;
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            ret = this.mNameSpaces;
        }
        else {
            if (!name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                throw new SAXNotSupportedException(name + " not yet understood");
            }
            ret = this.mNameSpacePrefixes;
        }
        return ret;
    }
    
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            this.mNameSpaces = value;
        }
        else {
            if (!name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                throw new SAXNotSupportedException(name + " not yet understood");
            }
            this.mNameSpacePrefixes = value;
        }
    }
    
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name + " not yet understood");
    }
    
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException(name + " not yet understood");
    }
    
    public void setEntityResolver(final EntityResolver resolver) {
        this.mEntityResolver = resolver;
    }
    
    public EntityResolver getEntityResolver() {
        return this.mEntityResolver;
    }
    
    public void setDTDHandler(final DTDHandler handler) {
        this.mDTDHandler = handler;
    }
    
    public DTDHandler getDTDHandler() {
        return this.mDTDHandler;
    }
    
    public void setContentHandler(final ContentHandler handler) {
        this.mContentHandler = handler;
    }
    
    public ContentHandler getContentHandler() {
        return this.mContentHandler;
    }
    
    public void setErrorHandler(final ErrorHandler handler) {
        this.mErrorHandler = handler;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.mErrorHandler;
    }
    
    public void parse(final InputSource input) throws IOException, SAXException {
        if (null != this.mContentHandler) {
            try {
                this.mParser = new Parser(new Lexer(new Page(input.getByteStream(), input.getEncoding())));
                final Locator locator = new Locator(this.mParser);
                ParserFeedback feedback;
                if (null != this.mErrorHandler) {
                    feedback = new Feedback(this.mErrorHandler, locator);
                }
                else {
                    feedback = new DefaultParserFeedback(0);
                }
                this.mParser.setFeedback(feedback);
                this.mContentHandler.setDocumentLocator(locator);
                try {
                    this.mContentHandler.startDocument();
                    final NodeIterator iterator = this.mParser.elements();
                    while (iterator.hasMoreNodes()) {
                        this.doSAX(iterator.nextNode());
                    }
                    this.mContentHandler.endDocument();
                }
                catch (final SAXException se) {
                    if (null != this.mErrorHandler) {
                        this.mErrorHandler.fatalError(new SAXParseException("contentHandler threw me", locator, se));
                    }
                }
            }
            catch (final ParserException pe) {
                if (null != this.mErrorHandler) {
                    this.mErrorHandler.fatalError(new SAXParseException(pe.getMessage(), "", "", 0, 0));
                }
            }
        }
    }
    
    public void parse(final String systemId) throws IOException, SAXException {
        if (null != this.mContentHandler) {
            try {
                this.mParser = new Parser(systemId);
                final Locator locator = new Locator(this.mParser);
                ParserFeedback feedback;
                if (null != this.mErrorHandler) {
                    feedback = new Feedback(this.mErrorHandler, locator);
                }
                else {
                    feedback = new DefaultParserFeedback(0);
                }
                this.mParser.setFeedback(feedback);
                this.mContentHandler.setDocumentLocator(locator);
                try {
                    this.mContentHandler.startDocument();
                    final NodeIterator iterator = this.mParser.elements();
                    while (iterator.hasMoreNodes()) {
                        this.doSAX(iterator.nextNode());
                    }
                    this.mContentHandler.endDocument();
                }
                catch (final SAXException se) {
                    if (null != this.mErrorHandler) {
                        this.mErrorHandler.fatalError(new SAXParseException("contentHandler threw me", locator, se));
                    }
                }
            }
            catch (final ParserException pe) {
                if (null != this.mErrorHandler) {
                    this.mErrorHandler.fatalError(new SAXParseException(pe.getMessage(), "", systemId, 0, 0));
                }
            }
        }
    }
    
    protected void doSAX(final Node node) throws ParserException, SAXException {
        if (node instanceof Remark) {
            final String text = this.mParser.getLexer().getPage().getText(node.getStartPosition(), node.getEndPosition());
            this.mContentHandler.ignorableWhitespace(text.toCharArray(), 0, text.length());
        }
        else if (node instanceof Text) {
            final String text = this.mParser.getLexer().getPage().getText(node.getStartPosition(), node.getEndPosition());
            this.mContentHandler.characters(text.toCharArray(), 0, text.length());
        }
        else if (node instanceof Tag) {
            final Tag tag = (Tag)node;
            if (this.mNameSpaces) {
                this.mSupport.processName(tag.getTagName(), this.mParts, false);
            }
            else {
                this.mParts[0] = "";
                this.mParts[1] = "";
            }
            if (this.mNameSpacePrefixes) {
                this.mParts[2] = tag.getTagName();
            }
            else if (this.mNameSpaces) {
                this.mParts[2] = "";
            }
            else {
                this.mParts[2] = tag.getTagName();
            }
            this.mContentHandler.startElement(this.mParts[0], this.mParts[1], this.mParts[2], new Attributes(tag, this.mSupport, this.mParts));
            final NodeList children = tag.getChildren();
            if (null != children) {
                for (int i = 0; i < children.size(); ++i) {
                    this.doSAX(children.elementAt(i));
                }
            }
            final Tag end = tag.getEndTag();
            if (null != end) {
                if (this.mNameSpaces) {
                    this.mSupport.processName(end.getTagName(), this.mParts, false);
                }
                else {
                    this.mParts[0] = "";
                    this.mParts[1] = "";
                }
                if (this.mNameSpacePrefixes) {
                    this.mParts[2] = end.getTagName();
                }
                else if (this.mNameSpaces) {
                    this.mParts[2] = "";
                }
                else {
                    this.mParts[2] = end.getTagName();
                }
                this.mContentHandler.endElement(this.mParts[0], this.mParts[1], this.mParts[2]);
            }
        }
    }
}
