package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPResponse implements Serializable
{
    private static final long serialVersionUID = -8401666939604882177L;
    private final LDAPResult ldapResult;
    
    public LDAPResponse(final LDAPResult ldapResult) {
        this.ldapResult = ldapResult;
    }
    
    public int getMessageID() {
        return this.ldapResult.getMessageID();
    }
    
    public int getResultCode() {
        return this.ldapResult.getResultCode().intValue();
    }
    
    public String getErrorMessage() {
        return this.ldapResult.getDiagnosticMessage();
    }
    
    public String getMatchedDN() {
        return this.ldapResult.getMatchedDN();
    }
    
    public String[] getReferrals() {
        final String[] referrals = this.ldapResult.getReferralURLs();
        if (referrals.length == 0) {
            return null;
        }
        return referrals;
    }
    
    public LDAPControl[] getControls() {
        final Control[] controls = this.ldapResult.getResponseControls();
        if (controls.length == 0) {
            return null;
        }
        return LDAPControl.toLDAPControls(controls);
    }
    
    public final LDAPResult toLDAPResult() {
        return this.ldapResult;
    }
    
    @Override
    public String toString() {
        return this.ldapResult.toString();
    }
}
