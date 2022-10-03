package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.stax.events.ProcessingInstructionEvent;
import javax.xml.stream.events.ProcessingInstruction;
import com.sun.xml.internal.fastinfoset.stax.events.DTDEvent;
import javax.xml.stream.events.DTD;
import com.sun.xml.internal.fastinfoset.stax.events.CommentEvent;
import javax.xml.stream.events.Comment;
import com.sun.xml.internal.fastinfoset.stax.events.EntityReferenceEvent;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import com.sun.xml.internal.fastinfoset.stax.events.EndDocumentEvent;
import javax.xml.stream.events.EndDocument;
import com.sun.xml.internal.fastinfoset.stax.events.StartDocumentEvent;
import javax.xml.stream.events.StartDocument;
import com.sun.xml.internal.fastinfoset.stax.events.CharactersEvent;
import javax.xml.stream.events.Characters;
import com.sun.xml.internal.fastinfoset.stax.events.EndElementEvent;
import javax.xml.stream.events.EndElement;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.fastinfoset.stax.events.StartElementEvent;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import com.sun.xml.internal.fastinfoset.stax.events.NamespaceBase;
import javax.xml.stream.events.Namespace;
import javax.xml.namespace.QName;
import com.sun.xml.internal.fastinfoset.stax.events.AttributeBase;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;

public class StAXEventFactory extends XMLEventFactory
{
    Location location;
    
    public StAXEventFactory() {
        this.location = null;
    }
    
    @Override
    public void setLocation(final Location location) {
        this.location = location;
    }
    
    @Override
    public Attribute createAttribute(final String prefix, final String namespaceURI, final String localName, final String value) {
        final AttributeBase attr = new AttributeBase(prefix, namespaceURI, localName, value, null);
        if (this.location != null) {
            attr.setLocation(this.location);
        }
        return attr;
    }
    
    @Override
    public Attribute createAttribute(final String localName, final String value) {
        final AttributeBase attr = new AttributeBase(localName, value);
        if (this.location != null) {
            attr.setLocation(this.location);
        }
        return attr;
    }
    
    @Override
    public Attribute createAttribute(final QName name, final String value) {
        final AttributeBase attr = new AttributeBase(name, value);
        if (this.location != null) {
            attr.setLocation(this.location);
        }
        return attr;
    }
    
    @Override
    public Namespace createNamespace(final String namespaceURI) {
        final NamespaceBase event = new NamespaceBase(namespaceURI);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Namespace createNamespace(final String prefix, final String namespaceURI) {
        final NamespaceBase event = new NamespaceBase(prefix, namespaceURI);
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
        elem.addNamespaces(namespaces);
        elem.setNamespaceContext(context);
        if (this.location != null) {
            elem.setLocation(this.location);
        }
        return elem;
    }
    
    @Override
    public EndElement createEndElement(final QName name, final Iterator namespaces) {
        return this.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), namespaces);
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
    public Characters createCharacters(final String content) {
        final CharactersEvent charEvent = new CharactersEvent(content);
        if (this.location != null) {
            charEvent.setLocation(this.location);
        }
        return charEvent;
    }
    
    @Override
    public Characters createCData(final String content) {
        final CharactersEvent charEvent = new CharactersEvent(content, true);
        if (this.location != null) {
            charEvent.setLocation(this.location);
        }
        return charEvent;
    }
    
    @Override
    public Characters createSpace(final String content) {
        final CharactersEvent event = new CharactersEvent(content);
        event.setSpace(true);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
    
    @Override
    public Characters createIgnorableSpace(final String content) {
        final CharactersEvent event = new CharactersEvent(content, false);
        event.setSpace(true);
        event.setIgnorable(true);
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
        final StartDocumentEvent event = new StartDocumentEvent(encoding, version);
        event.setStandalone(standalone);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
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
    public EntityReference createEntityReference(final String name, final EntityDeclaration entityDeclaration) {
        final EntityReferenceEvent event = new EntityReferenceEvent(name, entityDeclaration);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
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
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
        final ProcessingInstructionEvent event = new ProcessingInstructionEvent(target, data);
        if (this.location != null) {
            event.setLocation(this.location);
        }
        return event;
    }
}
