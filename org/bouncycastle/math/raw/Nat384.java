package org.bouncycastle.math.raw;

public abstract class Nat384
{
    public static void mul(final int[] array, final int[] array2, final int[] array3) {
        Nat192.mul(array, array2, array3);
        Nat192.mul(array, 6, array2, 6, array3, 12);
        final int addToEachOther = Nat192.addToEachOther(array3, 6, array3, 12);
        final int n = addToEachOther + Nat192.addTo(array3, 18, array3, 12, addToEachOther + Nat192.addTo(array3, 0, array3, 6, 0));
        final int[] create = Nat192.create();
        final int[] create2 = Nat192.create();
        final boolean b = Nat192.diff(array, 6, array, 0, create, 0) != Nat192.diff(array2, 6, array2, 0, create2, 0);
        final int[] ext = Nat192.createExt();
        Nat192.mul(create, create2, ext);
        Nat.addWordAt(24, n + (b ? Nat.addTo(12, ext, 0, array3, 6) : Nat.subFrom(12, ext, 0, array3, 6)), array3, 18);
    }
    
    public static void square(final int[] array, final int[] array2) {
        Nat192.square(array, array2);
        Nat192.square(array, 6, array2, 12);
        final int addToEachOther = Nat192.addToEachOther(array2, 6, array2, 12);
        final int n = addToEachOther + Nat192.addTo(array2, 18, array2, 12, addToEachOther + Nat192.addTo(array2, 0, array2, 6, 0));
        final int[] create = Nat192.create();
        Nat192.diff(array, 6, array, 0, create, 0);
        final int[] ext = Nat192.createExt();
        Nat192.square(create, ext);
        Nat.addWordAt(24, n + Nat.subFrom(12, ext, 0, array2, 6), array2, 18);
    }
}
