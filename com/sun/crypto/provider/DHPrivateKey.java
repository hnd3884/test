package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.util.Objects;
import javax.crypto.spec.DHParameterSpec;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerOutputStream;
import sun.security.util.DerInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.ProviderException;
import sun.security.util.DerValue;
import java.security.InvalidKeyException;
import java.math.BigInteger;
import java.io.Serializable;
import java.security.PrivateKey;

final class DHPrivateKey implements PrivateKey, javax.crypto.interfaces.DHPrivateKey, Serializable
{
    static final long serialVersionUID = 7565477590005668886L;
    private static final BigInteger PKCS8_VERSION;
    private BigInteger x;
    private byte[] key;
    private byte[] encodedKey;
    private BigInteger p;
    private BigInteger g;
    private int l;
    private int[] DH_data;
    
    DHPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws InvalidKeyException {
        this(bigInteger, bigInteger2, bigInteger3, 0);
    }
    
    DHPrivateKey(final BigInteger x, final BigInteger p4, final BigInteger g, final int l) {
        this.DH_data = new int[] { 1, 2, 840, 113549, 1, 3, 1 };
        this.x = x;
        this.p = p4;
        this.g = g;
        this.l = l;
        try {
            this.key = new DerValue((byte)2, this.x.toByteArray()).toByteArray();
            this.encodedKey = this.getEncoded();
        }
        catch (final IOException ex) {
            throw new ProviderException("Cannot produce ASN.1 encoding", ex);
        }
    }
    
    DHPrivateKey(final byte[] array) throws InvalidKeyException {
        this.DH_data = new int[] { 1, 2, 840, 113549, 1, 3, 1 };
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        try {
            final DerValue derValue = new DerValue(byteArrayInputStream);
            if (derValue.tag != 48) {
                throw new InvalidKeyException("Key not a SEQUENCE");
            }
            final BigInteger bigInteger = derValue.data.getBigInteger();
            if (!bigInteger.equals(DHPrivateKey.PKCS8_VERSION)) {
                throw new IOException("version mismatch: (supported: " + DHPrivateKey.PKCS8_VERSION + ", parsed: " + bigInteger);
            }
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.tag != 48) {
                throw new InvalidKeyException("AlgId is not a SEQUENCE");
            }
            final DerInputStream derInputStream = derValue2.toDerInputStream();
            if (derInputStream.getOID() == null) {
                throw new InvalidKeyException("Null OID");
            }
            if (derInputStream.available() == 0) {
                throw new InvalidKeyException("Parameters missing");
            }
            final DerValue derValue3 = derInputStream.getDerValue();
            if (derValue3.tag == 5) {
                throw new InvalidKeyException("Null parameters");
            }
            if (derValue3.tag != 48) {
                throw new InvalidKeyException("Parameters not a SEQUENCE");
            }
            derValue3.data.reset();
            this.p = derValue3.data.getBigInteger();
            this.g = derValue3.data.getBigInteger();
            if (derValue3.data.available() != 0) {
                this.l = derValue3.data.getInteger();
            }
            if (derValue3.data.available() != 0) {
                throw new InvalidKeyException("Extra parameter data");
            }
            this.key = derValue.data.getOctetString();
            this.parseKeyBits();
            this.encodedKey = array.clone();
        }
        catch (final IOException | NumberFormatException ex) {
            throw new InvalidKeyException("Error parsing key encoding", (Throwable)ex);
        }
    }
    
    @Override
    public String getFormat() {
        return "PKCS#8";
    }
    
    @Override
    public String getAlgorithm() {
        return "DH";
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        if (this.encodedKey == null) {
            try {
                final DerOutputStream derOutputStream = new DerOutputStream();
                derOutputStream.putInteger(DHPrivateKey.PKCS8_VERSION);
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putOID(new ObjectIdentifier(this.DH_data));
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.putInteger(this.p);
                derOutputStream3.putInteger(this.g);
                if (this.l != 0) {
                    derOutputStream3.putInteger(this.l);
                }
                derOutputStream2.putDerValue(new DerValue((byte)48, derOutputStream3.toByteArray()));
                derOutputStream.write((byte)48, derOutputStream2);
                derOutputStream.putOctetString(this.key);
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                derOutputStream4.write((byte)48, derOutputStream);
                this.encodedKey = derOutputStream4.toByteArray();
            }
            catch (final IOException ex) {
                return null;
            }
        }
        return this.encodedKey.clone();
    }
    
    @Override
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public DHParameterSpec getParams() {
        if (this.l != 0) {
            return new DHParameterSpec(this.p, this.g, this.l);
        }
        return new DHParameterSpec(this.p, this.g);
    }
    
    private void parseKeyBits() throws InvalidKeyException {
        try {
            this.x = new DerInputStream(this.key).getBigInteger();
        }
        catch (final IOException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("Error parsing key encoding: " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.p, this.g);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof javax.crypto.interfaces.DHPrivateKey)) {
            return false;
        }
        final javax.crypto.interfaces.DHPrivateKey dhPrivateKey = (javax.crypto.interfaces.DHPrivateKey)o;
        final DHParameterSpec params = dhPrivateKey.getParams();
        return this.x.compareTo(dhPrivateKey.getX()) == 0 && this.p.compareTo(params.getP()) == 0 && this.g.compareTo(params.getG()) == 0;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PRIVATE, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    static {
        PKCS8_VERSION = BigInteger.ZERO;
    }
}
