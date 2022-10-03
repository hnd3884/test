package org.owasp.esapi.codecs;

public class PushbackString
{
    private String input;
    private Character pushback;
    private Character temp;
    private int index;
    private int mark;
    
    public PushbackString(final String input) {
        this.index = 0;
        this.mark = 0;
        this.input = input;
    }
    
    public void pushback(final Character c) {
        this.pushback = c;
    }
    
    public int index() {
        return this.index;
    }
    
    public boolean hasNext() {
        return this.pushback != null || (this.input != null && this.input.length() != 0 && this.index < this.input.length());
    }
    
    public Character next() {
        if (this.pushback != null) {
            final Character save = this.pushback;
            this.pushback = null;
            return save;
        }
        if (this.input == null) {
            return null;
        }
        if (this.input.length() == 0) {
            return null;
        }
        if (this.index >= this.input.length()) {
            return null;
        }
        return this.input.charAt(this.index++);
    }
    
    public Character nextHex() {
        final Character c = this.next();
        if (c == null) {
            return null;
        }
        if (isHexDigit(c)) {
            return c;
        }
        return null;
    }
    
    public Character nextOctal() {
        final Character c = this.next();
        if (c == null) {
            return null;
        }
        if (isOctalDigit(c)) {
            return c;
        }
        return null;
    }
    
    public static boolean isHexDigit(final Character c) {
        if (c == null) {
            return false;
        }
        final char ch = c;
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
    
    public static boolean isOctalDigit(final Character c) {
        if (c == null) {
            return false;
        }
        final char ch = c;
        return ch >= '0' && ch <= '7';
    }
    
    public Character peek() {
        if (this.pushback != null) {
            return this.pushback;
        }
        if (this.input == null) {
            return null;
        }
        if (this.input.length() == 0) {
            return null;
        }
        if (this.index >= this.input.length()) {
            return null;
        }
        return this.input.charAt(this.index);
    }
    
    public boolean peek(final char c) {
        return (this.pushback != null && this.pushback == c) || (this.input != null && this.input.length() != 0 && this.index < this.input.length() && this.input.charAt(this.index) == c);
    }
    
    public void mark() {
        this.temp = this.pushback;
        this.mark = this.index;
    }
    
    public void reset() {
        this.pushback = this.temp;
        this.index = this.mark;
    }
    
    protected String remainder() {
        String output = this.input.substring(this.index);
        if (this.pushback != null) {
            output = this.pushback + output;
        }
        return output;
    }
}
