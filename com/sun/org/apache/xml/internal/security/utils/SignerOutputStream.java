package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.slf4j.internal.Logger;
import java.io.ByteArrayOutputStream;

public class SignerOutputStream extends ByteArrayOutputStream
{
    private static final Logger LOG;
    final SignatureAlgorithm sa;
    
    public SignerOutputStream(final SignatureAlgorithm sa) {
        this.sa = sa;
    }
    
    @Override
    public void write(final byte[] array) {
        try {
            this.sa.update(array);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    @Override
    public void write(final int n) {
        try {
            this.sa.update((byte)n);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        if (SignerOutputStream.LOG.isDebugEnabled()) {
            SignerOutputStream.LOG.debug("Canonicalized SignedInfo:");
            final StringBuilder sb = new StringBuilder(n2);
            for (int i = n; i < n + n2; ++i) {
                sb.append((char)array[i]);
            }
            SignerOutputStream.LOG.debug(sb.toString());
        }
        try {
            this.sa.update(array, n, n2);
        }
        catch (final XMLSignatureException ex) {
            throw new RuntimeException("" + ex);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SignerOutputStream.class);
    }
}
