package org.apache.xerces.impl;

import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.XMLChar;
import java.io.IOException;
import org.apache.xerces.impl.io.UCSReader;
import java.util.Locale;
import org.apache.xerces.util.SymbolTable;
import java.io.EOFException;
import org.apache.xerces.xni.XMLLocator;

public class XMLEntityScanner implements XMLLocator
{
    private static final boolean DEBUG_ENCODINGS = false;
    private static final boolean DEBUG_BUFFER = false;
    private static final EOFException END_OF_DOCUMENT_ENTITY;
    private XMLEntityManager fEntityManager;
    protected XMLEntityManager.ScannedEntity fCurrentEntity;
    protected SymbolTable fSymbolTable;
    protected int fBufferSize;
    protected XMLErrorReporter fErrorReporter;
    
    public XMLEntityScanner() {
        this.fEntityManager = null;
        this.fCurrentEntity = null;
        this.fSymbolTable = null;
        this.fBufferSize = 2048;
    }
    
    public final String getBaseSystemId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
    }
    
    public final void setEncoding(final String encoding) throws IOException {
        if (this.fCurrentEntity.stream != null && (this.fCurrentEntity.encoding == null || !this.fCurrentEntity.encoding.equals(encoding))) {
            if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
                final String upperCase = encoding.toUpperCase(Locale.ENGLISH);
                if (upperCase.equals("UTF-16")) {
                    return;
                }
                if (upperCase.equals("ISO-10646-UCS-4")) {
                    if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
                    }
                    else {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
                    }
                    return;
                }
                if (upperCase.equals("ISO-10646-UCS-2")) {
                    if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
                    }
                    else {
                        this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
                    }
                    return;
                }
            }
            this.fCurrentEntity.setReader(this.fCurrentEntity.stream, encoding, null);
            this.fCurrentEntity.encoding = encoding;
        }
    }
    
    public final void setXMLVersion(final String xmlVersion) {
        this.fCurrentEntity.xmlVersion = xmlVersion;
    }
    
    public final boolean isExternal() {
        return this.fCurrentEntity.isExternal();
    }
    
    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return (c != '\r') ? c : '\n';
        }
        return c;
    }
    
    public int scanChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean external = false;
        if (n == 10 || (n == 13 && (external = this.fCurrentEntity.isExternal()))) {
            final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = (char)n;
                this.load(1, false);
            }
            if (n == 13 && external) {
                if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n') {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                }
                n = 10;
            }
        }
        final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
        ++fCurrentEntity3.columnNumber;
        return n;
    }
    
    public String scanNmtoken() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                final int n = this.fCurrentEntity.position - position;
                if (n == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(position, n);
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n);
                }
                position = 0;
                if (this.load(n, false)) {
                    break;
                }
                continue;
            }
        }
        final int n2 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
        fCurrentEntity.columnNumber += n2;
        String addSymbol = null;
        if (n2 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n2);
        }
        return addSymbol;
    }
    
    public String scanName() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        if (XMLChar.isNameStart(this.fCurrentEntity.ch[position])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[position];
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                }
            }
            while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n = this.fCurrentEntity.position - position;
                    if (n == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n);
                    }
                    position = 0;
                    if (this.load(n, false)) {
                        break;
                    }
                    continue;
                }
            }
        }
        final int n2 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
        fCurrentEntity2.columnNumber += n2;
        String addSymbol = null;
        if (n2 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n2);
        }
        return addSymbol;
    }
    
    public String scanNCName() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        if (XMLChar.isNCNameStart(this.fCurrentEntity.ch[position])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[position];
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                }
            }
            while (XMLChar.isNCName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n = this.fCurrentEntity.position - position;
                    if (n == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n);
                    }
                    position = 0;
                    if (this.load(n, false)) {
                        break;
                    }
                    continue;
                }
            }
        }
        final int n2 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
        fCurrentEntity2.columnNumber += n2;
        String addSymbol = null;
        if (n2 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n2);
        }
        return addSymbol;
    }
    
    public boolean scanQName(final QName qName) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        if (XMLChar.isNCNameStart(this.fCurrentEntity.ch[position])) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[position];
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    qName.setValues(null, addSymbol, addSymbol, null);
                    return true;
                }
            }
            int position2 = -1;
            while (XMLChar.isName(this.fCurrentEntity.ch[this.fCurrentEntity.position])) {
                if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == ':') {
                    if (position2 != -1) {
                        break;
                    }
                    position2 = this.fCurrentEntity.position;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n = this.fCurrentEntity.position - position;
                    if (n == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n);
                    }
                    if (position2 != -1) {
                        position2 -= position;
                    }
                    position = 0;
                    if (this.load(n, false)) {
                        break;
                    }
                    continue;
                }
            }
            final int n2 = this.fCurrentEntity.position - position;
            final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
            fCurrentEntity2.columnNumber += n2;
            if (n2 > 0) {
                String addSymbol2 = null;
                final String addSymbol3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n2);
                String addSymbol4;
                if (position2 != -1) {
                    final int n3 = position2 - position;
                    addSymbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n3);
                    final int n4 = n2 - n3 - 1;
                    final int n5 = position2 + 1;
                    if (!XMLChar.isNCNameStart(this.fCurrentEntity.ch[n5])) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
                    }
                    addSymbol4 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, n5, n4);
                }
                else {
                    addSymbol4 = addSymbol3;
                }
                qName.setValues(addSymbol2, addSymbol4, addSymbol3, null);
                return true;
            }
        }
        return false;
    }
    
    public int scanContent(final XMLString xmlString) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        int n = 0;
        final boolean external = this.fCurrentEntity.isExternal();
        if (c == '\n' || (c == '\r' && external)) {
            do {
                final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c2 == '\r' && external) {
                    ++n;
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        position = 0;
                        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = n;
                        this.fCurrentEntity.startPosition = n;
                        if (this.load(n, false)) {
                            break;
                        }
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++position;
                    }
                    else {
                        ++n;
                    }
                }
                else {
                    if (c2 != '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                        --fCurrentEntity4.position;
                        break;
                    }
                    ++n;
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    position = 0;
                    final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    fCurrentEntity6.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = n;
                    this.fCurrentEntity.startPosition = n;
                    if (this.load(n, false)) {
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (int i = position; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = '\n';
            }
            final int n2 = this.fCurrentEntity.position - position;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xmlString.setValues(this.fCurrentEntity.ch, position, n2);
                return -1;
            }
        }
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            if (!XMLChar.isContent(this.fCurrentEntity.ch[this.fCurrentEntity.position++])) {
                final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                --fCurrentEntity7.position;
                break;
            }
        }
        final int n3 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
        fCurrentEntity8.columnNumber += n3 - n;
        xmlString.setValues(this.fCurrentEntity.ch, position, n3);
        int n4;
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (n4 == 13 && external) {
                n4 = 10;
            }
        }
        else {
            n4 = -1;
        }
        return n4;
    }
    
    public int scanLiteral(final int n, final XMLString xmlString) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        int n2 = 0;
        final boolean external = this.fCurrentEntity.isExternal();
        if (c == '\n' || (c == '\r' && external)) {
            do {
                final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c2 == '\r' && external) {
                    ++n2;
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        position = 0;
                        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = n2;
                        this.fCurrentEntity.startPosition = n2;
                        if (this.load(n2, false)) {
                            break;
                        }
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++position;
                    }
                    else {
                        ++n2;
                    }
                }
                else {
                    if (c2 != '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                        --fCurrentEntity4.position;
                        break;
                    }
                    ++n2;
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    position = 0;
                    final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    fCurrentEntity6.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = n2;
                    this.fCurrentEntity.startPosition = n2;
                    if (this.load(n2, false)) {
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (int i = position; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = '\n';
            }
            final int n3 = this.fCurrentEntity.position - position;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xmlString.setValues(this.fCurrentEntity.ch, position, n3);
                return -1;
            }
        }
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if ((c3 == n && (!this.fCurrentEntity.literal || external)) || c3 == '%' || !XMLChar.isContent(c3)) {
                final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                --fCurrentEntity7.position;
                break;
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
        fCurrentEntity8.columnNumber += n4 - n2;
        xmlString.setValues(this.fCurrentEntity.ch, position, n4);
        int n5;
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n5 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (n5 == n && this.fCurrentEntity.literal) {
                n5 = -1;
            }
        }
        else {
            n5 = -1;
        }
        return n5;
    }
    
    public boolean scanData(final String s, final XMLStringBuffer xmlStringBuffer) throws IOException {
        boolean b = false;
        final int length = s.length();
        final char char1 = s.charAt(0);
        final boolean external = this.fCurrentEntity.isExternal();
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        for (boolean load = false; this.fCurrentEntity.position > this.fCurrentEntity.count - length && !load; load = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false), this.fCurrentEntity.position = 0, this.fCurrentEntity.startPosition = 0) {
            System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        }
        if (this.fCurrentEntity.position > this.fCurrentEntity.count - length) {
            xmlStringBuffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.count - this.fCurrentEntity.position);
            final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            fCurrentEntity.columnNumber += this.fCurrentEntity.count;
            final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
            fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
            this.fCurrentEntity.position = this.fCurrentEntity.count;
            this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
            this.load(0, true);
            return false;
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        int count = 0;
        if (c == '\n' || (c == '\r' && external)) {
            do {
                final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c2 == '\r' && external) {
                    ++count;
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    ++fCurrentEntity3.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        position = 0;
                        final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                        fCurrentEntity4.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = count;
                        this.fCurrentEntity.startPosition = count;
                        if (this.load(count, false)) {
                            break;
                        }
                    }
                    if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                        ++fCurrentEntity5.position;
                        ++position;
                    }
                    else {
                        ++count;
                    }
                }
                else {
                    if (c2 != '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.position;
                        break;
                    }
                    ++count;
                    final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    ++fCurrentEntity7.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    position = 0;
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    fCurrentEntity8.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = count;
                    this.fCurrentEntity.startPosition = count;
                    this.fCurrentEntity.count = count;
                    if (this.load(count, false)) {
                        break;
                    }
                    continue;
                }
            } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
            for (int i = position; i < this.fCurrentEntity.position; ++i) {
                this.fCurrentEntity.ch[i] = '\n';
            }
            final int n = this.fCurrentEntity.position - position;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                xmlStringBuffer.append(this.fCurrentEntity.ch, position, n);
                return true;
            }
        }
    Label_1040:
        while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
            final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c3 == char1) {
                final int n2 = this.fCurrentEntity.position - 1;
                for (int j = 1; j < length; ++j) {
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
                        fCurrentEntity9.position -= j;
                        break Label_1040;
                    }
                    if (s.charAt(j) != this.fCurrentEntity.ch[this.fCurrentEntity.position++]) {
                        final XMLEntityManager.ScannedEntity fCurrentEntity10 = this.fCurrentEntity;
                        --fCurrentEntity10.position;
                        break;
                    }
                }
                if (this.fCurrentEntity.position == n2 + length) {
                    b = true;
                    break;
                }
                continue;
            }
            else {
                if (c3 == '\n' || (external && c3 == '\r')) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity11 = this.fCurrentEntity;
                    --fCurrentEntity11.position;
                    break;
                }
                if (XMLChar.isInvalid(c3)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity12 = this.fCurrentEntity;
                    --fCurrentEntity12.position;
                    final int n3 = this.fCurrentEntity.position - position;
                    final XMLEntityManager.ScannedEntity fCurrentEntity13 = this.fCurrentEntity;
                    fCurrentEntity13.columnNumber += n3 - count;
                    xmlStringBuffer.append(this.fCurrentEntity.ch, position, n3);
                    return true;
                }
                continue;
            }
        }
        int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity14 = this.fCurrentEntity;
        fCurrentEntity14.columnNumber += n4 - count;
        if (b) {
            n4 -= length;
        }
        xmlStringBuffer.append(this.fCurrentEntity.ch, position, n4);
        return !b;
    }
    
    public boolean skipChar(final int n) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (c == n) {
            final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.position;
            if (n == 10) {
                final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                ++fCurrentEntity2.lineNumber;
                this.fCurrentEntity.columnNumber = 1;
            }
            else {
                final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                ++fCurrentEntity3.columnNumber;
            }
            return true;
        }
        if (n == 10 && c == '\r' && this.fCurrentEntity.isExternal()) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.load(1, false);
            }
            final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
            ++fCurrentEntity4.position;
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
                final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                ++fCurrentEntity5.position;
            }
            final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
            ++fCurrentEntity6.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            return true;
        }
        return false;
    }
    
    public boolean skipSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (XMLChar.isSpace(c)) {
            final boolean external = this.fCurrentEntity.isExternal();
            do {
                boolean load = false;
                if (c == '\n' || (external && c == '\r')) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        load = this.load(1, true);
                        if (!load) {
                            this.fCurrentEntity.position = 0;
                            this.fCurrentEntity.startPosition = 0;
                        }
                    }
                    if (c == '\r' && external && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                    }
                }
                else {
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    ++fCurrentEntity3.columnNumber;
                }
                if (!load) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.position;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true);
                }
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }
    
    public final boolean skipDeclSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (XMLChar.isSpace(c)) {
            final boolean external = this.fCurrentEntity.isExternal();
            do {
                boolean load = false;
                if (c == '\n' || (external && c == '\r')) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        load = this.load(1, true);
                        if (!load) {
                            this.fCurrentEntity.position = 0;
                            this.fCurrentEntity.startPosition = 0;
                        }
                    }
                    if (c == '\r' && external && this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                    }
                }
                else {
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    ++fCurrentEntity3.columnNumber;
                }
                if (!load) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                    ++fCurrentEntity4.position;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true);
                }
            } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
            return true;
        }
        return false;
    }
    
    public boolean skipString(final String s) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != s.charAt(i)) {
                final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                fCurrentEntity.position -= i + 1;
                return false;
            }
            if (i < length - 1 && this.fCurrentEntity.position == this.fCurrentEntity.count) {
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
                if (this.load(i + 1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    fCurrentEntity2.startPosition -= i + 1;
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    fCurrentEntity3.position -= i + 1;
                    return false;
                }
            }
        }
        final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
        fCurrentEntity4.columnNumber += length;
        return true;
    }
    
    public final String getPublicId() {
        return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
    }
    
    public final String getExpandedSystemId() {
        if (this.fCurrentEntity == null) {
            return null;
        }
        if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getExpandedSystemId() != null) {
            return this.fCurrentEntity.entityLocation.getExpandedSystemId();
        }
        return this.fCurrentEntity.getExpandedSystemId();
    }
    
    public final String getLiteralSystemId() {
        if (this.fCurrentEntity == null) {
            return null;
        }
        if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation.getLiteralSystemId() != null) {
            return this.fCurrentEntity.entityLocation.getLiteralSystemId();
        }
        return this.fCurrentEntity.getLiteralSystemId();
    }
    
    public final int getLineNumber() {
        if (this.fCurrentEntity == null) {
            return -1;
        }
        if (this.fCurrentEntity.isExternal()) {
            return this.fCurrentEntity.lineNumber;
        }
        return this.fCurrentEntity.getLineNumber();
    }
    
    public final int getColumnNumber() {
        if (this.fCurrentEntity == null) {
            return -1;
        }
        if (this.fCurrentEntity.isExternal()) {
            return this.fCurrentEntity.columnNumber;
        }
        return this.fCurrentEntity.getColumnNumber();
    }
    
    public final int getCharacterOffset() {
        if (this.fCurrentEntity == null) {
            return -1;
        }
        if (this.fCurrentEntity.isExternal()) {
            return this.fCurrentEntity.baseCharOffset + (this.fCurrentEntity.position - this.fCurrentEntity.startPosition);
        }
        return this.fCurrentEntity.getCharacterOffset();
    }
    
    public final String getEncoding() {
        if (this.fCurrentEntity == null) {
            return null;
        }
        if (this.fCurrentEntity.isExternal()) {
            return this.fCurrentEntity.encoding;
        }
        return this.fCurrentEntity.getEncoding();
    }
    
    public final String getXMLVersion() {
        if (this.fCurrentEntity == null) {
            return null;
        }
        if (this.fCurrentEntity.isExternal()) {
            return this.fCurrentEntity.xmlVersion;
        }
        return this.fCurrentEntity.getXMLVersion();
    }
    
    public final void setCurrentEntity(final XMLEntityManager.ScannedEntity fCurrentEntity) {
        this.fCurrentEntity = fCurrentEntity;
    }
    
    public final void setBufferSize(final int fBufferSize) {
        this.fBufferSize = fBufferSize;
    }
    
    public final void reset(final SymbolTable fSymbolTable, final XMLEntityManager fEntityManager, final XMLErrorReporter fErrorReporter) {
        this.fCurrentEntity = null;
        this.fSymbolTable = fSymbolTable;
        this.fEntityManager = fEntityManager;
        this.fErrorReporter = fErrorReporter;
    }
    
    final boolean load(final int startPosition, final boolean b) throws IOException {
        final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
        fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        int n = this.fCurrentEntity.ch.length - startPosition;
        if (!this.fCurrentEntity.mayReadChunks && n > 64) {
            n = 64;
        }
        final int read = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, startPosition, n);
        boolean b2 = false;
        if (read != -1) {
            if (read != 0) {
                this.fCurrentEntity.count = read + startPosition;
                this.fCurrentEntity.position = startPosition;
                this.fCurrentEntity.startPosition = startPosition;
            }
        }
        else {
            this.fCurrentEntity.count = startPosition;
            this.fCurrentEntity.position = startPosition;
            this.fCurrentEntity.startPosition = startPosition;
            b2 = true;
            if (b) {
                this.fEntityManager.endEntity();
                if (this.fCurrentEntity == null) {
                    throw XMLEntityScanner.END_OF_DOCUMENT_ENTITY;
                }
                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    this.load(0, true);
                }
            }
        }
        return b2;
    }
    
    final void resizeBuffer(final int n, final int n2) {
        final char[] ch = new char[this.fCurrentEntity.ch.length << 1];
        System.arraycopy(this.fCurrentEntity.ch, n, ch, 0, n2);
        this.fCurrentEntity.ch = ch;
    }
    
    static {
        END_OF_DOCUMENT_ENTITY = new EOFException() {
            private static final long serialVersionUID = 980337771224675268L;
            
            public Throwable fillInStackTrace() {
                return this;
            }
        };
    }
}
