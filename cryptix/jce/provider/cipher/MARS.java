package cryptix.jce.provider.cipher;

import java.security.InvalidKeyException;
import java.security.Key;

public final class MARS extends BlockCipher
{
    static final int BLOCK_SIZE = 16;
    static final int ROUNDS = 32;
    private static final int[] S;
    private boolean decrypt;
    private final int[] K;
    
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
        //     1: arraylength    
        //     2: istore_2        /* keyLen */
        //     3: iload_2         /* keyLen */
        //     4: iconst_4       
        //     5: idiv           
        //     6: istore_3        /* n */
        //     7: aload_0         /* this */
        //     8: getfield        cryptix/jce/provider/cipher/MARS.K:[I
        //    11: astore          K
        //    13: bipush          15
        //    15: newarray        I
        //    17: astore          T
        //    19: iconst_4       
        //    20: newarray        I
        //    22: dup            
        //    23: iconst_0       
        //    24: ldc             -1532439173
        //    26: iastore        
        //    27: dup            
        //    28: iconst_1       
        //    29: ldc             1532827963
        //    31: iastore        
        //    32: dup            
        //    33: iconst_2       
        //    34: ldc             -928501605
        //    36: iastore        
        //    37: dup            
        //    38: iconst_3       
        //    39: ldc             1945741688
        //    41: iastore        
        //    42: astore          B
        //    44: iconst_0       
        //    45: istore          7
        //    47: goto            77
        //    50: aload           T
        //    52: iload           7
        //    54: iconst_4       
        //    55: idiv           
        //    56: dup2           
        //    57: iaload         
        //    58: aload_1         /* key */
        //    59: iload           7
        //    61: baload         
        //    62: sipush          255
        //    65: iand           
        //    66: iload           7
        //    68: bipush          8
        //    70: imul           
        //    71: ishl           
        //    72: ior            
        //    73: iastore        
        //    74: iinc            7, 1
        //    77: iload           7
        //    79: iload_2         /* keyLen */
        //    80: if_icmplt       50
        //    83: aload           T
        //    85: iload           i
        //    87: iconst_4       
        //    88: idiv           
        //    89: iload           i
        //    91: iconst_4       
        //    92: idiv           
        //    93: iastore        
        //    94: iconst_0       
        //    95: istore          8
        //    97: goto            265
        //   100: iconst_0       
        //   101: istore          i
        //   103: goto            152
        //   106: aload           T
        //   108: iload           i
        //   110: dup2           
        //   111: iaload         
        //   112: aload           T
        //   114: iload           i
        //   116: bipush          8
        //   118: iadd           
        //   119: bipush          15
        //   121: irem           
        //   122: iaload         
        //   123: aload           T
        //   125: iload           i
        //   127: bipush          13
        //   129: iadd           
        //   130: bipush          15
        //   132: irem           
        //   133: iaload         
        //   134: ixor           
        //   135: iconst_3       
        //   136: invokestatic    cryptix/jce/provider/cipher/MARS.rotl:(II)I
        //   139: iconst_4       
        //   140: iload           i
        //   142: imul           
        //   143: iload           8
        //   145: iadd           
        //   146: ixor           
        //   147: ixor           
        //   148: iastore        
        //   149: iinc            i, 1
        //   152: iload           i
        //   154: bipush          15
        //   156: if_icmplt       106
        //   159: iconst_0       
        //   160: istore          9
        //   162: goto            219
        //   165: iconst_0       
        //   166: istore          i
        //   168: goto            209
        //   171: aload           T
        //   173: iload           i
        //   175: aload           T
        //   177: iload           i
        //   179: iaload         
        //   180: getstatic       cryptix/jce/provider/cipher/MARS.S:[I
        //   183: aload           T
        //   185: iload           i
        //   187: bipush          14
        //   189: iadd           
        //   190: bipush          15
        //   192: irem           
        //   193: iaload         
        //   194: sipush          511
        //   197: iand           
        //   198: iaload         
        //   199: iadd           
        //   200: bipush          9
        //   202: invokestatic    cryptix/jce/provider/cipher/MARS.rotl:(II)I
        //   205: iastore        
        //   206: iinc            i, 1
        //   209: iload           i
        //   211: bipush          15
        //   213: if_icmplt       171
        //   216: iinc            9, 1
        //   219: iload           9
        //   221: iconst_4       
        //   222: if_icmplt       165
        //   225: iconst_0       
        //   226: istore          i
        //   228: goto            255
        //   231: aload           K
        //   233: bipush          10
        //   235: iload           8
        //   237: imul           
        //   238: iload           i
        //   240: iadd           
        //   241: aload           T
        //   243: iconst_4       
        //   244: iload           i
        //   246: imul           
        //   247: bipush          15
        //   249: irem           
        //   250: iaload         
        //   251: iastore        
        //   252: iinc            i, 1
        //   255: iload           i
        //   257: bipush          10
        //   259: if_icmplt       231
        //   262: iinc            8, 1
        //   265: iload           8
        //   267: iconst_4       
        //   268: if_icmplt       100
        //   271: iconst_5       
        //   272: istore          i
        //   274: goto            342
        //   277: aload           K
        //   279: iload           i
        //   281: iaload         
        //   282: iconst_3       
        //   283: iand           
        //   284: istore          j
        //   286: aload           K
        //   288: iload           i
        //   290: iaload         
        //   291: iconst_3       
        //   292: ior            
        //   293: istore          w
        //   295: iload           w
        //   297: invokestatic    cryptix/jce/provider/cipher/MARS.maskFrom:(I)I
        //   300: istore          m
        //   302: aload           K
        //   304: iload           i
        //   306: iconst_1       
        //   307: isub           
        //   308: iaload         
        //   309: bipush          31
        //   311: iand           
        //   312: istore          r
        //   314: aload           B
        //   316: iload           j
        //   318: iaload         
        //   319: iload           r
        //   321: invokestatic    cryptix/jce/provider/cipher/MARS.rotl:(II)I
        //   324: istore          p
        //   326: aload           K
        //   328: iload           i
        //   330: iload           w
        //   332: iload           p
        //   334: iload           m
        //   336: iand           
        //   337: ixor           
        //   338: iastore        
        //   339: iinc            i, 2
        //   342: iload           i
        //   344: bipush          35
        //   346: if_icmple       277
        //   349: return         
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
    
