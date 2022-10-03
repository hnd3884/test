package org.apache.commons.csv;

import java.io.IOException;
import java.io.Closeable;

final class Lexer implements Closeable
{
    private static final char DISABLED = '\ufffe';
    private final char delimiter;
    private final char escape;
    private final char quoteChar;
    private final char commentStart;
    private final boolean ignoreSurroundingSpaces;
    private final boolean ignoreEmptyLines;
    private final ExtendedBufferedReader reader;
    
    Lexer(final CSVFormat format, final ExtendedBufferedReader reader) {
        this.reader = reader;
        this.delimiter = format.getDelimiter();
        this.escape = this.mapNullToDisabled(format.getEscapeCharacter());
        this.quoteChar = this.mapNullToDisabled(format.getQuoteCharacter());
        this.commentStart = this.mapNullToDisabled(format.getCommentMarker());
        this.ignoreSurroundingSpaces = format.getIgnoreSurroundingSpaces();
        this.ignoreEmptyLines = format.getIgnoreEmptyLines();
    }
    
    Token nextToken(final Token token) throws IOException {
        int lastChar = this.reader.getLastChar();
        int c = this.reader.read();
        boolean eol = this.readEndOfLine(c);
        if (this.ignoreEmptyLines) {
            while (eol && this.isStartOfLine(lastChar)) {
                lastChar = c;
                c = this.reader.read();
                eol = this.readEndOfLine(c);
                if (this.isEndOfFile(c)) {
                    token.type = Token.Type.EOF;
                    return token;
                }
            }
        }
        if (this.isEndOfFile(lastChar) || (!this.isDelimiter(lastChar) && this.isEndOfFile(c))) {
            token.type = Token.Type.EOF;
            return token;
        }
        if (!this.isStartOfLine(lastChar) || !this.isCommentStart(c)) {
            while (token.type == Token.Type.INVALID) {
                if (this.ignoreSurroundingSpaces) {
                    while (this.isWhitespace(c) && !eol) {
                        c = this.reader.read();
                        eol = this.readEndOfLine(c);
                    }
                }
                if (this.isDelimiter(c)) {
                    token.type = Token.Type.TOKEN;
                }
                else if (eol) {
                    token.type = Token.Type.EORECORD;
                }
                else if (this.isQuoteChar(c)) {
                    this.parseEncapsulatedToken(token);
                }
                else if (this.isEndOfFile(c)) {
                    token.type = Token.Type.EOF;
                    token.isReady = true;
                }
                else {
                    this.parseSimpleToken(token, c);
                }
            }
            return token;
        }
        final String line = this.reader.readLine();
        if (line == null) {
            token.type = Token.Type.EOF;
            return token;
        }
        final String comment = line.trim();
        token.content.append(comment);
        token.type = Token.Type.COMMENT;
        return token;
    }
    
    private Token parseSimpleToken(final Token token, int ch) throws IOException {
        while (true) {
            while (!this.readEndOfLine(ch)) {
                if (this.isEndOfFile(ch)) {
                    token.type = Token.Type.EOF;
                    token.isReady = true;
                }
                else if (this.isDelimiter(ch)) {
                    token.type = Token.Type.TOKEN;
                }
                else {
                    if (this.isEscape(ch)) {
                        final int unescaped = this.readEscape();
                        if (unescaped == -1) {
                            token.content.append((char)ch).append((char)this.reader.getLastChar());
                        }
                        else {
                            token.content.append((char)unescaped);
                        }
                        ch = this.reader.read();
                        continue;
                    }
                    token.content.append((char)ch);
                    ch = this.reader.read();
                    continue;
                }
                if (this.ignoreSurroundingSpaces) {
                    this.trimTrailingSpaces(token.content);
                }
                return token;
            }
            token.type = Token.Type.EORECORD;
            continue;
        }
    }
    
    private Token parseEncapsulatedToken(final Token token) throws IOException {
        final long startLineNumber = this.getCurrentLineNumber();
        while (true) {
            int c = this.reader.read();
            if (this.isEscape(c)) {
                final int unescaped = this.readEscape();
                if (unescaped == -1) {
                    token.content.append((char)c).append((char)this.reader.getLastChar());
                }
                else {
                    token.content.append((char)unescaped);
                }
            }
            else if (this.isQuoteChar(c)) {
                if (!this.isQuoteChar(this.reader.lookAhead())) {
                    break;
                }
                c = this.reader.read();
                token.content.append((char)c);
            }
            else {
                if (this.isEndOfFile(c)) {
                    throw new IOException("(startline " + startLineNumber + ") EOF reached before encapsulated token finished");
                }
                token.content.append((char)c);
            }
        }
        int c;
        do {
            c = this.reader.read();
            if (this.isDelimiter(c)) {
                token.type = Token.Type.TOKEN;
                return token;
            }
            if (this.isEndOfFile(c)) {
                token.type = Token.Type.EOF;
                token.isReady = true;
                return token;
            }
            if (this.readEndOfLine(c)) {
                token.type = Token.Type.EORECORD;
                return token;
            }
        } while (this.isWhitespace(c));
        throw new IOException("(line " + this.getCurrentLineNumber() + ") invalid char between encapsulated token and delimiter");
    }
    
    private char mapNullToDisabled(final Character c) {
        return (c == null) ? '\ufffe' : c;
    }
    
    long getCurrentLineNumber() {
        return this.reader.getCurrentLineNumber();
    }
    
    long getCharacterPosition() {
        return this.reader.getPosition();
    }
    
    int readEscape() throws IOException {
        final int ch = this.reader.read();
        switch (ch) {
            case 114: {
                return 13;
            }
            case 110: {
                return 10;
            }
            case 116: {
                return 9;
            }
            case 98: {
                return 8;
            }
            case 102: {
                return 12;
            }
            case 8:
            case 9:
            case 10:
            case 12:
            case 13: {
                return ch;
            }
            case -1: {
                throw new IOException("EOF whilst processing escape sequence");
            }
            default: {
                if (this.isMetaChar(ch)) {
                    return ch;
                }
                return -1;
            }
        }
    }
    
    void trimTrailingSpaces(final StringBuilder buffer) {
        int length;
        for (length = buffer.length(); length > 0 && Character.isWhitespace(buffer.charAt(length - 1)); --length) {}
        if (length != buffer.length()) {
            buffer.setLength(length);
        }
    }
    
    boolean readEndOfLine(int ch) throws IOException {
        if (ch == 13 && this.reader.lookAhead() == 10) {
            ch = this.reader.read();
        }
        return ch == 10 || ch == 13;
    }
    
    boolean isClosed() {
        return this.reader.isClosed();
    }
    
    boolean isWhitespace(final int ch) {
        return !this.isDelimiter(ch) && Character.isWhitespace((char)ch);
    }
    
    boolean isStartOfLine(final int ch) {
        return ch == 10 || ch == 13 || ch == -2;
    }
    
    boolean isEndOfFile(final int ch) {
        return ch == -1;
    }
    
    boolean isDelimiter(final int ch) {
        return ch == this.delimiter;
    }
    
    boolean isEscape(final int ch) {
        return ch == this.escape;
    }
    
    boolean isQuoteChar(final int ch) {
        return ch == this.quoteChar;
    }
    
    boolean isCommentStart(final int ch) {
        return ch == this.commentStart;
    }
    
    private boolean isMetaChar(final int ch) {
        return ch == this.delimiter || ch == this.escape || ch == this.quoteChar || ch == this.commentStart;
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
