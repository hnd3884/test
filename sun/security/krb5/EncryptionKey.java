package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DESKeySpec;
import java.security.GeneralSecurityException;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des3;
import sun.security.krb5.internal.crypto.Des;
import java.util.Arrays;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.ktab.KeyTab;

public class EncryptionKey implements Cloneable
{
    public static final EncryptionKey NULL_KEY;
    private int keyType;
    private byte[] keyValue;
    private Integer kvno;
    private static final boolean DEBUG;
    
    public synchronized int getEType() {
        return this.keyType;
    }
    
    public final Integer getKeyVersionNumber() {
        return this.kvno;
    }
    
    public final byte[] getBytes() {
        return this.keyValue;
    }
    
    public synchronized Object clone() {
        return new EncryptionKey(this.keyValue, this.keyType, this.kvno);
    }
    
    public static EncryptionKey[] acquireSecretKeys(final PrincipalName principalName, final String s) {
        if (principalName == null) {
            throw new IllegalArgumentException("Cannot have null pricipal name to look in keytab.");
        }
        return KeyTab.getInstance(s).readServiceKeys(principalName);
    }
    
    public static EncryptionKey acquireSecretKey(final PrincipalName principalName, final char[] array, final int n, final PAData.SaltAndParams saltAndParams) throws KrbException {
        String salt;
        byte[] params;
        if (saltAndParams != null) {
            salt = ((saltAndParams.salt != null) ? saltAndParams.salt : principalName.getSalt());
            params = saltAndParams.params;
        }
        else {
            salt = principalName.getSalt();
            params = null;
        }
        return acquireSecretKey(array, salt, n, params);
    }
    
    public static EncryptionKey acquireSecretKey(final char[] array, final String s, final int n, final byte[] array2) throws KrbException {
        return new EncryptionKey(stringToKey(array, s, array2, n), n, null);
    }
    
    public static EncryptionKey[] acquireSecretKeys(final char[] array, final String s) throws KrbException {
        final int[] defaults = EType.getDefaults("default_tkt_enctypes");
        final EncryptionKey[] array2 = new EncryptionKey[defaults.length];
        for (int i = 0; i < defaults.length; ++i) {
            if (EType.isSupported(defaults[i])) {
                array2[i] = new EncryptionKey(stringToKey(array, s, null, defaults[i]), defaults[i], null);
            }
            else if (EncryptionKey.DEBUG) {
                System.out.println("Encryption Type " + EType.toString(defaults[i]) + " is not supported/enabled");
            }
        }
        return array2;
    }
    
    public EncryptionKey(final byte[] array, final int keyType, final Integer kvno) {
        if (array != null) {
            System.arraycopy(array, 0, this.keyValue = new byte[array.length], 0, array.length);
            this.keyType = keyType;
            this.kvno = kvno;
            return;
        }
        throw new IllegalArgumentException("EncryptionKey: Key bytes cannot be null!");
    }
    
    public EncryptionKey(final int n, final byte[] array) {
        this(array, n, null);
    }
    
