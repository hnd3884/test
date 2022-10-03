package sun.security.pkcs11;

import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import sun.security.util.KeyUtil;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import java.math.BigInteger;
import java.io.IOException;
import sun.security.rsa.RSASignature;
import java.security.GeneralSecurityException;
import sun.nio.ch.DirectBuffer;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.InvalidAlgorithmParameterException;
import sun.security.rsa.RSAPadding;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import java.security.InvalidKeyException;
import java.security.interfaces.ECKey;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
import java.security.Key;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.NoSuchAlgorithmException;
import sun.security.x509.AlgorithmId;
import java.security.ProviderException;
import java.security.MessageDigest;
import sun.security.util.ObjectIdentifier;
import java.security.SignatureSpi;

final class P11Signature extends SignatureSpi
{
    private final Token token;
    private final String algorithm;
    private final String keyAlgorithm;
    private final long mechanism;
    private final ObjectIdentifier digestOID;
    private final int type;
    private P11Key p11Key;
    private final MessageDigest md;
    private Session session;
    private int mode;
    private boolean initialized;
    private final byte[] buffer;
    private int bytesProcessed;
    private static final int M_SIGN = 1;
    private static final int M_VERIFY = 2;
    private static final int T_DIGEST = 1;
    private static final int T_UPDATE = 2;
    private static final int T_RAW = 3;
    private static final int RAW_ECDSA_MAX = 128;
    
