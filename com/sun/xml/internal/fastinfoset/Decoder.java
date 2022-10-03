package com.sun.xml.internal.fastinfoset;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import java.io.EOFException;
import com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import java.util.ArrayList;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.org.jvnet.fastinfoset.ExternalVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import java.util.HashMap;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetParser;

public abstract class Decoder implements FastInfosetParser
{
    private static final char[] XML_NAMESPACE_NAME_CHARS;
    private static final char[] XMLNS_NAMESPACE_PREFIX_CHARS;
    private static final char[] XMLNS_NAMESPACE_NAME_CHARS;
    public static final String STRING_INTERNING_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.parser.string-interning";
    public static final String BUFFER_SIZE_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.parser.buffer-size";
    private static boolean _stringInterningSystemDefault;
    private static int _bufferSizeSystemDefault;
    private boolean _stringInterning;
    private InputStream _s;
    private Map _externalVocabularies;
    protected boolean _parseFragments;
    protected boolean _needForceStreamClose;
    private boolean _vIsInternal;
    protected List _notations;
    protected List _unparsedEntities;
    protected Map _registeredEncodingAlgorithms;
    protected ParserVocabulary _v;
    protected PrefixArray _prefixTable;
    protected QualifiedNameArray _elementNameTable;
    protected QualifiedNameArray _attributeNameTable;
    protected ContiguousCharArrayArray _characterContentChunkTable;
    protected StringArray _attributeValueTable;
    protected int _b;
    protected boolean _terminate;
    protected boolean _doubleTerminate;
    protected boolean _addToTable;
    protected int _integer;
    protected int _identifier;
    protected int _bufferSize;
    protected byte[] _octetBuffer;
    protected int _octetBufferStart;
    protected int _octetBufferOffset;
    protected int _octetBufferEnd;
    protected int _octetBufferLength;
    protected char[] _charBuffer;
    protected int _charBufferLength;
    protected DuplicateAttributeVerifier _duplicateAttributeVerifier;
    protected static final int NISTRING_STRING = 0;
    protected static final int NISTRING_INDEX = 1;
    protected static final int NISTRING_ENCODING_ALGORITHM = 2;
    protected static final int NISTRING_EMPTY_STRING = 3;
    protected int _prefixIndex;
    protected int _namespaceNameIndex;
    private int _bitsLeftInOctet;
    private char _utf8_highSurrogate;
    private char _utf8_lowSurrogate;
    
    protected Decoder() {
        this._stringInterning = Decoder._stringInterningSystemDefault;
        this._registeredEncodingAlgorithms = new HashMap();
        this._bufferSize = Decoder._bufferSizeSystemDefault;
        this._octetBuffer = new byte[Decoder._bufferSizeSystemDefault];
        this._charBuffer = new char[512];
        this._duplicateAttributeVerifier = new DuplicateAttributeVerifier();
        this._v = new ParserVocabulary();
        this._prefixTable = this._v.prefix;
        this._elementNameTable = this._v.elementName;
        this._attributeNameTable = this._v.attributeName;
        this._characterContentChunkTable = this._v.characterContentChunk;
        this._attributeValueTable = this._v.attributeValue;
        this._vIsInternal = true;
    }
    
    @Override
    public void setStringInterning(final boolean stringInterning) {
        this._stringInterning = stringInterning;
    }
    
    @Override
    public boolean getStringInterning() {
        return this._stringInterning;
    }
    
    @Override
    public void setBufferSize(final int bufferSize) {
        if (this._bufferSize > this._octetBuffer.length) {
            this._bufferSize = bufferSize;
        }
    }
    
    @Override
    public int getBufferSize() {
        return this._bufferSize;
    }
    
    @Override
    public void setRegisteredEncodingAlgorithms(final Map algorithms) {
        this._registeredEncodingAlgorithms = algorithms;
        if (this._registeredEncodingAlgorithms == null) {
            this._registeredEncodingAlgorithms = new HashMap();
        }
    }
    
    @Override
    public Map getRegisteredEncodingAlgorithms() {
        return this._registeredEncodingAlgorithms;
    }
    
    @Override
    public void setExternalVocabularies(final Map referencedVocabualries) {
        if (referencedVocabualries != null) {
            (this._externalVocabularies = new HashMap()).putAll(referencedVocabualries);
        }
        else {
            this._externalVocabularies = null;
        }
    }
    
    @Override
    public Map getExternalVocabularies() {
        return this._externalVocabularies;
    }
    
    @Override
    public void setParseFragments(final boolean parseFragments) {
        this._parseFragments = parseFragments;
    }
    
    @Override
    public boolean getParseFragments() {
        return this._parseFragments;
    }
    
    @Override
    public void setForceStreamClose(final boolean needForceStreamClose) {
        this._needForceStreamClose = needForceStreamClose;
    }
    
    @Override
    public boolean getForceStreamClose() {
        return this._needForceStreamClose;
    }
    
    public void reset() {
        final boolean b = false;
        this._doubleTerminate = b;
        this._terminate = b;
    }
    
    public void setVocabulary(final ParserVocabulary v) {
        this._v = v;
        this._prefixTable = this._v.prefix;
        this._elementNameTable = this._v.elementName;
        this._attributeNameTable = this._v.attributeName;
        this._characterContentChunkTable = this._v.characterContentChunk;
        this._attributeValueTable = this._v.attributeValue;
        this._vIsInternal = false;
    }
    
    public void setInputStream(final InputStream s) {
        this._s = s;
        this._octetBufferOffset = 0;
        this._octetBufferEnd = 0;
        if (this._vIsInternal) {
            this._v.clear();
        }
    }
    
    protected final void decodeDII() throws FastInfosetException, IOException {
        final int b = this.read();
        if (b == 32) {
            this.decodeInitialVocabulary();
        }
        else if (b != 0) {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.optinalValues"));
        }
    }
    
    protected final void decodeAdditionalData() throws FastInfosetException, IOException {
        for (int noOfItems = this.decodeNumberOfItemsOfSequence(), i = 0; i < noOfItems; ++i) {
            this.decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
            this.decodeNonEmptyOctetStringLengthOnSecondBit();
            this.ensureOctetBufferSize();
            this._octetBufferStart = this._octetBufferOffset;
            this._octetBufferOffset += this._octetBufferLength;
        }
    }
    
