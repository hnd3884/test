package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.crypto.CksumType;
import sun.security.krb5.internal.crypto.EType;

public class Checksum
{
    private int cksumType;
    private byte[] checksum;
    public static final int CKSUMTYPE_NULL = 0;
    public static final int CKSUMTYPE_CRC32 = 1;
    public static final int CKSUMTYPE_RSA_MD4 = 2;
    public static final int CKSUMTYPE_RSA_MD4_DES = 3;
    public static final int CKSUMTYPE_DES_MAC = 4;
    public static final int CKSUMTYPE_DES_MAC_K = 5;
    public static final int CKSUMTYPE_RSA_MD4_DES_K = 6;
    public static final int CKSUMTYPE_RSA_MD5 = 7;
    public static final int CKSUMTYPE_RSA_MD5_DES = 8;
    public static final int CKSUMTYPE_HMAC_SHA1_DES3_KD = 12;
    public static final int CKSUMTYPE_HMAC_SHA1_96_AES128 = 15;
    public static final int CKSUMTYPE_HMAC_SHA1_96_AES256 = 16;
    public static final int CKSUMTYPE_HMAC_MD5_ARCFOUR = -138;
    static int CKSUMTYPE_DEFAULT;
    static int SAFECKSUMTYPE_DEFAULT;
    private static boolean DEBUG;
    
    public static void initStatic() {
        Config instance = null;
        try {
            instance = Config.getInstance();
            final String value = instance.get("libdefaults", "default_checksum");
            if (value != null) {
                Checksum.CKSUMTYPE_DEFAULT = Config.getType(value);
            }
            else {
                Checksum.CKSUMTYPE_DEFAULT = -1;
            }
        }
        catch (final Exception ex) {
            if (Checksum.DEBUG) {
                System.out.println("Exception in getting default checksum value from the configuration. No default checksum set.");
                ex.printStackTrace();
            }
            Checksum.CKSUMTYPE_DEFAULT = -1;
        }
        try {
            final String value2 = instance.get("libdefaults", "safe_checksum_type");
            if (value2 != null) {
                Checksum.SAFECKSUMTYPE_DEFAULT = Config.getType(value2);
            }
            else {
                Checksum.SAFECKSUMTYPE_DEFAULT = -1;
            }
        }
        catch (final Exception ex2) {
            if (Checksum.DEBUG) {
                System.out.println("Exception in getting safe default checksum value from the configuration Setting.  No safe default checksum set.");
                ex2.printStackTrace();
            }
            Checksum.SAFECKSUMTYPE_DEFAULT = -1;
        }
    }
    
    public Checksum(final byte[] checksum, final int cksumType) {
        this.cksumType = cksumType;
        this.checksum = checksum;
    }
    
    public Checksum(final int cksumType, final byte[] array, final EncryptionKey encryptionKey, final int n) throws KdcErrException, KrbApErrException, KrbCryptoException {
        if (cksumType == -1) {
            this.cksumType = EType.getInstance(encryptionKey.getEType()).checksumType();
        }
        else {
            this.cksumType = cksumType;
        }
        this.checksum = CksumType.getInstance(this.cksumType).calculateChecksum(array, array.length, encryptionKey.getBytes(), n);
    }
    
    public boolean verifyKeyedChecksum(final byte[] array, final EncryptionKey encryptionKey, final int n) throws KdcErrException, KrbApErrException, KrbCryptoException {
        final CksumType instance = CksumType.getInstance(this.cksumType);
        if (!instance.isKeyed()) {
            throw new KrbApErrException(50);
        }
        return instance.verifyChecksum(array, array.length, encryptionKey.getBytes(), this.checksum, n);
    }
    
    public boolean verifyAnyChecksum(final byte[] array, final EncryptionKey encryptionKey, final int n) throws KdcErrException, KrbCryptoException {
        return CksumType.getInstance(this.cksumType).verifyChecksum(array, array.length, encryptionKey.getBytes(), this.checksum, n);
    }
    
    boolean isEqual(final Checksum checksum) throws KdcErrException {
        return this.cksumType == checksum.cksumType && CksumType.isChecksumEqual(this.checksum, checksum.checksum);
    }
    
    public Checksum(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.cksumType = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.checksum = derValue3.getData().getOctetString();
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.cksumType));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putOctetString(this.checksum);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public static Checksum parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new Checksum(derValue.getData().getDerValue());
    }
    
    public final byte[] getBytes() {
        return this.checksum;
    }
    
    public final int getType() {
        return this.cksumType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Checksum)) {
            return false;
        }
        try {
            return this.isEqual((Checksum)o);
        }
        catch (final KdcErrException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int n = 37 * 17 + this.cksumType;
        if (this.checksum != null) {
            n = 37 * n + Arrays.hashCode(this.checksum);
        }
        return n;
    }
    
    static {
        Checksum.DEBUG = Krb5.DEBUG;
        initStatic();
    }
}