    private static byte[] stringToKey(final char[] array, final String s, final byte[] array2, final int n) throws KrbCryptoException {
        final char[] charArray = s.toCharArray();
        final char[] array3 = new char[array.length + charArray.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(charArray, 0, array3, array.length, charArray.length);
        Arrays.fill(charArray, '0');
        try {
            switch (n) {
                case 1:
                case 3: {
                    return Des.string_to_key_bytes(array3);
                }
                case 16: {
                    return Des3.stringToKey(array3);
                }
                case 23: {
                    return ArcFourHmac.stringToKey(array);
                }
                case 17: {
                    return Aes128.stringToKey(array, s, array2);
                }
                case 18: {
                    return Aes256.stringToKey(array, s, array2);
                }
                default: {
                    throw new IllegalArgumentException("encryption type " + EType.toString(n) + " not supported");
                }
            }
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        finally {
            Arrays.fill(array3, '0');
        }
    }
    
    public EncryptionKey(final char[] array, final String s, final String s2) throws KrbCryptoException {
        if (s2 == null || s2.equalsIgnoreCase("DES")) {
            this.keyType = 3;
        }
        else if (s2.equalsIgnoreCase("DESede")) {
            this.keyType = 16;
        }
        else if (s2.equalsIgnoreCase("AES128")) {
            this.keyType = 17;
        }
        else if (s2.equalsIgnoreCase("ArcFourHmac")) {
            this.keyType = 23;
        }
        else {
            if (!s2.equalsIgnoreCase("AES256")) {
                throw new IllegalArgumentException("Algorithm " + s2 + " not supported");
            }
            this.keyType = 18;
            if (!EType.isSupported(this.keyType)) {
                throw new IllegalArgumentException("Algorithm " + s2 + " not enabled");
            }
        }
        this.keyValue = stringToKey(array, s, null, this.keyType);
        this.kvno = null;
    }
    
    public EncryptionKey(final EncryptionKey encryptionKey) throws KrbCryptoException {
        this.keyValue = Confounder.bytes(encryptionKey.keyValue.length);
        for (int i = 0; i < this.keyValue.length; ++i) {
            final byte[] keyValue = this.keyValue;
            final int n = i;
            keyValue[n] ^= encryptionKey.keyValue[i];
        }
        this.keyType = encryptionKey.keyType;
        try {
            if (this.keyType == 3 || this.keyType == 1) {
                if (!DESKeySpec.isParityAdjusted(this.keyValue, 0)) {
                    this.keyValue = Des.set_parity(this.keyValue);
                }
                if (DESKeySpec.isWeak(this.keyValue, 0)) {
                    this.keyValue[7] ^= (byte)240;
                }
            }
            if (this.keyType == 16) {
                if (!DESedeKeySpec.isParityAdjusted(this.keyValue, 0)) {
                    this.keyValue = Des3.parityFix(this.keyValue);
                }
                final byte[] array = new byte[8];
                for (int j = 0; j < this.keyValue.length; j += 8) {
                    System.arraycopy(this.keyValue, j, array, 0, 8);
                    if (DESKeySpec.isWeak(array, 0)) {
                        this.keyValue[j + 7] ^= (byte)240;
                    }
                }
            }
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    public EncryptionKey(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.keyType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.keyValue = derValue3.getData().getOctetString();
        if (derValue3.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public synchronized byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.keyType);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(this.keyValue);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public synchronized void destroy() {
        if (this.keyValue != null) {
            for (int i = 0; i < this.keyValue.length; ++i) {
                this.keyValue[i] = 0;
            }
        }
    }
    
    public static EncryptionKey parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new EncryptionKey(derValue.getData().getDerValue());
    }
    
    public synchronized void writeKey(final CCacheOutputStream cCacheOutputStream) throws IOException {
        cCacheOutputStream.write16(this.keyType);
        cCacheOutputStream.write16(this.keyType);
        cCacheOutputStream.write32(this.keyValue.length);
        for (int i = 0; i < this.keyValue.length; ++i) {
            cCacheOutputStream.write8(this.keyValue[i]);
        }
    }
    
    @Override
    public String toString() {
        return new String("EncryptionKey: keyType=" + this.keyType + " kvno=" + this.kvno + " keyValue (hex dump)=" + ((this.keyValue == null || this.keyValue.length == 0) ? " Empty Key" : ('\n' + Krb5.hexDumper.encodeBuffer(this.keyValue) + '\n')));
    }
    
    public static EncryptionKey findKey(final int n, final EncryptionKey[] array) throws KrbException {
        return findKey(n, null, array);
    }
    
    private static boolean versionMatches(final Integer n, final Integer n2) {
        return n == null || n == 0 || n2 == null || n2 == 0 || n.equals(n2);
    }
    
    public static EncryptionKey findKey(final int n, final Integer n2, final EncryptionKey[] array) throws KrbException {
        if (!EType.isSupported(n)) {
            throw new KrbException("Encryption type " + EType.toString(n) + " is not supported/enabled");
        }
        boolean b = false;
        int n3 = 0;
        EncryptionKey encryptionKey = null;
        for (int i = 0; i < array.length; ++i) {
            final int eType = array[i].getEType();
            if (EType.isSupported(eType)) {
                final Integer keyVersionNumber = array[i].getKeyVersionNumber();
                if (n == eType) {
                    b = true;
                    if (versionMatches(n2, keyVersionNumber)) {
                        return array[i];
                    }
                    if (keyVersionNumber > n3) {
                        encryptionKey = array[i];
                        n3 = keyVersionNumber;
                    }
                }
            }
        }
        if (n == 1 || n == 3) {
            for (int j = 0; j < array.length; ++j) {
                final int eType2 = array[j].getEType();
                if (eType2 == 1 || eType2 == 3) {
                    final Integer keyVersionNumber2 = array[j].getKeyVersionNumber();
                    b = true;
                    if (versionMatches(n2, keyVersionNumber2)) {
                        return new EncryptionKey(n, array[j].getBytes());
                    }
                    if (keyVersionNumber2 > n3) {
                        encryptionKey = new EncryptionKey(n, array[j].getBytes());
                        n3 = keyVersionNumber2;
                    }
                }
            }
        }
        if (b) {
            return encryptionKey;
        }
        return null;
    }
    
    static {
        NULL_KEY = new EncryptionKey(new byte[0], 0, null);
        DEBUG = Krb5.DEBUG;
    }
}
