package org.cyberneko.html.parsers;

import org.w3c.dom.EntityReference;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Comment;
import org.w3c.dom.ProcessingInstruction;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.xml.sax.ErrorHandler;
import java.io.Reader;
import java.io.InputStream;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.XMLDocumentHandler;

public class DOMFragmentParser implements XMLDocumentHandler
{
    protected static final String DOCUMENT_FRAGMENT = "http://cyberneko.org/html/features/document-fragment";
    protected static final String[] RECOGNIZED_FEATURES;
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    protected static final String[] RECOGNIZED_PROPERTIES;
    protected XMLParserConfiguration fParserConfiguration;
    protected XMLDocumentSource fDocumentSource;
    protected DocumentFragment fDocumentFragment;
    protected Document fDocument;
    protected Node fCurrentNode;
    protected boolean fInCDATASection;
    
    public DOMFragmentParser() {
        (this.fParserConfiguration = (XMLParserConfiguration)new HTMLConfiguration()).addRecognizedFeatures(DOMFragmentParser.RECOGNIZED_FEATURES);
        this.fParserConfiguration.addRecognizedProperties(DOMFragmentParser.RECOGNIZED_PROPERTIES);
        this.fParserConfiguration.setFeature("http://cyberneko.org/html/features/document-fragment", true);
        this.fParserConfiguration.setDocumentHandler((XMLDocumentHandler)this);
    }
    
    public void parse(final String systemId, final DocumentFragment fragment) throws SAXException, IOException {
        this.parse(new InputSource(systemId), fragment);
    }
    
    public void parse(final InputSource source, final DocumentFragment fragment) throws SAXException, IOException {
        this.fDocumentFragment = fragment;
        this.fCurrentNode = fragment;
        this.fDocument = this.fDocumentFragment.getOwnerDocument();
        try {
            final String pubid = source.getPublicId();
            final String sysid = source.getSystemId();
            final String encoding = source.getEncoding();
            final InputStream stream = source.getByteStream();
            final Reader reader = source.getCharacterStream();
            final XMLInputSource inputSource = new XMLInputSource(pubid, sysid, sysid);
            inputSource.setEncoding(encoding);
            inputSource.setByteStream(stream);
            inputSource.setCharacterStream(reader);
            this.fParserConfiguration.parse(inputSource);
        }
        catch (final XMLParseException e) {
            final Exception ex = e.getException();
            if (ex != null) {
                throw new SAXParseException(e.getMessage(), null, ex);
            }
            throw new SAXParseException(e.getMessage(), (Locator)null);
        }
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fParserConfiguration.setErrorHandler((XMLErrorHandler)new ErrorHandlerWrapper(errorHandler));
    }
    
    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            final XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fParserConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler != null && xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (final XMLConfigurationException ex) {}
        return errorHandler;
    }
    
    public void setFeature(final String featureId, final boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fParserConfiguration.setFeature(featureId, state);
        }
        catch (final XMLConfigurationException e) {
            final String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }
    
    public boolean getFeature(final String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            return this.fParserConfiguration.getFeature(featureId);
        }
        catch (final XMLConfigurationException e) {
            final String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }
    
    public void setProperty(final String propertyId, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fParserConfiguration.setProperty(propertyId, value);
        }
        catch (final XMLConfigurationException e) {
            final String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }
    
    public Object getProperty(final String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (propertyId.equals("http://apache.org/xml/properties/dom/current-element-node")) {
            return (this.fCurrentNode != null && this.fCurrentNode.getNodeType() == 1) ? this.fCurrentNode : null;
        }
        try {
            return this.fParserConfiguration.getProperty(propertyId);
        }
        catch (final XMLConfigurationException e) {
            final String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }
    
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
    }
    
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
    }
    
    public void doctypeDecl(final String root, final String pubid, final String sysid, final Augmentations augs) throws XNIException {
    }
    
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        final String s = data.toString();
        if (XMLChar.isValidName(s)) {
            final ProcessingInstruction pi = this.fDocument.createProcessingInstruction(target, s);
            this.fCurrentNode.appendChild(pi);
        }
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        final Comment comment = this.fDocument.createComment(text.toString());
        this.fCurrentNode.appendChild(comment);
    }
    
    public void startPrefixMapping(final String prefix, final String uri, final Augmentations augs) throws XNIException {
    }
    
    public void endPrefixMapping(final String prefix, final Augmentations augs) throws XNIException {
    }
    
    public void startElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        final Element elementNode = this.fDocument.createElement(element.rawname);
        for (int count = (attrs != null) ? attrs.getLength() : 0, i = 0; i < count; ++i) {
            final String aname = attrs.getQName(i);
            final String avalue = attrs.getValue(i);
            if (XMLChar.isValidName(aname)) {
                elementNode.setAttribute(aname, avalue);
            }
        }
        this.fCurrentNode.appendChild(elementNode);
        this.fCurrentNode = elementNode;
    }
    
    public void emptyElement(final QName element, final XMLAttributes attrs, final Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        this.endElement(element, augs);
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fInCDATASection) {
            final Node node = this.fCurrentNode.getLastChild();
            if (node != null && node.getNodeType() == 4) {
                final CDATASection cdata = (CDATASection)node;
                cdata.appendData(text.toString());
            }
            else {
                final CDATASection cdata = this.fDocument.createCDATASection(text.toString());
                this.fCurrentNode.appendChild(cdata);
            }
        }
        else {
            final Node node = this.fCurrentNode.getLastChild();
            if (node != null && node.getNodeType() == 3) {
                final Text textNode = (Text)node;
                textNode.appendData(text.toString());
            }
            else {
                final Text textNode = this.fDocument.createTextNode(text.toString());
                this.fCurrentNode.appendChild(textNode);
            }
        }
    }
    
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }
    
    public void startGeneralEntity(final String name, final XMLResourceIdentifier id, final String encoding, final Augmentations augs) throws XNIException {
        final EntityReference entityRef = this.fDocument.createEntityReference(name);
        this.fCurrentNode.appendChild(entityRef);
        this.fCurrentNode = entityRef;
    }
    
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }
    
    public void endDocument(final Augmentations augs) throws XNIException {
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://cyberneko.org/html/features/document-fragment" };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/dom/current-element-node" };
    }
}
