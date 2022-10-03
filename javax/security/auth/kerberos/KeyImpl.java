package javax.security.auth.kerberos;

import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;
import java.io.ObjectInputStream;
import sun.security.krb5.Asn1Exception;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.security.auth.DestroyFailedException;
import java.util.Arrays;
import sun.security.krb5.KrbException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import java.io.Serializable;
import javax.security.auth.Destroyable;
import javax.crypto.SecretKey;

class KeyImpl implements SecretKey, Destroyable, Serializable
{
    private static final long serialVersionUID = -7889313790214321193L;
    private transient byte[] keyBytes;
    private transient int keyType;
    private transient volatile boolean destroyed;
    
    public KeyImpl(final byte[] array, final int keyType) {
        this.destroyed = false;
        this.keyBytes = array.clone();
        this.keyType = keyType;
    }
    
    public KeyImpl(final KerberosPrincipal kerberosPrincipal, final char[] array, final String s) {
        this.destroyed = false;
        try {
            final EncryptionKey encryptionKey = new EncryptionKey(array, new PrincipalName(kerberosPrincipal.getName()).getSalt(), s);
            this.keyBytes = encryptionKey.getBytes();
            this.keyType = encryptionKey.getEType();
        }
        catch (final KrbException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    public final int getKeyType() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.keyType;
    }
    
    @Override
    public final String getAlgorithm() {
        return this.getAlgorithmName(this.keyType);
    }
    
    private String getAlgorithmName(final int n) {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        switch (n) {
            case 1:
            case 3: {
                return "DES";
            }
            case 16: {
                return "DESede";
            }
            case 23: {
                return "ArcFourHmac";
            }
            case 17: {
                return "AES128";
            }
            case 18: {
                return "AES256";
            }
            case 0: {
                return "NULL";
            }
            default: {
                throw new IllegalArgumentException("Unsupported encryption type: " + n);
            }
        }
    }
    
    @Override
    public final String getFormat() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return "RAW";
    }
    
    @Override
    public final byte[] getEncoded() {
        if (this.destroyed) {
            throw new IllegalStateException("This key is no longer valid");
        }
        return this.keyBytes.clone();
    }
    
    @Override
    public void destroy() throws DestroyFailedException {
        if (!this.destroyed) {
            this.destroyed = true;
            Arrays.fill(this.keyBytes, (byte)0);
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.destroyed) {
            throw new IOException("This key is no longer valid");
        }
        try {
            objectOutputStream.writeObject(new EncryptionKey(this.keyType, this.keyBytes).asn1Encode());
        }
        catch (final Asn1Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        try {
            final EncryptionKey encryptionKey = new EncryptionKey(new DerValue((byte[])objectInputStream.readObject()));
            this.keyType = encryptionKey.getEType();
            this.keyBytes = encryptionKey.getBytes();
        }
        catch (final Asn1Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public String toString() {
        final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        return "EncryptionKey: keyType=" + this.keyType + " keyBytes (hex dump)=" + ((this.keyBytes == null || this.keyBytes.length == 0) ? " Empty Key" : ('\n' + hexDumpEncoder.encodeBuffer(this.keyBytes) + '\n'));
    }
    
    @Override
    public int hashCode() {
        final int n = 17;
        if (this.isDestroyed()) {
            return n;
        }
        return 37 * (37 * n + Arrays.hashCode(this.keyBytes)) + this.keyType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KeyImpl)) {
            return false;
        }
        final KeyImpl keyImpl = (KeyImpl)o;
        return !this.isDestroyed() && !keyImpl.isDestroyed() && this.keyType == keyImpl.getKeyType() && Arrays.equals(this.keyBytes, keyImpl.getEncoded());
    }
}
