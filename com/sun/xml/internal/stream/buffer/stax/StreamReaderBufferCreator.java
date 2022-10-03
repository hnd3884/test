package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import java.util.HashMap;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.util.Map;

public class StreamReaderBufferCreator extends StreamBufferCreator
{
    private int _eventType;
    private boolean _storeInScopeNamespacesOnElementFragment;
    private Map<String, Integer> _inScopePrefixes;
    
    public StreamReaderBufferCreator() {
    }
    
    public StreamReaderBufferCreator(final MutableXMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }
    
    public MutableXMLStreamBuffer create(final XMLStreamReader reader) throws XMLStreamException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        this.store(reader);
        return this.getXMLStreamBuffer();
    }
    
    public MutableXMLStreamBuffer createElementFragment(final XMLStreamReader reader, final boolean storeInScopeNamespaces) throws XMLStreamException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        if (!reader.hasNext()) {
            return this._buffer;
        }
        this._storeInScopeNamespacesOnElementFragment = storeInScopeNamespaces;
        this._eventType = reader.getEventType();
        if (this._eventType != 1) {
            do {
                this._eventType = reader.next();
            } while (this._eventType != 1 && this._eventType != 8);
        }
        if (storeInScopeNamespaces) {
            this._inScopePrefixes = new HashMap<String, Integer>();
        }
        this.storeElementAndChildren(reader);
        return this.getXMLStreamBuffer();
    }
    
    private void store(final XMLStreamReader reader) throws XMLStreamException {
        if (!reader.hasNext()) {
            return;
        }
        switch (this._eventType = reader.getEventType()) {
            case 7: {
                this.storeDocumentAndChildren(reader);
                break;
            }
            case 1: {
                this.storeElementAndChildren(reader);
                break;
            }
            default: {
                throw new XMLStreamException("XMLStreamReader not positioned at a document or element");
            }
        }
        this.increaseTreeCount();
    }
    
    private void storeDocumentAndChildren(final XMLStreamReader reader) throws XMLStreamException {
        this.storeStructure(16);
        this._eventType = reader.next();
        while (this._eventType != 8) {
            switch (this._eventType) {
                case 1: {
                    this.storeElementAndChildren(reader);
                    continue;
                }
                case 5: {
                    this.storeComment(reader);
                    break;
                }
                case 3: {
                    this.storeProcessingInstruction(reader);
                    break;
                }
            }
            this._eventType = reader.next();
        }
        this.storeStructure(144);
    }
    
    private void storeElementAndChildren(final XMLStreamReader reader) throws XMLStreamException {
        if (reader instanceof XMLStreamReaderEx) {
            this.storeElementAndChildrenEx((XMLStreamReaderEx)reader);
        }
        else {
            this.storeElementAndChildrenNoEx(reader);
        }
    }
    
    private void storeElementAndChildrenEx(final XMLStreamReaderEx reader) throws XMLStreamException {
        int depth = 1;
        if (this._storeInScopeNamespacesOnElementFragment) {
            this.storeElementWithInScopeNamespaces(reader);
        }
        else {
            this.storeElement(reader);
        }
        while (depth > 0) {
            switch (this._eventType = reader.next()) {
                case 1: {
                    ++depth;
                    this.storeElement(reader);
                    continue;
                }
                case 2: {
                    --depth;
                    this.storeStructure(144);
                    continue;
                }
                case 13: {
                    this.storeNamespaceAttributes(reader);
                    continue;
                }
                case 10: {
                    this.storeAttributes(reader);
                    continue;
                }
                case 4:
                case 6:
                case 12: {
                    final CharSequence c = reader.getPCDATA();
                    if (c instanceof Base64Data) {
                        this.storeStructure(92);
                        this.storeContentObject(c);
                        continue;
                    }
                    this.storeContentCharacters(80, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                    continue;
                }
                case 5: {
                    this.storeComment(reader);
                    continue;
                }
                case 3: {
                    this.storeProcessingInstruction(reader);
                    continue;
                }
            }
        }
        this._eventType = reader.next();
    }
    
    private void storeElementAndChildrenNoEx(final XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        if (this._storeInScopeNamespacesOnElementFragment) {
            this.storeElementWithInScopeNamespaces(reader);
        }
        else {
            this.storeElement(reader);
        }
        while (depth > 0) {
            switch (this._eventType = reader.next()) {
                case 1: {
                    ++depth;
                    this.storeElement(reader);
                    continue;
                }
                case 2: {
                    --depth;
                    this.storeStructure(144);
                    continue;
                }
                case 13: {
                    this.storeNamespaceAttributes(reader);
                    continue;
                }
                case 10: {
                    this.storeAttributes(reader);
                    continue;
                }
                case 4:
                case 6:
                case 12: {
                    this.storeContentCharacters(80, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                    continue;
                }
                case 5: {
                    this.storeComment(reader);
                    continue;
                }
                case 3: {
                    this.storeProcessingInstruction(reader);
                    continue;
                }
            }
        }
        this._eventType = reader.next();
    }
    
    private void storeElementWithInScopeNamespaces(final XMLStreamReader reader) {
        this.storeQualifiedName(32, reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
        if (reader.getNamespaceCount() > 0) {
            this.storeNamespaceAttributes(reader);
        }
        if (reader.getAttributeCount() > 0) {
            this.storeAttributes(reader);
        }
    }
    
    private void storeElement(final XMLStreamReader reader) {
        this.storeQualifiedName(32, reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
        if (reader.getNamespaceCount() > 0) {
            this.storeNamespaceAttributes(reader);
        }
        if (reader.getAttributeCount() > 0) {
            this.storeAttributes(reader);
        }
    }
    
    public void storeElement(final String nsURI, final String localName, final String prefix, final String[] ns) {
        this.storeQualifiedName(32, prefix, nsURI, localName);
        this.storeNamespaceAttributes(ns);
    }
    
    public void storeEndElement() {
        this.storeStructure(144);
    }
    
    private void storeNamespaceAttributes(final XMLStreamReader reader) {
        for (int count = reader.getNamespaceCount(), i = 0; i < count; ++i) {
            this.storeNamespaceAttribute(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
        }
    }
    
    private void storeNamespaceAttributes(final String[] ns) {
        for (int i = 0; i < ns.length; i += 2) {
            this.storeNamespaceAttribute(ns[i], ns[i + 1]);
        }
    }
    
    private void storeAttributes(final XMLStreamReader reader) {
        for (int count = reader.getAttributeCount(), i = 0; i < count; ++i) {
            this.storeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeType(i), reader.getAttributeValue(i));
        }
    }
    
    private void storeComment(final XMLStreamReader reader) {
        this.storeContentCharacters(96, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
    }
    
    private void storeProcessingInstruction(final XMLStreamReader reader) {
        this.storeProcessingInstruction(reader.getPITarget(), reader.getPIData());
    }
}
