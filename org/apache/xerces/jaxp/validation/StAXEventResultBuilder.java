package org.apache.xerces.jaxp.validation;

import java.util.NoSuchElementException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXResult;
import java.util.Iterator;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;

final class StAXEventResultBuilder implements StAXDocumentHandler
{
    private XMLEventWriter fEventWriter;
    private final XMLEventFactory fEventFactory;
    private final StAXValidatorHelper fStAXValidatorHelper;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private boolean fIgnoreChars;
    private boolean fInCDATA;
    private final QName fAttrName;
    private static final Iterator EMPTY_COLLECTION_ITERATOR;
    
    public StAXEventResultBuilder(final StAXValidatorHelper fStAXValidatorHelper, final JAXPNamespaceContextWrapper fNamespaceContext) {
        this.fAttrName = new QName();
        this.fStAXValidatorHelper = fStAXValidatorHelper;
        this.fNamespaceContext = fNamespaceContext;
        this.fEventFactory = XMLEventFactory.newInstance();
    }
    
    public void setStAXResult(final StAXResult stAXResult) {
        this.fIgnoreChars = false;
        this.fInCDATA = false;
        this.fEventWriter = ((stAXResult != null) ? stAXResult.getXMLEventWriter() : null);
    }
    
    public void startDocument(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final String version = xmlStreamReader.getVersion();
        final String characterEncodingScheme = xmlStreamReader.getCharacterEncodingScheme();
        this.fEventWriter.add(this.fEventFactory.createStartDocument((characterEncodingScheme != null) ? characterEncodingScheme : "UTF-8", (version != null) ? version : "1.0", xmlStreamReader.standaloneSet()));
    }
    
    public void endDocument(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        this.fEventWriter.add(this.fEventFactory.createEndDocument());
        this.fEventWriter.flush();
    }
    
    public void comment(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        this.fEventWriter.add(this.fEventFactory.createComment(xmlStreamReader.getText()));
    }
    
    public void processingInstruction(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final String piData = xmlStreamReader.getPIData();
        this.fEventWriter.add(this.fEventFactory.createProcessingInstruction(xmlStreamReader.getPITarget(), (piData != null) ? piData : ""));
    }
    
    public void entityReference(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final String localName = xmlStreamReader.getLocalName();
        this.fEventWriter.add(this.fEventFactory.createEntityReference(localName, this.fStAXValidatorHelper.getEntityDeclaration(localName)));
    }
    
    public void startDocument(final StartDocument startDocument) throws XMLStreamException {
        this.fEventWriter.add(startDocument);
    }
    
    public void endDocument(final EndDocument endDocument) throws XMLStreamException {
        this.fEventWriter.add(endDocument);
        this.fEventWriter.flush();
    }
    
    public void doctypeDecl(final DTD dtd) throws XMLStreamException {
        this.fEventWriter.add(dtd);
    }
    
    public void characters(final Characters characters) throws XMLStreamException {
        this.fEventWriter.add(characters);
    }
    
    public void cdata(final Characters characters) throws XMLStreamException {
        this.fEventWriter.add(characters);
    }
    
    public void comment(final Comment comment) throws XMLStreamException {
        this.fEventWriter.add(comment);
    }
    
    public void processingInstruction(final ProcessingInstruction processingInstruction) throws XMLStreamException {
        this.fEventWriter.add(processingInstruction);
    }
    
    public void entityReference(final EntityReference entityReference) throws XMLStreamException {
        this.fEventWriter.add(entityReference);
    }
    
