package sun.security.provider;

import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import sun.security.util.Debug;
import sun.security.jca.JCAUtil;
import java.util.Arrays;
import java.util.Random;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import sun.security.util.DerInputStream;
import java.io.IOException;
import java.security.SignatureException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import java.nio.ByteBuffer;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.SignatureSpi;

abstract class DSA extends SignatureSpi
{
    private static final boolean debug = false;
    private static final int BLINDING_BITS = 7;
    private static final BigInteger BLINDING_CONSTANT;
    private DSAParams params;
    private BigInteger presetP;
    private BigInteger presetQ;
    private BigInteger presetG;
    private BigInteger presetY;
    private BigInteger presetX;
    private SecureRandom signingRandom;
    private final MessageDigest md;
    
    DSA(final MessageDigest md) {
        this.md = md;
    }
    
    private static void checkKey(final DSAParams dsaParams, final int n, final String s) throws InvalidKeyException {
        if (dsaParams.getQ().bitLength() > n) {
            throw new InvalidKeyException("The security strength of " + s + " digest algorithm is not sufficient for this key size");
        }
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof DSAPrivateKey)) {
            throw new InvalidKeyException("not a DSA private key: " + privateKey);
        }
        final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)privateKey;
        final DSAParams params = dsaPrivateKey.getParams();
        if (params == null) {
            throw new InvalidKeyException("DSA private key lacks parameters");
        }
        if (this.md.getAlgorithm() != "NullDigest20") {
            checkKey(params, this.md.getDigestLength() * 8, this.md.getAlgorithm());
        }
        this.params = params;
        this.presetX = dsaPrivateKey.getX();
        this.presetY = null;
        this.presetP = params.getP();
        this.presetQ = params.getQ();
        this.presetG = params.getG();
        this.md.reset();
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof DSAPublicKey)) {
            throw new InvalidKeyException("not a DSA public key: " + publicKey);
        }
        final DSAPublicKey dsaPublicKey = (DSAPublicKey)publicKey;
        final DSAParams params = dsaPublicKey.getParams();
        if (params == null) {
            throw new InvalidKeyException("DSA public key lacks parameters");
        }
        this.params = params;
        this.presetY = dsaPublicKey.getY();
        this.presetX = null;
        this.presetP = params.getP();
        this.presetQ = params.getQ();
        this.presetG = params.getG();
        this.md.reset();
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.md.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        this.md.update(array, n, n2);
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        this.md.update(byteBuffer);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final BigInteger generateK = this.generateK(this.presetQ);
        final BigInteger generateR = this.generateR(this.presetP, this.presetQ, this.presetG, generateK);
        final BigInteger generateS = this.generateS(this.presetX, this.presetQ, generateR, generateK);
        try {
            final DerOutputStream derOutputStream = new DerOutputStream(100);
            derOutputStream.putInteger(generateR);
            derOutputStream.putInteger(generateS);
            return new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final IOException ex) {
            throw new SignatureException("error encoding signature");
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.engineVerify(array, 0, array.length);
    }
    
    @Override
    protected boolean engineVerify(final byte[] array, final int n, final int n2) throws SignatureException {
        BigInteger bigInteger;
        BigInteger bigInteger2;
        try {
            final DerInputStream derInputStream = new DerInputStream(array, n, n2, false);
            final DerValue[] sequence = derInputStream.getSequence(2);
            if (sequence.length != 2 || derInputStream.available() != 0) {
                throw new IOException("Invalid encoding for signature");
            }
            bigInteger = sequence[0].getBigInteger();
            bigInteger2 = sequence[1].getBigInteger();
        }
        catch (final IOException ex) {
            throw new SignatureException("Invalid encoding for signature", ex);
        }
        if (bigInteger.signum() < 0) {
            bigInteger = new BigInteger(1, bigInteger.toByteArray());
        }
        if (bigInteger2.signum() < 0) {
            bigInteger2 = new BigInteger(1, bigInteger2.toByteArray());
        }
        if (bigInteger.compareTo(this.presetQ) == -1 && bigInteger2.compareTo(this.presetQ) == -1) {
            return this.generateV(this.presetY, this.presetP, this.presetQ, this.presetG, this.generateW(this.presetP, this.presetQ, this.presetG, bigInteger2), bigInteger).equals(bigInteger);
        }
        throw new SignatureException("invalid signature: out of range values");
    }
    
    @Deprecated
    @Override
    protected void engineSetParameter(final String s, final Object o) {
        throw new InvalidParameterException("No parameter accepted");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("No parameter accepted");
        }
    }
    
    @Deprecated
    @Override
    protected Object engineGetParameter(final String s) {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    private BigInteger generateR(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, BigInteger add) {
        add = add.add(bigInteger2.multiply(new BigInteger(7, this.getSigningRandom()).add(DSA.BLINDING_CONSTANT)));
        return bigInteger3.modPow(add, bigInteger).mod(bigInteger2);
    }
    
    private BigInteger generateS(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws SignatureException {
        byte[] array;
        try {
            array = this.md.digest();
        }
        catch (final RuntimeException ex) {
            throw new SignatureException(ex.getMessage());
        }
        final int n = bigInteger2.bitLength() / 8;
        if (n < array.length) {
            array = Arrays.copyOfRange(array, 0, n);
        }
        return bigInteger.multiply(bigInteger3).add(new BigInteger(1, array)).multiply(bigInteger4.modInverse(bigInteger2)).mod(bigInteger2);
    }
    
    private BigInteger generateW(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return bigInteger4.modInverse(bigInteger2);
    }
    
    private BigInteger generateV(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6) throws SignatureException {
        byte[] array;
        try {
            array = this.md.digest();
        }
        catch (final RuntimeException ex) {
            throw new SignatureException(ex.getMessage());
        }
        final int n = bigInteger3.bitLength() / 8;
        if (n < array.length) {
            array = Arrays.copyOfRange(array, 0, n);
        }
        return bigInteger4.modPow(new BigInteger(1, array).multiply(bigInteger5).mod(bigInteger3), bigInteger2).multiply(bigInteger.modPow(bigInteger6.multiply(bigInteger5).mod(bigInteger3), bigInteger2)).mod(bigInteger2).mod(bigInteger3);
    }
    
    protected BigInteger generateK(final BigInteger bigInteger) {
        final SecureRandom signingRandom = this.getSigningRandom();
        final byte[] array = new byte[(bigInteger.bitLength() + 7) / 8 + 8];
        signingRandom.nextBytes(array);
        return new BigInteger(1, array).mod(bigInteger.subtract(BigInteger.ONE)).add(BigInteger.ONE);
    }
    
    protected SecureRandom getSigningRandom() {
        if (this.signingRandom == null) {
            if (this.appRandom != null) {
                this.signingRandom = this.appRandom;
            }
            else {
                this.signingRandom = JCAUtil.getSecureRandom();
            }
        }
        return this.signingRandom;
    }
    
    @Override
    public String toString() {
        final String s = "DSA Signature";
        String s2;
        if (this.presetP != null && this.presetQ != null && this.presetG != null) {
            s2 = s + "\n\tp: " + Debug.toHexString(this.presetP) + "\n\tq: " + Debug.toHexString(this.presetQ) + "\n\tg: " + Debug.toHexString(this.presetG);
        }
        else {
            s2 = s + "\n\t P, Q or G not initialized.";
        }
        if (this.presetY != null) {
            s2 = s2 + "\n\ty: " + Debug.toHexString(this.presetY);
        }
        if (this.presetY == null && this.presetX == null) {
            s2 += "\n\tUNINIIALIZED";
        }
        return s2;
    }
    
    static {
        BLINDING_CONSTANT = BigInteger.valueOf(128L);
    }
    
    public static final class SHA224withDSA extends DSA
    {
        public SHA224withDSA() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-224"));
        }
    }
    
    public static final class SHA256withDSA extends DSA
    {
        public SHA256withDSA() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-256"));
        }
    }
    
    public static final class SHA1withDSA extends DSA
    {
        public SHA1withDSA() throws NoSuchAlgorithmException {
            super(MessageDigest.getInstance("SHA-1"));
        }
    }
    
    public static final class RawDSA extends DSA
    {
        public RawDSA() throws NoSuchAlgorithmException {
            super(new NullDigest20());
        }
        
        public static final class NullDigest20 extends MessageDigest
        {
            private final byte[] digestBuffer;
            private int ofs;
            
            protected NullDigest20() {
                super("NullDigest20");
                this.digestBuffer = new byte[20];
                this.ofs = 0;
            }
            
            @Override
            protected void engineUpdate(final byte b) {
                if (this.ofs == this.digestBuffer.length) {
                    this.ofs = Integer.MAX_VALUE;
                }
                else {
                    this.digestBuffer[this.ofs++] = b;
                }
            }
            
            @Override
            protected void engineUpdate(final byte[] array, final int n, final int n2) {
                if (n2 > this.digestBuffer.length - this.ofs) {
                    this.ofs = Integer.MAX_VALUE;
                }
                else {
                    System.arraycopy(array, n, this.digestBuffer, this.ofs, n2);
                    this.ofs += n2;
                }
            }
            
            @Override
            protected final void engineUpdate(final ByteBuffer byteBuffer) {
                final int remaining = byteBuffer.remaining();
                if (remaining > this.digestBuffer.length - this.ofs) {
                    this.ofs = Integer.MAX_VALUE;
                }
                else {
                    byteBuffer.get(this.digestBuffer, this.ofs, remaining);
                    this.ofs += remaining;
                }
            }
            
            @Override
            protected byte[] engineDigest() throws RuntimeException {
                if (this.ofs != this.digestBuffer.length) {
                    throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long");
                }
                this.reset();
                return this.digestBuffer;
            }
            
            @Override
            protected int engineDigest(final byte[] array, final int n, final int n2) throws DigestException {
                if (this.ofs != this.digestBuffer.length) {
                    throw new DigestException("Data for RawDSA must be exactly 20 bytes long");
                }
                if (n2 < this.digestBuffer.length) {
                    throw new DigestException("Output buffer too small; must be at least 20 bytes");
                }
                System.arraycopy(this.digestBuffer, 0, array, n, this.digestBuffer.length);
                this.reset();
                return this.digestBuffer.length;
            }
            
            @Override
            protected void engineReset() {
                this.ofs = 0;
            }
            
            @Override
            protected final int engineGetDigestLength() {
                return this.digestBuffer.length;
            }
        }
    }
}
