package org.apache.xerces.impl;

import java.io.IOException;
import java.io.EOFException;
import java.io.CharConversionException;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.SymbolTable;

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
    private final char[] fExpectedVersionString;
    
    public XMLVersionDetector() {
        this.fEncoding = null;
        this.fExpectedVersionString = new char[] { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' ' };
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityManager = (XMLEntityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        for (int i = 14; i < this.fExpectedVersionString.length; ++i) {
            this.fExpectedVersionString[i] = ' ';
        }
    }
    
    public void startDocumentParsing(final XMLEntityHandler entityHandler, final short n) {
        if (n == 1) {
            this.fEntityManager.setScannerVersion((short)1);
        }
        else {
            this.fEntityManager.setScannerVersion((short)2);
        }
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fEntityManager.setEntityHandler(entityHandler);
        entityHandler.startEntity(XMLVersionDetector.fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
    }
    
    public short determineDocVersion(final XMLInputSource xmlInputSource) throws IOException {
        this.fEncoding = this.fEntityManager.setupCurrentEntity(XMLVersionDetector.fXMLSymbol, xmlInputSource, false, true);
        this.fEntityManager.setScannerVersion((short)1);
        final XMLEntityScanner entityScanner = this.fEntityManager.getEntityScanner();
        try {
            if (!entityScanner.skipString("<?xml")) {
                return 1;
            }
            if (!entityScanner.skipDeclSpaces()) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
                return 1;
            }
            if (!entityScanner.skipString("version")) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
                return 1;
            }
            entityScanner.skipDeclSpaces();
            if (entityScanner.peekChar() != 61) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
                return 1;
            }
            entityScanner.scanChar();
            entityScanner.skipDeclSpaces();
            this.fExpectedVersionString[14] = (char)entityScanner.scanChar();
            for (int i = 0; i < XMLVersionDetector.XML11_VERSION.length; ++i) {
                this.fExpectedVersionString[15 + i] = (char)entityScanner.scanChar();
            }
            this.fExpectedVersionString[18] = (char)entityScanner.scanChar();
            this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
            int n;
            for (n = 0; n < XMLVersionDetector.XML11_VERSION.length && this.fExpectedVersionString[15 + n] == XMLVersionDetector.XML11_VERSION[n]; ++n) {}
            return (short)((n == XMLVersionDetector.XML11_VERSION.length) ? 2 : 1);
        }
        catch (final MalformedByteSequenceException ex) {
            this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
            return -1;
        }
        catch (final CharConversionException ex2) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
            return -1;
        }
        catch (final EOFException ex3) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
            return -1;
        }
    }
    
    private void fixupCurrentEntity(final XMLEntityManager xmlEntityManager, final char[] array, final int n) {
        final XMLEntityManager.ScannedEntity currentEntity = xmlEntityManager.getCurrentEntity();
        if (currentEntity.count - currentEntity.position + n > currentEntity.ch.length) {
            final char[] ch = currentEntity.ch;
            System.arraycopy(ch, 0, currentEntity.ch = new char[n + currentEntity.count - currentEntity.position + 1], 0, ch.length);
        }
        if (currentEntity.position < n) {
            System.arraycopy(currentEntity.ch, currentEntity.position, currentEntity.ch, n, currentEntity.count - currentEntity.position);
            final XMLEntityManager.ScannedEntity scannedEntity = currentEntity;
            scannedEntity.count += n - currentEntity.position;
        }
        else {
            for (int i = n; i < currentEntity.position; ++i) {
                currentEntity.ch[i] = ' ';
            }
        }
        System.arraycopy(array, 0, currentEntity.ch, 0, n);
        currentEntity.position = 0;
        currentEntity.baseCharOffset = 0;
        currentEntity.startPosition = 0;
        final XMLEntityManager.ScannedEntity scannedEntity2 = currentEntity;
        final XMLEntityManager.ScannedEntity scannedEntity3 = currentEntity;
        final int n2 = 1;
        scannedEntity3.lineNumber = n2;
        scannedEntity2.columnNumber = n2;
    }
    
    static {
        XML11_VERSION = new char[] { '1', '.', '1' };
        fVersionSymbol = "version".intern();
        fXMLSymbol = "[xml]".intern();
    }
}
