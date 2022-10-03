package sun.security.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.InvalidKeyException;
import java.math.BigInteger;

public final class DSAPublicKeyImpl extends DSAPublicKey
{
    private static final long serialVersionUID = 7819830118247182730L;
    
    public DSAPublicKeyImpl(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws InvalidKeyException {
        super(bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public DSAPublicKeyImpl(final byte[] array) throws InvalidKeyException {
        super(array);
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
}
