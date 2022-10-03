package org.apache.axiom.util.stax.xop;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import javax.xml.stream.XMLStreamReader;

public class XOPEncodingStreamReader extends XOPEncodingStreamWrapper implements XMLStreamReader
{
    private static final int STATE_PASS_THROUGH = 0;
    private static final int STATE_XOP_INCLUDE_START_ELEMENT = 1;
    private static final int STATE_XOP_INCLUDE_END_ELEMENT = 2;
    private final XMLStreamReader parent;
    private final DataHandlerReader dataHandlerReader;
    private int state;
    private String currentContentID;
    
    public XOPEncodingStreamReader(final XMLStreamReader parent, final ContentIDGenerator contentIDGenerator, final OptimizationPolicy optimizationPolicy) {
        super(contentIDGenerator, optimizationPolicy);
        this.state = 0;
        this.parent = parent;
        DataHandlerReader dataHandlerReader;
        try {
            dataHandlerReader = (DataHandlerReader)parent.getProperty(DataHandlerReader.PROPERTY);
        }
        catch (final IllegalArgumentException ex) {
            dataHandlerReader = null;
        }
        if (dataHandlerReader == null) {
            throw new IllegalArgumentException("The supplied XMLStreamReader doesn't implement the DataHandlerReader extension");
        }
        this.dataHandlerReader = dataHandlerReader;
    }
    
