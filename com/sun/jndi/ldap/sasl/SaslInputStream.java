package com.sun.jndi.ldap.sasl;

import java.io.EOFException;
import java.io.IOException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslClient;
import java.io.InputStream;

public class SaslInputStream extends InputStream
{
    private static final boolean debug = false;
    private byte[] saslBuffer;
    private byte[] lenBuf;
    private byte[] buf;
    private int bufPos;
    private InputStream in;
    private SaslClient sc;
    private int recvMaxBufSize;
    
    SaslInputStream(final SaslClient sc, final InputStream in) throws SaslException {
        this.lenBuf = new byte[4];
        this.buf = new byte[0];
        this.bufPos = 0;
        this.recvMaxBufSize = 65536;
        this.in = in;
        this.sc = sc;
        final String s = (String)sc.getNegotiatedProperty("javax.security.sasl.maxbuffer");
        if (s != null) {
            try {
                this.recvMaxBufSize = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                throw new SaslException("javax.security.sasl.maxbuffer property must be numeric string: " + s);
            }
        }
        this.saslBuffer = new byte[this.recvMaxBufSize];
    }
    
    @Override
    public int read() throws IOException {
        final byte[] array = { 0 };
        if (this.read(array, 0, 1) > 0) {
            return array[0];
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.bufPos >= this.buf.length) {
            int i;
            for (i = this.fill(); i == 0; i = this.fill()) {}
            if (i == -1) {
                return -1;
            }
        }
        final int n3 = this.buf.length - this.bufPos;
        if (n2 > n3) {
            System.arraycopy(this.buf, this.bufPos, array, n, n3);
            this.bufPos = this.buf.length;
            return n3;
        }
        System.arraycopy(this.buf, this.bufPos, array, n, n2);
        this.bufPos += n2;
        return n2;
    }
    
    private int fill() throws IOException {
        if (this.readFully(this.lenBuf, 4) != 4) {
            return -1;
        }
        final int networkByteOrderToInt = networkByteOrderToInt(this.lenBuf, 0, 4);
        if (networkByteOrderToInt > this.recvMaxBufSize) {
            throw new IOException(networkByteOrderToInt + "exceeds the negotiated receive buffer size limit:" + this.recvMaxBufSize);
        }
        final int fully = this.readFully(this.saslBuffer, networkByteOrderToInt);
        if (fully != networkByteOrderToInt) {
            throw new EOFException("Expecting to read " + networkByteOrderToInt + " bytes but got " + fully + " bytes before EOF");
        }
        this.buf = this.sc.unwrap(this.saslBuffer, 0, networkByteOrderToInt);
        this.bufPos = 0;
        return this.buf.length;
    }
    
    private int readFully(final byte[] array, int i) throws IOException {
        int n = 0;
        while (i > 0) {
            final int read = this.in.read(array, n, i);
            if (read == -1) {
                return (n == 0) ? -1 : n;
            }
            n += read;
            i -= read;
        }
        return n;
    }
    
    @Override
    public int available() throws IOException {
        return this.buf.length - this.bufPos;
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
        this.in.close();
        if (ex != null) {
            throw ex;
        }
    }
    
    private static int networkByteOrderToInt(final byte[] array, final int n, final int n2) {
        if (n2 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            n3 = (n3 << 8 | (array[n + i] & 0xFF));
        }
        return n3;
    }
}
