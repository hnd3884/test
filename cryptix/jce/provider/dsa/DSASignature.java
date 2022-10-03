package cryptix.jce.provider.dsa;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.InvalidParameterException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.SignatureSpi;

public final class DSASignature extends SignatureSpi implements Cloneable
{
    private static final SecureRandom _fallbackRng;
    private static final boolean MODE_SIGN = false;
    private static final boolean MODE_VERIFY = true;
    private final MessageDigest _md;
    private BigInteger _g;
    private BigInteger _p;
    private BigInteger _q;
    private BigInteger _exp;
    
    public Object clone() throws CloneNotSupportedException {
        return new DSASignature(this);
    }
    
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        throw new InvalidParameterException("No params supported.");
    }
    
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, DSASignature._fallbackRng);
    }
    
    protected void engineInitSign(final PrivateKey privKey, final SecureRandom random) throws InvalidKeyException {
        if (!(privKey instanceof DSAPrivateKey)) {
            throw new InvalidKeyException("Not a DSA private key");
        }
        final DSAPrivateKey dsaPrivKey = (DSAPrivateKey)privKey;
        this._exp = dsaPrivKey.getX();
        final DSAParams params = dsaPrivKey.getParams();
        this._g = params.getG();
        this._p = params.getP();
        this._q = params.getQ();
        access$1(this, random);
        this._md.reset();
        if (!this._isValid(false)) {
            this._clear();
            throw new InvalidKeyException("Corrupt key?");
        }
    }
    
    protected byte[] engineSign() throws SignatureException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: iconst_1       
        //     5: aload_0         /* this */
        //     6: getfield        cryptix/jce/provider/dsa/DSASignature._md:Ljava/security/MessageDigest;
        //     9: invokevirtual   java/security/MessageDigest.digest:()[B
        //    12: invokespecial   java/math/BigInteger.<init>:(I[B)V
        //    15: astore_1        /* data */
        //    16: aload_0         /* this */
        //    17: getfield        cryptix/jce/provider/dsa/DSASignature._q:Ljava/math/BigInteger;
        //    20: invokevirtual   java/math/BigInteger.bitLength:()I
        //    23: istore          qBitLen
        //    25: new             Ljava/math/BigInteger;
        //    28: dup            
        //    29: iload           qBitLen
        //    31: aload_0         /* this */
        //    32: invokestatic    cryptix/jce/provider/dsa/DSASignature.access$0:(Lcryptix/jce/provider/dsa/DSASignature;)Ljava/security/SecureRandom;
        //    35: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //    38: astore_2       
        //    39: aload_2        
        //    40: aload_0         /* this */
        //    41: getfield        cryptix/jce/provider/dsa/DSASignature._q:Ljava/math/BigInteger;
        //    44: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    47: iconst_m1      
        //    48: if_icmpne       25
        //    51: aload_0         /* this */
        //    52: getfield        cryptix/jce/provider/dsa/DSASignature._g:Ljava/math/BigInteger;
        //    55: aload_2         /* k */
        //    56: aload_0         /* this */
        //    57: getfield        cryptix/jce/provider/dsa/DSASignature._p:Ljava/math/BigInteger;
        //    60: invokevirtual   java/math/BigInteger.modPow:(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    63: aload_0         /* this */
        //    64: getfield        cryptix/jce/provider/dsa/DSASignature._q:Ljava/math/BigInteger;
        //    67: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    70: astore_3        /* r */
        //    71: aload_2         /* k */
        //    72: aload_0         /* this */
        //    73: getfield        cryptix/jce/provider/dsa/DSASignature._q:Ljava/math/BigInteger;
        //    76: invokevirtual   java/math/BigInteger.modInverse:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    79: aload_1         /* data */
        //    80: aload_0         /* this */
        //    81: getfield        cryptix/jce/provider/dsa/DSASignature._exp:Ljava/math/BigInteger;
        //    84: aload_3         /* r */
        //    85: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    88: invokevirtual   java/math/BigInteger.add:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    91: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    94: aload_0         /* this */
        //    95: getfield        cryptix/jce/provider/dsa/DSASignature._q:Ljava/math/BigInteger;
        //    98: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   101: astore          4
        //   103: aload_3         /* r */
        //   104: getstatic       java/math/BigInteger.ZERO:Ljava/math/BigInteger;
        //   107: invokevirtual   java/math/BigInteger.equals:(Ljava/lang/Object;)Z
        //   110: ifne            16
        //   113: aload           4
        //   115: getstatic       java/math/BigInteger.ZERO:Ljava/math/BigInteger;
        //   118: invokevirtual   java/math/BigInteger.equals:(Ljava/lang/Object;)Z
        //   121: ifne            16
        //   124: new             Lcryptix/jce/provider/dsa/SignatureData;
        //   127: dup            
        //   128: aload_3         /* r */
        //   129: aload           s
        //   131: invokespecial   cryptix/jce/provider/dsa/SignatureData.<init>:(Ljava/math/BigInteger;Ljava/math/BigInteger;)V
        //   134: invokevirtual   cryptix/jce/provider/dsa/SignatureData.getData:()[B
        //   137: areturn        
        //    Exceptions:
        //  throws java.security.SignatureException
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2945)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2501)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    protected int engineSign(final byte[] outbuf, final int offset, final int len) throws SignatureException {
        final byte[] sigBytes = this.engineSign();
        if (sigBytes.length > len) {
            throw new SignatureException("Buffer too small.");
        }
        System.arraycopy(sigBytes, 0, outbuf, offset, sigBytes.length);
        return sigBytes.length;
    }
    
    protected void engineUpdate(final byte b) throws SignatureException {
        this._md.update(b);
    }
    
    protected void engineUpdate(final byte[] b, final int off, final int len) throws SignatureException {
        this._md.update(b, off, len);
    }
    
    protected void engineInitVerify(final PublicKey pubKey) throws InvalidKeyException {
        if (!(pubKey instanceof DSAPublicKey)) {
            throw new InvalidKeyException("Not a DSA public key");
        }
        final DSAPublicKey dsaPubKey = (DSAPublicKey)pubKey;
        this._exp = dsaPubKey.getY();
        final DSAParams dsaParams = dsaPubKey.getParams();
        this._g = dsaParams.getG();
        this._p = dsaParams.getP();
        this._q = dsaParams.getQ();
        access$1(this, null);
        this._md.reset();
        if (!this._isValid(true)) {
            this._clear();
            throw new InvalidKeyException("Corrupt key?");
        }
    }
    
    protected boolean engineVerify(final byte[] sigBytes) throws SignatureException {
        final SignatureData sigData = new SignatureData(sigBytes);
        final BigInteger r = sigData.getR();
        final BigInteger s = sigData.getS();
        if (r.compareTo(BigInteger.ZERO) != 1 || r.compareTo(this._q) != -1 || s.compareTo(BigInteger.ZERO) != 1 || s.compareTo(this._q) != -1) {
            throw new SignatureException("Invalid signature data");
        }
        final BigInteger data = new BigInteger(1, this._md.digest());
        if (data.bitLength() > 160) {
            throw new InternalError("PANIC");
        }
        final BigInteger w = s.modInverse(this._q);
        final BigInteger u1 = data.multiply(w).mod(this._q);
        final BigInteger u2 = r.multiply(w).mod(this._q);
        final BigInteger gu1 = this._g.modPow(u1, this._p);
        final BigInteger yu2 = this._exp.modPow(u2, this._p);
        final BigInteger v = gu1.multiply(yu2).mod(this._p).mod(this._q);
        if (w.compareTo(this._q) != -1) {
            throw new InternalError("PANIC");
        }
        if (v.compareTo(this._q) != -1) {
            throw new InternalError("PANIC");
        }
        if (u1.compareTo(this._q) != -1) {
            throw new InternalError("PANIC");
        }
        if (u2.compareTo(this._q) != -1) {
            throw new InternalError("PANIC");
        }
        if (gu1.compareTo(this._p) != -1) {
            throw new InternalError("PANIC");
        }
        if (yu2.compareTo(this._p) != -1) {
            throw new InternalError("PANIC");
        }
        return v.equals(r);
    }
    
    protected void engineSetParameter(final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No params supported.");
    }
    
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {
        throw new InvalidParameterException("No params supported.");
    }
    
    private void _clear() {
        access$1(this, null);
        final BigInteger bigInteger = null;
        this._exp = bigInteger;
        this._q = bigInteger;
        this._p = bigInteger;
        this._g = bigInteger;
        this._md.reset();
    }
    
    private boolean _isValid(final boolean mode) {
        final int pLen = this._p.bitLength();
        return pLen <= 1024 && pLen >= 512 && pLen % 64 == 0 && this._q.bitLength() == 160 && this._g.compareTo(this._p) == -1 && this._exp != null && this._exp.compareTo(this._p) == -1 && this.appRandom == null == mode;
    }
    
    static /* synthetic */ void access$1(final DSASignature dsaSignature, final SecureRandom appRandom) {
        dsaSignature.appRandom = appRandom;
    }
    
    public DSASignature() {
        try {
            this._md = MessageDigest.getInstance("SHA");
        }
        catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException("PANIC: Algorithm SHA not found!");
        }
    }
    
    private DSASignature(final DSASignature srcSig) throws CloneNotSupportedException {
        this._md = (MessageDigest)srcSig._md.clone();
        this._g = srcSig._g;
        this._p = srcSig._p;
        this._q = srcSig._q;
        this._exp = srcSig._exp;
    }
    
    static {
        _fallbackRng = new SecureRandom();
    }
}
