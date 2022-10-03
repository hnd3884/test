package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyLDAPRequest extends Serializable
{
    List<Control> getControlList();
    
    boolean hasControl();
    
    boolean hasControl(final String p0);
    
    Control getControl(final String p0);
    
    long getResponseTimeoutMillis(final LDAPConnection p0);
    
    boolean followReferrals(final LDAPConnection p0);
    
    ReferralConnector getReferralConnector(final LDAPConnection p0);
    
    LDAPRequest duplicate();
    
    LDAPRequest duplicate(final Control[] p0);
    
    String toString();
    
    void toString(final StringBuilder p0);
    
    void toCode(final List<String> p0, final String p1, final int p2, final boolean p3);
}
