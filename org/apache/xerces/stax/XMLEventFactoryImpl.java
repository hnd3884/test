package org.apache.xerces.stax;

import org.apache.xerces.stax.events.DTDImpl;
import javax.xml.stream.events.DTD;
import org.apache.xerces.stax.events.ProcessingInstructionImpl;
import javax.xml.stream.events.ProcessingInstruction;
import org.apache.xerces.stax.events.CommentImpl;
import javax.xml.stream.events.Comment;
import org.apache.xerces.stax.events.EntityReferenceImpl;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.EntityDeclaration;
import org.apache.xerces.stax.events.EndDocumentImpl;
import javax.xml.stream.events.EndDocument;
import org.apache.xerces.stax.events.StartDocumentImpl;
import javax.xml.stream.events.StartDocument;
import org.apache.xerces.stax.events.CharactersImpl;
import javax.xml.stream.events.Characters;
import org.apache.xerces.stax.events.EndElementImpl;
import javax.xml.stream.events.EndElement;
import org.apache.xerces.stax.events.StartElementImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;
import org.apache.xerces.stax.events.NamespaceImpl;
import javax.xml.stream.events.Namespace;
import org.apache.xerces.stax.events.AttributeImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;

public final class XMLEventFactoryImpl extends XMLEventFactory
{
    private Location fLocation;
    
    public void setLocation(final Location fLocation) {
        this.fLocation = fLocation;
    }
    
    public Attribute createAttribute(final String s, final String s2, final String s3, final String s4) {
        return this.createAttribute(new QName(s2, s3, s), s4);
    }
    
    public Attribute createAttribute(final String s, final String s2) {
        return this.createAttribute(new QName(s), s2);
    }
    
    public Attribute createAttribute(final QName qName, final String s) {
        return new AttributeImpl(qName, s, "CDATA", true, this.fLocation);
    }
    
    public Namespace createNamespace(final String s) {
        return this.createNamespace("", s);
    }
    
    public Namespace createNamespace(final String s, final String s2) {
        return new NamespaceImpl(s, s2, this.fLocation);
    }
    
    public StartElement createStartElement(final QName qName, final Iterator iterator, final Iterator iterator2) {
        return this.createStartElement(qName, iterator, iterator2, null);
    }
    
    public StartElement createStartElement(final String s, final String s2, final String s3) {
        return this.createStartElement(new QName(s2, s3, s), null, null);
    }
    
    public StartElement createStartElement(final String s, final String s2, final String s3, final Iterator iterator, final Iterator iterator2) {
        return this.createStartElement(new QName(s2, s3, s), iterator, iterator2);
    }
    
    public StartElement createStartElement(final String s, final String s2, final String s3, final Iterator iterator, final Iterator iterator2, final NamespaceContext namespaceContext) {
        return this.createStartElement(new QName(s2, s3, s), iterator, iterator2, namespaceContext);
    }
    
    private StartElement createStartElement(final QName qName, final Iterator iterator, final Iterator iterator2, final NamespaceContext namespaceContext) {
        return new StartElementImpl(qName, iterator, iterator2, namespaceContext, this.fLocation);
    }
    
    public EndElement createEndElement(final QName qName, final Iterator iterator) {
        return new EndElementImpl(qName, iterator, this.fLocation);
    }
    
    public EndElement createEndElement(final String s, final String s2, final String s3) {
        return this.createEndElement(new QName(s2, s3, s), null);
    }
    
    public EndElement createEndElement(final String s, final String s2, final String s3, final Iterator iterator) {
        return this.createEndElement(new QName(s2, s3, s), iterator);
    }
    
    public Characters createCharacters(final String s) {
        return new CharactersImpl(s, 4, this.fLocation);
    }
    
    public Characters createCData(final String s) {
        return new CharactersImpl(s, 12, this.fLocation);
    }
    
    public Characters createSpace(final String s) {
        return this.createCharacters(s);
    }
    
    public Characters createIgnorableSpace(final String s) {
        return new CharactersImpl(s, 6, this.fLocation);
    }
    
    public StartDocument createStartDocument() {
        return this.createStartDocument(null, null);
    }
    
    public StartDocument createStartDocument(final String s, final String s2, final boolean b) {
        return new StartDocumentImpl(s, s != null, b, true, s2, this.fLocation);
    }
    
    public StartDocument createStartDocument(final String s, final String s2) {
        return new StartDocumentImpl(s, s != null, false, false, s2, this.fLocation);
    }
    
    public StartDocument createStartDocument(final String s) {
        return this.createStartDocument(s, null);
    }
    
    public EndDocument createEndDocument() {
        return new EndDocumentImpl(this.fLocation);
    }
    
    public EntityReference createEntityReference(final String s, final EntityDeclaration entityDeclaration) {
        return new EntityReferenceImpl(s, entityDeclaration, this.fLocation);
    }
    
    public Comment createComment(final String s) {
        return new CommentImpl(s, this.fLocation);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String s, final String s2) {
        return new ProcessingInstructionImpl(s, s2, this.fLocation);
    }
    
    public DTD createDTD(final String s) {
        return new DTDImpl(s, this.fLocation);
    }
}
