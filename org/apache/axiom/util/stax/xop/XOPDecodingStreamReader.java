package org.apache.axiom.util.stax.xop;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.util.base64.Base64Utils;
import javax.activation.DataHandler;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;
import org.apache.axiom.util.stax.XMLEventUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

public class XOPDecodingStreamReader extends XMLStreamReaderWrapper implements DataHandlerReader
{
    private static final String SOLE_CHILD_MSG = "Expected xop:Include as the sole child of an element information item (see section 3.2 of http://www.w3.org/TR/xop10/)";
    private static final Log log;
    private final MimePartProvider mimePartProvider;
    private DataHandlerProviderImpl dh;
    private String base64;
    
    public XOPDecodingStreamReader(final XMLStreamReader parent, final MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
    }
    
    private void resetDataHandler() {
        this.dh = null;
        this.base64 = null;
    }
    
    private String processXopInclude() throws XMLStreamException {
        if (super.getAttributeCount() != 1 || !super.getAttributeLocalName(0).equals("href")) {
            throw new XMLStreamException("Expected xop:Include element information item with a (single) href attribute");
        }
        final String href = super.getAttributeValue(0);
        if (XOPDecodingStreamReader.log.isDebugEnabled()) {
            XOPDecodingStreamReader.log.debug((Object)("processXopInclude - found href : " + href));
        }
        if (!href.startsWith("cid:")) {
            throw new XMLStreamException("Expected href attribute containing a URL in the cid scheme");
        }
        String contentID;
        try {
            contentID = URLDecoder.decode(href.substring(4), "ascii");
            if (XOPDecodingStreamReader.log.isDebugEnabled()) {
                XOPDecodingStreamReader.log.debug((Object)("processXopInclude - decoded contentID : " + contentID));
            }
        }
        catch (final UnsupportedEncodingException ex) {
            throw new XMLStreamException(ex);
        }
        if (super.next() != 2) {
            throw new XMLStreamException("Expected xop:Include element information item to be empty");
        }
        if (super.next() != 2) {
            throw new XMLStreamException("Expected xop:Include as the sole child of an element information item (see section 3.2 of http://www.w3.org/TR/xop10/)");
        }
        if (XOPDecodingStreamReader.log.isDebugEnabled()) {
            XOPDecodingStreamReader.log.debug((Object)("Encountered xop:Include for content ID '" + contentID + "'"));
        }
        return contentID;
    }
    
    @Override
    public int next() throws XMLStreamException {
        int event;
        boolean wasStartElement;
        if (this.dh != null) {
            this.resetDataHandler();
            event = 2;
            wasStartElement = false;
        }
        else {
            wasStartElement = (super.getEventType() == 1);
            event = super.next();
        }
        if (event != 1 || !super.getLocalName().equals("Include") || !super.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
            return event;
        }
        if (!wasStartElement) {
            throw new XMLStreamException("Expected xop:Include as the sole child of an element information item (see section 3.2 of http://www.w3.org/TR/xop10/)");
        }
        this.dh = new DataHandlerProviderImpl(this.mimePartProvider, this.processXopInclude());
        return 4;
    }
    
    @Override
    public int getEventType() {
        return (this.dh == null) ? super.getEventType() : 4;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        if (this.dh != null) {
            this.resetDataHandler();
            return 2;
        }
        return super.nextTag();
    }
    
    @Override
    public Object getProperty(final String name) throws IllegalArgumentException {
        if (DataHandlerReader.PROPERTY.equals(name)) {
            return this;
        }
        return super.getProperty(name);
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        if (super.getEventType() != 1) {
            throw new XMLStreamException("The current event is not a START_ELEMENT event");
        }
        int event = super.next();
        if (event == 1 && super.getLocalName().equals("Include") && super.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
            final String contentID = this.processXopInclude();
            try {
                return toBase64(this.mimePartProvider.getDataHandler(contentID));
            }
            catch (final IOException ex) {
                throw new XMLStreamException("Failed to load MIME part '" + contentID + "'", ex);
            }
        }
        String text = null;
        StringBuffer buffer = null;
        while (event != 2) {
            switch (event) {
                case 4:
                case 6:
                case 9:
                case 12: {
                    if (text == null && buffer == null) {
                        text = super.getText();
                        break;
                    }
                    final String thisText = super.getText();
                    if (buffer == null) {
                        buffer = new StringBuffer(text.length() + thisText.length());
                        buffer.append(text);
                    }
                    buffer.append(thisText);
                    break;
                }
                case 3:
                case 5: {
                    break;
                }
                default: {
                    throw new XMLStreamException("Unexpected event " + XMLEventUtils.getEventTypeString(event) + " while reading element text");
                }
            }
            event = super.next();
        }
        if (buffer != null) {
            return buffer.toString();
        }
        if (text != null) {
            return text;
        }
        return "";
    }
    
