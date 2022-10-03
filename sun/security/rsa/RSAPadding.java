package sun.security.rsa;

import java.util.Collections;
import java.util.HashMap;
import sun.security.jca.JCAUtil;
import javax.crypto.BadPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.OAEPParameterSpec;
import java.util.Map;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class RSAPadding
{
    public static final int PAD_BLOCKTYPE_1 = 1;
    public static final int PAD_BLOCKTYPE_2 = 2;
    public static final int PAD_NONE = 3;
    public static final int PAD_OAEP_MGF1 = 4;
    private final int type;
    private final int paddedSize;
    private SecureRandom random;
    private final int maxDataSize;
    private MessageDigest md;
    private MGF1 mgf;
    private byte[] lHash;
    private static final Map<String, byte[]> emptyHashes;
    
    public static RSAPadding getInstance(final int n, final int n2) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new RSAPadding(n, n2, null, null);
    }
    
    public static RSAPadding getInstance(final int n, final int n2, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new RSAPadding(n, n2, secureRandom, null);
    }
    
    public static RSAPadding getInstance(final int n, final int n2, final SecureRandom secureRandom, final OAEPParameterSpec oaepParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        return new RSAPadding(n, n2, secureRandom, oaepParameterSpec);
    }
    
    private RSAPadding(final int type, final int n, final SecureRandom random, final OAEPParameterSpec oaepParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.type = type;
        this.paddedSize = n;
        this.random = random;
        if (n < 64) {
            throw new InvalidKeyException("Padded size must be at least 64");
        }
        switch (type) {
            case 1:
            case 2: {
                this.maxDataSize = n - 11;
                break;
            }
            case 3: {
                this.maxDataSize = n;
                break;
            }
            case 4: {
                String digestAlgorithm2;
                String digestAlgorithm = digestAlgorithm2 = "SHA-1";
                byte[] value = null;
                try {
                    if (oaepParameterSpec != null) {
                        digestAlgorithm = oaepParameterSpec.getDigestAlgorithm();
                        final String mgfAlgorithm = oaepParameterSpec.getMGFAlgorithm();
                        if (!mgfAlgorithm.equalsIgnoreCase("MGF1")) {
                            throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + mgfAlgorithm);
                        }
                        digestAlgorithm2 = ((MGF1ParameterSpec)oaepParameterSpec.getMGFParameters()).getDigestAlgorithm();
                        final PSource pSource = oaepParameterSpec.getPSource();
                        final String algorithm = pSource.getAlgorithm();
                        if (!algorithm.equalsIgnoreCase("PSpecified")) {
                            throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + algorithm);
                        }
                        value = ((PSource.PSpecified)pSource).getValue();
                    }
                    this.md = MessageDigest.getInstance(digestAlgorithm);
                    this.mgf = new MGF1(digestAlgorithm2);
                }
                catch (final NoSuchAlgorithmException ex) {
                    throw new InvalidKeyException("Digest not available", ex);
                }
                this.lHash = getInitialHash(this.md, value);
                this.maxDataSize = n - 2 - 2 * this.lHash.length;
                if (this.maxDataSize <= 0) {
                    throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + digestAlgorithm + " and " + this.mgf.getName());
                }
                break;
            }
            default: {
                throw new InvalidKeyException("Invalid padding: " + type);
            }
        }
    }
    
    private static byte[] getInitialHash(final MessageDigest messageDigest, final byte[] array) {
        byte[] array2;
        if (array == null || array.length == 0) {
            final String algorithm = messageDigest.getAlgorithm();
            array2 = RSAPadding.emptyHashes.get(algorithm);
            if (array2 == null) {
                array2 = messageDigest.digest();
                RSAPadding.emptyHashes.put(algorithm, array2);
            }
        }
        else {
            array2 = messageDigest.digest(array);
        }
        return array2;
    }
    
    public int getMaxDataSize() {
        return this.maxDataSize;
    }
    
    public byte[] pad(final byte[] array, final int n, final int n2) throws BadPaddingException {
        return this.pad(RSACore.convert(array, n, n2));
    }
    
    public byte[] pad(final byte[] array) throws BadPaddingException {
        if (array.length > this.maxDataSize) {
            throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes but received " + array.length + " bytes.");
        }
        switch (this.type) {
            case 3: {
                return array;
            }
            case 1:
            case 2: {
                return this.padV15(array);
            }
            case 4: {
                return this.padOAEP(array);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public byte[] unpad(final byte[] array, final int n, final int n2) throws BadPaddingException {
        return this.unpad(RSACore.convert(array, n, n2));
    }
    
    public byte[] unpad(final byte[] array) throws BadPaddingException {
        if (array.length != this.paddedSize) {
            throw new BadPaddingException("Decryption error.The padded array length (" + array.length + ") is not the specified padded size (" + this.paddedSize + ")");
        }
        switch (this.type) {
            case 3: {
                return array;
            }
            case 1:
            case 2: {
                return this.unpadV15(array);
            }
            case 4: {
                return this.unpadOAEP(array);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private byte[] padV15(final byte[] array) throws BadPaddingException {
        final byte[] array2 = new byte[this.paddedSize];
        System.arraycopy(array, 0, array2, this.paddedSize - array.length, array.length);
        int n = this.paddedSize - 3 - array.length;
        int n2 = 0;
        array2[n2++] = 0;
        array2[n2++] = (byte)this.type;
        if (this.type == 1) {
            while (n-- > 0) {
                array2[n2++] = -1;
            }
        }
        else {
            if (this.random == null) {
                this.random = JCAUtil.getSecureRandom();
            }
            final byte[] array3 = new byte[64];
            int n3 = -1;
            while (n-- > 0) {
                int i;
                do {
                    if (n3 < 0) {
                        this.random.nextBytes(array3);
                        n3 = array3.length - 1;
                    }
                    i = (array3[n3--] & 0xFF);
                } while (i == 0);
                array2[n2++] = (byte)i;
            }
        }
        return array2;
    }
    
    private byte[] unpadV15(final byte[] array) throws BadPaddingException {
        int i = 0;
        boolean b = false;
        if (array[i++] != 0) {
            b = true;
        }
        if (array[i++] != this.type) {
            b = true;
        }
        int n = 0;
        while (i < array.length) {
            final int n2 = array[i++] & 0xFF;
            if (n2 == 0 && n == 0) {
                n = i;
            }
            if (i == array.length && n == 0) {
                b = true;
            }
            if (this.type == 1 && n2 != 255 && n == 0) {
                b = true;
            }
        }
        final int n3 = array.length - n;
        if (n3 > this.maxDataSize) {
            b = true;
        }
        System.arraycopy(array, 0, new byte[n], 0, n);
        final byte[] array2 = new byte[n3];
        System.arraycopy(array, n, array2, 0, n3);
        final BadPaddingException ex = new BadPaddingException("Decryption error");
        if (b) {
            throw ex;
        }
        return array2;
    }
    
    private byte[] padOAEP(final byte[] array) throws BadPaddingException {
        if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
        }
        final int length = this.lHash.length;
        final byte[] array2 = new byte[length];
        this.random.nextBytes(array2);
        final byte[] array3 = new byte[this.paddedSize];
        final int n = 1;
        final int n2 = length;
        System.arraycopy(array2, 0, array3, n, n2);
        final int n3 = length + 1;
        final int n4 = array3.length - n3;
        final int n5 = this.paddedSize - array.length;
        System.arraycopy(this.lHash, 0, array3, n3, length);
        array3[n5 - 1] = 1;
        System.arraycopy(array, 0, array3, n5, array.length);
        this.mgf.generateAndXor(array3, n, n2, n4, array3, n3);
        this.mgf.generateAndXor(array3, n3, n4, n2, array3, n);
        return array3;
    }
    
    private byte[] unpadOAEP(final byte[] array) throws BadPaddingException {
        boolean b = false;
        final int length = this.lHash.length;
        if (array[0] != 0) {
            b = true;
        }
        final int n = 1;
        final int n2 = length;
        final int n3 = length + 1;
        final int n4 = array.length - n3;
        this.mgf.generateAndXor(array, n3, n4, n2, array, n);
        this.mgf.generateAndXor(array, n, n2, n4, array, n3);
        for (int i = 0; i < length; ++i) {
            if (this.lHash[i] != array[n3 + i]) {
                b = true;
            }
        }
        final int n5 = n3 + length;
        int n6 = -1;
        for (int j = n5; j < array.length; ++j) {
            final byte b2 = array[j];
            if (n6 == -1) {
                if (b2 != 0) {
                    if (b2 == 1) {
                        n6 = j;
                    }
                    else {
                        b = true;
                    }
                }
            }
        }
        if (n6 == -1) {
            b = true;
            n6 = array.length - 1;
        }
        final int n7 = n6 + 1;
        final byte[] array2 = new byte[n7 - n5];
        System.arraycopy(array, n5, array2, 0, array2.length);
        final byte[] array3 = new byte[array.length - n7];
        System.arraycopy(array, n7, array3, 0, array3.length);
        final BadPaddingException ex = new BadPaddingException("Decryption error");
        if (b) {
            throw ex;
        }
        return array3;
    }
    
    static {
        emptyHashes = Collections.synchronizedMap(new HashMap<String, byte[]>());
    }
}
