package sun.security.mscapi;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAKey;
import sun.security.util.KeyUtil;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import java.security.KeyException;
import java.security.ProviderException;
import javax.crypto.IllegalBlockSizeException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyStoreException;
import sun.security.rsa.RSAKeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.InvalidAlgorithmParameterException;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.CipherSpi;

public final class CRSACipher extends CipherSpi
{
    private static final byte[] B0;
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int MODE_SIGN = 3;
    private static final int MODE_VERIFY = 4;
    private static final String PAD_PKCS1 = "PKCS1Padding";
    private static final int PAD_PKCS1_LENGTH = 11;
    private int mode;
    private String paddingType;
    private int paddingLength;
    private byte[] buffer;
    private int bufOfs;
    private int outputSize;
    private CKey publicKey;
    private CKey privateKey;
    private AlgorithmParameterSpec spec;
    private SecureRandom random;
    
    public CRSACipher() {
        this.paddingLength = 0;
        this.spec = null;
        this.paddingType = "PKCS1Padding";
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("Unsupported mode " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (s.equalsIgnoreCase("PKCS1Padding")) {
            this.paddingType = "PKCS1Padding";
            return;
        }
        throw new NoSuchPaddingException("Padding " + s + " not supported");
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.outputSize;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.init(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec spec, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (spec != null) {
            if (!(spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Parameters not supported");
            }
            this.spec = spec;
            this.random = random;
        }
        this.init(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.init(n, key);
    }
    
    private void init(final int n, Key importPublicKey) throws InvalidKeyException {
        boolean b = false;
        switch (n) {
            case 1:
            case 3: {
                this.paddingLength = 11;
                b = true;
                break;
            }
            case 2:
            case 4: {
                this.paddingLength = 0;
                b = false;
                break;
            }
            default: {
                throw new InvalidKeyException("Unknown mode: " + n);
            }
        }
        if (!(importPublicKey instanceof CKey)) {
            if (!(importPublicKey instanceof RSAPublicKey)) {
                throw new InvalidKeyException("Unsupported key type: " + importPublicKey);
            }
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)importPublicKey;
            final BigInteger modulus = rsaPublicKey.getModulus();
            final BigInteger publicExponent = rsaPublicKey.getPublicExponent();
            RSAKeyFactory.checkKeyLengths(modulus.bitLength() + 7 & 0xFFFFFFF8, publicExponent, -1, 16384);
            final byte[] byteArray = modulus.toByteArray();
            final byte[] byteArray2 = publicExponent.toByteArray();
            final int n2 = (byteArray[0] == 0) ? ((byteArray.length - 1) * 8) : (byteArray.length * 8);
            final byte[] generatePublicKeyBlob = CSignature.RSA.generatePublicKeyBlob(n2, byteArray, byteArray2);
            try {
                importPublicKey = CSignature.importPublicKey("RSA", generatePublicKeyBlob, n2);
            }
            catch (final KeyStoreException ex) {
                throw new InvalidKeyException(ex);
            }
        }
        if (importPublicKey instanceof PublicKey) {
            this.mode = (b ? 1 : 4);
            this.publicKey = (CKey)importPublicKey;
            this.privateKey = null;
            this.outputSize = this.publicKey.length() / 8;
        }
        else {
            if (!(importPublicKey instanceof PrivateKey)) {
                throw new InvalidKeyException("Unknown key type: " + importPublicKey);
            }
            this.mode = (b ? 3 : 2);
            this.privateKey = (CKey)importPublicKey;
            this.publicKey = null;
            this.outputSize = this.privateKey.length() / 8;
        }
        this.bufOfs = 0;
        this.buffer = new byte[this.outputSize];
    }
    
    private void update(final byte[] array, final int n, final int n2) {
        if (n2 == 0 || array == null) {
            return;
        }
        if (this.bufOfs + n2 > this.buffer.length - this.paddingLength) {
            this.bufOfs = this.buffer.length + 1;
            return;
        }
        System.arraycopy(array, n, this.buffer, this.bufOfs, n2);
        this.bufOfs += n2;
    }
    
    private byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
        if (this.bufOfs > this.buffer.length) {
            throw new IllegalBlockSizeException("Data must not be longer than " + (this.buffer.length - this.paddingLength) + " bytes");
        }
        try {
            final byte[] buffer = this.buffer;
            switch (this.mode) {
                case 3: {
                    return encryptDecrypt(buffer, this.bufOfs, this.privateKey.getHCryptKey(), true);
                }
                case 4: {
                    return encryptDecrypt(buffer, this.bufOfs, this.publicKey.getHCryptKey(), false);
                }
                case 1: {
                    return encryptDecrypt(buffer, this.bufOfs, this.publicKey.getHCryptKey(), true);
                }
                case 2: {
                    return encryptDecrypt(buffer, this.bufOfs, this.privateKey.getHCryptKey(), false);
                }
                default: {
                    throw new AssertionError((Object)"Internal error");
                }
            }
        }
        catch (final KeyException ex) {
            throw new ProviderException(ex);
        }
        finally {
            this.bufOfs = 0;
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.update(array, n, n2);
        return CRSACipher.B0;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        this.update(array, n, n2);
        return 0;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws BadPaddingException, IllegalBlockSizeException {
        this.update(array, n, n2);
        return this.doFinal();
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, BadPaddingException, IllegalBlockSizeException {
        if (this.outputSize > array2.length - n3) {
            throw new ShortBufferException("Need " + this.outputSize + " bytes for output");
        }
        this.update(array, n, n2);
        final byte[] doFinal = this.doFinal();
        final int length = doFinal.length;
        System.arraycopy(doFinal, 0, array2, n3, length);
        return length;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws InvalidKeyException, IllegalBlockSizeException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Could not obtain encoded key");
        }
        if (encoded.length > this.buffer.length) {
            throw new InvalidKeyException("Key is too long for wrapping");
        }
        this.update(encoded, 0, encoded.length);
        try {
            return this.doFinal();
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException("Wrapping failed", ex);
        }
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        if (array.length > this.buffer.length) {
            throw new InvalidKeyException("Key is too long for unwrapping");
        }
        final boolean equals = s.equals("TlsRsaPremasterSecret");
        BadPaddingException ex = null;
        byte[] array2 = null;
        this.update(array, 0, array.length);
        try {
            array2 = this.doFinal();
        }
        catch (final BadPaddingException ex2) {
            if (!equals) {
                throw new InvalidKeyException("Unwrapping failed", ex2);
            }
            ex = ex2;
        }
        catch (final IllegalBlockSizeException ex3) {
            throw new InvalidKeyException("Unwrapping failed", ex3);
        }
        if (equals) {
            if (!(this.spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                throw new IllegalStateException("No TlsRsaPremasterSecretParameterSpec specified");
            }
            array2 = KeyUtil.checkTlsPreMasterSecretKey(((TlsRsaPremasterSecretParameterSpec)this.spec).getClientVersion(), ((TlsRsaPremasterSecretParameterSpec)this.spec).getServerVersion(), this.random, array2, ex != null);
        }
        return constructKey(array2, s, n);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        if (key instanceof CKey) {
            return ((CKey)key).length();
        }
        if (key instanceof RSAKey) {
            return ((RSAKey)key).getModulus().bitLength();
        }
        throw new InvalidKeyException("Unsupported key type: " + key);
    }
    
    private static PublicKey constructPublicKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            return KeyFactory.getInstance(s).generatePublic(new X509EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("No installed provider supports the " + s + " algorithm", ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("Cannot construct public key", ex2);
        }
    }
    
    private static PrivateKey constructPrivateKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            return KeyFactory.getInstance(s).generatePrivate(new PKCS8EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("No installed provider supports the " + s + " algorithm", ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("Cannot construct private key", ex2);
        }
    }
    
    private static SecretKey constructSecretKey(final byte[] array, final String s) {
        return new SecretKeySpec(array, s);
    }
    
    private static Key constructKey(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        switch (n) {
            case 1: {
                return constructPublicKey(array, s);
            }
            case 2: {
                return constructPrivateKey(array, s);
            }
            case 3: {
                return constructSecretKey(array, s);
            }
            default: {
                throw new InvalidKeyException("Unknown key type " + n);
            }
        }
    }
    
    private static native byte[] encryptDecrypt(final byte[] p0, final int p1, final long p2, final boolean p3) throws KeyException;
    
    static {
        B0 = new byte[0];
    }
}
