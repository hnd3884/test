package org.apache.xerces.impl.xs.util;

public final class XIntPool
{
    private static final short POOL_SIZE = 10;
    private static final XInt[] fXIntPool;
    
    public final XInt getXInt(final int n) {
        if (n >= 0 && n < XIntPool.fXIntPool.length) {
            return XIntPool.fXIntPool[n];
        }
        return new XInt(n);
    }
    
    static {
        fXIntPool = new XInt[10];
        for (int i = 0; i < 10; ++i) {
            XIntPool.fXIntPool[i] = new XInt(i);
        }
    }
}
