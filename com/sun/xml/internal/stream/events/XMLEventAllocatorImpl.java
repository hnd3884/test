package com.sun.xml.internal.stream.events;

import javax.xml.namespace.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import javax.xml.stream.events.Namespace;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.util.List;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;

public class XMLEventAllocatorImpl implements XMLEventAllocator
{
    @Override
    public XMLEvent allocate(final XMLStreamReader xMLStreamReader) throws XMLStreamException {
        if (xMLStreamReader == null) {
            throw new XMLStreamException("Reader cannot be null");
        }
        return this.getXMLEvent(xMLStreamReader);
    }
    
    @Override
    public void allocate(final XMLStreamReader xMLStreamReader, final XMLEventConsumer xMLEventConsumer) throws XMLStreamException {
        final XMLEvent currentEvent = this.getXMLEvent(xMLStreamReader);
        if (currentEvent != null) {
            xMLEventConsumer.add(currentEvent);
        }
    }
    
    @Override
    public XMLEventAllocator newInstance() {
        return new XMLEventAllocatorImpl();
    }
    
    XMLEvent getXMLEvent(final XMLStreamReader streamReader) {
        XMLEvent event = null;
        final int eventType = streamReader.getEventType();
        switch (eventType) {
            case 1: {
                final StartElementEvent startElementEvent = new StartElementEvent(this.getQName(streamReader));
                this.fillAttributes(startElementEvent, streamReader);
                if (streamReader.getProperty("javax.xml.stream.isNamespaceAware")) {
                    this.fillNamespaceAttributes(startElementEvent, streamReader);
                    this.setNamespaceContext(startElementEvent, streamReader);
                }
                startElementEvent.setLocation(streamReader.getLocation());
                event = startElementEvent;
                break;
            }
            case 2: {
                final EndElementEvent endElementEvent = new EndElementEvent(this.getQName(streamReader));
                endElementEvent.setLocation(streamReader.getLocation());
                if (streamReader.getProperty("javax.xml.stream.isNamespaceAware")) {
                    this.fillNamespaceAttributes(endElementEvent, streamReader);
                }
                event = endElementEvent;
                break;
            }
            case 3: {
                final ProcessingInstructionEvent piEvent = new ProcessingInstructionEvent(streamReader.getPITarget(), streamReader.getPIData());
                piEvent.setLocation(streamReader.getLocation());
                event = piEvent;
                break;
            }
            case 4: {
                final CharacterEvent cDataEvent = new CharacterEvent(streamReader.getText());
                cDataEvent.setLocation(streamReader.getLocation());
                event = cDataEvent;
                break;
            }
            case 5: {
                final CommentEvent commentEvent = new CommentEvent(streamReader.getText());
                commentEvent.setLocation(streamReader.getLocation());
                event = commentEvent;
                break;
            }
            case 7: {
                final StartDocumentEvent sdEvent = new StartDocumentEvent();
                sdEvent.setVersion(streamReader.getVersion());
                sdEvent.setEncoding(streamReader.getEncoding());
                if (streamReader.getCharacterEncodingScheme() != null) {
                    sdEvent.setDeclaredEncoding(true);
                }
                else {
                    sdEvent.setDeclaredEncoding(false);
                }
                sdEvent.setStandalone(streamReader.isStandalone());
                sdEvent.setLocation(streamReader.getLocation());
                event = sdEvent;
                break;
            }
            case 8: {
                final EndDocumentEvent endDocumentEvent = new EndDocumentEvent();
                endDocumentEvent.setLocation(streamReader.getLocation());
                event = endDocumentEvent;
                break;
            }
            case 9: {
                final EntityReferenceEvent entityEvent = new EntityReferenceEvent(streamReader.getLocalName(), new EntityDeclarationImpl(streamReader.getLocalName(), streamReader.getText()));
                entityEvent.setLocation(streamReader.getLocation());
                event = entityEvent;
                break;
            }
            case 10: {
                event = null;
                break;
            }
            case 11: {
                final DTDEvent dtdEvent = new DTDEvent(streamReader.getText());
                dtdEvent.setLocation(streamReader.getLocation());
                final List entities = (List)streamReader.getProperty("javax.xml.stream.entities");
                if (entities != null && entities.size() != 0) {
                    dtdEvent.setEntities(entities);
                }
                final List notations = (List)streamReader.getProperty("javax.xml.stream.notations");
                if (notations != null && notations.size() != 0) {
                    dtdEvent.setNotations(notations);
                }
                event = dtdEvent;
                break;
            }
            case 12: {
                final CharacterEvent cDataEvent = new CharacterEvent(streamReader.getText(), true);
                cDataEvent.setLocation(streamReader.getLocation());
                event = cDataEvent;
                break;
            }
            case 6: {
                final CharacterEvent spaceEvent = new CharacterEvent(streamReader.getText(), false, true);
                spaceEvent.setLocation(streamReader.getLocation());
                event = spaceEvent;
                break;
            }
        }
        return event;
    }
    
    protected XMLEvent getNextEvent(final XMLStreamReader streamReader) throws XMLStreamException {
        streamReader.next();
        return this.getXMLEvent(streamReader);
    }
    
    protected void fillAttributes(final StartElementEvent event, final XMLStreamReader xmlr) {
        final int len = xmlr.getAttributeCount();
        QName qname = null;
        AttributeImpl attr = null;
        final NamespaceImpl nattr = null;
        for (int i = 0; i < len; ++i) {
            qname = xmlr.getAttributeName(i);
            attr = new AttributeImpl();
            attr.setName(qname);
            attr.setAttributeType(xmlr.getAttributeType(i));
            attr.setSpecified(xmlr.isAttributeSpecified(i));
            attr.setValue(xmlr.getAttributeValue(i));
            event.addAttribute(attr);
        }
    }
    
    protected void fillNamespaceAttributes(final StartElementEvent event, final XMLStreamReader xmlr) {
        final int count = xmlr.getNamespaceCount();
        String uri = null;
        String prefix = null;
        NamespaceImpl attr = null;
        for (int i = 0; i < count; ++i) {
            uri = xmlr.getNamespaceURI(i);
            prefix = xmlr.getNamespacePrefix(i);
            if (prefix == null) {
                prefix = "";
            }
            attr = new NamespaceImpl(prefix, uri);
            event.addNamespaceAttribute(attr);
        }
    }
    
    protected void fillNamespaceAttributes(final EndElementEvent event, final XMLStreamReader xmlr) {
        final int count = xmlr.getNamespaceCount();
        String uri = null;
        String prefix = null;
        NamespaceImpl attr = null;
        for (int i = 0; i < count; ++i) {
            uri = xmlr.getNamespaceURI(i);
            prefix = xmlr.getNamespacePrefix(i);
            if (prefix == null) {
                prefix = "";
            }
            attr = new NamespaceImpl(prefix, uri);
            event.addNamespace(attr);
        }
    }
    
    private void setNamespaceContext(final StartElementEvent event, final XMLStreamReader xmlr) {
        final NamespaceContextWrapper contextWrapper = (NamespaceContextWrapper)xmlr.getNamespaceContext();
        final NamespaceSupport ns = new NamespaceSupport(contextWrapper.getNamespaceContext());
        event.setNamespaceContext(new NamespaceContextWrapper(ns));
    }
    
    private QName getQName(final XMLStreamReader xmlr) {
        return new QName(xmlr.getNamespaceURI(), xmlr.getLocalName(), xmlr.getPrefix());
    }
}
