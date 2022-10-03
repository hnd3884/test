package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;

public class ServerSRPParams
{
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger B;
    protected byte[] s;
    
    public ServerSRPParams(final BigInteger n, final BigInteger g, final byte[] array, final BigInteger b) {
        this.N = n;
        this.g = g;
        this.s = Arrays.clone(array);
        this.B = b;
    }
    
    public BigInteger getB() {
        return this.B;
    }
    
    public BigInteger getG() {
        return this.g;
    }
    
    public BigInteger getN() {
        return this.N;
    }
    
    public byte[] getS() {
        return this.s;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsSRPUtils.writeSRPParameter(this.N, outputStream);
        TlsSRPUtils.writeSRPParameter(this.g, outputStream);
        TlsUtils.writeOpaque8(this.s, outputStream);
        TlsSRPUtils.writeSRPParameter(this.B, outputStream);
    }
    
    public static ServerSRPParams parse(final InputStream inputStream) throws IOException {
        return new ServerSRPParams(TlsSRPUtils.readSRPParameter(inputStream), TlsSRPUtils.readSRPParameter(inputStream), TlsUtils.readOpaque8(inputStream), TlsSRPUtils.readSRPParameter(inputStream));
    }
}
