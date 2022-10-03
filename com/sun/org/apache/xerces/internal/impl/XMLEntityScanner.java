package com.sun.org.apache.xerces.internal.impl;

import java.io.InputStreamReader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import java.io.Reader;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.io.EOFException;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.xml.internal.stream.Entity;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public class XMLEntityScanner implements XMLLocator
{
    protected Entity.ScannedEntity fCurrentEntity;
    protected int fBufferSize;
    protected XMLEntityManager fEntityManager;
    protected XMLSecurityManager fSecurityManager;
    protected XMLLimitAnalyzer fLimitAnalyzer;
    private static final boolean DEBUG_ENCODINGS = false;
    private ArrayList<XMLBufferListener> listeners;
    private static final boolean[] VALID_NAMES;
    private static final boolean DEBUG_BUFFER = false;
    private static final boolean DEBUG_SKIP_STRING = false;
    private static final EOFException END_OF_DOCUMENT_ENTITY;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    int[] whiteSpaceLookup;
    int whiteSpaceLen;
    boolean whiteSpaceInfoNeeded;
    protected boolean fAllowJavaEncodings;
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected PropertyManager fPropertyManager;
    boolean isExternal;
    protected boolean xmlVersionSetExplicitly;
    boolean detectingVersion;
    
    public XMLEntityScanner() {
        this.fCurrentEntity = null;
        this.fBufferSize = 8192;
        this.fSecurityManager = null;
        this.fLimitAnalyzer = null;
        this.listeners = new ArrayList<XMLBufferListener>();
        this.fSymbolTable = null;
        this.fErrorReporter = null;
        this.whiteSpaceLookup = new int[100];
        this.whiteSpaceLen = 0;
        this.whiteSpaceInfoNeeded = true;
        this.fPropertyManager = null;
        this.isExternal = false;
        this.xmlVersionSetExplicitly = false;
        this.detectingVersion = false;
    }
    
    public XMLEntityScanner(final PropertyManager propertyManager, final XMLEntityManager entityManager) {
        this.fCurrentEntity = null;
        this.fBufferSize = 8192;
        this.fSecurityManager = null;
        this.fLimitAnalyzer = null;
        this.listeners = new ArrayList<XMLBufferListener>();
        this.fSymbolTable = null;
        this.fErrorReporter = null;
        this.whiteSpaceLookup = new int[100];
        this.whiteSpaceLen = 0;
        this.whiteSpaceInfoNeeded = true;
        this.fPropertyManager = null;
        this.isExternal = false;
        this.xmlVersionSetExplicitly = false;
        this.detectingVersion = false;
        this.fEntityManager = entityManager;
        this.reset(propertyManager);
    }
    
    public final void setBufferSize(final int size) {
        this.fBufferSize = size;
    }
    
    public void reset(final PropertyManager propertyManager) {
        this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.resetCommon();
    }
    
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.resetCommon();
    }
    
    public final void reset(final SymbolTable symbolTable, final XMLEntityManager entityManager, final XMLErrorReporter reporter) {
        this.fCurrentEntity = null;
        this.fSymbolTable = symbolTable;
        this.fEntityManager = entityManager;
        this.fErrorReporter = reporter;
        this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
        this.fSecurityManager = this.fEntityManager.fSecurityManager;
    }
    
    private void resetCommon() {
        this.fCurrentEntity = null;
        this.whiteSpaceLen = 0;
        this.whiteSpaceInfoNeeded = true;
        this.listeners.clear();
        this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
        this.fSecurityManager = this.fEntityManager.fSecurityManager;
    }
    
    @Override
    public final String getXMLVersion() {
        if (this.fCurrentEntity != null) {
            return this.fCurrentEntity.xmlVersion;
        }
        return null;
    }
    
    public final void setXMLVersion(final String xmlVersion) {
        this.xmlVersionSetExplicitly = true;
        this.fCurrentEntity.xmlVersion = xmlVersion;
    }
    
    public final void setCurrentEntity(final Entity.ScannedEntity scannedEntity) {
        this.fCurrentEntity = scannedEntity;
        if (this.fCurrentEntity != null) {
            this.isExternal = this.fCurrentEntity.isExternal();
        }
    }
    
    public Entity.ScannedEntity getCurrentEntity() {
        return this.fCurrentEntity;
    }
    
    @Override
    public final String getBaseSystemId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
    }
    
    public void setBaseSystemId(final String systemId) {
    }
    
    @Override
    public final int getLineNumber() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.lineNumber : -1;
    }
    
    public void setLineNumber(final int line) {
    }
    
    @Override
    public final int getColumnNumber() {
        return (this.fCurrentEntity != null) ? this.fCurrentEntity.columnNumber : -1;
    }
    
    public void setColumnNumber(final int col) {
    }
    
    @Override
    public final int getCharacterOffset() {
        return (this.fCurrentEntity != null) ? (this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position) : -1;
    }
    
    @Override
    public final String getExpandedSystemId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
    }
    
    public void setExpandedSystemId(final String systemId) {
    }
    
    @Override
    public final String getLiteralSystemId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null;
    }
    
    public void setLiteralSystemId(final String systemId) {
    }
    
    @Override
    public final String getPublicId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
    }
    
    public void setPublicId(final String publicId) {
    }
    
    public void setVersion(final String version) {
        this.fCurrentEntity.version = version;
    }
    
    public String getVersion() {
        if (this.fCurrentEntity != null) {
            return this.fCurrentEntity.version;
        }
        return null;
    }
    
    @Override
    public final String getEncoding() {
        if (this.fCurrentEntity != null) {
            return this.fCurrentEntity.encoding;
        }
        return null;
    }
    
    public final void setEncoding(final String encoding) throws IOException {
        if (this.fCurrentEntity.stream != null && (this.fCurrentEntity.encoding == null || !this.fCurrentEntity.encoding.equals(encoding))) {
            if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
                final String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
                if (ENCODING.equals("UTF-16")) {
                    return;
                }
                if (ENCODING.equals("ISO-10646-UCS-4")) {
                    if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
                    }
                    else {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
                    }
                    return;
                }
                if (ENCODING.equals("ISO-10646-UCS-2")) {
                    if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
                    }
                    else {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
                    }
                    return;
                }
            }
            this.fCurrentEntity.reader = this.createReader(this.fCurrentEntity.stream, encoding, null);
            this.fCurrentEntity.encoding = encoding;
        }
    }
    
    public final boolean isExternal() {
        return this.fCurrentEntity.isExternal();
    }
    
    public int getChar(final int relative) throws IOException {
        if (this.arrangeCapacity(relative + 1, false)) {
            return this.fCurrentEntity.ch[this.fCurrentEntity.position + relative];
        }
        return -1;
    }
    
    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        final int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.isExternal) {
            return (c != 13) ? c : 10;
        }
        return c;
    }
    
    protected int scanChar(final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c == 10 || (c == 13 && this.isExternal)) {
            final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = (char)c;
                this.load(1, false, false);
                offset = 0;
            }
            if (c == 13 && this.isExternal) {
                if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n') {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                }
                c = 10;
            }
        }
        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
        ++fCurrentEntity3.columnNumber;
        if (!this.detectingVersion) {
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
        }
        return c;
    }
    
    protected String scanNmtoken() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        boolean vc = false;
        while (true) {
            final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c < '\u007f') {
                vc = XMLEntityScanner.VALID_NAMES[c];
            }
            else {
                vc = XMLChar.isName(c);
            }
            if (!vc) {
                break;
            }
            if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                continue;
            }
            final int length = this.fCurrentEntity.position - offset;
            this.invokeListeners(length);
            if (length == this.fCurrentEntity.fBufferSize) {
                final char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
                System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                this.fCurrentEntity.ch = tmp;
                final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                fCurrentEntity.fBufferSize *= 2;
            }
            else {
                System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
            }
            offset = 0;
            if (this.load(length, false, false)) {
                break;
            }
        }
        final int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
        fCurrentEntity2.columnNumber += length;
        String symbol = null;
        if (length > 0) {
            symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        }
        return symbol;
    }
    
    protected String scanName(final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return symbol;
                }
            }
            boolean vc = false;
            while (true) {
                final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (c < '\u007f') {
                    vc = XMLEntityScanner.VALID_NAMES[c];
                }
                else {
                    vc = XMLChar.isName(c);
                }
                if (!vc) {
                    break;
                }
                final int length;
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) <= 0) {
                    continue;
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
            }
        }
        int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
        fCurrentEntity2.columnNumber += length;
        String symbol;
        if (length > 0) {
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
            symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        }
        else {
            symbol = null;
        }
        return symbol;
    }
    
    protected boolean scanQName(final QName qname, final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    qname.setValues(null, name, name, null);
                    this.checkEntityLimit(nt, this.fCurrentEntity, 0, 1);
                    return true;
                }
            }
            int index = -1;
            boolean vc = false;
            while (true) {
                final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (c < '\u007f') {
                    vc = XMLEntityScanner.VALID_NAMES[c];
                }
                else {
                    vc = XMLChar.isName(c);
                }
                if (!vc) {
                    break;
                }
                if (c == ':') {
                    if (index != -1) {
                        break;
                    }
                    index = this.fCurrentEntity.position;
                    this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, index - offset);
                }
                final int length;
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, index)) <= 0) {
                    continue;
                }
                if (index != -1) {
                    index -= offset;
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
            }
            int length = this.fCurrentEntity.position - offset;
            final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
            fCurrentEntity2.columnNumber += length;
            if (length > 0) {
                String prefix = null;
                String localpart = null;
                final String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
                if (index != -1) {
                    final int prefixLength = index - offset;
                    this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, prefixLength);
                    prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
                    final int len = length - prefixLength - 1;
                    this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, index + 1, len);
                    localpart = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, index + 1, len);
                }
                else {
                    localpart = rawname;
                    this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
                }
                qname.setValues(prefix, localpart, rawname, null);
                this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
                return true;
            }
        }
        return false;
    }
    
    protected int checkBeforeLoad(final Entity.ScannedEntity entity, final int offset, int nameOffset) throws IOException {
        int length = 0;
        if (++entity.position == entity.count) {
            int nameLength;
            length = (nameLength = entity.position - offset);
            if (nameOffset != -1) {
                nameOffset -= offset;
                nameLength = length - nameOffset;
            }
            else {
                nameOffset = offset;
            }
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, entity, nameOffset, nameLength);
            this.invokeListeners(length);
            if (length == entity.ch.length) {
                final char[] tmp = new char[entity.fBufferSize * 2];
                System.arraycopy(entity.ch, offset, tmp, 0, length);
                entity.ch = tmp;
                entity.fBufferSize *= 2;
            }
            else {
                System.arraycopy(entity.ch, offset, entity.ch, 0, length);
            }
        }
        return length;
    }
    
    protected void checkEntityLimit(final XMLScanner.NameType nt, final Entity.ScannedEntity entity, final int offset, final int length) {
        if (entity == null || !entity.isGE) {
            return;
        }
        if (nt != XMLScanner.NameType.REFERENCE) {
            this.checkLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entity, offset, length);
        }
        if (nt == XMLScanner.NameType.ELEMENTSTART || nt == XMLScanner.NameType.ATTRIBUTENAME) {
            this.checkNodeCount(entity);
        }
    }
    
    protected void checkNodeCount(final Entity.ScannedEntity entity) {
        if (entity != null && entity.isGE) {
            this.checkLimit(XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT, entity, 0, 1);
        }
    }
    
    protected void checkLimit(final XMLSecurityManager.Limit limit, final Entity.ScannedEntity entity, final int offset, final int length) {
        this.fLimitAnalyzer.addValue(limit, entity.name, length);
        if (this.fSecurityManager.isOverLimit(limit, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            final Object[] e = (limit == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT) ? new Object[] { this.fLimitAnalyzer.getValue(limit), this.fSecurityManager.getLimit(limit), this.fSecurityManager.getStateLiteral(limit) } : new Object[] { entity.name, this.fLimitAnalyzer.getValue(limit), this.fSecurityManager.getLimit(limit), this.fSecurityManager.getStateLiteral(limit) };
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", limit.key(), e, (short)2);
        }
        if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimit", new Object[] { this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) }, (short)2);
        }
    }
    
    protected int scanContent(final XMLString content) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false, false);
            this.fCurrentEntity.position = 0;
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[offset];
        int newlines = 0;
        boolean counted = false;
        if (c == 10 || (c == 13 && this.isExternal)) {
            do {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == 13 && this.isExternal) {
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        this.checkEntityLimit(null, this.fCurrentEntity, offset, newlines);
                        offset = 0;
                        this.fCurrentEntity.position = newlines;
                        if (this.load(newlines, false, true)) {
                            counted = true;
                            break;
                        }
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        ++fCurrentEntity2.position;
                        ++offset;
                    }
                    else {
                        ++newlines;
                    }
                }
                else {
                    if (c != 10) {
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        --fCurrentEntity3.position;
                        break;
                    }
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    this.checkEntityLimit(null, this.fCurrentEntity, offset, newlines);
                    offset = 0;
                    this.fCurrentEntity.position = newlines;
                    if (this.load(newlines, false, true)) {
                        counted = true;
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (int i = offset; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = '\n';
            }
            final int length = this.fCurrentEntity.position - offset;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                this.checkEntityLimit(null, this.fCurrentEntity, offset, length);
                content.setValues(this.fCurrentEntity.ch, offset, length);
                return -1;
            }
        }
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (!XMLChar.isContent(c)) {
                final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                --fCurrentEntity5.position;
                break;
            }
        }
        final int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
        fCurrentEntity6.columnNumber += length - newlines;
        if (!counted) {
            this.checkEntityLimit(null, this.fCurrentEntity, offset, length);
        }
        content.setValues(this.fCurrentEntity.ch, offset, length);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c == 13 && this.isExternal) {
                c = 10;
            }
        }
        else {
            c = -1;
        }
        return c;
    }
    
    protected int scanLiteral(final int quote, final XMLString content, final boolean isNSURI) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false, false);
            this.fCurrentEntity.position = 0;
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[offset];
        int newlines = 0;
        if (this.whiteSpaceInfoNeeded) {
            this.whiteSpaceLen = 0;
        }
        if (c == 10 || (c == 13 && this.isExternal)) {
            do {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == 13 && this.isExternal) {
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        offset = 0;
                        this.fCurrentEntity.position = newlines;
                        if (this.load(newlines, false, true)) {
                            break;
                        }
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        ++fCurrentEntity2.position;
                        ++offset;
                    }
                    else {
                        ++newlines;
                    }
                }
                else {
                    if (c != 10) {
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        --fCurrentEntity3.position;
                        break;
                    }
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    offset = 0;
                    this.fCurrentEntity.position = newlines;
                    if (this.load(newlines, false, true)) {
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            int i;
            for (i = 0, i = offset; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = '\n';
                this.storeWhiteSpace(i);
            }
            final int length = this.fCurrentEntity.position - offset;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                content.setValues(this.fCurrentEntity.ch, offset, length);
                return -1;
            }
        }
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if ((c == quote && (!this.fCurrentEntity.literal || this.isExternal)) || c == 37) {
                break;
            }
            if (!XMLChar.isContent(c)) {
                break;
            }
            if (this.whiteSpaceInfoNeeded && c == 9) {
                this.storeWhiteSpace(this.fCurrentEntity.position);
            }
            final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
            ++fCurrentEntity5.position;
        }
        final int length2 = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
        fCurrentEntity6.columnNumber += length2 - newlines;
        this.checkEntityLimit(null, this.fCurrentEntity, offset, length2);
        if (isNSURI) {
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length2);
        }
        content.setValues(this.fCurrentEntity.ch, offset, length2);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (c == quote && this.fCurrentEntity.literal) {
                c = -1;
            }
        }
        else {
            c = -1;
        }
        return c;
    }
    
    private void storeWhiteSpace(final int whiteSpacePos) {
        if (this.whiteSpaceLen >= this.whiteSpaceLookup.length) {
            final int[] tmp = new int[this.whiteSpaceLookup.length + 100];
            System.arraycopy(this.whiteSpaceLookup, 0, tmp, 0, this.whiteSpaceLookup.length);
            this.whiteSpaceLookup = tmp;
        }
        this.whiteSpaceLookup[this.whiteSpaceLen++] = whiteSpacePos;
    }
    
    protected boolean scanData(final String delimiter, final XMLStringBuffer buffer) throws IOException {
        boolean done = false;
        final int delimLen = delimiter.length();
        final char charAt0 = delimiter.charAt(0);
        do {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.load(0, true, false);
            }
            for (boolean bNextEntity = false; this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen && !bNextEntity; bNextEntity = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false), this.fCurrentEntity.position = 0, this.fCurrentEntity.startPosition = 0) {
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            }
            if (this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen) {
                final int length = this.fCurrentEntity.count - this.fCurrentEntity.position;
                this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, this.fCurrentEntity.position, length);
                buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, length);
                final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                fCurrentEntity.columnNumber += this.fCurrentEntity.count;
                final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                this.fCurrentEntity.position = this.fCurrentEntity.count;
                this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
                this.load(0, true, false);
                return false;
            }
            int offset = this.fCurrentEntity.position;
            int c = this.fCurrentEntity.ch[offset];
            int newlines = 0;
            if (c == 10 || (c == 13 && this.isExternal)) {
                do {
                    c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                    if (c == 13 && this.isExternal) {
                        ++newlines;
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                            offset = 0;
                            this.fCurrentEntity.position = newlines;
                            if (this.load(newlines, false, true)) {
                                break;
                            }
                        }
                        if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                            final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                            ++fCurrentEntity4.position;
                            ++offset;
                        }
                        else {
                            ++newlines;
                        }
                    }
                    else {
                        if (c != 10) {
                            final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                            --fCurrentEntity5.position;
                            break;
                        }
                        ++newlines;
                        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        ++fCurrentEntity6.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                            continue;
                        }
                        offset = 0;
                        this.fCurrentEntity.position = newlines;
                        this.fCurrentEntity.count = newlines;
                        if (this.load(newlines, false, true)) {
                            break;
                        }
                        continue;
                    }
                } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
                for (int i = offset; i < this.fCurrentEntity.position; ++i) {
                    this.fCurrentEntity.ch[i] = '\n';
                }
                final int length2 = this.fCurrentEntity.position - offset;
                if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                    this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
                    buffer.append(this.fCurrentEntity.ch, offset, length2);
                    return true;
                }
            }
        Label_1024:
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == charAt0) {
                    final int delimOffset = this.fCurrentEntity.position - 1;
                    for (int j = 1; j < delimLen; ++j) {
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                            final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                            fCurrentEntity7.position -= j;
                            break Label_1024;
                        }
                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (delimiter.charAt(j) != c) {
                            final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                            fCurrentEntity8.position -= j;
                            break;
                        }
                    }
                    if (this.fCurrentEntity.position == delimOffset + delimLen) {
                        done = true;
                        break;
                    }
                    continue;
                }
                else {
                    if (c == 10 || (this.isExternal && c == 13)) {
                        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
                        --fCurrentEntity9.position;
                        break;
                    }
                    if (XMLChar.isInvalid(c)) {
                        final Entity.ScannedEntity fCurrentEntity10 = this.fCurrentEntity;
                        --fCurrentEntity10.position;
                        final int length2 = this.fCurrentEntity.position - offset;
                        final Entity.ScannedEntity fCurrentEntity11 = this.fCurrentEntity;
                        fCurrentEntity11.columnNumber += length2 - newlines;
                        this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
                        buffer.append(this.fCurrentEntity.ch, offset, length2);
                        return true;
                    }
                    continue;
                }
            }
            int length2 = this.fCurrentEntity.position - offset;
            final Entity.ScannedEntity fCurrentEntity12 = this.fCurrentEntity;
            fCurrentEntity12.columnNumber += length2 - newlines;
            this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
            if (done) {
                length2 -= delimLen;
            }
            buffer.append(this.fCurrentEntity.ch, offset, length2);
        } while (!done);
        return !done;
    }
    
    protected boolean skipChar(final int c, final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        final int offset = this.fCurrentEntity.position;
        final int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (cc == c) {
            final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.position;
            if (c == 10) {
                final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                ++fCurrentEntity2.lineNumber;
                this.fCurrentEntity.columnNumber = 1;
            }
            else {
                final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                ++fCurrentEntity3.columnNumber;
            }
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
            return true;
        }
        if (c == 10 && cc == 13 && this.isExternal) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = (char)cc;
                this.load(1, false, false);
            }
            final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
            ++fCurrentEntity4.position;
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                ++fCurrentEntity5.position;
            }
            final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
            ++fCurrentEntity6.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
            return true;
        }
        return false;
    }
    
    public boolean isSpace(final char ch) {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }
    
    protected boolean skipSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        if (this.fCurrentEntity == null) {
            return false;
        }
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        int offset = this.fCurrentEntity.position - 1;
        if (XMLChar.isSpace(c)) {
            do {
                boolean entityChanged = false;
                if (c == 10 || (this.isExternal && c == 13)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.invokeListeners(1);
                        this.fCurrentEntity.ch[0] = (char)c;
                        entityChanged = this.load(1, true, false);
                        if (!entityChanged) {
                            this.fCurrentEntity.position = 0;
                        }
                        else if (this.fCurrentEntity == null) {
                            return true;
                        }
                    }
                    if (c == 13 && this.isExternal && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                    }
                }
                else {
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    ++fCurrentEntity3.columnNumber;
                }
                this.checkEntityLimit(null, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
                offset = this.fCurrentEntity.position;
                if (!entityChanged) {
                    final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.position;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true, true);
                    if (this.fCurrentEntity == null) {
                        return true;
                    }
                    continue;
                }
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }
    
    public boolean arrangeCapacity(final int length) throws IOException {
        return this.arrangeCapacity(length, false);
    }
    
    public boolean arrangeCapacity(final int length, final boolean changeEntity) throws IOException {
        if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length) {
            return true;
        }
        boolean entityChanged = false;
        while (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
            if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < length) {
                this.invokeListeners(0);
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
                this.fCurrentEntity.count -= this.fCurrentEntity.position;
                this.fCurrentEntity.position = 0;
            }
            if (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
                final int pos = this.fCurrentEntity.position;
                this.invokeListeners(pos);
                entityChanged = this.load(this.fCurrentEntity.count, changeEntity, false);
                this.fCurrentEntity.position = pos;
                if (entityChanged) {
                    break;
                }
                continue;
            }
        }
        return this.fCurrentEntity.count - this.fCurrentEntity.position >= length;
    }
    
    protected boolean skipString(final String s) throws IOException {
        final int length = s.length();
        if (this.arrangeCapacity(length, false)) {
            final int beforeSkip = this.fCurrentEntity.position;
            int afterSkip = this.fCurrentEntity.position + length - 1;
            int i = length - 1;
            while (s.charAt(i--) == this.fCurrentEntity.ch[afterSkip]) {
                if (afterSkip-- == beforeSkip) {
                    this.fCurrentEntity.position += length;
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    fCurrentEntity.columnNumber += length;
                    if (!this.detectingVersion) {
                        this.checkEntityLimit(null, this.fCurrentEntity, beforeSkip, length);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    protected boolean skipString(final char[] s) throws IOException {
        final int length = s.length;
        if (this.arrangeCapacity(length, false)) {
            int beforeSkip = this.fCurrentEntity.position;
            for (int i = 0; i < length; ++i) {
                if (this.fCurrentEntity.ch[beforeSkip++] != s[i]) {
                    return false;
                }
            }
            this.fCurrentEntity.position += length;
            final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            fCurrentEntity.columnNumber += length;
            if (!this.detectingVersion) {
                this.checkEntityLimit(null, this.fCurrentEntity, beforeSkip, length);
            }
            return true;
        }
        return false;
    }
    
    final boolean load(final int offset, final boolean changeEntity, final boolean notify) throws IOException {
        if (notify) {
            this.invokeListeners(offset);
        }
        this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
        int length = this.fCurrentEntity.ch.length - offset;
        if (!this.fCurrentEntity.mayReadChunks && length > 64) {
            length = 64;
        }
        final int count = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, offset, length);
        boolean entityChanged = false;
        if (count != -1) {
            if (count != 0) {
                this.fCurrentEntity.fLastCount = count;
                this.fCurrentEntity.count = count + offset;
                this.fCurrentEntity.position = offset;
            }
        }
        else {
            this.fCurrentEntity.count = offset;
            this.fCurrentEntity.position = offset;
            entityChanged = true;
            if (changeEntity) {
                this.fEntityManager.endEntity();
                if (this.fCurrentEntity == null) {
                    throw XMLEntityScanner.END_OF_DOCUMENT_ENTITY;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true, false);
                }
            }
        }
        return entityChanged;
    }
    
    protected Reader createReader(final InputStream inputStream, String encoding, final Boolean isBigEndian) throws IOException {
        if (encoding == null) {
            encoding = "UTF-8";
        }
        final String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
        if (ENCODING.equals("UTF-8")) {
            return new UTF8Reader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        if (ENCODING.equals("US-ASCII")) {
            return new ASCIIReader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        if (ENCODING.equals("ISO-10646-UCS-4")) {
            if (isBigEndian != null) {
                final boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, (short)8);
                }
                return new UCSReader(inputStream, (short)4);
            }
            else {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
            }
        }
        if (ENCODING.equals("ISO-10646-UCS-2")) {
            if (isBigEndian != null) {
                final boolean isBE = isBigEndian;
                if (isBE) {
                    return new UCSReader(inputStream, (short)2);
                }
                return new UCSReader(inputStream, (short)1);
            }
            else {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
            }
        }
        final boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
        final boolean validJava = XMLChar.isValidJavaEncoding(encoding);
        if (!validIANA || (this.fAllowJavaEncodings && !validJava)) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
            encoding = "ISO-8859-1";
        }
        String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
        if (javaEncoding == null) {
            if (this.fAllowJavaEncodings) {
                javaEncoding = encoding;
            }
            else {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
                javaEncoding = "ISO8859_1";
            }
        }
        else if (javaEncoding.equals("ASCII")) {
            return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
        }
        return new InputStreamReader(inputStream, javaEncoding);
    }
    
    protected Object[] getEncodingName(final byte[] b4, final int count) {
        if (count < 2) {
            return new Object[] { "UTF-8", null };
        }
        final int b5 = b4[0] & 0xFF;
        final int b6 = b4[1] & 0xFF;
        if (b5 == 254 && b6 == 255) {
            return new Object[] { "UTF-16BE", new Boolean(true) };
        }
        if (b5 == 255 && b6 == 254) {
            return new Object[] { "UTF-16LE", new Boolean(false) };
        }
        if (count < 3) {
            return new Object[] { "UTF-8", null };
        }
        final int b7 = b4[2] & 0xFF;
        if (b5 == 239 && b6 == 187 && b7 == 191) {
            return new Object[] { "UTF-8", null };
        }
        if (count < 4) {
            return new Object[] { "UTF-8", null };
        }
        final int b8 = b4[3] & 0xFF;
        if (b5 == 0 && b6 == 0 && b7 == 0 && b8 == 60) {
            return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
        }
        if (b5 == 60 && b6 == 0 && b7 == 0 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
        }
        if (b5 == 0 && b6 == 0 && b7 == 60 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", null };
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 0) {
            return new Object[] { "ISO-10646-UCS-4", null };
        }
        if (b5 == 0 && b6 == 60 && b7 == 0 && b8 == 63) {
            return new Object[] { "UTF-16BE", new Boolean(true) };
        }
        if (b5 == 60 && b6 == 0 && b7 == 63 && b8 == 0) {
            return new Object[] { "UTF-16LE", new Boolean(false) };
        }
        if (b5 == 76 && b6 == 111 && b7 == 167 && b8 == 148) {
            return new Object[] { "CP037", null };
        }
        return new Object[] { "UTF-8", null };
    }
    
    final void print() {
    }
    
    public void registerListener(final XMLBufferListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    public void invokeListeners(final int loadPos) {
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).refresh(loadPos);
        }
    }
    
    protected final boolean skipDeclSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, false);
        }
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (XMLChar.isSpace(c)) {
            final boolean external = this.fCurrentEntity.isExternal();
            do {
                boolean entityChanged = false;
                if (c == 10 || (external && c == 13)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = (char)c;
                        entityChanged = this.load(1, true, false);
                        if (!entityChanged) {
                            this.fCurrentEntity.position = 0;
                        }
                    }
                    if (c == 13 && external && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                    }
                }
                else {
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    ++fCurrentEntity3.columnNumber;
                }
                if (!entityChanged) {
                    final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.position;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true, false);
                }
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }
    
    static {
        VALID_NAMES = new boolean[127];
        END_OF_DOCUMENT_ENTITY = new EOFException() {
            private static final long serialVersionUID = 980337771224675268L;
            
            @Override
            public Throwable fillInStackTrace() {
                return this;
            }
        };
        for (int i = 65; i <= 90; ++i) {
            XMLEntityScanner.VALID_NAMES[i] = true;
        }
        for (int i = 97; i <= 122; ++i) {
            XMLEntityScanner.VALID_NAMES[i] = true;
        }
        for (int i = 48; i <= 57; ++i) {
            XMLEntityScanner.VALID_NAMES[i] = true;
        }
        XMLEntityScanner.VALID_NAMES[45] = true;
        XMLEntityScanner.VALID_NAMES[46] = true;
        XMLEntityScanner.VALID_NAMES[58] = true;
        XMLEntityScanner.VALID_NAMES[95] = true;
    }
}
