package com.sun.crypto.provider;

import java.security.MessageDigest;
import java.security.InvalidKeyException;

final class AESCrypt extends SymmetricCipher implements AESConstants
{
    private boolean ROUNDS_12;
    private boolean ROUNDS_14;
    private int[][] sessionK;
    private int[] K;
    private byte[] lastKey;
    private int limit;
    private static int[] alog;
    private static int[] log;
    private static final byte[] S;
    private static final byte[] Si;
    private static final int[] T1;
    private static final int[] T2;
    private static final int[] T3;
    private static final int[] T4;
    private static final int[] T5;
    private static final int[] T6;
    private static final int[] T7;
    private static final int[] T8;
    private static final int[] U1;
    private static final int[] U2;
    private static final int[] U3;
    private static final int[] U4;
    private static final byte[] rcon;
    
    AESCrypt() {
        this.ROUNDS_12 = false;
        this.ROUNDS_14 = false;
        this.sessionK = null;
        this.K = null;
        this.lastKey = null;
        this.limit = 0;
    }
    
    @Override
    int getBlockSize() {
        return 16;
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array) throws InvalidKeyException {
        if (!s.equalsIgnoreCase("AES") && !s.equalsIgnoreCase("Rijndael")) {
            throw new InvalidKeyException("Wrong algorithm: AES or Rijndael required");
        }
        if (!isKeySizeValid(array.length)) {
            throw new InvalidKeyException("Invalid AES key length: " + array.length + " bytes");
        }
        if (!MessageDigest.isEqual(array, this.lastKey)) {
            this.makeSessionKey(array);
            this.lastKey = array.clone();
        }
        this.K = this.sessionK[b];
    }
    
    private static final int[] expandToSubKey(final int[][] array, final boolean b) {
        final int length = array.length;
        final int[] array2 = new int[length * 4];
        if (b) {
            for (int i = 0; i < 4; ++i) {
                array2[i] = array[length - 1][i];
            }
            for (int j = 1; j < length; ++j) {
                for (int k = 0; k < 4; ++k) {
                    array2[j * 4 + k] = array[j - 1][k];
                }
            }
        }
        else {
            for (int l = 0; l < length; ++l) {
                for (int n = 0; n < 4; ++n) {
                    array2[l * 4 + n] = array[l][n];
                }
            }
        }
        return array2;
    }
    
    private static final int mul(final int n, final int n2) {
        return (n != 0 && n2 != 0) ? AESCrypt.alog[(AESCrypt.log[n & 0xFF] + AESCrypt.log[n2 & 0xFF]) % 255] : 0;
    }
    
    private static final int mul4(int n, final byte[] array) {
        if (n == 0) {
            return 0;
        }
        n = AESCrypt.log[n & 0xFF];
        return ((array[0] != 0) ? (AESCrypt.alog[(n + AESCrypt.log[array[0] & 0xFF]) % 255] & 0xFF) : 0) << 24 | ((array[1] != 0) ? (AESCrypt.alog[(n + AESCrypt.log[array[1] & 0xFF]) % 255] & 0xFF) : 0) << 16 | ((array[2] != 0) ? (AESCrypt.alog[(n + AESCrypt.log[array[2] & 0xFF]) % 255] & 0xFF) : 0) << 8 | ((array[3] != 0) ? (AESCrypt.alog[(n + AESCrypt.log[array[3] & 0xFF]) % 255] & 0xFF) : 0);
    }
    