    public void setIgnoringCharacters(final boolean fIgnoreChars) {
        this.fIgnoreChars = fIgnoreChars;
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        try {
            final int length = xmlAttributes.getLength();
            if (length == 0) {
                final XMLEvent currentEvent = this.fStAXValidatorHelper.getCurrentEvent();
                if (currentEvent != null) {
                    this.fEventWriter.add(currentEvent);
                    return;
                }
            }
            this.fEventWriter.add(this.fEventFactory.createStartElement(qName.prefix, (qName.uri != null) ? qName.uri : "", qName.localpart, this.getAttributeIterator(xmlAttributes, length), this.getNamespaceIterator(), this.fNamespaceContext.getNamespaceContext()));
        }
        catch (final XMLStreamException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.fIgnoreChars) {
            try {
                if (!this.fInCDATA) {
                    this.fEventWriter.add(this.fEventFactory.createCharacters(xmlString.toString()));
                }
                else {
                    this.fEventWriter.add(this.fEventFactory.createCData(xmlString.toString()));
                }
            }
            catch (final XMLStreamException ex) {
                throw new XNIException(ex);
            }
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        this.characters(xmlString, augmentations);
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        try {
            final XMLEvent currentEvent = this.fStAXValidatorHelper.getCurrentEvent();
            if (currentEvent != null) {
                this.fEventWriter.add(currentEvent);
            }
            else {
                this.fEventWriter.add(this.fEventFactory.createEndElement(qName.prefix, qName.uri, qName.localpart, this.getNamespaceIterator()));
            }
        }
        catch (final XMLStreamException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATA = true;
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        this.fInCDATA = false;
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
    }
    
    public void setDocumentSource(final XMLDocumentSource xmlDocumentSource) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
    private Iterator getAttributeIterator(final XMLAttributes xmlAttributes, final int n) {
        return (n > 0) ? new AttributeIterator(xmlAttributes, n) : StAXEventResultBuilder.EMPTY_COLLECTION_ITERATOR;
    }
    
    private Iterator getNamespaceIterator() {
        final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
        return (declaredPrefixCount > 0) ? new NamespaceIterator(declaredPrefixCount) : StAXEventResultBuilder.EMPTY_COLLECTION_ITERATOR;
    }
    
    static {
        EMPTY_COLLECTION_ITERATOR = new Iterator() {
            public boolean hasNext() {
                return false;
            }
            
            public Object next() {
                throw new NoSuchElementException();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    final class AttributeIterator implements Iterator
    {
        XMLAttributes fAttributes;
        int fIndex;
        int fEnd;
        
        AttributeIterator(final XMLAttributes fAttributes, final int fEnd) {
            this.fAttributes = fAttributes;
            this.fIndex = 0;
            this.fEnd = fEnd;
        }
        
        public boolean hasNext() {
            if (this.fIndex < this.fEnd) {
                return true;
            }
            this.fAttributes = null;
            return false;
        }
        
        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.fAttributes.getName(this.fIndex, StAXEventResultBuilder.this.fAttrName);
            return StAXEventResultBuilder.this.fEventFactory.createAttribute(StAXEventResultBuilder.this.fAttrName.prefix, (StAXEventResultBuilder.this.fAttrName.uri != null) ? StAXEventResultBuilder.this.fAttrName.uri : "", StAXEventResultBuilder.this.fAttrName.localpart, this.fAttributes.getValue(this.fIndex++));
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    final class NamespaceIterator implements Iterator
    {
        javax.xml.namespace.NamespaceContext fNC;
        int fIndex;
        int fEnd;
        
        NamespaceIterator(final int fEnd) {
            this.fNC = StAXEventResultBuilder.this.fNamespaceContext.getNamespaceContext();
            this.fIndex = 0;
            this.fEnd = fEnd;
        }
        
        public boolean hasNext() {
            if (this.fIndex < this.fEnd) {
                return true;
            }
            this.fNC = null;
            return false;
        }
        
        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String declaredPrefix = StAXEventResultBuilder.this.fNamespaceContext.getDeclaredPrefixAt(this.fIndex++);
            final String namespaceURI = this.fNC.getNamespaceURI(declaredPrefix);
            if (declaredPrefix.length() == 0) {
                return StAXEventResultBuilder.this.fEventFactory.createNamespace((namespaceURI != null) ? namespaceURI : "");
            }
            return StAXEventResultBuilder.this.fEventFactory.createNamespace(declaredPrefix, (namespaceURI != null) ? namespaceURI : "");
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
