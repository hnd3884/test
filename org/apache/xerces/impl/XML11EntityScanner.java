package org.apache.xerces.impl;

import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XML11Char;
import java.io.IOException;

public class XML11EntityScanner extends XMLEntityScanner
{
    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return (c != '\r' && c != '\u0085' && c != '\u2028') ? c : '\n';
        }
        return c;
    }
    
    public int scanChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int n = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean external = false;
        if (n == 10 || ((n == 13 || n == 133 || n == 8232) && (external = this.fCurrentEntity.isExternal()))) {
            final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = (char)n;
                this.load(1, false);
            }
            if (n == 13 && external) {
                final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c != '\n' && c != '\u0085') {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                }
            }
            n = 10;
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
        while (true) {
            final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(c)) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
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
            else {
                if (!XML11Char.isXML11NameHighSurrogate(c)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n2 = this.fCurrentEntity.position - position;
                    if (n2 == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n2);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n2);
                    }
                    position = 0;
                    if (this.load(n2, false)) {
                        final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                        --fCurrentEntity.startPosition;
                        final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                        break;
                    }
                }
                final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(c2) || !XML11Char.isXML11Name(XMLChar.supplemental(c, c2))) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int n3 = this.fCurrentEntity.position - position;
                if (n3 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(position, n3);
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n3);
                }
                position = 0;
                if (this.load(n3, false)) {
                    break;
                }
                continue;
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
        fCurrentEntity4.columnNumber += n4;
        String addSymbol = null;
        if (n4 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n4);
        }
        return addSymbol;
    }
    
    public String scanName() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        if (XML11Char.isXML11NameStart(c)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                }
            }
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(c)) {
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.startPosition;
                    return null;
                }
            }
            final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(c2) || !XML11Char.isXML11NameStart(XMLChar.supplemental(c, c2))) {
                final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.fCurrentEntity.ch[1] = c2;
                position = 0;
                if (this.load(2, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                }
            }
        }
        while (true) {
            final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(c3)) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
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
            else {
                if (!XML11Char.isXML11NameHighSurrogate(c3)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n2 = this.fCurrentEntity.position - position;
                    if (n2 == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n2);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n2);
                    }
                    position = 0;
                    if (this.load(n2, false)) {
                        final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.position;
                        final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.startPosition;
                        break;
                    }
                }
                final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(c4) || !XML11Char.isXML11Name(XMLChar.supplemental(c3, c4))) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int n3 = this.fCurrentEntity.position - position;
                if (n3 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(position, n3);
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n3);
                }
                position = 0;
                if (this.load(n3, false)) {
                    break;
                }
                continue;
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += n4;
        String addSymbol = null;
        if (n4 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n4);
        }
        return addSymbol;
    }
    
    public String scanNCName() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        if (XML11Char.isXML11NCNameStart(c)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                }
            }
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(c)) {
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.startPosition;
                    return null;
                }
            }
            final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(c2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c, c2))) {
                final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.fCurrentEntity.ch[1] = c2;
                position = 0;
                if (this.load(2, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    return this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                }
            }
        }
        while (true) {
            final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11NCName(c3)) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
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
            else {
                if (!XML11Char.isXML11NameHighSurrogate(c3)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n2 = this.fCurrentEntity.position - position;
                    if (n2 == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n2);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n2);
                    }
                    position = 0;
                    if (this.load(n2, false)) {
                        final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.startPosition;
                        final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.position;
                        break;
                    }
                }
                final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(c4) || !XML11Char.isXML11NCName(XMLChar.supplemental(c3, c4))) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int n3 = this.fCurrentEntity.position - position;
                if (n3 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(position, n3);
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n3);
                }
                position = 0;
                if (this.load(n3, false)) {
                    break;
                }
                continue;
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += n4;
        String addSymbol = null;
        if (n4 > 0) {
            addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n4);
        }
        return addSymbol;
    }
    
    public boolean scanQName(final QName qName) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true);
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        if (XML11Char.isXML11NCNameStart(c)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String addSymbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    qName.setValues(null, addSymbol, addSymbol, null);
                    return true;
                }
            }
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(c)) {
                return false;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                position = 0;
                if (this.load(1, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.startPosition;
                    final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.position;
                    return false;
                }
            }
            final char c2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(c2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(c, c2))) {
                final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return false;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.fCurrentEntity.ch[1] = c2;
                position = 0;
                if (this.load(2, false)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    final String addSymbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    qName.setValues(null, addSymbol2, addSymbol2, null);
                    return true;
                }
            }
        }
        int position2 = -1;
        boolean b = false;
        while (true) {
            final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(c3)) {
                if (c3 == ':') {
                    if (position2 != -1) {
                        break;
                    }
                    position2 = this.fCurrentEntity.position;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
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
            else {
                if (!XML11Char.isXML11NameHighSurrogate(c3)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int n2 = this.fCurrentEntity.position - position;
                    if (n2 == this.fCurrentEntity.ch.length) {
                        this.resizeBuffer(position, n2);
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n2);
                    }
                    if (position2 != -1) {
                        position2 -= position;
                    }
                    position = 0;
                    if (this.load(n2, false)) {
                        b = true;
                        final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.startPosition;
                        final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.position;
                        break;
                    }
                }
                final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(c4) || !XML11Char.isXML11Name(XMLChar.supplemental(c3, c4))) {
                    b = true;
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int n3 = this.fCurrentEntity.position - position;
                if (n3 == this.fCurrentEntity.ch.length) {
                    this.resizeBuffer(position, n3);
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, position, this.fCurrentEntity.ch, 0, n3);
                }
                if (position2 != -1) {
                    position2 -= position;
                }
                position = 0;
                if (this.load(n3, false)) {
                    break;
                }
                continue;
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += n4;
        if (n4 > 0) {
            String addSymbol3 = null;
            final String addSymbol4 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n4);
            String addSymbol5;
            if (position2 != -1) {
                final int n5 = position2 - position;
                addSymbol3 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position, n5);
                final int n6 = n4 - n5 - 1;
                final int n7 = position2 + 1;
                if (!XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[n7]) && (!XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[n7]) || b)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
                }
                addSymbol5 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, position2 + 1, n6);
            }
            else {
                addSymbol5 = addSymbol4;
            }
            qName.setValues(addSymbol3, addSymbol5, addSymbol4, null);
            return true;
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
        if (c == '\n' || ((c == '\r' || c == '\u0085' || c == '\u2028') && external)) {
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
                    final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                    if (c3 == '\n' || c3 == '\u0085') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++position;
                    }
                    else {
                        ++n;
                    }
                }
                else {
                    if (c2 != '\n' && ((c2 != '\u0085' && c2 != '\u2028') || !external)) {
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
        if (external) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (!XML11Char.isXML11Content(c4) || c4 == '\u0085' || c4 == '\u2028') {
                    final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    --fCurrentEntity7.position;
                    break;
                }
            }
        }
        else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                if (!XML11Char.isXML11InternalEntityContent(this.fCurrentEntity.ch[this.fCurrentEntity.position++])) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
            }
        }
        final int n3 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += n3 - n;
        xmlString.setValues(this.fCurrentEntity.ch, position, n3);
        int n4;
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            n4 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if ((n4 == 13 || n4 == 133 || n4 == 8232) && external) {
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
            this.fCurrentEntity.startPosition = 0;
            this.fCurrentEntity.position = 0;
        }
        int position = this.fCurrentEntity.position;
        final char c = this.fCurrentEntity.ch[position];
        int n2 = 0;
        final boolean external = this.fCurrentEntity.isExternal();
        if (c == '\n' || ((c == '\r' || c == '\u0085' || c == '\u2028') && external)) {
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
                    final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                    if (c3 == '\n' || c3 == '\u0085') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++position;
                    }
                    else {
                        ++n2;
                    }
                }
                else {
                    if (c2 != '\n' && ((c2 != '\u0085' && c2 != '\u2028') || !external)) {
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
        if (external) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c4 == n || c4 == '%' || !XML11Char.isXML11Content(c4) || c4 == '\u0085' || c4 == '\u2028') {
                    final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    --fCurrentEntity7.position;
                    break;
                }
            }
        }
        else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                final char c5 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if ((c5 == n && !this.fCurrentEntity.literal) || c5 == '%' || !XML11Char.isXML11InternalEntityContent(c5)) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
            }
        }
        final int n4 = this.fCurrentEntity.position - position;
        final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += n4 - n2;
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
        do {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.load(0, true);
            }
            for (boolean load = false; this.fCurrentEntity.position >= this.fCurrentEntity.count - length && !load; load = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false), this.fCurrentEntity.position = 0, this.fCurrentEntity.startPosition = 0) {
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            }
            if (this.fCurrentEntity.position >= this.fCurrentEntity.count - length) {
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
            if (c == '\n' || ((c == '\r' || c == '\u0085' || c == '\u2028') && external)) {
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
                        final char c3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                        if (c3 == '\n' || c3 == '\u0085') {
                            final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                            ++fCurrentEntity5.position;
                            ++position;
                        }
                        else {
                            ++count;
                        }
                    }
                    else {
                        if (c2 != '\n' && ((c2 != '\u0085' && c2 != '\u2028') || !external)) {
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
            Label_1375: {
                if (external) {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        final char c4 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (c4 == char1) {
                            final int n2 = this.fCurrentEntity.position - 1;
                            for (int j = 1; j < length; ++j) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    final XMLEntityManager.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
                                    fCurrentEntity9.position -= j;
                                    break Label_1375;
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
                            if (c4 == '\n' || c4 == '\r' || c4 == '\u0085' || c4 == '\u2028') {
                                final XMLEntityManager.ScannedEntity fCurrentEntity11 = this.fCurrentEntity;
                                --fCurrentEntity11.position;
                                break;
                            }
                            if (!XML11Char.isXML11ValidLiteral(c4)) {
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
                }
                else {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        final char c5 = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (c5 == char1) {
                            final int n4 = this.fCurrentEntity.position - 1;
                            for (int k = 1; k < length; ++k) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    final XMLEntityManager.ScannedEntity fCurrentEntity14 = this.fCurrentEntity;
                                    fCurrentEntity14.position -= k;
                                    break Label_1375;
                                }
                                if (s.charAt(k) != this.fCurrentEntity.ch[this.fCurrentEntity.position++]) {
                                    final XMLEntityManager.ScannedEntity fCurrentEntity15 = this.fCurrentEntity;
                                    --fCurrentEntity15.position;
                                    break;
                                }
                            }
                            if (this.fCurrentEntity.position == n4 + length) {
                                b = true;
                                break;
                            }
                            continue;
                        }
                        else {
                            if (c5 == '\n') {
                                final XMLEntityManager.ScannedEntity fCurrentEntity16 = this.fCurrentEntity;
                                --fCurrentEntity16.position;
                                break;
                            }
                            if (!XML11Char.isXML11Valid(c5)) {
                                final XMLEntityManager.ScannedEntity fCurrentEntity17 = this.fCurrentEntity;
                                --fCurrentEntity17.position;
                                final int n5 = this.fCurrentEntity.position - position;
                                final XMLEntityManager.ScannedEntity fCurrentEntity18 = this.fCurrentEntity;
                                fCurrentEntity18.columnNumber += n5 - count;
                                xmlStringBuffer.append(this.fCurrentEntity.ch, position, n5);
                                return true;
                            }
                            continue;
                        }
                    }
                }
            }
            int n6 = this.fCurrentEntity.position - position;
            final XMLEntityManager.ScannedEntity fCurrentEntity19 = this.fCurrentEntity;
            fCurrentEntity19.columnNumber += n6 - count;
            if (b) {
                n6 -= length;
            }
            xmlStringBuffer.append(this.fCurrentEntity.ch, position, n6);
        } while (!b);
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
        if (n == 10 && (c == '\u2028' || c == '\u0085') && this.fCurrentEntity.isExternal()) {
            final XMLEntityManager.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
            ++fCurrentEntity4.position;
            final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
            ++fCurrentEntity5.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            return true;
        }
        if (n == 10 && c == '\r' && this.fCurrentEntity.isExternal()) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.fCurrentEntity.ch[0] = c;
                this.load(1, false);
            }
            final char c2 = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
            if (c2 == '\n' || c2 == '\u0085') {
                final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                ++fCurrentEntity6.position;
            }
            final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
            ++fCurrentEntity7.lineNumber;
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
        if (this.fCurrentEntity.isExternal()) {
            if (XML11Char.isXML11Space(c)) {
                do {
                    boolean load = false;
                    if (c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028') {
                        final XMLEntityManager.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                        ++fCurrentEntity.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                            this.fCurrentEntity.ch[0] = c;
                            load = this.load(1, true);
                            if (!load) {
                                this.fCurrentEntity.startPosition = 0;
                                this.fCurrentEntity.position = 0;
                            }
                        }
                        if (c == '\r') {
                            final char c2 = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
                            if (c2 != '\n' && c2 != '\u0085') {
                                final XMLEntityManager.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                                --fCurrentEntity2.position;
                            }
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
                } while (XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
                return true;
            }
        }
        else if (XMLChar.isSpace(c)) {
            do {
                boolean load2 = false;
                if (c == '\n') {
                    final XMLEntityManager.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.fCurrentEntity.ch[0] = c;
                        load2 = this.load(1, true);
                        if (!load2) {
                            this.fCurrentEntity.startPosition = 0;
                            this.fCurrentEntity.position = 0;
                        }
                    }
                }
                else {
                    final XMLEntityManager.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    ++fCurrentEntity6.columnNumber;
                }
                if (!load2) {
                    final XMLEntityManager.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    ++fCurrentEntity7.position;
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
}
