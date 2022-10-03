package org.bouncycastle.asn1.x500.style;

public class X500NameTokenizer
{
    private String value;
    private int index;
    private char separator;
    private StringBuffer buf;
    
    public X500NameTokenizer(final String s) {
        this(s, ',');
    }
    
    public X500NameTokenizer(final String value, final char separator) {
        this.buf = new StringBuffer();
        this.value = value;
        this.index = -1;
        this.separator = separator;
    }
    
    public boolean hasMoreTokens() {
        return this.index != this.value.length();
    }
    
    public String nextToken() {
        if (this.index == this.value.length()) {
            return null;
        }
        int i = this.index + 1;
        boolean b = false;
        int n = 0;
        this.buf.setLength(0);
        while (i != this.value.length()) {
            final char char1 = this.value.charAt(i);
            if (char1 == '\"') {
                if (n == 0) {
                    b = !b;
                }
                this.buf.append(char1);
                n = 0;
            }
            else if (n != 0 || b) {
                this.buf.append(char1);
                n = 0;
            }
            else if (char1 == '\\') {
                this.buf.append(char1);
                n = 1;
            }
            else {
                if (char1 == this.separator) {
                    break;
                }
                this.buf.append(char1);
            }
            ++i;
        }
        this.index = i;
        return this.buf.toString();
    }
}
