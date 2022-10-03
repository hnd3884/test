package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import javax.xml.stream.events.Namespace;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.XMLStreamReader;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import org.w3c.dom.Document;
import java.util.List;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.JAXPNamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

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
    
    public void reset(final SchemaDOMParser schemaDOMParser, final SymbolTable symbolTable) {
        this.fSchemaDOMParser = schemaDOMParser;
        this.fSymbolTable = symbolTable;
        this.fNamespaceContext.setSymbolTable(this.fSymbolTable);
        this.fNamespaceContext.reset();
    }
    
    public Document getDocument() {
        return this.fSchemaDOMParser.getDocument();
    }
    
    public void parse(final XMLEventReader input) throws XMLStreamException, XNIException {
        XMLEvent currentEvent = input.peek();
        if (currentEvent != null) {
            int eventType = currentEvent.getEventType();
            if (eventType != 7 && eventType != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(currentEvent.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
        Label_0474:
            while (input.hasNext()) {
                currentEvent = input.nextEvent();
                eventType = currentEvent.getEventType();
                switch (eventType) {
                    case 1: {
                        ++this.fDepth;
                        final StartElement start = currentEvent.asStartElement();
                        this.fillQName(this.fElementQName, start.getName());
                        this.fLocationWrapper.setLocation(start.getLocation());
                        this.fNamespaceContext.setNamespaceContext(start.getNamespaceContext());
                        this.fillXMLAttributes(start);
                        this.fillDeclaredPrefixes(start);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        continue;
                    }
                    case 2: {
                        final EndElement end = currentEvent.asEndElement();
                        this.fillQName(this.fElementQName, end.getName());
                        this.fillDeclaredPrefixes(end);
                        this.fLocationWrapper.setLocation(end.getLocation());
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth <= 0) {
                            break Label_0474;
                        }
                        continue;
                    }
                    case 4: {
                        this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), false);
                        continue;
                    }
                    case 6: {
                        this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), true);
                        continue;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.sendCharactersToSchemaParser(currentEvent.asCharacters().getData(), false);
                        this.fSchemaDOMParser.endCDATA(null);
                        continue;
                    }
                    case 3: {
                        final ProcessingInstruction pi = (ProcessingInstruction)currentEvent;
                        this.fillProcessingInstruction(pi.getData());
                        this.fSchemaDOMParser.processingInstruction(pi.getTarget(), this.fTempString, null);
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
    
    public void parse(final XMLStreamReader input) throws XMLStreamException, XNIException {
        if (input.hasNext()) {
            int eventType = input.getEventType();
            if (eventType != 7 && eventType != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(input.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
            boolean first = true;
        Label_0546:
            while (input.hasNext()) {
                if (!first) {
                    eventType = input.next();
                }
                else {
                    first = false;
                }
                switch (eventType) {
                    case 1: {
                        ++this.fDepth;
                        this.fLocationWrapper.setLocation(input.getLocation());
                        this.fNamespaceContext.setNamespaceContext(input.getNamespaceContext());
                        this.fillQName(this.fElementQName, input.getNamespaceURI(), input.getLocalName(), input.getPrefix());
                        this.fillXMLAttributes(input);
                        this.fillDeclaredPrefixes(input);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        continue;
                    }
                    case 2: {
                        this.fLocationWrapper.setLocation(input.getLocation());
                        this.fNamespaceContext.setNamespaceContext(input.getNamespaceContext());
                        this.fillQName(this.fElementQName, input.getNamespaceURI(), input.getLocalName(), input.getPrefix());
                        this.fillDeclaredPrefixes(input);
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth <= 0) {
                            break Label_0546;
                        }
                        continue;
                    }
                    case 4: {
                        this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        continue;
                    }
                    case 6: {
                        this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
                        this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                        continue;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.fTempString.setValues(input.getTextCharacters(), input.getTextStart(), input.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        this.fSchemaDOMParser.endCDATA(null);
                        continue;
                    }
                    case 3: {
                        this.fillProcessingInstruction(input.getPIData());
                        this.fSchemaDOMParser.processingInstruction(input.getPITarget(), this.fTempString, null);
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
    
    private void sendCharactersToSchemaParser(final String str, final boolean whitespace) {
        if (str != null) {
            final int length = str.length();
            final int remainder = length & 0x3FF;
            if (remainder > 0) {
                str.getChars(0, remainder, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, remainder);
                if (whitespace) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                }
                else {
                    this.fSchemaDOMParser.characters(this.fTempString, null);
                }
            }
            int i = remainder;
            while (i < length) {
                final int n = i;
                i += 1024;
                str.getChars(n, i, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                if (whitespace) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                }
                else {
                    this.fSchemaDOMParser.characters(this.fTempString, null);
                }
            }
        }
    }
    
    private void fillProcessingInstruction(final String data) {
        final int dataLength = data.length();
        char[] charBuffer = this.fCharBuffer;
        if (charBuffer.length < dataLength) {
            charBuffer = data.toCharArray();
        }
        else {
            data.getChars(0, dataLength, charBuffer, 0);
        }
        this.fTempString.setValues(charBuffer, 0, dataLength);
    }
    
    private void fillXMLAttributes(final StartElement event) {
        this.fAttributes.removeAllAttributes();
        final Iterator attrs = event.getAttributes();
        while (attrs.hasNext()) {
            final Attribute attr = attrs.next();
            this.fillQName(this.fAttributeQName, attr.getName());
            final String type = attr.getDTDType();
            final int idx = this.fAttributes.getLength();
            this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, attr.getValue());
            this.fAttributes.setSpecified(idx, attr.isSpecified());
        }
    }
    
    private void fillXMLAttributes(final XMLStreamReader input) {
        this.fAttributes.removeAllAttributes();
        for (int len = input.getAttributeCount(), i = 0; i < len; ++i) {
            this.fillQName(this.fAttributeQName, input.getAttributeNamespace(i), input.getAttributeLocalName(i), input.getAttributePrefix(i));
            final String type = input.getAttributeType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, input.getAttributeValue(i));
            this.fAttributes.setSpecified(i, input.isAttributeSpecified(i));
        }
    }
    
    private void addNamespaceDeclarations() {
        String prefix = null;
        String localpart = null;
        String rawname = null;
        String nsPrefix = null;
        String nsURI = null;
        final Iterator iter = this.fDeclaredPrefixes.iterator();
        while (iter.hasNext()) {
            nsPrefix = iter.next();
            nsURI = this.fNamespaceContext.getURI(nsPrefix);
            if (nsPrefix.length() > 0) {
                prefix = XMLSymbols.PREFIX_XMLNS;
                localpart = nsPrefix;
                this.fStringBuffer.clear();
                this.fStringBuffer.append(prefix);
                this.fStringBuffer.append(':');
                this.fStringBuffer.append(localpart);
                rawname = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            }
            else {
                prefix = XMLSymbols.EMPTY_STRING;
                localpart = XMLSymbols.PREFIX_XMLNS;
                rawname = XMLSymbols.PREFIX_XMLNS;
            }
            this.fAttributeQName.setValues(prefix, localpart, rawname, NamespaceContext.XMLNS_URI);
            this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, (nsURI != null) ? nsURI : XMLSymbols.EMPTY_STRING);
        }
    }
    
    private void fillDeclaredPrefixes(final StartElement event) {
        this.fillDeclaredPrefixes(event.getNamespaces());
    }
    
    private void fillDeclaredPrefixes(final EndElement event) {
        this.fillDeclaredPrefixes(event.getNamespaces());
    }
    
    private void fillDeclaredPrefixes(final Iterator namespaces) {
        this.fDeclaredPrefixes.clear();
        while (namespaces.hasNext()) {
            final Namespace ns = namespaces.next();
            final String prefix = ns.getPrefix();
            this.fDeclaredPrefixes.add((prefix != null) ? prefix : "");
        }
    }
    
    private void fillDeclaredPrefixes(final XMLStreamReader reader) {
        this.fDeclaredPrefixes.clear();
        for (int len = reader.getNamespaceCount(), i = 0; i < len; ++i) {
            final String prefix = reader.getNamespacePrefix(i);
            this.fDeclaredPrefixes.add((prefix != null) ? prefix : "");
        }
    }
    
    private void fillQName(final QName toFill, final javax.xml.namespace.QName toCopy) {
        this.fillQName(toFill, toCopy.getNamespaceURI(), toCopy.getLocalPart(), toCopy.getPrefix());
    }
    
    final void fillQName(final QName toFill, String uri, String localpart, String prefix) {
        uri = ((uri != null && uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null);
        localpart = ((localpart != null) ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING);
        prefix = ((prefix != null && prefix.length() > 0) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
        String raw = localpart;
        if (prefix != XMLSymbols.EMPTY_STRING) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append(prefix);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(localpart);
            raw = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        }
        toFill.setValues(prefix, localpart, raw, uri);
    }
}
