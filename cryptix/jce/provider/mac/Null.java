package cryptix.jce.provider.mac;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.MacSpi;

public final class Null extends MacSpi
{
    protected final int engineGetMacLength() {
        return 0;
    }
    
    protected final void engineInit(final Key key, final AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
    }
    
    protected final void engineUpdate(final byte input) {
    }
    
    protected final void engineUpdate(final byte[] input, final int offset, final int len) {
    }
    
    protected final byte[] engineDoFinal() {
        return new byte[0];
    }
    
    protected final void engineReset() {
    }
    
    public Object clone() throws CloneNotSupportedException {
        return new Null();
    }
}
