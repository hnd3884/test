package com.adventnet.persistence;

public interface AccessControl
{
    public static final int READ = 0;
    public static final int WRITE = 1;
    public static final int SHARE = 2;
    public static final int EXPORT = 3;
    public static final int IMPORT = 4;
    
    boolean isResourceAllowed(final long p0, final long p1, final Object p2, final int p3);
}
