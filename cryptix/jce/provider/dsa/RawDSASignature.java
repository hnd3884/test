package cryptix.jce.provider.dsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SignatureException;
import java.security.interfaces.DSAPrivateKey;
import java.security.PrivateKey;
import java.security.interfaces.DSAParams;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.SignatureSpi;

public final class RawDSASignature extends SignatureSpi
{
    private BigInteger ZERO;
    private BigInteger x;
    private BigInteger y;
    private BigInteger g;
    private BigInteger p;
    private BigInteger q;
    private final byte[] buf;
    private int bufPtr;
    private SecureRandom random;
    
    protected void engineInitVerify(final PublicKey key) throws InvalidKeyException {
        this.burn();
        if (!(key instanceof DSAPublicKey)) {
            throw new InvalidKeyException("Not a DSA public key");
        }
        final DSAPublicKey dsa = (DSAPublicKey)key;
        this.y = dsa.getY();
        final DSAParams params = dsa.getParams();
        this.g = params.getG();
        this.p = params.getP();
        this.q = params.getQ();
        if (!this.validate()) {
            this.burn();
            throw new InvalidKeyException("Invalid key values");
        }
    }
    
    protected void engineInitSign(final PrivateKey key) throws InvalidKeyException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.engineInitSign(key, this.random);
    }
    
    protected void engineInitSign(final PrivateKey key, final SecureRandom random) throws InvalidKeyException {
        this.burn();
        if (!(key instanceof DSAPrivateKey)) {
            throw new InvalidKeyException("Not a DSA private key");
        }
        final DSAPrivateKey dsa = (DSAPrivateKey)key;
        this.x = dsa.getX();
        final DSAParams params = dsa.getParams();
        this.g = params.getG();
        this.p = params.getP();
        this.q = params.getQ();
        this.random = random;
        if (!this.validate()) {
            this.burn();
            throw new InvalidKeyException("Invalid key values");
        }
    }
    
    protected void engineUpdate(final byte b) throws SignatureException {
        if (this.bufPtr >= 20) {
            throw new SignatureException("Signature data length exceeded");
        }
        this.buf[this.bufPtr++] = b;
    }
    
    protected void engineUpdate(final byte[] in, final int offset, final int length) throws SignatureException {
        if (this.bufPtr + length > 20) {
            throw new SignatureException("Signature data length exceeded");
        }
        System.arraycopy(in, offset, this.buf, this.bufPtr, length);
        this.bufPtr += length;
    }
    
    protected byte[] engineSign() throws SignatureException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        cryptix/jce/provider/dsa/RawDSASignature.bufPtr:I
        //     4: bipush          20
        //     6: if_icmpeq       19
        //     9: new             Ljava/security/SignatureException;
        //    12: dup            
        //    13: ldc             "Insufficient data for signature"
        //    15: invokespecial   java/security/SignatureException.<init>:(Ljava/lang/String;)V
        //    18: athrow         
        //    19: new             Ljava/math/BigInteger;
        //    22: dup            
        //    23: iconst_1       
        //    24: aload_0         /* this */
        //    25: getfield        cryptix/jce/provider/dsa/RawDSASignature.buf:[B
        //    28: invokespecial   java/math/BigInteger.<init>:(I[B)V
        //    31: astore_1        /* data */
        //    32: aload_0         /* this */
        //    33: getfield        cryptix/jce/provider/dsa/RawDSASignature.q:Ljava/math/BigInteger;
        //    36: invokevirtual   java/math/BigInteger.bitLength:()I
        //    39: istore          qBitLen
        //    41: new             Ljava/math/BigInteger;
        //    44: dup            
        //    45: iload           qBitLen
        //    47: aload_0         /* this */
        //    48: getfield        cryptix/jce/provider/dsa/RawDSASignature.random:Ljava/security/SecureRandom;
        //    51: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //    54: astore_2       
        //    55: aload_2        
        //    56: aload_0         /* this */
        //    57: getfield        cryptix/jce/provider/dsa/RawDSASignature.q:Ljava/math/BigInteger;
        //    60: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    63: iconst_m1      
        //    64: if_icmpne       41
        //    67: aload_0         /* this */
        //    68: getfield        cryptix/jce/provider/dsa/RawDSASignature.g:Ljava/math/BigInteger;
        //    71: aload_2         /* k */
        //    72: aload_0         /* this */
        //    73: getfield        cryptix/jce/provider/dsa/RawDSASignature.p:Ljava/math/BigInteger;
        //    76: invokevirtual   java/math/BigInteger.modPow:(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    79: aload_0         /* this */
        //    80: getfield        cryptix/jce/provider/dsa/RawDSASignature.q:Ljava/math/BigInteger;
        //    83: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    86: astore_3        /* r */
        //    87: aload_2         /* k */
        //    88: aload_0         /* this */
        //    89: getfield        cryptix/jce/provider/dsa/RawDSASignature.q:Ljava/math/BigInteger;
        //    92: invokevirtual   java/math/BigInteger.modInverse:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    95: aload_1         /* data */
        //    96: aload_0         /* this */
        //    97: getfield        cryptix/jce/provider/dsa/RawDSASignature.x:Ljava/math/BigInteger;
        //   100: aload_3         /* r */
        //   101: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   104: invokevirtual   java/math/BigInteger.add:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   107: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   110: aload_0         /* this */
        //   111: getfield        cryptix/jce/provider/dsa/RawDSASignature.q:Ljava/math/BigInteger;
        //   114: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   117: astore          4
        //   119: aload_3         /* r */
        //   120: aload_0         /* this */
        //   121: getfield        cryptix/jce/provider/dsa/RawDSASignature.ZERO:Ljava/math/BigInteger;
        //   124: invokevirtual   java/math/BigInteger.equals:(Ljava/lang/Object;)Z
        //   127: ifne            32
        //   130: aload           4
        //   132: aload_0         /* this */
        //   133: getfield        cryptix/jce/provider/dsa/RawDSASignature.ZERO:Ljava/math/BigInteger;
        //   136: invokevirtual   java/math/BigInteger.equals:(Ljava/lang/Object;)Z
        //   139: ifne            32
        //   142: new             Lcryptix/jce/provider/dsa/SignatureData;
        //   145: dup            
        //   146: aload_3         /* r */
        //   147: aload           s
        //   149: invokespecial   cryptix/jce/provider/dsa/SignatureData.<init>:(Ljava/math/BigInteger;Ljava/math/BigInteger;)V
        //   152: invokevirtual   cryptix/jce/provider/dsa/SignatureData.getData:()[B
        //   155: areturn        
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
    
    protected boolean engineVerify(final byte[] signature) throws SignatureException {
        if (this.bufPtr != 20) {
            throw new SignatureException("Insufficient data for signature");
        }
        final SignatureData sigData = new SignatureData(signature);
        final BigInteger r = sigData.getR();
        final BigInteger s = sigData.getS();
        if (r.compareTo(this.ZERO) != 1 || s.compareTo(this.ZERO) != 1 || r.compareTo(this.q) != -1 || s.compareTo(this.q) != -1) {
            throw new SignatureException("Invalid signature data");
        }
        final BigInteger data = new BigInteger(1, this.buf);
        if (data.bitLength() > 160) {
            throw new InternalError("PANIC");
        }
        final BigInteger w = s.modInverse(this.q);
        final BigInteger u1 = data.multiply(w).mod(this.q);
        final BigInteger u2 = r.multiply(w).mod(this.q);
        final BigInteger gu1 = this.g.modPow(u1, this.p);
        final BigInteger yu2 = this.y.modPow(u2, this.p);
        final BigInteger v = gu1.multiply(yu2).mod(this.p).mod(this.q);
        if (w.compareTo(this.q) != -1) {
            throw new InternalError("PANIC");
        }
        if (v.compareTo(this.q) != -1) {
            throw new InternalError("PANIC");
        }
        if (u1.compareTo(this.q) != -1) {
            throw new InternalError("PANIC");
        }
        if (u2.compareTo(this.q) != -1) {
            throw new InternalError("PANIC");
        }
        if (gu1.compareTo(this.p) != -1) {
            throw new InternalError("PANIC");
        }
        if (yu2.compareTo(this.p) != -1) {
            throw new InternalError("PANIC");
        }
        return v.equals(r);
    }
    
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {
        throw new InvalidParameterException("This algorithm does not accept parameters.");
    }
    
    protected void engineSetParameter(final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("This algorithm does not accept AlgorithmParameterSpec.");
    }
    
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        throw new InvalidParameterException("This algorithm does not have parameters.");
    }
    
    private void burn() {
        final BigInteger x = null;
        this.q = x;
        this.p = x;
        this.g = x;
        this.y = x;
        this.x = x;
        this.bufPtr = 0;
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
    }
    
    private boolean validate() {
        final int pLen = this.p.bitLength();
        if (pLen > 1024 || pLen < 512 || pLen % 64 != 0) {
            return false;
        }
        if (this.q.bitLength() != 160 || this.g.compareTo(this.p) != -1) {
            return false;
        }
        if (this.y != null && this.y.compareTo(this.p) != -1) {
            return false;
        }
        if (this.x != null && this.x.compareTo(this.p) != -1) {
            return false;
        }
        if (this.x != null && this.y != null) {
            throw new InternalError("PANIC");
        }
        if (this.x == null && this.y == null) {
            throw new InternalError("PANIC");
        }
        return true;
    }
    
    public RawDSASignature() {
        this.ZERO = BigInteger.valueOf(0L);
        this.buf = new byte[20];
        this.burn();
    }
}
