package sun.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class UUDecoder extends CharacterDecoder
{
    public String bufferName;
    public int mode;
    private byte[] decoderBuffer;
    
    public UUDecoder() {
        this.decoderBuffer = new byte[4];
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
    protected void decodeAtom(final PushbackInputStream pushbackInputStream, final OutputStream outputStream, final int n) throws IOException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; ++i) {
            final int read = pushbackInputStream.read();
            if (read == -1) {
                throw new CEStreamExhausted();
            }
            sb.append((char)read);
            this.decoderBuffer[i] = (byte)(read - 32 & 0x3F);
        }
        final int n2 = (this.decoderBuffer[0] << 2 & 0xFC) | (this.decoderBuffer[1] >>> 4 & 0x3);
        final int n3 = (this.decoderBuffer[1] << 4 & 0xF0) | (this.decoderBuffer[2] >>> 2 & 0xF);
        final int n4 = (this.decoderBuffer[2] << 6 & 0xC0) | (this.decoderBuffer[3] & 0x3F);
        outputStream.write((byte)(n2 & 0xFF));
        if (n > 1) {
            outputStream.write((byte)(n3 & 0xFF));
        }
        if (n > 2) {
            outputStream.write((byte)(n4 & 0xFF));
        }
    }
    
    @Override
    protected void decodeBufferPrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        final StringBuffer sb = new StringBuffer(32);
        boolean b = true;
        while (true) {
            int n = pushbackInputStream.read();
            if (n == -1) {
                throw new CEFormatException("UUDecoder: No begin line.");
            }
            if (n == 98 && b) {
                n = pushbackInputStream.read();
                if (n == 101) {
                    while (n != 10 && n != 13) {
                        n = pushbackInputStream.read();
                        if (n == -1) {
                            throw new CEFormatException("UUDecoder: No begin line.");
                        }
                        if (n == 10 || n == 13) {
                            continue;
                        }
                        sb.append((char)n);
                    }
                    final String string = sb.toString();
                    if (string.indexOf(32) != 3) {
                        throw new CEFormatException("UUDecoder: Malformed begin line.");
                    }
                    this.mode = Integer.parseInt(string.substring(4, 7));
                    this.bufferName = string.substring(string.indexOf(32, 6) + 1);
                    if (n == 13) {
                        final int read = pushbackInputStream.read();
                        if (read != 10 && read != -1) {
                            pushbackInputStream.unread(read);
                        }
                    }
                    return;
                }
            }
            b = (n == 10 || n == 13);
        }
    }
    
    @Override
    protected int decodeLinePrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        final int read = pushbackInputStream.read();
        if (read == 32) {
            pushbackInputStream.read();
            final int read2 = pushbackInputStream.read();
            if (read2 != 10 && read2 != -1) {
                pushbackInputStream.unread(read2);
            }
            throw new CEStreamExhausted();
        }
        if (read == -1) {
            throw new CEFormatException("UUDecoder: Short Buffer.");
        }
        final int n = read - 32 & 0x3F;
        if (n > this.bytesPerLine()) {
            throw new CEFormatException("UUDecoder: Bad Line Length.");
        }
        return n;
    }
    
    @Override
    protected void decodeLineSuffix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        int i;
        do {
            i = pushbackInputStream.read();
            if (i == -1) {
                throw new CEStreamExhausted();
            }
            if (i == 10) {
                return;
            }
        } while (i != 13);
        final int read = pushbackInputStream.read();
        if (read != 10 && read != -1) {
            pushbackInputStream.unread(read);
        }
    }
    
    @Override
    protected void decodeBufferSuffix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        pushbackInputStream.read(this.decoderBuffer);
        if (this.decoderBuffer[0] != 101 || this.decoderBuffer[1] != 110 || this.decoderBuffer[2] != 100) {
            throw new CEFormatException("UUDecoder: Missing 'end' line.");
        }
    }
}
