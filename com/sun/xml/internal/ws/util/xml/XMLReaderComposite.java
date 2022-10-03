package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;

public class XMLReaderComposite implements XMLStreamReaderEx
{
    protected State state;
    protected ElemInfo elemInfo;
    protected TagInfoset tagInfo;
    protected XMLStreamReader[] children;
    protected int payloadIndex;
    protected XMLStreamReader payloadReader;
    
    public XMLReaderComposite(final ElemInfo elem, final XMLStreamReader[] wrapees) {
        this.state = State.StartTag;
        this.payloadIndex = -1;
        this.elemInfo = elem;
        this.tagInfo = elem.tagInfo;
        this.children = wrapees;
        if (this.children != null && this.children.length > 0) {
            this.payloadIndex = 0;
            this.payloadReader = this.children[this.payloadIndex];
        }
    }
    
    @Override
    public int next() throws XMLStreamException {
        switch (this.state) {
            case StartTag: {
                if (this.payloadReader != null) {
                    this.state = State.Payload;
                    return this.payloadReader.getEventType();
                }
                this.state = State.EndTag;
                return 2;
            }
            case EndTag: {
                return 8;
            }
            default: {
                int next = 8;
                if (this.payloadReader != null && this.payloadReader.hasNext()) {
                    next = this.payloadReader.next();
                }
                if (next != 8) {
                    return next;
                }
                if (this.payloadIndex + 1 < this.children.length) {
                    ++this.payloadIndex;
                    this.payloadReader = this.children[this.payloadIndex];
                    return this.payloadReader.getEventType();
                }
                this.state = State.EndTag;
                return 2;
            }
        }
    }
    
