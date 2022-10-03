package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.slf4j.internal.Logger;
import java.io.ByteArrayOutputStream;

public class DigesterOutputStream extends ByteArrayOutputStream
{
    private static final Logger LOG;
    final MessageDigestAlgorithm mda;
    
    public DigesterOutputStream(final MessageDigestAlgorithm mda) {
        this.mda = mda;
    }
    
    @Override
    public void write(final byte[] array) {
        this.write(array, 0, array.length);
    }
    
    @Override
    public void write(final int n) {
        this.mda.update((byte)n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        if (DigesterOutputStream.LOG.isDebugEnabled()) {
            DigesterOutputStream.LOG.debug("Pre-digested input:");
            final StringBuilder sb = new StringBuilder(n2);
            for (int i = n; i < n + n2; ++i) {
                sb.append((char)array[i]);
            }
            DigesterOutputStream.LOG.debug(sb.toString());
        }
        this.mda.update(array, n, n2);
    }
    
    public byte[] getDigestValue() {
        return this.mda.digest();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DigesterOutputStream.class);
    }
}
