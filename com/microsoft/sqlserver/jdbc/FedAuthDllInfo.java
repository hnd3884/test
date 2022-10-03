package com.microsoft.sqlserver.jdbc;

class FedAuthDllInfo
{
    byte[] accessTokenBytes;
    long expiresIn;
    
    FedAuthDllInfo(final byte[] accessTokenBytes, final long expiresIn) {
        this.accessTokenBytes = null;
        this.expiresIn = 0L;
        this.accessTokenBytes = accessTokenBytes;
        this.expiresIn = expiresIn;
    }
}
