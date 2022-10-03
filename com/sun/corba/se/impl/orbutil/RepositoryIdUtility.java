package com.sun.corba.se.impl.orbutil;

public interface RepositoryIdUtility
{
    public static final int NO_TYPE_INFO = 0;
    public static final int SINGLE_REP_TYPE_INFO = 2;
    public static final int PARTIAL_LIST_TYPE_INFO = 6;
    
    boolean isChunkedEncoding(final int p0);
    
    boolean isCodeBasePresent(final int p0);
    
    int getTypeInfo(final int p0);
    
    int getStandardRMIChunkedNoRepStrId();
    
    int getCodeBaseRMIChunkedNoRepStrId();
    
    int getStandardRMIChunkedId();
    
    int getCodeBaseRMIChunkedId();
    
    int getStandardRMIUnchunkedId();
    
    int getCodeBaseRMIUnchunkedId();
    
    int getStandardRMIUnchunkedNoRepStrId();
    
    int getCodeBaseRMIUnchunkedNoRepStrId();
}
