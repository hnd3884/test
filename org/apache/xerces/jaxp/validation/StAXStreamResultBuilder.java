package org.apache.xerces.jaxp.validation;

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
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXResult;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import javax.xml.stream.XMLStreamWriter;

final class StAXStreamResultBuilder implements StAXDocumentHandler
{
    private XMLStreamWriter fStreamWriter;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private boolean fIgnoreChars;
    private boolean fInCDATA;
    private final QName fAttrName;
    
    public StAXStreamResultBuilder(final JAXPNamespaceContextWrapper fNamespaceContext) {
        this.fAttrName = new QName();
        this.fNamespaceContext = fNamespaceContext;
    }
    
    public void setStAXResult(final StAXResult stAXResult) {
        this.fIgnoreChars = false;
        this.fInCDATA = false;
        this.fAttrName.clear();
        this.fStreamWriter = ((stAXResult != null) ? stAXResult.getXMLStreamWriter() : null);
    }
    
    public void startDocument(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final String version = xmlStreamReader.getVersion();
        final String characterEncodingScheme = xmlStreamReader.getCharacterEncodingScheme();
        this.fStreamWriter.writeStartDocument((characterEncodingScheme != null) ? characterEncodingScheme : "UTF-8", (version != null) ? version : "1.0");
    }
    
    public void endDocument(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        this.fStreamWriter.writeEndDocument();
        this.fStreamWriter.flush();
    }
    
    public void comment(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        this.fStreamWriter.writeComment(xmlStreamReader.getText());
    }
    
    public void processingInstruction(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        final String piData = xmlStreamReader.getPIData();
        if (piData != null && piData.length() > 0) {
            this.fStreamWriter.writeProcessingInstruction(xmlStreamReader.getPITarget(), piData);
        }
        else {
            this.fStreamWriter.writeProcessingInstruction(xmlStreamReader.getPITarget());
        }
    }
    
    public void entityReference(final XMLStreamReader xmlStreamReader) throws XMLStreamException {
        this.fStreamWriter.writeEntityRef(xmlStreamReader.getLocalName());
    }
    
    public void startDocument(final StartDocument startDocument) throws XMLStreamException {
        final String version = startDocument.getVersion();
        final String characterEncodingScheme = startDocument.getCharacterEncodingScheme();
        this.fStreamWriter.writeStartDocument((characterEncodingScheme != null) ? characterEncodingScheme : "UTF-8", (version != null) ? version : "1.0");
    }
    
    public void endDocument(final EndDocument endDocument) throws XMLStreamException {
        this.fStreamWriter.writeEndDocument();
        this.fStreamWriter.flush();
    }
    
    public void doctypeDecl(final DTD dtd) throws XMLStreamException {
        this.fStreamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
    }
    
    public void characters(final Characters characters) throws XMLStreamException {
        this.fStreamWriter.writeCharacters(characters.getData());
    }
    
    public void cdata(final Characters characters) throws XMLStreamException {
        this.fStreamWriter.writeCData(characters.getData());
    }
    
    public void comment(final Comment comment) throws XMLStreamException {
        this.fStreamWriter.writeComment(comment.getText());
    }
    
    public void processingInstruction(final ProcessingInstruction processingInstruction) throws XMLStreamException {
        final String data = processingInstruction.getData();
        if (data != null && data.length() > 0) {
            this.fStreamWriter.writeProcessingInstruction(processingInstruction.getTarget(), data);
        }
        else {
            this.fStreamWriter.writeProcessingInstruction(processingInstruction.getTarget());
        }
    }
    
    public void entityReference(final EntityReference entityReference) throws XMLStreamException {
        this.fStreamWriter.writeEntityRef(entityReference.getName());
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
            if (qName.prefix.length() > 0) {
                this.fStreamWriter.writeStartElement(qName.prefix, qName.localpart, (qName.uri != null) ? qName.uri : "");
            }
            else if (qName.uri != null) {
                this.fStreamWriter.writeStartElement(qName.uri, qName.localpart);
            }
            else {
                this.fStreamWriter.writeStartElement(qName.localpart);
            }
            final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
            final javax.xml.namespace.NamespaceContext namespaceContext = this.fNamespaceContext.getNamespaceContext();
            for (int i = 0; i < declaredPrefixCount; ++i) {
                final String declaredPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                final String namespaceURI = namespaceContext.getNamespaceURI(declaredPrefix);
                if (declaredPrefix.length() == 0) {
                    this.fStreamWriter.writeDefaultNamespace((namespaceURI != null) ? namespaceURI : "");
                }
                else {
                    this.fStreamWriter.writeNamespace(declaredPrefix, (namespaceURI != null) ? namespaceURI : "");
                }
            }
            for (int length = xmlAttributes.getLength(), j = 0; j < length; ++j) {
                xmlAttributes.getName(j, this.fAttrName);
                if (this.fAttrName.prefix.length() > 0) {
                    this.fStreamWriter.writeAttribute(this.fAttrName.prefix, (this.fAttrName.uri != null) ? this.fAttrName.uri : "", this.fAttrName.localpart, xmlAttributes.getValue(j));
                }
                else if (this.fAttrName.uri != null) {
                    this.fStreamWriter.writeAttribute(this.fAttrName.uri, this.fAttrName.localpart, xmlAttributes.getValue(j));
                }
                else {
                    this.fStreamWriter.writeAttribute(this.fAttrName.localpart, xmlAttributes.getValue(j));
                }
            }
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
                    this.fStreamWriter.writeCharacters(xmlString.ch, xmlString.offset, xmlString.length);
                }
                else {
                    this.fStreamWriter.writeCData(xmlString.toString());
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
            this.fStreamWriter.writeEndElement();
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
}
