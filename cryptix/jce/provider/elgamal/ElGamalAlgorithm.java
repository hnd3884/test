package cryptix.jce.provider.elgamal;

import java.math.BigInteger;

final class ElGamalAlgorithm
{
    private static BigInteger generateK(final BigInteger p) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokestatic    java/math/BigInteger.valueOf:(J)Ljava/math/BigInteger;
        //     4: astore_2        /* ONE */
        //     5: aload_0         /* p */
        //     6: aload_2         /* ONE */
        //     7: invokevirtual   java/math/BigInteger.subtract:(Ljava/math/BigInteger;)Ljava/math/BigInteger;
        //    10: astore_3        /* p_1 */
        //    11: new             Ljava/security/SecureRandom;
        //    14: dup            
        //    15: invokespecial   java/security/SecureRandom.<init>:()V
        //    18: astore          sr
        //    20: new             Ljava/math/BigInteger;
        //    23: dup            
        //    24: aload_0         /* p */
        //    25: invokevirtual   java/math/BigInteger.bitLength:()I
        //    28: aload           sr
        //    30: invokespecial   java/math/BigInteger.<init>:(ILjava/util/Random;)V
        //    33: astore_1       
        //    34: aload_1        
        //    35: aload_2         /* ONE */
        //    36: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    39: ifle            20
        //    42: aload_1        
        //    43: aload_3         /* p_1 */
        //    44: invokevirtual   java/math/BigInteger.compareTo:(Ljava/math/BigInteger;)I
        //    47: ifge            20
        //    50: aload_1         /* k */
        //    51: areturn        
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
    
    public static BigInteger[] encrypt(final BigInteger m, final BigInteger p, final BigInteger g, final BigInteger a) {
        final BigInteger k = generateK(p);
        try {
            return new BigInteger[] { g.modPow(k, p), a.modPow(k, p).multiply(m).mod(p) };
        }
        catch (final ArithmeticException e) {
            throw new RuntimeException("PANIC: Should not happend!!");
        }
    }
    
    public static BigInteger decrypt(final BigInteger[] bia, final BigInteger p, final BigInteger a) throws ArithmeticException {
        return bia[0].modPow(a, p).modInverse(p).multiply(bia[1]).mod(p);
    }
    
    private ElGamalAlgorithm() {
    }
}