    public int next() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                return this.state = 2;
            }
            case 2: {
                this.state = 0;
                this.currentContentID = null;
                break;
            }
        }
        final int event = this.parent.next();
        if (event != 4 || !this.dataHandlerReader.isBinary()) {
            return event;
        }
        String contentID;
        try {
            if (this.dataHandlerReader.isDeferred()) {
                contentID = this.processDataHandler(this.dataHandlerReader.getDataHandlerProvider(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
            else {
                contentID = this.processDataHandler(this.dataHandlerReader.getDataHandler(), this.dataHandlerReader.getContentID(), this.dataHandlerReader.isOptimized());
            }
        }
        catch (final IOException ex) {
            throw new XMLStreamException("Error while processing data handler", ex);
        }
        if (contentID != null) {
            this.currentContentID = contentID;
            return this.state = 1;
        }
        return 4;
    }
    
    public boolean hasNext() throws XMLStreamException {
        return this.state != 0 || this.parent.hasNext();
    }
    
    public int nextTag() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                return this.state = 2;
            }
            case 2: {
                this.currentContentID = null;
                break;
            }
        }
        return this.parent.nextTag();
    }
    
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (this.state == 0) {
            this.parent.require(type, namespaceURI, localName);
        }
        else if ((this.state == 1 && type != 1) || (this.state == 2 && type != 2) || (namespaceURI != null && !namespaceURI.equals("http://www.w3.org/2004/08/xop/include")) || (localName != null && !localName.equals("Include"))) {
            throw new XMLStreamException();
        }
    }
    
    public Location getLocation() {
        return this.parent.getLocation();
    }
    
    public void close() throws XMLStreamException {
        this.parent.close();
    }
    
    public Object getProperty(final String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
    
    public String getEncoding() {
        return this.parent.getEncoding();
    }
    
    public String getCharacterEncodingScheme() {
        return this.parent.getCharacterEncodingScheme();
    }
    
    public String getVersion() {
        return this.parent.getVersion();
    }
    
    public boolean isStandalone() {
        return this.parent.isStandalone();
    }
    
    public boolean standaloneSet() {
        return this.parent.standaloneSet();
    }
    
    public String getPIData() {
        return this.parent.getPIData();
    }
    
    public String getPITarget() {
        return this.parent.getPITarget();
    }
    
    public int getAttributeCount() {
        switch (this.state) {
            case 1: {
                return 1;
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeCount();
            }
        }
    }
    
    public String getAttributeLocalName(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "href";
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeLocalName(index);
            }
        }
    }
    
    public QName getAttributeName(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return new QName("href");
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeName(index);
            }
        }
    }
    
    public String getAttributeNamespace(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeNamespace(index);
            }
        }
    }
    
    public String getAttributePrefix(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributePrefix(index);
            }
        }
    }
    
    public String getAttributeType(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "CDATA";
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeType(index);
            }
        }
    }
    
    public String getAttributeValue(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return "cid:" + this.currentContentID.replaceAll("%", "%25");
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeValue(index);
            }
        }
    }
    
    public boolean isAttributeSpecified(final int index) {
        switch (this.state) {
            case 1: {
                if (index != 0) {
                    throw new IllegalArgumentException();
                }
                return true;
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.isAttributeSpecified(index);
            }
        }
    }
    
    public String getAttributeValue(final String namespaceURI, final String localName) {
        switch (this.state) {
            case 1: {
                if ((namespaceURI == null || namespaceURI.length() == 0) && localName.equals("href")) {
                    return "cid:" + this.currentContentID;
                }
                return null;
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getAttributeValue(namespaceURI, localName);
            }
        }
    }
    
    public String getElementText() throws XMLStreamException {
        switch (this.state) {
            case 1: {
                this.state = 2;
                return "";
            }
            case 2: {
                throw new IllegalStateException();
            }
            default: {
                return this.parent.getElementText();
            }
        }
    }
    
    public int getEventType() {
        switch (this.state) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            default: {
                return this.parent.getEventType();
            }
        }
    }
    
    public String getNamespaceURI() {
        return (this.state == 0) ? this.parent.getNamespaceURI() : "http://www.w3.org/2004/08/xop/include";
    }
    
    public String getLocalName() {
        return (this.state == 0) ? this.parent.getLocalName() : "Include";
    }
    
    public String getPrefix() {
        return (this.state == 0) ? this.parent.getPrefix() : "xop";
    }
    
    public QName getName() {
        return (this.state == 0) ? this.parent.getName() : XOPConstants.INCLUDE_QNAME;
    }
    
    public NamespaceContext getNamespaceContext() {
        NamespaceContext ctx = this.parent.getNamespaceContext();
        if (this.state != 0) {
            ctx = new NamespaceContextWrapper(ctx);
        }
        return ctx;
    }
    
    public String getNamespaceURI(final String prefix) {
        if (this.state != 0 && "xop".equals(prefix)) {
            return "http://www.w3.org/2004/08/xop/include";
        }
        return this.parent.getNamespaceURI(prefix);
    }
    
    public int getNamespaceCount() {
        return (this.state == 0) ? this.parent.getNamespaceCount() : 1;
    }
    
    public String getNamespacePrefix(final int index) {
        if (this.state == 0) {
            return this.parent.getNamespacePrefix(index);
        }
        if (index != 0) {
            throw new IllegalArgumentException();
        }
        return "xop";
    }
    
    public String getNamespaceURI(final int index) {
        if (this.state == 0) {
            return this.parent.getNamespaceURI(index);
        }
        if (index != 0) {
            throw new IllegalArgumentException();
        }
        return "http://www.w3.org/2004/08/xop/include";
    }
    
    public String getText() {
        if (this.state == 0) {
            return this.parent.getText();
        }
        throw new IllegalStateException();
    }
    
    public int getTextStart() {
        if (this.state == 0) {
            return this.parent.getTextStart();
        }
        throw new IllegalStateException();
    }
    
    public int getTextLength() {
        if (this.state == 0) {
            return this.parent.getTextLength();
        }
        throw new IllegalStateException();
    }
    
    public char[] getTextCharacters() {
        if (this.state == 0) {
            return this.parent.getTextCharacters();
        }
        throw new IllegalStateException();
    }
    
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (this.state == 0) {
            return this.parent.getTextCharacters(sourceStart, target, targetStart, length);
        }
        throw new IllegalStateException();
    }
    
    public boolean hasName() {
        return this.state != 0 || this.parent.hasName();
    }
    
    public boolean hasText() {
        return this.state == 0 && this.parent.hasText();
    }
    
    public boolean isCharacters() {
        return this.state == 0 && this.parent.isCharacters();
    }
    
    public boolean isWhiteSpace() {
        return this.state == 0 && this.parent.isWhiteSpace();
    }
    
    public boolean isStartElement() {
        switch (this.state) {
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
            default: {
                return this.parent.isStartElement();
            }
        }
    }
    
    public boolean isEndElement() {
        switch (this.state) {
            case 1: {
                return false;
            }
            case 2: {
                return true;
            }
            default: {
                return this.parent.isEndElement();
            }
        }
    }
    
    private static class NamespaceContextWrapper implements NamespaceContext
    {
        private static final List xopPrefixList;
        private final NamespaceContext parent;
        
        public NamespaceContextWrapper(final NamespaceContext parent) {
            this.parent = parent;
        }
        
        public String getNamespaceURI(final String prefix) {
            return "xop".equals(prefix) ? "http://www.w3.org/2004/08/xop/include" : this.parent.getNamespaceURI(prefix);
        }
        
        public String getPrefix(final String namespaceURI) {
            return "http://www.w3.org/2004/08/xop/include".equals(namespaceURI) ? "xop" : this.parent.getPrefix(namespaceURI);
        }
        
        public Iterator getPrefixes(final String namespaceURI) {
            final Iterator prefixes = this.parent.getPrefixes(namespaceURI);
            if (!"http://www.w3.org/2004/08/xop/include".equals(namespaceURI)) {
                return prefixes;
            }
            if (!prefixes.hasNext()) {
                return NamespaceContextWrapper.xopPrefixList.iterator();
            }
            final List prefixList = new ArrayList();
            do {
                prefixList.add(prefixes.next());
            } while (prefixes.hasNext());
            prefixList.add("xop");
            return prefixList.iterator();
        }
        
        static {
            xopPrefixList = Arrays.asList("xop");
        }
    }
}
