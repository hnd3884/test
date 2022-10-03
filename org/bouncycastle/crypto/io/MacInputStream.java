package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Mac;
import java.io.FilterInputStream;

public class MacInputStream extends FilterInputStream
{
    protected Mac mac;
    
    public MacInputStream(final InputStream inputStream, final Mac mac) {
        super(inputStream);
        this.mac = mac;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read >= 0) {
            this.mac.update((byte)read);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.in.read(array, n, n2);
        if (read >= 0) {
            this.mac.update(array, n, read);
        }
        return read;
    }
    
    public Mac getMac() {
        return this.mac;
    }
}
