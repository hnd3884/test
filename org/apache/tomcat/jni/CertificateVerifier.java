package org.apache.tomcat.jni;

public interface CertificateVerifier
{
    boolean verify(final long p0, final byte[][] p1, final String p2);
}
