package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.Credentials;

public interface CredentialsProvider
{
    public static final String PROVIDER = "http.authentication.credential-provider";
    
    Credentials getCredentials(final AuthScheme p0, final String p1, final int p2, final boolean p3) throws CredentialsNotAvailableException;
}
