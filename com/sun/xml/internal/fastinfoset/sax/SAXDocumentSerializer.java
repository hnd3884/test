package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Locator;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import org.xml.sax.Attributes;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetWriter;
import com.sun.xml.internal.fastinfoset.Encoder;

public class SAXDocumentSerializer extends Encoder implements FastInfosetWriter
{
    protected boolean _elementHasNamespaces;
    protected boolean _charactersAsCDATA;
    
    protected SAXDocumentSerializer(final boolean v) {
        super(v);
        this._elementHasNamespaces = false;
        this._charactersAsCDATA = false;
    }
    
    public SAXDocumentSerializer() {
        this._elementHasNamespaces = false;
        this._charactersAsCDATA = false;
    }
    
    @Override
    public void reset() {
        super.reset();
        this._elementHasNamespaces = false;
        this._charactersAsCDATA = false;
    }
    
    @Override
    public final void startDocument() throws SAXException {
        try {
            this.reset();
            this.encodeHeader(false);
            this.encodeInitialVocabulary();
        }
        catch (final IOException e) {
            throw new SAXException("startDocument", e);
        }
    }
    
    @Override
    public final void endDocument() throws SAXException {
        try {
            this.encodeDocumentTermination();
        }
        catch (final IOException e) {
            throw new SAXException("endDocument", e);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        try {
            if (!this._elementHasNamespaces) {
                this.encodeTermination();
                this.mark();
                this._elementHasNamespaces = true;
                this.write(56);
            }
            this.encodeNamespaceAttribute(prefix, uri);
        }
        catch (final IOException e) {
            throw new SAXException("startElement", e);
        }
    }
    
    @Override
    public final void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        final int attributeCount = (atts != null && atts.getLength() > 0) ? this.countAttributes(atts) : 0;
        try {
            if (this._elementHasNamespaces) {
                this._elementHasNamespaces = false;
                if (attributeCount > 0) {
                    final byte[] octetBuffer = this._octetBuffer;
                    final int markIndex = this._markIndex;
                    octetBuffer[markIndex] |= 0x40;
                }
                this.resetMark();
                this.write(240);
                this._b = 0;
            }
            else {
                this.encodeTermination();
                this._b = 0;
                if (attributeCount > 0) {
                    this._b |= 0x40;
                }
            }
            this.encodeElement(namespaceURI, qName, localName);
            if (attributeCount > 0) {
                this.encodeAttributes(atts);
            }
        }
        catch (final IOException e) {
            throw new SAXException("startElement", e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException("startElement", e2);
        }
    }
    
    @Override
    public final void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            this.encodeElementTermination();
        }
        catch (final IOException e) {
            throw new SAXException("endElement", e);
        }
    }
    
    @Override
    public final void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, start, length)) {
            return;
        }
        try {
            this.encodeTermination();
            if (!this._charactersAsCDATA) {
                this.encodeCharacters(ch, start, length);
            }
            else {
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.getIgnoreWhiteSpaceTextContent()) {
            return;
        }
        this.characters(ch, start, length);
    }
    
    @Override
    public final void processingInstruction(final String target, final String data) throws SAXException {
        try {
            if (this.getIgnoreProcesingInstructions()) {
                return;
            }
            if (target.length() == 0) {
                throw new SAXException(CommonResourceBundle.getInstance().getString("message.processingInstructionTargetIsEmpty"));
            }
            this.encodeTermination();
            this.encodeProcessingInstruction(target, data);
        }
        catch (final IOException e) {
            throw new SAXException("processingInstruction", e);
        }
    }
    
    @Override
    public final void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public final void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public final void comment(final char[] ch, final int start, final int length) throws SAXException {
        try {
            if (this.getIgnoreComments()) {
                return;
            }
            this.encodeTermination();
            this.encodeComment(ch, start, length);
        }
        catch (final IOException e) {
            throw new SAXException("startElement", e);
        }
    }
    
    @Override
    public final void startCDATA() throws SAXException {
        this._charactersAsCDATA = true;
    }
    
    @Override
    public final void endCDATA() throws SAXException {
        this._charactersAsCDATA = false;
    }
    
    @Override
    public final void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        if (this.getIgnoreDTD()) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeDocumentTypeDeclaration(publicId, systemId);
            this.encodeElementTermination();
        }
        catch (final IOException e) {
            throw new SAXException("startDTD", e);
        }
    }
    
    @Override
    public final void endDTD() throws SAXException {
    }
    
    @Override
    public final void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public final void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public final void octets(final String URI, final int id, final byte[] b, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeNonIdentifyingStringOnThirdBit(URI, id, b, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void object(final String URI, final int id, final Object data) throws SAXException {
        try {
            this.encodeTermination();
            this.encodeNonIdentifyingStringOnThirdBit(URI, id, data);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void bytes(final byte[] b, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIOctetAlgorithmData(1, b, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public final void shorts(final short[] s, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(2, s, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void ints(final int[] i, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(3, i, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void longs(final long[] l, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(4, l, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void booleans(final boolean[] b, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(5, b, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void floats(final float[] f, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(6, f, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public final void doubles(final double[] d, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(7, d, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void uuids(final long[] msblsb, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(8, msblsb, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void numericCharacters(final char[] ch, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeNumericFourBitCharacters(ch, start, length, addToTable);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void dateTimeCharacters(final char[] ch, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeDateTimeFourBitCharacters(ch, start, length, addToTable);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void alphabetCharacters(final String alphabet, final char[] ch, final int start, final int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeAlphabetCharacters(alphabet, ch, start, length, addToTable);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length, final boolean index) throws SAXException {
        if (length <= 0) {
            return;
        }
        if (this.getIgnoreWhiteSpaceTextContent() && Encoder.isWhiteSpace(ch, start, length)) {
            return;
        }
        try {
            this.encodeTermination();
            if (!this._charactersAsCDATA) {
                this.encodeNonIdentifyingStringOnThirdBit(ch, start, length, this._v.characterContentChunk, index, true);
            }
            else {
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        catch (final FastInfosetException e2) {
            throw new SAXException(e2);
        }
    }
    
    protected final int countAttributes(final Attributes atts) {
        int count = 0;
        for (int i = 0; i < atts.getLength(); ++i) {
            final String uri = atts.getURI(i);
            if (uri != "http://www.w3.org/2000/xmlns/") {
                if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    protected void encodeAttributes(final Attributes atts) throws IOException, FastInfosetException {
        if (atts instanceof EncodingAlgorithmAttributes) {
            final EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            for (int i = 0; i < eAtts.getLength(); ++i) {
                if (this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
                    final Object data = eAtts.getAlgorithmData(i);
                    if (data == null) {
                        final String value = eAtts.getValue(i);
                        final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                        final boolean mustBeAddedToTable = eAtts.getToIndex(i);
                        final String alphabet = eAtts.getAlpababet(i);
                        if (alphabet == null) {
                            this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                        }
                        else if (alphabet == "0123456789-:TZ ") {
                            this.encodeDateTimeNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                        }
                        else if (alphabet == "0123456789-+.E ") {
                            this.encodeNumericNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                        }
                        else {
                            this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                        }
                    }
                    else {
                        this.encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i), eAtts.getAlgorithmIndex(i), data);
                    }
                }
            }
        }
        else {
            for (int j = 0; j < atts.getLength(); ++j) {
                if (this.encodeAttribute(atts.getURI(j), atts.getQName(j), atts.getLocalName(j))) {
                    final String value = atts.getValue(j);
                    final boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                    this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
                }
            }
        }
        this._b = 240;
        this._terminate = true;
    }
    
    protected void encodeElement(final String namespaceURI, final String qName, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                final QualifiedName n = names[i];
                if (namespaceURI == n.namespaceName || namespaceURI.equals(n.namespaceName)) {
                    this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }
        this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, Encoder.getPrefixFromQualifiedName(qName), localName, entry);
    }
    
    protected boolean encodeAttribute(final String namespaceURI, final String qName, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName)) {
                    this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return true;
                }
            }
        }
        return this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, Encoder.getPrefixFromQualifiedName(qName), localName, entry);
    }
}
