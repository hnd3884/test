package org.apache.xerces.jaxp.validation;

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
import org.apache.xerces.xni.XMLDocumentHandler;

interface StAXDocumentHandler extends XMLDocumentHandler
{
    void setStAXResult(final StAXResult p0);
    
    void startDocument(final XMLStreamReader p0) throws XMLStreamException;
    
    void endDocument(final XMLStreamReader p0) throws XMLStreamException;
    
    void comment(final XMLStreamReader p0) throws XMLStreamException;
    
    void processingInstruction(final XMLStreamReader p0) throws XMLStreamException;
    
    void entityReference(final XMLStreamReader p0) throws XMLStreamException;
    
    void startDocument(final StartDocument p0) throws XMLStreamException;
    
    void endDocument(final EndDocument p0) throws XMLStreamException;
    
    void doctypeDecl(final DTD p0) throws XMLStreamException;
    
    void characters(final Characters p0) throws XMLStreamException;
    
    void cdata(final Characters p0) throws XMLStreamException;
    
    void comment(final Comment p0) throws XMLStreamException;
    
    void processingInstruction(final ProcessingInstruction p0) throws XMLStreamException;
    
    void entityReference(final EntityReference p0) throws XMLStreamException;
    
    void setIgnoringCharacters(final boolean p0);
}