    protected final void decodeInitialVocabulary() throws FastInfosetException, IOException {
        final int b = this.read();
        final int b2 = this.read();
        if (b == 16 && b2 == 0) {
            this.decodeExternalVocabularyURI();
            return;
        }
        if ((b & 0x10) > 0) {
            this.decodeExternalVocabularyURI();
        }
        if ((b & 0x8) > 0) {
            this.decodeTableItems(this._v.restrictedAlphabet);
        }
        if ((b & 0x4) > 0) {
            this.decodeTableItems(this._v.encodingAlgorithm);
        }
        if ((b & 0x2) > 0) {
            this.decodeTableItems(this._v.prefix);
        }
        if ((b & 0x1) > 0) {
            this.decodeTableItems(this._v.namespaceName);
        }
        if ((b2 & 0x80) > 0) {
            this.decodeTableItems(this._v.localName);
        }
        if ((b2 & 0x40) > 0) {
            this.decodeTableItems(this._v.otherNCName);
        }
        if ((b2 & 0x20) > 0) {
            this.decodeTableItems(this._v.otherURI);
        }
        if ((b2 & 0x10) > 0) {
            this.decodeTableItems(this._v.attributeValue);
        }
        if ((b2 & 0x8) > 0) {
            this.decodeTableItems(this._v.characterContentChunk);
        }
        if ((b2 & 0x4) > 0) {
            this.decodeTableItems(this._v.otherString);
        }
        if ((b2 & 0x2) > 0) {
            this.decodeTableItems(this._v.elementName, false);
        }
        if ((b2 & 0x1) > 0) {
            this.decodeTableItems(this._v.attributeName, true);
        }
    }
    
