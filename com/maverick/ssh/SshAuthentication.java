package com.maverick.ssh;

public interface SshAuthentication
{
    public static final int COMPLETE = 1;
    public static final int FAILED = 2;
    public static final int FURTHER_AUTHENTICATION_REQUIRED = 3;
    public static final int CANCELLED = 4;
    public static final int PUBLIC_KEY_ACCEPTABLE = 5;
    
    void setUsername(final String p0);
    
    String getUsername();
    
    String getMethod();
}
