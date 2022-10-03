package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_NOT_THREADSAFE)
public final class RetainConnectExceptionReferralConnector implements ReferralConnector
{
    private final ReferralConnector wrappedReferralConnector;
    private volatile LDAPException connectExceptionFromLastAttempt;
    
    public RetainConnectExceptionReferralConnector() {
        this(null);
    }
    
    public RetainConnectExceptionReferralConnector(final ReferralConnector wrappedReferralConnector) {
        this.wrappedReferralConnector = wrappedReferralConnector;
        this.connectExceptionFromLastAttempt = null;
    }
    
    public LDAPException getExceptionFromLastConnectAttempt() {
        return this.connectExceptionFromLastAttempt;
    }
    
    @Override
    public LDAPConnection getReferralConnection(final LDAPURL referralURL, final LDAPConnection connection) throws LDAPException {
        ReferralConnector connector;
        if (this.wrappedReferralConnector == null) {
            connector = connection.getReferralConnector();
        }
        else {
            connector = this.wrappedReferralConnector;
        }
        LDAPException connectException = null;
        try {
            return connector.getReferralConnection(referralURL, connection);
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            connectException = e;
            throw e;
        }
        finally {
            this.connectExceptionFromLastAttempt = connectException;
        }
    }
}
