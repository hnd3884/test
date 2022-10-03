package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.util.Objects;
import sun.security.util.Debug;
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
import java.security.PublicKey;

final class DHPublicKey implements PublicKey, javax.crypto.interfaces.DHPublicKey, Serializable
{
    static final long serialVersionUID = 7647557958927458271L;
    private BigInteger y;
    private byte[] key;
    private byte[] encodedKey;
    private BigInteger p;
    private BigInteger g;
    private int l;
    private int[] DH_data;
    
    DHPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws InvalidKeyException {
        this(bigInteger, bigInteger2, bigInteger3, 0);
    }
    
    DHPublicKey(final BigInteger y, final BigInteger p4, final BigInteger g, final int l) {
        this.DH_data = new int[] { 1, 2, 840, 113549, 1, 3, 1 };
        this.y = y;
        this.p = p4;
        this.g = g;
        this.l = l;
        try {
            this.key = new DerValue((byte)2, this.y.toByteArray()).toByteArray();
            this.encodedKey = this.getEncoded();
        }
        catch (final IOException ex) {
            throw new ProviderException("Cannot produce ASN.1 encoding", ex);
        }
    }
    
    DHPublicKey(final byte[] array) throws InvalidKeyException {
        this.DH_data = new int[] { 1, 2, 840, 113549, 1, 3, 1 };
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        try {
            final DerValue derValue = new DerValue(byteArrayInputStream);
            if (derValue.tag != 48) {
                throw new InvalidKeyException("Invalid key format");
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
            this.key = derValue.data.getBitString();
            this.parseKeyBits();
            if (derValue.data.available() != 0) {
                throw new InvalidKeyException("Excess key data");
            }
            this.encodedKey = array.clone();
        }
        catch (final IOException | NumberFormatException ex) {
            throw new InvalidKeyException("Error parsing key encoding", (Throwable)ex);
        }
    }
    
    @Override
    public String getFormat() {
        return "X.509";
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
                derOutputStream.putOID(new ObjectIdentifier(this.DH_data));
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                derOutputStream2.putInteger(this.p);
                derOutputStream2.putInteger(this.g);
                if (this.l != 0) {
                    derOutputStream2.putInteger(this.l);
                }
                derOutputStream.putDerValue(new DerValue((byte)48, derOutputStream2.toByteArray()));
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.write((byte)48, derOutputStream);
                derOutputStream3.putBitString(this.key);
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                derOutputStream4.write((byte)48, derOutputStream3);
                this.encodedKey = derOutputStream4.toByteArray();
            }
            catch (final IOException ex) {
                return null;
            }
        }
        return this.encodedKey.clone();
    }
    
    @Override
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public DHParameterSpec getParams() {
        if (this.l != 0) {
            return new DHParameterSpec(this.p, this.g, this.l);
        }
        return new DHParameterSpec(this.p, this.g);
    }
    
    @Override
    public String toString() {
        final String property = System.getProperty("line.separator");
        final StringBuffer sb = new StringBuffer("SunJCE Diffie-Hellman Public Key:" + property + "y:" + property + Debug.toHexString(this.y) + property + "p:" + property + Debug.toHexString(this.p) + property + "g:" + property + Debug.toHexString(this.g));
        if (this.l != 0) {
            sb.append(property + "l:" + property + "    " + this.l);
        }
        return sb.toString();
    }
    
    private void parseKeyBits() throws InvalidKeyException {
        try {
            this.y = new DerInputStream(this.key).getBigInteger();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Error parsing key encoding: " + ex.toString());
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.y, this.p, this.g);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof javax.crypto.interfaces.DHPublicKey)) {
            return false;
        }
        final javax.crypto.interfaces.DHPublicKey dhPublicKey = (javax.crypto.interfaces.DHPublicKey)o;
        final DHParameterSpec params = dhPublicKey.getParams();
        return this.y.compareTo(dhPublicKey.getY()) == 0 && this.p.compareTo(params.getP()) == 0 && this.g.compareTo(params.getG()) == 0;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
}
