package com.maverick.crypto.asn1.x509;

public class X509NameTokenizer
{
    private String d;
    private int c;
    private StringBuffer b;
    
    public X509NameTokenizer(final String d) {
        this.b = new StringBuffer();
        this.d = d;
        this.c = -1;
    }
    
    public boolean hasMoreTokens() {
        return this.c != this.d.length();
    }
    
    public String nextToken() {
        if (this.c == this.d.length()) {
            return null;
        }
        int i = this.c + 1;
        boolean b = false;
        int n = 0;
        this.b.setLength(0);
        while (i != this.d.length()) {
            final char char1 = this.d.charAt(i);
            if (char1 == '\"') {
                if (n == 0) {
                    b = !b;
                }
                else {
                    this.b.append(char1);
                }
                n = 0;
            }
            else if (n != 0 || b) {
                this.b.append(char1);
                n = 0;
            }
            else if (char1 == '\\') {
                n = 1;
            }
            else {
                if (char1 == ',') {
                    break;
                }
                this.b.append(char1);
            }
            ++i;
        }
        this.c = i;
        return this.b.toString().trim();
    }
}
