package sun.security.pkcs;

import java.security.MessageDigest;
import java.security.Key;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import sun.security.util.DerOutputStream;
import sun.security.util.Debug;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.InvalidKeyException;
import java.math.BigInteger;
import sun.security.x509.AlgorithmId;
import java.security.PrivateKey;

public class PKCS8Key implements PrivateKey
{
    private static final long serialVersionUID = -3836890099307167124L;
    protected AlgorithmId algid;
    protected byte[] key;
    protected byte[] encodedKey;
    public static final BigInteger version;
    
    public PKCS8Key() {
    }
    
    private PKCS8Key(final AlgorithmId algid, final byte[] key) throws InvalidKeyException {
        this.algid = algid;
        this.key = key;
        this.encode();
    }
    
    public static PKCS8Key parse(final DerValue derValue) throws IOException {
        final PrivateKey key = parseKey(derValue);
        if (key instanceof PKCS8Key) {
            return (PKCS8Key)key;
        }
        throw new IOException("Provider did not return PKCS8Key");
    }
    
    public static PrivateKey parseKey(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("corrupt private key");
        }
        final BigInteger bigInteger = derValue.data.getBigInteger();
        if (!PKCS8Key.version.equals(bigInteger)) {
            throw new IOException("version mismatch: (supported: " + Debug.toHexString(PKCS8Key.version) + ", parsed: " + Debug.toHexString(bigInteger));
        }
        final AlgorithmId parse = AlgorithmId.parse(derValue.data.getDerValue());
        PrivateKey buildPKCS8Key;
        try {
            buildPKCS8Key = buildPKCS8Key(parse, derValue.data.getOctetString());
        }
        catch (final InvalidKeyException ex) {
            throw new IOException("corrupt private key");
        }
        if (derValue.data.available() != 0) {
            throw new IOException("excess private key");
        }
        return buildPKCS8Key;
    }
    
    protected void parseKeyBits() throws IOException, InvalidKeyException {
        this.encode();
    }
    
    static PrivateKey buildPKCS8Key(final AlgorithmId algorithmId, final byte[] array) throws IOException, InvalidKeyException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        encode(derOutputStream, algorithmId, array);
        final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(derOutputStream.toByteArray());
        try {
            return KeyFactory.getInstance(algorithmId.getName()).generatePrivate(pkcs8EncodedKeySpec);
        }
        catch (final NoSuchAlgorithmException ex) {}
        catch (final InvalidKeySpecException ex2) {}
        String property = "";
        try {
            final Provider provider = Security.getProvider("SUN");
            if (provider == null) {
                throw new InstantiationException();
            }
            property = provider.getProperty("PrivateKey.PKCS#8." + algorithmId.getName());
            if (property == null) {
                throw new InstantiationException();
            }
            Class<?> clazz = null;
            try {
                clazz = Class.forName(property);
            }
            catch (final ClassNotFoundException ex3) {
                final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                if (systemClassLoader != null) {
                    clazz = systemClassLoader.loadClass(property);
                }
            }
            Object instance = null;
            if (clazz != null) {
                instance = clazz.newInstance();
            }
            if (instance instanceof PKCS8Key) {
                final PKCS8Key pkcs8Key = (PKCS8Key)instance;
                pkcs8Key.algid = algorithmId;
                pkcs8Key.key = array;
                pkcs8Key.parseKeyBits();
                return pkcs8Key;
            }
        }
        catch (final ClassNotFoundException ex4) {}
        catch (final InstantiationException ex5) {}
        catch (final IllegalAccessException ex6) {
            throw new IOException(property + " [internal error]");
        }
        final PKCS8Key pkcs8Key2 = new PKCS8Key();
        pkcs8Key2.algid = algorithmId;
        pkcs8Key2.key = array;
        return pkcs8Key2;
    }
    
    @Override
    public String getAlgorithm() {
        return this.algid.getName();
    }
    
    public AlgorithmId getAlgorithmId() {
        return this.algid;
    }
    
    public final void encode(final DerOutputStream derOutputStream) throws IOException {
        encode(derOutputStream, this.algid, this.key);
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        byte[] encode = null;
        try {
            encode = this.encode();
        }
        catch (final InvalidKeyException ex) {}
        return encode;
    }
    
    @Override
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] encode() throws InvalidKeyException {
        if (this.encodedKey == null) {
            try {
                final DerOutputStream derOutputStream = new DerOutputStream();
                this.encode(derOutputStream);
                this.encodedKey = derOutputStream.toByteArray();
            }
            catch (final IOException ex) {
                throw new InvalidKeyException("IOException : " + ex.getMessage());
            }
        }
        return this.encodedKey.clone();
    }
    
    public void decode(final InputStream inputStream) throws InvalidKeyException {
        try {
            final DerValue derValue = new DerValue(inputStream);
            if (derValue.tag != 48) {
                throw new InvalidKeyException("invalid key format");
            }
            final BigInteger bigInteger = derValue.data.getBigInteger();
            if (!bigInteger.equals(PKCS8Key.version)) {
                throw new IOException("version mismatch: (supported: " + Debug.toHexString(PKCS8Key.version) + ", parsed: " + Debug.toHexString(bigInteger));
            }
            this.algid = AlgorithmId.parse(derValue.data.getDerValue());
            this.key = derValue.data.getOctetString();
            this.parseKeyBits();
            if (derValue.data.available() != 0) {}
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("IOException : " + ex.getMessage());
        }
    }
    
    public void decode(final byte[] array) throws InvalidKeyException {
        this.decode(new ByteArrayInputStream(array));
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PRIVATE, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException {
        try {
            this.decode(objectInputStream);
        }
        catch (final InvalidKeyException ex) {
            ex.printStackTrace();
            throw new IOException("deserialized key is invalid: " + ex.getMessage());
        }
    }
    
    static void encode(final DerOutputStream derOutputStream, final AlgorithmId algorithmId, final byte[] array) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(PKCS8Key.version);
        algorithmId.encode(derOutputStream2);
        derOutputStream2.putOctetString(array);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Key) {
            byte[] array;
            if (this.encodedKey != null) {
                array = this.encodedKey;
            }
            else {
                array = this.getEncoded();
            }
            return MessageDigest.isEqual(array, ((Key)o).getEncoded());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        final byte[] encoded = this.getEncoded();
        for (byte b = 1; b < encoded.length; ++b) {
            n += encoded[b] * b;
        }
        return n;
    }
    
    static {
        version = BigInteger.ZERO;
    }
}
