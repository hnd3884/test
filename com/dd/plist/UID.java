package com.dd.plist;

import java.io.IOException;

public class UID extends NSObject
{
    private final byte[] bytes;
    private final String name;
    
    public UID(final String name, final byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<string>");
        for (int i = 0; i < this.bytes.length; ++i) {
            final byte b = this.bytes[i];
            if (b < 16) {
                xml.append('0');
            }
            xml.append(Integer.toHexString(b));
        }
        xml.append("</string>");
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        out.write(128 + this.bytes.length - 1);
        out.write(this.bytes);
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('\"');
        for (int i = 0; i < this.bytes.length; ++i) {
            final byte b = this.bytes[i];
            if (b < 16) {
                ascii.append('0');
            }
            ascii.append(Integer.toHexString(b));
        }
        ascii.append('\"');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.toASCII(ascii, level);
    }
}
