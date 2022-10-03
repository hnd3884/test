package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;

final class Scanner
{
    private final String s;
    private int pos;
    
    Scanner(final String s) {
        this.s = s;
    }
    
    int peek() {
        return (this.pos == this.s.length()) ? -1 : this.s.charAt(this.pos);
    }
    
    String getName() {
        final int start = this.pos;
        while (this.pos < this.s.length()) {
            final char c = this.s.charAt(this.pos);
            if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && ('0' > c || c > '9') && c != ':' && c != '_' && c != '-' && c != '.' && c <= '\u0080') {
                break;
            }
            ++this.pos;
        }
        return (this.pos == start) ? null : this.s.substring(start, this.pos);
    }
    
    String getQuotedString() throws XMLStreamException {
        final int quoteChar = this.peek();
        if (quoteChar != 39 && quoteChar != 34) {
            throw new XMLStreamException("Expected quote char at position " + this.pos);
        }
        ++this.pos;
        final int start = this.pos;
        while (this.pos < this.s.length() && this.s.charAt(this.pos) != quoteChar) {
            ++this.pos;
        }
        if (this.peek() == quoteChar) {
            return this.s.substring(start, this.pos++);
        }
        throw new XMLStreamException("Unterminated quoted string");
    }
    
    void expect(final String seq) throws XMLStreamException {
        boolean found;
        if (this.pos + seq.length() > this.s.length()) {
            found = false;
        }
        else {
            found = true;
            for (int i = 0; i < seq.length(); ++i) {
                if (this.s.charAt(this.pos + i) != seq.charAt(i)) {
                    found = false;
                    break;
                }
            }
        }
        if (found) {
            this.pos += seq.length();
            return;
        }
        throw new XMLStreamException("Expected \"" + seq + "\" at position " + this.pos);
    }
    
    void skipSpace() {
        while (this.pos < this.s.length()) {
            final char c = this.s.charAt(this.pos);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                break;
            }
            ++this.pos;
        }
    }
}
