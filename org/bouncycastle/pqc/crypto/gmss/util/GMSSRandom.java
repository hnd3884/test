package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class GMSSRandom
{
    private Digest messDigestTree;
    
    public GMSSRandom(final Digest messDigestTree) {
        this.messDigestTree = messDigestTree;
    }
    
    public byte[] nextSeed(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        this.messDigestTree.update(array, 0, array.length);
        final byte[] array3 = new byte[this.messDigestTree.getDigestSize()];
        this.messDigestTree.doFinal(array3, 0);
        this.addByteArrays(array, array3);
        this.addOne(array);
        return array3;
    }
    
    private void addByteArrays(final byte[] array, final byte[] array2) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final int n2 = (0xFF & array[i]) + (0xFF & array2[i]) + n;
            array[i] = (byte)n2;
            n = (byte)(n2 >> 8);
        }
    }
    
    private void addOne(final byte[] array) {
        int n = 1;
        for (int i = 0; i < array.length; ++i) {
            final int n2 = (0xFF & array[i]) + n;
            array[i] = (byte)n2;
            n = (byte)(n2 >> 8);
        }
    }
}
