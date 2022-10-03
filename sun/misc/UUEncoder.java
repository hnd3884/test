package sun.misc;

import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;

public class UUEncoder extends CharacterEncoder
{
    private String bufferName;
    private int mode;
    
    public UUEncoder() {
        this.bufferName = "encoder.buf";
        this.mode = 644;
    }
    
    public UUEncoder(final String bufferName) {
        this.bufferName = bufferName;
        this.mode = 644;
    }
    
    public UUEncoder(final String bufferName, final int mode) {
        this.bufferName = bufferName;
        this.mode = mode;
    }
    
    @Override
    protected int bytesPerAtom() {
        return 3;
    }
    
    @Override
    protected int bytesPerLine() {
        return 45;
    }
    
    @Override
    protected void encodeAtom(final OutputStream outputStream, final byte[] array, final int n, final int n2) throws IOException {
        int n3 = 1;
        int n4 = 1;
        final byte b = array[n];
        if (n2 > 1) {
            n3 = array[n + 1];
        }
        if (n2 > 2) {
            n4 = array[n + 2];
        }
        final int n5 = b >>> 2 & 0x3F;
        final int n6 = (b << 4 & 0x30) | (n3 >>> 4 & 0xF);
        final int n7 = (n3 << 2 & 0x3C) | (n4 >>> 6 & 0x3);
        final int n8 = n4 & 0x3F;
        outputStream.write(n5 + 32);
        outputStream.write(n6 + 32);
        outputStream.write(n7 + 32);
        outputStream.write(n8 + 32);
    }
    
    @Override
    protected void encodeLinePrefix(final OutputStream outputStream, final int n) throws IOException {
        outputStream.write((n & 0x3F) + 32);
    }
    
    @Override
    protected void encodeLineSuffix(final OutputStream outputStream) throws IOException {
        this.pStream.println();
    }
    
    @Override
    protected void encodeBufferPrefix(final OutputStream outputStream) throws IOException {
        (super.pStream = new PrintStream(outputStream)).print("begin " + this.mode + " ");
        if (this.bufferName != null) {
            super.pStream.println(this.bufferName);
        }
        else {
            super.pStream.println("encoder.bin");
        }
        super.pStream.flush();
    }
    
    @Override
    protected void encodeBufferSuffix(final OutputStream outputStream) throws IOException {
        super.pStream.println(" \nend");
        super.pStream.flush();
    }
}
