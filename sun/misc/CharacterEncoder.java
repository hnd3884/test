package sun.misc;

import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public abstract class CharacterEncoder
{
    protected PrintStream pStream;
    
    protected abstract int bytesPerAtom();
    
    protected abstract int bytesPerLine();
    
    protected void encodeBufferPrefix(final OutputStream outputStream) throws IOException {
        this.pStream = new PrintStream(outputStream);
    }
    
    protected void encodeBufferSuffix(final OutputStream outputStream) throws IOException {
    }
    
    protected void encodeLinePrefix(final OutputStream outputStream, final int n) throws IOException {
    }
    
    protected void encodeLineSuffix(final OutputStream outputStream) throws IOException {
        this.pStream.println();
    }
    
    protected abstract void encodeAtom(final OutputStream p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    protected int readFully(final InputStream inputStream, final byte[] array) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            final int read = inputStream.read();
            if (read == -1) {
                return i;
            }
            array[i] = (byte)read;
        }
        return array.length;
    }
    
    public void encode(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[this.bytesPerLine()];
        this.encodeBufferPrefix(outputStream);
        while (true) {
            final int fully = this.readFully(inputStream, array);
            if (fully == 0) {
                break;
            }
            this.encodeLinePrefix(outputStream, fully);
            for (int i = 0; i < fully; i += this.bytesPerAtom()) {
                if (i + this.bytesPerAtom() <= fully) {
                    this.encodeAtom(outputStream, array, i, this.bytesPerAtom());
                }
                else {
                    this.encodeAtom(outputStream, array, i, fully - i);
                }
            }
            if (fully < this.bytesPerLine()) {
                break;
            }
            this.encodeLineSuffix(outputStream);
        }
        this.encodeBufferSuffix(outputStream);
    }
    
    public void encode(final byte[] array, final OutputStream outputStream) throws IOException {
        this.encode(new ByteArrayInputStream(array), outputStream);
    }
    
    public String encode(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        String string;
        try {
            this.encode(byteArrayInputStream, byteArrayOutputStream);
            string = byteArrayOutputStream.toString("8859_1");
        }
        catch (final Exception ex) {
            throw new Error("CharacterEncoder.encode internal error");
        }
        return string;
    }
    
    private byte[] getBytes(final ByteBuffer byteBuffer) {
        byte[] array = null;
        if (byteBuffer.hasArray()) {
            final byte[] array2 = byteBuffer.array();
            if (array2.length == byteBuffer.capacity() && array2.length == byteBuffer.remaining()) {
                array = array2;
                byteBuffer.position(byteBuffer.limit());
            }
        }
        if (array == null) {
            array = new byte[byteBuffer.remaining()];
            byteBuffer.get(array);
        }
        return array;
    }
    
    public void encode(final ByteBuffer byteBuffer, final OutputStream outputStream) throws IOException {
        this.encode(this.getBytes(byteBuffer), outputStream);
    }
    
    public String encode(final ByteBuffer byteBuffer) {
        return this.encode(this.getBytes(byteBuffer));
    }
    
    public void encodeBuffer(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] array = new byte[this.bytesPerLine()];
        this.encodeBufferPrefix(outputStream);
        int i;
        do {
            i = this.readFully(inputStream, array);
            if (i == 0) {
                break;
            }
            this.encodeLinePrefix(outputStream, i);
            for (int j = 0; j < i; j += this.bytesPerAtom()) {
                if (j + this.bytesPerAtom() <= i) {
                    this.encodeAtom(outputStream, array, j, this.bytesPerAtom());
                }
                else {
                    this.encodeAtom(outputStream, array, j, i - j);
                }
            }
            this.encodeLineSuffix(outputStream);
        } while (i >= this.bytesPerLine());
        this.encodeBufferSuffix(outputStream);
    }
    
    public void encodeBuffer(final byte[] array, final OutputStream outputStream) throws IOException {
        this.encodeBuffer(new ByteArrayInputStream(array), outputStream);
    }
    
    public String encodeBuffer(final byte[] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        try {
            this.encodeBuffer(byteArrayInputStream, byteArrayOutputStream);
        }
        catch (final Exception ex) {
            throw new Error("CharacterEncoder.encodeBuffer internal error");
        }
        return byteArrayOutputStream.toString();
    }
    
    public void encodeBuffer(final ByteBuffer byteBuffer, final OutputStream outputStream) throws IOException {
        this.encodeBuffer(this.getBytes(byteBuffer), outputStream);
    }
    
    public String encodeBuffer(final ByteBuffer byteBuffer) {
        return this.encodeBuffer(this.getBytes(byteBuffer));
    }
}