    private static int maskFrom(final int x) {
        int m = (~x ^ x >>> 1) & Integer.MAX_VALUE;
        m &= (m >>> 1 & m >>> 2);
        m &= (m >>> 3 & m >>> 6);
        m <<= 1;
        m |= m << 1;
        m |= m << 2;
        m |= m << 4;
        return m & 0xFFFFFFFC;
    }
    
    private static int rotl(final int arg, final int amount) {
        return arg << amount | arg >>> 32 - amount;
    }
    
    private final void blockEncrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int D0 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D2 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D3 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D4 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset] & 0xFF) << 24;
        D0 += this.K[0];
        D2 += this.K[1];
        D3 += this.K[2];
        D4 += this.K[3];
        for (int j = 0; j < 8; ++j) {
            D2 ^= MARS.S[D0 & 0xFF];
            D2 += MARS.S[256 + (D0 >>> 8 & 0xFF)];
            D3 += MARS.S[D0 >>> 16 & 0xFF];
            D4 ^= MARS.S[256 + (D0 >>> 24 & 0xFF)];
            D0 = (D0 >>> 24 | D0 << 8);
            switch (j) {
                case 0:
                case 4: {
                    D0 += D4;
                    break;
                }
                case 1:
                case 5: {
                    D0 += D2;
                    break;
                }
            }
            final int t = D0;
            D0 = D2;
            D2 = D3;
            D3 = D4;
            D4 = t;
        }
        for (int i = 0; i < 16; ++i) {
            final int[] ia = E(D0, this.K[2 * i + 4], this.K[2 * i + 5]);
            D0 = (D0 << 13 | D0 >>> 19);
            D3 += ia[1];
            if (i < 8) {
                D2 += ia[0];
                D4 ^= ia[2];
            }
            else {
                D4 += ia[0];
                D2 ^= ia[2];
            }
            final int t = D0;
            D0 = D2;
            D2 = D3;
            D3 = D4;
            D4 = t;
        }
        for (int i = 0; i < 8; ++i) {
            switch (i) {
                case 2:
                case 6: {
                    D0 -= D4;
                    break;
                }
                case 3:
                case 7: {
                    D0 -= D2;
                    break;
                }
            }
            D2 ^= MARS.S[256 + (D0 & 0xFF)];
            D3 -= MARS.S[D0 >>> 24 & 0xFF];
            D4 -= MARS.S[256 + (D0 >>> 16 & 0xFF)];
            D4 ^= MARS.S[D0 >>> 8 & 0xFF];
            final int t;
            D0 = (t = (D0 << 24 | D0 >>> 8));
            D0 = D2;
            D2 = D3;
            D3 = D4;
            D4 = t;
        }
        D0 -= this.K[36];
        D2 -= this.K[37];
        D3 -= this.K[38];
        D4 -= this.K[39];
        out[outOffset++] = (byte)D0;
        out[outOffset++] = (byte)(D0 >>> 8);
        out[outOffset++] = (byte)(D0 >>> 16);
        out[outOffset++] = (byte)(D0 >>> 24);
        out[outOffset++] = (byte)D2;
        out[outOffset++] = (byte)(D2 >>> 8);
        out[outOffset++] = (byte)(D2 >>> 16);
        out[outOffset++] = (byte)(D2 >>> 24);
        out[outOffset++] = (byte)D3;
        out[outOffset++] = (byte)(D3 >>> 8);
        out[outOffset++] = (byte)(D3 >>> 16);
        out[outOffset++] = (byte)(D3 >>> 24);
        out[outOffset++] = (byte)D4;
        out[outOffset++] = (byte)(D4 >>> 8);
        out[outOffset++] = (byte)(D4 >>> 16);
        out[outOffset] = (byte)(D4 >>> 24);
    }
    
    private final void blockDecrypt(final byte[] in, int inOffset, final byte[] out, int outOffset) {
        int D0 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D2 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D3 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
        int D4 = (in[inOffset++] & 0xFF) | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset] & 0xFF) << 24;
        D0 += this.K[36];
        D2 += this.K[37];
        D3 += this.K[38];
        D4 += this.K[39];
        for (int j = 7; j >= 0; --j) {
            final int t = D4;
            D4 = D3;
            D3 = D2;
            D2 = D0;
            D0 = t;
            D0 = (D0 >>> 24 | D0 << 8);
            D4 ^= MARS.S[D0 >>> 8 & 0xFF];
            D4 += MARS.S[256 + (D0 >>> 16 & 0xFF)];
            D3 += MARS.S[D0 >>> 24 & 0xFF];
            D2 ^= MARS.S[256 + (D0 & 0xFF)];
            switch (j) {
                case 2:
                case 6: {
                    D0 += D4;
                    break;
                }
                case 3:
                case 7: {
                    D0 += D2;
                    break;
                }
            }
        }
        for (int i = 15; i >= 0; --i) {
            final int t = D4;
            D4 = D3;
            D3 = D2;
            D2 = D0;
            D0 = t;
            D0 = (D0 >>> 13 | D0 << 19);
            final int[] ia = E(D0, this.K[2 * i + 4], this.K[2 * i + 5]);
            D3 -= ia[1];
            if (i < 8) {
                D2 -= ia[0];
                D4 ^= ia[2];
            }
            else {
                D4 -= ia[0];
                D2 ^= ia[2];
            }
        }
        for (int i = 7; i >= 0; --i) {
            final int t = D4;
            D4 = D3;
            D3 = D2;
            D2 = D0;
            D0 = t;
            switch (i) {
                case 0:
                case 4: {
                    D0 -= D4;
                    break;
                }
                case 1:
                case 5: {
                    D0 -= D2;
                    break;
                }
            }
            D0 = (D0 << 24 | D0 >>> 8);
            D4 ^= MARS.S[256 + (D0 >>> 24 & 0xFF)];
            D3 -= MARS.S[D0 >>> 16 & 0xFF];
            D2 -= MARS.S[256 + (D0 >>> 8 & 0xFF)];
            D2 ^= MARS.S[D0 & 0xFF];
        }
        D0 -= this.K[0];
        D2 -= this.K[1];
        D3 -= this.K[2];
        D4 -= this.K[3];
        out[outOffset++] = (byte)D0;
        out[outOffset++] = (byte)(D0 >>> 8);
        out[outOffset++] = (byte)(D0 >>> 16);
        out[outOffset++] = (byte)(D0 >>> 24);
        out[outOffset++] = (byte)D2;
        out[outOffset++] = (byte)(D2 >>> 8);
        out[outOffset++] = (byte)(D2 >>> 16);
        out[outOffset++] = (byte)(D2 >>> 24);
        out[outOffset++] = (byte)D3;
        out[outOffset++] = (byte)(D3 >>> 8);
        out[outOffset++] = (byte)(D3 >>> 16);
        out[outOffset++] = (byte)(D3 >>> 24);
        out[outOffset++] = (byte)D4;
        out[outOffset++] = (byte)(D4 >>> 8);
        out[outOffset++] = (byte)(D4 >>> 16);
        out[outOffset] = (byte)(D4 >>> 24);
    }
    
    private static int[] E(final int in, final int key1, final int key2) {
        int M = in + key1;
        int R = (in << 13 | in >>> 19) * key2;
        final int i = M & 0x1FF;
        int L = MARS.S[i];
        R = (R << 5 | R >>> 27);
        int r = R & 0x1F;
        M = (M << r | M >>> 32 - r);
        L ^= R;
        R = (R << 5 | R >>> 27);
        L ^= R;
        r = (R & 0x1F);
        L = (L << r | L >>> 32 - r);
        return new int[] { L, M, R };
    }
    
    public MARS() {
        super(16);
        this.K = new int[40];
    }
    
    static {
        S = new int[] { 164676729, 684261344, -2069205959, -1649577337, 2113903587, -735673503, -915562028, 2037697683, -2049943506, 709580549, 480340578, -1011013731, 253699557, 1365260079, -963264005, 1300230628, -1369478156, 225635910, -14426486, -1311797629, -246873374, 1050156610, -1946861898, 2135685292, -2090655869, 630653445, 1991239556, 981021140, 1334076496, 1550107638, 554327832, -963089882, 687138854, 979413020, -750737820, 2124947652, 1382451141, 2128466219, 849419549, -1667305338, -2131302351, -1418787667, 1459329875, -1959917220, -1232775506, -769324275, 692746017, -501239213, -1374460087, -399855994, -1825156860, -1723839898, 2024244444, -1231312821, 67397523, 601578526, 1187701206, 803373364, 1512192322, 409193819, -1047476509, 132102214, 1857587222, 755878986, -1530089895, 932734733, -872770413, 1330126149, -352547672, -619632170, -1337680352, 257165307, 1634195880, -772515997, 1303025347, 1005345112, -1414815724, -1228090879, 953558943, 40378901, -1892193067, 153887614, -1077582445, 847813932, -2058666347, 96181059, 2110610893, -1607560596, -85645339, 916570928, 873652654, -229116830, 1011817841, 815982601, 1759901009, -1671316924, 1575815864, 1976437192, -1772816066, 1770785994, 607957988, 721824663, 255561118, 14700767, -60989082, -480275832, -1065790195, 93433576, -1907155340, 1979594104, 795706010, -167042642, -1779897459, 1721321579, -1877115493, -43160015, 277916911, -530117160, -625809774, -848477339, -449235692, 989021680, 1648491597, 1175298979, 2077177641, -1947086368, 346734704, 361229653, 989691198, -755851775, 698995190, -273609645, -818182385, -1273719972, 1715758573, 38456007, 1504134337, 489240231, -598209882, -816362328, 67795472, 1826101255, -1969701300, -1395744669, -1019309387, -773791683, -1289596546, 546487571, 946547786, 1390992728, 1483042299, 1350697841, 1098979478, -482873474, -744509159, -914420234, 1761517595, -1588549915, 1428523362, -344984549, -674640518, -1508258402, -1125083786, 645054579, -268174532, 1253549323, 344199786, 1216063921, -1504720442, -157975223, 951085685, -578789427, 1674613967, -182675042, 447009603, -1203154284, -828404071, -1073948816, -943557172, 931419047, 2065808947, 964641213, 1318375729, -1832116328, 1494608465, -1711775305, -912782712, 494206303, -1331390984, 1632497360, -1250455830, 1497463085, -91176456, 871900340, -996781198, 1073141072, 1285947072, -2043614876, 1530903510, 2107796040, -1308417254, 69825812, 761500422, 783364425, 379867762, 1394891168, -1906358158, -110704679, 118656448, 222616418, -1345792723, 103899441, -667359282, 465837312, 976129039, -360479874, -1182502019, 330975377, -990332599, -1495880525, -1575629362, -2091285621, 1808902705, -176562862, 565788563, -183015551, 410910174, -380966026, 725155156, 1126031834, -1422309339, -1697446769, -538299734, 1704227811, 1648326755, -214668199, -1422215547, 860268811, 1800815678, -958529032, -1925046336, -1693527796, -1997430295, 1421996073, 2112542075, -1921276890, 1291130762, 1427799242, 446324488, -53063239, 627069314, -518011197, -1224894858, 863711271, -1211569388, -1637887952, 1800921940, -1382990601, 2115397773, 1485253225, 741328863, -477508410, 812879345, 136619828, -512132469, -1532439173, 1532827963, -928501605, 1945741688, 1933151538, 257513278, -371250429, -391792952, -2071132412, -1730178110, 1913265603, 1750017434, -1808029624, -1506344622, -2042929501, -780429429, 1838897391, 175115732, -1489160513, -1905653719, -2037007340, -480562105, -1824864920, -2003521502, 793844775, -570700740, 6709942, 293372926, 1309848596, -1027823770, 976219152, -195681248, 1434005034, 1190516823, -824564274, -1017111237, 1811983174, -273900504, -1279045561, 1629355747, 628896263, -36338558, 991221839, 599706468, -1602882653, 143625901, 128840024, 2006357052, -87376067, -1063833395, -144205463, -633017875, 746933266, 898850723, 788888991, 1762010360, 481344253, 2064498118, -2129714245, -91055590, 1838586391, 1732417435, 1957461253, 17720772, -2036082333, -116935264, -805033005, 361594115, 679117397, -1146603921, 158260275, 563178107, 2008378246, -1793711367, -1496979888, -1930815535, -846437700, -1546243021, 898263293, 261856572, -693491365, -30197686, -1073712242, -848827601, -487887422, -1758229758, 113898054, 1100804525, 730054725, 925102474, -883216247, 369879767, 1565083222, 905488203, 508456094, 235965, 1732667520, -1273542607, -1393286222, -897492301, 1516475879, 808085445, -1948564373, 277843876, 270679770, -1833388917, 2134429934, -1423506988, 40031253, -1356707280, -1277654342, -122502225, -621285520, -862465097, -379499668, 732872692, 1895204815, 946639190, -838258971, 32013734, 1827217002, -1149514620, -946721760, -1657048579, 101597638, -682029291, 1308867399, 406591886, 1676587584, 769389722, 1834793556, -1841868625, -102677962, 2016941611, 1500671090, -2118752416, -1154931007, 1218498061, -1497347731, -1388097413, 1905084778, -1645193297, 1152499174, 88299228, -562238155, -719711349, 1644727497, 897778449, -915473246, 1752139500, -1904751768, 1675744952, -938502280, 2042925565, 457992178, 1919520125, 1580633137, -136749522, -1579988673, -589741250, -1989208750, 1271188090, -1496204556, -1515622964, 200248134, -1586889305, 1960788151, 1310754820, -1703582201, 59672520, 539041348, -1954884417, -968868043, 407429997, -2135052232, 509774108, 1681727751, -1090201864, 554249356, -163253367, 125321294, 2071506360, -1564136109, 1108703934, -1574617554, 2079585454, -2131389191, -1791912729, 2011927277, -1283364560, -628255946, -1086032791, -227699581, -294670635, 1899434549, -566075533, -1260019901, 2109615438, 758624645, 1239194211, -1732010600, 318884258, 949689279, 202922125, -1541291590, 2057537116, 1910539608, 1012726954, 2099485860, 43506141, -673437648, 1261928491, 1948822356, -1360133305, 1262467973, 1764699912, 323901326, 920248511, -1366597681, -306626865, 723985550, 386839023, 2110095318, 511053590, -1324128767, -2041701477, -672500967, 985166269, -811280529, -484714622, 230601833, -1413914527, 1399912541, -5210569, -634334981, -339118154, 194556943, 978128870, -548421013, 362607146, -1030170910, 729345898, 1638484672, -1420422777, 351183088, -552779420, 430928110 };
    }
}