    static final boolean isKeySizeValid(final int n) {
        for (int i = 0; i < AESCrypt.AES_KEYSIZES.length; ++i) {
            if (n == AESCrypt.AES_KEYSIZES[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.implEncryptBlock(array, n, array2, n2);
    }
    
    private void implEncryptBlock(final byte[] array, int n, final byte[] array2, int n2) {
        int i;
        int n3;
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        int n9;
        for (i = 0, n3 = ((array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[i++]), n4 = ((array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[i++]), n5 = ((array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[i++]), n6 = ((array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[i++]); i < this.limit; n7 = (AESCrypt.T1[n3 >>> 24] ^ AESCrypt.T2[n4 >>> 16 & 0xFF] ^ AESCrypt.T3[n5 >>> 8 & 0xFF] ^ AESCrypt.T4[n6 & 0xFF] ^ this.K[i++]), n8 = (AESCrypt.T1[n4 >>> 24] ^ AESCrypt.T2[n5 >>> 16 & 0xFF] ^ AESCrypt.T3[n6 >>> 8 & 0xFF] ^ AESCrypt.T4[n3 & 0xFF] ^ this.K[i++]), n9 = (AESCrypt.T1[n5 >>> 24] ^ AESCrypt.T2[n6 >>> 16 & 0xFF] ^ AESCrypt.T3[n3 >>> 8 & 0xFF] ^ AESCrypt.T4[n4 & 0xFF] ^ this.K[i++]), n6 = (AESCrypt.T1[n6 >>> 24] ^ AESCrypt.T2[n3 >>> 16 & 0xFF] ^ AESCrypt.T3[n4 >>> 8 & 0xFF] ^ AESCrypt.T4[n5 & 0xFF] ^ this.K[i++]), n3 = n7, n4 = n8, n5 = n9) {}
        final int n10 = this.K[i++];
        array2[n2++] = (byte)(AESCrypt.S[n3 >>> 24] ^ n10 >>> 24);
        array2[n2++] = (byte)(AESCrypt.S[n4 >>> 16 & 0xFF] ^ n10 >>> 16);
        array2[n2++] = (byte)(AESCrypt.S[n5 >>> 8 & 0xFF] ^ n10 >>> 8);
        array2[n2++] = (byte)(AESCrypt.S[n6 & 0xFF] ^ n10);
        final int n11 = this.K[i++];
        array2[n2++] = (byte)(AESCrypt.S[n4 >>> 24] ^ n11 >>> 24);
        array2[n2++] = (byte)(AESCrypt.S[n5 >>> 16 & 0xFF] ^ n11 >>> 16);
        array2[n2++] = (byte)(AESCrypt.S[n6 >>> 8 & 0xFF] ^ n11 >>> 8);
        array2[n2++] = (byte)(AESCrypt.S[n3 & 0xFF] ^ n11);
        final int n12 = this.K[i++];
        array2[n2++] = (byte)(AESCrypt.S[n5 >>> 24] ^ n12 >>> 24);
        array2[n2++] = (byte)(AESCrypt.S[n6 >>> 16 & 0xFF] ^ n12 >>> 16);
        array2[n2++] = (byte)(AESCrypt.S[n3 >>> 8 & 0xFF] ^ n12 >>> 8);
        array2[n2++] = (byte)(AESCrypt.S[n4 & 0xFF] ^ n12);
        final int n13 = this.K[i++];
        array2[n2++] = (byte)(AESCrypt.S[n6 >>> 24] ^ n13 >>> 24);
        array2[n2++] = (byte)(AESCrypt.S[n3 >>> 16 & 0xFF] ^ n13 >>> 16);
        array2[n2++] = (byte)(AESCrypt.S[n4 >>> 8 & 0xFF] ^ n13 >>> 8);
        array2[n2] = (byte)(AESCrypt.S[n5 & 0xFF] ^ n13);
    }
    
    @Override
    void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        this.implDecryptBlock(array, n, array2, n2);
    }
    
    private void implDecryptBlock(final byte[] array, int n, final byte[] array2, int n2) {
        int n3 = 4;
        int n4 = (array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[n3++];
        int n5 = (array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[n3++];
        int n6 = (array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF)) ^ this.K[n3++];
        int n7 = (array[n++] << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF)) ^ this.K[n3++];
        if (this.ROUNDS_12) {
            final int n8 = AESCrypt.T5[n4 >>> 24] ^ AESCrypt.T6[n7 >>> 16 & 0xFF] ^ AESCrypt.T7[n6 >>> 8 & 0xFF] ^ AESCrypt.T8[n5 & 0xFF] ^ this.K[n3++];
            final int n9 = AESCrypt.T5[n5 >>> 24] ^ AESCrypt.T6[n4 >>> 16 & 0xFF] ^ AESCrypt.T7[n7 >>> 8 & 0xFF] ^ AESCrypt.T8[n6 & 0xFF] ^ this.K[n3++];
            final int n10 = AESCrypt.T5[n6 >>> 24] ^ AESCrypt.T6[n5 >>> 16 & 0xFF] ^ AESCrypt.T7[n4 >>> 8 & 0xFF] ^ AESCrypt.T8[n7 & 0xFF] ^ this.K[n3++];
            final int n11 = AESCrypt.T5[n7 >>> 24] ^ AESCrypt.T6[n6 >>> 16 & 0xFF] ^ AESCrypt.T7[n5 >>> 8 & 0xFF] ^ AESCrypt.T8[n4 & 0xFF] ^ this.K[n3++];
            n4 = (AESCrypt.T5[n8 >>> 24] ^ AESCrypt.T6[n11 >>> 16 & 0xFF] ^ AESCrypt.T7[n10 >>> 8 & 0xFF] ^ AESCrypt.T8[n9 & 0xFF] ^ this.K[n3++]);
            n5 = (AESCrypt.T5[n9 >>> 24] ^ AESCrypt.T6[n8 >>> 16 & 0xFF] ^ AESCrypt.T7[n11 >>> 8 & 0xFF] ^ AESCrypt.T8[n10 & 0xFF] ^ this.K[n3++]);
            n6 = (AESCrypt.T5[n10 >>> 24] ^ AESCrypt.T6[n9 >>> 16 & 0xFF] ^ AESCrypt.T7[n8 >>> 8 & 0xFF] ^ AESCrypt.T8[n11 & 0xFF] ^ this.K[n3++]);
            n7 = (AESCrypt.T5[n11 >>> 24] ^ AESCrypt.T6[n10 >>> 16 & 0xFF] ^ AESCrypt.T7[n9 >>> 8 & 0xFF] ^ AESCrypt.T8[n8 & 0xFF] ^ this.K[n3++]);
            if (this.ROUNDS_14) {
                final int n12 = AESCrypt.T5[n4 >>> 24] ^ AESCrypt.T6[n7 >>> 16 & 0xFF] ^ AESCrypt.T7[n6 >>> 8 & 0xFF] ^ AESCrypt.T8[n5 & 0xFF] ^ this.K[n3++];
                final int n13 = AESCrypt.T5[n5 >>> 24] ^ AESCrypt.T6[n4 >>> 16 & 0xFF] ^ AESCrypt.T7[n7 >>> 8 & 0xFF] ^ AESCrypt.T8[n6 & 0xFF] ^ this.K[n3++];
                final int n14 = AESCrypt.T5[n6 >>> 24] ^ AESCrypt.T6[n5 >>> 16 & 0xFF] ^ AESCrypt.T7[n4 >>> 8 & 0xFF] ^ AESCrypt.T8[n7 & 0xFF] ^ this.K[n3++];
                final int n15 = AESCrypt.T5[n7 >>> 24] ^ AESCrypt.T6[n6 >>> 16 & 0xFF] ^ AESCrypt.T7[n5 >>> 8 & 0xFF] ^ AESCrypt.T8[n4 & 0xFF] ^ this.K[n3++];
                n4 = (AESCrypt.T5[n12 >>> 24] ^ AESCrypt.T6[n15 >>> 16 & 0xFF] ^ AESCrypt.T7[n14 >>> 8 & 0xFF] ^ AESCrypt.T8[n13 & 0xFF] ^ this.K[n3++]);
                n5 = (AESCrypt.T5[n13 >>> 24] ^ AESCrypt.T6[n12 >>> 16 & 0xFF] ^ AESCrypt.T7[n15 >>> 8 & 0xFF] ^ AESCrypt.T8[n14 & 0xFF] ^ this.K[n3++]);
                n6 = (AESCrypt.T5[n14 >>> 24] ^ AESCrypt.T6[n13 >>> 16 & 0xFF] ^ AESCrypt.T7[n12 >>> 8 & 0xFF] ^ AESCrypt.T8[n15 & 0xFF] ^ this.K[n3++]);
                n7 = (AESCrypt.T5[n15 >>> 24] ^ AESCrypt.T6[n14 >>> 16 & 0xFF] ^ AESCrypt.T7[n13 >>> 8 & 0xFF] ^ AESCrypt.T8[n12 & 0xFF] ^ this.K[n3++]);
            }
        }
        final int n16 = AESCrypt.T5[n4 >>> 24] ^ AESCrypt.T6[n7 >>> 16 & 0xFF] ^ AESCrypt.T7[n6 >>> 8 & 0xFF] ^ AESCrypt.T8[n5 & 0xFF] ^ this.K[n3++];
        final int n17 = AESCrypt.T5[n5 >>> 24] ^ AESCrypt.T6[n4 >>> 16 & 0xFF] ^ AESCrypt.T7[n7 >>> 8 & 0xFF] ^ AESCrypt.T8[n6 & 0xFF] ^ this.K[n3++];
        final int n18 = AESCrypt.T5[n6 >>> 24] ^ AESCrypt.T6[n5 >>> 16 & 0xFF] ^ AESCrypt.T7[n4 >>> 8 & 0xFF] ^ AESCrypt.T8[n7 & 0xFF] ^ this.K[n3++];
        final int n19 = AESCrypt.T5[n7 >>> 24] ^ AESCrypt.T6[n6 >>> 16 & 0xFF] ^ AESCrypt.T7[n5 >>> 8 & 0xFF] ^ AESCrypt.T8[n4 & 0xFF] ^ this.K[n3++];
        final int n20 = AESCrypt.T5[n16 >>> 24] ^ AESCrypt.T6[n19 >>> 16 & 0xFF] ^ AESCrypt.T7[n18 >>> 8 & 0xFF] ^ AESCrypt.T8[n17 & 0xFF] ^ this.K[n3++];
        final int n21 = AESCrypt.T5[n17 >>> 24] ^ AESCrypt.T6[n16 >>> 16 & 0xFF] ^ AESCrypt.T7[n19 >>> 8 & 0xFF] ^ AESCrypt.T8[n18 & 0xFF] ^ this.K[n3++];
        final int n22 = AESCrypt.T5[n18 >>> 24] ^ AESCrypt.T6[n17 >>> 16 & 0xFF] ^ AESCrypt.T7[n16 >>> 8 & 0xFF] ^ AESCrypt.T8[n19 & 0xFF] ^ this.K[n3++];
        final int n23 = AESCrypt.T5[n19 >>> 24] ^ AESCrypt.T6[n18 >>> 16 & 0xFF] ^ AESCrypt.T7[n17 >>> 8 & 0xFF] ^ AESCrypt.T8[n16 & 0xFF] ^ this.K[n3++];
        final int n24 = AESCrypt.T5[n20 >>> 24] ^ AESCrypt.T6[n23 >>> 16 & 0xFF] ^ AESCrypt.T7[n22 >>> 8 & 0xFF] ^ AESCrypt.T8[n21 & 0xFF] ^ this.K[n3++];
        final int n25 = AESCrypt.T5[n21 >>> 24] ^ AESCrypt.T6[n20 >>> 16 & 0xFF] ^ AESCrypt.T7[n23 >>> 8 & 0xFF] ^ AESCrypt.T8[n22 & 0xFF] ^ this.K[n3++];
        final int n26 = AESCrypt.T5[n22 >>> 24] ^ AESCrypt.T6[n21 >>> 16 & 0xFF] ^ AESCrypt.T7[n20 >>> 8 & 0xFF] ^ AESCrypt.T8[n23 & 0xFF] ^ this.K[n3++];
        final int n27 = AESCrypt.T5[n23 >>> 24] ^ AESCrypt.T6[n22 >>> 16 & 0xFF] ^ AESCrypt.T7[n21 >>> 8 & 0xFF] ^ AESCrypt.T8[n20 & 0xFF] ^ this.K[n3++];
        final int n28 = AESCrypt.T5[n24 >>> 24] ^ AESCrypt.T6[n27 >>> 16 & 0xFF] ^ AESCrypt.T7[n26 >>> 8 & 0xFF] ^ AESCrypt.T8[n25 & 0xFF] ^ this.K[n3++];
        final int n29 = AESCrypt.T5[n25 >>> 24] ^ AESCrypt.T6[n24 >>> 16 & 0xFF] ^ AESCrypt.T7[n27 >>> 8 & 0xFF] ^ AESCrypt.T8[n26 & 0xFF] ^ this.K[n3++];
        final int n30 = AESCrypt.T5[n26 >>> 24] ^ AESCrypt.T6[n25 >>> 16 & 0xFF] ^ AESCrypt.T7[n24 >>> 8 & 0xFF] ^ AESCrypt.T8[n27 & 0xFF] ^ this.K[n3++];
        final int n31 = AESCrypt.T5[n27 >>> 24] ^ AESCrypt.T6[n26 >>> 16 & 0xFF] ^ AESCrypt.T7[n25 >>> 8 & 0xFF] ^ AESCrypt.T8[n24 & 0xFF] ^ this.K[n3++];
        final int n32 = AESCrypt.T5[n28 >>> 24] ^ AESCrypt.T6[n31 >>> 16 & 0xFF] ^ AESCrypt.T7[n30 >>> 8 & 0xFF] ^ AESCrypt.T8[n29 & 0xFF] ^ this.K[n3++];
        final int n33 = AESCrypt.T5[n29 >>> 24] ^ AESCrypt.T6[n28 >>> 16 & 0xFF] ^ AESCrypt.T7[n31 >>> 8 & 0xFF] ^ AESCrypt.T8[n30 & 0xFF] ^ this.K[n3++];
        final int n34 = AESCrypt.T5[n30 >>> 24] ^ AESCrypt.T6[n29 >>> 16 & 0xFF] ^ AESCrypt.T7[n28 >>> 8 & 0xFF] ^ AESCrypt.T8[n31 & 0xFF] ^ this.K[n3++];
        final int n35 = AESCrypt.T5[n31 >>> 24] ^ AESCrypt.T6[n30 >>> 16 & 0xFF] ^ AESCrypt.T7[n29 >>> 8 & 0xFF] ^ AESCrypt.T8[n28 & 0xFF] ^ this.K[n3++];
        final int n36 = AESCrypt.T5[n32 >>> 24] ^ AESCrypt.T6[n35 >>> 16 & 0xFF] ^ AESCrypt.T7[n34 >>> 8 & 0xFF] ^ AESCrypt.T8[n33 & 0xFF] ^ this.K[n3++];
        final int n37 = AESCrypt.T5[n33 >>> 24] ^ AESCrypt.T6[n32 >>> 16 & 0xFF] ^ AESCrypt.T7[n35 >>> 8 & 0xFF] ^ AESCrypt.T8[n34 & 0xFF] ^ this.K[n3++];
        final int n38 = AESCrypt.T5[n34 >>> 24] ^ AESCrypt.T6[n33 >>> 16 & 0xFF] ^ AESCrypt.T7[n32 >>> 8 & 0xFF] ^ AESCrypt.T8[n35 & 0xFF] ^ this.K[n3++];
        final int n39 = AESCrypt.T5[n35 >>> 24] ^ AESCrypt.T6[n34 >>> 16 & 0xFF] ^ AESCrypt.T7[n33 >>> 8 & 0xFF] ^ AESCrypt.T8[n32 & 0xFF] ^ this.K[n3++];
        final int n40 = AESCrypt.T5[n36 >>> 24] ^ AESCrypt.T6[n39 >>> 16 & 0xFF] ^ AESCrypt.T7[n38 >>> 8 & 0xFF] ^ AESCrypt.T8[n37 & 0xFF] ^ this.K[n3++];
        final int n41 = AESCrypt.T5[n37 >>> 24] ^ AESCrypt.T6[n36 >>> 16 & 0xFF] ^ AESCrypt.T7[n39 >>> 8 & 0xFF] ^ AESCrypt.T8[n38 & 0xFF] ^ this.K[n3++];
        final int n42 = AESCrypt.T5[n38 >>> 24] ^ AESCrypt.T6[n37 >>> 16 & 0xFF] ^ AESCrypt.T7[n36 >>> 8 & 0xFF] ^ AESCrypt.T8[n39 & 0xFF] ^ this.K[n3++];
        final int n43 = AESCrypt.T5[n39 >>> 24] ^ AESCrypt.T6[n38 >>> 16 & 0xFF] ^ AESCrypt.T7[n37 >>> 8 & 0xFF] ^ AESCrypt.T8[n36 & 0xFF] ^ this.K[n3++];
        final int n44 = AESCrypt.T5[n40 >>> 24] ^ AESCrypt.T6[n43 >>> 16 & 0xFF] ^ AESCrypt.T7[n42 >>> 8 & 0xFF] ^ AESCrypt.T8[n41 & 0xFF] ^ this.K[n3++];
        final int n45 = AESCrypt.T5[n41 >>> 24] ^ AESCrypt.T6[n40 >>> 16 & 0xFF] ^ AESCrypt.T7[n43 >>> 8 & 0xFF] ^ AESCrypt.T8[n42 & 0xFF] ^ this.K[n3++];
        final int n46 = AESCrypt.T5[n42 >>> 24] ^ AESCrypt.T6[n41 >>> 16 & 0xFF] ^ AESCrypt.T7[n40 >>> 8 & 0xFF] ^ AESCrypt.T8[n43 & 0xFF] ^ this.K[n3++];
        final int n47 = AESCrypt.T5[n43 >>> 24] ^ AESCrypt.T6[n42 >>> 16 & 0xFF] ^ AESCrypt.T7[n41 >>> 8 & 0xFF] ^ AESCrypt.T8[n40 & 0xFF] ^ this.K[n3++];
        final int n48 = AESCrypt.T5[n44 >>> 24] ^ AESCrypt.T6[n47 >>> 16 & 0xFF] ^ AESCrypt.T7[n46 >>> 8 & 0xFF] ^ AESCrypt.T8[n45 & 0xFF] ^ this.K[n3++];
        final int n49 = AESCrypt.T5[n45 >>> 24] ^ AESCrypt.T6[n44 >>> 16 & 0xFF] ^ AESCrypt.T7[n47 >>> 8 & 0xFF] ^ AESCrypt.T8[n46 & 0xFF] ^ this.K[n3++];
        final int n50 = AESCrypt.T5[n46 >>> 24] ^ AESCrypt.T6[n45 >>> 16 & 0xFF] ^ AESCrypt.T7[n44 >>> 8 & 0xFF] ^ AESCrypt.T8[n47 & 0xFF] ^ this.K[n3++];
        final int n51 = AESCrypt.T5[n47 >>> 24] ^ AESCrypt.T6[n46 >>> 16 & 0xFF] ^ AESCrypt.T7[n45 >>> 8 & 0xFF] ^ AESCrypt.T8[n44 & 0xFF] ^ this.K[n3++];
        final int n52 = this.K[0];
        array2[n2++] = (byte)(AESCrypt.Si[n48 >>> 24] ^ n52 >>> 24);
        array2[n2++] = (byte)(AESCrypt.Si[n51 >>> 16 & 0xFF] ^ n52 >>> 16);
        array2[n2++] = (byte)(AESCrypt.Si[n50 >>> 8 & 0xFF] ^ n52 >>> 8);
        array2[n2++] = (byte)(AESCrypt.Si[n49 & 0xFF] ^ n52);
        final int n53 = this.K[1];
        array2[n2++] = (byte)(AESCrypt.Si[n49 >>> 24] ^ n53 >>> 24);
        array2[n2++] = (byte)(AESCrypt.Si[n48 >>> 16 & 0xFF] ^ n53 >>> 16);
        array2[n2++] = (byte)(AESCrypt.Si[n51 >>> 8 & 0xFF] ^ n53 >>> 8);
        array2[n2++] = (byte)(AESCrypt.Si[n50 & 0xFF] ^ n53);
        final int n54 = this.K[2];
        array2[n2++] = (byte)(AESCrypt.Si[n50 >>> 24] ^ n54 >>> 24);
        array2[n2++] = (byte)(AESCrypt.Si[n49 >>> 16 & 0xFF] ^ n54 >>> 16);
        array2[n2++] = (byte)(AESCrypt.Si[n48 >>> 8 & 0xFF] ^ n54 >>> 8);
        array2[n2++] = (byte)(AESCrypt.Si[n51 & 0xFF] ^ n54);
        final int n55 = this.K[3];
        array2[n2++] = (byte)(AESCrypt.Si[n51 >>> 24] ^ n55 >>> 24);
        array2[n2++] = (byte)(AESCrypt.Si[n50 >>> 16 & 0xFF] ^ n55 >>> 16);
        array2[n2++] = (byte)(AESCrypt.Si[n49 >>> 8 & 0xFF] ^ n55 >>> 8);
        array2[n2] = (byte)(AESCrypt.Si[n48 & 0xFF] ^ n55);
    }
    
    private void makeSessionKey(final byte[] array) throws InvalidKeyException {
        if (array == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (!isKeySizeValid(array.length)) {
            throw new InvalidKeyException("Invalid AES key length: " + array.length + " bytes");
        }
        final int rounds = getRounds(array.length);
        final int n = (rounds + 1) * 4;
        final int n2 = 4;
        final int[][] array2 = new int[rounds + 1][4];
        final int[][] array3 = new int[rounds + 1][4];
        final int n3 = array.length / 4;
        final int[] array4 = new int[n3];
        for (int i = 0, n4 = 0; i < n3; ++i, n4 += 4) {
            array4[i] = (array[n4] << 24 | (array[n4 + 1] & 0xFF) << 16 | (array[n4 + 2] & 0xFF) << 8 | (array[n4 + 3] & 0xFF));
        }
        int j = 0;
        for (int n5 = 0; n5 < n3 && j < n; ++n5, ++j) {
            array2[j / 4][j % 4] = array4[n5];
            array3[rounds - j / 4][j % 4] = array4[n5];
        }
        int n6 = 0;
        while (j < n) {
            final int n7 = array4[n3 - 1];
            final int[] array5 = array4;
            final int n8 = 0;
            array5[n8] ^= (AESCrypt.S[n7 >>> 16 & 0xFF] << 24 ^ (AESCrypt.S[n7 >>> 8 & 0xFF] & 0xFF) << 16 ^ (AESCrypt.S[n7 & 0xFF] & 0xFF) << 8 ^ (AESCrypt.S[n7 >>> 24] & 0xFF) ^ AESCrypt.rcon[n6++] << 24);
            if (n3 != 8) {
                for (int k = 1, n9 = 0; k < n3; ++k, ++n9) {
                    final int[] array6 = array4;
                    final int n10 = k;
                    array6[n10] ^= array4[n9];
                }
            }
            else {
                for (int l = 1, n11 = 0; l < n3 / 2; ++l, ++n11) {
                    final int[] array7 = array4;
                    final int n12 = l;
                    array7[n12] ^= array4[n11];
                }
                final int n13 = array4[n3 / 2 - 1];
                final int[] array8 = array4;
                final int n14 = n3 / 2;
                array8[n14] ^= ((AESCrypt.S[n13 & 0xFF] & 0xFF) ^ (AESCrypt.S[n13 >>> 8 & 0xFF] & 0xFF) << 8 ^ (AESCrypt.S[n13 >>> 16 & 0xFF] & 0xFF) << 16 ^ AESCrypt.S[n13 >>> 24] << 24);
                for (int n15 = n3 / 2, n16 = n15 + 1; n16 < n3; ++n16, ++n15) {
                    final int[] array9 = array4;
                    final int n17 = n16;
                    array9[n17] ^= array4[n15];
                }
            }
            for (int n18 = 0; n18 < n3 && j < n; ++n18, ++j) {
                array2[j / 4][j % 4] = array4[n18];
                array3[rounds - j / 4][j % 4] = array4[n18];
            }
        }
        for (int n19 = 1; n19 < rounds; ++n19) {
            for (int n20 = 0; n20 < n2; ++n20) {
                final int n21 = array3[n19][n20];
                array3[n19][n20] = (AESCrypt.U1[n21 >>> 24 & 0xFF] ^ AESCrypt.U2[n21 >>> 16 & 0xFF] ^ AESCrypt.U3[n21 >>> 8 & 0xFF] ^ AESCrypt.U4[n21 & 0xFF]);
            }
        }
        final int[] expandToSubKey = expandToSubKey(array2, false);
        final int[] expandToSubKey2 = expandToSubKey(array3, true);
        this.ROUNDS_12 = (rounds >= 12);
        this.ROUNDS_14 = (rounds == 14);
        this.limit = rounds * 4;
        this.sessionK = new int[][] { expandToSubKey, expandToSubKey2 };
    }
    
    private static int getRounds(final int n) {
        return (n >> 2) + 6;
    }
    
    static {
        AESCrypt.alog = new int[256];
        AESCrypt.log = new int[256];
        S = new byte[256];
        Si = new byte[256];
        T1 = new int[256];
        T2 = new int[256];
        T3 = new int[256];
        T4 = new int[256];
        T5 = new int[256];
        T6 = new int[256];
        T7 = new int[256];
        T8 = new int[256];
        U1 = new int[256];
        U2 = new int[256];
        U3 = new int[256];
        U4 = new int[256];
        rcon = new byte[30];
        final int n = 283;
        AESCrypt.alog[0] = 1;
        for (int i = 1; i < 256; ++i) {
            int n2 = AESCrypt.alog[i - 1] << 1 ^ AESCrypt.alog[i - 1];
            if ((n2 & 0x100) != 0x0) {
                n2 ^= n;
            }
            AESCrypt.alog[i] = n2;
        }
        for (int j = 1; j < 255; ++j) {
            AESCrypt.log[AESCrypt.alog[j]] = j;
        }
        final byte[][] array = { { 1, 1, 1, 1, 1, 0, 0, 0 }, { 0, 1, 1, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 1, 1, 0 }, { 0, 0, 0, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 0, 1, 1, 1 }, { 1, 1, 1, 0, 0, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0, 0, 1 } };
        final byte[] array2 = { 0, 1, 1, 0, 0, 0, 1, 1 };
        final byte[][] array3 = new byte[256][8];
        array3[1][7] = 1;
        for (int k = 2; k < 256; ++k) {
            final int n3 = AESCrypt.alog[255 - AESCrypt.log[k]];
            for (int l = 0; l < 8; ++l) {
                array3[k][l] = (byte)(n3 >>> 7 - l & 0x1);
            }
        }
        final byte[][] array4 = new byte[256][8];
        for (int n4 = 0; n4 < 256; ++n4) {
            for (int n5 = 0; n5 < 8; ++n5) {
                array4[n4][n5] = array2[n5];
                for (int n6 = 0; n6 < 8; ++n6) {
                    final byte[] array5 = array4[n4];
                    final int n7 = n5;
                    array5[n7] ^= (byte)(array[n5][n6] * array3[n4][n6]);
                }
            }
        }
        for (int n8 = 0; n8 < 256; ++n8) {
            AESCrypt.S[n8] = (byte)(array4[n8][0] << 7);
            for (int n9 = 1; n9 < 8; ++n9) {
                final byte[] s = AESCrypt.S;
                final int n10 = n8;
                s[n10] ^= (byte)(array4[n8][n9] << 7 - n9);
            }
            AESCrypt.Si[AESCrypt.S[n8] & 0xFF] = (byte)n8;
        }
        final byte[][] array6 = { { 2, 1, 1, 3 }, { 3, 2, 1, 1 }, { 1, 3, 2, 1 }, { 1, 1, 3, 2 } };
        final byte[][] array7 = new byte[4][8];
        for (int n11 = 0; n11 < 4; ++n11) {
            for (int n12 = 0; n12 < 4; ++n12) {
                array7[n11][n12] = array6[n11][n12];
            }
            array7[n11][n11 + 4] = 1;
        }
        final byte[][] array8 = new byte[4][4];
        for (int n13 = 0; n13 < 4; ++n13) {
            byte b = array7[n13][n13];
            if (b == 0) {
                int n14;
                for (n14 = n13 + 1; array7[n14][n13] == 0 && n14 < 4; ++n14) {}
                if (n14 == 4) {
                    throw new RuntimeException("G matrix is not invertible");
                }
                for (int n15 = 0; n15 < 8; ++n15) {
                    final byte b2 = array7[n13][n15];
                    array7[n13][n15] = array7[n14][n15];
                    array7[n14][n15] = b2;
                }
                b = array7[n13][n13];
            }
            for (int n16 = 0; n16 < 8; ++n16) {
                if (array7[n13][n16] != 0) {
                    array7[n13][n16] = (byte)AESCrypt.alog[(255 + AESCrypt.log[array7[n13][n16] & 0xFF] - AESCrypt.log[b & 0xFF]) % 255];
                }
            }
            for (int n17 = 0; n17 < 4; ++n17) {
                if (n13 != n17) {
                    for (int n18 = n13 + 1; n18 < 8; ++n18) {
                        final byte[] array9 = array7[n17];
                        final int n19 = n18;
                        array9[n19] ^= (byte)mul(array7[n13][n18], array7[n17][n13]);
                    }
                    array7[n17][n13] = 0;
                }
            }
        }
        for (int n20 = 0; n20 < 4; ++n20) {
            for (int n21 = 0; n21 < 4; ++n21) {
                array8[n20][n21] = array7[n20][n21 + 4];
            }
        }
        for (int n22 = 0; n22 < 256; ++n22) {
            final byte b3 = AESCrypt.S[n22];
            AESCrypt.T1[n22] = mul4(b3, array6[0]);
            AESCrypt.T2[n22] = mul4(b3, array6[1]);
            AESCrypt.T3[n22] = mul4(b3, array6[2]);
            AESCrypt.T4[n22] = mul4(b3, array6[3]);
            final byte b4 = AESCrypt.Si[n22];
            AESCrypt.T5[n22] = mul4(b4, array8[0]);
            AESCrypt.T6[n22] = mul4(b4, array8[1]);
            AESCrypt.T7[n22] = mul4(b4, array8[2]);
            AESCrypt.T8[n22] = mul4(b4, array8[3]);
            AESCrypt.U1[n22] = mul4(n22, array8[0]);
            AESCrypt.U2[n22] = mul4(n22, array8[1]);
            AESCrypt.U3[n22] = mul4(n22, array8[2]);
            AESCrypt.U4[n22] = mul4(n22, array8[3]);
        }
        AESCrypt.rcon[0] = 1;
        int mul = 1;
        for (int n23 = 1; n23 < 30; ++n23) {
            mul = mul(2, mul);
            AESCrypt.rcon[n23] = (byte)mul;
        }
        AESCrypt.log = null;
        AESCrypt.alog = null;
    }
}
