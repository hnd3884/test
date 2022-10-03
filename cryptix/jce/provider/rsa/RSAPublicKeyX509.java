package cryptix.jce.provider.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

final class RSAPublicKeyX509 implements RSAPublicKey
{
    private final BigInteger n;
    private final BigInteger e;
    
    public BigInteger getModulus() {
        return this.n;
    }
    
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: anewarray       Lcryptix/jce/provider/asn/AsnObject;
        //     4: dup            
        //     5: iconst_0       
        //     6: new             Lcryptix/jce/provider/asn/AsnInteger;
        //     9: dup            
        //    10: aload_0         /* this */
        //    11: getfield        cryptix/jce/provider/rsa/RSAPublicKeyX509.n:Ljava/math/BigInteger;
        //    14: invokespecial   cryptix/jce/provider/asn/AsnInteger.<init>:(Ljava/math/BigInteger;)V
        //    17: aastore        
        //    18: dup            
        //    19: iconst_1       
        //    20: new             Lcryptix/jce/provider/asn/AsnInteger;
        //    23: dup            
        //    24: aload_0         /* this */
        //    25: getfield        cryptix/jce/provider/rsa/RSAPublicKeyX509.e:Ljava/math/BigInteger;
        //    28: invokespecial   cryptix/jce/provider/asn/AsnInteger.<init>:(Ljava/math/BigInteger;)V
        //    31: aastore        
        //    32: astore_1        /* spkData */
        //    33: new             Ljava/io/ByteArrayOutputStream;
        //    36: dup            
        //    37: invokespecial   java/io/ByteArrayOutputStream.<init>:()V
        //    40: astore_3        /* baos */
        //    41: new             Lcryptix/jce/provider/asn/AsnOutputStream;
        //    44: dup            
        //    45: aload_3         /* baos */
        //    46: invokespecial   cryptix/jce/provider/asn/AsnOutputStream.<init>:(Ljava/io/OutputStream;)V
        //    49: astore          dos
        //    51: aload           dos
        //    53: new             Lcryptix/jce/provider/asn/AsnSequence;
        //    56: dup            
        //    57: aload_1         /* spkData */
        //    58: invokespecial   cryptix/jce/provider/asn/AsnSequence.<init>:([Lcryptix/jce/provider/asn/AsnObject;)V
        //    61: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.write:(Lcryptix/jce/provider/asn/AsnObject;)V
        //    64: aload           dos
        //    66: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.flush:()V
        //    69: aload           dos
        //    71: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.close:()V
        //    74: aload_3         /* baos */
        //    75: invokevirtual   java/io/ByteArrayOutputStream.toByteArray:()[B
        //    78: astore_2       
        //    79: goto            93
        //    82: astore_3        /* e */
        //    83: new             Ljava/lang/RuntimeException;
        //    86: dup            
        //    87: ldc             "PANIC"
        //    89: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //    92: athrow         
        //    93: new             Lcryptix/jce/provider/asn/AsnBitString;
        //    96: dup            
        //    97: aload_2         /* spkBytes */
        //    98: invokespecial   cryptix/jce/provider/asn/AsnBitString.<init>:([B)V
        //   101: astore_3        /* subjectPublicKey */
        //   102: iconst_1       
        //   103: anewarray       Lcryptix/jce/provider/asn/AsnObject;
        //   106: dup            
        //   107: iconst_0       
        //   108: getstatic       cryptix/jce/provider/asn/AsnObjectId.OID_rsaEncryption:Lcryptix/jce/provider/asn/AsnObjectId;
        //   111: aastore        
        //   112: astore          algData
        //   114: new             Lcryptix/jce/provider/asn/AsnSequence;
        //   117: dup            
        //   118: aload           algData
        //   120: invokespecial   cryptix/jce/provider/asn/AsnSequence.<init>:([Lcryptix/jce/provider/asn/AsnObject;)V
        //   123: astore          algorithm
        //   125: iconst_2       
        //   126: anewarray       Lcryptix/jce/provider/asn/AsnObject;
        //   129: dup            
        //   130: iconst_0       
        //   131: aload           algorithm
        //   133: aastore        
        //   134: dup            
        //   135: iconst_1       
        //   136: aload_3         /* subjectPublicKey */
        //   137: aastore        
        //   138: astore          spkiData
        //   140: new             Lcryptix/jce/provider/asn/AsnSequence;
        //   143: dup            
        //   144: aload           spkiData
        //   146: invokespecial   cryptix/jce/provider/asn/AsnSequence.<init>:([Lcryptix/jce/provider/asn/AsnObject;)V
        //   149: astore          subjectPublicKeyInfo
        //   151: new             Ljava/io/ByteArrayOutputStream;
        //   154: dup            
        //   155: invokespecial   java/io/ByteArrayOutputStream.<init>:()V
        //   158: astore          8
        //   160: new             Lcryptix/jce/provider/asn/AsnOutputStream;
        //   163: dup            
        //   164: aload           8
        //   166: invokespecial   cryptix/jce/provider/asn/AsnOutputStream.<init>:(Ljava/io/OutputStream;)V
        //   169: astore          dos
        //   171: aload           dos
        //   173: aload           subjectPublicKeyInfo
        //   175: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.write:(Lcryptix/jce/provider/asn/AsnObject;)V
        //   178: aload           dos
        //   180: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.flush:()V
        //   183: aload           dos
        //   185: invokevirtual   cryptix/jce/provider/asn/AsnOutputStream.close:()V
        //   188: aload           8
        //   190: invokevirtual   java/io/ByteArrayOutputStream.toByteArray:()[B
        //   193: areturn        
        //   194: astore          e
        //   196: new             Ljava/lang/RuntimeException;
        //   199: dup            
        //   200: ldc             "PANIC"
        //   202: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
        //   205: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  33     79     82     93     Ljava/io/IOException;
        //  151    194    194    206    Ljava/io/IOException;
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
    
    RSAPublicKeyX509(final BigInteger n, final BigInteger e) {
        this.n = n;
        this.e = e;
    }
}
