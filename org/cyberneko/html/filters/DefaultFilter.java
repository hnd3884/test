package org.cyberneko.html.filters;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.cyberneko.html.HTMLComponent;
import org.apache.xerces.xni.parser.XMLDocumentFilter;

public class DefaultFilter implements XMLDocumentFilter, HTMLComponent
{
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startDocument(this.fDocumentHandler, locator, encoding, nscontext, augs);
        }
    }
    
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }
    
    public void doctypeDecl(final String root, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(root, publicId, systemId, augs);
        }
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(text, augs);
        }
    }
    
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }
    
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(element, attributes, augs);
        }
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(text, augs);
        }
    }
    
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    public void startGeneralEntity(final String name, final XMLResourceIdentifier id, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, id, encoding, augs);
        }
    }
    
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(element, augs);
        }
    }
    
    public void endDocument(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
    }
    
    public void startDocument(final XMLLocator locator, final String encoding, final Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }
    
    public void startPrefixMapping(final String prefix, final String uri, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
        }
    }
    
    public void endPrefixMapping(final String prefix, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
        }
    }
    
    public String[] getRecognizedFeatures() {
        return null;
    }
    
    public Boolean getFeatureDefault(final String featureId) {
        return null;
    }
    
    public String[] getRecognizedProperties() {
        return null;
    }
    
    public Object getPropertyDefault(final String propertyId) {
        return null;
    }
    
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
    }
    
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
    }
    
    protected static String[] merge(final String[] array1, final String[] array2) {
        if (array1 == array2) {
            return array1;
        }
        if (array1 == null) {
            return array2;
        }
        if (array2 == null) {
            return array1;
        }
        final String[] array3 = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, array3, 0, array1.length);
        System.arraycopy(array2, 0, array3, array1.length, array2.length);
        return array3;
    }
}
