package sun.security.provider;

import sun.security.util.DerInputStream;
import java.security.AlgorithmParameters;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.DSAParameterSpec;
import java.security.interfaces.DSAParams;
import java.io.IOException;
import java.security.InvalidKeyException;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import java.math.BigInteger;
import java.io.Serializable;
import sun.security.pkcs.PKCS8Key;

public final class DSAPrivateKey extends PKCS8Key implements java.security.interfaces.DSAPrivateKey, Serializable
{
    private static final long serialVersionUID = -3244453684193605938L;
    private BigInteger x;
    
    public DSAPrivateKey() {
    }
    
    public DSAPrivateKey(final BigInteger x, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws InvalidKeyException {
        this.x = x;
        this.algid = new AlgIdDSA(bigInteger, bigInteger2, bigInteger3);
        try {
            this.key = new DerValue((byte)2, x.toByteArray()).toByteArray();
            this.encode();
        }
        catch (final IOException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("could not DER encode x: " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    public DSAPrivateKey(final byte[] array) throws InvalidKeyException {
        this.clearOldKey();
        this.decode(array);
    }
    
    @Override
    public DSAParams getParams() {
        try {
            if (this.algid instanceof DSAParams) {
                return (DSAParams)this.algid;
            }
            final AlgorithmParameters parameters = this.algid.getParameters();
            if (parameters == null) {
                return null;
            }
            return parameters.getParameterSpec(DSAParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ex) {
            return null;
        }
    }
    
    @Override
    public BigInteger getX() {
        return this.x;
    }
    
    private void clearOldKey() {
        if (this.encodedKey != null) {
            for (int i = 0; i < this.encodedKey.length; ++i) {
                this.encodedKey[i] = 0;
            }
        }
        if (this.key != null) {
            for (int j = 0; j < this.key.length; ++j) {
                this.key[j] = 0;
            }
        }
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            this.x = new DerInputStream(this.key).getBigInteger();
        }
        catch (final IOException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
}
