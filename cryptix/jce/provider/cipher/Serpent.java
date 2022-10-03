package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class Serpent extends BlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private static final int ROUNDS = 32;
    private static final int PHI = -1640531527;
    private static final byte[][] Sbox;
    private boolean decrypt;
    private int[] K;
    
    protected void coreInit(final Key key, final boolean decrypt) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key: key is null");
        }
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("key: wrong format, RAW needed");
        }
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("RAW bytes missing");
        }
        final int len = userkey.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new InvalidKeyException("Invalid user key length");
        }
        this.generateSubKeys(userkey);
        this.decrypt = decrypt;
    }
    
    protected void coreCrypt(final byte[] in, final int inOffset, final byte[] out, final int outOffset) {
        if (this.decrypt) {
            this.blockDecrypt(in, inOffset, out, outOffset);
        }
        else {
            this.blockEncrypt(in, inOffset, out, outOffset);
        }
    }
    
    private final void generateSubKeys(final byte[] key) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: sipush          132
        //     4: newarray        I
        //     6: putfield        cryptix/jce/provider/cipher/Serpent.K:[I
        //     9: aload_0         /* this */
        //    10: getfield        cryptix/jce/provider/cipher/Serpent.K:[I
        //    13: astore_2        /* w */
        //    14: aload_1         /* key */
        //    15: arraylength    
        //    16: iconst_4       
        //    17: idiv           
        //    18: istore_3        /* limit */
        //    19: aload_1         /* key */
        //    20: arraylength    
        //    21: iconst_1       
        //    22: isub           
        //    23: istore          offset
        //    25: iconst_0       
        //    26: istore          5
        //    28: goto            94
        //    31: aload_2         /* w */
        //    32: iload           5
        //    34: aload_1         /* key */
        //    35: iload           offset
        //    37: iinc            offset, -1
        //    40: baload         
        //    41: sipush          255
        //    44: iand           
        //    45: aload_1         /* key */
        //    46: iload           offset
        //    48: iinc            offset, -1
        //    51: baload         
        //    52: sipush          255
        //    55: iand           
        //    56: bipush          8
        //    58: ishl           
        //    59: ior            
        //    60: aload_1         /* key */
        //    61: iload           offset
        //    63: iinc            offset, -1
        //    66: baload         
        //    67: sipush          255
        //    70: iand           
        //    71: bipush          16
        //    73: ishl           
        //    74: ior            
        //    75: aload_1         /* key */
        //    76: iload           offset
        //    78: iinc            offset, -1
        //    81: baload         
        //    82: sipush          255
        //    85: iand           
        //    86: bipush          24
        //    88: ishl           
        //    89: ior            
        //    90: iastore        
        //    91: iinc            5, 1
        //    94: iload           5
        //    96: iload_3         /* limit */
        //    97: if_icmplt       31
        //   100: iload           i
        //   102: bipush          8
        //   104: if_icmpge       115
        //   107: aload_2         /* w */
        //   108: iload           i
        //   110: iinc            i, 1
        //   113: iconst_1       
        //   114: iastore        
        //   115: bipush          8
        //   117: istore          i
        //   119: iconst_0       
        //   120: istore          6
        //   122: goto            179
        //   125: aload_2         /* w */
        //   126: iload           6
        //   128: iaload         
        //   129: aload_2         /* w */
        //   130: iload           i
        //   132: iconst_5       
        //   133: isub           
        //   134: iaload         
        //   135: ixor           
        //   136: aload_2         /* w */
        //   137: iload           i
        //   139: iconst_3       
        //   140: isub           
        //   141: iaload         
        //   142: ixor           
        //   143: aload_2         /* w */
        //   144: iload           i
        //   146: iconst_1       
        //   147: isub           
        //   148: iaload         
        //   149: ixor           
        //   150: ldc             -1640531527
        //   152: ixor           
        //   153: iload           6
        //   155: iinc            6, 1
        //   158: ixor           
        //   159: istore          t
        //   161: aload_2         /* w */
        //   162: iload           i
        //   164: iload           t
        //   166: bipush          11
        //   168: ishl           
        //   169: iload           t
        //   171: bipush          21
        //   173: iushr          
        //   174: ior            
        //   175: iastore        
        //   176: iinc            i, 1
        //   179: iload           i
        //   181: bipush          16
        //   183: if_icmplt       125
        //   186: iconst_0       
        //   187: istore          i
        //   189: bipush          8
        //   191: istore          j
        //   193: goto            210
        //   196: aload_2         /* w */
        //   197: iload           i
        //   199: iinc            i, 1
        //   202: aload_2         /* w */
        //   203: iload           j
        //   205: iinc            j, 1
        //   208: iaload         
        //   209: iastore        
        //   210: iload           i
        //   212: bipush          8
        //   214: if_icmplt       196
        //   217: sipush          132
        //   220: istore_3        /* limit */
        //   221: goto            278
        //   224: aload_2         /* w */
        //   225: iload           i
        //   227: bipush          8
        //   229: isub           
        //   230: iaload         
        //   231: aload_2         /* w */
        //   232: iload           i
        //   234: iconst_5       
        //   235: isub           
        //   236: iaload         
        //   237: ixor           
        //   238: aload_2         /* w */
        //   239: iload           i
        //   241: iconst_3       
        //   242: isub           
        //   243: iaload         
        //   244: ixor           
        //   245: aload_2         /* w */
        //   246: iload           i
        //   248: iconst_1       
        //   249: isub           
        //   250: iaload         
        //   251: ixor           
        //   252: ldc             -1640531527
        //   254: ixor           
        //   255: iload           i
        //   257: ixor           
        //   258: istore          t
        //   260: aload_2         /* w */
        //   261: iload           i
        //   263: iload           t
        //   265: bipush          11
        //   267: ishl           
        //   268: iload           t
        //   270: bipush          21
        //   272: iushr          
        //   273: ior            
        //   274: iastore        
        //   275: iinc            i, 1
        //   278: iload           i
        //   280: iload_3         /* limit */
        //   281: if_icmplt       224
        //   284: iconst_0       
        //   285: istore          i
        //   287: goto            507
        //   290: aload_2         /* w */
        //   291: iconst_4       
        //   292: iload           i
        //   294: imul           
        //   295: iaload         
        //   296: istore          x0
        //   298: aload_2         /* w */
        //   299: iconst_4       
        //   300: iload           i
        //   302: imul           
        //   303: iconst_1       
        //   304: iadd           
        //   305: iaload         
        //   306: istore          x1
        //   308: aload_2         /* w */
        //   309: iconst_4       
        //   310: iload           i
        //   312: imul           
        //   313: iconst_2       
        //   314: iadd           
        //   315: iaload         
        //   316: istore          x2
        //   318: aload_2         /* w */
        //   319: iconst_4       
        //   320: iload           i
        //   322: imul           
        //   323: iconst_3       
        //   324: iadd           
        //   325: iaload         
        //   326: istore          x3
        //   328: iconst_0       
        //   329: dup            
        //   330: istore          y3
        //   332: dup            
        //   333: istore          y2
        //   335: dup            
        //   336: istore          y1
        //   338: istore          y0
        //   340: getstatic       cryptix/jce/provider/cipher/Serpent.Sbox:[[B
        //   343: bipush          35
        //   345: iload           i
        //   347: isub           
        //   348: bipush          8
        //   350: irem           
        //   351: aaload         
        //   352: astore          sb
        //   354: iconst_0       
        //   355: istore          j
        //   357: goto            459
        //   360: aload           sb
        //   362: iload           x0
        //   364: iload           j
        //   366: iushr          
        //   367: iconst_1       
        //   368: iand           
        //   369: iload           x1
        //   371: iload           j
        //   373: iushr          
        //   374: iconst_1       
        //   375: iand           
        //   376: iconst_1       
        //   377: ishl           
        //   378: ior            
        //   379: iload           x2
        //   381: iload           j
        //   383: iushr          
        //   384: iconst_1       
        //   385: iand           
        //   386: iconst_2       
        //   387: ishl           
        //   388: ior            
        //   389: iload           x3
        //   391: iload           j
        //   393: iushr          
        //   394: iconst_1       
        //   395: iand           
        //   396: iconst_3       
        //   397: ishl           
        //   398: ior            
        //   399: baload         
        //   400: istore          z
        //   402: iload           y0
        //   404: iload           z
        //   406: iconst_1       
        //   407: iand           
        //   408: iload           j
        //   410: ishl           
        //   411: ior            
        //   412: istore          y0
        //   414: iload           y1
        //   416: iload           z
        //   418: iconst_1       
        //   419: iushr          
        //   420: iconst_1       
        //   421: iand           
        //   422: iload           j
        //   424: ishl           
        //   425: ior            
        //   426: istore          y1
        //   428: iload           y2
        //   430: iload           z
        //   432: iconst_2       
        //   433: iushr          
        //   434: iconst_1       
        //   435: iand           
        //   436: iload           j
        //   438: ishl           
        //   439: ior            
        //   440: istore          y2
        //   442: iload           y3
        //   444: iload           z
        //   446: iconst_3       
        //   447: iushr          
        //   448: iconst_1       
        //   449: iand           
        //   450: iload           j
        //   452: ishl           
        //   453: ior            
        //   454: istore          y3
        //   456: iinc            j, 1
        //   459: iload           j
        //   461: bipush          32
        //   463: if_icmplt       360
        //   466: aload_2         /* w */
        //   467: iconst_4       
        //   468: iload           i
        //   470: imul           
        //   471: iload           y0
        //   473: iastore        
        //   474: aload_2         /* w */
        //   475: iconst_4       
        //   476: iload           i
        //   478: imul           
        //   479: iconst_1       
        //   480: iadd           
        //   481: iload           y1
        //   483: iastore        
        //   484: aload_2         /* w */
        //   485: iconst_4       
        //   486: iload           i
        //   488: imul           
        //   489: iconst_2       
        //   490: iadd           
        //   491: iload           y2
        //   493: iastore        
        //   494: aload_2         /* w */
        //   495: iconst_4       
        //   496: iload           i
        //   498: imul           
        //   499: iconst_3       
        //   500: iadd           
        //   501: iload           y3
        //   503: iastore        
        //   504: iinc            i, 1
        //   507: iload           i
        //   509: bipush          33
        //   511: if_icmplt       290
        //   514: return         
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
    
    private final void blockEncrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int x3 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x4 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x5 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x6 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int y0 = 0;
        int y2 = 0;
        int y3 = 0;
        int y4 = 0;
        int idxK = 0;
        for (int i = 0; i < 4; ++i) {
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            int t01 = x5 ^ x4;
            int t2 = x6 | x3;
            int t3 = x6 ^ x5;
            y4 = (t2 ^ t01);
            int t4 = x4 | y4;
            int t5 = x6 ^ x3;
            int t6 = x5 | x4;
            int t7 = x3 & t4;
            int t8 = t3 & t6;
            y3 = (t8 ^ t7);
            int t9 = t8 & y3;
            int t10 = x4 ^ x3;
            int t11 = t6 ^ t9;
            int t12 = x5 & t5;
            int t13 = t5 ^ t11;
            y0 = ~t13;
            int t14 = y0 ^ t12;
            y2 = (t10 ^ t14);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 | x3);
            t2 = (x4 ^ x3);
            t3 = ~x5;
            int t15 = x6 ^ x4;
            t4 = (x6 | t3);
            t5 = (x3 & t15);
            t6 = (t01 & t2);
            t7 = (x5 | t5);
            y3 = (t2 ^ t4);
            int t16 = t6 ^ t7;
            t9 = (t01 ^ t16);
            t10 = (y3 ^ t9);
            t11 = (x5 & x3);
            y4 = ~t16;
            y2 = (t11 ^ t10);
            int t17 = t16 | y2;
            t14 = (t4 & t17);
            y0 = (x4 ^ t14);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 | x4);
            t2 = (x6 ^ x5);
            t3 = (x3 ^ t01);
            y0 = (t2 ^ t3);
            t4 = (x4 ^ y0);
            t5 = (x5 ^ t4);
            t6 = (x5 | t4);
            t7 = (t01 & t5);
            t8 = (t3 ^ t6);
            t16 = (t2 | t8);
            y2 = (t16 ^ t7);
            t10 = (x6 | x3);
            t11 = (t8 ^ y2);
            t12 = (x5 ^ t11);
            y4 = ~t8;
            y3 = (t10 ^ t12);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 ^ x4);
            t2 = (x6 | x3);
            t3 = (x6 & x3);
            t15 = (t01 & t2);
            t4 = (x5 | t3);
            t5 = (x6 & x5);
            t6 = (x3 ^ t15);
            t7 = (x4 | t5);
            t8 = (x5 ^ t6);
            t16 = (x3 & t4);
            t9 = (t2 ^ t16);
            y4 = (t7 ^ t8);
            t11 = (x3 | y4);
            t12 = (x6 | t6);
            t13 = (x5 & t11);
            y3 = (t7 ^ t9);
            y0 = (t12 ^ t13);
            y2 = (t4 ^ t15);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 | x5);
            t2 = (x5 | x4);
            t3 = (x6 ^ t2);
            t15 = (x5 ^ x3);
            t4 = (x3 | t3);
            t5 = (x3 & t01);
            y4 = (t3 ^ t5);
            t7 = (y4 & t15);
            t8 = (t15 & t4);
            t16 = (x4 ^ t5);
            t9 = (x5 & x4);
            t10 = (t15 ^ t7);
            t11 = (t9 | t3);
            t12 = (t16 ^ t8);
            t13 = (x6 & t4);
            t17 = (t9 | t10);
            y3 = (t11 ^ t7);
            y2 = (t13 ^ t17);
            y0 = ~t12;
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x5 ^ x3);
            t2 = (x5 | x3);
            t3 = (x6 & t01);
            t15 = (x4 ^ t2);
            t4 = (t3 ^ t15);
            y0 = ~t4;
            t6 = (x6 ^ t01);
            t7 = (x3 | y0);
            t8 = (x5 | t4);
            t16 = (x3 ^ t7);
            t9 = (x5 | t6);
            t10 = (t3 | y0);
            t11 = (t6 | t16);
            t12 = (t01 ^ t9);
            y3 = (t8 ^ t11);
            y2 = (t6 ^ t7);
            y4 = (t10 ^ t12);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 & x3);
            t2 = (x5 ^ x4);
            t3 = (x6 ^ x3);
            t15 = (t01 ^ t2);
            t4 = (x5 | x4);
            y2 = ~t15;
            t6 = (t3 & t4);
            t7 = (x5 & y2);
            t8 = (x6 | x4);
            t16 = (t6 ^ t7);
            t9 = (x5 | x3);
            t10 = (x4 ^ t9);
            t11 = (t8 ^ t16);
            y3 = ~t11;
            t13 = (y2 & t3);
            y4 = (t10 ^ t6);
            t14 = (x6 ^ x5);
            final int t18 = y3 ^ t13;
            y0 = (t14 ^ t18);
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
            x6 ^= this.K[idxK++];
            x5 ^= this.K[idxK++];
            x4 ^= this.K[idxK++];
            x3 ^= this.K[idxK++];
            t01 = (x6 & x4);
            t2 = ~x3;
            t3 = (x6 & t2);
            t15 = (x5 | t01);
            t4 = (x6 & x5);
            t5 = (x4 ^ t15);
            y4 = (t3 ^ t5);
            t7 = (x4 | y4);
            t8 = (x3 | t4);
            t16 = (x6 ^ t7);
            t9 = (t15 & y4);
            y2 = (t8 ^ t16);
            t11 = (x5 ^ y2);
            t12 = (t01 ^ y2);
            t13 = (x4 ^ t4);
            t17 = (t9 | t11);
            t14 = (t2 | t12);
            y0 = (t13 ^ t14);
            y3 = (x6 ^ t17);
            if (i == 3) {
                break;
            }
            x6 = (y0 << 13 | y0 >>> 19);
            x4 = (y3 << 3 | y3 >>> 29);
            x5 = (y2 ^ x6 ^ x4);
            x3 = (y4 ^ x4 ^ x6 << 3);
            x5 = (x5 << 1 | x5 >>> 31);
            x3 = (x3 << 7 | x3 >>> 25);
            x6 = (x6 ^ x5 ^ x3);
            x4 = (x4 ^ x3 ^ x5 << 7);
            x6 = (x6 << 5 | x6 >>> 27);
            x4 = (x4 << 22 | x4 >>> 10);
        }
        y0 ^= this.K[idxK++];
        y2 ^= this.K[idxK++];
        y3 ^= this.K[idxK++];
        y4 ^= this.K[idxK];
        out[outOffset++] = (byte)(y4 >>> 24);
        out[outOffset++] = (byte)(y4 >>> 16);
        out[outOffset++] = (byte)(y4 >>> 8);
        out[outOffset++] = (byte)y4;
        out[outOffset++] = (byte)(y3 >>> 24);
        out[outOffset++] = (byte)(y3 >>> 16);
        out[outOffset++] = (byte)(y3 >>> 8);
        out[outOffset++] = (byte)y3;
        out[outOffset++] = (byte)(y2 >>> 24);
        out[outOffset++] = (byte)(y2 >>> 16);
        out[outOffset++] = (byte)(y2 >>> 8);
        out[outOffset++] = (byte)y2;
        out[outOffset++] = (byte)(y0 >>> 24);
        out[outOffset++] = (byte)(y0 >>> 16);
        out[outOffset++] = (byte)(y0 >>> 8);
        out[outOffset] = (byte)y0;
    }
    
    private final void blockDecrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int x3 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x4 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x5 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int x6 = (in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF);
        int y0 = 0;
        int y2 = 0;
        int y3 = 0;
        int y4 = 0;
        int idxK = 131;
        x3 ^= this.K[idxK--];
        x4 ^= this.K[idxK--];
        x5 ^= this.K[idxK--];
        x6 ^= this.K[idxK--];
        for (int i = 0; i < 4; ++i) {
            int t01 = x6 & x5;
            int t2 = x6 | x5;
            int t3 = x4 | t01;
            int t4 = x3 & t2;
            y4 = (t3 ^ t4);
            int t5 = x5 ^ t4;
            int t6 = x3 ^ y4;
            int t7 = ~t6;
            int t8 = t5 | t7;
            int t9 = x5 ^ x3;
            int t10 = x6 | x3;
            y2 = (x6 ^ t8);
            int t11 = x4 ^ t5;
            int t12 = x4 & t10;
            int t13 = x3 | y2;
            int t14 = t01 | t9;
            y0 = (t11 ^ t13);
            y3 = (t12 ^ t14);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x6 ^ x4);
            t2 = ~x4;
            t3 = (x5 & t01);
            t4 = (x5 | t2);
            int t15 = x3 | t3;
            t5 = (x5 ^ x3);
            t6 = (x6 & t4);
            t7 = (x6 | t2);
            t8 = (t6 ^ t15);
            y2 = (t5 ^ t7);
            y0 = ~t8;
            int t16 = x5 & y0;
            t11 = (t01 & t15);
            t12 = (t01 ^ t16);
            t13 = (t6 ^ t11);
            t14 = (x3 | t2);
            int t17 = x6 ^ y2;
            y4 = (t17 ^ t13);
            y3 = (t14 ^ t12);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x6 & x3);
            t2 = (x4 ^ t01);
            t3 = (x6 ^ x3);
            t4 = (x5 & t2);
            t15 = (x6 & x4);
            y0 = (t3 ^ t4);
            t6 = (x6 & y0);
            t7 = (t01 ^ y0);
            t8 = (x5 | t15);
            t9 = ~x5;
            y2 = (t7 ^ t8);
            t16 = (t9 | t6);
            t11 = (y0 | y2);
            y4 = (t2 ^ t16);
            t13 = (t2 ^ t11);
            t14 = (x5 ^ x3);
            y3 = (t14 ^ t13);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x5 | x3);
            t2 = (x4 | x3);
            t3 = (x6 & t01);
            t4 = (x5 ^ t2);
            t15 = (x4 ^ x3);
            t5 = ~t3;
            t6 = (x6 & t4);
            y2 = (t15 ^ t6);
            t8 = (y2 | t5);
            t9 = (x6 ^ t6);
            t10 = (t01 ^ t8);
            t16 = (x3 ^ t4);
            t11 = (x4 | t9);
            y4 = (t3 ^ t16);
            t13 = (x6 ^ t4);
            y3 = (t10 ^ t11);
            y0 = (t13 ^ t8);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x4 | x3);
            t2 = (x6 | x3);
            t3 = (x4 ^ t2);
            t4 = (x5 ^ t2);
            t15 = (x6 ^ x3);
            t5 = (t4 & t3);
            t6 = (x5 & t01);
            y3 = (t15 ^ t5);
            t8 = (x6 ^ t3);
            y0 = (t6 ^ t3);
            t10 = (y0 | t15);
            t16 = (t8 & t10);
            t11 = (x6 & y3);
            t12 = (t01 ^ t15);
            y2 = (x5 ^ t16);
            t14 = (x5 | t11);
            y4 = (t12 ^ t14);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x6 ^ x3);
            t2 = (x4 ^ x3);
            t3 = (x6 & x4);
            t4 = (x5 | t2);
            y0 = (t01 ^ t4);
            t5 = (x6 | x4);
            t6 = (x3 | y0);
            t7 = ~x3;
            t8 = (x5 & t5);
            t9 = (t7 | t3);
            t10 = (x5 & t6);
            t16 = (t5 & t2);
            y4 = (t8 ^ t9);
            y2 = (t16 ^ t10);
            t13 = (x4 & y4);
            t14 = (y0 ^ y2);
            t17 = (t9 ^ t13);
            y3 = (t14 ^ t17);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x6 ^ x5);
            t2 = (x5 | x3);
            t3 = (x6 & x4);
            t4 = (x4 ^ t2);
            t15 = (x6 | t4);
            t5 = (t01 & t15);
            t6 = (x3 | t3);
            t7 = (x5 ^ t5);
            t8 = (t6 ^ t5);
            t9 = (t4 | t3);
            t10 = (x3 & t7);
            y3 = ~t8;
            y2 = (t9 ^ t10);
            t12 = (x6 | y3);
            t13 = (t5 ^ y2);
            y4 = (t01 ^ t4);
            t17 = (x4 ^ t13);
            y0 = (t12 ^ t17);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
            t01 = (x4 ^ x3);
            t2 = (x6 | x5);
            t3 = (x5 | x4);
            t4 = (x4 & t01);
            t15 = (t2 ^ t01);
            t5 = (x6 | t4);
            y3 = ~t15;
            t7 = (x5 ^ x3);
            t8 = (t3 & t7);
            t9 = (x3 | y3);
            y2 = (t8 ^ t5);
            t16 = (x6 | t15);
            t11 = (y2 ^ t16);
            t12 = (t3 ^ t9);
            t13 = (x6 ^ x4);
            y4 = (t12 ^ t11);
            t17 = (t15 & t11);
            final int t18 = t12 | t17;
            y0 = (t13 ^ t18);
            y4 ^= this.K[idxK--];
            y3 ^= this.K[idxK--];
            y2 ^= this.K[idxK--];
            y0 ^= this.K[idxK--];
            if (i == 3) {
                break;
            }
            x4 = (y3 << 10 | y3 >>> 22);
            x6 = (y0 << 27 | y0 >>> 5);
            x4 = (x4 ^ y4 ^ y2 << 7);
            x6 = (x6 ^ y2 ^ y4);
            x3 = (y4 << 25 | y4 >>> 7);
            x5 = (y2 << 31 | y2 >>> 1);
            x3 = (x3 ^ x4 ^ x6 << 3);
            x5 = (x5 ^ x6 ^ x4);
            x4 = (x4 << 29 | x4 >>> 3);
            x6 = (x6 << 19 | x6 >>> 13);
        }
        out[outOffset++] = (byte)(y4 >>> 24);
        out[outOffset++] = (byte)(y4 >>> 16);
        out[outOffset++] = (byte)(y4 >>> 8);
        out[outOffset++] = (byte)y4;
        out[outOffset++] = (byte)(y3 >>> 24);
        out[outOffset++] = (byte)(y3 >>> 16);
        out[outOffset++] = (byte)(y3 >>> 8);
        out[outOffset++] = (byte)y3;
        out[outOffset++] = (byte)(y2 >>> 24);
        out[outOffset++] = (byte)(y2 >>> 16);
        out[outOffset++] = (byte)(y2 >>> 8);
        out[outOffset++] = (byte)y2;
        out[outOffset++] = (byte)(y0 >>> 24);
        out[outOffset++] = (byte)(y0 >>> 16);
        out[outOffset++] = (byte)(y0 >>> 8);
        out[outOffset] = (byte)y0;
    }
    
    public Serpent() {
        super(16);
        this.K = new int[132];
    }
    
    static {
        Sbox = new byte[8][16];
        final String SBOX_INIT = "8\u00f1¦[\u00edBp\u009c\u00fc'\u0090Z\u001b\u00e8m4\u0086y<¯\u00d1\u00e4\u000bR\u000f¸\u00c9c\u00d1$§^\u001f\u0083\u00c0¶%J\u009e}\u00f5+J\u009c\u0003\u00e8\u00d6qr\u00c5\u0084k\u00e9\u001f\u00d3 \u001d\u00f0\u00e8+t\u00ca\u0093V";
        for (int i = 0; i < 8; ++i) {
            int ci;
            for (int j = 0; j < 16; ++j, Serpent.Sbox[i][j] = (byte)(SBOX_INIT.charAt(ci) & '\u000f'), ++j) {
                ci = i * 8 + j / 2;
                Serpent.Sbox[i][j] = (byte)(SBOX_INIT.charAt(ci) >>> 4);
            }
        }
    }
}