    @Override
    public boolean hasNext() throws XMLStreamException {
        switch (this.state) {
            case EndTag: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        switch (this.state) {
            case StartTag: {
                if (this.payloadReader.isCharacters()) {
                    return this.payloadReader.getText();
                }
                return "";
            }
            default: {
                return this.payloadReader.getElementText();
            }
        }
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        int e = this.next();
        if (e == 8) {
            return e;
        }
        while (e != 8) {
            if (e == 1) {
                return e;
            }
            if (e == 2) {
                return e;
            }
            e = this.next();
        }
        return e;
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        return (this.payloadReader != null) ? this.payloadReader.getProperty(name) : null;
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (this.payloadReader != null) {
            this.payloadReader.require(type, namespaceURI, localName);
        }
    }
    
    @Override
    public void close() throws XMLStreamException {
        if (this.payloadReader != null) {
            this.payloadReader.close();
        }
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.elemInfo.getNamespaceURI(prefix);
            }
            default: {
                return this.payloadReader.getNamespaceURI(prefix);
            }
        }
    }
    
    @Override
    public boolean isStartElement() {
        switch (this.state) {
            case StartTag: {
                return true;
            }
            case EndTag: {
                return false;
            }
            default: {
                return this.payloadReader.isStartElement();
            }
        }
    }
    
    @Override
    public boolean isEndElement() {
        switch (this.state) {
            case StartTag: {
                return false;
            }
            case EndTag: {
                return true;
            }
            default: {
                return this.payloadReader.isEndElement();
            }
        }
    }
    
    @Override
    public boolean isCharacters() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return false;
            }
            default: {
                return this.payloadReader.isCharacters();
            }
        }
    }
    
    @Override
    public boolean isWhiteSpace() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return false;
            }
            default: {
                return this.payloadReader.isWhiteSpace();
            }
        }
    }
    
    @Override
    public String getAttributeValue(final String uri, final String localName) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getValue(uri, localName);
            }
            default: {
                return this.payloadReader.getAttributeValue(uri, localName);
            }
        }
    }
    
    @Override
    public int getAttributeCount() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getLength();
            }
            default: {
                return this.payloadReader.getAttributeCount();
            }
        }
    }
    
    @Override
    public QName getAttributeName(final int i) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return new QName(this.tagInfo.atts.getURI(i), this.tagInfo.atts.getLocalName(i), getPrfix(this.tagInfo.atts.getQName(i)));
            }
            default: {
                return this.payloadReader.getAttributeName(i);
            }
        }
    }
    
    @Override
    public String getAttributeNamespace(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getURI(index);
            }
            default: {
                return this.payloadReader.getAttributeNamespace(index);
            }
        }
    }
    
    @Override
    public String getAttributeLocalName(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getLocalName(index);
            }
            default: {
                return this.payloadReader.getAttributeLocalName(index);
            }
        }
    }
    
    @Override
    public String getAttributePrefix(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return getPrfix(this.tagInfo.atts.getQName(index));
            }
            default: {
                return this.payloadReader.getAttributePrefix(index);
            }
        }
    }
    
    private static String getPrfix(final String qName) {
        if (qName == null) {
            return null;
        }
        final int i = qName.indexOf(":");
        return (i > 0) ? qName.substring(0, i) : "";
    }
    
    @Override
    public String getAttributeType(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getType(index);
            }
            default: {
                return this.payloadReader.getAttributeType(index);
            }
        }
    }
    
    @Override
    public String getAttributeValue(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.atts.getValue(index);
            }
            default: {
                return this.payloadReader.getAttributeValue(index);
            }
        }
    }
    
    @Override
    public boolean isAttributeSpecified(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return index < this.tagInfo.atts.getLength() && this.tagInfo.atts.getLocalName(index) != null;
            }
            default: {
                return this.payloadReader.isAttributeSpecified(index);
            }
        }
    }
    
    @Override
    public int getNamespaceCount() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.ns.length / 2;
            }
            default: {
                return this.payloadReader.getNamespaceCount();
            }
        }
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.ns[2 * index];
            }
            default: {
                return this.payloadReader.getNamespacePrefix(index);
            }
        }
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.ns[2 * index + 1];
            }
            default: {
                return this.payloadReader.getNamespaceURI(index);
            }
        }
    }
    
    @Override
    public NamespaceContextEx getNamespaceContext() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return new NamespaceContextExAdaper(this.elemInfo);
            }
            default: {
                return this.isPayloadReaderEx() ? this.payloadReaderEx().getNamespaceContext() : new NamespaceContextExAdaper(this.payloadReader.getNamespaceContext());
            }
        }
    }
    
    private boolean isPayloadReaderEx() {
        return this.payloadReader instanceof XMLStreamReaderEx;
    }
    
    private XMLStreamReaderEx payloadReaderEx() {
        return (XMLStreamReaderEx)this.payloadReader;
    }
    
    @Override
    public int getEventType() {
        switch (this.state) {
            case StartTag: {
                return 1;
            }
            case EndTag: {
                return 2;
            }
            default: {
                return this.payloadReader.getEventType();
            }
        }
    }
    
    @Override
    public String getText() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getText();
            }
        }
    }
    
    @Override
    public char[] getTextCharacters() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getTextCharacters();
            }
        }
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return -1;
            }
            default: {
                return this.payloadReader.getTextCharacters(sourceStart, target, targetStart, length);
            }
        }
    }
    
    @Override
    public int getTextStart() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return 0;
            }
            default: {
                return this.payloadReader.getTextStart();
            }
        }
    }
    
    @Override
    public int getTextLength() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return 0;
            }
            default: {
                return this.payloadReader.getTextLength();
            }
        }
    }
    
    @Override
    public String getEncoding() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getEncoding();
            }
        }
    }
    
    @Override
    public boolean hasText() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return false;
            }
            default: {
                return this.payloadReader.hasText();
            }
        }
    }
    
    @Override
    public Location getLocation() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return new Location() {
                    @Override
                    public int getLineNumber() {
                        return 0;
                    }
                    
                    @Override
                    public int getColumnNumber() {
                        return 0;
                    }
                    
                    @Override
                    public int getCharacterOffset() {
                        return 0;
                    }
                    
                    @Override
                    public String getPublicId() {
                        return null;
                    }
                    
                    @Override
                    public String getSystemId() {
                        return null;
                    }
                };
            }
            default: {
                return this.payloadReader.getLocation();
            }
        }
    }
    
    @Override
    public QName getName() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return new QName(this.tagInfo.nsUri, this.tagInfo.localName, this.tagInfo.prefix);
            }
            default: {
                return this.payloadReader.getName();
            }
        }
    }
    
    @Override
    public String getLocalName() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.localName;
            }
            default: {
                return this.payloadReader.getLocalName();
            }
        }
    }
    
    @Override
    public boolean hasName() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return true;
            }
            default: {
                return this.payloadReader.hasName();
            }
        }
    }
    
    @Override
    public String getNamespaceURI() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.nsUri;
            }
            default: {
                return this.payloadReader.getNamespaceURI();
            }
        }
    }
    
    @Override
    public String getPrefix() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return this.tagInfo.prefix;
            }
            default: {
                return this.payloadReader.getPrefix();
            }
        }
    }
    
    @Override
    public String getVersion() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getVersion();
            }
        }
    }
    
    @Override
    public boolean isStandalone() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return true;
            }
            default: {
                return this.payloadReader.isStandalone();
            }
        }
    }
    
    @Override
    public boolean standaloneSet() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return true;
            }
            default: {
                return this.payloadReader.standaloneSet();
            }
        }
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getCharacterEncodingScheme();
            }
        }
    }
    
    @Override
    public String getPITarget() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getPITarget();
            }
        }
    }
    
    @Override
    public String getPIData() {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.payloadReader.getPIData();
            }
        }
    }
    
    @Override
    public String getElementTextTrim() throws XMLStreamException {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.isPayloadReaderEx() ? this.payloadReaderEx().getElementTextTrim() : this.payloadReader.getElementText().trim();
            }
        }
    }
    
    @Override
    public CharSequence getPCDATA() throws XMLStreamException {
        switch (this.state) {
            case StartTag:
            case EndTag: {
                return null;
            }
            default: {
                return this.isPayloadReaderEx() ? this.payloadReaderEx().getPCDATA() : this.payloadReader.getElementText();
            }
        }
    }
    
    public enum State
    {
        StartTag, 
        Payload, 
        EndTag;
    }
    
    public static class ElemInfo implements NamespaceContext
    {
        ElemInfo ancestor;
        TagInfoset tagInfo;
        
        public ElemInfo(final TagInfoset tag, final ElemInfo parent) {
            this.tagInfo = tag;
            this.ancestor = parent;
        }
        
        @Override
        public String getNamespaceURI(final String prefix) {
            final String n = this.tagInfo.getNamespaceURI(prefix);
            return (n != null) ? n : ((this.ancestor != null) ? this.ancestor.getNamespaceURI(prefix) : null);
        }
        
        @Override
        public String getPrefix(final String uri) {
            final String p = this.tagInfo.getPrefix(uri);
            return (p != null) ? p : ((this.ancestor != null) ? this.ancestor.getPrefix(uri) : null);
        }
        
        public List<String> allPrefixes(final String namespaceURI) {
            final List<String> l = this.tagInfo.allPrefixes(namespaceURI);
            if (this.ancestor != null) {
                final List<String> p = this.ancestor.allPrefixes(namespaceURI);
                p.addAll(l);
                return p;
            }
            return l;
        }
        
        @Override
        public Iterator<String> getPrefixes(final String namespaceURI) {
            return this.allPrefixes(namespaceURI).iterator();
        }
    }
}
