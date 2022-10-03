package sun.misc;

import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public abstract class CharacterDecoder
{
    protected abstract int bytesPerAtom();
    
    protected abstract int bytesPerLine();
    
    protected void decodeBufferPrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
    }
    
    protected void decodeBufferSuffix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
    }
    
    protected int decodeLinePrefix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
        return this.bytesPerLine();
    }
    
    protected void decodeLineSuffix(final PushbackInputStream pushbackInputStream, final OutputStream outputStream) throws IOException {
    }
    
    protected void decodeAtom(final PushbackInputStream pushbackInputStream, final OutputStream outputStream, final int n) throws IOException {
        throw new CEStreamExhausted();
    }
    
    protected int readFully(final InputStream inputStream, final byte[] array, final int n, final int n2) throws IOException {
        for (int i = 0; i < n2; ++i) {
            final int read = inputStream.read();
            if (read == -1) {
                return (i == 0) ? -1 : i;
            }
            array[i + n] = (byte)read;
        }
        return n2;
    }
    
    public void decodeBuffer(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        int n = 0;
        final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        this.decodeBufferPrefix(pushbackInputStream, outputStream);
        try {
            while (true) {
                int decodeLinePrefix;
                int n2;
                for (decodeLinePrefix = this.decodeLinePrefix(pushbackInputStream, outputStream), n2 = 0; n2 + this.bytesPerAtom() < decodeLinePrefix; n2 += this.bytesPerAtom()) {
                    this.decodeAtom(pushbackInputStream, outputStream, this.bytesPerAtom());
                    n += this.bytesPerAtom();
                }
                if (n2 + this.bytesPerAtom() == decodeLinePrefix) {
                    this.decodeAtom(pushbackInputStream, outputStream, this.bytesPerAtom());
                    n += this.bytesPerAtom();
                }
                else {
                    this.decodeAtom(pushbackInputStream, outputStream, decodeLinePrefix - n2);
                    n += decodeLinePrefix - n2;
                }
                this.decodeLineSuffix(pushbackInputStream, outputStream);
            }
        }
        catch (final CEStreamExhausted ceStreamExhausted) {
            this.decodeBufferSuffix(pushbackInputStream, outputStream);
        }
    }
    
    public byte[] decodeBuffer(final String s) throws IOException {
        final byte[] array = new byte[s.length()];
        s.getBytes(0, s.length(), array, 0);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.decodeBuffer(byteArrayInputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public byte[] decodeBuffer(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.decodeBuffer(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public ByteBuffer decodeBufferToByteBuffer(final String s) throws IOException {
        return ByteBuffer.wrap(this.decodeBuffer(s));
    }
    
    public ByteBuffer decodeBufferToByteBuffer(final InputStream inputStream) throws IOException {
        return ByteBuffer.wrap(this.decodeBuffer(inputStream));
    }
}
