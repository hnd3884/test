package sun.security.jgss.krb5;

import javax.crypto.CipherInputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import sun.security.jgss.GSSToken;
import java.io.InputStream;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import java.security.GeneralSecurityException;
import sun.security.krb5.internal.crypto.Des3;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import sun.security.krb5.internal.crypto.EType;
import org.ietf.jgss.GSSException;
import sun.security.krb5.EncryptionKey;

class CipherHelper
{
    private static final int KG_USAGE_SEAL = 22;
    private static final int KG_USAGE_SIGN = 23;
    private static final int KG_USAGE_SEQ = 24;
    private static final int DES_CHECKSUM_SIZE = 8;
    private static final int DES_IV_SIZE = 8;
    private static final int AES_IV_SIZE = 16;
    private static final int HMAC_CHECKSUM_SIZE = 8;
    private static final int KG_USAGE_SIGN_MS = 15;
    private static final boolean DEBUG;
    private static final byte[] ZERO_IV;
    private static final byte[] ZERO_IV_AES;
    private int etype;
    private int sgnAlg;
    private int sealAlg;
    private byte[] keybytes;
    
    CipherHelper(final EncryptionKey encryptionKey) throws GSSException {
        this.etype = encryptionKey.getEType();
        this.keybytes = encryptionKey.getBytes();
        switch (this.etype) {
            case 1:
            case 3: {
                this.sgnAlg = 0;
                this.sealAlg = 0;
                break;
            }
            case 16: {
                this.sgnAlg = 1024;
                this.sealAlg = 512;
                break;
            }
            case 23: {
                this.sgnAlg = 4352;
                this.sealAlg = 4096;
                break;
            }
            case 17:
            case 18: {
                this.sgnAlg = -1;
                this.sealAlg = -1;
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
            }
        }
    }
    
    int getSgnAlg() {
        return this.sgnAlg;
    }
    
    int getSealAlg() {
        return this.sealAlg;
    }
    
    int getProto() {
        return EType.isNewer(this.etype) ? 1 : 0;
    }
    
    int getEType() {
        return this.etype;
    }
    
    boolean isArcFour() {
        boolean b = false;
        if (this.etype == 23) {
            b = true;
        }
        return b;
    }
    
