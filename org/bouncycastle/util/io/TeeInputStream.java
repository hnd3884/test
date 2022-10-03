package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class TeeInputStream extends InputStream
{
    private final InputStream input;
    private final OutputStream output;
    
    public TeeInputStream(final InputStream input, final OutputStream output) {
        this.input = input;
        this.output = output;
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.input.read(array, n, n2);
        if (read > 0) {
            this.output.write(array, n, read);
        }
        return read;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.input.read();
        if (read >= 0) {
            this.output.write(read);
        }
        return read;
    }
    
    @Override
    public void close() throws IOException {
        this.input.close();
        this.output.close();
    }
    
    public OutputStream getOutputStream() {
        return this.output;
    }
}
