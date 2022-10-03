package jdk.internal.util.xml.impl;

import java.io.Reader;

public class Input
{
    public String pubid;
    public String sysid;
    public String xmlenc;
    public char xmlver;
    public Reader src;
    public char[] chars;
    public int chLen;
    public int chIdx;
    public Input next;
    
    public Input(final int n) {
        this.chars = new char[n];
        this.chLen = this.chars.length;
    }
    
    public Input(final char[] chars) {
        this.chars = chars;
        this.chLen = this.chars.length;
    }
    
    public Input() {
    }
}
