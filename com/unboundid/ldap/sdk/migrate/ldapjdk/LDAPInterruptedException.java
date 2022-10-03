package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPInterruptedException extends LDAPException
{
    private static final long serialVersionUID = 7867903105944011998L;
    
    LDAPInterruptedException() {
        super(null, 88);
    }
    
    LDAPInterruptedException(final com.unboundid.ldap.sdk.LDAPException ldapException) {
        super(ldapException);
    }
}
