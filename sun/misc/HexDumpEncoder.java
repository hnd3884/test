package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HexDumpEncoder extends CharacterEncoder
{
    private int offset;
    private int thisLineLength;
    private int currentByte;
    private byte[] thisLine;
    
    public HexDumpEncoder() {
        this.thisLine = new byte[16];
    }
    
    static void hexDigit(final PrintStream printStream, final byte b) {
        final char c = (char)(b >> 4 & 0xF);
        char c2;
        if (c > '\t') {
            c2 = (char)(c - '\n' + 65);
        }
        else {
            c2 = (char)(c + '0');
        }
        printStream.write(c2);
        final char c3 = (char)(b & 0xF);
        char c4;
        if (c3 > '\t') {
            c4 = (char)(c3 - '\n' + 65);
        }
        else {
            c4 = (char)(c3 + '0');
        }
        printStream.write(c4);
    }
    
    @Override
    protected int bytesPerAtom() {
        return 1;
    }
    
    @Override
    protected int bytesPerLine() {
        return 16;
    }
    
    @Override
    protected void encodeBufferPrefix(final OutputStream outputStream) throws IOException {
        this.offset = 0;
        super.encodeBufferPrefix(outputStream);
    }
    
    @Override
    protected void encodeLinePrefix(final OutputStream outputStream, final int thisLineLength) throws IOException {
        hexDigit(this.pStream, (byte)(this.offset >>> 8 & 0xFF));
        hexDigit(this.pStream, (byte)(this.offset & 0xFF));
        this.pStream.print(": ");
        this.currentByte = 0;
        this.thisLineLength = thisLineLength;
    }
    
    @Override
    protected void encodeAtom(final OutputStream outputStream, final byte[] array, final int n, final int n2) throws IOException {
        this.thisLine[this.currentByte] = array[n];
        hexDigit(this.pStream, array[n]);
        this.pStream.print(" ");
        ++this.currentByte;
        if (this.currentByte == 8) {
            this.pStream.print("  ");
        }
    }
    
    @Override
    protected void encodeLineSuffix(final OutputStream outputStream) throws IOException {
        if (this.thisLineLength < 16) {
            for (int i = this.thisLineLength; i < 16; ++i) {
                this.pStream.print("   ");
                if (i == 7) {
                    this.pStream.print("  ");
                }
            }
        }
        this.pStream.print(" ");
        for (int j = 0; j < this.thisLineLength; ++j) {
            if (this.thisLine[j] < 32 || this.thisLine[j] > 122) {
                this.pStream.print(".");
            }
            else {
                this.pStream.write(this.thisLine[j]);
            }
        }
        this.pStream.println();
        this.offset += this.thisLineLength;
    }
}
