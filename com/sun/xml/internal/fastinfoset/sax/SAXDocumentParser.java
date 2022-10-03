package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.internal.fastinfoset.EncodingConstants;
import org.xml.sax.Attributes;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.util.Map;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmState;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import java.util.logging.Logger;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;
import com.sun.xml.internal.fastinfoset.Decoder;

public class SAXDocumentParser extends Decoder implements FastInfosetReader
{
    private static final Logger logger;
    protected boolean _namespacePrefixesFeature;
    protected EntityResolver _entityResolver;
    protected DTDHandler _dtdHandler;
    protected ContentHandler _contentHandler;
    protected ErrorHandler _errorHandler;
    protected LexicalHandler _lexicalHandler;
    protected DeclHandler _declHandler;
    protected EncodingAlgorithmContentHandler _algorithmHandler;
    protected PrimitiveTypeContentHandler _primitiveHandler;
    protected BuiltInEncodingAlgorithmState builtInAlgorithmState;
    protected AttributesHolder _attributes;
    protected int[] _namespacePrefixes;
    protected int _namespacePrefixesIndex;
    protected boolean _clearAttributes;
    
    public SAXDocumentParser() {
        this._namespacePrefixesFeature = false;
        this.builtInAlgorithmState = new BuiltInEncodingAlgorithmState();
        this._namespacePrefixes = new int[16];
        this._clearAttributes = false;
        final DefaultHandler handler = new DefaultHandler();
        this._attributes = new AttributesHolder(this._registeredEncodingAlgorithms);
        this._entityResolver = handler;
        this._dtdHandler = handler;
        this._contentHandler = handler;
        this._errorHandler = handler;
        this._lexicalHandler = new LexicalHandlerImpl();
        this._declHandler = new DeclHandlerImpl();
    }
    
