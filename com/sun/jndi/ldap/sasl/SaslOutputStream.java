package com.sun.jndi.ldap.sasl;

import java.io.IOException;
import javax.security.sasl.SaslException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import java.io.FilterOutputStream;

class SaslOutputStream extends FilterOutputStream
{
    private static final boolean debug = false;
    private byte[] lenBuf;
    private int rawSendSize;
    private SaslClient sc;
    
    SaslOutputStream(final SaslClient sc, final OutputStream outputStream) throws SaslException {
        super(outputStream);
        this.lenBuf = new byte[4];
        this.rawSendSize = 65536;
        this.sc = sc;
        final String s = (String)sc.getNegotiatedProperty("javax.security.sasl.rawsendsize");
        if (s != null) {
            try {
                this.rawSendSize = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                throw new SaslException("javax.security.sasl.rawsendsize property must be numeric string: " + s);
            }
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        for (int i = 0; i < n2; i += this.rawSendSize) {
            final byte[] wrap = this.sc.wrap(array, n + i, (n2 - i < this.rawSendSize) ? (n2 - i) : this.rawSendSize);
            intToNetworkByteOrder(wrap.length, this.lenBuf, 0, 4);
            this.out.write(this.lenBuf, 0, 4);
            this.out.write(wrap, 0, wrap.length);
        }
    }
    
    @Override
    public void close() throws IOException {
        SaslException ex = null;
        try {
            this.sc.dispose();
        }
        catch (final SaslException ex2) {
            ex = ex2;
        }
        super.close();
        if (ex != null) {
            throw ex;
        }
    }
    
    private static void intToNetworkByteOrder(int n, final byte[] array, final int n2, final int n3) {
        if (n3 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        for (int i = n3 - 1; i >= 0; --i) {
            array[n2 + i] = (byte)(n & 0xFF);
            n >>>= 8;
        }
    }
}
