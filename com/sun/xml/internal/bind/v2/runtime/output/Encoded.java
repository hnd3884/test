package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public final class Encoded
{
    public byte[] buf;
    public int len;
    private static final byte[][] entities;
    private static final byte[][] attributeEntities;
    
    public Encoded() {
    }
    
    public Encoded(final String text) {
        this.set(text);
    }
    
    public void ensureSize(final int size) {
        if (this.buf == null || this.buf.length < size) {
            this.buf = new byte[size];
        }
    }
    
    public final void set(final String text) {
        final int length = text.length();
        this.ensureSize(length * 3 + 1);
        int ptr = 0;
        for (int i = 0; i < length; ++i) {
            final char chr = text.charAt(i);
            if (chr > '\u007f') {
                if (chr > '\u07ff') {
                    if ('\ud800' <= chr && chr <= '\udfff') {
                        final int uc = ((chr & '\u03ff') << 10 | (text.charAt(++i) & '\u03ff')) + 65536;
                        this.buf[ptr++] = (byte)(0xF0 | uc >> 18);
                        this.buf[ptr++] = (byte)(0x80 | (uc >> 12 & 0x3F));
                        this.buf[ptr++] = (byte)(0x80 | (uc >> 6 & 0x3F));
                        this.buf[ptr++] = (byte)(128 + (uc & 0x3F));
                        continue;
                    }
                    this.buf[ptr++] = (byte)(224 + (chr >> 12));
                    this.buf[ptr++] = (byte)(128 + (chr >> 6 & 0x3F));
                }
                else {
                    this.buf[ptr++] = (byte)(192 + (chr >> 6));
                }
                this.buf[ptr++] = (byte)(128 + (chr & '?'));
            }
            else {
                this.buf[ptr++] = (byte)chr;
            }
        }
        this.len = ptr;
    }
    
    public final void setEscape(final String text, final boolean isAttribute) {
        final int length = text.length();
        this.ensureSize(length * 6 + 1);
        int ptr = 0;
        for (int i = 0; i < length; ++i) {
            final char chr = text.charAt(i);
            int ptr2 = ptr;
            if (chr > '\u007f') {
                if (chr > '\u07ff') {
                    if ('\ud800' <= chr && chr <= '\udfff') {
                        final int uc = ((chr & '\u03ff') << 10 | (text.charAt(++i) & '\u03ff')) + 65536;
                        this.buf[ptr++] = (byte)(0xF0 | uc >> 18);
                        this.buf[ptr++] = (byte)(0x80 | (uc >> 12 & 0x3F));
                        this.buf[ptr++] = (byte)(0x80 | (uc >> 6 & 0x3F));
                        this.buf[ptr++] = (byte)(128 + (uc & 0x3F));
                        continue;
                    }
                    this.buf[ptr2++] = (byte)(224 + (chr >> 12));
                    this.buf[ptr2++] = (byte)(128 + (chr >> 6 & 0x3F));
                }
                else {
                    this.buf[ptr2++] = (byte)(192 + (chr >> 6));
                }
                this.buf[ptr2++] = (byte)(128 + (chr & '?'));
            }
            else {
                final byte[] ent;
                if ((ent = Encoded.attributeEntities[chr]) != null) {
                    if (isAttribute || Encoded.entities[chr] != null) {
                        ptr2 = this.writeEntity(ent, ptr2);
                    }
                    else {
                        this.buf[ptr2++] = (byte)chr;
                    }
                }
                else {
                    this.buf[ptr2++] = (byte)chr;
                }
            }
            ptr = ptr2;
        }
        this.len = ptr;
    }
    
    private int writeEntity(final byte[] entity, final int ptr) {
        System.arraycopy(entity, 0, this.buf, ptr, entity.length);
        return ptr + entity.length;
    }
    
    public final void write(final UTF8XmlOutput out) throws IOException {
        out.write(this.buf, 0, this.len);
    }
    
    public void append(final char b) {
        this.buf[this.len++] = (byte)b;
    }
    
    public void compact() {
        final byte[] b = new byte[this.len];
        System.arraycopy(this.buf, 0, b, 0, this.len);
        this.buf = b;
    }
    
    private static void add(final char c, final String s, final boolean attOnly) {
        final byte[] image = UTF8XmlOutput.toBytes(s);
        Encoded.attributeEntities[c] = image;
        if (!attOnly) {
            Encoded.entities[c] = image;
        }
    }
    
    static {
        entities = new byte[128][];
        attributeEntities = new byte[128][];
        add('&', "&amp;", false);
        add('<', "&lt;", false);
        add('>', "&gt;", false);
        add('\"', "&quot;", true);
        add('\t', "&#x9;", true);
        add('\r', "&#xD;", false);
        add('\n', "&#xA;", true);
    }
}
