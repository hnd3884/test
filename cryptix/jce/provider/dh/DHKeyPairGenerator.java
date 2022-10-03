package cryptix.jce.provider.dh;

import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import cryptix.jce.util.Group;
import cryptix.jce.util.Precomputed;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.KeyPairGeneratorSpi;

public final class DHKeyPairGenerator extends KeyPairGeneratorSpi
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    private static final int KEYSIZE_MIN = 384;
    private static final int KEYSIZE_MAX = 16384;
    private static final int KEYSIZE_DEFAULT = 16384;
    private static final int CERTAINTY = 80;
    private SecureRandom random;
    private BigInteger p;
    private BigInteger g;
    private int xLen;
    private boolean initialized;
    
    public void initialize(final int keysize, final SecureRandom random) {
        if (keysize < 384 || keysize > 16384) {
            throw new IllegalArgumentException("keysize: invalid size (" + keysize + ")");
        }
        final Group group = Precomputed.getStrongGroup(keysize);
        if (group == null) {
            throw new RuntimeException("keysize: sorry, no parameters available");
        }
        this.p = group.getP();
        this.g = group.getG();
        this.xLen = this.p.bitLength() - 1;
        this.random = random;
        this.initialized = true;
    }
    
    public void initialize(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        final DHParameterSpec dhps = (DHParameterSpec)params;
        final BigInteger p = dhps.getP();
        final BigInteger g = dhps.getG();
        final int l = dhps.getL();
        this.p = p;
        this.g = g;
        this.xLen = ((l == 0) ? (p.bitLength() - 1) : l);
        this.random = random;
        this.initialized = true;
    }
    
    public KeyPair generateKeyPair() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.initialized:Z
        //     4: ifne            11
        //     7: aload_0         /* this */
        //     8: invokespecial   cryptix/jce/provider/dh/DHKeyPairGenerator.initialize:()V
        //    11: new             Ljava/math/BigInteger;
        //    14: dup            
        //    15: aload_0         /* this */
        //    16: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.xLen:I
        //    19: aload_0         /* this */
        //    20: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.random:Ljava/security/SecureRandom;
        //    23: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //    26: astore_1       
        //    27: aload_1        
        //    28: getstatic       cryptix/jce/provider/dh/DHKeyPairGenerator.ZERO:Ljava/math/BigInteger;
        //    31: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    34: iconst_1       
        //    35: if_icmpne       11
        //    38: aload_1        
        //    39: aload_0         /* this */
        //    40: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.p:Ljava/math/BigInteger;
        //    43: getstatic       cryptix/jce/provider/dh/DHKeyPairGenerator.ONE:Ljava/math/BigInteger;
        //    46: invokevirtual   java/math/BigInteger.subtract:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    49: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    52: iconst_m1      
        //    53: if_icmpne       11
        //    56: aload_0         /* this */
        //    57: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.g:Ljava/math/BigInteger;
        //    60: aload_1         /* x */
        //    61: aload_0         /* this */
        //    62: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.p:Ljava/math/BigInteger;
        //    65: invokevirtual   java/math/BigInteger.modPow:(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    68: astore_2        /* y */
        //    69: new             Ljavax/crypto/spec/DHParameterSpec;
        //    72: dup            
        //    73: aload_0         /* this */
        //    74: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.p:Ljava/math/BigInteger;
        //    77: aload_0         /* this */
        //    78: getfield        cryptix/jce/provider/dh/DHKeyPairGenerator.g:Ljava/math/BigInteger;
        //    81: invokespecial   javax/crypto/spec/DHParameterSpec.<init>:(Ljava/math/BigInteger;Ljava/math/BigInteger;)V
        //    84: astore_3        /* params */
        //    85: new             Lcryptix/jce/provider/dh/DHPrivateKeyCryptix;
        //    88: dup            
        //    89: aload_1         /* x */
        //    90: aload_3         /* params */
        //    91: invokespecial   cryptix/jce/provider/dh/DHPrivateKeyCryptix.<init>:(Ljava/math/BigInteger;Ljavax/crypto/spec/DHParameterSpec;)V
        //    94: astore          priv
        //    96: new             Lcryptix/jce/provider/dh/DHPublicKeyCryptix;
        //    99: dup            
        //   100: aload_2         /* y */
        //   101: aload_3         /* params */
        //   102: invokespecial   cryptix/jce/provider/dh/DHPublicKeyCryptix.<init>:(Ljava/math/BigInteger;Ljavax/crypto/spec/DHParameterSpec;)V
        //   105: astore          pub
        //   107: new             Ljava/security/KeyPair;
        //   110: dup            
        //   111: aload           pub
        //   113: aload           priv
        //   115: invokespecial   java/security/KeyPair.<init>:(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
        //   118: areturn        
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
    
    private void initialize() {
        this.initialize(16384, new SecureRandom());
    }
    
    public DHKeyPairGenerator() {
        this.initialized = false;
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
    }
}
