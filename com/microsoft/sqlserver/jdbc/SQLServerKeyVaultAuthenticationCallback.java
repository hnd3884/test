package com.microsoft.sqlserver.jdbc;

public interface SQLServerKeyVaultAuthenticationCallback
{
    String getAccessToken(final String p0, final String p1, final String p2);
}
