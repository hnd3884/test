package org.bouncycastle.jcajce.io;

import java.io.IOException;
import javax.crypto.Mac;
import java.io.OutputStream;

public final class MacOutputStream extends OutputStream
{
    private Mac mac;
    
    public MacOutputStream(final Mac mac) {
        this.mac = mac;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.mac.update((byte)n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.mac.update(array, n, n2);
    }
    
    public byte[] getMac() {
        return this.mac.doFinal();
    }
}
