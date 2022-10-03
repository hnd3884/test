package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import javax.xml.stream.Location;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;

final class FastInfosetConnector extends StAXConnector
{
    private final StAXDocumentParser fastInfosetStreamReader;
    private boolean textReported;
    private final Base64Data base64Data;
    private final StringBuilder buffer;
    private final CharSequenceImpl charArray;
    
    public FastInfosetConnector(final StAXDocumentParser fastInfosetStreamReader, final XmlVisitor visitor) {
        super(visitor);
        this.base64Data = new Base64Data();
        this.buffer = new StringBuilder();
        this.charArray = new CharSequenceImpl();
        fastInfosetStreamReader.setStringInterning(true);
        this.fastInfosetStreamReader = fastInfosetStreamReader;
    }
    
    @Override
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            int event = this.fastInfosetStreamReader.getEventType();
            if (event == 7) {
                while (!this.fastInfosetStreamReader.isStartElement()) {
                    event = this.fastInfosetStreamReader.next();
                }
            }
            if (event != 1) {
                throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
            }
            this.handleStartDocument(this.fastInfosetStreamReader.getNamespaceContext());
        Block_4:
            while (true) {
                switch (event) {
                    case 1: {
                        this.handleStartElement();
                        ++depth;
                        break;
                    }
                    case 2: {
                        --depth;
                        this.handleEndElement();
                        if (depth == 0) {
                            break Block_4;
                        }
                        break;
                    }
                    case 4:
                    case 6:
                    case 12: {
                        if (!this.predictor.expectText()) {
                            break;
                        }
                        event = this.fastInfosetStreamReader.peekNext();
                        if (event == 2) {
                            this.processNonIgnorableText();
                            break;
                        }
                        if (event == 1) {
                            this.processIgnorableText();
                            break;
                        }
                        this.handleFragmentedCharacters();
                        break;
                    }
                }
                event = this.fastInfosetStreamReader.next();
            }
            this.fastInfosetStreamReader.next();
            this.handleEndDocument();
        }
        catch (final SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    protected Location getCurrentLocation() {
        return this.fastInfosetStreamReader.getLocation();
    }
    
    @Override
    protected String getCurrentQName() {
        return this.fastInfosetStreamReader.getNameString();
    }
    
    private void handleStartElement() throws SAXException {
        this.processUnreportedText();
        for (int i = 0; i < this.fastInfosetStreamReader.accessNamespaceCount(); ++i) {
            this.visitor.startPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(i), this.fastInfosetStreamReader.getNamespaceURI(i));
        }
        this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
        this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
        this.tagName.atts = this.fastInfosetStreamReader.getAttributesHolder();
        this.visitor.startElement(this.tagName);
    }
    
    private void handleFragmentedCharacters() throws XMLStreamException, SAXException {
        this.buffer.setLength(0);
        this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
        while (true) {
            switch (this.fastInfosetStreamReader.peekNext()) {
                case 1: {
                    this.processBufferedText(true);
                    return;
                }
                case 2: {
                    this.processBufferedText(false);
                    return;
                }
                case 4:
                case 6:
                case 12: {
                    this.fastInfosetStreamReader.next();
                    this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
                    continue;
                }
                default: {
                    this.fastInfosetStreamReader.next();
                    continue;
                }
            }
        }
    }
    
    private void handleEndElement() throws SAXException {
        this.processUnreportedText();
        this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
        this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
        this.visitor.endElement(this.tagName);
        for (int i = this.fastInfosetStreamReader.accessNamespaceCount() - 1; i >= 0; --i) {
            this.visitor.endPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(i));
        }
    }
    
    private void processNonIgnorableText() throws SAXException {
        this.textReported = true;
        final boolean isTextAlgorithmAplied = this.fastInfosetStreamReader.getTextAlgorithmBytes() != null;
        if (isTextAlgorithmAplied && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
            this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
            this.visitor.text(this.base64Data);
        }
        else {
            if (isTextAlgorithmAplied) {
                this.fastInfosetStreamReader.getText();
            }
            this.charArray.set();
            this.visitor.text(this.charArray);
        }
    }
    
    private void processIgnorableText() throws SAXException {
        final boolean isTextAlgorithmAplied = this.fastInfosetStreamReader.getTextAlgorithmBytes() != null;
        if (isTextAlgorithmAplied && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
            this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
            this.visitor.text(this.base64Data);
            this.textReported = true;
        }
        else {
            if (isTextAlgorithmAplied) {
                this.fastInfosetStreamReader.getText();
            }
            this.charArray.set();
            if (!WhiteSpaceProcessor.isWhiteSpace(this.charArray)) {
                this.visitor.text(this.charArray);
                this.textReported = true;
            }
        }
    }
    
    private void processBufferedText(final boolean ignorable) throws SAXException {
        if (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer)) {
            this.visitor.text(this.buffer);
            this.textReported = true;
        }
    }
    
    private void processUnreportedText() throws SAXException {
        if (!this.textReported && this.predictor.expectText()) {
            this.visitor.text("");
        }
        this.textReported = false;
    }
    
    private final class CharSequenceImpl implements CharSequence
    {
        char[] ch;
        int start;
        int length;
        
        CharSequenceImpl() {
        }
        
        CharSequenceImpl(final char[] ch, final int start, final int length) {
            this.ch = ch;
            this.start = start;
            this.length = length;
        }
        
        public void set() {
            this.ch = FastInfosetConnector.this.fastInfosetStreamReader.getTextCharacters();
            this.start = FastInfosetConnector.this.fastInfosetStreamReader.getTextStart();
            this.length = FastInfosetConnector.this.fastInfosetStreamReader.getTextLength();
        }
        
        @Override
        public final int length() {
            return this.length;
        }
        
        @Override
        public final char charAt(final int index) {
            return this.ch[this.start + index];
        }
        
        @Override
        public final CharSequence subSequence(final int start, final int end) {
            return new CharSequenceImpl(this.ch, this.start + start, end - start);
        }
        
        @Override
        public String toString() {
            return new String(this.ch, this.start, this.length);
        }
    }
}
