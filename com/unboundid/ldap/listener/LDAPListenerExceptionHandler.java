package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.LDAPException;
import java.net.Socket;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface LDAPListenerExceptionHandler
{
    void connectionCreationFailure(final Socket p0, final Throwable p1);
    
    void connectionTerminated(final LDAPListenerClientConnection p0, final LDAPException p1);
}
