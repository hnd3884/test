package org.apache.xerces.impl.xs.traversers;

import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import org.apache.xerces.util.XMLSymbols;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLStreamReader;
import org.apache.xerces.xni.XNIException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import org.w3c.dom.Document;
import java.util.List;
import org.apache.xerces.util.XMLStringBuffer;
import java.util.ArrayList;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import org.apache.xerces.util.StAXLocationWrapper;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.util.SymbolTable;

final class StAXSchemaParser
{
    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_MASK = 1023;
    private final char[] fCharBuffer;
    private SymbolTable fSymbolTable;
    private SchemaDOMParser fSchemaDOMParser;
    private final StAXLocationWrapper fLocationWrapper;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private final QName fElementQName;
    private final QName fAttributeQName;
    private final XMLAttributesImpl fAttributes;
    private final XMLString fTempString;
    private final ArrayList fDeclaredPrefixes;
    private final XMLStringBuffer fStringBuffer;
    private int fDepth;
    
    public StAXSchemaParser() {
        this.fCharBuffer = new char[1024];
        this.fSymbolTable = new SymbolTable();
        this.fLocationWrapper = new StAXLocationWrapper();
        this.fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable);
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fTempString = new XMLString();
        this.fDeclaredPrefixes = new ArrayList();
        this.fStringBuffer = new XMLStringBuffer();
        this.fNamespaceContext.setDeclaredPrefixes(this.fDeclaredPrefixes);
    }
    
    public void reset(final SchemaDOMParser fSchemaDOMParser, final SymbolTable fSymbolTable) {
        this.fSchemaDOMParser = fSchemaDOMParser;
        this.fSymbolTable = fSymbolTable;
        this.fNamespaceContext.setSymbolTable(this.fSymbolTable);
        this.fNamespaceContext.reset();
    }
    
    public Document getDocument() {
        return this.fSchemaDOMParser.getDocument();
    }
    
    public void parse(final XMLEventReader xmlEventReader) throws XMLStreamException, XNIException {
        final XMLEvent peek = xmlEventReader.peek();
        if (peek != null) {
            final int eventType = peek.getEventType();
            if (eventType != 7 && eventType != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(peek.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
        Label_0474:
            while (xmlEventReader.hasNext()) {
                final XMLEvent nextEvent = xmlEventReader.nextEvent();
                switch (nextEvent.getEventType()) {
                    case 1: {
                        ++this.fDepth;
                        final StartElement startElement = nextEvent.asStartElement();
                        this.fillQName(this.fElementQName, startElement.getName());
                        this.fLocationWrapper.setLocation(startElement.getLocation());
                        this.fNamespaceContext.setNamespaceContext(startElement.getNamespaceContext());
                        this.fillXMLAttributes(startElement);
                        this.fillDeclaredPrefixes(startElement);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        continue;
                    }
                    case 2: {
                        final EndElement endElement = nextEvent.asEndElement();
                        this.fillQName(this.fElementQName, endElement.getName());
                        this.fillDeclaredPrefixes(endElement);
                        this.fLocationWrapper.setLocation(endElement.getLocation());
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth <= 0) {
                            break Label_0474;
                        }
                        continue;
                    }
                    case 4: {
                        this.sendCharactersToSchemaParser(nextEvent.asCharacters().getData(), false);
                        continue;
                    }
                    case 6: {
                        this.sendCharactersToSchemaParser(nextEvent.asCharacters().getData(), true);
                        continue;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.sendCharactersToSchemaParser(nextEvent.asCharacters().getData(), false);
                        this.fSchemaDOMParser.endCDATA(null);
                        continue;
                    }
                    case 3: {
                        final ProcessingInstruction processingInstruction = (ProcessingInstruction)nextEvent;
                        this.fillProcessingInstruction(processingInstruction.getData());
                        this.fSchemaDOMParser.processingInstruction(processingInstruction.getTarget(), this.fTempString, null);
                    }
                    case 11: {}
                    case 9: {}
                    case 5: {
                        continue;
                    }
                    case 7: {
                        ++this.fDepth;
                        continue;
                    }
                }
            }
            this.fLocationWrapper.setLocation(null);
            this.fNamespaceContext.setNamespaceContext(null);
            this.fSchemaDOMParser.endDocument(null);
        }
    }
    
    public void parse(final XMLStreamReader xmlStreamReader) throws XMLStreamException, XNIException {
        if (xmlStreamReader.hasNext()) {
            int n = xmlStreamReader.getEventType();
            if (n != 7 && n != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(xmlStreamReader.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
            int n2 = 1;
        Label_0546:
            while (xmlStreamReader.hasNext()) {
                if (n2 == 0) {
                    n = xmlStreamReader.next();
                }
                else {
                    n2 = 0;
                }
                switch (n) {
                    case 1: {
                        ++this.fDepth;
                        this.fLocationWrapper.setLocation(xmlStreamReader.getLocation());
                        this.fNamespaceContext.setNamespaceContext(xmlStreamReader.getNamespaceContext());
                        this.fillQName(this.fElementQName, xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getPrefix());
                        this.fillXMLAttributes(xmlStreamReader);
                        this.fillDeclaredPrefixes(xmlStreamReader);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        continue;
                    }
                    case 2: {
                        this.fLocationWrapper.setLocation(xmlStreamReader.getLocation());
                        this.fNamespaceContext.setNamespaceContext(xmlStreamReader.getNamespaceContext());
                        this.fillQName(this.fElementQName, xmlStreamReader.getNamespaceURI(), xmlStreamReader.getLocalName(), xmlStreamReader.getPrefix());
                        this.fillDeclaredPrefixes(xmlStreamReader);
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth <= 0) {
                            break Label_0546;
                        }
                        continue;
                    }
                    case 4: {
                        this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        continue;
                    }
                    case 6: {
                        this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                        this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                        continue;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.fTempString.setValues(xmlStreamReader.getTextCharacters(), xmlStreamReader.getTextStart(), xmlStreamReader.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        this.fSchemaDOMParser.endCDATA(null);
                        continue;
                    }
                    case 3: {
                        this.fillProcessingInstruction(xmlStreamReader.getPIData());
                        this.fSchemaDOMParser.processingInstruction(xmlStreamReader.getPITarget(), this.fTempString, null);
                    }
                    case 11: {}
                    case 9: {}
                    case 5: {
                        continue;
                    }
                    case 7: {
                        ++this.fDepth;
                        continue;
                    }
                }
            }
            this.fLocationWrapper.setLocation(null);
            this.fNamespaceContext.setNamespaceContext(null);
            this.fSchemaDOMParser.endDocument(null);
        }
    }
    
    private void sendCharactersToSchemaParser(final String s, final boolean b) {
        if (s != null) {
            final int length = s.length();
            final int n = length & 0x3FF;
            if (n > 0) {
                s.getChars(0, n, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, n);
                if (b) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                }
                else {
                    this.fSchemaDOMParser.characters(this.fTempString, null);
                }
            }
            int i = n;
            while (i < length) {
                s.getChars(i, i += 1024, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                if (b) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                }
                else {
                    this.fSchemaDOMParser.characters(this.fTempString, null);
                }
            }
        }
    }
    
    private void fillProcessingInstruction(final String s) {
        final int length = s.length();
        char[] array = this.fCharBuffer;
        if (array.length < length) {
            array = s.toCharArray();
        }
        else {
            s.getChars(0, length, array, 0);
        }
        this.fTempString.setValues(array, 0, length);
    }
    
    private void fillXMLAttributes(final StartElement startElement) {
        this.fAttributes.removeAllAttributes();
        final Iterator attributes = startElement.getAttributes();
        while (attributes.hasNext()) {
            final Attribute attribute = attributes.next();
            this.fillQName(this.fAttributeQName, attribute.getName());
            final String dtdType = attribute.getDTDType();
            final int length = this.fAttributes.getLength();
            this.fAttributes.addAttributeNS(this.fAttributeQName, (dtdType != null) ? dtdType : XMLSymbols.fCDATASymbol, attribute.getValue());
            this.fAttributes.setSpecified(length, attribute.isSpecified());
        }
    }
    
    private void fillXMLAttributes(final XMLStreamReader xmlStreamReader) {
        this.fAttributes.removeAllAttributes();
        for (int attributeCount = xmlStreamReader.getAttributeCount(), i = 0; i < attributeCount; ++i) {
            this.fillQName(this.fAttributeQName, xmlStreamReader.getAttributeNamespace(i), xmlStreamReader.getAttributeLocalName(i), xmlStreamReader.getAttributePrefix(i));
            final String attributeType = xmlStreamReader.getAttributeType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, (attributeType != null) ? attributeType : XMLSymbols.fCDATASymbol, xmlStreamReader.getAttributeValue(i));
            this.fAttributes.setSpecified(i, xmlStreamReader.isAttributeSpecified(i));
        }
    }
    
    private void addNamespaceDeclarations() {
        final Iterator iterator = this.fDeclaredPrefixes.iterator();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            final String uri = this.fNamespaceContext.getURI(s);
            String s2;
            String prefix_XMLNS;
            String s3;
            if (s.length() > 0) {
                s2 = XMLSymbols.PREFIX_XMLNS;
                prefix_XMLNS = s;
                this.fStringBuffer.clear();
                this.fStringBuffer.append(s2);
                this.fStringBuffer.append(':');
                this.fStringBuffer.append(prefix_XMLNS);
                s3 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            }
            else {
                s2 = XMLSymbols.EMPTY_STRING;
                prefix_XMLNS = XMLSymbols.PREFIX_XMLNS;
                s3 = XMLSymbols.PREFIX_XMLNS;
            }
            this.fAttributeQName.setValues(s2, prefix_XMLNS, s3, NamespaceContext.XMLNS_URI);
            this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, (uri != null) ? uri : XMLSymbols.EMPTY_STRING);
        }
    }
    
    private void fillDeclaredPrefixes(final StartElement startElement) {
        this.fillDeclaredPrefixes(startElement.getNamespaces());
    }
    
    private void fillDeclaredPrefixes(final EndElement endElement) {
        this.fillDeclaredPrefixes(endElement.getNamespaces());
    }
    
    private void fillDeclaredPrefixes(final Iterator iterator) {
        this.fDeclaredPrefixes.clear();
        while (iterator.hasNext()) {
            final String prefix = iterator.next().getPrefix();
            this.fDeclaredPrefixes.add((prefix != null) ? prefix : "");
        }
    }
    
    private void fillDeclaredPrefixes(final XMLStreamReader xmlStreamReader) {
        this.fDeclaredPrefixes.clear();
        for (int namespaceCount = xmlStreamReader.getNamespaceCount(), i = 0; i < namespaceCount; ++i) {
            final String namespacePrefix = xmlStreamReader.getNamespacePrefix(i);
            this.fDeclaredPrefixes.add((namespacePrefix != null) ? namespacePrefix : "");
        }
    }
    
    private void fillQName(final QName qName, final javax.xml.namespace.QName qName2) {
        this.fillQName(qName, qName2.getNamespaceURI(), qName2.getLocalPart(), qName2.getPrefix());
    }
    
    final void fillQName(final QName qName, String s, String s2, String s3) {
        s = ((s != null && s.length() > 0) ? this.fSymbolTable.addSymbol(s) : null);
        s2 = ((s2 != null) ? this.fSymbolTable.addSymbol(s2) : XMLSymbols.EMPTY_STRING);
        s3 = ((s3 != null && s3.length() > 0) ? this.fSymbolTable.addSymbol(s3) : XMLSymbols.EMPTY_STRING);
        String addSymbol = s2;
        if (s3 != XMLSymbols.EMPTY_STRING) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append(s3);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(s2);
            addSymbol = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        }
        qName.setValues(s3, s2, addSymbol, s);
    }
}
