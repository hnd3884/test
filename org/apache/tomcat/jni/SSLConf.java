package org.apache.tomcat.jni;

public final class SSLConf
{
    public static native long make(final long p0, final int p1) throws Exception;
    
    public static native void free(final long p0);
    
    public static native int check(final long p0, final String p1, final String p2) throws Exception;
    
    public static native void assign(final long p0, final long p1);
    
    public static native int apply(final long p0, final String p1, final String p2) throws Exception;
    
    public static native int finish(final long p0);
}
