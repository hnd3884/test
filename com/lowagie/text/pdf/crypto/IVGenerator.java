package com.lowagie.text.pdf.crypto;

public final class IVGenerator
{
    private static ARCFOUREncryption arcfour;
    
    private IVGenerator() {
    }
    
    public static byte[] getIV() {
        return getIV(16);
    }
    
    public static byte[] getIV(final int len) {
        final byte[] b = new byte[len];
        synchronized (IVGenerator.arcfour) {
            IVGenerator.arcfour.encryptARCFOUR(b);
        }
        return b;
    }
    
    static {
        IVGenerator.arcfour = new ARCFOUREncryption();
        final long time = System.currentTimeMillis();
        final long mem = Runtime.getRuntime().freeMemory();
        final String s = time + "+" + mem;
        IVGenerator.arcfour.prepareARCFOURKey(s.getBytes());
    }
}
