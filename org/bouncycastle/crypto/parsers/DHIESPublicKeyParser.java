package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.InputStream;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.KeyParser;

public class DHIESPublicKeyParser implements KeyParser
{
    private DHParameters dhParams;
    
    public DHIESPublicKeyParser(final DHParameters dhParams) {
        this.dhParams = dhParams;
    }
    
    public AsymmetricKeyParameter readKey(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[(this.dhParams.getP().bitLength() + 7) / 8];
        Streams.readFully(inputStream, array, 0, array.length);
        return new DHPublicKeyParameters(new BigInteger(1, array), this.dhParams);
    }
}
