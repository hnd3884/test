package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import javax.xml.validation.Schema;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.ValidatorHandler;

final class ValidatingUnmarshaller implements XmlVisitor, TextPredictor
{
    private final XmlVisitor next;
    private final ValidatorHandler validator;
    private NamespaceContext nsContext;
    private final TextPredictor predictor;
    private char[] buf;
    
    public ValidatingUnmarshaller(final Schema schema, final XmlVisitor next) {
        this.nsContext = null;
        this.buf = new char[256];
        this.validator = schema.newValidatorHandler();
        this.next = next;
        this.predictor = next.getPredictor();
        this.validator.setErrorHandler(new FatalAdapter(this.getContext()));
    }
    
    @Override
    public void startDocument(final LocatorEx locator, final NamespaceContext nsContext) throws SAXException {
        this.nsContext = nsContext;
        this.validator.setDocumentLocator(locator);
        this.validator.startDocument();
        this.next.startDocument(locator, nsContext);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.nsContext = null;
        this.validator.endDocument();
        this.next.endDocument();
    }
    
    @Override
    public void startElement(final TagName tagName) throws SAXException {
        if (this.nsContext != null) {
            final String tagNamePrefix = tagName.getPrefix().intern();
            if (tagNamePrefix != "") {
                this.validator.startPrefixMapping(tagNamePrefix, this.nsContext.getNamespaceURI(tagNamePrefix));
            }
        }
        this.validator.startElement(tagName.uri, tagName.local, tagName.getQname(), tagName.atts);
        this.next.startElement(tagName);
    }
    
    @Override
    public void endElement(final TagName tagName) throws SAXException {
        this.validator.endElement(tagName.uri, tagName.local, tagName.getQname());
        this.next.endElement(tagName);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String nsUri) throws SAXException {
        this.validator.startPrefixMapping(prefix, nsUri);
        this.next.startPrefixMapping(prefix, nsUri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.validator.endPrefixMapping(prefix);
        this.next.endPrefixMapping(prefix);
    }
    
    @Override
    public void text(final CharSequence pcdata) throws SAXException {
        final int len = pcdata.length();
        if (this.buf.length < len) {
            this.buf = new char[len];
        }
        for (int i = 0; i < len; ++i) {
            this.buf[i] = pcdata.charAt(i);
        }
        this.validator.characters(this.buf, 0, len);
        if (this.predictor.expectText()) {
            this.next.text(pcdata);
        }
    }
    
    @Override
    public UnmarshallingContext getContext() {
        return this.next.getContext();
    }
    
    @Override
    public TextPredictor getPredictor() {
        return this;
    }
    
    @Deprecated
    @Override
    public boolean expectText() {
        return true;
    }
}
