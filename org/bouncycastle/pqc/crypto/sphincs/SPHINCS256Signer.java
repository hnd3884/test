package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class SPHINCS256Signer implements MessageSigner
{
    private final HashFunctions hashFunctions;
    private byte[] keyData;
    
    public SPHINCS256Signer(final Digest digest, final Digest digest2) {
        if (digest.getDigestSize() != 32) {
            throw new IllegalArgumentException("n-digest needs to produce 32 bytes of output");
        }
        if (digest2.getDigestSize() != 64) {
            throw new IllegalArgumentException("2n-digest needs to produce 64 bytes of output");
        }
        this.hashFunctions = new HashFunctions(digest, digest2);
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (b) {
            this.keyData = ((SPHINCSPrivateKeyParameters)cipherParameters).getKeyData();
        }
        else {
            this.keyData = ((SPHINCSPublicKeyParameters)cipherParameters).getKeyData();
        }
    }
    
    public byte[] generateSignature(final byte[] array) {
        return this.crypto_sign(this.hashFunctions, array, this.keyData);
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        return this.verify(this.hashFunctions, array, array2, this.keyData);
    }
    
    static void validate_authpath(final HashFunctions hashFunctions, final byte[] array, final byte[] array2, int n, final byte[] array3, final int n2, final byte[] array4, final int n3) {
        final byte[] array5 = new byte[64];
        if ((n & 0x1) != 0x0) {
            for (int i = 0; i < 32; ++i) {
                array5[32 + i] = array2[i];
            }
            for (int j = 0; j < 32; ++j) {
                array5[j] = array3[n2 + j];
            }
        }
        else {
            for (int k = 0; k < 32; ++k) {
                array5[k] = array2[k];
            }
            for (int l = 0; l < 32; ++l) {
                array5[32 + l] = array3[n2 + l];
            }
        }
        int n4 = n2 + 32;
        for (int n5 = 0; n5 < n3 - 1; ++n5) {
            n >>>= 1;
            if ((n & 0x1) != 0x0) {
                hashFunctions.hash_2n_n_mask(array5, 32, array5, 0, array4, 2 * (7 + n5) * 32);
                for (int n6 = 0; n6 < 32; ++n6) {
                    array5[n6] = array3[n4 + n6];
                }
            }
            else {
                hashFunctions.hash_2n_n_mask(array5, 0, array5, 0, array4, 2 * (7 + n5) * 32);
                for (int n7 = 0; n7 < 32; ++n7) {
                    array5[n7 + 32] = array3[n4 + n7];
                }
            }
            n4 += 32;
        }
        hashFunctions.hash_2n_n_mask(array, 0, array5, 0, array4, 2 * (7 + n3 - 1) * 32);
    }
    
    static void compute_authpath_wots(final HashFunctions hashFunctions, final byte[] array, final byte[] array2, final int n, final Tree.leafaddr leafaddr, final byte[] array3, final byte[] array4, final int n2) {
        final Tree.leafaddr leafaddr2 = new Tree.leafaddr(leafaddr);
        final byte[] array5 = new byte[2048];
        final byte[] array6 = new byte[1024];
        final byte[] array7 = new byte[68608];
        leafaddr2.subleaf = 0L;
        while (leafaddr2.subleaf < 32L) {
            Seed.get_seed(hashFunctions, array6, (int)(leafaddr2.subleaf * 32L), array3, leafaddr2);
            final Tree.leafaddr leafaddr3 = leafaddr2;
            ++leafaddr3.subleaf;
        }
        final Wots wots = new Wots();
        leafaddr2.subleaf = 0L;
        while (leafaddr2.subleaf < 32L) {
            wots.wots_pkgen(hashFunctions, array7, (int)(leafaddr2.subleaf * 67L * 32L), array6, (int)(leafaddr2.subleaf * 32L), array4, 0);
            final Tree.leafaddr leafaddr4 = leafaddr2;
            ++leafaddr4.subleaf;
        }
        leafaddr2.subleaf = 0L;
        while (leafaddr2.subleaf < 32L) {
            Tree.l_tree(hashFunctions, array5, (int)(1024L + leafaddr2.subleaf * 32L), array7, (int)(leafaddr2.subleaf * 67L * 32L), array4, 0);
            final Tree.leafaddr leafaddr5 = leafaddr2;
            ++leafaddr5.subleaf;
        }
        int n3 = 0;
        for (int i = 32; i > 0; i >>>= 1) {
            for (int j = 0; j < i; j += 2) {
                hashFunctions.hash_2n_n_mask(array5, (i >>> 1) * 32 + (j >>> 1) * 32, array5, i * 32 + j * 32, array4, 2 * (7 + n3) * 32);
            }
            ++n3;
        }
        final int n4 = (int)leafaddr.subleaf;
        for (int k = 0; k < n2; ++k) {
            System.arraycopy(array5, (32 >>> k) * 32 + (n4 >>> k ^ 0x1) * 32, array2, n + k * 32, 32);
        }
        System.arraycopy(array5, 32, array, 0, 32);
    }
    
    byte[] crypto_sign(final HashFunctions hashFunctions, final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[41000];
        final byte[] array4 = new byte[32];
        final byte[] array5 = new byte[64];
        final long[] array6 = new long[8];
        final byte[] array7 = new byte[32];
        final byte[] array8 = new byte[32];
        final byte[] array9 = new byte[1024];
        final byte[] array10 = new byte[1088];
        for (int i = 0; i < 1088; ++i) {
            array10[i] = array2[i];
        }
        final int n = 40968;
        System.arraycopy(array10, 1056, array3, n, 32);
        final Digest messageHash = hashFunctions.getMessageHash();
        final byte[] array11 = new byte[messageHash.getDigestSize()];
        messageHash.update(array3, n, 32);
        messageHash.update(array, 0, array.length);
        messageHash.doFinal(array11, 0);
        this.zerobytes(array3, n, 32);
        for (int j = 0; j != array6.length; ++j) {
            array6[j] = Pack.littleEndianToLong(array11, j * 8);
        }
        final long n2 = array6[0] & 0xFFFFFFFFFFFFFFFL;
        System.arraycopy(array11, 16, array4, 0, 32);
        final int n3 = 39912;
        System.arraycopy(array4, 0, array3, n3, 32);
        final Tree.leafaddr leafaddr = new Tree.leafaddr();
        leafaddr.level = 11;
        leafaddr.subtree = 0L;
        leafaddr.subleaf = 0L;
        final int n4 = n3 + 32;
        System.arraycopy(array10, 32, array3, n4, 1024);
        Tree.treehash(hashFunctions, array3, n4 + 1024, 5, array10, leafaddr, array3, n4);
        final Digest messageHash2 = hashFunctions.getMessageHash();
        messageHash2.update(array3, n3, 1088);
        messageHash2.update(array, 0, array.length);
        messageHash2.doFinal(array5, 0);
        final Tree.leafaddr leafaddr2 = new Tree.leafaddr();
        leafaddr2.level = 12;
        leafaddr2.subleaf = (int)(n2 & 0x1FL);
        leafaddr2.subtree = n2 >>> 5;
        for (int k = 0; k < 32; ++k) {
            array3[k] = array4[k];
        }
        int n5 = 32;
        System.arraycopy(array10, 32, array9, 0, 1024);
        for (int l = 0; l < 8; ++l) {
            array3[n5 + l] = (byte)(n2 >>> 8 * l & 0xFFL);
        }
        n5 += 8;
        Seed.get_seed(hashFunctions, array8, 0, array10, leafaddr2);
        final Horst horst = new Horst();
        int n6 = n5 + Horst.horst_sign(hashFunctions, array3, n5, array7, array8, array9, array5);
        final Wots wots = new Wots();
        for (int level = 0; level < 12; ++level) {
            leafaddr2.level = level;
            Seed.get_seed(hashFunctions, array8, 0, array10, leafaddr2);
            wots.wots_sign(hashFunctions, array3, n6, array7, array8, array9);
            n6 += 2144;
            compute_authpath_wots(hashFunctions, array7, array3, n6, leafaddr2, array10, array9, 5);
            n6 += 160;
            leafaddr2.subleaf = (int)(leafaddr2.subtree & 0x1FL);
            final Tree.leafaddr leafaddr3 = leafaddr2;
            leafaddr3.subtree >>>= 5;
        }
        this.zerobytes(array10, 0, 1088);
        return array3;
    }
    
    private void zerobytes(final byte[] array, final int n, final int n2) {
        for (int i = 0; i != n2; ++i) {
            array[n + i] = 0;
        }
    }
    
    boolean verify(final HashFunctions hashFunctions, final byte[] array, final byte[] array2, final byte[] array3) {
        int length = array2.length;
        long n = 0L;
        final byte[] array4 = new byte[2144];
        final byte[] array5 = new byte[32];
        final byte[] array6 = new byte[32];
        final byte[] array7 = new byte[41000];
        final byte[] array8 = new byte[1056];
        if (length != 41000) {
            throw new IllegalArgumentException("signature wrong size");
        }
        final byte[] array9 = new byte[64];
        for (int i = 0; i < 1056; ++i) {
            array8[i] = array3[i];
        }
        final byte[] array10 = new byte[32];
        for (int j = 0; j < 32; ++j) {
            array10[j] = array2[j];
        }
        System.arraycopy(array2, 0, array7, 0, 41000);
        final Digest messageHash = hashFunctions.getMessageHash();
        messageHash.update(array10, 0, 32);
        messageHash.update(array8, 0, 1056);
        messageHash.update(array, 0, array.length);
        messageHash.doFinal(array9, 0);
        int n2 = 0;
        n2 += 32;
        length -= 32;
        for (int k = 0; k < 8; ++k) {
            n ^= (long)(array7[n2 + k] & 0xFF) << 8 * k;
        }
        new Horst();
        Horst.horst_verify(hashFunctions, array6, array7, n2 + 8, array8, array9);
        n2 += 8;
        length -= 8;
        n2 += 13312;
        length -= 13312;
        final Wots wots = new Wots();
        for (int l = 0; l < 12; ++l) {
            wots.wots_verify(hashFunctions, array4, array7, n2, array6, array8);
            n2 += 2144;
            length -= 2144;
            Tree.l_tree(hashFunctions, array5, 0, array4, 0, array8, 0);
            validate_authpath(hashFunctions, array6, array5, (int)(n & 0x1FL), array7, n2, array8, 5);
            n >>= 5;
            n2 += 160;
            length -= 160;
        }
        boolean b = true;
        for (int n3 = 0; n3 < 32; ++n3) {
            if (array6[n3] != array8[n3 + 1024]) {
                b = false;
            }
        }
        return b;
    }
}