    P11Signature(final Token token, final String algorithm, final long mechanism) throws NoSuchAlgorithmException, PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
        byte[] buffer = null;
        ObjectIdentifier digestOID = null;
        MessageDigest md = null;
        switch ((int)mechanism) {
            case 4:
            case 5:
            case 6:
            case 64:
            case 65:
            case 66:
            case 70: {
                this.keyAlgorithm = "RSA";
                this.type = 2;
                buffer = new byte[] { 0 };
                break;
            }
            case 18: {
                this.keyAlgorithm = "DSA";
                this.type = 2;
                buffer = new byte[] { 0 };
                break;
            }
            case 4162: {
                this.keyAlgorithm = "EC";
                this.type = 2;
                buffer = new byte[] { 0 };
                break;
            }
            case 17: {
                this.keyAlgorithm = "DSA";
                if (algorithm.equals("DSA")) {
                    this.type = 1;
                    md = MessageDigest.getInstance("SHA-1");
                    break;
                }
                if (algorithm.equals("RawDSA")) {
                    this.type = 3;
                    buffer = new byte[20];
                    break;
                }
                throw new ProviderException(algorithm);
            }
            case 4161: {
                this.keyAlgorithm = "EC";
                if (algorithm.equals("NONEwithECDSA")) {
                    this.type = 3;
                    buffer = new byte[128];
                    break;
                }
                String s;
                if (algorithm.equals("SHA1withECDSA")) {
                    s = "SHA-1";
                }
                else if (algorithm.equals("SHA224withECDSA")) {
                    s = "SHA-224";
                }
                else if (algorithm.equals("SHA256withECDSA")) {
                    s = "SHA-256";
                }
                else if (algorithm.equals("SHA384withECDSA")) {
                    s = "SHA-384";
                }
                else {
                    if (!algorithm.equals("SHA512withECDSA")) {
                        throw new ProviderException(algorithm);
                    }
                    s = "SHA-512";
                }
                this.type = 1;
                md = MessageDigest.getInstance(s);
                break;
            }
            case 1:
            case 3: {
                this.keyAlgorithm = "RSA";
                this.type = 1;
                if (algorithm.equals("MD5withRSA")) {
                    md = MessageDigest.getInstance("MD5");
                    digestOID = AlgorithmId.MD5_oid;
                    break;
                }
                if (algorithm.equals("SHA1withRSA")) {
                    md = MessageDigest.getInstance("SHA-1");
                    digestOID = AlgorithmId.SHA_oid;
                    break;
                }
                if (algorithm.equals("MD2withRSA")) {
                    md = MessageDigest.getInstance("MD2");
                    digestOID = AlgorithmId.MD2_oid;
                    break;
                }
                if (algorithm.equals("SHA224withRSA")) {
                    md = MessageDigest.getInstance("SHA-224");
                    digestOID = AlgorithmId.SHA224_oid;
                    break;
                }
                if (algorithm.equals("SHA256withRSA")) {
                    md = MessageDigest.getInstance("SHA-256");
                    digestOID = AlgorithmId.SHA256_oid;
                    break;
                }
                if (algorithm.equals("SHA384withRSA")) {
                    md = MessageDigest.getInstance("SHA-384");
                    digestOID = AlgorithmId.SHA384_oid;
                    break;
                }
                if (algorithm.equals("SHA512withRSA")) {
                    md = MessageDigest.getInstance("SHA-512");
                    digestOID = AlgorithmId.SHA512_oid;
                    break;
                }
                throw new ProviderException("Unknown signature: " + algorithm);
            }
            default: {
                throw new ProviderException("Unknown mechanism: " + mechanism);
            }
        }
        this.buffer = buffer;
        this.digestOID = digestOID;
        this.md = md;
    }
    
    private void reset(final boolean b) {
        if (!this.initialized) {
            return;
        }
        this.initialized = false;
        try {
            if (this.session == null) {
                return;
            }
            if (b && this.token.explicitCancel) {
                this.cancelOperation();
            }
        }
        finally {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
        }
    }
    
    private void cancelOperation() {
        this.token.ensureValid();
        try {
            if (this.mode == 1) {
                if (this.type == 2) {
                    this.token.p11.C_SignFinal(this.session.id(), 0);
                }
                else {
                    byte[] array;
                    if (this.type == 1) {
                        array = this.md.digest();
                    }
                    else {
                        array = this.buffer;
                    }
                    this.token.p11.C_Sign(this.session.id(), array);
                }
            }
            else {
                byte[] array2;
                if (this.keyAlgorithm.equals("DSA")) {
                    array2 = new byte[40];
                }
                else {
                    array2 = new byte[this.p11Key.length() + 7 >> 3];
                }
                if (this.type == 2) {
                    this.token.p11.C_VerifyFinal(this.session.id(), array2);
                }
                else {
                    byte[] array3;
                    if (this.type == 1) {
                        array3 = this.md.digest();
                    }
                    else {
                        array3 = this.buffer;
                    }
                    this.token.p11.C_Verify(this.session.id(), array3, array2);
                }
            }
        }
        catch (final PKCS11Exception ex) {
            if (this.mode == 2) {
                final long errorCode = ex.getErrorCode();
                if (errorCode == 192L || errorCode == 193L) {
                    return;
                }
            }
            throw new ProviderException("cancel failed", ex);
        }
    }
    
    private void ensureInitialized() {
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void initialize() {
        if (this.p11Key == null) {
            throw new ProviderException("Operation cannot be performed without calling engineInit first");
        }
        final long keyID = this.p11Key.getKeyID();
        try {
            this.token.ensureValid();
            if (this.session == null) {
                this.session = this.token.getOpSession();
            }
            if (this.mode == 1) {
                this.token.p11.C_SignInit(this.session.id(), new CK_MECHANISM(this.mechanism), keyID);
            }
            else {
                this.token.p11.C_VerifyInit(this.session.id(), new CK_MECHANISM(this.mechanism), keyID);
            }
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw new ProviderException("Initialization failed", ex);
        }
        if (this.bytesProcessed != 0) {
            this.bytesProcessed = 0;
            if (this.md != null) {
                this.md.reset();
            }
        }
        this.initialized = true;
    }
    
    private void checkKeySize(final String s, final Key key) throws InvalidKeyException {
        CK_MECHANISM_INFO mechanismInfo = null;
        try {
            mechanismInfo = this.token.getMechanismInfo(this.mechanism);
        }
        catch (final PKCS11Exception ex) {}
        if (mechanismInfo == null) {
            return;
        }
        final int iMinKeySize = mechanismInfo.iMinKeySize;
        int iMaxKeySize = mechanismInfo.iMaxKeySize;
        if (this.md != null && this.mechanism == 17L && iMaxKeySize > 1024) {
            iMaxKeySize = 1024;
        }
        int n;
        if (key instanceof P11Key) {
            n = ((P11Key)key).length();
        }
        else {
            try {
                if (s.equals("RSA")) {
                    n = ((RSAKey)key).getModulus().bitLength();
                }
                else if (s.equals("DSA")) {
                    n = ((DSAKey)key).getParams().getP().bitLength();
                }
                else {
                    if (!s.equals("EC")) {
                        throw new ProviderException("Error: unsupported algo " + s);
                    }
                    n = ((ECKey)key).getParams().getCurve().getField().getFieldSize();
                }
            }
            catch (final ClassCastException ex2) {
                throw new InvalidKeyException(s + " key must be the right type", ex2);
            }
        }
        if (n < iMinKeySize) {
            throw new InvalidKeyException(s + " key must be at least " + iMinKeySize + " bits");
        }
        if (n > iMaxKeySize) {
            throw new InvalidKeyException(s + " key must be at most " + iMaxKeySize + " bits");
        }
        if (s.equals("RSA")) {
            this.checkRSAKeyLength(n);
        }
    }
    
    private void checkRSAKeyLength(final int n) throws InvalidKeyException {
        RSAPadding instance;
        try {
            instance = RSAPadding.getInstance(1, n + 7 >> 3);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
        final int maxDataSize = instance.getMaxDataSize();
        int n2;
        if (this.algorithm.equals("MD5withRSA") || this.algorithm.equals("MD2withRSA")) {
            n2 = 34;
        }
        else if (this.algorithm.equals("SHA1withRSA")) {
            n2 = 35;
        }
        else if (this.algorithm.equals("SHA224withRSA")) {
            n2 = 47;
        }
        else if (this.algorithm.equals("SHA256withRSA")) {
            n2 = 51;
        }
        else if (this.algorithm.equals("SHA384withRSA")) {
            n2 = 67;
        }
        else {
            if (!this.algorithm.equals("SHA512withRSA")) {
                throw new ProviderException("Unknown signature algo: " + this.algorithm);
            }
            n2 = 83;
        }
        if (n2 > maxDataSize) {
            throw new InvalidKeyException("Key is too short for this signature algorithm");
        }
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (publicKey != this.p11Key) {
            this.checkKeySize(this.keyAlgorithm, publicKey);
        }
        this.reset(true);
        this.mode = 2;
        this.p11Key = P11KeyFactory.convertKey(this.token, publicKey, this.keyAlgorithm);
        this.initialize();
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (privateKey != this.p11Key) {
            this.checkKeySize(this.keyAlgorithm, privateKey);
        }
        this.reset(true);
        this.mode = 1;
        this.p11Key = P11KeyFactory.convertKey(this.token, privateKey, this.keyAlgorithm);
        this.initialize();
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.ensureInitialized();
        switch (this.type) {
            case 2: {
                this.buffer[0] = b;
                this.engineUpdate(this.buffer, 0, 1);
                break;
            }
            case 1: {
                this.md.update(b);
                ++this.bytesProcessed;
                break;
            }
            case 3: {
                if (this.bytesProcessed >= this.buffer.length) {
                    this.bytesProcessed = this.buffer.length + 1;
                    return;
                }
                this.buffer[this.bytesProcessed++] = b;
                break;
            }
            default: {
                throw new ProviderException("Internal error");
            }
        }
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.ensureInitialized();
        if (n2 == 0) {
            return;
        }
        if (n2 + this.bytesProcessed < 0) {
            throw new ProviderException("Processed bytes limits exceeded.");
        }
        switch (this.type) {
            case 2: {
                try {
                    if (this.mode == 1) {
                        this.token.p11.C_SignUpdate(this.session.id(), 0L, array, n, n2);
                    }
                    else {
                        this.token.p11.C_VerifyUpdate(this.session.id(), 0L, array, n, n2);
                    }
                    this.bytesProcessed += n2;
                    break;
                }
                catch (final PKCS11Exception ex) {
                    this.reset(false);
                    throw new ProviderException(ex);
                }
            }
            case 1: {
                this.md.update(array, n, n2);
                this.bytesProcessed += n2;
                break;
            }
            case 3: {
                if (this.bytesProcessed + n2 > this.buffer.length) {
                    this.bytesProcessed = this.buffer.length + 1;
                    return;
                }
                System.arraycopy(array, n, this.buffer, this.bytesProcessed, n2);
                this.bytesProcessed += n2;
                break;
            }
            default: {
                throw new ProviderException("Internal error");
            }
        }
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        this.ensureInitialized();
        final int remaining = byteBuffer.remaining();
        if (remaining <= 0) {
            return;
        }
        switch (this.type) {
            case 2: {
                if (!(byteBuffer instanceof DirectBuffer)) {
                    super.engineUpdate(byteBuffer);
                    return;
                }
                final long address = ((DirectBuffer)byteBuffer).address();
                final int position = byteBuffer.position();
                try {
                    if (this.mode == 1) {
                        this.token.p11.C_SignUpdate(this.session.id(), address + position, null, 0, remaining);
                    }
                    else {
                        this.token.p11.C_VerifyUpdate(this.session.id(), address + position, null, 0, remaining);
                    }
                    this.bytesProcessed += remaining;
                    byteBuffer.position(position + remaining);
                    break;
                }
                catch (final PKCS11Exception ex) {
                    this.reset(false);
                    throw new ProviderException("Update failed", ex);
                }
            }
            case 1: {
                this.md.update(byteBuffer);
                this.bytesProcessed += remaining;
                break;
            }
            case 3: {
                if (this.bytesProcessed + remaining > this.buffer.length) {
                    this.bytesProcessed = this.buffer.length + 1;
                    return;
                }
                byteBuffer.get(this.buffer, this.bytesProcessed, remaining);
                this.bytesProcessed += remaining;
                break;
            }
            default: {
                this.reset(false);
                throw new ProviderException("Internal error");
            }
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        this.ensureInitialized();
        boolean b = true;
        try {
            byte[] array;
            if (this.type == 2) {
                array = this.token.p11.C_SignFinal(this.session.id(), this.keyAlgorithm.equals("DSA") ? 40 : 0);
            }
            else {
                byte[] array2;
                if (this.type == 1) {
                    array2 = this.md.digest();
                }
                else if (this.mechanism == 17L) {
                    if (this.bytesProcessed != this.buffer.length) {
                        throw new SignatureException("Data for RawDSA must be exactly 20 bytes long");
                    }
                    array2 = this.buffer;
                }
                else {
                    if (this.bytesProcessed > this.buffer.length) {
                        throw new SignatureException("Data for NONEwithECDSA must be at most 128 bytes long");
                    }
                    array2 = new byte[this.bytesProcessed];
                    System.arraycopy(this.buffer, 0, array2, 0, this.bytesProcessed);
                }
                if (!this.keyAlgorithm.equals("RSA")) {
                    array = this.token.p11.C_Sign(this.session.id(), array2);
                }
                else {
                    byte[] array3 = this.encodeSignature(array2);
                    if (this.mechanism == 3L) {
                        array3 = this.pkcs1Pad(array3);
                    }
                    array = this.token.p11.C_Sign(this.session.id(), array3);
                }
            }
            b = false;
            if (!this.keyAlgorithm.equals("RSA")) {
                return dsaToASN1(array);
            }
            return array;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            throw new ProviderException(ex);
        }
        finally {
            this.reset(b);
        }
    }
    
    @Override
    protected boolean engineVerify(byte[] array) throws SignatureException {
        this.ensureInitialized();
        boolean b = true;
        try {
            if (this.keyAlgorithm.equals("DSA")) {
                array = asn1ToDSA(array);
            }
            else if (this.keyAlgorithm.equals("EC")) {
                array = this.asn1ToECDSA(array);
            }
            if (this.type == 2) {
                this.token.p11.C_VerifyFinal(this.session.id(), array);
            }
            else {
                byte[] array2;
                if (this.type == 1) {
                    array2 = this.md.digest();
                }
                else if (this.mechanism == 17L) {
                    if (this.bytesProcessed != this.buffer.length) {
                        throw new SignatureException("Data for RawDSA must be exactly 20 bytes long");
                    }
                    array2 = this.buffer;
                }
                else {
                    if (this.bytesProcessed > this.buffer.length) {
                        throw new SignatureException("Data for NONEwithECDSA must be at most 128 bytes long");
                    }
                    array2 = new byte[this.bytesProcessed];
                    System.arraycopy(this.buffer, 0, array2, 0, this.bytesProcessed);
                }
                if (!this.keyAlgorithm.equals("RSA")) {
                    this.token.p11.C_Verify(this.session.id(), array2, array);
                }
                else {
                    byte[] array3 = this.encodeSignature(array2);
                    if (this.mechanism == 3L) {
                        array3 = this.pkcs1Pad(array3);
                    }
                    this.token.p11.C_Verify(this.session.id(), array3, array);
                }
            }
            b = false;
            return true;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            final long errorCode = ex.getErrorCode();
            if (errorCode == 192L) {
                return false;
            }
            if (errorCode == 193L) {
                return false;
            }
            if (errorCode == 33L) {
                return false;
            }
            throw new ProviderException(ex);
        }
        finally {
            this.reset(b);
        }
    }
    
    private byte[] pkcs1Pad(final byte[] array) {
        try {
            return RSAPadding.getInstance(1, this.p11Key.length() + 7 >> 3).pad(array);
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException(ex);
        }
    }
    
    private byte[] encodeSignature(final byte[] array) throws SignatureException {
        try {
            return RSASignature.encodeSignature(this.digestOID, array);
        }
        catch (final IOException ex) {
            throw new SignatureException("Invalid encoding", ex);
        }
    }
    
    private static byte[] dsaToASN1(final byte[] array) {
        final int n = array.length >> 1;
        final BigInteger bigInteger = new BigInteger(1, P11Util.subarray(array, 0, n));
        final BigInteger bigInteger2 = new BigInteger(1, P11Util.subarray(array, n, n));
        try {
            final DerOutputStream derOutputStream = new DerOutputStream(100);
            derOutputStream.putInteger(bigInteger);
            derOutputStream.putInteger(bigInteger2);
            return new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final IOException ex) {
            throw new RuntimeException("Internal error", ex);
        }
    }
    
    private static byte[] asn1ToDSA(final byte[] array) throws SignatureException {
        try {
            final DerInputStream derInputStream = new DerInputStream(array, 0, array.length, false);
            final DerValue[] sequence = derInputStream.getSequence(2);
            if (sequence.length != 2 || derInputStream.available() != 0) {
                throw new IOException("Invalid encoding for signature");
            }
            final BigInteger positiveBigInteger = sequence[0].getPositiveBigInteger();
            final BigInteger positiveBigInteger2 = sequence[1].getPositiveBigInteger();
            final byte[] byteArray = toByteArray(positiveBigInteger, 20);
            final byte[] byteArray2 = toByteArray(positiveBigInteger2, 20);
            if (byteArray == null || byteArray2 == null) {
                throw new SignatureException("Out of range value for R or S");
            }
            return P11Util.concat(byteArray, byteArray2);
        }
        catch (final SignatureException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new SignatureException("Invalid encoding for signature", ex2);
        }
    }
    
    private byte[] asn1ToECDSA(final byte[] array) throws SignatureException {
        try {
            final DerInputStream derInputStream = new DerInputStream(array, 0, array.length, false);
            final DerValue[] sequence = derInputStream.getSequence(2);
            if (sequence.length != 2 || derInputStream.available() != 0) {
                throw new IOException("Invalid encoding for signature");
            }
            final BigInteger positiveBigInteger = sequence[0].getPositiveBigInteger();
            final BigInteger positiveBigInteger2 = sequence[1].getPositiveBigInteger();
            final byte[] trimZeroes = KeyUtil.trimZeroes(positiveBigInteger.toByteArray());
            final byte[] trimZeroes2 = KeyUtil.trimZeroes(positiveBigInteger2.toByteArray());
            final int max = Math.max(trimZeroes.length, trimZeroes2.length);
            final byte[] array2 = new byte[max << 1];
            System.arraycopy(trimZeroes, 0, array2, max - trimZeroes.length, trimZeroes.length);
            System.arraycopy(trimZeroes2, 0, array2, array2.length - trimZeroes2.length, trimZeroes2.length);
            return array2;
        }
        catch (final Exception ex) {
            throw new SignatureException("Invalid encoding for signature", ex);
        }
    }
    
    private static byte[] toByteArray(final BigInteger bigInteger, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        final int length = byteArray.length;
        if (length == n) {
            return byteArray;
        }
        if (length == n + 1 && byteArray[0] == 0) {
            final byte[] array = new byte[n];
            System.arraycopy(byteArray, 1, array, 0, n);
            return array;
        }
        if (length > n) {
            return null;
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(byteArray, 0, array2, n - length, length);
        return array2;
    }
    
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("setParameter() not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("No parameter accepted");
        }
    }
    
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("getParameter() not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
}
