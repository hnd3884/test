package com.sun.xml.internal.fastinfoset;

import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import com.sun.xml.internal.org.jvnet.fastinfoset.ExternalVocabulary;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import java.util.HashMap;
import java.io.OutputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import java.util.Map;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Encoder extends DefaultHandler implements FastInfosetSerializer
{
    public static final String CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme";
    protected static final String _characterEncodingSchemeSystemDefault;
    private static int[] NUMERIC_CHARACTERS_TABLE;
    private static int[] DATE_TIME_CHARACTERS_TABLE;
    private boolean _ignoreDTD;
    private boolean _ignoreComments;
    private boolean _ignoreProcessingInstructions;
    private boolean _ignoreWhiteSpaceTextContent;
    private boolean _useLocalNameAsKeyForQualifiedNameLookup;
    private boolean _encodingStringsAsUtf8;
    private int _nonIdentifyingStringOnThirdBitCES;
    private int _nonIdentifyingStringOnFirstBitCES;
    private Map _registeredEncodingAlgorithms;
    protected SerializerVocabulary _v;
    protected VocabularyApplicationData _vData;
    private boolean _vIsInternal;
    protected boolean _terminate;
    protected int _b;
    protected OutputStream _s;
    protected char[] _charBuffer;
    protected byte[] _octetBuffer;
    protected int _octetBufferIndex;
    protected int _markIndex;
    protected int minAttributeValueSize;
    protected int maxAttributeValueSize;
    protected int attributeValueMapTotalCharactersConstraint;
    protected int minCharacterContentChunkSize;
    protected int maxCharacterContentChunkSize;
    protected int characterContentChunkMapTotalCharactersConstraint;
    private int _bitsLeftInOctet;
    private EncodingBufferOutputStream _encodingBufferOutputStream;
    private byte[] _encodingBuffer;
    private int _encodingBufferIndex;
    
    private static String getDefaultEncodingScheme() {
        final String p = System.getProperty("com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme", "UTF-8");
        if (p.equals("UTF-16BE")) {
            return "UTF-16BE";
        }
        return "UTF-8";
    }
    
    private static int maxCharacter(final String alphabet) {
        int c = 0;
        for (int i = 0; i < alphabet.length(); ++i) {
            if (c < alphabet.charAt(i)) {
                c = alphabet.charAt(i);
            }
        }
        return c;
    }
    
    protected Encoder() {
        this._encodingStringsAsUtf8 = true;
        this._registeredEncodingAlgorithms = new HashMap();
        this._terminate = false;
        this._charBuffer = new char[512];
        this._octetBuffer = new byte[1024];
        this._markIndex = -1;
        this.minAttributeValueSize = 0;
        this.maxAttributeValueSize = 32;
        this.attributeValueMapTotalCharactersConstraint = 1073741823;
        this.minCharacterContentChunkSize = 0;
        this.maxCharacterContentChunkSize = 32;
        this.characterContentChunkMapTotalCharactersConstraint = 1073741823;
        this._encodingBufferOutputStream = new EncodingBufferOutputStream();
        this._encodingBuffer = new byte[512];
        this.setCharacterEncodingScheme(Encoder._characterEncodingSchemeSystemDefault);
    }
    
    protected Encoder(final boolean useLocalNameAsKeyForQualifiedNameLookup) {
        this._encodingStringsAsUtf8 = true;
        this._registeredEncodingAlgorithms = new HashMap();
        this._terminate = false;
        this._charBuffer = new char[512];
        this._octetBuffer = new byte[1024];
        this._markIndex = -1;
        this.minAttributeValueSize = 0;
        this.maxAttributeValueSize = 32;
        this.attributeValueMapTotalCharactersConstraint = 1073741823;
        this.minCharacterContentChunkSize = 0;
        this.maxCharacterContentChunkSize = 32;
        this.characterContentChunkMapTotalCharactersConstraint = 1073741823;
        this._encodingBufferOutputStream = new EncodingBufferOutputStream();
        this._encodingBuffer = new byte[512];
        this.setCharacterEncodingScheme(Encoder._characterEncodingSchemeSystemDefault);
        this._useLocalNameAsKeyForQualifiedNameLookup = useLocalNameAsKeyForQualifiedNameLookup;
    }
    
    @Override
    public final void setIgnoreDTD(final boolean ignoreDTD) {
        this._ignoreDTD = ignoreDTD;
    }
    
    @Override
    public final boolean getIgnoreDTD() {
        return this._ignoreDTD;
    }
    
    @Override
    public final void setIgnoreComments(final boolean ignoreComments) {
        this._ignoreComments = ignoreComments;
    }
    
    @Override
    public final boolean getIgnoreComments() {
        return this._ignoreComments;
    }
    
    @Override
    public final void setIgnoreProcesingInstructions(final boolean ignoreProcesingInstructions) {
        this._ignoreProcessingInstructions = ignoreProcesingInstructions;
    }
    
    @Override
    public final boolean getIgnoreProcesingInstructions() {
        return this._ignoreProcessingInstructions;
    }
    
    @Override
    public final void setIgnoreWhiteSpaceTextContent(final boolean ignoreWhiteSpaceTextContent) {
        this._ignoreWhiteSpaceTextContent = ignoreWhiteSpaceTextContent;
    }
    
    @Override
    public final boolean getIgnoreWhiteSpaceTextContent() {
        return this._ignoreWhiteSpaceTextContent;
    }
    
    @Override
    public void setCharacterEncodingScheme(final String characterEncodingScheme) {
        if (characterEncodingScheme.equals("UTF-16BE")) {
            this._encodingStringsAsUtf8 = false;
            this._nonIdentifyingStringOnThirdBitCES = 132;
            this._nonIdentifyingStringOnFirstBitCES = 16;
        }
        else {
            this._encodingStringsAsUtf8 = true;
            this._nonIdentifyingStringOnThirdBitCES = 128;
            this._nonIdentifyingStringOnFirstBitCES = 0;
        }
    }
    
    @Override
    public String getCharacterEncodingScheme() {
        return this._encodingStringsAsUtf8 ? "UTF-8" : "UTF-16BE";
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
    public int getMinCharacterContentChunkSize() {
        return this.minCharacterContentChunkSize;
    }
    
    @Override
    public void setMinCharacterContentChunkSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.minCharacterContentChunkSize = size;
    }
    
    @Override
    public int getMaxCharacterContentChunkSize() {
        return this.maxCharacterContentChunkSize;
    }
    
    @Override
    public void setMaxCharacterContentChunkSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.maxCharacterContentChunkSize = size;
    }
    
    @Override
    public int getCharacterContentChunkMapMemoryLimit() {
        return this.characterContentChunkMapTotalCharactersConstraint * 2;
    }
    
    @Override
    public void setCharacterContentChunkMapMemoryLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.characterContentChunkMapTotalCharactersConstraint = size / 2;
    }
    
    public boolean isCharacterContentChunkLengthMatchesLimit(final int length) {
        return length >= this.minCharacterContentChunkSize && length < this.maxCharacterContentChunkSize;
    }
    
    public boolean canAddCharacterContentToTable(final int length, final CharArrayIntMap map) {
        return map.getTotalCharacterCount() + length < this.characterContentChunkMapTotalCharactersConstraint;
    }
    
    @Override
    public int getMinAttributeValueSize() {
        return this.minAttributeValueSize;
    }
    
    @Override
    public void setMinAttributeValueSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.minAttributeValueSize = size;
    }
    
    @Override
    public int getMaxAttributeValueSize() {
        return this.maxAttributeValueSize;
    }
    
    @Override
    public void setMaxAttributeValueSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.maxAttributeValueSize = size;
    }
    
    @Override
    public void setAttributeValueMapMemoryLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.attributeValueMapTotalCharactersConstraint = size / 2;
    }
    
    @Override
    public int getAttributeValueMapMemoryLimit() {
        return this.attributeValueMapTotalCharactersConstraint * 2;
    }
    
    public boolean isAttributeValueLengthMatchesLimit(final int length) {
        return length >= this.minAttributeValueSize && length < this.maxAttributeValueSize;
    }
    
    public boolean canAddAttributeToTable(final int length) {
        return this._v.attributeValue.getTotalCharacterCount() + length < this.attributeValueMapTotalCharactersConstraint;
    }
    
    @Override
    public void setExternalVocabulary(final ExternalVocabulary v) {
        this._v = new SerializerVocabulary();
        final SerializerVocabulary ev = new SerializerVocabulary(v.vocabulary, this._useLocalNameAsKeyForQualifiedNameLookup);
        this._v.setExternalVocabulary(v.URI, ev, false);
        this._vIsInternal = true;
    }
    
    @Override
    public void setVocabularyApplicationData(final VocabularyApplicationData data) {
        this._vData = data;
    }
    
    @Override
    public VocabularyApplicationData getVocabularyApplicationData() {
        return this._vData;
    }
    
    @Override
    public void reset() {
        this._terminate = false;
    }
    
    @Override
    public void setOutputStream(final OutputStream s) {
        this._octetBufferIndex = 0;
        this._markIndex = -1;
        this._s = s;
    }
    
    public void setVocabulary(final SerializerVocabulary vocabulary) {
        this._v = vocabulary;
        this._vIsInternal = false;
    }
    
    protected final void encodeHeader(final boolean encodeXmlDecl) throws IOException {
        if (encodeXmlDecl) {
            this._s.write(EncodingConstants.XML_DECLARATION_VALUES[0]);
        }
        this._s.write(EncodingConstants.BINARY_HEADER);
    }
    
    protected final void encodeInitialVocabulary() throws IOException {
        if (this._v == null) {
            this._v = new SerializerVocabulary();
            this._vIsInternal = true;
        }
        else if (this._vIsInternal) {
            this._v.clear();
            if (this._vData != null) {
                this._vData.clear();
            }
        }
        if (!this._v.hasInitialVocabulary() && !this._v.hasExternalVocabulary()) {
            this.write(0);
        }
        else if (this._v.hasInitialVocabulary()) {
            this.write(this._b = 32);
            final SerializerVocabulary initialVocabulary = this._v.getReadOnlyVocabulary();
            if (initialVocabulary.hasExternalVocabulary()) {
                this.write(this._b = 16);
                this.write(0);
            }
            if (initialVocabulary.hasExternalVocabulary()) {
                this.encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI());
            }
        }
        else if (this._v.hasExternalVocabulary()) {
            this.write(this._b = 32);
            this.write(this._b = 16);
            this.write(0);
            this.encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI());
        }
    }
    
    protected final void encodeDocumentTermination() throws IOException {
        this.encodeElementTermination();
        this.encodeTermination();
        this._flush();
        this._s.flush();
    }
    
    protected final void encodeElementTermination() throws IOException {
        this._terminate = true;
        switch (this._b) {
            case 240: {
                this._b = 255;
                return;
            }
            case 255: {
                this.write(255);
                break;
            }
        }
        this._b = 240;
    }
    
    protected final void encodeTermination() throws IOException {
        if (this._terminate) {
            this.write(this._b);
            this._b = 0;
            this._terminate = false;
        }
    }
    
    protected final void encodeNamespaceAttribute(final String prefix, final String uri) throws IOException {
        this._b = 204;
        if (prefix.length() > 0) {
            this._b |= 0x2;
        }
        if (uri.length() > 0) {
            this._b |= 0x1;
        }
        this.write(this._b);
        if (prefix.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(prefix, this._v.prefix);
        }
        if (uri.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(uri, this._v.namespaceName);
        }
    }
    
    protected final void encodeCharacters(final char[] ch, final int offset, final int length) throws IOException {
        final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnThirdBit(ch, offset, length, this._v.characterContentChunk, addToTable, true);
    }
    
    protected final void encodeCharactersNoClone(final char[] ch, final int offset, final int length) throws IOException {
        final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnThirdBit(ch, offset, length, this._v.characterContentChunk, addToTable, false);
    }
    
    protected final void encodeNumericFourBitCharacters(final char[] ch, final int offset, final int length, final boolean addToTable) throws FastInfosetException, IOException {
        this.encodeFourBitCharacters(0, Encoder.NUMERIC_CHARACTERS_TABLE, ch, offset, length, addToTable);
    }
    
    protected final void encodeDateTimeFourBitCharacters(final char[] ch, final int offset, final int length, final boolean addToTable) throws FastInfosetException, IOException {
        this.encodeFourBitCharacters(1, Encoder.DATE_TIME_CHARACTERS_TABLE, ch, offset, length, addToTable);
    }
    
    protected final void encodeFourBitCharacters(final int id, final int[] table, final char[] ch, final int offset, final int length, final boolean addToTable) throws FastInfosetException, IOException {
        if (addToTable) {
            final boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, this._v.characterContentChunk);
            final int index = canAddCharacterContentToTable ? this._v.characterContentChunk.obtainIndex(ch, offset, length, true) : this._v.characterContentChunk.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
                return;
            }
            if (canAddCharacterContentToTable) {
                this._b = 152;
            }
            else {
                this._b = 136;
            }
        }
        else {
            this._b = 136;
        }
        this.write(this._b);
        this._b = id << 2;
        this.encodeNonEmptyFourBitCharacterStringOnSeventhBit(table, ch, offset, length);
    }
    
    protected final void encodeAlphabetCharacters(final String alphabet, final char[] ch, final int offset, final int length, final boolean addToTable) throws FastInfosetException, IOException {
        if (addToTable) {
            final boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, this._v.characterContentChunk);
            final int index = canAddCharacterContentToTable ? this._v.characterContentChunk.obtainIndex(ch, offset, length, true) : this._v.characterContentChunk.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
                return;
            }
            if (canAddCharacterContentToTable) {
                this._b = 152;
            }
            else {
                this._b = 136;
            }
        }
        else {
            this._b = 136;
        }
        int id = this._v.restrictedAlphabet.get(alphabet);
        if (id == -1) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.restrictedAlphabetNotPresent"));
        }
        id += 32;
        this.write(this._b |= (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        this.encodeNonEmptyNBitCharacterStringOnSeventhBit(alphabet, ch, offset, length);
    }
    
    protected final void encodeProcessingInstruction(final String target, final String data) throws IOException {
        this.write(225);
        this.encodeIdentifyingNonEmptyStringOnFirstBit(target, this._v.otherNCName);
        final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(data.length());
        this.encodeNonIdentifyingStringOnFirstBit(data, this._v.otherString, addToTable);
    }
    
    protected final void encodeDocumentTypeDeclaration(final String systemId, final String publicId) throws IOException {
        this._b = 196;
        if (systemId != null && systemId.length() > 0) {
            this._b |= 0x2;
        }
        if (publicId != null && publicId.length() > 0) {
            this._b |= 0x1;
        }
        this.write(this._b);
        if (systemId != null && systemId.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(systemId, this._v.otherURI);
        }
        if (publicId != null && publicId.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(publicId, this._v.otherURI);
        }
    }
    
    protected final void encodeComment(final char[] ch, final int offset, final int length) throws IOException {
        this.write(226);
        final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnFirstBit(ch, offset, length, this._v.otherString, addToTable, true);
    }
    
    protected final void encodeCommentNoClone(final char[] ch, final int offset, final int length) throws IOException {
        this.write(226);
        final boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnFirstBit(ch, offset, length, this._v.otherString, addToTable, false);
    }
    
    protected final void encodeElementQualifiedNameOnThirdBit(final String namespaceURI, final String prefix, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix)) && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }
        this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, prefix, localName, entry);
    }
    
    protected final void encodeLiteralElementQualifiedNameOnThirdBit(final String namespaceURI, final String prefix, final String localName, final LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        final QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", this._v.elementName.getNextIndex());
        entry.addQualifiedName(name);
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (namespaceURI.length() > 0) {
            namespaceURIIndex = this._v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == -1) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { namespaceURI }));
            }
            if (prefix.length() > 0) {
                prefixIndex = this._v.prefix.get(prefix);
                if (prefixIndex == -1) {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { prefix }));
                }
            }
        }
        final int localNameIndex = this._v.localName.obtainIndex(localName);
        this._b |= 0x3C;
        if (namespaceURIIndex >= 0) {
            this._b |= 0x1;
            if (prefixIndex >= 0) {
                this._b |= 0x2;
            }
        }
        this.write(this._b);
        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        }
        if (localNameIndex >= 0) {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        }
        else {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        }
    }
    
    protected final void encodeAttributeQualifiedNameOnSecondBit(final String namespaceURI, final String prefix, final String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            final QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix)) && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return;
                }
            }
        }
        this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, prefix, localName, entry);
    }
    
    protected final boolean encodeLiteralAttributeQualifiedNameOnSecondBit(final String namespaceURI, final String prefix, final String localName, final LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (namespaceURI.length() > 0) {
            namespaceURIIndex = this._v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == -1) {
                if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                    return false;
                }
                throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { namespaceURI }));
            }
            else if (prefix.length() > 0) {
                prefixIndex = this._v.prefix.get(prefix);
                if (prefixIndex == -1) {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { prefix }));
                }
            }
        }
        final int localNameIndex = this._v.localName.obtainIndex(localName);
        final QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", this._v.attributeName.getNextIndex());
        entry.addQualifiedName(name);
        this._b = 120;
        if (namespaceURI.length() > 0) {
            this._b |= 0x1;
            if (prefix.length() > 0) {
                this._b |= 0x2;
            }
        }
        this.write(this._b);
        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        }
        else if (namespaceURI != "") {
            this.encodeNonEmptyOctetStringOnSecondBit("xml");
            this.encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
        }
        if (localNameIndex >= 0) {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        }
        else {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        }
        return true;
    }
    
    protected final void encodeNonIdentifyingStringOnFirstBit(final String s, final StringIntMap map, final boolean addToTable, final boolean mustBeAddedToTable) throws IOException {
        if (s == null || s.length() == 0) {
            this.write(255);
        }
        else if (addToTable || mustBeAddedToTable) {
            final boolean canAddAttributeToTable = mustBeAddedToTable || this.canAddAttributeToTable(s.length());
            final int index = canAddAttributeToTable ? map.obtainIndex(s) : map.get(s);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            }
            else if (canAddAttributeToTable) {
                this._b = (0x40 | this._nonIdentifyingStringOnFirstBitCES);
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            }
            else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        }
        else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(s);
        }
    }
    
    protected final void encodeNonIdentifyingStringOnFirstBit(final String s, final CharArrayIntMap map, final boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            this.write(255);
        }
        else if (addToTable) {
            final char[] ch = s.toCharArray();
            final int length = s.length();
            final boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            final int index = canAddCharacterContentToTable ? map.obtainIndex(ch, 0, length, false) : map.get(ch, 0, length);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            }
            else if (canAddCharacterContentToTable) {
                this._b = (0x40 | this._nonIdentifyingStringOnFirstBitCES);
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, 0, length);
            }
            else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        }
        else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(s);
        }
    }
    
    protected final void encodeNonIdentifyingStringOnFirstBit(final char[] ch, final int offset, final int length, final CharArrayIntMap map, final boolean addToTable, final boolean clone) throws IOException {
        if (length == 0) {
            this.write(255);
        }
        else if (addToTable) {
            final boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            final int index = canAddCharacterContentToTable ? map.obtainIndex(ch, offset, length, clone) : map.get(ch, offset, length);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            }
            else if (canAddCharacterContentToTable) {
                this._b = (0x40 | this._nonIdentifyingStringOnFirstBitCES);
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
            }
            else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
            }
        }
        else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
        }
    }
    
    protected final void encodeNumericNonIdentifyingStringOnFirstBit(final String s, final boolean addToTable, final boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        this.encodeNonIdentifyingStringOnFirstBit(0, Encoder.NUMERIC_CHARACTERS_TABLE, s, addToTable, mustBeAddedToTable);
    }
    
    protected final void encodeDateTimeNonIdentifyingStringOnFirstBit(final String s, final boolean addToTable, final boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        this.encodeNonIdentifyingStringOnFirstBit(1, Encoder.DATE_TIME_CHARACTERS_TABLE, s, addToTable, mustBeAddedToTable);
    }
    
    protected final void encodeNonIdentifyingStringOnFirstBit(final int id, final int[] table, final String s, final boolean addToTable, final boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        if (s == null || s.length() == 0) {
            this.write(255);
            return;
        }
        if (addToTable || mustBeAddedToTable) {
            final boolean canAddAttributeToTable = mustBeAddedToTable || this.canAddAttributeToTable(s.length());
            final int index = canAddAttributeToTable ? this._v.attributeValue.obtainIndex(s) : this._v.attributeValue.get(s);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                return;
            }
            if (canAddAttributeToTable) {
                this._b = 96;
            }
            else {
                this._b = 32;
            }
        }
        else {
            this._b = 32;
        }
        this.write(this._b | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        final int length = s.length();
        final int octetPairLength = length / 2;
        final int octetSingleLength = length % 2;
        this.encodeNonZeroOctetStringLengthOnFifthBit(octetPairLength + octetSingleLength);
        this.encodeNonEmptyFourBitCharacterString(table, s.toCharArray(), 0, octetPairLength, octetSingleLength);
    }
    
    protected final void encodeNonIdentifyingStringOnFirstBit(final String URI, int id, final Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { URI }));
            }
            id += 32;
            final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                this.encodeAIIObjectAlgorithmData(id, data, ea);
            }
            else {
                if (!(data instanceof byte[])) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
                }
                final byte[] d = (byte[])data;
                this.encodeAIIOctetAlgorithmData(id, d, 0, d.length);
            }
        }
        else if (id <= 9) {
            int length = 0;
            switch (id) {
                case 0:
                case 1: {
                    length = ((byte[])data).length;
                    break;
                }
                case 2: {
                    length = ((short[])data).length;
                    break;
                }
                case 3: {
                    length = ((int[])data).length;
                    break;
                }
                case 4:
                case 8: {
                    length = ((long[])data).length;
                    break;
                }
                case 5: {
                    length = ((boolean[])data).length;
                    break;
                }
                case 6: {
                    length = ((float[])data).length;
                    break;
                }
                case 7: {
                    length = ((double[])data).length;
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
                }
                default: {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { id }));
                }
            }
            this.encodeAIIBuiltInAlgorithmData(id, data, 0, length);
        }
        else {
            if (id < 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
            if (!(data instanceof byte[])) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
            }
            final byte[] d2 = (byte[])data;
            this.encodeAIIOctetAlgorithmData(id, d2, 0, d2.length);
        }
    }
    
    protected final void encodeAIIOctetAlgorithmData(final int id, final byte[] d, final int offset, final int length) throws IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(d, offset, length);
    }
    
    protected final void encodeAIIObjectAlgorithmData(final int id, final Object data, final EncodingAlgorithm ea) throws FastInfosetException, IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        this._encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, this._encodingBufferOutputStream);
        this.encodeNonZeroOctetStringLengthOnFifthBit(this._encodingBufferIndex);
        this.write(this._encodingBuffer, this._encodingBufferIndex);
    }
    
    protected final void encodeAIIBuiltInAlgorithmData(final int id, final Object data, final int offset, final int length) throws IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        final int octetLength = BuiltInEncodingAlgorithmFactory.getAlgorithm(id).getOctetLengthFromPrimitiveLength(length);
        this.encodeNonZeroOctetStringLengthOnFifthBit(octetLength);
        this.ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(id).encodeToBytes(data, offset, length, this._octetBuffer, this._octetBufferIndex);
        this._octetBufferIndex += octetLength;
    }
    
    protected final void encodeNonIdentifyingStringOnThirdBit(final char[] ch, final int offset, final int length, final CharArrayIntMap map, final boolean addToTable, final boolean clone) throws IOException {
        if (addToTable) {
            final boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            final int index = canAddCharacterContentToTable ? map.obtainIndex(ch, offset, length, clone) : map.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
            }
            else if (canAddCharacterContentToTable) {
                this._b = (0x10 | this._nonIdentifyingStringOnThirdBitCES);
                this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
            }
            else {
                this._b = this._nonIdentifyingStringOnThirdBitCES;
                this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
            }
        }
        else {
            this._b = this._nonIdentifyingStringOnThirdBitCES;
            this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
        }
    }
    
    protected final void encodeNonIdentifyingStringOnThirdBit(final String URI, int id, final Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { URI }));
            }
            id += 32;
            final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                this.encodeCIIObjectAlgorithmData(id, data, ea);
            }
            else {
                if (!(data instanceof byte[])) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
                }
                final byte[] d = (byte[])data;
                this.encodeCIIOctetAlgorithmData(id, d, 0, d.length);
            }
        }
        else if (id <= 9) {
            int length = 0;
            switch (id) {
                case 0:
                case 1: {
                    length = ((byte[])data).length;
                    break;
                }
                case 2: {
                    length = ((short[])data).length;
                    break;
                }
                case 3: {
                    length = ((int[])data).length;
                    break;
                }
                case 4:
                case 8: {
                    length = ((long[])data).length;
                    break;
                }
                case 5: {
                    length = ((boolean[])data).length;
                    break;
                }
                case 6: {
                    length = ((float[])data).length;
                    break;
                }
                case 7: {
                    length = ((double[])data).length;
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
                }
                default: {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { id }));
                }
            }
            this.encodeCIIBuiltInAlgorithmData(id, data, 0, length);
        }
        else {
            if (id < 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
            if (!(data instanceof byte[])) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
            }
            final byte[] d2 = (byte[])data;
            this.encodeCIIOctetAlgorithmData(id, d2, 0, d2.length);
        }
    }
    
    protected final void encodeNonIdentifyingStringOnThirdBit(final String URI, int id, final byte[] d, final int offset, final int length) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { URI }));
            }
            id += 32;
        }
        this.encodeCIIOctetAlgorithmData(id, d, offset, length);
    }
    
    protected final void encodeCIIOctetAlgorithmData(final int id, final byte[] d, final int offset, final int length) throws IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(d, offset, length);
    }
    
    protected final void encodeCIIObjectAlgorithmData(final int id, final Object data, final EncodingAlgorithm ea) throws FastInfosetException, IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        this._encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, this._encodingBufferOutputStream);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(this._encodingBufferIndex);
        this.write(this._encodingBuffer, this._encodingBufferIndex);
    }
    
    protected final void encodeCIIBuiltInAlgorithmData(final int id, final Object data, final int offset, final int length) throws FastInfosetException, IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        final int octetLength = BuiltInEncodingAlgorithmFactory.getAlgorithm(id).getOctetLengthFromPrimitiveLength(length);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(octetLength);
        this.ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(id).encodeToBytes(data, offset, length, this._octetBuffer, this._octetBufferIndex);
        this._octetBufferIndex += octetLength;
    }
    
    protected final void encodeCIIBuiltInAlgorithmDataAsCDATA(final char[] ch, final int offset, int length) throws FastInfosetException, IOException {
        this.write(140);
        this._b = 36;
        length = this.encodeUTF8String(ch, offset, length);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeIdentifyingNonEmptyStringOnFirstBit(final String s, final StringIntMap map) throws IOException {
        final int index = map.obtainIndex(s);
        if (index == -1) {
            this.encodeNonEmptyOctetStringOnSecondBit(s);
        }
        else {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
        }
    }
    
    protected final void encodeNonEmptyOctetStringOnSecondBit(final String s) throws IOException {
        final int length = this.encodeUTF8String(s);
        this.encodeNonZeroOctetStringLengthOnSecondBit(length);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeNonZeroOctetStringLengthOnSecondBit(int length) throws IOException {
        if (length < 65) {
            this.write(length - 1);
        }
        else if (length < 321) {
            this.write(64);
            this.write(length - 65);
        }
        else {
            this.write(96);
            length -= 321;
            this.write(length >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }
    
    protected final void encodeNonEmptyCharacterStringOnFifthBit(final String s) throws IOException {
        final int length = this._encodingStringsAsUtf8 ? this.encodeUTF8String(s) : this.encodeUtf16String(s);
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeNonEmptyCharacterStringOnFifthBit(final char[] ch, final int offset, int length) throws IOException {
        length = (this._encodingStringsAsUtf8 ? this.encodeUTF8String(ch, offset, length) : this.encodeUtf16String(ch, offset, length));
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeNonZeroOctetStringLengthOnFifthBit(int length) throws IOException {
        if (length < 9) {
            this.write(this._b | length - 1);
        }
        else if (length < 265) {
            this.write(this._b | 0x8);
            this.write(length - 9);
        }
        else {
            this.write(this._b | 0xC);
            length -= 265;
            this.write(length >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }
    
    protected final void encodeNonEmptyCharacterStringOnSeventhBit(final char[] ch, final int offset, int length) throws IOException {
        length = (this._encodingStringsAsUtf8 ? this.encodeUTF8String(ch, offset, length) : this.encodeUtf16String(ch, offset, length));
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeNonEmptyFourBitCharacterStringOnSeventhBit(final int[] table, final char[] ch, final int offset, final int length) throws FastInfosetException, IOException {
        final int octetPairLength = length / 2;
        final int octetSingleLength = length % 2;
        this.encodeNonZeroOctetStringLengthOnSenventhBit(octetPairLength + octetSingleLength);
        this.encodeNonEmptyFourBitCharacterString(table, ch, offset, octetPairLength, octetSingleLength);
    }
    
    protected final void encodeNonEmptyFourBitCharacterString(final int[] table, final char[] ch, int offset, final int octetPairLength, final int octetSingleLength) throws FastInfosetException, IOException {
        this.ensureSize(octetPairLength + octetSingleLength);
        int v = 0;
        for (int i = 0; i < octetPairLength; ++i) {
            v = (table[ch[offset++]] << 4 | table[ch[offset++]]);
            if (v < 0) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this._octetBuffer[this._octetBufferIndex++] = (byte)v;
        }
        if (octetSingleLength == 1) {
            v = (table[ch[offset]] << 4 | 0xF);
            if (v < 0) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this._octetBuffer[this._octetBufferIndex++] = (byte)v;
        }
    }
    
    protected final void encodeNonEmptyNBitCharacterStringOnSeventhBit(final String alphabet, final char[] ch, final int offset, final int length) throws FastInfosetException, IOException {
        int bitsPerCharacter;
        for (bitsPerCharacter = 1; 1 << bitsPerCharacter <= alphabet.length(); ++bitsPerCharacter) {}
        final int bits = length * bitsPerCharacter;
        final int octets = bits / 8;
        final int bitsOfLastOctet = bits % 8;
        final int totalOctets = octets + ((bitsOfLastOctet > 0) ? 1 : 0);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(totalOctets);
        this.resetBits();
        this.ensureSize(totalOctets);
        int v = 0;
        for (int i = 0; i < length; ++i) {
            char c;
            for (c = ch[offset + i], v = 0; v < alphabet.length() && c != alphabet.charAt(v); ++v) {}
            if (v == alphabet.length()) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this.writeBits(bitsPerCharacter, v);
        }
        if (bitsOfLastOctet > 0) {
            this.write(this._b |= (1 << 8 - bitsOfLastOctet) - 1);
        }
    }
    
    private final void resetBits() {
        this._bitsLeftInOctet = 8;
        this._b = 0;
    }
    
    private final void writeBits(int bits, final int v) throws IOException {
        while (bits > 0) {
            final int bit = ((v & 1 << --bits) > 0) ? 1 : 0;
            final int b = this._b;
            final int n = bit;
            final int bitsLeftInOctet = this._bitsLeftInOctet - 1;
            this._bitsLeftInOctet = bitsLeftInOctet;
            this._b = (b | n << bitsLeftInOctet);
            if (this._bitsLeftInOctet == 0) {
                this.write(this._b);
                this._bitsLeftInOctet = 8;
                this._b = 0;
            }
        }
    }
    
    protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int length) throws IOException {
        if (length < 3) {
            this.write(this._b | length - 1);
        }
        else if (length < 259) {
            this.write(this._b | 0x2);
            this.write(length - 3);
        }
        else {
            this.write(this._b | 0x3);
            length -= 259;
            this.write(length >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }
    
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int i) throws IOException {
        if (i < 64) {
            this.write(0x80 | i);
        }
        else if (i < 8256) {
            i -= 64;
            this.write(this._b = (0xC0 | i >> 8));
            this.write(i & 0xFF);
        }
        else {
            if (i >= 1048576) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { 1048576 }));
            }
            i -= 8256;
            this.write(this._b = (0xE0 | i >> 16));
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }
    
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int i) throws IOException {
        if (i < 64) {
            this.write(i);
        }
        else if (i < 8256) {
            i -= 64;
            this.write(this._b = (0x40 | i >> 8));
            this.write(i & 0xFF);
        }
        else {
            i -= 8256;
            this.write(this._b = (0x60 | i >> 16));
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }
    
    protected final void encodeNonZeroIntegerOnThirdBit(int i) throws IOException {
        if (i < 32) {
            this.write(this._b | i);
        }
        else if (i < 2080) {
            i -= 32;
            this.write(this._b |= (0x20 | i >> 8));
            this.write(i & 0xFF);
        }
        else if (i < 526368) {
            i -= 2080;
            this.write(this._b |= (0x28 | i >> 16));
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
        else {
            i -= 526368;
            this.write(this._b |= 0x30);
            this.write(i >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }
    
    protected final void encodeNonZeroIntegerOnFourthBit(int i) throws IOException {
        if (i < 16) {
            this.write(this._b | i);
        }
        else if (i < 1040) {
            i -= 16;
            this.write(this._b |= (0x10 | i >> 8));
            this.write(i & 0xFF);
        }
        else if (i < 263184) {
            i -= 1040;
            this.write(this._b |= (0x14 | i >> 16));
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
        else {
            i -= 263184;
            this.write(this._b |= 0x18);
            this.write(i >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }
    
    protected final void encodeNonEmptyUTF8StringAsOctetString(final int b, final String s, final int[] constants) throws IOException {
        final char[] ch = s.toCharArray();
        this.encodeNonEmptyUTF8StringAsOctetString(b, ch, 0, ch.length, constants);
    }
    
    protected final void encodeNonEmptyUTF8StringAsOctetString(final int b, final char[] ch, final int offset, int length, final int[] constants) throws IOException {
        length = this.encodeUTF8String(ch, offset, length);
        this.encodeNonZeroOctetStringLength(b, length, constants);
        this.write(this._encodingBuffer, length);
    }
    
    protected final void encodeNonZeroOctetStringLength(final int b, int length, final int[] constants) throws IOException {
        if (length < constants[0]) {
            this.write(b | length - 1);
        }
        else if (length < constants[1]) {
            this.write(b | constants[2]);
            this.write(length - constants[0]);
        }
        else {
            this.write(b | constants[3]);
            length -= constants[1];
            this.write(length >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }
    
    protected final void encodeNonZeroInteger(final int b, int i, final int[] constants) throws IOException {
        if (i < constants[0]) {
            this.write(b | i);
        }
        else if (i < constants[1]) {
            i -= constants[0];
            this.write(b | constants[3] | i >> 8);
            this.write(i & 0xFF);
        }
        else if (i < constants[2]) {
            i -= constants[1];
            this.write(b | constants[4] | i >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
        else {
            if (i >= 1048576) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { 1048576 }));
            }
            i -= constants[2];
            this.write(b | constants[5]);
            this.write(i >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }
    
    protected final void mark() {
        this._markIndex = this._octetBufferIndex;
    }
    
    protected final void resetMark() {
        this._markIndex = -1;
    }
    
    protected final boolean hasMark() {
        return this._markIndex != -1;
    }
    
    protected final void write(final int i) throws IOException {
        if (this._octetBufferIndex < this._octetBuffer.length) {
            this._octetBuffer[this._octetBufferIndex++] = (byte)i;
        }
        else if (this._markIndex == -1) {
            this._s.write(this._octetBuffer);
            this._octetBufferIndex = 1;
            this._octetBuffer[0] = (byte)i;
        }
        else {
            this.resize(this._octetBuffer.length * 3 / 2);
            this._octetBuffer[this._octetBufferIndex++] = (byte)i;
        }
    }
    
    protected final void write(final byte[] b, final int length) throws IOException {
        this.write(b, 0, length);
    }
    
    protected final void write(final byte[] b, final int offset, final int length) throws IOException {
        if (this._octetBufferIndex + length < this._octetBuffer.length) {
            System.arraycopy(b, offset, this._octetBuffer, this._octetBufferIndex, length);
            this._octetBufferIndex += length;
        }
        else if (this._markIndex == -1) {
            this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
            this._s.write(b, offset, length);
            this._octetBufferIndex = 0;
        }
        else {
            this.resize((this._octetBuffer.length + length) * 3 / 2 + 1);
            System.arraycopy(b, offset, this._octetBuffer, this._octetBufferIndex, length);
            this._octetBufferIndex += length;
        }
    }
    
    private void ensureSize(final int length) {
        if (this._octetBufferIndex + length > this._octetBuffer.length) {
            this.resize((this._octetBufferIndex + length) * 3 / 2 + 1);
        }
    }
    
    private void resize(final int length) {
        final byte[] b = new byte[length];
        System.arraycopy(this._octetBuffer, 0, b, 0, this._octetBufferIndex);
        this._octetBuffer = b;
    }
    
    private void _flush() throws IOException {
        if (this._octetBufferIndex > 0) {
            this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
            this._octetBufferIndex = 0;
        }
    }
    
    protected final int encodeUTF8String(final String s) throws IOException {
        final int length = s.length();
        if (length < this._charBuffer.length) {
            s.getChars(0, length, this._charBuffer, 0);
            return this.encodeUTF8String(this._charBuffer, 0, length);
        }
        final char[] ch = s.toCharArray();
        return this.encodeUTF8String(ch, 0, length);
    }
    
    private void ensureEncodingBufferSizeForUtf8String(final int length) {
        final int newLength = 4 * length;
        if (this._encodingBuffer.length < newLength) {
            this._encodingBuffer = new byte[newLength];
        }
    }
    
    protected final int encodeUTF8String(final char[] ch, int offset, final int length) throws IOException {
        int bpos = 0;
        this.ensureEncodingBufferSizeForUtf8String(length);
        final int end = offset + length;
        while (end != offset) {
            final int c = ch[offset++];
            if (c < 128) {
                this._encodingBuffer[bpos++] = (byte)c;
            }
            else if (c < 2048) {
                this._encodingBuffer[bpos++] = (byte)(0xC0 | c >> 6);
                this._encodingBuffer[bpos++] = (byte)(0x80 | (c & 0x3F));
            }
            else {
                if (c > 65535) {
                    continue;
                }
                if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
                    this._encodingBuffer[bpos++] = (byte)(0xE0 | c >> 12);
                    this._encodingBuffer[bpos++] = (byte)(0x80 | (c >> 6 & 0x3F));
                    this._encodingBuffer[bpos++] = (byte)(0x80 | (c & 0x3F));
                }
                else {
                    this.encodeCharacterAsUtf8FourByte(c, ch, offset, end, bpos);
                    bpos += 4;
                    ++offset;
                }
            }
        }
        return bpos;
    }
    
    private void encodeCharacterAsUtf8FourByte(final int c, final char[] ch, final int chpos, final int chend, int bpos) throws IOException {
        if (chpos == chend) {
            throw new IOException("");
        }
        final char d = ch[chpos];
        if (!XMLChar.isLowSurrogate(d)) {
            throw new IOException("");
        }
        final int uc = ((c & 0x3FF) << 10 | (d & '\u03ff')) + 65536;
        if (uc < 0 || uc >= 2097152) {
            throw new IOException("");
        }
        this._encodingBuffer[bpos++] = (byte)(0xF0 | uc >> 18);
        this._encodingBuffer[bpos++] = (byte)(0x80 | (uc >> 12 & 0x3F));
        this._encodingBuffer[bpos++] = (byte)(0x80 | (uc >> 6 & 0x3F));
        this._encodingBuffer[bpos++] = (byte)(0x80 | (uc & 0x3F));
    }
    
    protected final int encodeUtf16String(final String s) throws IOException {
        final int length = s.length();
        if (length < this._charBuffer.length) {
            s.getChars(0, length, this._charBuffer, 0);
            return this.encodeUtf16String(this._charBuffer, 0, length);
        }
        final char[] ch = s.toCharArray();
        return this.encodeUtf16String(ch, 0, length);
    }
    
    private void ensureEncodingBufferSizeForUtf16String(final int length) {
        final int newLength = 2 * length;
        if (this._encodingBuffer.length < newLength) {
            this._encodingBuffer = new byte[newLength];
        }
    }
    
    protected final int encodeUtf16String(final char[] ch, final int offset, final int length) throws IOException {
        int byteLength = 0;
        this.ensureEncodingBufferSizeForUtf16String(length);
        for (int n = offset + length, i = offset; i < n; ++i) {
            final int c = ch[i];
            this._encodingBuffer[byteLength++] = (byte)(c >> 8);
            this._encodingBuffer[byteLength++] = (byte)(c & 0xFF);
        }
        return byteLength;
    }
    
    public static String getPrefixFromQualifiedName(final String qName) {
        final int i = qName.indexOf(58);
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }
    
    public static boolean isWhiteSpace(final char[] ch, int start, final int length) {
        if (!XMLChar.isSpace(ch[start])) {
            return false;
        }
        final int end = start + length;
        while (++start < end && XMLChar.isSpace(ch[start])) {}
        return start == end;
    }
    
    public static boolean isWhiteSpace(final String s) {
        if (!XMLChar.isSpace(s.charAt(0))) {
            return false;
        }
        final int end = s.length();
        int start = 1;
        while (start < end && XMLChar.isSpace(s.charAt(start++))) {}
        return start == end;
    }
    
    static {
        _characterEncodingSchemeSystemDefault = getDefaultEncodingScheme();
        Encoder.NUMERIC_CHARACTERS_TABLE = new int[maxCharacter("0123456789-+.E ") + 1];
        Encoder.DATE_TIME_CHARACTERS_TABLE = new int[maxCharacter("0123456789-:TZ ") + 1];
        for (int i = 0; i < Encoder.NUMERIC_CHARACTERS_TABLE.length; ++i) {
            Encoder.NUMERIC_CHARACTERS_TABLE[i] = -1;
        }
        for (int i = 0; i < Encoder.DATE_TIME_CHARACTERS_TABLE.length; ++i) {
            Encoder.DATE_TIME_CHARACTERS_TABLE[i] = -1;
        }
        for (int i = 0; i < "0123456789-+.E ".length(); ++i) {
            Encoder.NUMERIC_CHARACTERS_TABLE["0123456789-+.E ".charAt(i)] = i;
        }
        for (int i = 0; i < "0123456789-:TZ ".length(); ++i) {
            Encoder.DATE_TIME_CHARACTERS_TABLE["0123456789-:TZ ".charAt(i)] = i;
        }
    }
    
    private class EncodingBufferOutputStream extends OutputStream
    {
        @Override
        public void write(final int b) throws IOException {
            if (Encoder.this._encodingBufferIndex < Encoder.this._encodingBuffer.length) {
                Encoder.this._encodingBuffer[Encoder.this._encodingBufferIndex++] = (byte)b;
            }
            else {
                final byte[] newbuf = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, Encoder.this._encodingBufferIndex)];
                System.arraycopy(Encoder.this._encodingBuffer, 0, newbuf, 0, Encoder.this._encodingBufferIndex);
                Encoder.this._encodingBuffer = newbuf;
                Encoder.this._encodingBuffer[Encoder.this._encodingBufferIndex++] = (byte)b;
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            final int newoffset = Encoder.this._encodingBufferIndex + len;
            if (newoffset > Encoder.this._encodingBuffer.length) {
                final byte[] newbuf = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, newoffset)];
                System.arraycopy(Encoder.this._encodingBuffer, 0, newbuf, 0, Encoder.this._encodingBufferIndex);
                Encoder.this._encodingBuffer = newbuf;
            }
            System.arraycopy(b, off, Encoder.this._encodingBuffer, Encoder.this._encodingBufferIndex, len);
            Encoder.this._encodingBufferIndex = newoffset;
        }
        
        public int getLength() {
            return Encoder.this._encodingBufferIndex;
        }
        
        public void reset() {
            Encoder.this._encodingBufferIndex = 0;
        }
    }
}
