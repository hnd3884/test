package com.sun.xml.internal.stream.events;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.EndElement;
import java.util.Iterator;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.Characters;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;

public class XMLEventFactoryImpl extends XMLEventFactory
{
    Location location;
    
    public XMLEventFactoryImpl() {
        this.location = null;
    }
    
    @Override
    public Attribute createAttribute(final String localName, final String value) {
        final AttributeImpl attr = new AttributeImpl(localName, value);
        if (this.location != null) {
            attr.setLocation(this.location);
        }
        return attr;
    }
    
    @Override
    public Attribute createAttribute(final QName name, final String value) {
        return this.createAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), value);
    }
    
    @Override
    public Attribute createAttribute(final String prefix, final String namespaceURI, final String localName, final String value) {
        final AttributeImpl attr = new AttributeImpl(prefix, namespaceURI, localName, value, null);
        if (this.location != null) {
            attr.setLocation(this.location);
        }
        return attr;
    }
    
    @Override
    public Characters createCData(final String content) {
        final CharacterEvent charEvent = new CharacterEvent(content, true);
        if (this.location != null) {
            charEvent.setLocation(this.location);
        }
        return charEvent;
    }
    
    @Override
    public Characters createCharacters(final String content) {
        final CharacterEvent charEvent = new CharacterEvent(content);
        if (this.location != null) {
            charEvent.setLocation(this.location);
        }
        return charEvent;
    }
    
    @Override
    public Comment createComment(final String text) {
        final CommentEvent charEvent = new CommentEvent(text);
        if (this.location != null) {
            charEvent.setLocation(this.location);
        }
        return charEvent;
    }
    
    @Override
    public DTD createDTD(final String dtd) {
        final DTDEvent dtdEvent = new DTDEvent(dtd);
        if (this.location != null) {
            dtdEvent.setLocation(this.location);
        }
        return dtdEvent;
    }
    
    @Override
    public EndDocument createEndDocument() {
        final EndDocumentEvent event = new EndDocumentEvent();
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public EndElement createEndElement(final QName name, final Iterator namespaces) {
        return this.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart());
    }
    
    @Override
    public EndElement createEndElement(final String prefix, final String namespaceUri, final String localName) {
        final EndElementEvent event = new EndElementEvent(prefix, namespaceUri, localName);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public EndElement createEndElement(final String prefix, final String namespaceUri, final String localName, final Iterator namespaces) {
        final EndElementEvent event = new EndElementEvent(prefix, namespaceUri, localName);
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                event.addNamespace(namespaces.next());
            }
        }
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public EntityReference createEntityReference(final String name, final EntityDeclaration entityDeclaration) {
        final EntityReferenceEvent event = new EntityReferenceEvent(name, entityDeclaration);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Characters createIgnorableSpace(final String content) {
        final CharacterEvent event = new CharacterEvent(content, false, true);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Namespace createNamespace(final String namespaceURI) {
        final NamespaceImpl event = new NamespaceImpl(namespaceURI);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Namespace createNamespace(final String prefix, final String namespaceURI) {
        final NamespaceImpl event = new NamespaceImpl(prefix, namespaceURI);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
        final ProcessingInstructionEvent event = new ProcessingInstructionEvent(target, data);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Characters createSpace(final String content) {
        final CharacterEvent event = new CharacterEvent(content);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartDocument createStartDocument() {
        final StartDocumentEvent event = new StartDocumentEvent();
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartDocument createStartDocument(final String encoding) {
        final StartDocumentEvent event = new StartDocumentEvent(encoding);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartDocument createStartDocument(final String encoding, final String version) {
        final StartDocumentEvent event = new StartDocumentEvent(encoding, version);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartDocument createStartDocument(final String encoding, final String version, final boolean standalone) {
        final StartDocumentEvent event = new StartDocumentEvent(encoding, version, standalone);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartElement createStartElement(final QName name, final Iterator attributes, final Iterator namespaces) {
        return this.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attributes, namespaces);
    }
    
    @Override
    public StartElement createStartElement(final String prefix, final String namespaceUri, final String localName) {
        final StartElementEvent event = new StartElementEvent(prefix, namespaceUri, localName);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public StartElement createStartElement(final String prefix, final String namespaceUri, final String localName, final Iterator attributes, final Iterator namespaces) {
        return this.createStartElement(prefix, namespaceUri, localName, attributes, namespaces, null);
    }
    
    @Override
    public StartElement createStartElement(final String prefix, final String namespaceUri, final String localName, final Iterator attributes, final Iterator namespaces, final NamespaceContext context) {
        final StartElementEvent elem = new StartElementEvent(prefix, namespaceUri, localName);
        elem.addAttributes(attributes);
        elem.addNamespaceAttributes(namespaces);
        elem.setNamespaceContext(context);
        if (this.location != null) {
            elem.setLocation(this.location);
        }
        return elem;
    }
    
    @Override
    public void setLocation(final Location location) {
        this.location = location;
    }
}