    byte[] calculateChecksum(final int n, byte[] array, final byte[] array2, byte[] digest, int n2, int length, final int n3) throws GSSException {
        switch (n) {
            case 0: {
                try {
                    final MessageDigest instance = MessageDigest.getInstance("MD5");
                    instance.update(array);
                    instance.update(digest, n2, length);
                    if (array2 != null) {
                        instance.update(array2);
                    }
                    digest = instance.digest();
                    n2 = 0;
                    length = digest.length;
                    array = null;
                }
                catch (final NoSuchAlgorithmException ex) {
                    final GSSException ex2 = new GSSException(11, -1, "Could not get MD5 Message Digest - " + ex.getMessage());
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
            case 512: {
                return this.getDesCbcChecksum(this.keybytes, array, digest, n2, length);
            }
            case 1024: {
                byte[] array3;
                int n4;
                int n5;
                if (array == null && array2 == null) {
                    array3 = digest;
                    n4 = length;
                    n5 = n2;
                }
                else {
                    n4 = ((array != null) ? array.length : 0) + length + ((array2 != null) ? array2.length : 0);
                    array3 = new byte[n4];
                    int length2 = 0;
                    if (array != null) {
                        System.arraycopy(array, 0, array3, 0, array.length);
                        length2 = array.length;
                    }
                    System.arraycopy(digest, n2, array3, length2, length);
                    final int n6 = length2 + length;
                    if (array2 != null) {
                        System.arraycopy(array2, 0, array3, n6, array2.length);
                    }
                    n5 = 0;
                }
                try {
                    return Des3.calculateChecksum(this.keybytes, 23, array3, n5, n4);
                }
                catch (final GeneralSecurityException ex3) {
                    final GSSException ex4 = new GSSException(11, -1, "Could not use HMAC-SHA1-DES3-KD signing algorithm - " + ex3.getMessage());
                    ex4.initCause(ex3);
                    throw ex4;
                }
            }
            case 4352: {
                byte[] array4;
                int n7;
                int n8;
                if (array == null && array2 == null) {
                    array4 = digest;
                    n7 = length;
                    n8 = n2;
                }
                else {
                    n7 = ((array != null) ? array.length : 0) + length + ((array2 != null) ? array2.length : 0);
                    array4 = new byte[n7];
                    int length3 = 0;
                    if (array != null) {
                        System.arraycopy(array, 0, array4, 0, array.length);
                        length3 = array.length;
                    }
                    System.arraycopy(digest, n2, array4, length3, length);
                    final int n9 = length3 + length;
                    if (array2 != null) {
                        System.arraycopy(array2, 0, array4, n9, array2.length);
                    }
                    n8 = 0;
                }
                try {
                    int n10 = 23;
                    if (n3 == 257) {
                        n10 = 15;
                    }
                    final byte[] calculateChecksum = ArcFourHmac.calculateChecksum(this.keybytes, n10, array4, n8, n7);
                    final byte[] array5 = new byte[this.getChecksumLength()];
                    System.arraycopy(calculateChecksum, 0, array5, 0, array5.length);
                    return array5;
                }
                catch (final GeneralSecurityException ex5) {
                    final GSSException ex6 = new GSSException(11, -1, "Could not use HMAC_MD5_ARCFOUR signing algorithm - " + ex5.getMessage());
                    ex6.initCause(ex5);
                    throw ex6;
                }
                break;
            }
        }
        throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
    }
    
    byte[] calculateChecksum(final byte[] array, final byte[] array2, final int n, final int n2, final int n3) throws GSSException {
        final int n4 = ((array != null) ? array.length : 0) + n2;
        final byte[] array3 = new byte[n4];
        System.arraycopy(array2, n, array3, 0, n2);
        if (array != null) {
            System.arraycopy(array, 0, array3, n2, array.length);
        }
        switch (this.etype) {
            case 17: {
                try {
                    return Aes128.calculateChecksum(this.keybytes, n3, array3, 0, n4);
                }
                catch (final GeneralSecurityException ex) {
                    final GSSException ex2 = new GSSException(11, -1, "Could not use AES128 signing algorithm - " + ex.getMessage());
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
            case 18: {
                try {
                    return Aes256.calculateChecksum(this.keybytes, n3, array3, 0, n4);
                }
                catch (final GeneralSecurityException ex3) {
                    final GSSException ex4 = new GSSException(11, -1, "Could not use AES256 signing algorithm - " + ex3.getMessage());
                    ex4.initCause(ex3);
                    throw ex4;
                }
                break;
            }
        }
        throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
    }
    
    byte[] encryptSeq(final byte[] array, final byte[] array2, final int n, final int n2) throws GSSException {
        switch (this.sgnAlg) {
            case 0:
            case 512: {
                try {
                    return this.getInitializedDes(true, this.keybytes, array).doFinal(array2, n, n2);
                }
                catch (final GeneralSecurityException ex) {
                    final GSSException ex2 = new GSSException(11, -1, "Could not encrypt sequence number using DES - " + ex.getMessage());
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
            case 1024: {
                byte[] array3;
                if (array.length == 8) {
                    array3 = array;
                }
                else {
                    array3 = new byte[8];
                    System.arraycopy(array, 0, array3, 0, 8);
                }
                try {
                    return Des3.encryptRaw(this.keybytes, 24, array3, array2, n, n2);
                }
                catch (final Exception ex3) {
                    final GSSException ex4 = new GSSException(11, -1, "Could not encrypt sequence number using DES3-KD - " + ex3.getMessage());
                    ex4.initCause(ex3);
                    throw ex4;
                }
            }
            case 4352: {
                byte[] array4;
                if (array.length == 8) {
                    array4 = array;
                }
                else {
                    array4 = new byte[8];
                    System.arraycopy(array, 0, array4, 0, 8);
                }
                try {
                    return ArcFourHmac.encryptSeq(this.keybytes, 24, array4, array2, n, n2);
                }
                catch (final Exception ex5) {
                    final GSSException ex6 = new GSSException(11, -1, "Could not encrypt sequence number using RC4-HMAC - " + ex5.getMessage());
                    ex6.initCause(ex5);
                    throw ex6;
                }
                break;
            }
        }
        throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
    }
    
    byte[] decryptSeq(final byte[] array, final byte[] array2, final int n, final int n2) throws GSSException {
        switch (this.sgnAlg) {
            case 0:
            case 512: {
                try {
                    return this.getInitializedDes(false, this.keybytes, array).doFinal(array2, n, n2);
                }
                catch (final GeneralSecurityException ex) {
                    final GSSException ex2 = new GSSException(11, -1, "Could not decrypt sequence number using DES - " + ex.getMessage());
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
            case 1024: {
                byte[] array3;
                if (array.length == 8) {
                    array3 = array;
                }
                else {
                    array3 = new byte[8];
                    System.arraycopy(array, 0, array3, 0, 8);
                }
                try {
                    return Des3.decryptRaw(this.keybytes, 24, array3, array2, n, n2);
                }
                catch (final Exception ex3) {
                    final GSSException ex4 = new GSSException(11, -1, "Could not decrypt sequence number using DES3-KD - " + ex3.getMessage());
                    ex4.initCause(ex3);
                    throw ex4;
                }
            }
            case 4352: {
                byte[] array4;
                if (array.length == 8) {
                    array4 = array;
                }
                else {
                    array4 = new byte[8];
                    System.arraycopy(array, 0, array4, 0, 8);
                }
                try {
                    return ArcFourHmac.decryptSeq(this.keybytes, 24, array4, array2, n, n2);
                }
                catch (final Exception ex5) {
                    final GSSException ex6 = new GSSException(11, -1, "Could not decrypt sequence number using RC4-HMAC - " + ex5.getMessage());
                    ex6.initCause(ex5);
                    throw ex6;
                }
                break;
            }
        }
        throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
    }
    
    int getChecksumLength() throws GSSException {
        switch (this.etype) {
            case 1:
            case 3: {
                return 8;
            }
            case 16: {
                return Des3.getChecksumLength();
            }
            case 17: {
                return Aes128.getChecksumLength();
            }
            case 18: {
                return Aes256.getChecksumLength();
            }
            case 23: {
                return 8;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
            }
        }
    }
    
    void decryptData(final WrapToken wrapToken, final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws GSSException {
        switch (this.sealAlg) {
            case 0: {
                this.desCbcDecrypt(wrapToken, getDesEncryptionKey(this.keybytes), array, n, n2, array2, n3);
                break;
            }
            case 512: {
                this.des3KdDecrypt(wrapToken, array, n, n2, array2, n3);
                break;
            }
            case 4096: {
                this.arcFourDecrypt(wrapToken, array, n, n2, array2, n3);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
            }
        }
    }
    
    void decryptData(final WrapToken_v2 wrapToken_v2, final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) throws GSSException {
        switch (this.etype) {
            case 17: {
                this.aes128Decrypt(wrapToken_v2, array, n, n2, array2, n3, n4);
                break;
            }
            case 18: {
                this.aes256Decrypt(wrapToken_v2, array, n, n2, array2, n3, n4);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
            }
        }
    }
    
    void decryptData(final WrapToken wrapToken, final InputStream inputStream, final int n, final byte[] array, final int n2) throws GSSException, IOException {
        switch (this.sealAlg) {
            case 0: {
                this.desCbcDecrypt(wrapToken, getDesEncryptionKey(this.keybytes), inputStream, n, array, n2);
                break;
            }
            case 512: {
                final byte[] array2 = new byte[n];
                try {
                    GSSToken.readFully(inputStream, array2, 0, n);
                }
                catch (final IOException ex) {
                    final GSSException ex2 = new GSSException(10, -1, "Cannot read complete token");
                    ex2.initCause(ex);
                    throw ex2;
                }
                this.des3KdDecrypt(wrapToken, array2, 0, n, array, n2);
                break;
            }
            case 4096: {
                final byte[] array3 = new byte[n];
                try {
                    GSSToken.readFully(inputStream, array3, 0, n);
                }
                catch (final IOException ex3) {
                    final GSSException ex4 = new GSSException(10, -1, "Cannot read complete token");
                    ex4.initCause(ex3);
                    throw ex4;
                }
                this.arcFourDecrypt(wrapToken, array3, 0, n, array, n2);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
            }
        }
    }
    
    void decryptData(final WrapToken_v2 wrapToken_v2, final InputStream inputStream, final int n, final byte[] array, final int n2, final int n3) throws GSSException, IOException {
        final byte[] array2 = new byte[n];
        try {
            GSSToken.readFully(inputStream, array2, 0, n);
        }
        catch (final IOException ex) {
            final GSSException ex2 = new GSSException(10, -1, "Cannot read complete token");
            ex2.initCause(ex);
            throw ex2;
        }
        switch (this.etype) {
            case 17: {
                this.aes128Decrypt(wrapToken_v2, array2, 0, n, array, n2, n3);
                break;
            }
            case 18: {
                this.aes256Decrypt(wrapToken_v2, array2, 0, n, array, n2, n3);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
            }
        }
    }
    
    void encryptData(final WrapToken wrapToken, final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3, final OutputStream outputStream) throws GSSException, IOException {
        switch (this.sealAlg) {
            case 0: {
                final CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, this.getInitializedDes(true, getDesEncryptionKey(this.keybytes), CipherHelper.ZERO_IV));
                cipherOutputStream.write(array);
                cipherOutputStream.write(array2, n, n2);
                cipherOutputStream.write(array3);
                break;
            }
            case 512: {
                outputStream.write(this.des3KdEncrypt(array, array2, n, n2, array3));
                break;
            }
            case 4096: {
                outputStream.write(this.arcFourEncrypt(wrapToken, array, array2, n, n2, array3));
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
            }
        }
    }
    
    byte[] encryptData(final WrapToken_v2 wrapToken_v2, final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2, final int n3) throws GSSException {
        switch (this.etype) {
            case 17: {
                return this.aes128Encrypt(array, array2, array3, n, n2, n3);
            }
            case 18: {
                return this.aes256Encrypt(array, array2, array3, n, n2, n3);
            }
            default: {
                throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
            }
        }
    }
    
    void encryptData(final WrapToken wrapToken, final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3, final byte[] array4, final int n3) throws GSSException {
        switch (this.sealAlg) {
            case 0: {
                final Cipher initializedDes = this.getInitializedDes(true, getDesEncryptionKey(this.keybytes), CipherHelper.ZERO_IV);
                try {
                    final int n4 = n3 + initializedDes.update(array, 0, array.length, array4, n3);
                    initializedDes.update(array3, 0, array3.length, array4, n4 + initializedDes.update(array2, n, n2, array4, n4));
                    initializedDes.doFinal();
                    break;
                }
                catch (final GeneralSecurityException ex) {
                    final GSSException ex2 = new GSSException(11, -1, "Could not use DES Cipher - " + ex.getMessage());
                    ex2.initCause(ex);
                    throw ex2;
                }
            }
            case 512: {
                final byte[] des3KdEncrypt = this.des3KdEncrypt(array, array2, n, n2, array3);
                System.arraycopy(des3KdEncrypt, 0, array4, n3, des3KdEncrypt.length);
                break;
            }
            case 4096: {
                final byte[] arcFourEncrypt = this.arcFourEncrypt(wrapToken, array, array2, n, n2, array3);
                System.arraycopy(arcFourEncrypt, 0, array4, n3, arcFourEncrypt.length);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
            }
        }
    }
    
    int encryptData(final WrapToken_v2 wrapToken_v2, final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2, final byte[] array4, final int n3, final int n4) throws GSSException {
        byte[] array5 = null;
        switch (this.etype) {
            case 17: {
                array5 = this.aes128Encrypt(array, array2, array3, n, n2, n4);
                break;
            }
            case 18: {
                array5 = this.aes256Encrypt(array, array2, array3, n, n2, n4);
                break;
            }
            default: {
                throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
            }
        }
        System.arraycopy(array5, 0, array4, n3, array5.length);
        return array5.length;
    }
    
    private byte[] getDesCbcChecksum(final byte[] array, final byte[] array2, final byte[] array3, int n, final int n2) throws GSSException {
        final Cipher initializedDes = this.getInitializedDes(true, array, CipherHelper.ZERO_IV);
        final int blockSize = initializedDes.getBlockSize();
        final byte[] array4 = new byte[blockSize];
        int n3 = n2 / blockSize;
        final int n4 = n2 % blockSize;
        if (n4 == 0) {
            --n3;
            System.arraycopy(array3, n + n3 * blockSize, array4, 0, blockSize);
        }
        else {
            System.arraycopy(array3, n + n3 * blockSize, array4, 0, n4);
        }
        try {
            final byte[] array5 = new byte[Math.max(blockSize, (array2 == null) ? blockSize : array2.length)];
            if (array2 != null) {
                initializedDes.update(array2, 0, array2.length, array5, 0);
            }
            for (int i = 0; i < n3; ++i) {
                initializedDes.update(array3, n, blockSize, array5, 0);
                n += blockSize;
            }
            final byte[] array6 = new byte[blockSize];
            initializedDes.update(array4, 0, blockSize, array6, 0);
            initializedDes.doFinal();
            return array6;
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use DES Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private final Cipher getInitializedDes(final boolean b, final byte[] array, final byte[] array2) throws GSSException {
        try {
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(array2);
            final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "DES");
            final Cipher instance = Cipher.getInstance("DES/CBC/NoPadding");
            instance.init(b ? 1 : 2, secretKeySpec, ivParameterSpec);
            return instance;
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void desCbcDecrypt(final WrapToken wrapToken, final byte[] array, final byte[] array2, int n, int n2, final byte[] array3, int n3) throws GSSException {
        try {
            final Cipher initializedDes = this.getInitializedDes(false, array, CipherHelper.ZERO_IV);
            initializedDes.update(array2, n, 8, wrapToken.confounder);
            n += 8;
            n2 -= 8;
            final int blockSize = initializedDes.getBlockSize();
            for (int n4 = n2 / blockSize - 1, i = 0; i < n4; ++i) {
                initializedDes.update(array2, n, blockSize, array3, n3);
                n += blockSize;
                n3 += blockSize;
            }
            final byte[] array4 = new byte[blockSize];
            initializedDes.update(array2, n, blockSize, array4);
            initializedDes.doFinal();
            final byte b = array4[blockSize - 1];
            if (b < 1 || b > 8) {
                throw new GSSException(10, -1, "Invalid padding on Wrap Token");
            }
            wrapToken.padding = WrapToken.pads[b];
            System.arraycopy(array4, 0, array3, n3, blockSize - b);
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use DES cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void desCbcDecrypt(final WrapToken wrapToken, final byte[] array, final InputStream inputStream, int n, final byte[] array2, int n2) throws GSSException, IOException {
        final Cipher initializedDes = this.getInitializedDes(false, array, CipherHelper.ZERO_IV);
        final CipherInputStream cipherInputStream = new CipherInputStream(new WrapTokenInputStream(inputStream, n), initializedDes);
        n -= cipherInputStream.read(wrapToken.confounder);
        final int blockSize = initializedDes.getBlockSize();
        for (int n3 = n / blockSize - 1, i = 0; i < n3; ++i) {
            cipherInputStream.read(array2, n2, blockSize);
            n2 += blockSize;
        }
        final byte[] array3 = new byte[blockSize];
        cipherInputStream.read(array3);
        try {
            initializedDes.doFinal();
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use DES cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final byte b = array3[blockSize - 1];
        if (b < 1 || b > 8) {
            throw new GSSException(10, -1, "Invalid padding on Wrap Token");
        }
        wrapToken.padding = WrapToken.pads[b];
        System.arraycopy(array3, 0, array2, n2, blockSize - b);
    }
    
    private static byte[] getDesEncryptionKey(final byte[] array) throws GSSException {
        if (array.length > 8) {
            throw new GSSException(11, -100, "Invalid DES Key!");
        }
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)(array[i] ^ 0xF0);
        }
        return array2;
    }
    
    private void des3KdDecrypt(final WrapToken wrapToken, final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws GSSException {
        byte[] decryptRaw;
        try {
            decryptRaw = Des3.decryptRaw(this.keybytes, 22, CipherHelper.ZERO_IV, array, n, n2);
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final byte b = decryptRaw[decryptRaw.length - 1];
        if (b < 1 || b > 8) {
            throw new GSSException(10, -1, "Invalid padding on Wrap Token");
        }
        wrapToken.padding = WrapToken.pads[b];
        System.arraycopy(decryptRaw, 8, array2, n3, decryptRaw.length - 8 - b);
        System.arraycopy(decryptRaw, 0, wrapToken.confounder, 0, 8);
    }
    
    private byte[] des3KdEncrypt(final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) throws GSSException {
        final byte[] array4 = new byte[array.length + n2 + array3.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        System.arraycopy(array2, n, array4, array.length, n2);
        System.arraycopy(array3, 0, array4, array.length + n2, array3.length);
        try {
            return Des3.encryptRaw(this.keybytes, 22, CipherHelper.ZERO_IV, array4, 0, array4.length);
        }
        catch (final Exception ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void arcFourDecrypt(final WrapToken wrapToken, final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws GSSException {
        final byte[] decryptSeq = this.decryptSeq(wrapToken.getChecksum(), wrapToken.getEncSeqNumber(), 0, 8);
        byte[] decryptRaw;
        try {
            decryptRaw = ArcFourHmac.decryptRaw(this.keybytes, 22, CipherHelper.ZERO_IV, array, n, n2, decryptSeq);
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use ArcFour Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final byte b = decryptRaw[decryptRaw.length - 1];
        if (b < 1) {
            throw new GSSException(10, -1, "Invalid padding on Wrap Token");
        }
        wrapToken.padding = WrapToken.pads[b];
        System.arraycopy(decryptRaw, 8, array2, n3, decryptRaw.length - 8 - b);
        System.arraycopy(decryptRaw, 0, wrapToken.confounder, 0, 8);
    }
    
    private byte[] arcFourEncrypt(final WrapToken wrapToken, final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) throws GSSException {
        final byte[] array4 = new byte[array.length + n2 + array3.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        System.arraycopy(array2, n, array4, array.length, n2);
        System.arraycopy(array3, 0, array4, array.length + n2, array3.length);
        final byte[] array5 = new byte[4];
        GSSToken.writeBigEndian(wrapToken.getSequenceNumber(), array5);
        try {
            return ArcFourHmac.encryptRaw(this.keybytes, 22, array5, array4, 0, array4.length);
        }
        catch (final Exception ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use ArcFour Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private byte[] aes128Encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2, final int n3) throws GSSException {
        final byte[] array4 = new byte[array.length + n2 + array2.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        System.arraycopy(array3, n, array4, array.length, n2);
        System.arraycopy(array2, 0, array4, array.length + n2, array2.length);
        try {
            return Aes128.encryptRaw(this.keybytes, n3, CipherHelper.ZERO_IV_AES, array4, 0, array4.length);
        }
        catch (final Exception ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use AES128 Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void aes128Decrypt(final WrapToken_v2 wrapToken_v2, final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) throws GSSException {
        byte[] decryptRaw;
        try {
            decryptRaw = Aes128.decryptRaw(this.keybytes, n4, CipherHelper.ZERO_IV_AES, array, n, n2);
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use AES128 Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        System.arraycopy(decryptRaw, 16, array2, n3, decryptRaw.length - 16 - 16);
    }
    
    private byte[] aes256Encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2, final int n3) throws GSSException {
        final byte[] array4 = new byte[array.length + n2 + array2.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        System.arraycopy(array3, n, array4, array.length, n2);
        System.arraycopy(array2, 0, array4, array.length + n2, array2.length);
        try {
            return Aes256.encryptRaw(this.keybytes, n3, CipherHelper.ZERO_IV_AES, array4, 0, array4.length);
        }
        catch (final Exception ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use AES256 Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void aes256Decrypt(final WrapToken_v2 wrapToken_v2, final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) throws GSSException {
        byte[] decryptRaw;
        try {
            decryptRaw = Aes256.decryptRaw(this.keybytes, n4, CipherHelper.ZERO_IV_AES, array, n, n2);
        }
        catch (final GeneralSecurityException ex) {
            final GSSException ex2 = new GSSException(11, -1, "Could not use AES128 Cipher - " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        System.arraycopy(decryptRaw, 16, array2, n3, decryptRaw.length - 16 - 16);
    }
    
    static {
        DEBUG = Krb5Util.DEBUG;
        ZERO_IV = new byte[8];
        ZERO_IV_AES = new byte[16];
    }
    
    class WrapTokenInputStream extends InputStream
    {
        private InputStream is;
        private int length;
        private int remaining;
        private int temp;
        
        public WrapTokenInputStream(final InputStream is, final int n) {
            this.is = is;
            this.length = n;
            this.remaining = n;
        }
        
        @Override
        public final int read() throws IOException {
            if (this.remaining == 0) {
                return -1;
            }
            this.temp = this.is.read();
            if (this.temp != -1) {
                this.remaining -= this.temp;
            }
            return this.temp;
        }
        
        @Override
        public final int read(final byte[] array) throws IOException {
            if (this.remaining == 0) {
                return -1;
            }
            this.temp = Math.min(this.remaining, array.length);
            this.temp = this.is.read(array, 0, this.temp);
            if (this.temp != -1) {
                this.remaining -= this.temp;
            }
            return this.temp;
        }
        
        @Override
        public final int read(final byte[] array, final int n, final int n2) throws IOException {
            if (this.remaining == 0) {
                return -1;
            }
            this.temp = Math.min(this.remaining, n2);
            this.temp = this.is.read(array, n, this.temp);
            if (this.temp != -1) {
                this.remaining -= this.temp;
            }
            return this.temp;
        }
        
        @Override
        public final long skip(final long n) throws IOException {
            if (this.remaining == 0) {
                return 0L;
            }
            this.temp = (int)Math.min(this.remaining, n);
            this.temp = (int)this.is.skip(this.temp);
            this.remaining -= this.temp;
            return this.temp;
        }
        
        @Override
        public final int available() throws IOException {
            return Math.min(this.remaining, this.is.available());
        }
        
        @Override
        public final void close() throws IOException {
            this.remaining = 0;
        }
    }
}
