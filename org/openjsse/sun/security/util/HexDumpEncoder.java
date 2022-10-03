package org.openjsse.sun.security.util;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HexDumpEncoder
{
    private int offset;
    private int thisLineLength;
    private int currentByte;
    private byte[] thisLine;
    protected PrintStream pStream;
    
    public HexDumpEncoder() {
        this.thisLine = new byte[16];
    }
    
    static void hexDigit(final PrintStream p, final byte x) {
        char c = (char)(x >> 4 & 0xF);
        if (c > '\t') {
            c = (char)(c - '\n' + 65);
        }
        else {
            c += '0';
        }
        p.write(c);
        c = (char)(x & 0xF);
        if (c > '\t') {
            c = (char)(c - '\n' + 65);
        }
        else {
            c += '0';
        }
        p.write(c);
    }
    
    protected int bytesPerAtom() {
        return 1;
    }
    
    protected int bytesPerLine() {
        return 16;
    }
    
    protected void encodeBufferPrefix(final OutputStream o) throws IOException {
        this.offset = 0;
        this.pStream = new PrintStream(o);
    }
    
    protected void encodeLinePrefix(final OutputStream o, final int len) throws IOException {
        hexDigit(this.pStream, (byte)(this.offset >>> 8 & 0xFF));
        hexDigit(this.pStream, (byte)(this.offset & 0xFF));
        this.pStream.print(": ");
        this.currentByte = 0;
        this.thisLineLength = len;
    }
    
    protected void encodeAtom(final OutputStream o, final byte[] buf, final int off, final int len) throws IOException {
        this.thisLine[this.currentByte] = buf[off];
        hexDigit(this.pStream, buf[off]);
        this.pStream.print(" ");
        ++this.currentByte;
        if (this.currentByte == 8) {
            this.pStream.print("  ");
        }
    }
    
    protected void encodeLineSuffix(final OutputStream o) throws IOException {
        if (this.thisLineLength < 16) {
            for (int i = this.thisLineLength; i < 16; ++i) {
                this.pStream.print("   ");
                if (i == 7) {
                    this.pStream.print("  ");
                }
            }
        }
        this.pStream.print(" ");
        for (int i = 0; i < this.thisLineLength; ++i) {
            if (this.thisLine[i] < 32 || this.thisLine[i] > 122) {
                this.pStream.print(".");
            }
            else {
                this.pStream.write(this.thisLine[i]);
            }
        }
        this.pStream.println();
        this.offset += this.thisLineLength;
    }
    
    protected int readFully(final InputStream in, final byte[] buffer) throws IOException {
        for (int i = 0; i < buffer.length; ++i) {
            final int q = in.read();
            if (q == -1) {
                return i;
            }
            buffer[i] = (byte)q;
        }
        return buffer.length;
    }
    
    public void encode(final InputStream inStream, final OutputStream outStream) throws IOException {
        final byte[] tmpbuffer = new byte[this.bytesPerLine()];
        this.encodeBufferPrefix(outStream);
        while (true) {
            final int numBytes = this.readFully(inStream, tmpbuffer);
            if (numBytes == 0) {
                break;
            }
            this.encodeLinePrefix(outStream, numBytes);
            for (int j = 0; j < numBytes; j += this.bytesPerAtom()) {
                if (j + this.bytesPerAtom() <= numBytes) {
                    this.encodeAtom(outStream, tmpbuffer, j, this.bytesPerAtom());
                }
                else {
                    this.encodeAtom(outStream, tmpbuffer, j, numBytes - j);
                }
            }
            if (numBytes < this.bytesPerLine()) {
                break;
            }
            this.encodeLineSuffix(outStream);
        }
    }
    
    public String encode(final byte[] aBuffer) {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        String retVal = null;
        try {
            this.encode(inStream, outStream);
            retVal = outStream.toString("ISO-8859-1");
        }
        catch (final Exception IOException) {
            throw new Error("CharacterEncoder.encode internal error");
        }
        return retVal;
    }
    
    private byte[] getBytes(final ByteBuffer bb) {
        byte[] buf = null;
        if (bb.hasArray()) {
            final byte[] tmp = bb.array();
            if (tmp.length == bb.capacity() && tmp.length == bb.remaining()) {
                buf = tmp;
                bb.position(bb.limit());
            }
        }
        if (buf == null) {
            buf = new byte[bb.remaining()];
            bb.get(buf);
        }
        return buf;
    }
    
    public String encode(final ByteBuffer aBuffer) {
        final byte[] buf = this.getBytes(aBuffer);
        return this.encode(buf);
    }
    
    public void encodeBuffer(final InputStream inStream, final OutputStream outStream) throws IOException {
        final byte[] tmpbuffer = new byte[this.bytesPerLine()];
        this.encodeBufferPrefix(outStream);
        int numBytes;
        do {
            numBytes = this.readFully(inStream, tmpbuffer);
            if (numBytes == 0) {
                break;
            }
            this.encodeLinePrefix(outStream, numBytes);
            for (int j = 0; j < numBytes; j += this.bytesPerAtom()) {
                if (j + this.bytesPerAtom() <= numBytes) {
                    this.encodeAtom(outStream, tmpbuffer, j, this.bytesPerAtom());
                }
                else {
                    this.encodeAtom(outStream, tmpbuffer, j, numBytes - j);
                }
            }
            this.encodeLineSuffix(outStream);
        } while (numBytes >= this.bytesPerLine());
    }
    
    public void encodeBuffer(final byte[] aBuffer, final OutputStream aStream) throws IOException {
        final ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        this.encodeBuffer(inStream, aStream);
    }
    
    public String encodeBuffer(final byte[] aBuffer) {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
        try {
            this.encodeBuffer(inStream, outStream);
        }
        catch (final Exception IOException) {
            throw new Error("CharacterEncoder.encodeBuffer internal error");
        }
        return outStream.toString();
    }
    
    public void encodeBuffer(final ByteBuffer aBuffer, final OutputStream aStream) throws IOException {
        final byte[] buf = this.getBytes(aBuffer);
        this.encodeBuffer(buf, aStream);
    }
}
