package org.bouncycastle.crypto.parsers;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.util.io.Streams;
import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.InputStream;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.KeyParser;

public class ECIESPublicKeyParser implements KeyParser
{
    private ECDomainParameters ecParams;
    
    public ECIESPublicKeyParser(final ECDomainParameters ecParams) {
        this.ecParams = ecParams;
    }
    
    public AsymmetricKeyParameter readKey(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        byte[] array = null;
        switch (read) {
            case 0: {
                throw new IOException("Sender's public key invalid.");
            }
            case 2:
            case 3: {
                array = new byte[1 + (this.ecParams.getCurve().getFieldSize() + 7) / 8];
                break;
            }
            case 4:
            case 6:
            case 7: {
                array = new byte[1 + 2 * ((this.ecParams.getCurve().getFieldSize() + 7) / 8)];
                break;
            }
            default: {
                throw new IOException("Sender's public key has invalid point encoding 0x" + Integer.toString(read, 16));
            }
        }
        array[0] = (byte)read;
        Streams.readFully(inputStream, array, 1, array.length - 1);
        return new ECPublicKeyParameters(this.ecParams.getCurve().decodePoint(array), this.ecParams);
    }
}
