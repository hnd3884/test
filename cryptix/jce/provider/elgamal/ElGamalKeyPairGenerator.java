package cryptix.jce.provider.elgamal;

import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.KeyPairGeneratorSpi;

public final class ElGamalKeyPairGenerator extends KeyPairGeneratorSpi
{
    private static final int KEYSIZE_MIN = 384;
    private static final int KEYSIZE_MAX = 16384;
    private static final int KEYSIZE_DEFAULT = 1536;
    private static final BigInteger TWO;
    private SecureRandom random;
    private int keysize;
    private boolean initialized;
    
    public void initialize(final int keysize, final SecureRandom random) {
        if (keysize < 384 || keysize > 16384) {
            throw new IllegalArgumentException("keysize: invalid size (" + keysize + ")");
        }
        this.keysize = keysize;
        this.random = random;
        this.initialized = true;
    }
    
    public void initialize(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("NYI");
    }
    
    public KeyPair generateKeyPair() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.initialized:Z
        //     4: ifne            11
        //     7: aload_0         /* this */
        //     8: invokespecial   cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.initialize:()V
        //    11: aload_0         /* this */
        //    12: getfield        cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.keysize:I
        //    15: invokestatic    cryptix/jce/provider/elgamal/PrecomputedParams.get:(I)Lcryptix/jce/ElGamalParams;
        //    18: astore_1        /* params */
        //    19: aload_1         /* params */
        //    20: ifnonnull       33
        //    23: new             Ljava/lang/RuntimeException;
        //    26: dup            
        //    27: ldc             "NYI"
        //    29: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //    32: athrow         
        //    33: aload_1         /* params */
        //    34: invokeinterface cryptix/jce/ElGamalParams.getP:()Ljava/math/BigInteger;
        //    39: astore_2        /* p */
        //    40: aload_1         /* params */
        //    41: invokeinterface cryptix/jce/ElGamalParams.getG:()Ljava/math/BigInteger;
        //    46: astore_3        /* g */
        //    47: getstatic       cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.TWO:Ljava/math/BigInteger;
        //    50: astore          xMin
        //    52: aload_2         /* p */
        //    53: getstatic       cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.TWO:Ljava/math/BigInteger;
        //    56: invokevirtual   java/math/BigInteger.subtract:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    59: astore          xMax
        //    61: aload_2         /* p */
        //    62: invokevirtual   java/math/BigInteger.bitLength:()I
        //    65: istore          xLen
        //    67: new             Ljava/math/BigInteger;
        //    70: dup            
        //    71: iload           xLen
        //    73: aload_0         /* this */
        //    74: getfield        cryptix/jce/provider/elgamal/ElGamalKeyPairGenerator.random:Ljava/security/SecureRandom;
        //    77: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //    80: astore          7
        //    82: aload           7
        //    84: aload           xMin
        //    86: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    89: iconst_m1      
        //    90: if_icmpeq       67
        //    93: aload           7
        //    95: aload           xMax
        //    97: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //   100: iconst_1       
        //   101: if_icmpeq       67
        //   104: aload_3         /* g */
        //   105: aload           x
        //   107: aload_2         /* p */
        //   108: invokevirtual   java/math/BigInteger.modPow:(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   111: astore          y
        //   113: new             Lcryptix/jce/provider/elgamal/ElGamalPublicKeyCryptix;
        //   116: dup            
        //   117: aload           y
        //   119: aload_1         /* params */
        //   120: invokespecial   cryptix/jce/provider/elgamal/ElGamalPublicKeyCryptix.<init>:(Ljava/math/BigInteger;Lcryptix/jce/ElGamalParams;)V
        //   123: astore          pub
        //   125: new             Lcryptix/jce/provider/elgamal/ElGamalPrivateKeyCryptix;
        //   128: dup            
        //   129: aload           x
        //   131: aload_1         /* params */
        //   132: invokespecial   cryptix/jce/provider/elgamal/ElGamalPrivateKeyCryptix.<init>:(Ljava/math/BigInteger;Lcryptix/jce/ElGamalParams;)V
        //   135: astore          priv
        //   137: new             Ljava/security/KeyPair;
        //   140: dup            
        //   141: aload           pub
        //   143: aload           priv
        //   145: invokespecial   java/security/KeyPair.<init>:(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
        //   148: areturn        
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
        this.initialize(1536, new SecureRandom());
    }
    
    public ElGamalKeyPairGenerator() {
        this.initialized = false;
    }
    
    static {
        TWO = BigInteger.valueOf(2L);
    }
}
