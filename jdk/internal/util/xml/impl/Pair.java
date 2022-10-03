package jdk.internal.util.xml.impl;

public class Pair
{
    public String name;
    public String value;
    public int num;
    public char[] chars;
    public int id;
    public Pair list;
    public Pair next;
    
    public String qname() {
        return new String(this.chars, 1, this.chars.length - 1);
    }
    
    public String local() {
        if (this.chars[0] != '\0') {
            return new String(this.chars, this.chars[0] + '\u0001', this.chars.length - this.chars[0] - 1);
        }
        return new String(this.chars, 1, this.chars.length - 1);
    }
    
    public String pref() {
        if (this.chars[0] != '\0') {
            return new String(this.chars, 1, this.chars[0] - '\u0001');
        }
        return "";
    }
    
    public boolean eqpref(final char[] array) {
        if (this.chars[0] == array[0]) {
            for (char c = this.chars[0], c2 = '\u0001'; c2 < c; ++c2) {
                if (this.chars[c2] != array[c2]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean eqname(final char[] array) {
        final char c = (char)this.chars.length;
        if (c == array.length) {
            for (char c2 = '\0'; c2 < c; ++c2) {
                if (this.chars[c2] != array[c2]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
