package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.xml.internal.stream.Entity;
import java.io.IOException;

public class XML11EntityScanner extends XMLEntityScanner
{
    @Override
    public int peekChar() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        final int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (this.fCurrentEntity.isExternal()) {
            return (c != 13 && c != 133 && c != 8232) ? c : 10;
        }
        return c;
    }
    
    @Override
    protected int scanChar(final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        boolean external = false;
        if (c == 10 || ((c == 13 || c == 133 || c == 8232) && (external = this.fCurrentEntity.isExternal()))) {
            final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
            ++fCurrentEntity.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = (char)c;
                this.load(1, false, false);
                offset = 0;
            }
            if (c == 13 && external) {
                final int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (cc != 10 && cc != 133) {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                }
            }
            c = 10;
        }
        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
        ++fCurrentEntity3.columnNumber;
        if (!this.detectingVersion) {
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
        }
        return c;
    }
    
    @Override
    protected String scanNmtoken() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        while (true) {
            final char ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(ch)) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int length = this.fCurrentEntity.position - offset;
                this.invokeListeners(length);
                if (length == this.fCurrentEntity.ch.length) {
                    final char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                    System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                    this.fCurrentEntity.ch = tmp;
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
                continue;
            }
            else {
                if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int length = this.fCurrentEntity.position - offset;
                    this.invokeListeners(length);
                    if (length == this.fCurrentEntity.ch.length) {
                        final char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                        System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                        this.fCurrentEntity.ch = tmp;
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
                    }
                    offset = 0;
                    if (this.load(length, false, false)) {
                        final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                        --fCurrentEntity.startPosition;
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        --fCurrentEntity2.position;
                        break;
                    }
                }
                final char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch2))) {
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int length2 = this.fCurrentEntity.position - offset;
                this.invokeListeners(length2);
                if (length2 == this.fCurrentEntity.ch.length) {
                    final char[] tmp2 = new char[this.fCurrentEntity.ch.length << 1];
                    System.arraycopy(this.fCurrentEntity.ch, offset, tmp2, 0, length2);
                    this.fCurrentEntity.ch = tmp2;
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length2);
                }
                offset = 0;
                if (this.load(length2, false, false)) {
                    break;
                }
                continue;
            }
        }
        final int length3 = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
        fCurrentEntity4.columnNumber += length3;
        String symbol = null;
        if (length3 > 0) {
            symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length3);
        }
        return symbol;
    }
    
    @Override
    protected String scanName(final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        char ch = this.fCurrentEntity.ch[offset];
        if (XML11Char.isXML11NameStart(ch)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return symbol;
                }
            }
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.startPosition;
                    return null;
                }
            }
            final char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NameStart(XMLChar.supplemental(ch, ch2))) {
                final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(2);
                this.fCurrentEntity.ch[0] = ch;
                this.fCurrentEntity.ch[1] = ch2;
                offset = 0;
                if (this.load(2, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    final String symbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    return symbol2;
                }
            }
        }
        int length = 0;
        while (true) {
            ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(ch)) {
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) <= 0) {
                    continue;
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
                continue;
            }
            else {
                if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                    break;
                }
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) > 0) {
                    offset = 0;
                    if (this.load(length, false, false)) {
                        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.position;
                        final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.startPosition;
                        break;
                    }
                }
                final char ch3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(ch3) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch3))) {
                    final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, offset)) <= 0) {
                    continue;
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
                continue;
            }
        }
        length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += length;
        String symbol2 = null;
        if (length > 0) {
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, length);
            symbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        }
        return symbol2;
    }
    
    protected String scanNCName() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        char ch = this.fCurrentEntity.ch[offset];
        if (XML11Char.isXML11NCNameStart(ch)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.columnNumber;
                    final String symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
                    return symbol;
                }
            }
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.position;
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.startPosition;
                    return null;
                }
            }
            final char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))) {
                final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return null;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(2);
                this.fCurrentEntity.ch[0] = ch;
                this.fCurrentEntity.ch[1] = ch2;
                offset = 0;
                if (this.load(2, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    final String symbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    return symbol2;
                }
            }
        }
        while (true) {
            ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11NCName(ch)) {
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int length = this.fCurrentEntity.position - offset;
                this.invokeListeners(length);
                if (length == this.fCurrentEntity.ch.length) {
                    final char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                    System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                    this.fCurrentEntity.ch = tmp;
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
                }
                offset = 0;
                if (this.load(length, false, false)) {
                    break;
                }
                continue;
            }
            else {
                if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                    break;
                }
                if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                    final int length = this.fCurrentEntity.position - offset;
                    this.invokeListeners(length);
                    if (length == this.fCurrentEntity.ch.length) {
                        final char[] tmp = new char[this.fCurrentEntity.ch.length << 1];
                        System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, length);
                        this.fCurrentEntity.ch = tmp;
                    }
                    else {
                        System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length);
                    }
                    offset = 0;
                    if (this.load(length, false, false)) {
                        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.startPosition;
                        final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.position;
                        break;
                    }
                }
                final char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCName(XMLChar.supplemental(ch, ch2))) {
                    final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
                if (++this.fCurrentEntity.position != this.fCurrentEntity.count) {
                    continue;
                }
                final int length2 = this.fCurrentEntity.position - offset;
                this.invokeListeners(length2);
                if (length2 == this.fCurrentEntity.ch.length) {
                    final char[] tmp2 = new char[this.fCurrentEntity.ch.length << 1];
                    System.arraycopy(this.fCurrentEntity.ch, offset, tmp2, 0, length2);
                    this.fCurrentEntity.ch = tmp2;
                }
                else {
                    System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, length2);
                }
                offset = 0;
                if (this.load(length2, false, false)) {
                    break;
                }
                continue;
            }
        }
        final int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += length;
        String symbol2 = null;
        if (length > 0) {
            symbol2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        }
        return symbol2;
    }
    
    @Override
    protected boolean scanQName(final QName qname, final XMLScanner.NameType nt) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        int offset = this.fCurrentEntity.position;
        char ch = this.fCurrentEntity.ch[offset];
        if (XML11Char.isXML11NCNameStart(ch)) {
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
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
        }
        else {
            if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                return false;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = ch;
                offset = 0;
                if (this.load(1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    --fCurrentEntity2.startPosition;
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    --fCurrentEntity3.position;
                    return false;
                }
            }
            final char ch2 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (!XMLChar.isLowSurrogate(ch2) || !XML11Char.isXML11NCNameStart(XMLChar.supplemental(ch, ch2))) {
                final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                --fCurrentEntity4.position;
                return false;
            }
            if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(2);
                this.fCurrentEntity.ch[0] = ch;
                this.fCurrentEntity.ch[1] = ch2;
                offset = 0;
                if (this.load(2, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    fCurrentEntity5.columnNumber += 2;
                    final String name2 = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 2);
                    qname.setValues(null, name2, name2, null);
                    this.checkEntityLimit(nt, this.fCurrentEntity, 0, 2);
                    return true;
                }
            }
        }
        int index = -1;
        int length = 0;
        boolean sawIncompleteSurrogatePair = false;
        while (true) {
            ch = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if (XML11Char.isXML11Name(ch)) {
                if (ch == ':') {
                    if (index != -1) {
                        break;
                    }
                    index = this.fCurrentEntity.position;
                    this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, index - offset);
                }
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
                continue;
            }
            else {
                if (!XML11Char.isXML11NameHighSurrogate(ch)) {
                    break;
                }
                if ((length = this.checkBeforeLoad(this.fCurrentEntity, offset, index)) > 0) {
                    if (index != -1) {
                        index -= offset;
                    }
                    offset = 0;
                    if (this.load(length, false, false)) {
                        sawIncompleteSurrogatePair = true;
                        final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                        --fCurrentEntity6.startPosition;
                        final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        --fCurrentEntity7.position;
                        break;
                    }
                }
                final char ch3 = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                if (!XMLChar.isLowSurrogate(ch3) || !XML11Char.isXML11Name(XMLChar.supplemental(ch, ch3))) {
                    sawIncompleteSurrogatePair = true;
                    final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
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
                continue;
            }
        }
        length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += length;
        if (length > 0) {
            String prefix = null;
            String localpart = null;
            final String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
            if (index != -1) {
                final int prefixLength = index - offset;
                this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, prefixLength);
                prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
                final int len = length - prefixLength - 1;
                final int startLocal = index + 1;
                if (!XML11Char.isXML11NCNameStart(this.fCurrentEntity.ch[startLocal]) && (!XML11Char.isXML11NameHighSurrogate(this.fCurrentEntity.ch[startLocal]) || sawIncompleteSurrogatePair)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
                }
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
        return false;
    }
    
    @Override
    protected int scanContent(final XMLString content) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false, false);
            this.fCurrentEntity.position = 0;
            this.fCurrentEntity.startPosition = 0;
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[offset];
        int newlines = 0;
        boolean counted = false;
        final boolean external = this.fCurrentEntity.isExternal();
        if (c == 10 || ((c == 13 || c == 133 || c == 8232) && external)) {
            do {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == 13 && external) {
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        this.checkEntityLimit(null, this.fCurrentEntity, offset, newlines);
                        offset = 0;
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = newlines;
                        this.fCurrentEntity.startPosition = newlines;
                        if (this.load(newlines, false, true)) {
                            counted = true;
                            break;
                        }
                    }
                    final int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                    if (cc == 10 || cc == 133) {
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++offset;
                    }
                    else {
                        ++newlines;
                    }
                }
                else {
                    if (c != 10 && ((c != 133 && c != 8232) || !external)) {
                        final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                        --fCurrentEntity4.position;
                        break;
                    }
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    this.checkEntityLimit(null, this.fCurrentEntity, offset, newlines);
                    offset = 0;
                    final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    fCurrentEntity6.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = newlines;
                    this.fCurrentEntity.startPosition = newlines;
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
        if (external) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (!XML11Char.isXML11Content(c) || c == 133 || c == 8232) {
                    final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    --fCurrentEntity7.position;
                    break;
                }
            }
        }
        else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (!XML11Char.isXML11InternalEntityContent(c)) {
                    final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
            }
        }
        final int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += length - newlines;
        if (!counted) {
            this.checkEntityLimit(null, this.fCurrentEntity, offset, length);
        }
        content.setValues(this.fCurrentEntity.ch, offset, length);
        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
            if ((c == 13 || c == 133 || c == 8232) && external) {
                c = 10;
            }
        }
        else {
            c = -1;
        }
        return c;
    }
    
    @Override
    protected int scanLiteral(final int quote, final XMLString content, final boolean isNSURI) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.invokeListeners(1);
            this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
            this.load(1, false, false);
            this.fCurrentEntity.startPosition = 0;
            this.fCurrentEntity.position = 0;
        }
        int offset = this.fCurrentEntity.position;
        int c = this.fCurrentEntity.ch[offset];
        int newlines = 0;
        final boolean external = this.fCurrentEntity.isExternal();
        if (c == 10 || ((c == 13 || c == 133 || c == 8232) && external)) {
            do {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == 13 && external) {
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                    ++fCurrentEntity.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                        offset = 0;
                        final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                        fCurrentEntity2.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = newlines;
                        this.fCurrentEntity.startPosition = newlines;
                        if (this.load(newlines, false, true)) {
                            break;
                        }
                    }
                    final int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                    if (cc == 10 || cc == 133) {
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.position;
                        ++offset;
                    }
                    else {
                        ++newlines;
                    }
                }
                else {
                    if (c != 10 && ((c != 133 && c != 8232) || !external)) {
                        final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                        --fCurrentEntity4.position;
                        break;
                    }
                    ++newlines;
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                        continue;
                    }
                    offset = 0;
                    final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    fCurrentEntity6.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                    this.fCurrentEntity.position = newlines;
                    this.fCurrentEntity.startPosition = newlines;
                    if (this.load(newlines, false, true)) {
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
                content.setValues(this.fCurrentEntity.ch, offset, length);
                return -1;
            }
        }
        if (external) {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if (c == quote || c == 37 || !XML11Char.isXML11Content(c) || c == 133 || c == 8232) {
                    final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    --fCurrentEntity7.position;
                    break;
                }
            }
        }
        else {
            while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                if ((c == quote && !this.fCurrentEntity.literal) || c == 37 || !XML11Char.isXML11InternalEntityContent(c)) {
                    final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                    --fCurrentEntity8.position;
                    break;
                }
            }
        }
        final int length = this.fCurrentEntity.position - offset;
        final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
        fCurrentEntity9.columnNumber += length - newlines;
        this.checkEntityLimit(null, this.fCurrentEntity, offset, length);
        if (isNSURI) {
            this.checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, this.fCurrentEntity, offset, length);
        }
        content.setValues(this.fCurrentEntity.ch, offset, length);
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
    
    @Override
    protected boolean scanData(final String delimiter, final XMLStringBuffer buffer) throws IOException {
        boolean done = false;
        final int delimLen = delimiter.length();
        final char charAt0 = delimiter.charAt(0);
        final boolean external = this.fCurrentEntity.isExternal();
        do {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.load(0, true, false);
            }
            for (boolean bNextEntity = false; this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen && !bNextEntity; bNextEntity = this.load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false), this.fCurrentEntity.position = 0, this.fCurrentEntity.startPosition = 0) {
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
            }
            if (this.fCurrentEntity.position >= this.fCurrentEntity.count - delimLen) {
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
            if (c == 10 || ((c == 13 || c == 133 || c == 8232) && external)) {
                do {
                    c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                    if (c == 13 && external) {
                        ++newlines;
                        final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                        ++fCurrentEntity3.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                            offset = 0;
                            final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
                            fCurrentEntity4.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                            this.fCurrentEntity.position = newlines;
                            this.fCurrentEntity.startPosition = newlines;
                            if (this.load(newlines, false, true)) {
                                break;
                            }
                        }
                        final int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
                        if (cc == 10 || cc == 133) {
                            final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                            ++fCurrentEntity5.position;
                            ++offset;
                        }
                        else {
                            ++newlines;
                        }
                    }
                    else {
                        if (c != 10 && ((c != 133 && c != 8232) || !external)) {
                            final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                            --fCurrentEntity6.position;
                            break;
                        }
                        ++newlines;
                        final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                        ++fCurrentEntity7.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
                            continue;
                        }
                        offset = 0;
                        final Entity.ScannedEntity fCurrentEntity8 = this.fCurrentEntity;
                        fCurrentEntity8.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
                        this.fCurrentEntity.position = newlines;
                        this.fCurrentEntity.startPosition = newlines;
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
            Label_1451: {
                if (external) {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (c == charAt0) {
                            final int delimOffset = this.fCurrentEntity.position - 1;
                            for (int j = 1; j < delimLen; ++j) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    final Entity.ScannedEntity fCurrentEntity9 = this.fCurrentEntity;
                                    fCurrentEntity9.position -= j;
                                    break Label_1451;
                                }
                                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                                if (delimiter.charAt(j) != c) {
                                    final Entity.ScannedEntity fCurrentEntity10 = this.fCurrentEntity;
                                    --fCurrentEntity10.position;
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
                            if (c == 10 || c == 13 || c == 133 || c == 8232) {
                                final Entity.ScannedEntity fCurrentEntity11 = this.fCurrentEntity;
                                --fCurrentEntity11.position;
                                break;
                            }
                            if (!XML11Char.isXML11ValidLiteral(c)) {
                                final Entity.ScannedEntity fCurrentEntity12 = this.fCurrentEntity;
                                --fCurrentEntity12.position;
                                final int length2 = this.fCurrentEntity.position - offset;
                                final Entity.ScannedEntity fCurrentEntity13 = this.fCurrentEntity;
                                fCurrentEntity13.columnNumber += length2 - newlines;
                                this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
                                buffer.append(this.fCurrentEntity.ch, offset, length2);
                                return true;
                            }
                            continue;
                        }
                    }
                }
                else {
                    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
                        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                        if (c == charAt0) {
                            final int delimOffset = this.fCurrentEntity.position - 1;
                            for (int j = 1; j < delimLen; ++j) {
                                if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                                    final Entity.ScannedEntity fCurrentEntity14 = this.fCurrentEntity;
                                    fCurrentEntity14.position -= j;
                                    break Label_1451;
                                }
                                c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
                                if (delimiter.charAt(j) != c) {
                                    final Entity.ScannedEntity fCurrentEntity15 = this.fCurrentEntity;
                                    --fCurrentEntity15.position;
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
                            if (c == 10) {
                                final Entity.ScannedEntity fCurrentEntity16 = this.fCurrentEntity;
                                --fCurrentEntity16.position;
                                break;
                            }
                            if (!XML11Char.isXML11Valid(c)) {
                                final Entity.ScannedEntity fCurrentEntity17 = this.fCurrentEntity;
                                --fCurrentEntity17.position;
                                final int length2 = this.fCurrentEntity.position - offset;
                                final Entity.ScannedEntity fCurrentEntity18 = this.fCurrentEntity;
                                fCurrentEntity18.columnNumber += length2 - newlines;
                                this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
                                buffer.append(this.fCurrentEntity.ch, offset, length2);
                                return true;
                            }
                            continue;
                        }
                    }
                }
            }
            int length2 = this.fCurrentEntity.position - offset;
            final Entity.ScannedEntity fCurrentEntity19 = this.fCurrentEntity;
            fCurrentEntity19.columnNumber += length2 - newlines;
            this.checkEntityLimit(XMLScanner.NameType.COMMENT, this.fCurrentEntity, offset, length2);
            if (done) {
                length2 -= delimLen;
            }
            buffer.append(this.fCurrentEntity.ch, offset, length2);
        } while (!done);
        return !done;
    }
    
    @Override
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
        if (c == 10 && (cc == 8232 || cc == 133) && this.fCurrentEntity.isExternal()) {
            final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
            ++fCurrentEntity4.position;
            final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
            ++fCurrentEntity5.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
            return true;
        }
        if (c == 10 && cc == 13 && this.fCurrentEntity.isExternal()) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(1);
                this.fCurrentEntity.ch[0] = (char)cc;
                this.load(1, false, false);
            }
            final int ccc = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
            if (ccc == 10 || ccc == 133) {
                final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                ++fCurrentEntity6.position;
            }
            final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
            ++fCurrentEntity7.lineNumber;
            this.fCurrentEntity.columnNumber = 1;
            this.checkEntityLimit(nt, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean skipSpaces() throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        if (this.fCurrentEntity == null) {
            return false;
        }
        int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        int offset = this.fCurrentEntity.position - 1;
        if (this.fCurrentEntity.isExternal()) {
            if (XML11Char.isXML11Space(c)) {
                do {
                    boolean entityChanged = false;
                    if (c == 10 || c == 13 || c == 133 || c == 8232) {
                        final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                        ++fCurrentEntity.lineNumber;
                        this.fCurrentEntity.columnNumber = 1;
                        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                            this.invokeListeners(1);
                            this.fCurrentEntity.ch[0] = (char)c;
                            entityChanged = this.load(1, true, false);
                            if (!entityChanged) {
                                this.fCurrentEntity.startPosition = 0;
                                this.fCurrentEntity.position = 0;
                            }
                            else if (this.fCurrentEntity == null) {
                                return true;
                            }
                        }
                        if (c == 13) {
                            final int cc = this.fCurrentEntity.ch[++this.fCurrentEntity.position];
                            if (cc != 10 && cc != 133) {
                                final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                                --fCurrentEntity2.position;
                            }
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
                } while (XML11Char.isXML11Space(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
                return true;
            }
        }
        else if (XMLChar.isSpace(c)) {
            do {
                boolean entityChanged = false;
                if (c == 10) {
                    final Entity.ScannedEntity fCurrentEntity5 = this.fCurrentEntity;
                    ++fCurrentEntity5.lineNumber;
                    this.fCurrentEntity.columnNumber = 1;
                    if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
                        this.invokeListeners(1);
                        this.fCurrentEntity.ch[0] = (char)c;
                        entityChanged = this.load(1, true, false);
                        if (!entityChanged) {
                            this.fCurrentEntity.startPosition = 0;
                            this.fCurrentEntity.position = 0;
                        }
                        else if (this.fCurrentEntity == null) {
                            return true;
                        }
                    }
                }
                else {
                    final Entity.ScannedEntity fCurrentEntity6 = this.fCurrentEntity;
                    ++fCurrentEntity6.columnNumber;
                }
                this.checkEntityLimit(null, this.fCurrentEntity, offset, this.fCurrentEntity.position - offset);
                offset = this.fCurrentEntity.position;
                if (!entityChanged) {
                    final Entity.ScannedEntity fCurrentEntity7 = this.fCurrentEntity;
                    ++fCurrentEntity7.position;
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
    
    @Override
    protected boolean skipString(final String s) throws IOException {
        if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            this.load(0, true, true);
        }
        final int length = s.length();
        final int beforeSkip = this.fCurrentEntity.position;
        for (int i = 0; i < length; ++i) {
            final char c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (c != s.charAt(i)) {
                final Entity.ScannedEntity fCurrentEntity = this.fCurrentEntity;
                fCurrentEntity.position -= i + 1;
                return false;
            }
            if (i < length - 1 && this.fCurrentEntity.position == this.fCurrentEntity.count) {
                this.invokeListeners(0);
                System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.count - i - 1, this.fCurrentEntity.ch, 0, i + 1);
                if (this.load(i + 1, false, false)) {
                    final Entity.ScannedEntity fCurrentEntity2 = this.fCurrentEntity;
                    fCurrentEntity2.startPosition -= i + 1;
                    final Entity.ScannedEntity fCurrentEntity3 = this.fCurrentEntity;
                    fCurrentEntity3.position -= i + 1;
                    return false;
                }
            }
        }
        final Entity.ScannedEntity fCurrentEntity4 = this.fCurrentEntity;
        fCurrentEntity4.columnNumber += length;
        if (!this.detectingVersion) {
            this.checkEntityLimit(null, this.fCurrentEntity, beforeSkip, length);
        }
        return true;
    }
}