    @Override
    public String getPrefix() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getPrefix();
    }
    
    @Override
    public String getNamespaceURI() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceURI();
    }
    
    @Override
    public String getLocalName() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getLocalName();
    }
    
    @Override
    public QName getName() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getName();
    }
    
    @Override
    public Location getLocation() {
        return super.getLocation();
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        final String uri = super.getNamespaceURI(prefix);
        if ("xop".equals(prefix) && uri != null) {
            System.out.println(prefix + " -> " + uri);
        }
        return uri;
    }
    
    @Override
    public int getNamespaceCount() {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceCount();
    }
    
    @Override
    public String getNamespacePrefix(final int index) {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespacePrefix(index);
    }
    
    @Override
    public String getNamespaceURI(final int index) {
        if (this.dh != null) {
            throw new IllegalStateException();
        }
        return super.getNamespaceURI(index);
    }
    
    private static String toBase64(final DataHandler dh) throws XMLStreamException {
        try {
            return Base64Utils.encode(dh);
        }
        catch (final IOException ex) {
            throw new XMLStreamException("Exception when encoding data handler as base64", ex);
        }
    }
    
    private String toBase64() throws XMLStreamException {
        if (this.base64 == null) {
            try {
                this.base64 = toBase64(this.dh.getDataHandler());
            }
            catch (final IOException ex) {
                throw new XMLStreamException("Failed to load MIME part '" + this.dh.getContentID() + "'", ex);
            }
        }
        return this.base64;
    }
    
    @Override
    public String getText() {
        if (this.dh != null) {
            try {
                return this.toBase64();
            }
            catch (final XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getText();
    }
    
    @Override
    public char[] getTextCharacters() {
        if (this.dh != null) {
            try {
                return this.toBase64().toCharArray();
            }
            catch (final XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getTextCharacters();
    }
    
    @Override
    public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
        if (this.dh != null) {
            final String text = this.toBase64();
            final int copied = Math.min(length, text.length() - sourceStart);
            text.getChars(sourceStart, sourceStart + copied, target, targetStart);
            return copied;
        }
        return super.getTextCharacters(sourceStart, target, targetStart, length);
    }
    
    @Override
    public int getTextLength() {
        if (this.dh != null) {
            try {
                return this.toBase64().length();
            }
            catch (final XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getTextLength();
    }
    
    @Override
    public int getTextStart() {
        if (this.dh != null) {
            return 0;
        }
        return super.getTextStart();
    }
    
    @Override
    public boolean hasText() {
        return this.dh != null || super.hasText();
    }
    
    @Override
    public boolean isCharacters() {
        return this.dh != null || super.isCharacters();
    }
    
    @Override
    public boolean isStartElement() {
        return this.dh == null && super.isStartElement();
    }
    
    @Override
    public boolean isEndElement() {
        return this.dh == null && super.isEndElement();
    }
    
    @Override
    public boolean hasName() {
        return this.dh == null && super.hasName();
    }
    
    @Override
    public boolean isWhiteSpace() {
        return this.dh == null && super.isWhiteSpace();
    }
    
    @Override
    public void require(final int type, final String namespaceURI, final String localName) throws XMLStreamException {
        if (this.dh != null) {
            if (type != 4) {
                throw new XMLStreamException("Expected CHARACTERS event");
            }
        }
        else {
            super.require(type, namespaceURI, localName);
        }
    }
    
    public boolean isBinary() {
        return this.dh != null;
    }
    
    public boolean isOptimized() {
        return true;
    }
    
    public boolean isDeferred() {
        return true;
    }
    
    public String getContentID() {
        return this.dh.getContentID();
    }
    
    public DataHandler getDataHandler() throws XMLStreamException {
        try {
            return this.dh.getDataHandler();
        }
        catch (final IOException ex) {
            throw new XMLStreamException("Failed to load MIME part '" + this.dh.getContentID() + "'");
        }
    }
    
    public DataHandlerProvider getDataHandlerProvider() {
        return this.dh;
    }
    
    XOPEncodedStream getXOPEncodedStream() {
        return new XOPEncodedStream(this.getParent(), this.mimePartProvider);
    }
    
    static {
        log = LogFactory.getLog((Class)XOPDecodingStreamReader.class);
    }
    
    private static class DataHandlerProviderImpl implements DataHandlerProvider
    {
        private final MimePartProvider mimePartProvider;
        private final String contentID;
        
        public DataHandlerProviderImpl(final MimePartProvider mimePartProvider, final String contentID) {
            this.mimePartProvider = mimePartProvider;
            this.contentID = contentID;
        }
        
        public String getContentID() {
            return this.contentID;
        }
        
        public boolean isLoaded() {
            return this.mimePartProvider.isLoaded(this.contentID);
        }
        
        public DataHandler getDataHandler() throws IOException {
            return this.mimePartProvider.getDataHandler(this.contentID);
        }
    }
}
