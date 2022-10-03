package sun.security.x509;

import java.util.Arrays;
import java.security.Key;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import sun.misc.HexDumpEncoder;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.InvalidKeyException;
import sun.security.util.BitArray;
import java.security.PublicKey;

public class X509Key implements PublicKey
{
    private static final long serialVersionUID = -5359250853002055002L;
    protected AlgorithmId algid;
    @Deprecated
    protected byte[] key;
    @Deprecated
    private int unusedBits;
    private BitArray bitStringKey;
    protected byte[] encodedKey;
    
    public X509Key() {
        this.key = null;
        this.unusedBits = 0;
        this.bitStringKey = null;
    }
    
    private X509Key(final AlgorithmId algid, final BitArray key) throws InvalidKeyException {
        this.key = null;
        this.unusedBits = 0;
        this.bitStringKey = null;
        this.algid = algid;
        this.setKey(key);
        this.encode();
    }
    
    protected void setKey(final BitArray bitArray) {
        this.bitStringKey = (BitArray)bitArray.clone();
        this.key = bitArray.toByteArray();
        final int n = bitArray.length() % 8;
        this.unusedBits = ((n == 0) ? 0 : (8 - n));
    }
    
    protected BitArray getKey() {
        this.bitStringKey = new BitArray(this.key.length * 8 - this.unusedBits, this.key);
        return (BitArray)this.bitStringKey.clone();
    }
    
    public static PublicKey parse(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("corrupt subject key");
        }
        final AlgorithmId parse = AlgorithmId.parse(derValue.data.getDerValue());
        PublicKey buildX509Key;
        try {
            buildX509Key = buildX509Key(parse, derValue.data.getUnalignedBitString());
        }
        catch (final InvalidKeyException ex) {
            throw new IOException("subject key, " + ex.getMessage(), ex);
        }
        if (derValue.data.available() != 0) {
            throw new IOException("excess subject key");
        }
        return buildX509Key;
    }
    
    protected void parseKeyBits() throws IOException, InvalidKeyException {
        this.encode();
    }
    
    static PublicKey buildX509Key(final AlgorithmId algid, final BitArray key) throws IOException, InvalidKeyException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        encode(derOutputStream, algid, key);
        final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(derOutputStream.toByteArray());
        try {
            return KeyFactory.getInstance(algid.getName()).generatePublic(x509EncodedKeySpec);
        }
        catch (final NoSuchAlgorithmException ex) {
            String property = "";
            try {
                final Provider provider = Security.getProvider("SUN");
                if (provider == null) {
                    throw new InstantiationException();
                }
                property = provider.getProperty("PublicKey.X.509." + algid.getName());
                if (property == null) {
                    throw new InstantiationException();
                }
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(property);
                }
                catch (final ClassNotFoundException ex2) {
                    final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                    if (systemClassLoader != null) {
                        clazz = systemClassLoader.loadClass(property);
                    }
                }
                Object instance = null;
                if (clazz != null) {
                    instance = clazz.newInstance();
                }
                if (instance instanceof X509Key) {
                    final X509Key x509Key = (X509Key)instance;
                    x509Key.algid = algid;
                    x509Key.setKey(key);
                    x509Key.parseKeyBits();
                    return x509Key;
                }
            }
            catch (final ClassNotFoundException ex3) {}
            catch (final InstantiationException ex4) {}
            catch (final IllegalAccessException ex5) {
                throw new IOException(property + " [internal error]");
            }
            return new X509Key(algid, key);
        }
        catch (final InvalidKeySpecException ex6) {}
    }
    
    @Override
    public String getAlgorithm() {
        return this.algid.getName();
    }
    
    public AlgorithmId getAlgorithmId() {
        return this.algid;
    }
    
    public final void encode(final DerOutputStream derOutputStream) throws IOException {
        encode(derOutputStream, this.algid, this.getKey());
    }
    
    @Override
    public byte[] getEncoded() {
        try {
            return this.getEncodedInternal().clone();
        }
        catch (final InvalidKeyException ex) {
            return null;
        }
    }
    
    public byte[] getEncodedInternal() throws InvalidKeyException {
        byte[] encodedKey = this.encodedKey;
        if (encodedKey == null) {
            try {
                final DerOutputStream derOutputStream = new DerOutputStream();
                this.encode(derOutputStream);
                encodedKey = derOutputStream.toByteArray();
            }
            catch (final IOException ex) {
                throw new InvalidKeyException("IOException : " + ex.getMessage());
            }
            this.encodedKey = encodedKey;
        }
        return encodedKey;
    }
    
    @Override
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] encode() throws InvalidKeyException {
        return this.getEncodedInternal().clone();
    }
    
    @Override
    public String toString() {
        return "algorithm = " + this.algid.toString() + ", unparsed keybits = \n" + new HexDumpEncoder().encodeBuffer(this.key);
    }
    
    public void decode(final InputStream inputStream) throws InvalidKeyException {
        try {
            final DerValue derValue = new DerValue(inputStream);
            if (derValue.tag != 48) {
                throw new InvalidKeyException("invalid key format");
            }
            this.algid = AlgorithmId.parse(derValue.data.getDerValue());
            this.setKey(derValue.data.getUnalignedBitString());
            this.parseKeyBits();
            if (derValue.data.available() != 0) {
                throw new InvalidKeyException("excess key data");
            }
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("IOException: " + ex.getMessage());
        }
    }
    
    public void decode(final byte[] array) throws InvalidKeyException {
        this.decode(new ByteArrayInputStream(array));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.write(this.getEncoded());
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
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        try {
            final byte[] encodedInternal = this.getEncodedInternal();
            byte[] array;
            if (o instanceof X509Key) {
                array = ((X509Key)o).getEncodedInternal();
            }
            else {
                array = ((Key)o).getEncoded();
            }
            return Arrays.equals(encodedInternal, array);
        }
        catch (final InvalidKeyException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        try {
            final byte[] encodedInternal = this.getEncodedInternal();
            int length = encodedInternal.length;
            for (int i = 0; i < encodedInternal.length; ++i) {
                length += (encodedInternal[i] & 0xFF) * 37;
            }
            return length;
        }
        catch (final InvalidKeyException ex) {
            return 0;
        }
    }
    
    static void encode(final DerOutputStream derOutputStream, final AlgorithmId algorithmId, final BitArray bitArray) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        algorithmId.encode(derOutputStream2);
        derOutputStream2.putUnalignedBitString(bitArray);
        derOutputStream.write((byte)48, derOutputStream2);
    }
}