    protected void resetOnError() {
        this._clearAttributes = false;
        this._attributes.clear();
        this._namespacePrefixesIndex = 0;
        if (this._v != null) {
            this._v.prefix.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return this._namespacePrefixesFeature;
        }
        if (name.equals("http://xml.org/sax/features/string-interning") || name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
            return this.getStringInterning();
        }
        throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            if (!value) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        }
        else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            this._namespacePrefixesFeature = value;
        }
        else {
            if (!name.equals("http://xml.org/sax/features/string-interning") && !name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
                throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
            }
            this.setStringInterning(value);
        }
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            return this.getLexicalHandler();
        }
        if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
            return this.getDeclHandler();
        }
        if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
            return this.getExternalVocabularies();
        }
        if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
            return this.getRegisteredEncodingAlgorithms();
        }
        if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
            return this.getEncodingAlgorithmContentHandler();
        }
        if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
            return this.getPrimitiveTypeContentHandler();
        }
        throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { name }));
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            if (!(value instanceof LexicalHandler)) {
                throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
            }
            this.setLexicalHandler((LexicalHandler)value);
        }
        else if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
            if (!(value instanceof DeclHandler)) {
                throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
            }
            this.setDeclHandler((DeclHandler)value);
        }
        else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
            if (!(value instanceof Map)) {
                throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies");
            }
            this.setExternalVocabularies((Map)value);
        }
        else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
            if (!(value instanceof Map)) {
                throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms");
            }
            this.setRegisteredEncodingAlgorithms((Map)value);
        }
        else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
            if (!(value instanceof EncodingAlgorithmContentHandler)) {
                throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler");
            }
            this.setEncodingAlgorithmContentHandler((EncodingAlgorithmContentHandler)value);
        }
        else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
            if (!(value instanceof PrimitiveTypeContentHandler)) {
                throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler");
            }
            this.setPrimitiveTypeContentHandler((PrimitiveTypeContentHandler)value);
        }
        else {
            if (!name.equals("http://jvnet.org/fastinfoset/parser/properties/buffer-size")) {
                throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { name }));
            }
            if (!(value instanceof Integer)) {
                throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/buffer-size");
            }
            this.setBufferSize((int)value);
        }
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
        this._entityResolver = resolver;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler handler) {
        this._dtdHandler = handler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this._dtdHandler;
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this._contentHandler = handler;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this._contentHandler;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler handler) {
        this._errorHandler = handler;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }
    
    @Override
    public void parse(final InputSource input) throws IOException, SAXException {
        try {
            final InputStream s = input.getByteStream();
            if (s == null) {
                final String systemId = input.getSystemId();
                if (systemId == null) {
                    throw new SAXException(CommonResourceBundle.getInstance().getString("message.inputSource"));
                }
                this.parse(systemId);
            }
            else {
                this.parse(s);
            }
        }
        catch (final FastInfosetException e) {
            SAXDocumentParser.logger.log(Level.FINE, "parsing error", e);
            throw new SAXException(e);
        }
    }
    
    @Override
    public void parse(String systemId) throws IOException, SAXException {
        try {
            systemId = SystemIdResolver.getAbsoluteURI(systemId);
            this.parse(new URL(systemId).openStream());
        }
        catch (final FastInfosetException e) {
            SAXDocumentParser.logger.log(Level.FINE, "parsing error", e);
            throw new SAXException(e);
        }
    }
    
    @Override
    public final void parse(final InputStream s) throws IOException, FastInfosetException, SAXException {
        this.setInputStream(s);
        this.parse();
    }
    
    @Override
    public void setLexicalHandler(final LexicalHandler handler) {
        this._lexicalHandler = handler;
    }
    
    @Override
    public LexicalHandler getLexicalHandler() {
        return this._lexicalHandler;
    }
    
    @Override
    public void setDeclHandler(final DeclHandler handler) {
        this._declHandler = handler;
    }
    
    @Override
    public DeclHandler getDeclHandler() {
        return this._declHandler;
    }
    
    @Override
    public void setEncodingAlgorithmContentHandler(final EncodingAlgorithmContentHandler handler) {
        this._algorithmHandler = handler;
    }
    
    @Override
    public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler() {
        return this._algorithmHandler;
    }
    
    @Override
    public void setPrimitiveTypeContentHandler(final PrimitiveTypeContentHandler handler) {
        this._primitiveHandler = handler;
    }
    
    @Override
    public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler() {
        return this._primitiveHandler;
    }
    
    public final void parse() throws FastInfosetException, IOException {
        if (this._octetBuffer.length < this._bufferSize) {
            this._octetBuffer = new byte[this._bufferSize];
        }
        try {
            this.reset();
            this.decodeHeader();
            if (this._parseFragments) {
                this.processDIIFragment();
            }
            else {
                this.processDII();
            }
        }
        catch (final RuntimeException e) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), null, e));
            }
            catch (final Exception ex) {}
            this.resetOnError();
            throw new FastInfosetException(e);
        }
        catch (final FastInfosetException e2) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e2.getClass().getName(), null, e2));
            }
            catch (final Exception ex2) {}
            this.resetOnError();
            throw e2;
        }
        catch (final IOException e3) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e3.getClass().getName(), null, e3));
            }
            catch (final Exception ex3) {}
            this.resetOnError();
            throw e3;
        }
    }
    
    protected final void processDII() throws FastInfosetException, IOException {
        try {
            this._contentHandler.startDocument();
        }
        catch (final SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        boolean firstElementHasOccured = false;
        boolean documentTypeDeclarationOccured = false;
        while (!this._terminate || !firstElementHasOccured) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    firstElementHasOccured = true;
                    continue;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    firstElementHasOccured = true;
                    continue;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 5: {
                    final QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    firstElementHasOccured = true;
                    continue;
                }
                case 20: {
                    if (documentTypeDeclarationOccured) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
                    }
                    documentTypeDeclarationOccured = true;
                    final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    this._b = this.read();
                    while (this._b == 225) {
                        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
                            case 0: {
                                if (this._addToTable) {
                                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                                    break;
                                }
                                break;
                            }
                            case 2: {
                                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
                            }
                        }
                        this._b = this.read();
                    }
                    if ((this._b & 0xF0) != 0xF0) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
                    }
                    if (this._b == 255) {
                        this._terminate = true;
                    }
                    if (this._notations != null) {
                        this._notations.clear();
                    }
                    if (this._unparsedEntities != null) {
                        this._unparsedEntities.clear();
                        continue;
                    }
                    continue;
                }
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
                }
            }
        }
        while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
                }
            }
        }
        try {
            this._contentHandler.endDocument();
        }
        catch (final SAXException e2) {
            throw new FastInfosetException("processDII", e2);
        }
    }
    
    protected final void processDIIFragment() throws FastInfosetException, IOException {
        try {
            this._contentHandler.startDocument();
        }
        catch (final SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue;
                }
                case 5: {
                    final QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 12: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 13: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.processCIIEncodingAlgorithm(addToTable);
                    continue;
                }
                case 14: {
                    final int index = this._b & 0xF;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 15: {
                    final int index = ((this._b & 0x3) << 8 | this.read()) + 16;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 16: {
                    final int index = ((this._b & 0x3) << 16 | this.read() << 8 | this.read()) + 1040;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 17: {
                    final int index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 21: {
                    final String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    try {
                        this._contentHandler.skippedEntity(entity_reference_name);
                        continue;
                    }
                    catch (final SAXException e3) {
                        throw new FastInfosetException("processUnexpandedEntityReferenceII", e3);
                    }
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
                }
            }
        }
        try {
            this._contentHandler.endDocument();
        }
        catch (final SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
    }
    
    protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {
        if (this._b == 32) {
            this.decodeInitialVocabulary();
            return;
        }
        if ((this._b & 0x40) > 0) {
            this.decodeAdditionalData();
        }
        if ((this._b & 0x20) > 0) {
            this.decodeInitialVocabulary();
        }
        if ((this._b & 0x10) > 0) {
            this.decodeNotations();
        }
        if ((this._b & 0x8) > 0) {
            this.decodeUnparsedEntities();
        }
        if ((this._b & 0x4) > 0) {
            this.decodeCharacterEncodingScheme();
        }
        if ((this._b & 0x2) > 0) {
            this.read();
        }
        if ((this._b & 0x1) > 0) {
            this.decodeVersion();
        }
    }
    
    protected final void processEII(final QualifiedName name, final boolean hasAttributes) throws FastInfosetException, IOException {
        if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameOfEIINotInScope"));
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        try {
            this._contentHandler.startElement(name.namespaceName, name.localName, name.qName, this._attributes);
        }
        catch (final SAXException e) {
            SAXDocumentParser.logger.log(Level.FINE, "processEII error", e);
            throw new FastInfosetException("processEII", e);
        }
        if (this._clearAttributes) {
            this._attributes.clear();
            this._clearAttributes = false;
        }
        while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue;
                }
                case 5: {
                    final QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString();
                    continue;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 0x1) + 1;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 12: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 13: {
                    final boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 0x2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.processCIIEncodingAlgorithm(addToTable);
                    continue;
                }
                case 14: {
                    final int index = this._b & 0xF;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 15: {
                    final int index = ((this._b & 0x3) << 8 | this.read()) + 16;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 16: {
                    final int index = ((this._b & 0x3) << 16 | this.read() << 8 | this.read()) + 1040;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 17: {
                    final int index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue;
                    }
                    catch (final SAXException e2) {
                        throw new FastInfosetException("processCII", e2);
                    }
                }
                case 18: {
                    this.processCommentII();
                    continue;
                }
                case 19: {
                    this.processProcessingII();
                    continue;
                }
                case 21: {
                    final String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    final String system_identifier = ((this._b & 0x2) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    final String public_identifier = ((this._b & 0x1) > 0) ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    try {
                        this._contentHandler.skippedEntity(entity_reference_name);
                        continue;
                    }
                    catch (final SAXException e3) {
                        throw new FastInfosetException("processUnexpandedEntityReferenceII", e3);
                    }
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
                }
            }
        }
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
        try {
            this._contentHandler.endElement(name.namespaceName, name.localName, name.qName);
        }
        catch (final SAXException e) {
            throw new FastInfosetException("processEII", e);
        }
    }
    
    private final void processUtf8CharacterString() throws FastInfosetException, IOException {
        if ((this._b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            final int charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
            try {
                this._contentHandler.characters(this._characterContentChunkTable._array, charactersOffset, this._charBufferLength);
            }
            catch (final SAXException e) {
                throw new FastInfosetException("processCII", e);
            }
        }
        else {
            this.decodeUtf8StringAsCharBuffer();
            try {
                this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
            }
            catch (final SAXException e2) {
                throw new FastInfosetException("processCII", e2);
            }
        }
    }
    
    protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
        final boolean hasAttributes = (this._b & 0x40) > 0;
        this._clearAttributes = this._namespacePrefixesFeature;
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        String prefix = "";
        String namespaceName = "";
        final int start = this._namespacePrefixesIndex;
        int b;
        for (b = this.read(); (b & 0xFC) == 0xCC; b = this.read()) {
            if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
                final int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
                this._namespacePrefixes = namespaceAIIs;
            }
            switch (b & 0x3) {
                case 0: {
                    namespaceName = (prefix = "");
                    final int[] namespacePrefixes = this._namespacePrefixes;
                    final int n = this._namespacePrefixesIndex++;
                    final int n2 = -1;
                    namespacePrefixes[n] = n2;
                    this._prefixIndex = n2;
                    this._namespaceNameIndex = n2;
                    break;
                }
                case 1: {
                    prefix = "";
                    namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
                    final int[] namespacePrefixes2 = this._namespacePrefixes;
                    final int n3 = this._namespacePrefixesIndex++;
                    final int prefixIndex2 = -1;
                    namespacePrefixes2[n3] = prefixIndex2;
                    this._prefixIndex = prefixIndex2;
                    break;
                }
                case 2: {
                    prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    namespaceName = "";
                    this._namespaceNameIndex = -1;
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
            }
            this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
            if (this._namespacePrefixesFeature) {
                if (prefix != "") {
                    this._attributes.addAttribute(new QualifiedName("xmlns", "http://www.w3.org/2000/xmlns/", prefix), namespaceName);
                }
                else {
                    this._attributes.addAttribute(EncodingConstants.DEFAULT_NAMESPACE_DECLARATION, namespaceName);
                }
            }
            try {
                this._contentHandler.startPrefixMapping(prefix, namespaceName);
            }
            catch (final SAXException e) {
                throw new IOException("processStartNamespaceAII");
            }
        }
        if (b != 240) {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
        }
        final int end = this._namespacePrefixesIndex;
        this._b = this.read();
        switch (DecoderStateTables.EII(this._b)) {
            case 0: {
                this.processEII(this._elementNameTable._array[this._b], hasAttributes);
                break;
            }
            case 2: {
                this.processEII(this.decodeEIIIndexMedium(), hasAttributes);
                break;
            }
            case 3: {
                this.processEII(this.decodeEIIIndexLarge(), hasAttributes);
                break;
            }
            case 5: {
                final QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 0x3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
        try {
            for (int i = end - 1; i >= start; --i) {
                final int prefixIndex = this._namespacePrefixes[i];
                this._prefixTable.popScope(prefixIndex);
                prefix = ((prefixIndex > 0) ? this._prefixTable.get(prefixIndex - 1) : ((prefixIndex == -1) ? "" : "xml"));
                this._contentHandler.endPrefixMapping(prefix);
            }
            this._namespacePrefixesIndex = start;
        }
        catch (final SAXException e2) {
            throw new IOException("processStartNamespaceAII");
        }
    }
    
    protected final void processAIIs() throws FastInfosetException, IOException {
        this._clearAttributes = true;
        if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
            this._duplicateAttributeVerifier.clear();
        }
        do {
            int b = this.read();
            QualifiedName name = null;
            switch (DecoderStateTables.AII(b)) {
                case 0: {
                    name = this._attributeNameTable._array[b];
                    break;
                }
                case 1: {
                    final int i = ((b & 0x1F) << 8 | this.read()) + 64;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 2: {
                    final int i = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 3: {
                    final QualifiedName decodeLiteralQualifiedName;
                    name = (decodeLiteralQualifiedName = this.decodeLiteralQualifiedName(b & 0x3, this._attributeNameTable.getNext()));
                    final DuplicateAttributeVerifier duplicateAttributeVerifier = this._duplicateAttributeVerifier;
                    decodeLiteralQualifiedName.createAttributeValues(256);
                    this._attributeNameTable.add(name);
                    break;
                }
                case 5: {
                    this._doubleTerminate = true;
                }
                case 4: {
                    this._terminate = true;
                    continue;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
                }
            }
            if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
            }
            this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
            b = this.read();
            switch (DecoderStateTables.NISTRING(b)) {
                case 0: {
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 1: {
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 2: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    final String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 3: {
                    this._octetBufferLength = (b & 0x7) + 1;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 4: {
                    this._octetBufferLength = this.read() + 9;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 5: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    final String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 6: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    final String value = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    continue;
                }
                case 7: {
                    final boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    this.processAIIEncodingAlgorithm(name, addToTable);
                    continue;
                }
                case 8: {
                    this._attributes.addAttribute(name, this._attributeValueTable._array[b & 0x3F]);
                    continue;
                }
                case 9: {
                    final int index = ((b & 0x1F) << 8 | this.read()) + 64;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    continue;
                }
                case 10: {
                    final int index = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    continue;
                }
                case 11: {
                    this._attributes.addAttribute(name, "");
                    continue;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
                }
            }
        } while (!this._terminate);
        this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
    }
    
    protected final void processCommentII() throws FastInfosetException, IOException {
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                if (this._addToTable) {
                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                }
                try {
                    this._lexicalHandler.comment(this._charBuffer, 0, this._charBufferLength);
                    break;
                }
                catch (final SAXException e) {
                    throw new FastInfosetException("processCommentII", e);
                }
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                final CharArray ca = this._v.otherString.get(this._integer);
                try {
                    this._lexicalHandler.comment(ca.ch, ca.start, ca.length);
                    break;
                }
                catch (final SAXException e2) {
                    throw new FastInfosetException("processCommentII", e2);
                }
            }
            case 3: {
                try {
                    this._lexicalHandler.comment(this._charBuffer, 0, 0);
                }
                catch (final SAXException e2) {
                    throw new FastInfosetException("processCommentII", e2);
                }
                break;
            }
        }
    }
    
    protected final void processProcessingII() throws FastInfosetException, IOException {
        final String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                final String data = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(data));
                }
                try {
                    this._contentHandler.processingInstruction(target, data);
                    break;
                }
                catch (final SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                try {
                    this._contentHandler.processingInstruction(target, this._v.otherString.get(this._integer).toString());
                    break;
                }
                catch (final SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
            }
            case 3: {
                try {
                    this._contentHandler.processingInstruction(target, "");
                }
                catch (final SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
                break;
            }
        }
    }
    
    protected final void processCIIEncodingAlgorithm(final boolean addToTable) throws FastInfosetException, IOException {
        if (this._identifier < 9) {
            if (this._primitiveHandler != null) {
                this.processCIIBuiltInEncodingAlgorithmAsPrimitive();
            }
            else if (this._algorithmHandler != null) {
                final Object array = this.processBuiltInEncodingAlgorithmAsObject();
                try {
                    this._algorithmHandler.object(null, this._identifier, array);
                }
                catch (final SAXException e) {
                    throw new FastInfosetException(e);
                }
            }
            else {
                final StringBuffer buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                try {
                    this._contentHandler.characters(buffer.toString().toCharArray(), 0, buffer.length());
                }
                catch (final SAXException e) {
                    throw new FastInfosetException(e);
                }
            }
            if (addToTable) {
                final StringBuffer buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                this._characterContentChunkTable.add(buffer.toString().toCharArray(), buffer.length());
            }
        }
        else if (this._identifier == 9) {
            this._octetBufferOffset -= this._octetBufferLength;
            this.decodeUtf8StringIntoCharBuffer();
            try {
                this._lexicalHandler.startCDATA();
                this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                this._lexicalHandler.endCDATA();
            }
            catch (final SAXException e2) {
                throw new FastInfosetException(e2);
            }
            if (addToTable) {
                this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }
        }
        else if (this._identifier >= 32 && this._algorithmHandler != null) {
            final String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { this._identifier }));
            }
            final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                final Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                try {
                    this._algorithmHandler.object(URI, this._identifier, data);
                }
                catch (final SAXException e3) {
                    throw new FastInfosetException(e3);
                }
            }
            else {
                try {
                    this._algorithmHandler.octets(URI, this._identifier, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                }
                catch (final SAXException e4) {
                    throw new FastInfosetException(e4);
                }
            }
            if (addToTable) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.addToTableNotSupported"));
            }
        }
        else {
            if (this._identifier >= 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
    }
    
    protected final void processCIIBuiltInEncodingAlgorithmAsPrimitive() throws FastInfosetException, IOException {
        try {
            switch (this._identifier) {
                case 0:
                case 1: {
                    this._primitiveHandler.bytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    break;
                }
                case 2: {
                    final int length = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.shortArray.length) {
                        final short[] array = new short[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.shortArray, 0, array, 0, this.builtInAlgorithmState.shortArray.length);
                        this.builtInAlgorithmState.shortArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.decodeFromBytesToShortArray(this.builtInAlgorithmState.shortArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.shorts(this.builtInAlgorithmState.shortArray, 0, length);
                    break;
                }
                case 3: {
                    final int length = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.intArray.length) {
                        final int[] array2 = new int[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.intArray, 0, array2, 0, this.builtInAlgorithmState.intArray.length);
                        this.builtInAlgorithmState.intArray = array2;
                    }
                    BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.decodeFromBytesToIntArray(this.builtInAlgorithmState.intArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.ints(this.builtInAlgorithmState.intArray, 0, length);
                    break;
                }
                case 4: {
                    final int length = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.longArray.length) {
                        final long[] array3 = new long[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.longArray, 0, array3, 0, this.builtInAlgorithmState.longArray.length);
                        this.builtInAlgorithmState.longArray = array3;
                    }
                    BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.longs(this.builtInAlgorithmState.longArray, 0, length);
                    break;
                }
                case 5: {
                    final int length = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength, this._octetBuffer[this._octetBufferStart] & 0xFF);
                    if (length > this.builtInAlgorithmState.booleanArray.length) {
                        final boolean[] array4 = new boolean[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.booleanArray, 0, array4, 0, this.builtInAlgorithmState.booleanArray.length);
                        this.builtInAlgorithmState.booleanArray = array4;
                    }
                    BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.decodeFromBytesToBooleanArray(this.builtInAlgorithmState.booleanArray, 0, length, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.booleans(this.builtInAlgorithmState.booleanArray, 0, length);
                    break;
                }
                case 6: {
                    final int length = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.floatArray.length) {
                        final float[] array5 = new float[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.floatArray, 0, array5, 0, this.builtInAlgorithmState.floatArray.length);
                        this.builtInAlgorithmState.floatArray = array5;
                    }
                    BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.decodeFromBytesToFloatArray(this.builtInAlgorithmState.floatArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.floats(this.builtInAlgorithmState.floatArray, 0, length);
                    break;
                }
                case 7: {
                    final int length = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.doubleArray.length) {
                        final double[] array6 = new double[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.doubleArray, 0, array6, 0, this.builtInAlgorithmState.doubleArray.length);
                        this.builtInAlgorithmState.doubleArray = array6;
                    }
                    BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.decodeFromBytesToDoubleArray(this.builtInAlgorithmState.doubleArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.doubles(this.builtInAlgorithmState.doubleArray, 0, length);
                    break;
                }
                case 8: {
                    final int length = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.longArray.length) {
                        final long[] array3 = new long[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.longArray, 0, array3, 0, this.builtInAlgorithmState.longArray.length);
                        this.builtInAlgorithmState.longArray = array3;
                    }
                    BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.uuids(this.builtInAlgorithmState.longArray, 0, length);
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException("CDATA");
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unsupportedAlgorithm", new Object[] { this._identifier }));
                }
            }
        }
        catch (final SAXException e) {
            throw new FastInfosetException(e);
        }
    }
    
    protected final void processAIIEncodingAlgorithm(final QualifiedName name, final boolean addToTable) throws FastInfosetException, IOException {
        if (this._identifier < 9) {
            if (this._primitiveHandler != null || this._algorithmHandler != null) {
                final Object data = this.processBuiltInEncodingAlgorithmAsObject();
                this._attributes.addAttributeWithAlgorithmData(name, null, this._identifier, data);
            }
            else {
                final StringBuffer buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                this._attributes.addAttribute(name, buffer.toString());
            }
        }
        else if (this._identifier >= 32 && this._algorithmHandler != null) {
            final String URI = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { this._identifier }));
            }
            final EncodingAlgorithm ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                final Object data2 = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                this._attributes.addAttributeWithAlgorithmData(name, URI, this._identifier, data2);
            }
            else {
                final byte[] data3 = new byte[this._octetBufferLength];
                System.arraycopy(this._octetBuffer, this._octetBufferStart, data3, 0, this._octetBufferLength);
                this._attributes.addAttributeWithAlgorithmData(name, URI, this._identifier, data3);
            }
        }
        else {
            if (this._identifier >= 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }
            if (this._identifier == 9) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        if (addToTable) {
            this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(name.qName)));
        }
    }
    
    protected final void processBuiltInEncodingAlgorithmAsCharacters(final StringBuffer buffer) throws FastInfosetException, IOException {
        final Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
    }
    
    protected final Object processBuiltInEncodingAlgorithmAsObject() throws FastInfosetException, IOException {
        return BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
    }
    
    static {
        logger = Logger.getLogger(SAXDocumentParser.class.getName());
    }
    
    private static final class LexicalHandlerImpl implements LexicalHandler
    {
        @Override
        public void comment(final char[] ch, final int start, final int end) {
        }
        
        @Override
        public void startDTD(final String name, final String publicId, final String systemId) {
        }
        
        @Override
        public void endDTD() {
        }
        
        @Override
        public void startEntity(final String name) {
        }
        
        @Override
        public void endEntity(final String name) {
        }
        
        @Override
        public void startCDATA() {
        }
        
        @Override
        public void endCDATA() {
        }
    }
    
    private static final class DeclHandlerImpl implements DeclHandler
    {
        @Override
        public void elementDecl(final String name, final String model) throws SAXException {
        }
        
        @Override
        public void attributeDecl(final String eName, final String aName, final String type, final String mode, final String value) throws SAXException {
        }
        
        @Override
        public void internalEntityDecl(final String name, final String value) throws SAXException {
        }
        
        @Override
        public void externalEntityDecl(final String name, final String publicId, final String systemId) throws SAXException {
        }
    }
}
