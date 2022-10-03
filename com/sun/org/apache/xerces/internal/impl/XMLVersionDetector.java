package com.sun.org.apache.xerces.internal.impl;

import com.sun.xml.internal.stream.Entity;
import java.io.IOException;
import java.io.EOFException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.SymbolTable;

public class XMLVersionDetector
{
    private static final char[] XML11_VERSION;
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String fVersionSymbol;
    protected static final String fXMLSymbol;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected String fEncoding;
    private XMLString fVersionNum;
    private final char[] fExpectedVersionString;
    
    public XMLVersionDetector() {
        this.fEncoding = null;
        this.fVersionNum = new XMLString();
        this.fExpectedVersionString = new char[] { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' ' };
    }
    
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        for (int i = 14; i < this.fExpectedVersionString.length; ++i) {
            this.fExpectedVersionString[i] = ' ';
        }
    }
    
    public void startDocumentParsing(final XMLEntityHandler scanner, final short version) {
        if (version == 1) {
            this.fEntityManager.setScannerVersion((short)1);
        }
        else {
            this.fEntityManager.setScannerVersion((short)2);
        }
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fEntityManager.setEntityHandler(scanner);
        scanner.startEntity(XMLVersionDetector.fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
    }
    
    public short determineDocVersion(final XMLInputSource inputSource) throws IOException {
        this.fEncoding = this.fEntityManager.setupCurrentEntity(false, XMLVersionDetector.fXMLSymbol, inputSource, false, true);
        this.fEntityManager.setScannerVersion((short)1);
        final XMLEntityScanner scanner = this.fEntityManager.getEntityScanner();
        scanner.detectingVersion = true;
        try {
            if (!scanner.skipString("<?xml")) {
                scanner.detectingVersion = false;
                return 1;
            }
            if (!scanner.skipDeclSpaces()) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
                scanner.detectingVersion = false;
                return 1;
            }
            if (!scanner.skipString("version")) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
                scanner.detectingVersion = false;
                return 1;
            }
            scanner.skipDeclSpaces();
            if (scanner.peekChar() != 61) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
                scanner.detectingVersion = false;
                return 1;
            }
            scanner.scanChar(null);
            scanner.skipDeclSpaces();
            final int quoteChar = scanner.scanChar(null);
            this.fExpectedVersionString[14] = (char)quoteChar;
            for (int versionPos = 0; versionPos < XMLVersionDetector.XML11_VERSION.length; ++versionPos) {
                this.fExpectedVersionString[15 + versionPos] = (char)scanner.scanChar(null);
            }
            this.fExpectedVersionString[18] = (char)scanner.scanChar(null);
            this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
            int matched;
            for (matched = 0; matched < XMLVersionDetector.XML11_VERSION.length && this.fExpectedVersionString[15 + matched] == XMLVersionDetector.XML11_VERSION[matched]; ++matched) {}
            scanner.detectingVersion = false;
            if (matched == XMLVersionDetector.XML11_VERSION.length) {
                return 2;
            }
            return 1;
        }
        catch (final EOFException e) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
            scanner.detectingVersion = false;
            return 1;
        }
    }
    
    private void fixupCurrentEntity(final XMLEntityManager manager, final char[] scannedChars, final int length) {
        final Entity.ScannedEntity currentEntity = manager.getCurrentEntity();
        if (currentEntity.count - currentEntity.position + length > currentEntity.ch.length) {
            final char[] tempCh = currentEntity.ch;
            System.arraycopy(tempCh, 0, currentEntity.ch = new char[length + currentEntity.count - currentEntity.position + 1], 0, tempCh.length);
        }
        if (currentEntity.position < length) {
            System.arraycopy(currentEntity.ch, currentEntity.position, currentEntity.ch, length, currentEntity.count - currentEntity.position);
            final Entity.ScannedEntity scannedEntity = currentEntity;
            scannedEntity.count += length - currentEntity.position;
        }
        else {
            for (int i = length; i < currentEntity.position; ++i) {
                currentEntity.ch[i] = ' ';
            }
        }
        System.arraycopy(scannedChars, 0, currentEntity.ch, 0, length);
        currentEntity.position = 0;
        currentEntity.baseCharOffset = 0;
        currentEntity.startPosition = 0;
        final Entity.ScannedEntity scannedEntity2 = currentEntity;
        final Entity.ScannedEntity scannedEntity3 = currentEntity;
        final int n = 1;
        scannedEntity3.lineNumber = n;
        scannedEntity2.columnNumber = n;
    }
    
    static {
        XML11_VERSION = new char[] { '1', '.', '1' };
        fVersionSymbol = "version".intern();
        fXMLSymbol = "[xml]".intern();
    }
}
