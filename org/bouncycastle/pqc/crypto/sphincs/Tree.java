package org.bouncycastle.pqc.crypto.sphincs;

class Tree
{
    static void l_tree(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3) {
        int n4 = 67;
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < n4 >>> 1; ++j) {
                hashFunctions.hash_2n_n_mask(array2, n2 + j * 32, array2, n2 + j * 2 * 32, array3, n3 + i * 2 * 32);
            }
            if ((n4 & 0x1) != 0x0) {
                System.arraycopy(array2, n2 + (n4 - 1) * 32, array2, n2 + (n4 >>> 1) * 32, 32);
                n4 = (n4 >>> 1) + 1;
            }
            else {
                n4 >>>= 1;
            }
        }
        System.arraycopy(array2, n2, array, n, 32);
    }
    
    static void treehash(final HashFunctions hashFunctions, final byte[] array, final int n, final int n2, final byte[] array2, final leafaddr leafaddr, final byte[] array3, final int n3) {
        final leafaddr leafaddr2 = new leafaddr(leafaddr);
        final byte[] array4 = new byte[(n2 + 1) * 32];
        final int[] array5 = new int[n2 + 1];
        int n4 = 0;
        while (leafaddr2.subleaf < (int)(leafaddr2.subleaf + (1 << n2))) {
            gen_leaf_wots(hashFunctions, array4, n4 * 32, array3, n3, array2, leafaddr2);
            array5[n4] = 0;
            ++n4;
            while (n4 > 1 && array5[n4 - 1] == array5[n4 - 2]) {
                hashFunctions.hash_2n_n_mask(array4, (n4 - 2) * 32, array4, (n4 - 2) * 32, array3, n3 + 2 * (array5[n4 - 1] + 7) * 32);
                final int[] array6 = array5;
                final int n5 = n4 - 2;
                ++array6[n5];
                --n4;
            }
            final leafaddr leafaddr3 = leafaddr2;
            ++leafaddr3.subleaf;
        }
        for (int i = 0; i < 32; ++i) {
            array[n + i] = array4[i];
        }
    }
    
    static void gen_leaf_wots(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final leafaddr leafaddr) {
        final byte[] array4 = new byte[32];
        final byte[] array5 = new byte[2144];
        final Wots wots = new Wots();
        Seed.get_seed(hashFunctions, array4, 0, array3, leafaddr);
        wots.wots_pkgen(hashFunctions, array5, 0, array4, 0, array2, n2);
        l_tree(hashFunctions, array, n, array5, 0, array2, n2);
    }
    
    static class leafaddr
    {
        int level;
        long subtree;
        long subleaf;
        
        public leafaddr() {
        }
        
        public leafaddr(final leafaddr leafaddr) {
            this.level = leafaddr.level;
            this.subtree = leafaddr.subtree;
            this.subleaf = leafaddr.subleaf;
        }
    }
}
