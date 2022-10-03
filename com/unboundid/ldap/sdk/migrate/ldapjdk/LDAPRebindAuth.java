package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPRebindAuth implements Serializable
{
    private static final long serialVersionUID = -844389460595019929L;
    private final String dn;
    private final String password;
    
    public LDAPRebindAuth(final String dn, final String password) {
        this.dn = dn;
        this.password = password;
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public String getPassword() {
        return this.password;
    }
}
