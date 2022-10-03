package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.TweakableBlockCipherParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class ThreefishEngine implements BlockCipher
{
    public static final int BLOCKSIZE_256 = 256;
    public static final int BLOCKSIZE_512 = 512;
    public static final int BLOCKSIZE_1024 = 1024;
    private static final int TWEAK_SIZE_BYTES = 16;
    private static final int TWEAK_SIZE_WORDS = 2;
    private static final int ROUNDS_256 = 72;
    private static final int ROUNDS_512 = 72;
    private static final int ROUNDS_1024 = 80;
    private static final int MAX_ROUNDS = 80;
    private static final long C_240 = 2004413935125273122L;
    private static int[] MOD9;
    private static int[] MOD17;
    private static int[] MOD5;
    private static int[] MOD3;
    private int blocksizeBytes;
    private int blocksizeWords;
    private long[] currentBlock;
    private long[] t;
    private long[] kw;
    private ThreefishCipher cipher;
    private boolean forEncryption;
    
    public ThreefishEngine(final int n) {
        this.t = new long[5];
        this.blocksizeBytes = n / 8;
        this.blocksizeWords = this.blocksizeBytes / 8;
        this.currentBlock = new long[this.blocksizeWords];
        this.kw = new long[2 * this.blocksizeWords + 1];
        switch (n) {
            case 256: {
                this.cipher = new Threefish256Cipher(this.kw, this.t);
                break;
            }
            case 512: {
                this.cipher = new Threefish512Cipher(this.kw, this.t);
                break;
            }
            case 1024: {
                this.cipher = new Threefish1024Cipher(this.kw, this.t);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
            }
        }
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] array;
        byte[] tweak;
        if (cipherParameters instanceof TweakableBlockCipherParameters) {
            final TweakableBlockCipherParameters tweakableBlockCipherParameters = (TweakableBlockCipherParameters)cipherParameters;
            array = tweakableBlockCipherParameters.getKey().getKey();
            tweak = tweakableBlockCipherParameters.getTweak();
        }
        else {
            if (!(cipherParameters instanceof KeyParameter)) {
                throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + cipherParameters.getClass().getName());
            }
            array = ((KeyParameter)cipherParameters).getKey();
            tweak = null;
        }
        long[] array2 = null;
        long[] array3 = null;
        if (array != null) {
            if (array.length != this.blocksizeBytes) {
                throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)");
            }
            array2 = new long[this.blocksizeWords];
            for (int i = 0; i < array2.length; ++i) {
                array2[i] = bytesToWord(array, i * 8);
            }
        }
        if (tweak != null) {
            if (tweak.length != 16) {
                throw new IllegalArgumentException("Threefish tweak must be 16 bytes");
            }
            array3 = new long[] { bytesToWord(tweak, 0), bytesToWord(tweak, 8) };
        }
        this.init(b, array2, array3);
    }
    
    public void init(final boolean forEncryption, final long[] key, final long[] tweak) {
        this.forEncryption = forEncryption;
        if (key != null) {
            this.setKey(key);
        }
        if (tweak != null) {
            this.setTweak(tweak);
        }
    }
    
    private void setKey(final long[] array) {
        if (array.length != this.blocksizeWords) {
            throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)");
        }
        long n = 2004413935125273122L;
        for (int i = 0; i < this.blocksizeWords; ++i) {
            this.kw[i] = array[i];
            n ^= this.kw[i];
        }
        this.kw[this.blocksizeWords] = n;
        System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
    }
    
    private void setTweak(final long[] array) {
        if (array.length != 2) {
            throw new IllegalArgumentException("Tweak must be 2 words.");
        }
        this.t[0] = array[0];
        this.t[1] = array[1];
        this.t[2] = (this.t[0] ^ this.t[1]);
        this.t[3] = this.t[0];
        this.t[4] = this.t[1];
    }
    
    public String getAlgorithmName() {
        return "Threefish-" + this.blocksizeBytes * 8;
    }
    
    public int getBlockSize() {
        return this.blocksizeBytes;
    }
    
    public void reset() {
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blocksizeBytes > array.length) {
            throw new DataLengthException("Input buffer too short");
        }
        if (n2 + this.blocksizeBytes > array2.length) {
            throw new OutputLengthException("Output buffer too short");
        }
        for (int i = 0; i < this.blocksizeBytes; i += 8) {
            this.currentBlock[i >> 3] = bytesToWord(array, n + i);
        }
        this.processBlock(this.currentBlock, this.currentBlock);
        for (int j = 0; j < this.blocksizeBytes; j += 8) {
            wordToBytes(this.currentBlock[j >> 3], array2, n2 + j);
        }
        return this.blocksizeBytes;
    }
    
    public int processBlock(final long[] array, final long[] array2) throws DataLengthException, IllegalStateException {
        if (this.kw[this.blocksizeWords] == 0L) {
            throw new IllegalStateException("Threefish engine not initialised");
        }
        if (array.length != this.blocksizeWords) {
            throw new DataLengthException("Input buffer too short");
        }
        if (array2.length != this.blocksizeWords) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            this.cipher.encryptBlock(array, array2);
        }
        else {
            this.cipher.decryptBlock(array, array2);
        }
        return this.blocksizeWords;
    }
    
    public static long bytesToWord(final byte[] array, final int n) {
        if (n + 8 > array.length) {
            throw new IllegalArgumentException();
        }
        int n2 = n;
        return ((long)array[n2++] & 0xFFL) | ((long)array[n2++] & 0xFFL) << 8 | ((long)array[n2++] & 0xFFL) << 16 | ((long)array[n2++] & 0xFFL) << 24 | ((long)array[n2++] & 0xFFL) << 32 | ((long)array[n2++] & 0xFFL) << 40 | ((long)array[n2++] & 0xFFL) << 48 | ((long)array[n2++] & 0xFFL) << 56;
    }
    
    public static void wordToBytes(final long n, final byte[] array, final int n2) {
        if (n2 + 8 > array.length) {
            throw new IllegalArgumentException();
        }
        int n3 = n2;
        array[n3++] = (byte)n;
        array[n3++] = (byte)(n >> 8);
        array[n3++] = (byte)(n >> 16);
        array[n3++] = (byte)(n >> 24);
        array[n3++] = (byte)(n >> 32);
        array[n3++] = (byte)(n >> 40);
        array[n3++] = (byte)(n >> 48);
        array[n3++] = (byte)(n >> 56);
    }
    
    static long rotlXor(final long n, final int n2, final long n3) {
        return (n << n2 | n >>> -n2) ^ n3;
    }
    
    static long xorRotr(final long n, final int n2, final long n3) {
        final long n4 = n ^ n3;
        return n4 >>> n2 | n4 << -n2;
    }
    
    static {
        ThreefishEngine.MOD9 = new int[80];
        ThreefishEngine.MOD17 = new int[ThreefishEngine.MOD9.length];
        ThreefishEngine.MOD5 = new int[ThreefishEngine.MOD9.length];
        ThreefishEngine.MOD3 = new int[ThreefishEngine.MOD9.length];
        for (int i = 0; i < ThreefishEngine.MOD9.length; ++i) {
            ThreefishEngine.MOD17[i] = i % 17;
            ThreefishEngine.MOD9[i] = i % 9;
            ThreefishEngine.MOD5[i] = i % 5;
            ThreefishEngine.MOD3[i] = i % 3;
        }
    }
    
    private static final class Threefish1024Cipher extends ThreefishCipher
    {
        private static final int ROTATION_0_0 = 24;
        private static final int ROTATION_0_1 = 13;
        private static final int ROTATION_0_2 = 8;
        private static final int ROTATION_0_3 = 47;
        private static final int ROTATION_0_4 = 8;
        private static final int ROTATION_0_5 = 17;
        private static final int ROTATION_0_6 = 22;
        private static final int ROTATION_0_7 = 37;
        private static final int ROTATION_1_0 = 38;
        private static final int ROTATION_1_1 = 19;
        private static final int ROTATION_1_2 = 10;
        private static final int ROTATION_1_3 = 55;
        private static final int ROTATION_1_4 = 49;
        private static final int ROTATION_1_5 = 18;
        private static final int ROTATION_1_6 = 23;
        private static final int ROTATION_1_7 = 52;
        private static final int ROTATION_2_0 = 33;
        private static final int ROTATION_2_1 = 4;
        private static final int ROTATION_2_2 = 51;
        private static final int ROTATION_2_3 = 13;
        private static final int ROTATION_2_4 = 34;
        private static final int ROTATION_2_5 = 41;
        private static final int ROTATION_2_6 = 59;
        private static final int ROTATION_2_7 = 17;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 20;
        private static final int ROTATION_3_2 = 48;
        private static final int ROTATION_3_3 = 41;
        private static final int ROTATION_3_4 = 47;
        private static final int ROTATION_3_5 = 28;
        private static final int ROTATION_3_6 = 16;
        private static final int ROTATION_3_7 = 25;
        private static final int ROTATION_4_0 = 41;
        private static final int ROTATION_4_1 = 9;
        private static final int ROTATION_4_2 = 37;
        private static final int ROTATION_4_3 = 31;
        private static final int ROTATION_4_4 = 12;
        private static final int ROTATION_4_5 = 47;
        private static final int ROTATION_4_6 = 44;
        private static final int ROTATION_4_7 = 30;
        private static final int ROTATION_5_0 = 16;
        private static final int ROTATION_5_1 = 34;
        private static final int ROTATION_5_2 = 56;
        private static final int ROTATION_5_3 = 51;
        private static final int ROTATION_5_4 = 4;
        private static final int ROTATION_5_5 = 53;
        private static final int ROTATION_5_6 = 42;
        private static final int ROTATION_5_7 = 41;
        private static final int ROTATION_6_0 = 31;
        private static final int ROTATION_6_1 = 44;
        private static final int ROTATION_6_2 = 47;
        private static final int ROTATION_6_3 = 46;
        private static final int ROTATION_6_4 = 19;
        private static final int ROTATION_6_5 = 42;
        private static final int ROTATION_6_6 = 44;
        private static final int ROTATION_6_7 = 25;
        private static final int ROTATION_7_0 = 9;
        private static final int ROTATION_7_1 = 48;
        private static final int ROTATION_7_2 = 35;
        private static final int ROTATION_7_3 = 52;
        private static final int ROTATION_7_4 = 23;
        private static final int ROTATION_7_5 = 31;
        private static final int ROTATION_7_6 = 37;
        private static final int ROTATION_7_7 = 20;
        
        public Threefish1024Cipher(final long[] array, final long[] array2) {
            super(array, array2);
        }
        
        @Override
        void encryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$300 = ThreefishEngine.MOD17;
            final int[] access$301 = ThreefishEngine.MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            final long n = array[0];
            final long n2 = array[1];
            final long n3 = array[2];
            final long n4 = array[3];
            final long n5 = array[4];
            final long n6 = array[5];
            final long n7 = array[6];
            final long n8 = array[7];
            final long n9 = array[8];
            final long n10 = array[9];
            final long n11 = array[10];
            final long n12 = array[11];
            final long n13 = array[12];
            final long n14 = array[13];
            final long n15 = array[14];
            final long n16 = array[15];
            long n17 = n + kw[0];
            long n18 = n2 + kw[1];
            long n19 = n3 + kw[2];
            long n20 = n4 + kw[3];
            long n21 = n5 + kw[4];
            long n22 = n6 + kw[5];
            long n23 = n7 + kw[6];
            long n24 = n8 + kw[7];
            long n25 = n9 + kw[8];
            long n26 = n10 + kw[9];
            long n27 = n11 + kw[10];
            long n28 = n12 + kw[11];
            long n29 = n13 + kw[12];
            long n30 = n14 + (kw[13] + t[0]);
            long n31 = n15 + (kw[14] + t[1]);
            long n32 = n16 + kw[15];
            for (int i = 1; i < 20; i += 2) {
                final int n33 = access$300[i];
                final int n34 = access$301[i];
                final long n35;
                final long rotlXor = ThreefishEngine.rotlXor(n18, 24, n35 = n17 + n18);
                final long n36;
                final long rotlXor2 = ThreefishEngine.rotlXor(n20, 13, n36 = n19 + n20);
                final long n37;
                final long rotlXor3 = ThreefishEngine.rotlXor(n22, 8, n37 = n21 + n22);
                final long n38;
                final long rotlXor4 = ThreefishEngine.rotlXor(n24, 47, n38 = n23 + n24);
                final long n39;
                final long rotlXor5 = ThreefishEngine.rotlXor(n26, 8, n39 = n25 + n26);
                final long n40;
                final long rotlXor6 = ThreefishEngine.rotlXor(n28, 17, n40 = n27 + n28);
                final long n41;
                final long rotlXor7 = ThreefishEngine.rotlXor(n30, 22, n41 = n29 + n30);
                final long n42;
                final long rotlXor8 = ThreefishEngine.rotlXor(n32, 37, n42 = n31 + n32);
                final long n43;
                final long rotlXor9 = ThreefishEngine.rotlXor(rotlXor5, 38, n43 = n35 + rotlXor5);
                final long n44;
                final long rotlXor10 = ThreefishEngine.rotlXor(rotlXor7, 19, n44 = n36 + rotlXor7);
                final long n45;
                final long rotlXor11 = ThreefishEngine.rotlXor(rotlXor6, 10, n45 = n38 + rotlXor6);
                final long n46;
                final long rotlXor12 = ThreefishEngine.rotlXor(rotlXor8, 55, n46 = n37 + rotlXor8);
                final long n47;
                final long rotlXor13 = ThreefishEngine.rotlXor(rotlXor4, 49, n47 = n40 + rotlXor4);
                final long n48;
                final long rotlXor14 = ThreefishEngine.rotlXor(rotlXor2, 18, n48 = n41 + rotlXor2);
                final long n49;
                final long rotlXor15 = ThreefishEngine.rotlXor(rotlXor3, 23, n49 = n42 + rotlXor3);
                final long n50;
                final long rotlXor16 = ThreefishEngine.rotlXor(rotlXor, 52, n50 = n39 + rotlXor);
                final long n51;
                final long rotlXor17 = ThreefishEngine.rotlXor(rotlXor13, 33, n51 = n43 + rotlXor13);
                final long n52;
                final long rotlXor18 = ThreefishEngine.rotlXor(rotlXor15, 4, n52 = n44 + rotlXor15);
                final long n53;
                final long rotlXor19 = ThreefishEngine.rotlXor(rotlXor14, 51, n53 = n46 + rotlXor14);
                final long n54;
                final long rotlXor20 = ThreefishEngine.rotlXor(rotlXor16, 13, n54 = n45 + rotlXor16);
                final long n55;
                final long rotlXor21 = ThreefishEngine.rotlXor(rotlXor12, 34, n55 = n48 + rotlXor12);
                final long n56;
                final long rotlXor22 = ThreefishEngine.rotlXor(rotlXor10, 41, n56 = n49 + rotlXor10);
                final long n57;
                final long rotlXor23 = ThreefishEngine.rotlXor(rotlXor11, 59, n57 = n50 + rotlXor11);
                final long n58;
                final long rotlXor24 = ThreefishEngine.rotlXor(rotlXor9, 17, n58 = n47 + rotlXor9);
                final long n59;
                final long rotlXor25 = ThreefishEngine.rotlXor(rotlXor21, 5, n59 = n51 + rotlXor21);
                final long n60;
                final long rotlXor26 = ThreefishEngine.rotlXor(rotlXor23, 20, n60 = n52 + rotlXor23);
                final long n61;
                final long rotlXor27 = ThreefishEngine.rotlXor(rotlXor22, 48, n61 = n54 + rotlXor22);
                final long n62;
                final long rotlXor28 = ThreefishEngine.rotlXor(rotlXor24, 41, n62 = n53 + rotlXor24);
                final long n63;
                final long rotlXor29 = ThreefishEngine.rotlXor(rotlXor20, 47, n63 = n56 + rotlXor20);
                final long n64;
                final long rotlXor30 = ThreefishEngine.rotlXor(rotlXor18, 28, n64 = n57 + rotlXor18);
                final long n65;
                final long rotlXor31 = ThreefishEngine.rotlXor(rotlXor19, 16, n65 = n58 + rotlXor19);
                final long n66;
                final long rotlXor32 = ThreefishEngine.rotlXor(rotlXor17, 25, n66 = n55 + rotlXor17);
                final long n67 = n59 + kw[n33];
                final long n68 = rotlXor29 + kw[n33 + 1];
                final long n69 = n60 + kw[n33 + 2];
                final long n70 = rotlXor31 + kw[n33 + 3];
                final long n71 = n62 + kw[n33 + 4];
                final long n72 = rotlXor30 + kw[n33 + 5];
                final long n73 = n61 + kw[n33 + 6];
                final long n74 = rotlXor32 + kw[n33 + 7];
                final long n75 = n64 + kw[n33 + 8];
                final long n76 = rotlXor28 + kw[n33 + 9];
                final long n77 = n65 + kw[n33 + 10];
                final long n78 = rotlXor26 + kw[n33 + 11];
                final long n79 = n66 + kw[n33 + 12];
                final long n80 = rotlXor27 + (kw[n33 + 13] + t[n34]);
                final long n81 = n63 + (kw[n33 + 14] + t[n34 + 1]);
                final long n82 = rotlXor25 + (kw[n33 + 15] + i);
                final long n83;
                final long rotlXor33 = ThreefishEngine.rotlXor(n68, 41, n83 = n67 + n68);
                final long n84;
                final long rotlXor34 = ThreefishEngine.rotlXor(n70, 9, n84 = n69 + n70);
                final long n85;
                final long rotlXor35 = ThreefishEngine.rotlXor(n72, 37, n85 = n71 + n72);
                final long n86;
                final long rotlXor36 = ThreefishEngine.rotlXor(n74, 31, n86 = n73 + n74);
                final long n87;
                final long rotlXor37 = ThreefishEngine.rotlXor(n76, 12, n87 = n75 + n76);
                final long n88;
                final long rotlXor38 = ThreefishEngine.rotlXor(n78, 47, n88 = n77 + n78);
                final long n89;
                final long rotlXor39 = ThreefishEngine.rotlXor(n80, 44, n89 = n79 + n80);
                final long n90;
                final long rotlXor40 = ThreefishEngine.rotlXor(n82, 30, n90 = n81 + n82);
                final long n91;
                final long rotlXor41 = ThreefishEngine.rotlXor(rotlXor37, 16, n91 = n83 + rotlXor37);
                final long n92;
                final long rotlXor42 = ThreefishEngine.rotlXor(rotlXor39, 34, n92 = n84 + rotlXor39);
                final long n93;
                final long rotlXor43 = ThreefishEngine.rotlXor(rotlXor38, 56, n93 = n86 + rotlXor38);
                final long n94;
                final long rotlXor44 = ThreefishEngine.rotlXor(rotlXor40, 51, n94 = n85 + rotlXor40);
                final long n95;
                final long rotlXor45 = ThreefishEngine.rotlXor(rotlXor36, 4, n95 = n88 + rotlXor36);
                final long n96;
                final long rotlXor46 = ThreefishEngine.rotlXor(rotlXor34, 53, n96 = n89 + rotlXor34);
                final long n97;
                final long rotlXor47 = ThreefishEngine.rotlXor(rotlXor35, 42, n97 = n90 + rotlXor35);
                final long n98;
                final long rotlXor48 = ThreefishEngine.rotlXor(rotlXor33, 41, n98 = n87 + rotlXor33);
                final long n99;
                final long rotlXor49 = ThreefishEngine.rotlXor(rotlXor45, 31, n99 = n91 + rotlXor45);
                final long n100;
                final long rotlXor50 = ThreefishEngine.rotlXor(rotlXor47, 44, n100 = n92 + rotlXor47);
                final long n101;
                final long rotlXor51 = ThreefishEngine.rotlXor(rotlXor46, 47, n101 = n94 + rotlXor46);
                final long n102;
                final long rotlXor52 = ThreefishEngine.rotlXor(rotlXor48, 46, n102 = n93 + rotlXor48);
                final long n103;
                final long rotlXor53 = ThreefishEngine.rotlXor(rotlXor44, 19, n103 = n96 + rotlXor44);
                final long n104;
                final long rotlXor54 = ThreefishEngine.rotlXor(rotlXor42, 42, n104 = n97 + rotlXor42);
                final long n105;
                final long rotlXor55 = ThreefishEngine.rotlXor(rotlXor43, 44, n105 = n98 + rotlXor43);
                final long n106;
                final long rotlXor56 = ThreefishEngine.rotlXor(rotlXor41, 25, n106 = n95 + rotlXor41);
                final long n107;
                final long rotlXor57 = ThreefishEngine.rotlXor(rotlXor53, 9, n107 = n99 + rotlXor53);
                final long n108;
                final long rotlXor58 = ThreefishEngine.rotlXor(rotlXor55, 48, n108 = n100 + rotlXor55);
                final long n109;
                final long rotlXor59 = ThreefishEngine.rotlXor(rotlXor54, 35, n109 = n102 + rotlXor54);
                final long n110;
                final long rotlXor60 = ThreefishEngine.rotlXor(rotlXor56, 52, n110 = n101 + rotlXor56);
                final long n111;
                final long rotlXor61 = ThreefishEngine.rotlXor(rotlXor52, 23, n111 = n104 + rotlXor52);
                final long n112;
                final long rotlXor62 = ThreefishEngine.rotlXor(rotlXor50, 31, n112 = n105 + rotlXor50);
                final long n113;
                final long rotlXor63 = ThreefishEngine.rotlXor(rotlXor51, 37, n113 = n106 + rotlXor51);
                final long n114;
                final long rotlXor64 = ThreefishEngine.rotlXor(rotlXor49, 20, n114 = n103 + rotlXor49);
                n17 = n107 + kw[n33 + 1];
                n18 = rotlXor61 + kw[n33 + 2];
                n19 = n108 + kw[n33 + 3];
                n20 = rotlXor63 + kw[n33 + 4];
                n21 = n110 + kw[n33 + 5];
                n22 = rotlXor62 + kw[n33 + 6];
                n23 = n109 + kw[n33 + 7];
                n24 = rotlXor64 + kw[n33 + 8];
                n25 = n112 + kw[n33 + 9];
                n26 = rotlXor60 + kw[n33 + 10];
                n27 = n113 + kw[n33 + 11];
                n28 = rotlXor58 + kw[n33 + 12];
                n29 = n114 + kw[n33 + 13];
                n30 = rotlXor59 + (kw[n33 + 14] + t[n34 + 1]);
                n31 = n111 + (kw[n33 + 15] + t[n34 + 2]);
                n32 = rotlXor57 + (kw[n33 + 16] + i + 1L);
            }
            array2[0] = n17;
            array2[1] = n18;
            array2[2] = n19;
            array2[3] = n20;
            array2[4] = n21;
            array2[5] = n22;
            array2[6] = n23;
            array2[7] = n24;
            array2[8] = n25;
            array2[9] = n26;
            array2[10] = n27;
            array2[11] = n28;
            array2[12] = n29;
            array2[13] = n30;
            array2[14] = n31;
            array2[15] = n32;
        }
        
        @Override
        void decryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$300 = ThreefishEngine.MOD17;
            final int[] access$301 = ThreefishEngine.MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long n = array[0];
            long xorRotr = array[1];
            long n2 = array[2];
            long xorRotr2 = array[3];
            long n3 = array[4];
            long xorRotr3 = array[5];
            long n4 = array[6];
            long xorRotr4 = array[7];
            long n5 = array[8];
            long xorRotr5 = array[9];
            long n6 = array[10];
            long xorRotr6 = array[11];
            long n7 = array[12];
            long xorRotr7 = array[13];
            long n8 = array[14];
            long xorRotr8 = array[15];
            for (int i = 19; i >= 1; i -= 2) {
                final int n9 = access$300[i];
                final int n10 = access$301[i];
                final long n11 = n - kw[n9 + 1];
                final long n12 = xorRotr - kw[n9 + 2];
                final long n13 = n2 - kw[n9 + 3];
                final long n14 = xorRotr2 - kw[n9 + 4];
                final long n15 = n3 - kw[n9 + 5];
                final long n16 = xorRotr3 - kw[n9 + 6];
                final long n17 = n4 - kw[n9 + 7];
                final long n18 = xorRotr4 - kw[n9 + 8];
                final long n19 = n5 - kw[n9 + 9];
                final long n20 = xorRotr5 - kw[n9 + 10];
                final long n21 = n6 - kw[n9 + 11];
                final long n22 = xorRotr6 - kw[n9 + 12];
                final long n23 = n7 - kw[n9 + 13];
                final long n24 = xorRotr7 - (kw[n9 + 14] + t[n10 + 1]);
                final long n25 = n8 - (kw[n9 + 15] + t[n10 + 2]);
                final long xorRotr9 = ThreefishEngine.xorRotr(xorRotr8 - (kw[n9 + 16] + i + 1L), 9, n11);
                final long n26 = n11 - xorRotr9;
                final long xorRotr10 = ThreefishEngine.xorRotr(n22, 48, n13);
                final long n27 = n13 - xorRotr10;
                final long xorRotr11 = ThreefishEngine.xorRotr(n24, 35, n17);
                final long n28 = n17 - xorRotr11;
                final long xorRotr12 = ThreefishEngine.xorRotr(n20, 52, n15);
                final long n29 = n15 - xorRotr12;
                final long xorRotr13 = ThreefishEngine.xorRotr(n12, 23, n25);
                final long n30 = n25 - xorRotr13;
                final long xorRotr14 = ThreefishEngine.xorRotr(n16, 31, n19);
                final long n31 = n19 - xorRotr14;
                final long xorRotr15 = ThreefishEngine.xorRotr(n14, 37, n21);
                final long n32 = n21 - xorRotr15;
                final long xorRotr16 = ThreefishEngine.xorRotr(n18, 20, n23);
                final long n33 = n23 - xorRotr16;
                final long xorRotr17 = ThreefishEngine.xorRotr(xorRotr16, 31, n26);
                final long n34 = n26 - xorRotr17;
                final long xorRotr18 = ThreefishEngine.xorRotr(xorRotr14, 44, n27);
                final long n35 = n27 - xorRotr18;
                final long xorRotr19 = ThreefishEngine.xorRotr(xorRotr15, 47, n29);
                final long n36 = n29 - xorRotr19;
                final long xorRotr20 = ThreefishEngine.xorRotr(xorRotr13, 46, n28);
                final long n37 = n28 - xorRotr20;
                final long xorRotr21 = ThreefishEngine.xorRotr(xorRotr9, 19, n33);
                final long n38 = n33 - xorRotr21;
                final long xorRotr22 = ThreefishEngine.xorRotr(xorRotr11, 42, n30);
                final long n39 = n30 - xorRotr22;
                final long xorRotr23 = ThreefishEngine.xorRotr(xorRotr10, 44, n31);
                final long n40 = n31 - xorRotr23;
                final long xorRotr24 = ThreefishEngine.xorRotr(xorRotr12, 25, n32);
                final long n41 = n32 - xorRotr24;
                final long xorRotr25 = ThreefishEngine.xorRotr(xorRotr24, 16, n34);
                final long n42 = n34 - xorRotr25;
                final long xorRotr26 = ThreefishEngine.xorRotr(xorRotr22, 34, n35);
                final long n43 = n35 - xorRotr26;
                final long xorRotr27 = ThreefishEngine.xorRotr(xorRotr23, 56, n37);
                final long n44 = n37 - xorRotr27;
                final long xorRotr28 = ThreefishEngine.xorRotr(xorRotr21, 51, n36);
                final long n45 = n36 - xorRotr28;
                final long xorRotr29 = ThreefishEngine.xorRotr(xorRotr17, 4, n41);
                final long n46 = n41 - xorRotr29;
                final long xorRotr30 = ThreefishEngine.xorRotr(xorRotr19, 53, n38);
                final long n47 = n38 - xorRotr30;
                final long xorRotr31 = ThreefishEngine.xorRotr(xorRotr18, 42, n39);
                final long n48 = n39 - xorRotr31;
                final long xorRotr32 = ThreefishEngine.xorRotr(xorRotr20, 41, n40);
                final long n49 = n40 - xorRotr32;
                final long xorRotr33 = ThreefishEngine.xorRotr(xorRotr32, 41, n42);
                final long n50 = n42 - xorRotr33;
                final long xorRotr34 = ThreefishEngine.xorRotr(xorRotr30, 9, n43);
                final long n51 = n43 - xorRotr34;
                final long xorRotr35 = ThreefishEngine.xorRotr(xorRotr31, 37, n45);
                final long n52 = n45 - xorRotr35;
                final long xorRotr36 = ThreefishEngine.xorRotr(xorRotr29, 31, n44);
                final long n53 = n44 - xorRotr36;
                final long xorRotr37 = ThreefishEngine.xorRotr(xorRotr25, 12, n49);
                final long n54 = n49 - xorRotr37;
                final long xorRotr38 = ThreefishEngine.xorRotr(xorRotr27, 47, n46);
                final long n55 = n46 - xorRotr38;
                final long xorRotr39 = ThreefishEngine.xorRotr(xorRotr26, 44, n47);
                final long n56 = n47 - xorRotr39;
                final long xorRotr40 = ThreefishEngine.xorRotr(xorRotr28, 30, n48);
                final long n57 = n48 - xorRotr40;
                final long n58 = n50 - kw[n9];
                final long n59 = xorRotr33 - kw[n9 + 1];
                final long n60 = n51 - kw[n9 + 2];
                final long n61 = xorRotr34 - kw[n9 + 3];
                final long n62 = n52 - kw[n9 + 4];
                final long n63 = xorRotr35 - kw[n9 + 5];
                final long n64 = n53 - kw[n9 + 6];
                final long n65 = xorRotr36 - kw[n9 + 7];
                final long n66 = n54 - kw[n9 + 8];
                final long n67 = xorRotr37 - kw[n9 + 9];
                final long n68 = n55 - kw[n9 + 10];
                final long n69 = xorRotr38 - kw[n9 + 11];
                final long n70 = n56 - kw[n9 + 12];
                final long n71 = xorRotr39 - (kw[n9 + 13] + t[n10]);
                final long n72 = n57 - (kw[n9 + 14] + t[n10 + 1]);
                final long xorRotr41 = ThreefishEngine.xorRotr(xorRotr40 - (kw[n9 + 15] + i), 5, n58);
                final long n73 = n58 - xorRotr41;
                final long xorRotr42 = ThreefishEngine.xorRotr(n69, 20, n60);
                final long n74 = n60 - xorRotr42;
                final long xorRotr43 = ThreefishEngine.xorRotr(n71, 48, n64);
                final long n75 = n64 - xorRotr43;
                final long xorRotr44 = ThreefishEngine.xorRotr(n67, 41, n62);
                final long n76 = n62 - xorRotr44;
                final long xorRotr45 = ThreefishEngine.xorRotr(n59, 47, n72);
                final long n77 = n72 - xorRotr45;
                final long xorRotr46 = ThreefishEngine.xorRotr(n63, 28, n66);
                final long n78 = n66 - xorRotr46;
                final long xorRotr47 = ThreefishEngine.xorRotr(n61, 16, n68);
                final long n79 = n68 - xorRotr47;
                final long xorRotr48 = ThreefishEngine.xorRotr(n65, 25, n70);
                final long n80 = n70 - xorRotr48;
                final long xorRotr49 = ThreefishEngine.xorRotr(xorRotr48, 33, n73);
                final long n81 = n73 - xorRotr49;
                final long xorRotr50 = ThreefishEngine.xorRotr(xorRotr46, 4, n74);
                final long n82 = n74 - xorRotr50;
                final long xorRotr51 = ThreefishEngine.xorRotr(xorRotr47, 51, n76);
                final long n83 = n76 - xorRotr51;
                final long xorRotr52 = ThreefishEngine.xorRotr(xorRotr45, 13, n75);
                final long n84 = n75 - xorRotr52;
                final long xorRotr53 = ThreefishEngine.xorRotr(xorRotr41, 34, n80);
                final long n85 = n80 - xorRotr53;
                final long xorRotr54 = ThreefishEngine.xorRotr(xorRotr43, 41, n77);
                final long n86 = n77 - xorRotr54;
                final long xorRotr55 = ThreefishEngine.xorRotr(xorRotr42, 59, n78);
                final long n87 = n78 - xorRotr55;
                final long xorRotr56 = ThreefishEngine.xorRotr(xorRotr44, 17, n79);
                final long n88 = n79 - xorRotr56;
                final long xorRotr57 = ThreefishEngine.xorRotr(xorRotr56, 38, n81);
                final long n89 = n81 - xorRotr57;
                final long xorRotr58 = ThreefishEngine.xorRotr(xorRotr54, 19, n82);
                final long n90 = n82 - xorRotr58;
                final long xorRotr59 = ThreefishEngine.xorRotr(xorRotr55, 10, n84);
                final long n91 = n84 - xorRotr59;
                final long xorRotr60 = ThreefishEngine.xorRotr(xorRotr53, 55, n83);
                final long n92 = n83 - xorRotr60;
                final long xorRotr61 = ThreefishEngine.xorRotr(xorRotr49, 49, n88);
                final long n93 = n88 - xorRotr61;
                final long xorRotr62 = ThreefishEngine.xorRotr(xorRotr51, 18, n85);
                final long n94 = n85 - xorRotr62;
                final long xorRotr63 = ThreefishEngine.xorRotr(xorRotr50, 23, n86);
                final long n95 = n86 - xorRotr63;
                final long xorRotr64 = ThreefishEngine.xorRotr(xorRotr52, 52, n87);
                final long n96 = n87 - xorRotr64;
                xorRotr = ThreefishEngine.xorRotr(xorRotr64, 24, n89);
                n = n89 - xorRotr;
                xorRotr2 = ThreefishEngine.xorRotr(xorRotr62, 13, n90);
                n2 = n90 - xorRotr2;
                xorRotr3 = ThreefishEngine.xorRotr(xorRotr63, 8, n92);
                n3 = n92 - xorRotr3;
                xorRotr4 = ThreefishEngine.xorRotr(xorRotr61, 47, n91);
                n4 = n91 - xorRotr4;
                xorRotr5 = ThreefishEngine.xorRotr(xorRotr57, 8, n96);
                n5 = n96 - xorRotr5;
                xorRotr6 = ThreefishEngine.xorRotr(xorRotr59, 17, n93);
                n6 = n93 - xorRotr6;
                xorRotr7 = ThreefishEngine.xorRotr(xorRotr58, 22, n94);
                n7 = n94 - xorRotr7;
                xorRotr8 = ThreefishEngine.xorRotr(xorRotr60, 37, n95);
                n8 = n95 - xorRotr8;
            }
            final long n97 = n - kw[0];
            final long n98 = xorRotr - kw[1];
            final long n99 = n2 - kw[2];
            final long n100 = xorRotr2 - kw[3];
            final long n101 = n3 - kw[4];
            final long n102 = xorRotr3 - kw[5];
            final long n103 = n4 - kw[6];
            final long n104 = xorRotr4 - kw[7];
            final long n105 = n5 - kw[8];
            final long n106 = xorRotr5 - kw[9];
            final long n107 = n6 - kw[10];
            final long n108 = xorRotr6 - kw[11];
            final long n109 = n7 - kw[12];
            final long n110 = xorRotr7 - (kw[13] + t[0]);
            final long n111 = n8 - (kw[14] + t[1]);
            final long n112 = xorRotr8 - kw[15];
            array2[0] = n97;
            array2[1] = n98;
            array2[2] = n99;
            array2[3] = n100;
            array2[4] = n101;
            array2[5] = n102;
            array2[6] = n103;
            array2[7] = n104;
            array2[8] = n105;
            array2[9] = n106;
            array2[10] = n107;
            array2[11] = n108;
            array2[12] = n109;
            array2[13] = n110;
            array2[14] = n111;
            array2[15] = n112;
        }
    }
    
    private static final class Threefish256Cipher extends ThreefishCipher
    {
        private static final int ROTATION_0_0 = 14;
        private static final int ROTATION_0_1 = 16;
        private static final int ROTATION_1_0 = 52;
        private static final int ROTATION_1_1 = 57;
        private static final int ROTATION_2_0 = 23;
        private static final int ROTATION_2_1 = 40;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 37;
        private static final int ROTATION_4_0 = 25;
        private static final int ROTATION_4_1 = 33;
        private static final int ROTATION_5_0 = 46;
        private static final int ROTATION_5_1 = 12;
        private static final int ROTATION_6_0 = 58;
        private static final int ROTATION_6_1 = 22;
        private static final int ROTATION_7_0 = 32;
        private static final int ROTATION_7_1 = 32;
        
        public Threefish256Cipher(final long[] array, final long[] array2) {
            super(array, array2);
        }
        
        @Override
        void encryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$000 = ThreefishEngine.MOD5;
            final int[] access$2 = ThreefishEngine.MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            final long n = array[0];
            final long n2 = array[1];
            final long n3 = array[2];
            final long n4 = array[3];
            long n5 = n + kw[0];
            long n6 = n2 + (kw[1] + t[0]);
            long n7 = n3 + (kw[2] + t[1]);
            long n8 = n4 + kw[3];
            for (int i = 1; i < 18; i += 2) {
                final int n9 = access$000[i];
                final int n10 = access$2[i];
                final long n11;
                final long rotlXor = ThreefishEngine.rotlXor(n6, 14, n11 = n5 + n6);
                final long n12;
                final long rotlXor2 = ThreefishEngine.rotlXor(n8, 16, n12 = n7 + n8);
                final long n13;
                final long rotlXor3 = ThreefishEngine.rotlXor(rotlXor2, 52, n13 = n11 + rotlXor2);
                final long n14;
                final long rotlXor4 = ThreefishEngine.rotlXor(rotlXor, 57, n14 = n12 + rotlXor);
                final long n15;
                final long rotlXor5 = ThreefishEngine.rotlXor(rotlXor4, 23, n15 = n13 + rotlXor4);
                final long n16;
                final long rotlXor6 = ThreefishEngine.rotlXor(rotlXor3, 40, n16 = n14 + rotlXor3);
                final long n17;
                final long rotlXor7 = ThreefishEngine.rotlXor(rotlXor6, 5, n17 = n15 + rotlXor6);
                final long n18;
                final long rotlXor8 = ThreefishEngine.rotlXor(rotlXor5, 37, n18 = n16 + rotlXor5);
                final long n19 = n17 + kw[n9];
                final long n20 = rotlXor8 + (kw[n9 + 1] + t[n10]);
                final long n21 = n18 + (kw[n9 + 2] + t[n10 + 1]);
                final long n22 = rotlXor7 + (kw[n9 + 3] + i);
                final long n23;
                final long rotlXor9 = ThreefishEngine.rotlXor(n20, 25, n23 = n19 + n20);
                final long n24;
                final long rotlXor10 = ThreefishEngine.rotlXor(n22, 33, n24 = n21 + n22);
                final long n25;
                final long rotlXor11 = ThreefishEngine.rotlXor(rotlXor10, 46, n25 = n23 + rotlXor10);
                final long n26;
                final long rotlXor12 = ThreefishEngine.rotlXor(rotlXor9, 12, n26 = n24 + rotlXor9);
                final long n27;
                final long rotlXor13 = ThreefishEngine.rotlXor(rotlXor12, 58, n27 = n25 + rotlXor12);
                final long n28;
                final long rotlXor14 = ThreefishEngine.rotlXor(rotlXor11, 22, n28 = n26 + rotlXor11);
                final long n29;
                final long rotlXor15 = ThreefishEngine.rotlXor(rotlXor14, 32, n29 = n27 + rotlXor14);
                final long n30;
                final long rotlXor16 = ThreefishEngine.rotlXor(rotlXor13, 32, n30 = n28 + rotlXor13);
                n5 = n29 + kw[n9 + 1];
                n6 = rotlXor16 + (kw[n9 + 2] + t[n10 + 1]);
                n7 = n30 + (kw[n9 + 3] + t[n10 + 2]);
                n8 = rotlXor15 + (kw[n9 + 4] + i + 1L);
            }
            array2[0] = n5;
            array2[1] = n6;
            array2[2] = n7;
            array2[3] = n8;
        }
        
        @Override
        void decryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$000 = ThreefishEngine.MOD5;
            final int[] access$2 = ThreefishEngine.MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long n = array[0];
            long xorRotr = array[1];
            long n2 = array[2];
            long xorRotr2 = array[3];
            for (int i = 17; i >= 1; i -= 2) {
                final int n3 = access$000[i];
                final int n4 = access$2[i];
                final long n5 = n - kw[n3 + 1];
                final long n6 = xorRotr - (kw[n3 + 2] + t[n4 + 1]);
                final long n7 = n2 - (kw[n3 + 3] + t[n4 + 2]);
                final long xorRotr3 = ThreefishEngine.xorRotr(xorRotr2 - (kw[n3 + 4] + i + 1L), 32, n5);
                final long n8 = n5 - xorRotr3;
                final long xorRotr4 = ThreefishEngine.xorRotr(n6, 32, n7);
                final long n9 = n7 - xorRotr4;
                final long xorRotr5 = ThreefishEngine.xorRotr(xorRotr4, 58, n8);
                final long n10 = n8 - xorRotr5;
                final long xorRotr6 = ThreefishEngine.xorRotr(xorRotr3, 22, n9);
                final long n11 = n9 - xorRotr6;
                final long xorRotr7 = ThreefishEngine.xorRotr(xorRotr6, 46, n10);
                final long n12 = n10 - xorRotr7;
                final long xorRotr8 = ThreefishEngine.xorRotr(xorRotr5, 12, n11);
                final long n13 = n11 - xorRotr8;
                final long xorRotr9 = ThreefishEngine.xorRotr(xorRotr8, 25, n12);
                final long n14 = n12 - xorRotr9;
                final long xorRotr10 = ThreefishEngine.xorRotr(xorRotr7, 33, n13);
                final long n15 = n13 - xorRotr10;
                final long n16 = n14 - kw[n3];
                final long n17 = xorRotr9 - (kw[n3 + 1] + t[n4]);
                final long n18 = n15 - (kw[n3 + 2] + t[n4 + 1]);
                final long xorRotr11 = ThreefishEngine.xorRotr(xorRotr10 - (kw[n3 + 3] + i), 5, n16);
                final long n19 = n16 - xorRotr11;
                final long xorRotr12 = ThreefishEngine.xorRotr(n17, 37, n18);
                final long n20 = n18 - xorRotr12;
                final long xorRotr13 = ThreefishEngine.xorRotr(xorRotr12, 23, n19);
                final long n21 = n19 - xorRotr13;
                final long xorRotr14 = ThreefishEngine.xorRotr(xorRotr11, 40, n20);
                final long n22 = n20 - xorRotr14;
                final long xorRotr15 = ThreefishEngine.xorRotr(xorRotr14, 52, n21);
                final long n23 = n21 - xorRotr15;
                final long xorRotr16 = ThreefishEngine.xorRotr(xorRotr13, 57, n22);
                final long n24 = n22 - xorRotr16;
                xorRotr = ThreefishEngine.xorRotr(xorRotr16, 14, n23);
                n = n23 - xorRotr;
                xorRotr2 = ThreefishEngine.xorRotr(xorRotr15, 16, n24);
                n2 = n24 - xorRotr2;
            }
            final long n25 = n - kw[0];
            final long n26 = xorRotr - (kw[1] + t[0]);
            final long n27 = n2 - (kw[2] + t[1]);
            final long n28 = xorRotr2 - kw[3];
            array2[0] = n25;
            array2[1] = n26;
            array2[2] = n27;
            array2[3] = n28;
        }
    }
    
    private abstract static class ThreefishCipher
    {
        protected final long[] t;
        protected final long[] kw;
        
        protected ThreefishCipher(final long[] kw, final long[] t) {
            this.kw = kw;
            this.t = t;
        }
        
        abstract void encryptBlock(final long[] p0, final long[] p1);
        
        abstract void decryptBlock(final long[] p0, final long[] p1);
    }
    
    private static final class Threefish512Cipher extends ThreefishCipher
    {
        private static final int ROTATION_0_0 = 46;
        private static final int ROTATION_0_1 = 36;
        private static final int ROTATION_0_2 = 19;
        private static final int ROTATION_0_3 = 37;
        private static final int ROTATION_1_0 = 33;
        private static final int ROTATION_1_1 = 27;
        private static final int ROTATION_1_2 = 14;
        private static final int ROTATION_1_3 = 42;
        private static final int ROTATION_2_0 = 17;
        private static final int ROTATION_2_1 = 49;
        private static final int ROTATION_2_2 = 36;
        private static final int ROTATION_2_3 = 39;
        private static final int ROTATION_3_0 = 44;
        private static final int ROTATION_3_1 = 9;
        private static final int ROTATION_3_2 = 54;
        private static final int ROTATION_3_3 = 56;
        private static final int ROTATION_4_0 = 39;
        private static final int ROTATION_4_1 = 30;
        private static final int ROTATION_4_2 = 34;
        private static final int ROTATION_4_3 = 24;
        private static final int ROTATION_5_0 = 13;
        private static final int ROTATION_5_1 = 50;
        private static final int ROTATION_5_2 = 10;
        private static final int ROTATION_5_3 = 17;
        private static final int ROTATION_6_0 = 25;
        private static final int ROTATION_6_1 = 29;
        private static final int ROTATION_6_2 = 39;
        private static final int ROTATION_6_3 = 43;
        private static final int ROTATION_7_0 = 8;
        private static final int ROTATION_7_1 = 35;
        private static final int ROTATION_7_2 = 56;
        private static final int ROTATION_7_3 = 22;
        
        protected Threefish512Cipher(final long[] array, final long[] array2) {
            super(array, array2);
        }
        
        public void encryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$200 = ThreefishEngine.MOD9;
            final int[] access$201 = ThreefishEngine.MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            final long n = array[0];
            final long n2 = array[1];
            final long n3 = array[2];
            final long n4 = array[3];
            final long n5 = array[4];
            final long n6 = array[5];
            final long n7 = array[6];
            final long n8 = array[7];
            long n9 = n + kw[0];
            long n10 = n2 + kw[1];
            long n11 = n3 + kw[2];
            long n12 = n4 + kw[3];
            long n13 = n5 + kw[4];
            long n14 = n6 + (kw[5] + t[0]);
            long n15 = n7 + (kw[6] + t[1]);
            long n16 = n8 + kw[7];
            for (int i = 1; i < 18; i += 2) {
                final int n17 = access$200[i];
                final int n18 = access$201[i];
                final long n19;
                final long rotlXor = ThreefishEngine.rotlXor(n10, 46, n19 = n9 + n10);
                final long n20;
                final long rotlXor2 = ThreefishEngine.rotlXor(n12, 36, n20 = n11 + n12);
                final long n21;
                final long rotlXor3 = ThreefishEngine.rotlXor(n14, 19, n21 = n13 + n14);
                final long n22;
                final long rotlXor4 = ThreefishEngine.rotlXor(n16, 37, n22 = n15 + n16);
                final long n23;
                final long rotlXor5 = ThreefishEngine.rotlXor(rotlXor, 33, n23 = n20 + rotlXor);
                final long n24;
                final long rotlXor6 = ThreefishEngine.rotlXor(rotlXor4, 27, n24 = n21 + rotlXor4);
                final long n25;
                final long rotlXor7 = ThreefishEngine.rotlXor(rotlXor3, 14, n25 = n22 + rotlXor3);
                final long n26;
                final long rotlXor8 = ThreefishEngine.rotlXor(rotlXor2, 42, n26 = n19 + rotlXor2);
                final long n27;
                final long rotlXor9 = ThreefishEngine.rotlXor(rotlXor5, 17, n27 = n24 + rotlXor5);
                final long n28;
                final long rotlXor10 = ThreefishEngine.rotlXor(rotlXor8, 49, n28 = n25 + rotlXor8);
                final long n29;
                final long rotlXor11 = ThreefishEngine.rotlXor(rotlXor7, 36, n29 = n26 + rotlXor7);
                final long n30;
                final long rotlXor12 = ThreefishEngine.rotlXor(rotlXor6, 39, n30 = n23 + rotlXor6);
                final long n31;
                final long rotlXor13 = ThreefishEngine.rotlXor(rotlXor9, 44, n31 = n28 + rotlXor9);
                final long n32;
                final long rotlXor14 = ThreefishEngine.rotlXor(rotlXor12, 9, n32 = n29 + rotlXor12);
                final long n33;
                final long rotlXor15 = ThreefishEngine.rotlXor(rotlXor11, 54, n33 = n30 + rotlXor11);
                final long n34;
                final long rotlXor16 = ThreefishEngine.rotlXor(rotlXor10, 56, n34 = n27 + rotlXor10);
                final long n35 = n32 + kw[n17];
                final long n36 = rotlXor13 + kw[n17 + 1];
                final long n37 = n33 + kw[n17 + 2];
                final long n38 = rotlXor16 + kw[n17 + 3];
                final long n39 = n34 + kw[n17 + 4];
                final long n40 = rotlXor15 + (kw[n17 + 5] + t[n18]);
                final long n41 = n31 + (kw[n17 + 6] + t[n18 + 1]);
                final long n42 = rotlXor14 + (kw[n17 + 7] + i);
                final long n43;
                final long rotlXor17 = ThreefishEngine.rotlXor(n36, 39, n43 = n35 + n36);
                final long n44;
                final long rotlXor18 = ThreefishEngine.rotlXor(n38, 30, n44 = n37 + n38);
                final long n45;
                final long rotlXor19 = ThreefishEngine.rotlXor(n40, 34, n45 = n39 + n40);
                final long n46;
                final long rotlXor20 = ThreefishEngine.rotlXor(n42, 24, n46 = n41 + n42);
                final long n47;
                final long rotlXor21 = ThreefishEngine.rotlXor(rotlXor17, 13, n47 = n44 + rotlXor17);
                final long n48;
                final long rotlXor22 = ThreefishEngine.rotlXor(rotlXor20, 50, n48 = n45 + rotlXor20);
                final long n49;
                final long rotlXor23 = ThreefishEngine.rotlXor(rotlXor19, 10, n49 = n46 + rotlXor19);
                final long n50;
                final long rotlXor24 = ThreefishEngine.rotlXor(rotlXor18, 17, n50 = n43 + rotlXor18);
                final long n51;
                final long rotlXor25 = ThreefishEngine.rotlXor(rotlXor21, 25, n51 = n48 + rotlXor21);
                final long n52;
                final long rotlXor26 = ThreefishEngine.rotlXor(rotlXor24, 29, n52 = n49 + rotlXor24);
                final long n53;
                final long rotlXor27 = ThreefishEngine.rotlXor(rotlXor23, 39, n53 = n50 + rotlXor23);
                final long n54;
                final long rotlXor28 = ThreefishEngine.rotlXor(rotlXor22, 43, n54 = n47 + rotlXor22);
                final long n55;
                final long rotlXor29 = ThreefishEngine.rotlXor(rotlXor25, 8, n55 = n52 + rotlXor25);
                final long n56;
                final long rotlXor30 = ThreefishEngine.rotlXor(rotlXor28, 35, n56 = n53 + rotlXor28);
                final long n57;
                final long rotlXor31 = ThreefishEngine.rotlXor(rotlXor27, 56, n57 = n54 + rotlXor27);
                final long n58;
                final long rotlXor32 = ThreefishEngine.rotlXor(rotlXor26, 22, n58 = n51 + rotlXor26);
                n9 = n56 + kw[n17 + 1];
                n10 = rotlXor29 + kw[n17 + 2];
                n11 = n57 + kw[n17 + 3];
                n12 = rotlXor32 + kw[n17 + 4];
                n13 = n58 + kw[n17 + 5];
                n14 = rotlXor31 + (kw[n17 + 6] + t[n18 + 1]);
                n15 = n55 + (kw[n17 + 7] + t[n18 + 2]);
                n16 = rotlXor30 + (kw[n17 + 8] + i + 1L);
            }
            array2[0] = n9;
            array2[1] = n10;
            array2[2] = n11;
            array2[3] = n12;
            array2[4] = n13;
            array2[5] = n14;
            array2[6] = n15;
            array2[7] = n16;
        }
        
        public void decryptBlock(final long[] array, final long[] array2) {
            final long[] kw = this.kw;
            final long[] t = this.t;
            final int[] access$200 = ThreefishEngine.MOD9;
            final int[] access$201 = ThreefishEngine.MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long n = array[0];
            long xorRotr = array[1];
            long n2 = array[2];
            long xorRotr2 = array[3];
            long n3 = array[4];
            long xorRotr3 = array[5];
            long n4 = array[6];
            long xorRotr4 = array[7];
            for (int i = 17; i >= 1; i -= 2) {
                final int n5 = access$200[i];
                final int n6 = access$201[i];
                final long n7 = n - kw[n5 + 1];
                final long n8 = xorRotr - kw[n5 + 2];
                final long n9 = n2 - kw[n5 + 3];
                final long n10 = xorRotr2 - kw[n5 + 4];
                final long n11 = n3 - kw[n5 + 5];
                final long n12 = xorRotr3 - (kw[n5 + 6] + t[n6 + 1]);
                final long n13 = n4 - (kw[n5 + 7] + t[n6 + 2]);
                final long n14 = xorRotr4 - (kw[n5 + 8] + i + 1L);
                final long xorRotr5 = ThreefishEngine.xorRotr(n8, 8, n13);
                final long n15 = n13 - xorRotr5;
                final long xorRotr6 = ThreefishEngine.xorRotr(n14, 35, n7);
                final long n16 = n7 - xorRotr6;
                final long xorRotr7 = ThreefishEngine.xorRotr(n12, 56, n9);
                final long n17 = n9 - xorRotr7;
                final long xorRotr8 = ThreefishEngine.xorRotr(n10, 22, n11);
                final long n18 = n11 - xorRotr8;
                final long xorRotr9 = ThreefishEngine.xorRotr(xorRotr5, 25, n18);
                final long n19 = n18 - xorRotr9;
                final long xorRotr10 = ThreefishEngine.xorRotr(xorRotr8, 29, n15);
                final long n20 = n15 - xorRotr10;
                final long xorRotr11 = ThreefishEngine.xorRotr(xorRotr7, 39, n16);
                final long n21 = n16 - xorRotr11;
                final long xorRotr12 = ThreefishEngine.xorRotr(xorRotr6, 43, n17);
                final long n22 = n17 - xorRotr12;
                final long xorRotr13 = ThreefishEngine.xorRotr(xorRotr9, 13, n22);
                final long n23 = n22 - xorRotr13;
                final long xorRotr14 = ThreefishEngine.xorRotr(xorRotr12, 50, n19);
                final long n24 = n19 - xorRotr14;
                final long xorRotr15 = ThreefishEngine.xorRotr(xorRotr11, 10, n20);
                final long n25 = n20 - xorRotr15;
                final long xorRotr16 = ThreefishEngine.xorRotr(xorRotr10, 17, n21);
                final long n26 = n21 - xorRotr16;
                final long xorRotr17 = ThreefishEngine.xorRotr(xorRotr13, 39, n26);
                final long n27 = n26 - xorRotr17;
                final long xorRotr18 = ThreefishEngine.xorRotr(xorRotr16, 30, n23);
                final long n28 = n23 - xorRotr18;
                final long xorRotr19 = ThreefishEngine.xorRotr(xorRotr15, 34, n24);
                final long n29 = n24 - xorRotr19;
                final long xorRotr20 = ThreefishEngine.xorRotr(xorRotr14, 24, n25);
                final long n30 = n25 - xorRotr20;
                final long n31 = n27 - kw[n5];
                final long n32 = xorRotr17 - kw[n5 + 1];
                final long n33 = n28 - kw[n5 + 2];
                final long n34 = xorRotr18 - kw[n5 + 3];
                final long n35 = n29 - kw[n5 + 4];
                final long n36 = xorRotr19 - (kw[n5 + 5] + t[n6]);
                final long n37 = n30 - (kw[n5 + 6] + t[n6 + 1]);
                final long n38 = xorRotr20 - (kw[n5 + 7] + i);
                final long xorRotr21 = ThreefishEngine.xorRotr(n32, 44, n37);
                final long n39 = n37 - xorRotr21;
                final long xorRotr22 = ThreefishEngine.xorRotr(n38, 9, n31);
                final long n40 = n31 - xorRotr22;
                final long xorRotr23 = ThreefishEngine.xorRotr(n36, 54, n33);
                final long n41 = n33 - xorRotr23;
                final long xorRotr24 = ThreefishEngine.xorRotr(n34, 56, n35);
                final long n42 = n35 - xorRotr24;
                final long xorRotr25 = ThreefishEngine.xorRotr(xorRotr21, 17, n42);
                final long n43 = n42 - xorRotr25;
                final long xorRotr26 = ThreefishEngine.xorRotr(xorRotr24, 49, n39);
                final long n44 = n39 - xorRotr26;
                final long xorRotr27 = ThreefishEngine.xorRotr(xorRotr23, 36, n40);
                final long n45 = n40 - xorRotr27;
                final long xorRotr28 = ThreefishEngine.xorRotr(xorRotr22, 39, n41);
                final long n46 = n41 - xorRotr28;
                final long xorRotr29 = ThreefishEngine.xorRotr(xorRotr25, 33, n46);
                final long n47 = n46 - xorRotr29;
                final long xorRotr30 = ThreefishEngine.xorRotr(xorRotr28, 27, n43);
                final long n48 = n43 - xorRotr30;
                final long xorRotr31 = ThreefishEngine.xorRotr(xorRotr27, 14, n44);
                final long n49 = n44 - xorRotr31;
                final long xorRotr32 = ThreefishEngine.xorRotr(xorRotr26, 42, n45);
                final long n50 = n45 - xorRotr32;
                xorRotr = ThreefishEngine.xorRotr(xorRotr29, 46, n50);
                n = n50 - xorRotr;
                xorRotr2 = ThreefishEngine.xorRotr(xorRotr32, 36, n47);
                n2 = n47 - xorRotr2;
                xorRotr3 = ThreefishEngine.xorRotr(xorRotr31, 19, n48);
                n3 = n48 - xorRotr3;
                xorRotr4 = ThreefishEngine.xorRotr(xorRotr30, 37, n49);
                n4 = n49 - xorRotr4;
            }
            final long n51 = n - kw[0];
            final long n52 = xorRotr - kw[1];
            final long n53 = n2 - kw[2];
            final long n54 = xorRotr2 - kw[3];
            final long n55 = n3 - kw[4];
            final long n56 = xorRotr3 - (kw[5] + t[0]);
            final long n57 = n4 - (kw[6] + t[1]);
            final long n58 = xorRotr4 - kw[7];
            array2[0] = n51;
            array2[1] = n52;
            array2[2] = n53;
            array2[3] = n54;
            array2[4] = n55;
            array2[5] = n56;
            array2[6] = n57;
            array2[7] = n58;
        }
    }
}
