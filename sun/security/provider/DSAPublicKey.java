package sun.security.provider;

import sun.security.util.DerInputStream;
import sun.security.util.Debug;
import java.security.AlgorithmParameters;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.DSAParameterSpec;
import java.security.interfaces.DSAParams;
import java.io.IOException;
import java.security.InvalidKeyException;
import sun.security.util.BitArray;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import java.math.BigInteger;
import java.io.Serializable;
import sun.security.x509.X509Key;

public class DSAPublicKey extends X509Key implements java.security.interfaces.DSAPublicKey, Serializable
{
    private static final long serialVersionUID = -2994193307391104133L;
    private BigInteger y;
    
    public DSAPublicKey() {
    }
    
    public DSAPublicKey(final BigInteger y, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws InvalidKeyException {
        this.y = y;
        this.algid = new AlgIdDSA(bigInteger, bigInteger2, bigInteger3);
        try {
            final byte[] byteArray = new DerValue((byte)2, y.toByteArray()).toByteArray();
            this.setKey(new BitArray(byteArray.length * 8, byteArray));
            this.encode();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("could not DER encode y: " + ex.getMessage());
        }
    }
    
    public DSAPublicKey(final byte[] array) throws InvalidKeyException {
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
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public String toString() {
        return "Sun DSA Public Key\n    Parameters:" + this.algid + "\n  y:\n" + Debug.toHexString(this.y) + "\n";
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            this.y = new DerInputStream(this.getKey().toByteArray()).getBigInteger();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Invalid key: y value\n" + ex.getMessage());
        }
    }
}
