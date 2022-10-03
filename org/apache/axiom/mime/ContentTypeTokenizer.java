package org.apache.axiom.mime;

import java.text.ParseException;

final class ContentTypeTokenizer
{
    private static final String whitespace = " \t\n\r";
    private static final String tspecials = "()<>@,;:\\\"/[]?=";
    private final String s;
    private int index;
    
    public ContentTypeTokenizer(final String s) {
        this.s = s;
    }
    
    private void skipWhiteSpace() {
        final int len = this.s.length();
        while (this.index < len && " \t\n\r".indexOf(this.s.charAt(this.index)) != -1) {
            ++this.index;
        }
    }
    
    String expectToken() throws ParseException {
        this.skipWhiteSpace();
        final int begin = this.index;
        final int len = this.s.length();
        while (this.index < len && "()<>@,;:\\\"/[]?=".indexOf(this.s.charAt(this.index)) == -1) {
            ++this.index;
        }
        int end;
        for (end = this.index; end > begin && " \t\n\r".indexOf(this.s.charAt(end - 1)) != -1; --end) {}
        if (begin != end) {
            return this.s.substring(begin, end);
        }
        if (this.index == this.s.length()) {
            return null;
        }
        throw new ParseException("Expected token, but found '" + this.s.charAt(this.index) + "'", this.index);
    }
    
    String requireToken() throws ParseException {
        final String token = this.expectToken();
        if (token == null) {
            throw new ParseException("Token expected", this.index);
        }
        return token;
    }
    
    String requireTokenOrQuotedString() throws ParseException {
        this.skipWhiteSpace();
        final int len = this.s.length();
        if (this.index >= len) {
            throw new ParseException("Unexpected end of string; expected token or quoted string", this.index);
        }
        if (this.s.charAt(this.index) != '\"') {
            return this.requireToken();
        }
        final StringBuffer sb = new StringBuffer();
        ++this.index;
        while (this.index < len) {
            final char c = this.s.charAt(this.index);
            if (c == '\\') {
                ++this.index;
                if (this.index == len) {
                    throw new ParseException("Expected more input after escape character", this.index);
                }
                sb.append(this.s.charAt(this.index));
            }
            else {
                if (c == '\"') {
                    break;
                }
                sb.append(c);
            }
            ++this.index;
        }
        if (this.index == len) {
            throw new ParseException("Unclosed quoted string", this.index);
        }
        ++this.index;
        this.skipWhiteSpace();
        return sb.toString();
    }
    
    boolean expect(final char c) throws ParseException {
        if (this.index == this.s.length()) {
            return false;
        }
        final char actual = this.s.charAt(this.index);
        if (actual == c) {
            ++this.index;
            return true;
        }
        throw new ParseException("Expected '" + c + "' instead of '" + actual + "'", this.index);
    }
    
    void require(final char c) throws ParseException {
        if (!this.expect(c)) {
            throw new ParseException("Unexpected end of string; expected '" + c + "'", this.index);
        }
    }
    
    void requireEndOfString() throws ParseException {
        if (this.index != this.s.length()) {
            throw new ParseException("Unexpected character '" + this.s.charAt(this.index) + "'; expected end of string", this.index);
        }
    }
}
