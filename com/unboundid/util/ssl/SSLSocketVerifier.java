package com.unboundid.util.ssl;

import com.unboundid.ldap.sdk.LDAPException;
import javax.net.ssl.SSLSocket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class SSLSocketVerifier
{
    public abstract void verifySSLSocket(final String p0, final int p1, final SSLSocket p2) throws LDAPException;
}
