package com.dd.plist;

import java.util.Arrays;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;

public class NSData extends NSObject
{
    private final byte[] bytes;
    
    public NSData(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    public NSData(final String base64) throws IOException {
        final String data = base64.replaceAll("\\s+", "");
        this.bytes = Base64.decode(data, 4);
    }
    
    public NSData(final File file) throws IOException {
        this.bytes = new byte[(int)file.length()];
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.read(this.bytes);
        raf.close();
    }
    
    public byte[] bytes() {
        return this.bytes;
    }
    
    public int length() {
        return this.bytes.length;
    }
    
    public void getBytes(final ByteBuffer buf, final int length) {
        buf.put(this.bytes, 0, Math.min(this.bytes.length, length));
    }
    
    public void getBytes(final ByteBuffer buf, final int rangeStart, final int rangeStop) {
        buf.put(this.bytes, rangeStart, Math.min(this.bytes.length, rangeStop));
    }
    
    public String getBase64EncodedData() {
        return Base64.encodeBytes(this.bytes);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj.getClass().equals(this.getClass()) && Arrays.equals(((NSData)obj).bytes, this.bytes);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.hashCode(this.bytes);
        return hash;
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<data>");
        xml.append(NSObject.NEWLINE);
        final String base64 = this.getBase64EncodedData();
        for (final String line : base64.split("\n")) {
            this.indent(xml, level + 1);
            xml.append(line);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</data>");
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(4, this.bytes.length);
        out.write(this.bytes);
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('<');
        int indexOfLastNewLine = ascii.lastIndexOf(NSData.NEWLINE);
        for (int i = 0; i < this.bytes.length; ++i) {
            final int b = this.bytes[i] & 0xFF;
            if (b < 16) {
                ascii.append('0');
            }
            ascii.append(Integer.toHexString(b));
            if (ascii.length() - indexOfLastNewLine > 80) {
                ascii.append(NSData.NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
            else if ((i + 1) % 2 == 0 && i != this.bytes.length - 1) {
                ascii.append(' ');
            }
        }
        ascii.append('>');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.toASCII(ascii, level);
    }
}