    private void decodeExternalVocabularyURI() throws FastInfosetException, IOException {
        if (this._externalVocabularies == null) {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.noExternalVocabularies"));
        }
        final String externalVocabularyURI = this.decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
        final Object o = this._externalVocabularies.get(externalVocabularyURI);
        if (o instanceof ParserVocabulary) {
            this._v.setReferencedVocabulary(externalVocabularyURI, (ParserVocabulary)o, false);
        }
        else {
            if (!(o instanceof ExternalVocabulary)) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.externalVocabularyNotRegistered", new Object[] { externalVocabularyURI }));
            }
            final ExternalVocabulary v = (ExternalVocabulary)o;
            final ParserVocabulary pv = new ParserVocabulary(v.vocabulary);
            this._externalVocabularies.put(externalVocabularyURI, pv);
            this._v.setReferencedVocabulary(externalVocabularyURI, pv, false);
        }
    }
    
    private void decodeTableItems(final StringArray array) throws FastInfosetException, IOException {
        for (int noOfItems = this.decodeNumberOfItemsOfSequence(), i = 0; i < noOfItems; ++i) {
            array.add(this.decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
        }
    }
    
    private void decodeTableItems(final PrefixArray array) throws FastInfosetException, IOException {
        for (int noOfItems = this.decodeNumberOfItemsOfSequence(), i = 0; i < noOfItems; ++i) {
            array.add(this.decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
        }
    }
    
    private void decodeTableItems(final ContiguousCharArrayArray array) throws FastInfosetException, IOException {
        final int noOfItems = this.decodeNumberOfItemsOfSequence();
        int i = 0;
        while (i < noOfItems) {
            switch (this.decodeNonIdentifyingStringOnFirstBit()) {
                case 0: {
                    array.add(this._charBuffer, this._charBufferLength);
                    ++i;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalState"));
                }
            }
        }
    }
    
    private void decodeTableItems(final CharArrayArray array) throws FastInfosetException, IOException {
        final int noOfItems = this.decodeNumberOfItemsOfSequence();
        int i = 0;
        while (i < noOfItems) {
            switch (this.decodeNonIdentifyingStringOnFirstBit()) {
                case 0: {
                    array.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                    ++i;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalState"));
                }
            }
        }
    }
    
    private void decodeTableItems(final QualifiedNameArray array, final boolean isAttribute) throws FastInfosetException, IOException {
        for (int noOfItems = this.decodeNumberOfItemsOfSequence(), i = 0; i < noOfItems; ++i) {
            final int b = this.read();
            String prefix = "";
            int prefixIndex = -1;
            if ((b & 0x2) > 0) {
                prefixIndex = this.decodeIntegerIndexOnSecondBit();
                prefix = this._v.prefix.get(prefixIndex);
            }
            String namespaceName = "";
            int namespaceNameIndex = -1;
            if ((b & 0x1) > 0) {
                namespaceNameIndex = this.decodeIntegerIndexOnSecondBit();
                namespaceName = this._v.namespaceName.get(namespaceNameIndex);
            }
            if (namespaceName == "" && prefix != "") {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespace"));
            }
            final int localNameIndex = this.decodeIntegerIndexOnSecondBit();
            final String localName = this._v.localName.get(localNameIndex);
            final QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName, prefixIndex, namespaceNameIndex, localNameIndex, this._charBuffer);
            if (isAttribute) {
                qualifiedName.createAttributeValues(256);
            }
            array.add(qualifiedName);
        }
    }
    
    private int decodeNumberOfItemsOfSequence() throws IOException {
        final int b = this.read();
        if (b < 128) {
            return b + 1;
        }
        return ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 129;
    }
    
    protected final void decodeNotations() throws FastInfosetException, IOException {
        if (this._notations == null) {
            this._notations = new ArrayList();
        }
        else {
            this._notations.clear();
        }
        int b;
        for (b = this.read(); (b & 0xFC) == 0xC0; b = this.read()) {
            final String name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
            final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
            final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
            final Notation notation = new Notation(name, system_identifier, public_identifier);
            this._notations.add(notation);
        }
        if (b != 240) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IIsNotTerminatedCorrectly"));
        }
    }
    
    protected final void decodeUnparsedEntities() throws FastInfosetException, IOException {
        if (this._unparsedEntities == null) {
            this._unparsedEntities = new ArrayList();
        }
        else {
            this._unparsedEntities.clear();
        }
        int b;
        for (b = this.read(); (b & 0xFE) == 0xD0; b = this.read()) {
            final String name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
            final String system_identifier = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI);
            final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
            final String notation_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
            final UnparsedEntity unparsedEntity = new UnparsedEntity(name, system_identifier, public_identifier, notation_name);
            this._unparsedEntities.add(unparsedEntity);
        }
        if (b != 240) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unparsedEntities"));
        }
    }
    
    protected final String decodeCharacterEncodingScheme() throws FastInfosetException, IOException {
        return this.decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
    }
    
    protected final String decodeVersion() throws FastInfosetException, IOException {
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                final String data = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(data));
                }
                return data;
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNotSupported"));
            }
            case 1: {
                return this._v.otherString.get(this._integer).toString();
            }
            default: {
                return "";
            }
        }
    }
    
    protected final QualifiedName decodeEIIIndexMedium() throws FastInfosetException, IOException {
        final int i = ((this._b & 0x7) << 8 | this.read()) + 32;
        return this._v.elementName._array[i];
    }
    
    protected final QualifiedName decodeEIIIndexLarge() throws FastInfosetException, IOException {
        int i;
        if ((this._b & 0x30) == 0x20) {
            i = ((this._b & 0x7) << 16 | this.read() << 8 | this.read()) + 2080;
        }
        else {
            i = ((this.read() & 0xF) << 16 | this.read() << 8 | this.read()) + 526368;
        }
        return this._v.elementName._array[i];
    }
    
    protected final QualifiedName decodeLiteralQualifiedName(final int state, QualifiedName q) throws FastInfosetException, IOException {
        if (q == null) {
            q = new QualifiedName();
        }
        switch (state) {
            case 0: {
                return q.set("", "", this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
            }
            case 1: {
                return q.set("", this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
            }
        }
    }
    
    protected final int decodeNonIdentifyingStringOnFirstBit() throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.NISTRING(b)) {
            case 0: {
                this._addToTable = ((b & 0x40) > 0);
                this._octetBufferLength = (b & 0x7) + 1;
                this.decodeUtf8StringAsCharBuffer();
                return 0;
            }
            case 1: {
                this._addToTable = ((b & 0x40) > 0);
                this._octetBufferLength = this.read() + 9;
                this.decodeUtf8StringAsCharBuffer();
                return 0;
            }
            case 2: {
                this._addToTable = ((b & 0x40) > 0);
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 265;
                this.decodeUtf8StringAsCharBuffer();
                return 0;
            }
            case 3: {
                this._addToTable = ((b & 0x40) > 0);
                this._octetBufferLength = (b & 0x7) + 1;
                this.decodeUtf16StringAsCharBuffer();
                return 0;
            }
            case 4: {
                this._addToTable = ((b & 0x40) > 0);
                this._octetBufferLength = this.read() + 9;
                this.decodeUtf16StringAsCharBuffer();
                return 0;
            }
            case 5: {
                this._addToTable = ((b & 0x40) > 0);
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 265;
                this.decodeUtf16StringAsCharBuffer();
                return 0;
            }
            case 6: {
                this._addToTable = ((b & 0x40) > 0);
                this._identifier = (b & 0xF) << 4;
                final int b2 = this.read();
                this._identifier |= (b2 & 0xF0) >> 4;
                this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b2);
                this.decodeRestrictedAlphabetAsCharBuffer();
                return 0;
            }
            case 7: {
                this._addToTable = ((b & 0x40) > 0);
                this._identifier = (b & 0xF) << 4;
                final int b2 = this.read();
                this._identifier |= (b2 & 0xF0) >> 4;
                this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b2);
                return 2;
            }
            case 8: {
                this._integer = (b & 0x3F);
                return 1;
            }
            case 9: {
                this._integer = ((b & 0x1F) << 8 | this.read()) + 64;
                return 1;
            }
            case 10: {
                this._integer = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return 1;
            }
            case 11: {
                return 3;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNonIdentifyingString"));
            }
        }
    }
    
    protected final void decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(int b) throws FastInfosetException, IOException {
        b &= 0xF;
        switch (DecoderStateTables.NISTRING(b)) {
            case 0: {
                this._octetBufferLength = b + 1;
                break;
            }
            case 1: {
                this._octetBufferLength = this.read() + 9;
                break;
            }
            case 2: {
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 265;
                break;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingOctets"));
            }
        }
        this.ensureOctetBufferSize();
        this._octetBufferStart = this._octetBufferOffset;
        this._octetBufferOffset += this._octetBufferLength;
    }
    
    protected final void decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(final int b) throws FastInfosetException, IOException {
        switch (b & 0x3) {
            case 0: {
                this._octetBufferLength = 1;
                break;
            }
            case 1: {
                this._octetBufferLength = 2;
                break;
            }
            case 2: {
                this._octetBufferLength = this.read() + 3;
                break;
            }
            case 3: {
                this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read());
                this._octetBufferLength += 259;
                break;
            }
        }
        this.ensureOctetBufferSize();
        this._octetBufferStart = this._octetBufferOffset;
        this._octetBufferOffset += this._octetBufferLength;
    }
    
    protected final String decodeIdentifyingNonEmptyStringOnFirstBit(final StringArray table) throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING(b)) {
            case 0: {
                this._octetBufferLength = b + 1;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._identifier = table.add(s) - 1;
                return s;
            }
            case 1: {
                this._octetBufferLength = this.read() + 65;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._identifier = table.add(s) - 1;
                return s;
            }
            case 2: {
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 321;
                final String s2 = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._identifier = table.add(s2) - 1;
                return s2;
            }
            case 3: {
                this._identifier = (b & 0x3F);
                return table._array[this._identifier];
            }
            case 4: {
                this._identifier = ((b & 0x1F) << 8 | this.read()) + 64;
                return table._array[this._identifier];
            }
            case 5: {
                this._identifier = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return table._array[this._identifier];
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingString"));
            }
        }
    }
    
    protected final String decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(final boolean namespaceNamePresent) throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(b)) {
            case 6: {
                this._octetBufferLength = EncodingConstants.XML_NAMESPACE_PREFIX_LENGTH;
                this.decodeUtf8StringAsCharBuffer();
                if (this._charBuffer[0] == 'x' && this._charBuffer[1] == 'm' && this._charBuffer[2] == 'l') {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.prefixIllegal"));
                }
                final String s = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
                this._prefixIndex = this._v.prefix.add(s);
                return s;
            }
            case 7: {
                this._octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_PREFIX_LENGTH;
                this.decodeUtf8StringAsCharBuffer();
                if (this._charBuffer[0] == 'x' && this._charBuffer[1] == 'm' && this._charBuffer[2] == 'l' && this._charBuffer[3] == 'n' && this._charBuffer[4] == 's') {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.xmlns"));
                }
                final String s = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
                this._prefixIndex = this._v.prefix.add(s);
                return s;
            }
            case 0:
            case 8:
            case 9: {
                this._octetBufferLength = b + 1;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._prefixIndex = this._v.prefix.add(s);
                return s;
            }
            case 1: {
                this._octetBufferLength = this.read() + 65;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._prefixIndex = this._v.prefix.add(s);
                return s;
            }
            case 2: {
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 321;
                final String s2 = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._prefixIndex = this._v.prefix.add(s2);
                return s2;
            }
            case 10: {
                if (!namespaceNamePresent) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespaceName"));
                }
                this._prefixIndex = 0;
                if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(this.peek()) != 10) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.wrongNamespaceName"));
                }
                return "xml";
            }
            case 3: {
                this._prefixIndex = (b & 0x3F);
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            case 4: {
                this._prefixIndex = ((b & 0x1F) << 8 | this.read()) + 64;
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            case 5: {
                this._prefixIndex = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingStringForPrefix"));
            }
        }
    }
    
    protected final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(final boolean namespaceNamePresent) throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(b)) {
            case 10: {
                if (!namespaceNamePresent) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.missingNamespaceName"));
                }
                this._prefixIndex = 0;
                if (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(this.peek()) != 10) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.wrongNamespaceName"));
                }
                return "xml";
            }
            case 3: {
                this._prefixIndex = (b & 0x3F);
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            case 4: {
                this._prefixIndex = ((b & 0x1F) << 8 | this.read()) + 64;
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            case 5: {
                this._prefixIndex = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return this._v.prefix._array[this._prefixIndex - 1];
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIdentifyingStringForPrefix"));
            }
        }
    }
    
    protected final String decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(final boolean prefixPresent) throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(b)) {
            case 0:
            case 6:
            case 7: {
                this._octetBufferLength = b + 1;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._namespaceNameIndex = this._v.namespaceName.add(s);
                return s;
            }
            case 8: {
                this._octetBufferLength = EncodingConstants.XMLNS_NAMESPACE_NAME_LENGTH;
                this.decodeUtf8StringAsCharBuffer();
                if (this.compareCharsWithCharBufferFromEndToStart(Decoder.XMLNS_NAMESPACE_NAME_CHARS)) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.xmlnsConnotBeBoundToPrefix"));
                }
                final String s = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
                this._namespaceNameIndex = this._v.namespaceName.add(s);
                return s;
            }
            case 9: {
                this._octetBufferLength = EncodingConstants.XML_NAMESPACE_NAME_LENGTH;
                this.decodeUtf8StringAsCharBuffer();
                if (this.compareCharsWithCharBufferFromEndToStart(Decoder.XML_NAMESPACE_NAME_CHARS)) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.illegalNamespaceName"));
                }
                final String s = this._stringInterning ? new String(this._charBuffer, 0, this._charBufferLength).intern() : new String(this._charBuffer, 0, this._charBufferLength);
                this._namespaceNameIndex = this._v.namespaceName.add(s);
                return s;
            }
            case 1: {
                this._octetBufferLength = this.read() + 65;
                final String s = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._namespaceNameIndex = this._v.namespaceName.add(s);
                return s;
            }
            case 2: {
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 321;
                final String s2 = this._stringInterning ? this.decodeUtf8StringAsString().intern() : this.decodeUtf8StringAsString();
                this._namespaceNameIndex = this._v.namespaceName.add(s2);
                return s2;
            }
            case 10: {
                if (prefixPresent) {
                    this._namespaceNameIndex = 0;
                    return "http://www.w3.org/XML/1998/namespace";
                }
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.namespaceWithoutPrefix"));
            }
            case 3: {
                this._namespaceNameIndex = (b & 0x3F);
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            case 4: {
                this._namespaceNameIndex = ((b & 0x1F) << 8 | this.read()) + 64;
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            case 5: {
                this._namespaceNameIndex = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingForNamespaceName"));
            }
        }
    }
    
    protected final String decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(final boolean prefixPresent) throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING_PREFIX_NAMESPACE(b)) {
            case 10: {
                if (prefixPresent) {
                    this._namespaceNameIndex = 0;
                    return "http://www.w3.org/XML/1998/namespace";
                }
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.namespaceWithoutPrefix"));
            }
            case 3: {
                this._namespaceNameIndex = (b & 0x3F);
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            case 4: {
                this._namespaceNameIndex = ((b & 0x1F) << 8 | this.read()) + 64;
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            case 5: {
                this._namespaceNameIndex = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                return this._v.namespaceName._array[this._namespaceNameIndex - 1];
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingForNamespaceName"));
            }
        }
    }
    
    private boolean compareCharsWithCharBufferFromEndToStart(final char[] c) {
        int i = this._charBufferLength;
        while (--i >= 0) {
            if (c[i] != this._charBuffer[i]) {
                return false;
            }
        }
        return true;
    }
    
    protected final String decodeNonEmptyOctetStringOnSecondBitAsUtf8String() throws FastInfosetException, IOException {
        this.decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    protected final void decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray() throws FastInfosetException, IOException {
        this.decodeNonEmptyOctetStringLengthOnSecondBit();
        this.decodeUtf8StringAsCharBuffer();
    }
    
    protected final void decodeNonEmptyOctetStringLengthOnSecondBit() throws FastInfosetException, IOException {
        final int b = this.read();
        switch (DecoderStateTables.ISTRING(b)) {
            case 0: {
                this._octetBufferLength = b + 1;
                break;
            }
            case 1: {
                this._octetBufferLength = this.read() + 65;
                break;
            }
            case 2: {
                final int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                this._octetBufferLength = length + 321;
                break;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingNonEmptyOctet"));
            }
        }
    }
    
    protected final int decodeIntegerIndexOnSecondBit() throws FastInfosetException, IOException {
        final int b = this.read() | 0x80;
        switch (DecoderStateTables.ISTRING(b)) {
            case 3: {
                return b & 0x3F;
            }
            case 4: {
                return ((b & 0x1F) << 8 | this.read()) + 64;
            }
            case 5: {
                return ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingIndexOnSecondBit"));
            }
        }
    }
    
    protected final void decodeHeader() throws FastInfosetException, IOException {
        if (!this._isFastInfosetDocument()) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.notFIDocument"));
        }
    }
    
    protected final void decodeRestrictedAlphabetAsCharBuffer() throws FastInfosetException, IOException {
        if (this._identifier <= 1) {
            this.decodeFourBitAlphabetOctetsAsCharBuffer(BuiltInRestrictedAlphabets.table[this._identifier]);
        }
        else {
            if (this._identifier < 32) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetIdentifiersReserved"));
            }
            final CharArray ca = this._v.restrictedAlphabet.get(this._identifier - 32);
            if (ca == null) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetNotPresent", new Object[] { this._identifier }));
            }
            this.decodeAlphabetOctetsAsCharBuffer(ca.ch);
        }
    }
    
    protected final String decodeRestrictedAlphabetAsString() throws FastInfosetException, IOException {
        this.decodeRestrictedAlphabetAsCharBuffer();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    protected final String decodeRAOctetsAsString(final char[] restrictedAlphabet) throws FastInfosetException, IOException {
        this.decodeAlphabetOctetsAsCharBuffer(restrictedAlphabet);
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    protected final void decodeFourBitAlphabetOctetsAsCharBuffer(final char[] restrictedAlphabet) throws FastInfosetException, IOException {
        this._charBufferLength = 0;
        final int characters = this._octetBufferLength * 2;
        if (this._charBuffer.length < characters) {
            this._charBuffer = new char[characters];
        }
        int v = 0;
        for (int i = 0; i < this._octetBufferLength - 1; ++i) {
            v = (this._octetBuffer[this._octetBufferStart++] & 0xFF);
            this._charBuffer[this._charBufferLength++] = restrictedAlphabet[v >> 4];
            this._charBuffer[this._charBufferLength++] = restrictedAlphabet[v & 0xF];
        }
        v = (this._octetBuffer[this._octetBufferStart++] & 0xFF);
        this._charBuffer[this._charBufferLength++] = restrictedAlphabet[v >> 4];
        v &= 0xF;
        if (v != 15) {
            this._charBuffer[this._charBufferLength++] = restrictedAlphabet[v & 0xF];
        }
    }
    
    protected final void decodeAlphabetOctetsAsCharBuffer(final char[] restrictedAlphabet) throws FastInfosetException, IOException {
        if (restrictedAlphabet.length < 2) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.alphabetMustContain2orMoreChars"));
        }
        int bitsPerCharacter;
        for (bitsPerCharacter = 1; 1 << bitsPerCharacter <= restrictedAlphabet.length; ++bitsPerCharacter) {}
        final int terminatingValue = (1 << bitsPerCharacter) - 1;
        final int characters = (this._octetBufferLength << 3) / bitsPerCharacter;
        if (characters == 0) {
            throw new IOException("");
        }
        this._charBufferLength = 0;
        if (this._charBuffer.length < characters) {
            this._charBuffer = new char[characters];
        }
        this.resetBits();
        int i = 0;
        while (i < characters) {
            final int value = this.readBits(bitsPerCharacter);
            if (bitsPerCharacter < 8 && value == terminatingValue) {
                final int octetPosition = i * bitsPerCharacter >>> 3;
                if (octetPosition != this._octetBufferLength - 1) {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.alphabetIncorrectlyTerminated"));
                }
                break;
            }
            else {
                this._charBuffer[this._charBufferLength++] = restrictedAlphabet[value];
                ++i;
            }
        }
    }
    
    private void resetBits() {
        this._bitsLeftInOctet = 0;
    }
    
    private int readBits(int bits) throws IOException {
        int value;
        int bit;
        for (value = 0; bits > 0; value |= bit << --bits) {
            if (this._bitsLeftInOctet == 0) {
                this._b = (this._octetBuffer[this._octetBufferStart++] & 0xFF);
                this._bitsLeftInOctet = 8;
            }
            final int b = this._b;
            final int n = 1;
            final int bitsLeftInOctet = this._bitsLeftInOctet - 1;
            this._bitsLeftInOctet = bitsLeftInOctet;
            bit = (((b & n << bitsLeftInOctet) > 0) ? 1 : 0);
        }
        return value;
    }
    
    protected final void decodeUtf8StringAsCharBuffer() throws IOException {
        this.ensureOctetBufferSize();
        this.decodeUtf8StringIntoCharBuffer();
    }
    
    protected final void decodeUtf8StringAsCharBuffer(final char[] ch, final int offset) throws IOException {
        this.ensureOctetBufferSize();
        this.decodeUtf8StringIntoCharBuffer(ch, offset);
    }
    
    protected final String decodeUtf8StringAsString() throws IOException {
        this.decodeUtf8StringAsCharBuffer();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    protected final void decodeUtf16StringAsCharBuffer() throws IOException {
        this.ensureOctetBufferSize();
        this.decodeUtf16StringIntoCharBuffer();
    }
    
    protected final String decodeUtf16StringAsString() throws IOException {
        this.decodeUtf16StringAsCharBuffer();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }
    
    private void ensureOctetBufferSize() throws IOException {
        if (this._octetBufferEnd < this._octetBufferOffset + this._octetBufferLength) {
            final int octetsInBuffer = this._octetBufferEnd - this._octetBufferOffset;
            if (this._octetBuffer.length < this._octetBufferLength) {
                final byte[] newOctetBuffer = new byte[this._octetBufferLength];
                System.arraycopy(this._octetBuffer, this._octetBufferOffset, newOctetBuffer, 0, octetsInBuffer);
                this._octetBuffer = newOctetBuffer;
            }
            else {
                System.arraycopy(this._octetBuffer, this._octetBufferOffset, this._octetBuffer, 0, octetsInBuffer);
            }
            this._octetBufferOffset = 0;
            final int octetsRead = this._s.read(this._octetBuffer, octetsInBuffer, this._octetBuffer.length - octetsInBuffer);
            if (octetsRead < 0) {
                throw new EOFException("Unexpeceted EOF");
            }
            this._octetBufferEnd = octetsInBuffer + octetsRead;
            if (this._octetBufferEnd < this._octetBufferLength) {
                this.repeatedRead();
            }
        }
    }
    
    private void repeatedRead() throws IOException {
        while (this._octetBufferEnd < this._octetBufferLength) {
            final int octetsRead = this._s.read(this._octetBuffer, this._octetBufferEnd, this._octetBuffer.length - this._octetBufferEnd);
            if (octetsRead < 0) {
                throw new EOFException("Unexpeceted EOF");
            }
            this._octetBufferEnd += octetsRead;
        }
    }
    
    protected final void decodeUtf8StringIntoCharBuffer() throws IOException {
        if (this._charBuffer.length < this._octetBufferLength) {
            this._charBuffer = new char[this._octetBufferLength];
        }
        this._charBufferLength = 0;
        final int end = this._octetBufferLength + this._octetBufferOffset;
        while (end != this._octetBufferOffset) {
            final int b1 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
            if (DecoderStateTables.UTF8(b1) == 1) {
                this._charBuffer[this._charBufferLength++] = (char)b1;
            }
            else {
                this.decodeTwoToFourByteUtf8Character(b1, end);
            }
        }
    }
    
    protected final void decodeUtf8StringIntoCharBuffer(final char[] ch, final int offset) throws IOException {
        this._charBufferLength = offset;
        final int end = this._octetBufferLength + this._octetBufferOffset;
        while (end != this._octetBufferOffset) {
            final int b1 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
            if (DecoderStateTables.UTF8(b1) == 1) {
                ch[this._charBufferLength++] = (char)b1;
            }
            else {
                this.decodeTwoToFourByteUtf8Character(ch, b1, end);
            }
        }
        this._charBufferLength -= offset;
    }
    
    private void decodeTwoToFourByteUtf8Character(final int b1, final int end) throws IOException {
        switch (DecoderStateTables.UTF8(b1)) {
            case 2: {
                if (end == this._octetBufferOffset) {
                    this.decodeUtf8StringLengthTooSmall();
                }
                final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    this.decodeUtf8StringIllegalState();
                }
                this._charBuffer[this._charBufferLength++] = (char)((b1 & 0x1F) << 6 | (b2 & 0x3F));
                break;
            }
            case 3: {
                final char c = this.decodeUtf8ThreeByteChar(end, b1);
                if (XMLChar.isContent(c)) {
                    this._charBuffer[this._charBufferLength++] = c;
                    break;
                }
                this.decodeUtf8StringIllegalState();
                break;
            }
            case 4: {
                final int supplemental = this.decodeUtf8FourByteChar(end, b1);
                if (XMLChar.isContent(supplemental)) {
                    this._charBuffer[this._charBufferLength++] = this._utf8_highSurrogate;
                    this._charBuffer[this._charBufferLength++] = this._utf8_lowSurrogate;
                    break;
                }
                this.decodeUtf8StringIllegalState();
                break;
            }
            default: {
                this.decodeUtf8StringIllegalState();
                break;
            }
        }
    }
    
    private void decodeTwoToFourByteUtf8Character(final char[] ch, final int b1, final int end) throws IOException {
        switch (DecoderStateTables.UTF8(b1)) {
            case 2: {
                if (end == this._octetBufferOffset) {
                    this.decodeUtf8StringLengthTooSmall();
                }
                final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    this.decodeUtf8StringIllegalState();
                }
                ch[this._charBufferLength++] = (char)((b1 & 0x1F) << 6 | (b2 & 0x3F));
                break;
            }
            case 3: {
                final char c = this.decodeUtf8ThreeByteChar(end, b1);
                if (XMLChar.isContent(c)) {
                    ch[this._charBufferLength++] = c;
                    break;
                }
                this.decodeUtf8StringIllegalState();
                break;
            }
            case 4: {
                final int supplemental = this.decodeUtf8FourByteChar(end, b1);
                if (XMLChar.isContent(supplemental)) {
                    ch[this._charBufferLength++] = this._utf8_highSurrogate;
                    ch[this._charBufferLength++] = this._utf8_lowSurrogate;
                    break;
                }
                this.decodeUtf8StringIllegalState();
                break;
            }
            default: {
                this.decodeUtf8StringIllegalState();
                break;
            }
        }
    }
    
    protected final void decodeUtf8NCNameIntoCharBuffer() throws IOException {
        this._charBufferLength = 0;
        if (this._charBuffer.length < this._octetBufferLength) {
            this._charBuffer = new char[this._octetBufferLength];
        }
        final int end = this._octetBufferLength + this._octetBufferOffset;
        int b1 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if (DecoderStateTables.UTF8_NCNAME(b1) == 0) {
            this._charBuffer[this._charBufferLength++] = (char)b1;
        }
        else {
            this.decodeUtf8NCNameStartTwoToFourByteCharacters(b1, end);
        }
        while (end != this._octetBufferOffset) {
            b1 = (this._octetBuffer[this._octetBufferOffset++] & 0xFF);
            if (DecoderStateTables.UTF8_NCNAME(b1) < 2) {
                this._charBuffer[this._charBufferLength++] = (char)b1;
            }
            else {
                this.decodeUtf8NCNameTwoToFourByteCharacters(b1, end);
            }
        }
    }
    
    private void decodeUtf8NCNameStartTwoToFourByteCharacters(final int b1, final int end) throws IOException {
        switch (DecoderStateTables.UTF8_NCNAME(b1)) {
            case 2: {
                if (end == this._octetBufferOffset) {
                    this.decodeUtf8StringLengthTooSmall();
                }
                final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    this.decodeUtf8StringIllegalState();
                }
                final char c = (char)((b1 & 0x1F) << 6 | (b2 & 0x3F));
                if (XMLChar.isNCNameStart(c)) {
                    this._charBuffer[this._charBufferLength++] = c;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            case 3: {
                final char c2 = this.decodeUtf8ThreeByteChar(end, b1);
                if (XMLChar.isNCNameStart(c2)) {
                    this._charBuffer[this._charBufferLength++] = c2;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            case 4: {
                final int supplemental = this.decodeUtf8FourByteChar(end, b1);
                if (XMLChar.isNCNameStart(supplemental)) {
                    this._charBuffer[this._charBufferLength++] = this._utf8_highSurrogate;
                    this._charBuffer[this._charBufferLength++] = this._utf8_lowSurrogate;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            default: {
                this.decodeUtf8NCNameIllegalState();
                break;
            }
        }
    }
    
    private void decodeUtf8NCNameTwoToFourByteCharacters(final int b1, final int end) throws IOException {
        switch (DecoderStateTables.UTF8_NCNAME(b1)) {
            case 2: {
                if (end == this._octetBufferOffset) {
                    this.decodeUtf8StringLengthTooSmall();
                }
                final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
                if ((b2 & 0xC0) != 0x80) {
                    this.decodeUtf8StringIllegalState();
                }
                final char c = (char)((b1 & 0x1F) << 6 | (b2 & 0x3F));
                if (XMLChar.isNCName(c)) {
                    this._charBuffer[this._charBufferLength++] = c;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            case 3: {
                final char c2 = this.decodeUtf8ThreeByteChar(end, b1);
                if (XMLChar.isNCName(c2)) {
                    this._charBuffer[this._charBufferLength++] = c2;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            case 4: {
                final int supplemental = this.decodeUtf8FourByteChar(end, b1);
                if (XMLChar.isNCName(supplemental)) {
                    this._charBuffer[this._charBufferLength++] = this._utf8_highSurrogate;
                    this._charBuffer[this._charBufferLength++] = this._utf8_lowSurrogate;
                    break;
                }
                this.decodeUtf8NCNameIllegalState();
                break;
            }
            default: {
                this.decodeUtf8NCNameIllegalState();
                break;
            }
        }
    }
    
    private char decodeUtf8ThreeByteChar(final int end, final int b1) throws IOException {
        if (end == this._octetBufferOffset) {
            this.decodeUtf8StringLengthTooSmall();
        }
        final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if ((b2 & 0xC0) != 0x80 || (b1 == 237 && b2 >= 160) || ((b1 & 0xF) == 0x0 && (b2 & 0x20) == 0x0)) {
            this.decodeUtf8StringIllegalState();
        }
        if (end == this._octetBufferOffset) {
            this.decodeUtf8StringLengthTooSmall();
        }
        final int b3 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if ((b3 & 0xC0) != 0x80) {
            this.decodeUtf8StringIllegalState();
        }
        return (char)((b1 & 0xF) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x3F));
    }
    
    private int decodeUtf8FourByteChar(final int end, final int b1) throws IOException {
        if (end == this._octetBufferOffset) {
            this.decodeUtf8StringLengthTooSmall();
        }
        final int b2 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if ((b2 & 0xC0) != 0x80 || ((b2 & 0x30) == 0x0 && (b1 & 0x7) == 0x0)) {
            this.decodeUtf8StringIllegalState();
        }
        if (end == this._octetBufferOffset) {
            this.decodeUtf8StringLengthTooSmall();
        }
        final int b3 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if ((b3 & 0xC0) != 0x80) {
            this.decodeUtf8StringIllegalState();
        }
        if (end == this._octetBufferOffset) {
            this.decodeUtf8StringLengthTooSmall();
        }
        final int b4 = this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        if ((b4 & 0xC0) != 0x80) {
            this.decodeUtf8StringIllegalState();
        }
        final int uuuuu = (b1 << 2 & 0x1C) | (b2 >> 4 & 0x3);
        if (uuuuu > 16) {
            this.decodeUtf8StringIllegalState();
        }
        final int wwww = uuuuu - 1;
        this._utf8_highSurrogate = (char)(0xD800 | (wwww << 6 & 0x3C0) | (b2 << 2 & 0x3C) | (b3 >> 4 & 0x3));
        this._utf8_lowSurrogate = (char)(0xDC00 | (b3 << 6 & 0x3C0) | (b4 & 0x3F));
        return XMLChar.supplemental(this._utf8_highSurrogate, this._utf8_lowSurrogate);
    }
    
    private void decodeUtf8StringLengthTooSmall() throws IOException {
        throw new IOException(CommonResourceBundle.getInstance().getString("message.deliminatorTooSmall"));
    }
    
    private void decodeUtf8StringIllegalState() throws IOException {
        throw new IOException(CommonResourceBundle.getInstance().getString("message.UTF8Encoded"));
    }
    
    private void decodeUtf8NCNameIllegalState() throws IOException {
        throw new IOException(CommonResourceBundle.getInstance().getString("message.UTF8EncodedNCName"));
    }
    
    private void decodeUtf16StringIntoCharBuffer() throws IOException {
        this._charBufferLength = this._octetBufferLength / 2;
        if (this._charBuffer.length < this._charBufferLength) {
            this._charBuffer = new char[this._charBufferLength];
        }
        for (int i = 0; i < this._charBufferLength; ++i) {
            final char c = (char)(this.read() << 8 | this.read());
            this._charBuffer[i] = c;
        }
    }
    
    protected String createQualifiedNameString(final String second) {
        return this.createQualifiedNameString(Decoder.XMLNS_NAMESPACE_PREFIX_CHARS, second);
    }
    
    protected String createQualifiedNameString(final char[] first, final String second) {
        final int l1 = first.length;
        final int l2 = second.length();
        final int total = l1 + l2 + 1;
        if (total < this._charBuffer.length) {
            System.arraycopy(first, 0, this._charBuffer, 0, l1);
            this._charBuffer[l1] = ':';
            second.getChars(0, l2, this._charBuffer, l1 + 1);
            return new String(this._charBuffer, 0, total);
        }
        final StringBuilder b = new StringBuilder(new String(first));
        b.append(':');
        b.append(second);
        return b.toString();
    }
    
    protected final int read() throws IOException {
        if (this._octetBufferOffset < this._octetBufferEnd) {
            return this._octetBuffer[this._octetBufferOffset++] & 0xFF;
        }
        this._octetBufferEnd = this._s.read(this._octetBuffer);
        if (this._octetBufferEnd < 0) {
            throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
        }
        this._octetBufferOffset = 1;
        return this._octetBuffer[0] & 0xFF;
    }
    
    protected final void closeIfRequired() throws IOException {
        if (this._s != null && this._needForceStreamClose) {
            this._s.close();
        }
    }
    
    protected final int peek() throws IOException {
        return this.peek(null);
    }
    
    protected final int peek(final OctetBufferListener octetBufferListener) throws IOException {
        if (this._octetBufferOffset < this._octetBufferEnd) {
            return this._octetBuffer[this._octetBufferOffset] & 0xFF;
        }
        if (octetBufferListener != null) {
            octetBufferListener.onBeforeOctetBufferOverwrite();
        }
        this._octetBufferEnd = this._s.read(this._octetBuffer);
        if (this._octetBufferEnd < 0) {
            throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
        }
        this._octetBufferOffset = 0;
        return this._octetBuffer[0] & 0xFF;
    }
    
    protected final int peek2(final OctetBufferListener octetBufferListener) throws IOException {
        if (this._octetBufferOffset + 1 < this._octetBufferEnd) {
            return this._octetBuffer[this._octetBufferOffset + 1] & 0xFF;
        }
        if (octetBufferListener != null) {
            octetBufferListener.onBeforeOctetBufferOverwrite();
        }
        int offset = 0;
        if (this._octetBufferOffset < this._octetBufferEnd) {
            this._octetBuffer[0] = this._octetBuffer[this._octetBufferOffset];
            offset = 1;
        }
        this._octetBufferEnd = this._s.read(this._octetBuffer, offset, this._octetBuffer.length - offset);
        if (this._octetBufferEnd < 0) {
            throw new EOFException(CommonResourceBundle.getInstance().getString("message.EOF"));
        }
        this._octetBufferOffset = 0;
        return this._octetBuffer[1] & 0xFF;
    }
    
    protected final boolean _isFastInfosetDocument() throws IOException {
        this.peek();
        this._octetBufferLength = EncodingConstants.BINARY_HEADER.length;
        this.ensureOctetBufferSize();
        this._octetBufferOffset += this._octetBufferLength;
        if (this._octetBuffer[0] != EncodingConstants.BINARY_HEADER[0] || this._octetBuffer[1] != EncodingConstants.BINARY_HEADER[1] || this._octetBuffer[2] != EncodingConstants.BINARY_HEADER[2] || this._octetBuffer[3] != EncodingConstants.BINARY_HEADER[3]) {
            for (int i = 0; i < EncodingConstants.XML_DECLARATION_VALUES.length; ++i) {
                this._octetBufferLength = EncodingConstants.XML_DECLARATION_VALUES[i].length - this._octetBufferOffset;
                this.ensureOctetBufferSize();
                this._octetBufferOffset += this._octetBufferLength;
                if (this.arrayEquals(this._octetBuffer, 0, EncodingConstants.XML_DECLARATION_VALUES[i], EncodingConstants.XML_DECLARATION_VALUES[i].length)) {
                    this._octetBufferLength = EncodingConstants.BINARY_HEADER.length;
                    this.ensureOctetBufferSize();
                    return this._octetBuffer[this._octetBufferOffset++] == EncodingConstants.BINARY_HEADER[0] && this._octetBuffer[this._octetBufferOffset++] == EncodingConstants.BINARY_HEADER[1] && this._octetBuffer[this._octetBufferOffset++] == EncodingConstants.BINARY_HEADER[2] && this._octetBuffer[this._octetBufferOffset++] == EncodingConstants.BINARY_HEADER[3];
                }
            }
            return false;
        }
        return true;
    }
    
    private boolean arrayEquals(final byte[] b1, final int offset, final byte[] b2, final int length) {
        for (int i = 0; i < length; ++i) {
            if (b1[offset + i] != b2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isFastInfosetDocument(final InputStream s) throws IOException {
        final int headerSize = 4;
        final byte[] header = new byte[4];
        final int readBytesCount = s.read(header);
        return readBytesCount >= 4 && header[0] == EncodingConstants.BINARY_HEADER[0] && header[1] == EncodingConstants.BINARY_HEADER[1] && header[2] == EncodingConstants.BINARY_HEADER[2] && header[3] == EncodingConstants.BINARY_HEADER[3];
    }
    
    static {
        XML_NAMESPACE_NAME_CHARS = "http://www.w3.org/XML/1998/namespace".toCharArray();
        XMLNS_NAMESPACE_PREFIX_CHARS = "xmlns".toCharArray();
        XMLNS_NAMESPACE_NAME_CHARS = "http://www.w3.org/2000/xmlns/".toCharArray();
        Decoder._stringInterningSystemDefault = false;
        Decoder._bufferSizeSystemDefault = 1024;
        String p = System.getProperty("com.sun.xml.internal.fastinfoset.parser.string-interning", Boolean.toString(Decoder._stringInterningSystemDefault));
        Decoder._stringInterningSystemDefault = Boolean.valueOf(p);
        p = System.getProperty("com.sun.xml.internal.fastinfoset.parser.buffer-size", Integer.toString(Decoder._bufferSizeSystemDefault));
        try {
            final int i = Integer.valueOf(p);
            if (i > 0) {
                Decoder._bufferSizeSystemDefault = i;
            }
        }
        catch (final NumberFormatException ex) {}
    }
    
    protected class EncodingAlgorithmInputStream extends InputStream
    {
        @Override
        public int read() throws IOException {
            if (Decoder.this._octetBufferStart < Decoder.this._octetBufferOffset) {
                return Decoder.this._octetBuffer[Decoder.this._octetBufferStart++] & 0xFF;
            }
            return -1;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            final int newOctetBufferStart = Decoder.this._octetBufferStart + len;
            if (newOctetBufferStart < Decoder.this._octetBufferOffset) {
                System.arraycopy(Decoder.this._octetBuffer, Decoder.this._octetBufferStart, b, off, len);
                Decoder.this._octetBufferStart = newOctetBufferStart;
                return len;
            }
            if (Decoder.this._octetBufferStart < Decoder.this._octetBufferOffset) {
                final int bytesToRead = Decoder.this._octetBufferOffset - Decoder.this._octetBufferStart;
                System.arraycopy(Decoder.this._octetBuffer, Decoder.this._octetBufferStart, b, off, bytesToRead);
                final Decoder this$0 = Decoder.this;
                this$0._octetBufferStart += bytesToRead;
                return bytesToRead;
            }
            return -1;
        }
    }
}
