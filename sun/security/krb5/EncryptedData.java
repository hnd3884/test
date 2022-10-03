package sun.security.krb5;

import sun.security.util.DerInputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.crypto.EType;

public class EncryptedData implements Cloneable
{
    int eType;
    Integer kvno;
    byte[] cipher;
    byte[] plain;
    public static final int ETYPE_NULL = 0;
    public static final int ETYPE_DES_CBC_CRC = 1;
    public static final int ETYPE_DES_CBC_MD4 = 2;
    public static final int ETYPE_DES_CBC_MD5 = 3;
    public static final int ETYPE_ARCFOUR_HMAC = 23;
    public static final int ETYPE_ARCFOUR_HMAC_EXP = 24;
    public static final int ETYPE_DES3_CBC_HMAC_SHA1_KD = 16;
    public static final int ETYPE_AES128_CTS_HMAC_SHA1_96 = 17;
    public static final int ETYPE_AES256_CTS_HMAC_SHA1_96 = 18;
    
    private EncryptedData() {
    }
    
    public Object clone() {
        final EncryptedData encryptedData = new EncryptedData();
        encryptedData.eType = this.eType;
        if (this.kvno != null) {
            encryptedData.kvno = new Integer(this.kvno);
        }
        if (this.cipher != null) {
            encryptedData.cipher = new byte[this.cipher.length];
            System.arraycopy(this.cipher, 0, encryptedData.cipher, 0, this.cipher.length);
        }
        return encryptedData;
    }
    
    public EncryptedData(final int eType, final Integer kvno, final byte[] cipher) {
        this.eType = eType;
        this.kvno = kvno;
        this.cipher = cipher;
    }
    
    public EncryptedData(final EncryptionKey encryptionKey, final byte[] array, final int n) throws KdcErrException, KrbCryptoException {
        this.cipher = EType.getInstance(encryptionKey.getEType()).encrypt(array, encryptionKey.getBytes(), n);
        this.eType = encryptionKey.getEType();
        this.kvno = encryptionKey.getKeyVersionNumber();
    }
    
    public byte[] decrypt(final EncryptionKey encryptionKey, final int n) throws KdcErrException, KrbApErrException, KrbCryptoException {
        if (this.eType != encryptionKey.getEType()) {
            throw new KrbCryptoException("EncryptedData is encrypted using keytype " + EType.toString(this.eType) + " but decryption key is of type " + EType.toString(encryptionKey.getEType()));
        }
        final EType instance = EType.getInstance(this.eType);
        this.plain = instance.decrypt(this.cipher, encryptionKey.getBytes(), n);
        return instance.decryptedData(this.plain);
    }
    
    private byte[] decryptedData() throws KdcErrException {
        if (this.plain != null) {
            return EType.getInstance(this.eType).decryptedData(this.plain);
        }
        return null;
    }
    
    private EncryptedData(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.eType = derValue2.getData().getBigInteger().intValue();
        if ((derValue.getData().peekByte() & 0x1F) == 0x1) {
            this.kvno = new Integer(derValue.getData().getDerValue().getData().getBigInteger().intValue());
        }
        else {
            this.kvno = null;
        }
        final DerValue derValue3 = derValue.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x2) {
            throw new Asn1Exception(906);
        }
        this.cipher = derValue3.getData().getOctetString();
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.eType));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        DerOutputStream derOutputStream3 = new DerOutputStream();
        if (this.kvno != null) {
            derOutputStream3.putInteger(BigInteger.valueOf(this.kvno));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
            derOutputStream3 = new DerOutputStream();
        }
        derOutputStream3.putOctetString(this.cipher);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        return derOutputStream4.toByteArray();
    }
    
    public static EncryptedData parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new EncryptedData(derValue.getData().getDerValue());
    }
    
    public byte[] reset(final byte[] array) {
        Object o = null;
        if ((array[1] & 0xFF) < 128) {
            o = new byte[array[1] + 2];
            System.arraycopy(array, 0, o, 0, array[1] + 2);
        }
        else if ((array[1] & 0xFF) > 128) {
            final int n = array[1] & 0x7F;
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                n2 |= (array[i + 2] & 0xFF) << 8 * (n - i - 1);
            }
            o = new byte[n2 + n + 2];
            System.arraycopy(array, 0, o, 0, n2 + n + 2);
        }
        return (byte[])o;
    }
    
    public int getEType() {
        return this.eType;
    }
    
    public Integer getKeyVersionNumber() {
        return this.kvno;
    }
    
    public byte[] getBytes() {
        return this.cipher;
    }
}
