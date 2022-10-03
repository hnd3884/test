package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import java.io.OutputStream;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class ServerDHParams
{
    protected DHPublicKeyParameters publicKey;
    
    public ServerDHParams(final DHPublicKeyParameters publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("'publicKey' cannot be null");
        }
        this.publicKey = publicKey;
    }
    
    public DHPublicKeyParameters getPublicKey() {
        return this.publicKey;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        final DHParameters parameters = this.publicKey.getParameters();
        final BigInteger y = this.publicKey.getY();
        TlsDHUtils.writeDHParameter(parameters.getP(), outputStream);
        TlsDHUtils.writeDHParameter(parameters.getG(), outputStream);
        TlsDHUtils.writeDHParameter(y, outputStream);
    }
    
    public static ServerDHParams parse(final InputStream inputStream) throws IOException {
        return new ServerDHParams(TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(TlsDHUtils.readDHParameter(inputStream), new DHParameters(TlsDHUtils.readDHParameter(inputStream), TlsDHUtils.readDHParameter(inputStream)))));
    }
}
