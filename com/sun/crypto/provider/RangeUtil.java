package com.sun.crypto.provider;

import java.security.ProviderException;
import java.util.List;
import java.util.function.BiFunction;

final class RangeUtil
{
    private static final BiFunction<String, List<Integer>, ArrayIndexOutOfBoundsException> AIOOBE_SUPPLIER;
    
    public static void blockSizeCheck(final int n, final int n2) {
        if (n % n2 != 0) {
            throw new ProviderException("Internal error in input buffering");
        }
    }
    
    public static void nullAndBoundsCheck(final byte[] array, final int n, final int n2) {
        Preconditions.checkFromIndexSize(n, n2, array.length, RangeUtil.AIOOBE_SUPPLIER);
    }
    
    static {
        AIOOBE_SUPPLIER = Preconditions.outOfBoundsExceptionFormatter(ArrayIndexOutOfBoundsException::new);
    }
}
