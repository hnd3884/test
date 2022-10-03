package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.util.XMLEventConsumer;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.util.XMLEventAllocator;

public class StAXEventAllocatorBase implements XMLEventAllocator
{
    XMLEventFactory factory;
    
    public StAXEventAllocatorBase() {
        if (System.getProperty("javax.xml.stream.XMLEventFactory") == null) {
            System.setProperty("javax.xml.stream.XMLEventFactory", "com.sun.xml.internal.fastinfoset.stax.factory.StAXEventFactory");
        }
        this.factory = XMLEventFactory.newInstance();
    }
    
    @Override
    public XMLEventAllocator newInstance() {
        return new StAXEventAllocatorBase();
    }
    
    @Override
    public XMLEvent allocate(final XMLStreamReader streamReader) throws XMLStreamException {
        if (streamReader == null) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullReader"));
        }
        return this.getXMLEvent(streamReader);
    }
    
    @Override
    public void allocate(final XMLStreamReader streamReader, final XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(this.getXMLEvent(streamReader));
    }
    
    XMLEvent getXMLEvent(final XMLStreamReader reader) {
        XMLEvent event = null;
        final int eventType = reader.getEventType();
        this.factory.setLocation(reader.getLocation());
        switch (eventType) {
            case 1: {
                final StartElementEvent startElement = (StartElementEvent)this.factory.createStartElement(reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
                this.addAttributes(startElement, reader);
                this.addNamespaces(startElement, reader);
                event = startElement;
                break;
            }
            case 2: {
                final EndElementEvent endElement = (EndElementEvent)this.factory.createEndElement(reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
                this.addNamespaces(endElement, reader);
                event = endElement;
                break;
            }
            case 3: {
                event = this.factory.createProcessingInstruction(reader.getPITarget(), reader.getPIData());
                break;
            }
            case 4: {
                if (reader.isWhiteSpace()) {
                    event = this.factory.createSpace(reader.getText());
                    break;
                }
                event = this.factory.createCharacters(reader.getText());
                break;
            }
            case 5: {
                event = this.factory.createComment(reader.getText());
                break;
            }
            case 7: {
                final StartDocumentEvent docEvent = (StartDocumentEvent)this.factory.createStartDocument(reader.getVersion(), reader.getEncoding(), reader.isStandalone());
                if (reader.getCharacterEncodingScheme() != null) {
                    docEvent.setDeclaredEncoding(true);
                }
                else {
                    docEvent.setDeclaredEncoding(false);
                }
                event = docEvent;
                break;
            }
            case 8: {
                final EndDocumentEvent endDocumentEvent = (EndDocumentEvent)(event = new EndDocumentEvent());
                break;
            }
            case 9: {
                event = this.factory.createEntityReference(reader.getLocalName(), new EntityDeclarationImpl(reader.getLocalName(), reader.getText()));
                break;
            }
            case 10: {
                event = null;
                break;
            }
            case 11: {
                event = this.factory.createDTD(reader.getText());
                break;
            }
            case 12: {
                event = this.factory.createCData(reader.getText());
                break;
            }
            case 6: {
                event = this.factory.createSpace(reader.getText());
                break;
            }
        }
        return event;
    }
    
    protected void addAttributes(final StartElementEvent event, final XMLStreamReader streamReader) {
        AttributeBase attr = null;
        for (int i = 0; i < streamReader.getAttributeCount(); ++i) {
            attr = (AttributeBase)this.factory.createAttribute(streamReader.getAttributeName(i), streamReader.getAttributeValue(i));
            attr.setAttributeType(streamReader.getAttributeType(i));
            attr.setSpecified(streamReader.isAttributeSpecified(i));
            event.addAttribute(attr);
        }
    }
    
    protected void addNamespaces(final StartElementEvent event, final XMLStreamReader streamReader) {
        Namespace namespace = null;
        for (int i = 0; i < streamReader.getNamespaceCount(); ++i) {
            namespace = this.factory.createNamespace(streamReader.getNamespacePrefix(i), streamReader.getNamespaceURI(i));
            event.addNamespace(namespace);
        }
    }
    
    protected void addNamespaces(final EndElementEvent event, final XMLStreamReader streamReader) {
        Namespace namespace = null;
        for (int i = 0; i < streamReader.getNamespaceCount(); ++i) {
            namespace = this.factory.createNamespace(streamReader.getNamespacePrefix(i), streamReader.getNamespaceURI(i));
            event.addNamespace(namespace);
        }
    }
}
