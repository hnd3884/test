package javax.xml.validation;

import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;

public abstract class ValidatorHandler implements ContentHandler
{
    protected ValidatorHandler() {
    }
    
    public abstract void setContentHandler(final ContentHandler p0);
    
    public abstract ContentHandler getContentHandler();
    
    public abstract void setErrorHandler(final ErrorHandler p0);
    
    public abstract ErrorHandler getErrorHandler();
    
    public abstract void setResourceResolver(final LSResourceResolver p0);
    
    public abstract LSResourceResolver getResourceResolver();
    
    public abstract TypeInfoProvider getTypeInfoProvider();
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public void setFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        throw new SAXNotRecognizedException(s);
    }
    
    public abstract void skippedEntity(final String p0) throws SAXException;
    
    public abstract void processingInstruction(final String p0, final String p1) throws SAXException;
    
    public abstract void ignorableWhitespace(final char[] p0, final int p1, final int p2) throws SAXException;
    
    public abstract void characters(final char[] p0, final int p1, final int p2) throws SAXException;
    
    public abstract void endElement(final String p0, final String p1, final String p2) throws SAXException;
    
    public abstract void startElement(final String p0, final String p1, final String p2, final Attributes p3) throws SAXException;
    
    public abstract void endPrefixMapping(final String p0) throws SAXException;
    
    public abstract void startPrefixMapping(final String p0, final String p1) throws SAXException;
    
    public abstract void endDocument() throws SAXException;
    
    public abstract void startDocument() throws SAXException;
    
    public abstract void setDocumentLocator(final Locator p0);
}
