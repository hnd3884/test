package com.sun.xml.internal.fastinfoset.stax.events;

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
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLEventWriter;

public class StAXEventWriter implements XMLEventWriter
{
    private XMLStreamWriter _streamWriter;
    
    public StAXEventWriter(final XMLStreamWriter streamWriter) {
        this._streamWriter = streamWriter;
    }
    
    @Override
    public void flush() throws XMLStreamException {
        this._streamWriter.flush();
    }
    
    @Override
    public void close() throws XMLStreamException {
        this._streamWriter.close();
    }
    
    @Override
    public void add(final XMLEventReader eventReader) throws XMLStreamException {
        if (eventReader == null) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullEventReader"));
        }
        while (eventReader.hasNext()) {
            this.add(eventReader.nextEvent());
        }
    }
    
    @Override
    public void add(final XMLEvent event) throws XMLStreamException {
        final int type = event.getEventType();
        switch (type) {
            case 11: {
                final DTD dtd = (DTD)event;
                this._streamWriter.writeDTD(dtd.getDocumentTypeDeclaration());
                break;
            }
            case 7: {
                final StartDocument startDocument = (StartDocument)event;
                this._streamWriter.writeStartDocument(startDocument.getCharacterEncodingScheme(), startDocument.getVersion());
                break;
            }
            case 1: {
                final StartElement startElement = event.asStartElement();
                final QName qname = startElement.getName();
                this._streamWriter.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
                final Iterator iterator = startElement.getNamespaces();
                while (iterator.hasNext()) {
                    final Namespace namespace = iterator.next();
                    this._streamWriter.writeNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                final Iterator attributes = startElement.getAttributes();
                while (attributes.hasNext()) {
                    final Attribute attribute = attributes.next();
                    final QName name = attribute.getName();
                    this._streamWriter.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
                }
                break;
            }
            case 13: {
                final Namespace namespace2 = (Namespace)event;
                this._streamWriter.writeNamespace(namespace2.getPrefix(), namespace2.getNamespaceURI());
                break;
            }
            case 5: {
                final Comment comment = (Comment)event;
                this._streamWriter.writeComment(comment.getText());
                break;
            }
            case 3: {
                final ProcessingInstruction processingInstruction = (ProcessingInstruction)event;
                this._streamWriter.writeProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
                break;
            }
            case 4: {
                final Characters characters = event.asCharacters();
                if (characters.isCData()) {
                    this._streamWriter.writeCData(characters.getData());
                    break;
                }
                this._streamWriter.writeCharacters(characters.getData());
                break;
            }
            case 9: {
                final EntityReference entityReference = (EntityReference)event;
                this._streamWriter.writeEntityRef(entityReference.getName());
                break;
            }
            case 10: {
                final Attribute attribute2 = (Attribute)event;
                final QName qname = attribute2.getName();
                this._streamWriter.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), attribute2.getValue());
                break;
            }
            case 12: {
                final Characters characters = (Characters)event;
                if (characters.isCData()) {
                    this._streamWriter.writeCData(characters.getData());
                    break;
                }
                break;
            }
            case 2: {
                this._streamWriter.writeEndElement();
                break;
            }
            case 8: {
                this._streamWriter.writeEndDocument();
                break;
            }
            default: {
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotSupported", new Object[] { Util.getEventTypeString(type) }));
            }
        }
    }
    
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        return this._streamWriter.getPrefix(uri);
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return this._streamWriter.getNamespaceContext();
    }
    
    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException {
        this._streamWriter.setDefaultNamespace(uri);
    }
    
    @Override
    public void setNamespaceContext(final NamespaceContext namespaceContext) throws XMLStreamException {
        this._streamWriter.setNamespaceContext(namespaceContext);
    }
    
    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
        this._streamWriter.setPrefix(prefix, uri);
    }
}
