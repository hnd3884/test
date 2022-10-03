package sun.security.ec;

import java.security.AlgorithmParameters;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.ArrayUtil;
import sun.security.util.DerOutputStream;
import sun.security.util.ECParameters;
import sun.security.x509.AlgorithmId;
import java.security.InvalidKeyException;
import java.security.spec.ECParameterSpec;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import sun.security.pkcs.PKCS8Key;

public final class ECPrivateKeyImpl extends PKCS8Key implements ECPrivateKey
{
    private static final long serialVersionUID = 88695385615075129L;
    private BigInteger s;
    private byte[] arrayS;
    private ECParameterSpec params;
    
    public ECPrivateKeyImpl(final byte[] array) throws InvalidKeyException {
        this.decode(array);
    }
    
    public ECPrivateKeyImpl(final BigInteger s, final ECParameterSpec params) throws InvalidKeyException {
        this.s = s;
        this.params = params;
        this.makeEncoding(s);
    }
    
    ECPrivateKeyImpl(final byte[] array, final ECParameterSpec params) throws InvalidKeyException {
        this.arrayS = array.clone();
        this.params = params;
        this.makeEncoding(array);
    }
    
    private void makeEncoding(final byte[] array) throws InvalidKeyException {
        this.algid = new AlgorithmId(AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(this.params));
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putInteger(1);
            final byte[] array2 = array.clone();
            ArrayUtil.reverse(array2);
            derOutputStream.putOctetString(array2);
            this.key = new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    private void makeEncoding(final BigInteger bigInteger) throws InvalidKeyException {
        this.algid = new AlgorithmId(AlgorithmId.EC_oid, ECParameters.getAlgorithmParameters(this.params));
        try {
            final byte[] byteArray = bigInteger.toByteArray();
            final byte[] array = new byte[(this.params.getOrder().bitLength() + 7) / 8];
            System.arraycopy(byteArray, Math.max(byteArray.length - array.length, 0), array, Math.max(array.length - byteArray.length, 0), Math.min(byteArray.length, array.length));
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putInteger(1);
            derOutputStream.putOctetString(array);
            this.key = new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    @Override
    public String getAlgorithm() {
        return "EC";
    }
    
    @Override
    public BigInteger getS() {
        if (this.s == null) {
            final byte[] array = this.arrayS.clone();
            ArrayUtil.reverse(array);
            this.s = new BigInteger(1, array);
        }
        return this.s;
    }
    
    public byte[] getArrayS() {
        if (this.arrayS == null) {
            final byte[] byteArray = this.getS().toByteArray();
            ArrayUtil.reverse(byteArray);
            final int n = (this.params.getOrder().bitLength() + 7) / 8;
            System.arraycopy(byteArray, 0, this.arrayS = new byte[n], 0, Math.min(n, byteArray.length));
        }
        return this.arrayS.clone();
    }
    
    @Override
    public ECParameterSpec getParams() {
        return this.params;
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            final DerValue derValue = new DerInputStream(this.key).getDerValue();
            if (derValue.tag != 48) {
                throw new IOException("Not a SEQUENCE");
            }
            final DerInputStream data = derValue.data;
            if (data.getInteger() != 1) {
                throw new IOException("Version must be 1");
            }
            final byte[] octetString = data.getOctetString();
            ArrayUtil.reverse(octetString);
            this.arrayS = octetString;
            while (data.available() != 0) {
                final DerValue derValue2 = data.getDerValue();
                if (derValue2.isContextSpecific((byte)0)) {
                    continue;
                }
                if (derValue2.isContextSpecific((byte)1)) {
                    continue;
                }
                throw new InvalidKeyException("Unexpected value: " + derValue2);
            }
            final AlgorithmParameters parameters = this.algid.getParameters();
            if (parameters == null) {
                throw new InvalidKeyException("EC domain parameters must be encoded in the algorithm identifier");
            }
            this.params = parameters.getParameterSpec(ECParameterSpec.class);
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Invalid EC private key", ex);
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new InvalidKeyException("Invalid EC private key", ex2);
        }
    }
}
