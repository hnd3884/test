package io.netty.channel.unix;

public final class Limits
{
    public static final int IOV_MAX;
    public static final int UIO_MAX_IOV;
    public static final long SSIZE_MAX;
    public static final int SIZEOF_JLONG;
    
    private Limits() {
    }
    
    static {
        IOV_MAX = LimitsStaticallyReferencedJniMethods.iovMax();
        UIO_MAX_IOV = LimitsStaticallyReferencedJniMethods.uioMaxIov();
        SSIZE_MAX = LimitsStaticallyReferencedJniMethods.ssizeMax();
        SIZEOF_JLONG = LimitsStaticallyReferencedJniMethods.sizeOfjlong();
    }
}
