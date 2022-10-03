package org.apache.catalina;

public interface CredentialHandler
{
    boolean matches(final String p0, final String p1);
    
    String mutate(final String p0);
}
