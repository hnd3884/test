package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XIntPool
{
    private static final short POOL_SIZE = 10;
    private static final XInt[] fXIntPool;
    
    public final XInt getXInt(final int value) {
        if (value >= 0 && value < XIntPool.fXIntPool.length) {
            return XIntPool.fXIntPool[value];
        }
        return new XInt(value);
    }
    
    static {
        fXIntPool = new XInt[10];
        for (int i = 0; i < 10; ++i) {
            XIntPool.fXIntPool[i] = new XInt(i);
        }
    }
}
