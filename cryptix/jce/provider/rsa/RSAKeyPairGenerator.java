package cryptix.jce.provider.rsa;

import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.KeyPairGeneratorSpi;

public final class RSAKeyPairGenerator extends KeyPairGeneratorSpi
{
    private static final BigInteger ONE;
    private static final BigInteger F4;
    private static final int KEYSIZE_MIN = 384;
    private static final int KEYSIZE_DEFAULT = 3072;
    private static final int KEYSIZE_MAX = 16384;
    private static final int CERTAINTY = 80;
    private int keysize;
    private BigInteger publicExponent;
    private SecureRandom random;
    private boolean initialized;
    
    public void initialize(final int keysize, final SecureRandom random) {
        if (keysize < 384 || keysize > 16384) {
            throw new IllegalArgumentException("keysize: invalid size (" + keysize + ")");
        }
        this.keysize = keysize;
        this.random = random;
        this.publicExponent = RSAKeyPairGenerator.F4;
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
        //     1: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.initialized:Z
        //     4: ifne            11
        //     7: aload_0         /* this */
        //     8: invokespecial   cryptix/jce/provider/rsa/RSAKeyPairGenerator.initialize:()V
        //    11: aload_0         /* this */
        //    12: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.keysize:I
        //    15: iconst_2       
        //    16: idiv           
        //    17: istore_1        /* pLen */
        //    18: aload_0         /* this */
        //    19: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.keysize:I
        //    22: iload_1         /* pLen */
        //    23: isub           
        //    24: istore_2        /* qLen */
        //    25: aload_0         /* this */
        //    26: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.publicExponent:Ljava/math/BigInteger;
        //    29: astore          e
        //    31: goto            139
        //    34: new             Ljava/math/BigInteger;
        //    37: dup            
        //    38: iload_1         /* pLen */
        //    39: bipush          80
        //    41: aload_0         /* this */
        //    42: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.random:Ljava/security/SecureRandom;
        //    45: invokespecial   java/math/BigInteger.<init>:(IILjava/util/Random;)V
        //    48: astore          p
        //    50: new             Ljava/math/BigInteger;
        //    53: dup            
        //    54: iload_2         /* qLen */
        //    55: bipush          80
        //    57: aload_0         /* this */
        //    58: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.random:Ljava/security/SecureRandom;
        //    61: invokespecial   java/math/BigInteger.<init>:(IILjava/util/Random;)V
        //    64: astore          q
        //    66: aload           p
        //    68: aload           q
        //    70: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    73: astore          5
        //    75: aload           p
        //    77: aload           7
        //    79: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    82: ifeq            34
        //    85: aload           5
        //    87: invokevirtual   java/math/BigInteger.bitLength:()I
        //    90: aload_0         /* this */
        //    91: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.keysize:I
        //    94: if_icmpne       34
        //    97: aload           p
        //    99: getstatic       cryptix/jce/provider/rsa/RSAKeyPairGenerator.ONE:Ljava/math/BigInteger;
        //   102: invokevirtual   java/math/BigInteger.subtract:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   105: astore          pMinus1
        //   107: aload           q
        //   109: getstatic       cryptix/jce/provider/rsa/RSAKeyPairGenerator.ONE:Ljava/math/BigInteger;
        //   112: invokevirtual   java/math/BigInteger.subtract:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   115: astore          qMinus1
        //   117: aload           pMinus1
        //   119: aload           qMinus1
        //   121: invokevirtual   java/math/BigInteger.multiply:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   124: astore          phi
        //   126: aload           e
        //   128: aload           phi
        //   130: invokevirtual   java/math/BigInteger.modInverse:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   133: astore_3        /* d */
        //   134: goto            142
        //   137: astore          ae
        //   139: goto            34
        //   142: aload_3         /* d */
        //   143: aload           pMinus1
        //   145: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   148: astore          primeExponentP
        //   150: aload_3         /* d */
        //   151: aload           qMinus1
        //   153: invokevirtual   java/math/BigInteger.mod:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   156: astore          primeExponentQ
        //   158: aload           q
        //   160: aload           p
        //   162: invokevirtual   java/math/BigInteger.modInverse:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   165: astore          crtCoefficient
        //   167: new             Ljava/math/BigInteger;
        //   170: dup            
        //   171: iload_1         /* pLen */
        //   172: aload_0         /* this */
        //   173: getfield        cryptix/jce/provider/rsa/RSAKeyPairGenerator.random:Ljava/security/SecureRandom;
        //   176: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //   179: astore          x
        //   181: aload           x
        //   183: aload           n
        //   185: aload           e
        //   187: invokestatic    cryptix/jce/provider/rsa/RSAAlgorithm.rsa:(Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   190: astore          y
        //   192: aload           y
        //   194: aload           n
        //   196: aload_3         /* d */
        //   197: aload           p
        //   199: aload           q
        //   201: aload           primeExponentP
        //   203: aload           primeExponentQ
        //   205: aload           crtCoefficient
        //   207: invokestatic    cryptix/jce/provider/rsa/RSAAlgorithm.rsa:(Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //   210: astore          z
        //   212: aload           z
        //   214: aload           x
        //   216: invokevirtual   java/math/BigInteger.equals:(Ljava/lang/Object;)Z
        //   219: ifne            232
        //   222: new             Ljava/lang/RuntimeException;
        //   225: dup            
        //   226: ldc             "RSA KeyPair doesn't work"
        //   228: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //   231: athrow         
        //   232: new             Lcryptix/jce/provider/rsa/RSAPrivateCrtKeyCryptix;
        //   235: dup            
        //   236: aload           n
        //   238: aload           e
        //   240: aload_3         /* d */
        //   241: aload           p
        //   243: aload           q
        //   245: aload           primeExponentP
        //   247: aload           primeExponentQ
        //   249: aload           crtCoefficient
        //   251: invokespecial   cryptix/jce/provider/rsa/RSAPrivateCrtKeyCryptix.<init>:(Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;)V
        //   254: astore          priv
        //   256: new             Lcryptix/jce/provider/rsa/RSAPublicKeyCryptix;
        //   259: dup            
        //   260: aload           n
        //   262: aload           e
        //   264: invokespecial   cryptix/jce/provider/rsa/RSAPublicKeyCryptix.<init>:(Ljava/math/BigInteger;Ljava/math/BigInteger;)V
        //   267: astore          pub
        //   269: new             Ljava/security/KeyPair;
        //   272: dup            
        //   273: aload           pub
        //   275: aload           priv
        //   277: invokespecial   java/security/KeyPair.<init>:(Ljava/security/PublicKey;Ljava/security/PrivateKey;)V
        //   280: areturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                           
        //  -----  -----  -----  -----  -------------------------------
        //  34     137    137    139    Ljava/lang/ArithmeticException;
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
        this.initialize(3072, new SecureRandom());
    }
    
    public RSAKeyPairGenerator() {
        this.initialized = false;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
        F4 = BigInteger.valueOf(65537L);
    }
}
