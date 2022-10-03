package com.sun.xml.internal.stream.writers;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;

public class XMLEventWriterImpl implements XMLEventWriter
{
    private XMLStreamWriter fStreamWriter;
    private static final boolean DEBUG = false;
    
    public XMLEventWriterImpl(final XMLStreamWriter streamWriter) {
        this.fStreamWriter = streamWriter;
    }
    
    @Override
    public void add(final XMLEventReader xMLEventReader) throws XMLStreamException {
        if (xMLEventReader == null) {
            throw new XMLStreamException("Event reader shouldn't be null");
        }
        while (xMLEventReader.hasNext()) {
            this.add(xMLEventReader.nextEvent());
        }
    }
    
    @Override
    public void add(final XMLEvent xMLEvent) throws XMLStreamException {
        final int type = xMLEvent.getEventType();
        switch (type) {
            case 11: {
                final DTD dtd = (DTD)xMLEvent;
                this.fStreamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
                break;
            }
            case 7: {
                final StartDocument startDocument = (StartDocument)xMLEvent;
                try {
                    this.fStreamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                }
                catch (final XMLStreamException e) {
                    this.fStreamWriter.writeStartDocument(startDocument.getVersion());
                }
                break;
            }
            case 1: {
                final StartElement startElement = xMLEvent.asStartElement();
                final QName qname = startElement.getName();
                this.fStreamWriter.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
                final Iterator iterator = startElement.getNamespaces();
                while (iterator.hasNext()) {
                    final Namespace namespace = iterator.next();
                    this.fStreamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                final Iterator attributes = startElement.getAttributes();
                while (attributes.hasNext()) {
                    final Attribute attribute = attributes.next();
                    final QName aqname = attribute.getName();
                    this.fStreamWriter.writeAttribute(aqname.getPrefix(), aqname.getNamespaceURI(), aqname.getLocalPart(), attribute.getValue());
                }
                break;
            }
            case 13: {
                final Namespace namespace2 = (Namespace)xMLEvent;
                this.fStreamWriter.writeNamespace(namespace2.getPrefix(), namespace2.getNamespaceURI());
                break;
            }
            case 5: {
                final Comment comment = (Comment)xMLEvent;
                this.fStreamWriter.writeComment(comment.getText());
                break;
            }
            case 3: {
                final ProcessingInstruction processingInstruction = (ProcessingInstruction)xMLEvent;
                this.fStreamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            case 4: {
                final Characters characters = xMLEvent.asCharacters();
                if (characters.isCData()) {
                    this.fStreamWriter.writeCData(characters.getData());
                    break;
                }
                this.fStreamWriter.writeCharacters(characters.getData());
                break;
            }
            case 9: {
                final EntityReference entityReference = (EntityReference)xMLEvent;
                this.fStreamWriter.writeEntityRef(entityReference.getName());
                break;
            }
            case 10: {
                final Attribute attribute2 = (Attribute)xMLEvent;
                final QName qname = attribute2.getName();
                this.fStreamWriter.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), attribute2.getValue());
                break;
            }
            case 12: {
                final Characters characters = (Characters)xMLEvent;
                if (characters.isCData()) {
                    this.fStreamWriter.writeCData(characters.getData());
                    break;
                }
                break;
            }
            case 2: {
                this.fStreamWriter.writeEndElement();
                break;
            }
            case 8: {
                this.fStreamWriter.writeEndDocument();
                break;
            }
        }
    }
    
    @Override
    public void close() throws XMLStreamException {
        this.fStreamWriter.close();
    }
    
    @Override
    public void flush() throws XMLStreamException {
        this.fStreamWriter.flush();
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this.fStreamWriter.getNamespaceContext();
    }
    
    @Override
    public String getPrefix(final String namespaceURI) throws XMLStreamException {
        return this.fStreamWriter.getPrefix(namespaceURI);
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this.fStreamWriter.setDefaultNamespace(uri);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this.fStreamWriter.setNamespaceContext(namespaceContext);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this.fStreamWriter.setPrefix(prefix, uri);
    }
}
